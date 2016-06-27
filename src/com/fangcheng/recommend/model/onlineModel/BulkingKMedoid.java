package com.fangcheng.recommend.model.onlineModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * 增量 medoid方法
 * 
 * @author Administrator
 *
 */
public class BulkingKMedoid implements Serializable {


	/**
	 * 主类
	 * 
	 * @author Administrator
	 *
	 */
	public class Medoid implements Serializable {

		public ArrayList<int[]> dimension; // 质点的维度
		public double etdDisSum;// Medoid到本类簇中所有的欧式距离之和
		public int dbmsCount = 0;
		public double dbmsWeight=0.2;
		/**
		 * 老的类别
		 */
		public ArrayList<DataPoint> dataPoint = new ArrayList<DataPoint>();

		/**
		 * 
		 * @param dimension
		 * @param dbmsCount
		 *            结构化数据数量
		 */
		public Medoid(ArrayList<int[]> dimension, int dbmsCount,double dbmsWeight) {
			this.dimension = dimension;
			this.dbmsCount = dbmsCount;
			this.dbmsWeight=dbmsWeight;
		}

		public void addData(ArrayList<int[]> data) {
			this.dataPoint.add(new DataPoint(data));
		}

		public void addData(DataPoint data) {
			this.dataPoint.add(data);
		}

		public void clearn() {
			this.dataPoint = new ArrayList<DataPoint>();
		}

		/**
		 * 移除一个时间点之前的数据
		 * 
		 * @param dataTime
		 */
		public void move(long dataTime) {
			while (dataPoint.size() > 0) {
				DataPoint da = dataPoint.get(0);
				if (da.dataTime < dataTime) {
					dataPoint.remove(0);
				}
			}
		}

		/**
		 * 重新 计算质心 并获取 新的误差
		 */
		public void calcMedoid() {// 取代价最小的点
			double minEucDisSum = this.etdDisSum;
			for (int i = 0; i < dataPoint.size(); i++) {
				double tempeucDisSum = calDist(dimension, dataPoint.get(i),
						dbmsCount,dbmsCount);
				if (tempeucDisSum < minEucDisSum) {
					dimension = dataPoint.get(i).data;
					minEucDisSum = tempeucDisSum;
				}
			}
		}

		/**
		 * 获取距离
		 * 
		 * @param data
		 * @return
		 */
		public double getDist(DataPoint data, int dbmsCount,double dbmsWeight) {
			return calDist(dimension, data, dbmsCount,dbmsWeight);
		}

		/**
		 * 获取距离
		 * 
		 * @param data
		 * @return
		 */
		public double getDist(ArrayList<int[]> data, int dbmsCount,double dbmsWeight) {
			return calDist(dimension, data, dbmsCount,dbmsWeight);
		}

		/**
		 * 计算绝对距离
		 * 
		 * @param data1
		 * @param data2
		 * @return
		 */
		private double calDist(ArrayList<int[]> data1, DataPoint data2,
				int dbmsCount,double dbmsWeight) {
			ArrayList<int[]> da2 = data2.data;
			return calDist(data1, da2, dbmsCount,dbmsWeight);
		}

		/**
		 * 计算绝对距离
		 * 
		 * @param data1
		 * @param data2
		 * @return
		 */
		private double calDist(ArrayList<int[]> data1, ArrayList<int[]> data2,
				int dbmsCount,double dbmsWeight) {
			double sum = 0;
			for (int i = 0; i < data1.size(); i++) {
				int[] temp1 = data1.get(i);
				int[] temp2 = data2.get(i);
				if (i < dbmsCount) {
					if (temp1[0] == temp2[0]) {
					} else {
						sum += dbmsWeight;
					}
				} else {
					// 处理hash值
					int count = 0;
					for (int j = 0; j < temp1.length; j++) {
						if (temp1[j] != temp2[j]) {
							count += 1;
						}
					}
					sum += count / temp1.length;
				}
			}
			return sum;
		}

		public ArrayList<int[]> getDimension() {
			return dimension;
		}

		public void setDimension(ArrayList<int[]> dimension) {
			this.dimension = dimension;
		}

		public double getEtdDisSum() {
			return etdDisSum;
		}

		public void setEtdDisSum(double etdDisSum) {
			this.etdDisSum = etdDisSum;
		}

		public ArrayList<DataPoint> getDataPoint() {
			return dataPoint;
		}

		public void setDataPoint(ArrayList<DataPoint> dataPoint) {
			this.dataPoint = dataPoint;
		}
	}

	/**
	 * 类中心
	 */
	public ArrayList<Medoid> medoid = new ArrayList<Medoid>();
	/**
	 * 全部数据源
	 */
	public ArrayList<DataPoint> allData = new ArrayList<DataPoint>();
	/**
	 * 内部存储的有效数量
	 */
	public int validSize = 1000;
	/**
	 * 维度
	 */
	public int dimNum = 0;
	public double oldError = -1;
	public int k = 3;

	public int dbmsCount = 0;
	public double dbmsWeight=0.2;
	/**
	 * @param k
	 *            数量
	 * @param center
	 *            初始中心
	 */
	public BulkingKMedoid(int k, ArrayList<ArrayList<int[]>> center,
			int dbmsCount,double dbmsWeight) {
		this.dbmsCount = dbmsCount;
		this.dbmsWeight=dbmsWeight;
		this.k = k;
		if (center != null) {
			for (int i = 0; i < center.size(); i++) {
				Medoid me = new Medoid(center.get(i), dbmsCount,dbmsWeight);
				medoid.add(me);
			}
			this.dimNum = center.get(0).size();
		}
	}

	/**
	 * 新数据源的添加
	 * 
	 * @param newData
	 */
	public void train(ArrayList<ArrayList<int[]>> newData) {
		ArrayList<DataPoint> list = new ArrayList<DataPoint>();
		for (ArrayList<int[]> da : newData) {
			list.add(new DataPoint(da));
		}
		train2(list);
	}

	/**
	 * 新数据源的添加
	 * 
	 * @param newData
	 */
	public void train2(ArrayList<DataPoint> newData) {
		// 剔除老数据源 添加新数据源
		int size = newData.size();
		if (size == 0) {
			return;
		}
		if (medoid.size() == 0) {
			// 随机初始化类中心
			ArrayList<ArrayList<int[]>> val = randomCenter(newData);
			for (int i = 0; i < val.size(); i++) {
				medoid.add(new Medoid(val.get(i), dbmsCount,dbmsWeight));
			}
		}
		this.dimNum = newData.get(0).data.size();
		// 计算每个数据源对应的位置
		long trankTime = getTrankTime(size);
		if (trankTime != 0) {
			for (int i = 0; i < medoid.size(); i++) {
				medoid.get(i).move(trankTime);
			}
		}
		// 开始训练
		// 添加数据集
		addData2(newData);
		double error = 0;
		while (error != oldError) {
			oldError = error;
			for (int m = 0; m < medoid.size(); m++) {// 每次迭代开始情况各类簇的点
				medoid.get(m).clearn();
				;
			}
			for (int j = 0; j < allData.size(); j++) {
				int clusterIndex = 0;
				double minDistance = Double.MAX_VALUE;
				// 样本与质点的最小距离
				for (int k = 0; k < medoid.size(); k++) {// 判断样本点属于哪个类簇
					double eucDistance = medoid.get(k).getDist(allData.get(j),
							dbmsCount,dbmsWeight);
					if (eucDistance < minDistance) {
						minDistance = eucDistance;
						clusterIndex = k;
					}
				}
				// 将该样本点添加到该类簇
				medoid.get(clusterIndex).addData(allData.get(j));
			}

			for (int m = 0; m < medoid.size(); m++) {
				medoid.get(m).calcMedoid();// 重新计算各类簇的质点
			}

			error = 0;
			for (int n = 0; n < medoid.size(); n++) {
				error += medoid.get(n).etdDisSum;
			}
		}
	}

	/**
	 * 测试数据
	 * 
	 * @return 对应的分组id
	 */
	public int test(ArrayList<int[]> data) {
//		for(int[] val:data){
//			System.out.println("输入属性:"+Arrays.toString(val));
//		}
		int index = 0;
		double min = Double.MAX_VALUE;
		for (int k = 0; k < medoid.size(); k++) {// 判断样本点属于哪个类簇
			double eucDistance = medoid.get(k).getDist(data, dbmsCount,dbmsWeight);
			if (min > eucDistance) {
				min = eucDistance;
				index = k;
			}
		}
		return index;
	}

	// 添加新数据
	public void addData(ArrayList<ArrayList<int[]>> newData) {
		for (int i = 0; i < newData.size(); i++) {
			allData.add(new DataPoint(newData.get(i)));
		}
		// 重排序
		// sort();
	}

	public void addData2(ArrayList<DataPoint> newData) {
		for (DataPoint dp : newData) {
			allData.add(dp);
		}
		// 默认传入的就是有序数据所以 不需要再排序
	}

	/**
	 * 重排序
	 */
	public void sort() {
		Collections.sort(allData);
	}

	/**
	 * 获取需要被截断的时间戳
	 * 
	 * @param size
	 * @return 如果为0表示不截断
	 */
	public long getTrankTime(int size) {
		long val = 0;
		if (allData.size() + size > validSize) {
			// 如果
			int s = allData.size() + size - validSize;
			if(allData.size()>s)
			{
				long time = allData.get(s).dataTime;
				remove(time, s);
				val=time;
			}
		}
		return val;
	}

	/**
	 * 移除数据集
	 * 
	 * @param time
	 * @param index
	 */
	public void remove(long time, int index) {
		while (index >= 0) {
			allData.remove(0);
		}
		while (allData.size() > 0) {
			if (time < allData.get(0).dataTime) {
				allData.remove(0);
			} else {
				break;
			}
		}
	}

	/**
	 * 随机化类中心
	 * 
	 * @param data
	 * @return
	 */
	public ArrayList<ArrayList<int[]>> randomCenter(ArrayList<DataPoint> data) {
		// 以为使用 铭刻符距离
		// 计算最大
		// 使用随机膨胀算法 计算最大距离的k个点
		ArrayList<ArrayList<int[]>> da = new ArrayList<ArrayList<int[]>>(k);
		for (int i = 0; i < data.size(); i++) {
			da.add(data.get(i).data);
		}
		RandomFuzzer random = new RandomFuzzer(da, k, 100);
		random.train();
		return random.getOutPut();
	}

	public class RandomFuzzer {
		public ArrayList<ArrayList<int[]>> data = null;
		public int k = 0;
		public int iterCount = 0;
		public ArrayList<ArrayList<int[]>> center = null;
		public ArrayList<ArrayList<double[]>> centerPower = null;
		public ArrayList<ArrayList<double[]>> centerPower2 = null;
		public int[] cluster = null;
		public int dimNum = 0;
		public double alpha = 1;
		public double weight = 0.1;
		public double wegith2 = 0.2;

		public RandomFuzzer(ArrayList<ArrayList<int[]>> data, int k,
				int iterCount) {
			this.data = data;
			this.k = k;
			this.iterCount = iterCount;
			this.dimNum = data.get(0).size();
		}

		/**
		 * 训练
		 */
		public void train() {
			center = new ArrayList<ArrayList<int[]>>();
			HashSet<One> val = new HashSet<One>();
			int zn = 0;
			for (int i = 0; i < k; i++) {
				// 随机k个点
				while (true) {
					zn++;
					int index = (int) (Math.random() * data.size());
					val.add(new One(data.get(index)));
					if (val.size() == i + 1) {
						break;
					}
					if (zn > 3 * data.size()) {
						return;
					}
				}
			}
			// 弹性方法细节
			center = new ArrayList<ArrayList<int[]>>();
			int index = 0;
			for (One one : val) {
				center.add(one.data);
				//index++;
			}
			cluster = new int[k];
			for (int it = 0; it < iterCount; it++) {
				//System.out.println("迭代次数:"+it);
				centerPower = new ArrayList<ArrayList<double[]>>();
				centerPower2 = new ArrayList<ArrayList<double[]>>();
				for (int i = 0; i < center.size(); i++) {
					centerPower.add(new ArrayList<double[]>());
					centerPower2.add(new ArrayList<double[]>());
					for(int j=0;j<dimNum;j++)
					{
						double[] temp = new double[data.get(0).get(j).length];
						centerPower.get(i).add(temp);
						centerPower2.get(i).add(temp.clone());
					}
				}
				for (int zp = 0; zp < k; zp++) {
					// 计算每一个中心点的受力点
					for (int i = 0; i < data.size(); i++) {
						//System.out.println("样本:"+i+":cluster:"+zp);
						double dist = euaDist(centerPower.get(zp), data.get(i),dbmsCount,dbmsWeight);
						if (dist == 0d) {
							continue;
						}
						for (int j = 0; j < dimNum; j++) {
							// 距离越远受力越小
							int[] val1=data.get(i).get(j);
							double[] val2=centerPower.get(zp).get(j);
							for(int k=0;k<val1.length;k++)
							{
								double di = (val1[k] - val2[k]);
								if (di == 0d) {
									continue;
								}
								centerPower.get(zp).get(j)[k] += alpha / (dist / di);
							}
						}
					}
				}
				// 调整核心点之间的作用力

				for (int zp = 0; zp < k; zp++) {
					for (int zp2 = 0; zp2 < k; zp2++) {
						double dist = euaDist(centerPower.get(zp), centerPower.get(zp2));
						if (dist == 0d) {
							continue;
						}
						for (int j = 0; j < dimNum; j++) {
							for(int k=0;k<centerPower2.get(zp).get(j).length;k++)
							{
								centerPower2.get(zp).get(j)[k] += (centerPower.get(zp).get(j)[k] - 
										centerPower.get(zp2).get(j)[k])
										/ dist;
							}
						}
					}
				}
				// 调整中心点能量
				for (int zp = 0; zp < k; zp++) {
					for (int i = 0; i < dimNum; i++) {
						for(int k=0;k<centerPower.get(zp).get(i).length;k++)
						{
							centerPower.get(zp).get(i)[k] = centerPower.get(zp).get(i)[k] * weight
									+ centerPower2.get(zp).get(i)[k];
						}
					}
				}
				// 调整类中心
				for (int zp = 0; zp < k; zp++) {
					for (int i = 0; i < dimNum; i++) {
						for(int k=0;k<center.get(zp).get(i).length;k++)
						{
							center.get(zp).get(i)[k] += centerPower.get(zp).get(i)[k] * weight
									+ centerPower2.get(zp).get(i)[k] * wegith2;
						}
					}
				}
				// error不写
			}
		}

		/**
		 * 米考夫祭祀 1
		 * 
		 * @return
		 */
		public double euaDist(ArrayList<double[]> data1,
				ArrayList<int[]> data2, int dbmsCount,double dbmsWeight) {
			double sum = 0;
			for (int i = 0; i < dbmsCount; i++) {
				sum += Math.abs(data1.get(i)[0] - data2.get(i)[0])*dbmsWeight;
			}
//			for(int i=0;i<data1.size();i++){
//				System.out.println(i+":"+data1.get(i).length+":"+Arrays.toString(data1.get(i))+":");
//			}
//			for(int i=0;i<data2.size();i++){
//				System.out.println(i+":"+data2.get(i).length+":"+Arrays.toString(data2.get(i))+":iii");
//			}
			for (int i = dbmsCount; i < data1.size(); i++) {
				double[] val1 = data1.get(i);
				int[] val2 = data2.get(i);
				int count = 0;
				for (int j = 0; j < val1.length; j++) {

					if (val1[j] != val2[j]) {
						count += 1;
					}
				}
				sum += count * count;
			}
			return sum;
		}

		/**
		 * 米考夫祭祀 1
		 * 
		 * @return
		 */
		public double euaDist(ArrayList<double[]> data1,
				ArrayList<double[]> data2) {
			double sum = 0;
			for (int i = 0; i < dbmsCount; i++) {
				sum += Math.abs(data1.get(i)[0] - data2.get(i)[0])*dbmsWeight;
			}
			for (int i = dbmsCount; i < data1.size(); i++) {
				double[] val1 = data1.get(i);
				double[] val2 = data2.get(i);
				int count = 0;
				for (int j = 0; j < val1.length; j++) {

					if (val1[j] != val2[j]) {
						count += 1;
					}
				}
				sum += count * count;
			}
			return sum;
		}

		/**
		 * 基本hash类
		 * 
		 * @author Administrator
		 *
		 */
		public class One implements Comparable<One> {
			public ArrayList<int[]> data = null;

			public One(ArrayList<int[]> data) {
				this.data = data;
			}

			@Override
			public int compareTo(One o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int hashCode() {
				int v = 0;
				for (int i = 0; i < data.size(); i++) {
					int[] temp = data.get(i);
					for (int j = 0; j < temp.length; j++) {
						v += temp[j] >> j;
					}
				}
				return v;
			}
		}

		/**
		 * 获取最终的结果输出
		 * 
		 * @return
		 */
		public ArrayList<ArrayList<int[]>> getOutPut() {
			return center;
		}
	}

	public ArrayList<Medoid> getMedoid() {
		return medoid;
	}

	public void setMedoid(ArrayList<Medoid> medoid) {
		this.medoid = medoid;
	}

	public ArrayList<DataPoint> getAllData() {
		return allData;
	}

	public void setAllData(ArrayList<DataPoint> allData) {
		this.allData = allData;
	}

	public int getValidSize() {
		return validSize;
	}

	public void setValidSize(int validSize) {
		this.validSize = validSize;
	}

	public int getDimNum() {
		return dimNum;
	}

	public void setDimNum(int dimNum) {
		this.dimNum = dimNum;
	}

	public double getOldError() {
		return oldError;
	}

	public void setOldError(double oldError) {
		this.oldError = oldError;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public static void main(String[] args) {
		double[][] cen = { { 8, 7 }, { 8, 6 }, { 7, 7 } };
		int[][] data = { { 2, 3 }, { 2, 4 }, { 1, 4 }, { 1, 3 }, { 2, 2 },
				{ 3, 2 },

				{ 8, 7 }, { 8, 6 }, { 7, 7 }, { 7, 6 }, { 8, 5 },

				{ 100, 2 },// 孤立点

				{ 8, 20 }, { 8, 19 }, { 7, 18 }, { 7, 17 }, { 7, 20 } };
		BulkingKMedoid test = new BulkingKMedoid(3, null,3,0.2);
		// test.train(data);
		// for(int i=0;i<data.length;i++){
		// System.out.println(i+":"+test.test(data[i]));
		// }
	}
}

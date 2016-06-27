package com.fangcheng.recommend.model.onlineModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;

import com.fangcheng.recommend.model.bean.DataModel;
import com.fangcheng.util.HdfsUtil;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

/**
 * 最终确定不使用树结构 依然使用增量聚类算啊作为标准组控制器 根据新的增量数据 做tree划分
 * 
 * @author Administrator
 *
 */
public class DynamicClusterModel extends DataModel {
	/**
	 * 模型中总共存储的数量
	 */
	public int size = 1000;
	/**
	 * 每新增100个修复模型
	 */
	public int addSize = 100;

	/**
	 * 堵塞队列
	 */
	public LinkedBlockingQueue<DataPoint> queue = null;

	/**
	 * 类别数量
	 */
	public int k = 1;
	/**
	 * 比率dbms 和feather的
	 */
	public double weight=0.2;

	/**
	 * 增量聚类方法
	 */
	public BulkingKMedoid method = null;
	/**
	 * 使用的feather name 对应的值
	 */
	public ArrayList<HashMap<String, Integer>> featherMap = new ArrayList<HashMap<String, Integer>>();
	/**
	 * 反向
	 */
	public ArrayList<HashMap<Integer, String>> featherMapRev = new ArrayList<HashMap<Integer, String>>();
	/**
	 * 每一组中使用的最大值
	 */
	public ArrayList<Integer> maxFeatherIndex = new ArrayList<Integer>();
	/**
	 * 结构化数据数量
	 */
	public int dbmsCount=0;
	/**
	 * 从hdfs中加载对应的模型模块
	 * 
	 * @param hdfsModleFile
	 */
	public DynamicClusterModel(String modelName,int dbmsCount) {
		this.modelName = modelName;
		queue = new LinkedBlockingQueue<DataPoint>(this.addSize);
		this.dbmsCount=dbmsCount;
	}

	/**
	 * 模型写文件
	 * 
	 * @param hdfsModelFile
	 */
	@Override
	public boolean write() {
		// 需要重命名原来的数据文件
		HdfsUtil hdfs = new HdfsUtil();
		try {
			hdfs.renameFile(modelFile +modelName+ extendName, modelFile+modelName
					+  getDataString() + extendName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FSDataOutputStream output = hdfs.getWriteFile(modelFile + modelName
					+ extendName);
			ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new GZIPOutputStream(output)));
			oos.writeInt(k);
			oos.writeInt(dbmsCount);
			oos.writeObject(method);
			oos.writeObject(featherMap);
			oos.writeObject(featherMapRev);
			oos.writeObject(maxFeatherIndex);
			oos.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// 使用java 自己的 不适用kroy
		return true;
	}

	/**
	 * 模型读取
	 */
	@Override
	public boolean read() {
		HdfsUtil hdfs = new HdfsUtil();
		try {
//			System.out.println("读取:"+modelFile + modelName
//					+ extendName);
			FSDataInputStream input = hdfs.getReadFile(modelFile + modelName
					+ extendName);
			if(input==null){
				return false;
			}
			ObjectInputStream obj = new ObjectInputStream(
					new BufferedInputStream(new GZIPInputStream(input)));
			this.k = obj.readInt();
			this.dbmsCount=obj.readInt();
			this.method = (BulkingKMedoid) obj.readObject();
			this.featherMap = (ArrayList<HashMap<String, Integer>>) obj
					.readObject();
			this.featherMapRev = (ArrayList<HashMap<Integer, String>>) obj
					.readObject();
			this.maxFeatherIndex = (ArrayList<Integer>) obj.readObject();

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 数据训练
	 * 
	 * @param data
	 */
	public DynamicClusterModel(String[][] data,String modelName,int dbmsCount) {
		this.modelName=modelName;
		queue = new LinkedBlockingQueue<DataPoint>(this.addSize);
		ArrayList<ArrayList<int[]>> val = new ArrayList<ArrayList<int[]>>();
		for (int j = 0; j < data[0].length; j++) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			HashMap<Integer, String> map2 = new HashMap<Integer, String>();
			featherMap.add(map);
			featherMapRev.add(map2);
			maxFeatherIndex.add(0);
		}
		this.dbmsCount=dbmsCount;
		for (int i = 0; i < data.length; i++) {
			// 获取转换值
			ArrayList<int[]> temp = getMapping(data[i]);
			val.add(temp);
		}
		method = new BulkingKMedoid(k, null,dbmsCount,weight);
		// 训练
		method.train(val);
		//
		// method.test(val);
	}
	
	/**
	 * 数据训练
	 * 
	 * @param data
	 */
	public DynamicClusterModel(LinkedList<BasicDBObject> data,String modelName,int dbmsCount) {
		this.modelName=modelName;
		queue = new LinkedBlockingQueue<DataPoint>(this.addSize);
		ArrayList<ArrayList<int[]>> val = new ArrayList<ArrayList<int[]>>();	
		this.dbmsCount=dbmsCount;
		while(data.size()>0)
		{
			// 获取转换值
			val.add(getMapping(data.pollFirst()));
		}
		method = new BulkingKMedoid(k, null,dbmsCount,weight);
		// 训练
		method.train(val);
		//
		// method.test(val);
	}

	/**
	 * 获取 string 转化为 int的值 不适用 通过string转化为int的方法使用默认方法
	 * 
	 * @param strArray
	 * @return
	 */
	public ArrayList<int[]> getMapping(String[] strArray) {
		ArrayList<int[]> temp = new ArrayList<int[]>();
		// for (int i = 0; i < strArray.length; i++) {
		// HashMap<String, Integer> map = featherMap.get(i);
		// HashMap<Integer, String> map2 = featherMapRev.get(i);
		// if (map.containsKey(strArray[i])) {
		// temp[i] = map.get(strArray[i]);
		// } else {
		// int index = maxFeatherIndex.get(i) + 1;
		// map.put(strArray[i], index);
		// map2.put(index, strArray[i]);
		// temp[i] = index;
		// maxFeatherIndex.set(i, index);
		// }
		// }
		return temp;
	}

	public ArrayList<int[]> getMapping(BasicDBObject strArray) {
		ArrayList<int[]> temp = new ArrayList<int[]>();
		try {
			//System.out.println(strArray.getString("dbms"));
			int[] dbms =new int[dbmsCount];
			//System.out.println("mongo:"+strArray.toString());
			BasicDBList list=((BasicDBList) strArray.get("dbms"));
			for(int i=0;i<dbmsCount;i++){
				dbms[i]=(int)list.get(i);
			}
			// 加载结构化数据
			for (int i : dbms) {
				int[] val = new int[1];
				val[0] = i;
				temp.add(val);
			}
			ObjectNode feature=JsonUtil.parse(strArray.getString("f"));
			Iterator<String> iter=feature.fieldNames();
			//HashMap<String, int[]> obj = new HashMap<String,int[]>();
			while(iter.hasNext())
			{
				String keyString=iter.next();
				int key = Integer.parseInt(keyString);
				while (true) {
					if (key == temp.size()) {
						temp.add(getIntArray(feature.get(keyString)));
						break;
					} else if (key > temp.size()) {
						temp.add(new int[0]);
					} else {
						temp.set(key,getIntArray(feature.get(keyString)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}
	public int[] getIntArray(Object obj){
		ArrayNode array=(ArrayNode)JsonUtil.parse(obj);
		int[] re=new int[array.size()];
		for(int i=0;i<array.size();i++){
			re[i]=array.get(i).asInt();
		}
//		System.arraycopy(list,0,re,0,list.length);
		return re;
	}

	/**
	 * 在流式计算中不需要线程锁 新增属性
	 * 
	 * @param data
	 */
	public int addFeather(BasicDBObject data) {
//		System.out.println(data);
//		System.out.println(data.toString());
		ArrayList<int[]> feather = getMapping(data);
		queue.add(new DataPoint(feather));
		if (queue.size() >= addSize) {
			ArrayList<DataPoint> list = new ArrayList<DataPoint>(addSize);
			updateModel(list, addSize);
		}
		return this.method.test(feather);
	}

	/**
	 * 更新
	 */
	public synchronized void updateModel() {
		int size = queue.size();
		ArrayList<DataPoint> list = new ArrayList<DataPoint>(size);
		updateModel(list, size);
	}

	/**
	 * 更新
	 * 
	 * @param list
	 * @param size
	 */
	public synchronized void updateModel(ArrayList<DataPoint> list, int size) {
		if (queue.size() > 0) {
			int sizeTemp = size;
			while (sizeTemp > 0) {
				sizeTemp--;
				list.add(queue.poll());
			}
			// 执行调度函数
			this.method.addData2(list);
			timeUpdateModel = System.currentTimeMillis();
		}
	}

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis());
	}

}

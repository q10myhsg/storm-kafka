package com.fangcheng.recommend.model.bean;

import java.io.Serializable;

import com.fangcheng.recommend.model.onlineModel.StoreMaxNNModel;

public class StoreBean implements Comparable<StoreBean>, Cloneable,
			Serializable {
		public long i = 0l;
		/**
		 * 历史点击数量
		 */
		public int a = 0;
		/**
		 * 评估值
		 */
		public double s = 0;

		public String toString2() {
			return "[id:" + i + ",action:" + a + ",evalveScore:" + s + "]";
		}
		public StoreBean(){
			
		}
		public StoreBean(long id, int size) {
			this.i = id;
			this.a = size;
			this.s = size
					* Math.log((int) (System.currentTimeMillis() - StoreMaxNNModel.timeInit));
		}

		public StoreBean(long id, int size, double val) {
			this.i = id;
			this.a = size;
			this.s = val;
		}

		// public int delay=0;
		// public long tiemS=0l;
		@Override
		public int compareTo(StoreBean o) {
			// TODO Auto-generated method stub
			return Double.compare(o.s, this.s);
		}

		/**
		 * 添加新数据
		 * 
		 * @param bean
		 */
		public void add(StoreBean bean) {
			this.a += bean.a;
			this.s += bean.s;
		}

		/**
		 * 平均累积
		 * 
		 * @param bean
		 */
		public void addMean(StoreBean bean) {
			int sum = this.a + bean.a;
			this.a = sum / 2;
			this.s = (this.s * this.a * 1f / sum) + bean.s * 1f * bean.a / sum;
		}

		public StoreBean clone() {
			return new StoreBean(this.i, this.a, this.s);
		}

		public long getI() {
			return i;
		}

		public void setI(long i) {
			this.i = i;
		}

		public int getA() {
			return a;
		}

		public void setA(int a) {
			this.a = a;
		}

		public double getS() {
			return s;
		}

		public void setS(double s) {
			this.s = s;
		}

	}
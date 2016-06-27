package com.fangcheng.recommend.model.onlineModel;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPoint implements Comparable<DataPoint> ,Serializable{
		/**
		 * 数据源
		 */
		public ArrayList<int[]> data = null;
		/**
		 * 数据源的时间戳
		 */
		public long dataTime = System.currentTimeMillis();

		public DataPoint(ArrayList<int[]> data) {
			this.data = data;
		}

		/**
		 * 从小到大排序
		 */
		@Override
		public int compareTo(DataPoint o) {
			// TODO Auto-generated method stub
			return Long.compare(dataTime, o.dataTime);
		}

		public ArrayList<int[]> getData() {
			return data;
		}

		public void setData(ArrayList<int[]> data) {
			this.data = data;
		}

		public long getDataTime() {
			return dataTime;
		}

		public void setDataTime(long dataTime) {
			this.dataTime = dataTime;
		}
		
	}

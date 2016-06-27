package com.fangcheng.recommend.model.onlineModel;

import java.io.Serializable;
import java.util.ArrayList;

import com.db.MongoDb;
import com.fangcheng.util.JsonUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class Test implements Comparable<Test>, Cloneable,
Serializable  {

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
		public Test(){
			
		}
		
		public Test(long id, int size) {
			this.i = id;
			this.a = size;
			this.s = size
					* Math.log((int) (System.currentTimeMillis() - 1));
		}

		public Test(long id, int size, double val) {
			this.i = id;
			this.a = size;
			this.s = val;
		}

		// public int delay=0;
		// public long tiemS=0l;
		@Override
		public int compareTo(Test o) {
			// TODO Auto-generated method stub
			return Double.compare(o.s, this.s);
		}

		/**
		 * 添加新数据
		 * 
		 * @param bean
		 */
		public void add(Test bean) {
			this.a += bean.a;
			this.s += bean.s;
		}

		/**
		 * 平均累积
		 * 
		 * @param bean
		 */
		public void addMean(Test bean) {
			int sum = this.a + bean.a;
			this.a = sum / 2;
			this.s = (this.s * this.a * 1f / sum) + bean.s * 1f * bean.a / sum;
		}

		public Test clone() {
			return new Test(this.i, this.a, this.s);
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


	public static void main(String[] args) {
			int[] test=new int[]{1,2,3};
			int[] test2=new int[]{1,2,3};
			int[] test3=new int[]{1,2,4};
			System.out.println(test.hashCode());
			System.out.println(test2.hashCode());
			System.out.println(test3.hashCode());
			MongoDb mongo=new MongoDb("192.168.1.11",27017,"recommend");
			DBCursor cursor=mongo.findCursor("reService","{ \"id\" : 2 , \"t\" : 86999030 , \"c\" : 10000 , \"i\" : 14462}","{}");
			while(cursor.hasNext()){
				System.out.println(cursor.next().toString());
			}
			cursor=mongo.findCursor("reService","{ \"id\" : 1 , \"t\" : 86999030 , \"c\" : 10000 , \"i\" : 1}","{}");
			while(cursor.hasNext()){
				System.out.println(cursor.next().toString());
			}
			BasicDBObject obj=new BasicDBObject();
			obj.put("id",2);
			obj.put("t",86999030);
			obj.put("c",10000);
			obj.put("i", 14462);
			System.out.println(mongo.findOne("reService",obj ));
			
			String temp="[ { \"a\" : 4 , \"i\" : 14407 , \"s\" : 81.70609649170653} , { \"a\" : 4 , \"i\" : 17772 , \"s\" : 81.70533675771239}]";

			System.out.println((ArrayList<Test>)JsonUtil.getDtoArrFromJsonArrListStr(temp, Test.class));
		}
}

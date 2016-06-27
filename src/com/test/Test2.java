package com.test;

import com.db.MongoDb;
import com.mongodb.DBCursor;

public class Test2 {

	public static void main(String[] args) {
		MongoDb mongo=new MongoDb("192.168.1.11",27017,"demo");
		DBCursor cursor=mongo.find("fang");
		while(cursor.hasNext()){
			System.out.println(cursor.next());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

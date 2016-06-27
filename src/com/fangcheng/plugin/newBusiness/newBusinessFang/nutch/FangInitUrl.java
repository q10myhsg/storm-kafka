package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.db.MongoDb;
import com.fangcheng.plugin.newBusiness.newBusinessFang.FileUtil2;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class FangInitUrl {

	
	/**
	 * 初始化搜房 隊列
	 */
	public static void initUrlQueue(String outPutFilePath)
	{
		MongoDb mongo=new MongoDb("192.168.1.4",27017,"demo");
		BasicDBObject ref=new BasicDBObject();
		ref.put("fangListc.url",1);
		DBCursor cursor=mongo.find("fang",new BasicDBObject(),ref);
		FileUtil2 fileUtil=new FileUtil2(outPutFilePath,"utf-8");
		LinkedList<String> list=new LinkedList<String>();
		HashSet<String> set=new HashSet<String>();
		while(cursor.hasNext())
		{
			BasicDBObject obj=(BasicDBObject)cursor.next();
			String url=((BasicDBObject)obj.get("fangListc")).getString("url");
			set.add(url);
		}
		cursor.close();
		List<String> cityName=new ArrayList<String>();
		cityName.add("北京");
		cityName.add("上海");
		cityName.add("南京");
		cityName.add("广州");
		cityName.add("深圳");
		HashSet<String> parseUrl=FangInitDistinctMethod.getAllUrl(cityName);
		//過濾重複
		for(String url:parseUrl)
		{
			if(set.contains(url))
			{}else{
				//添加
				System.out.println("添加:"+url);
			}
			set.add(url);
		}
		for(String url:set)
		{
			list.add(url);
		}
		fileUtil.write(list);
		fileUtil.close();
	}
	public static void main(String[] args) {
		String filePath=System.getProperty("user.dir")+"/src/plugin/parse-fang/fangUrlAll.txt";
		System.out.println(filePath);
		FangInitUrl.initUrlQueue(filePath);
		
	}
}

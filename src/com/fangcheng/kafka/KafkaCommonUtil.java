package com.fangcheng.kafka;

import java.util.HashMap;
import java.util.LinkedList;
import clojure.main;

import com.db.MongoDb;
import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.plugin.newBusiness.newBusinessFang.FileUtil2;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;


/**
 * 通用测试程序
 * @author Administrator
 *
 */
public class KafkaCommonUtil {

	public static KafkaUtil produce =null;
	
	static{
		produce= new KafkaUtil();
	}
	public KafkaCommonUtil(){
		
	}
	
	/**
	 * 发送搜房url
	 * @param url
	 * @return
	 */
	public static boolean sendFang(String url)
	{
		return sendMsg(0,JobStatic.FANG,ParamsStatic.URL,url);
	}
	/**
	 * 
	 * @param jobId 平台自己的id
	 * @param url
	 * @return
	 */
	public static boolean sendFang(int jobId,String url)
	{
		return sendMsg(jobId,JobStatic.FANG,ParamsStatic.URL,url);
	}
	/**
	 * 获取搜房使用的参数key
	 * @return
	 */
	public static String[] getFangParam()
	{
		return new String[]{ParamsStatic.URL};
	}
	
	/**
	 * 发送搜房url
	 * @param url
	 * @return
	 */
	public static boolean sendDp(String url)
	{
		return sendMsg(0,JobStatic.DP,ParamsStatic.URL,url);
	}
	
	public static boolean sendDp(int jobId,String url)
	{
		return sendMsg(jobId,JobStatic.DP,ParamsStatic.URL,url);
	}
	
	/**
	 * 获取搜房使用的参数key
	 * @return
	 */
	public static String[] getDpParam()
	{
		return new String[]{ParamsStatic.URL};
	}
	
	/**
	 * 发送搜房url
	 * @param url
	 * @return
	 */
	public static boolean sendPoiTj(String string)
	{
		return sendMsg(0,JobStatic.POI_CRAWLER,ParamsStatic.POI_CRAWLER,string);
	}
	
	
	public static boolean sendPoiTj(int jobId,String string)
	{
		return sendMsg(jobId,JobStatic.POI_CRAWLER,ParamsStatic.POI_CRAWLER,string);
	}
	
	/**
	 * 获取搜房使用的参数key
	 * @return
	 */
	public static String[] getPoiTjParam()
	{
		return new String[]{ParamsStatic.POI_CRAWLER};
	}
	
	/**
	 * 统一的获取方法
	 * @param jobType
	 * @param strs
	 * @return
	 */
	public static boolean sendMsg(int jobId,String jobType,String...strs)
	{
		if(strs.length%2!=0)
		{
			System.out.println("输入参数数量异常");
			System.exit(1);
		}
		KafkaTransmitBean kafkaTransmitBean=new KafkaTransmitBean();
		kafkaTransmitBean.jobId=jobId;
		kafkaTransmitBean.topic=TopicStatic.ADD_NEW_BUSINESS;
		kafkaTransmitBean.partition="1";
		ParamsBean params=new ParamsBean();
		params.jobType=jobType;
		params.requestData=JsonUtil.init();
		for(int i=0;i<strs.length;i+=2)
		{
			params.requestData.put(strs[i],strs[i+1]);
		}
		kafkaTransmitBean.params=params;
		return produce.sentMsgs(kafkaTransmitBean);
	}
	
	
	/**
	 * 搜房的测试程序
	 */
	public static void testFang(int count)
	{
		MongoDb mongo=new MongoDb("192.168.1.4",27017,"demo");
		DBCursor cursor=mongo.findCursor("fang","{}","{fangListc.url:1}");
		int count1=0;
		while(cursor.hasNext())
		{
			count1++;
			BasicDBObject obj=(BasicDBObject)cursor.next();
			//System.out.println(obj.toString());
			//System.out.println(((BasicDBObject)obj.get("fangListc")).get("url"));
			boolean flag= KafkaCommonUtil.sendFang(((BasicDBObject)obj.get("fangListc")).getString("url"));
			if(count1>=count)
			{
				break;
			}
		}
		mongo.close();
	}
	/**
	 *poi 统计的测试程序
	 */
	public static void testPoiTj(int count)
	{
		FileUtil2 file=new FileUtil2(KafkaCommonUtil.class.getResource("beijing.txt").getPath(),"utf-8",false);
		LinkedList<String> list=file.readAndClose();
		int i=0;
		for(String str:list)
		{
			i++;
			//System.out.println(str);
			sendPoiTj(str);
			if(i>=count)
			{
				break;
			}
		}
	}
	/**
	 * 点评的测试程序
	 */
	public static void testDp(int count)
	{
//		for(int i=1100000;i<1200000;i+=3)
//		{
//			sendDp("http://http://www.dianping.com/shop/"+i);
//		}
		int[] list=new int[]{1,2,4,5,7};
		MongoDb mongo=new MongoDb("192.168.1.11",27017,"dianping");
		for(int i=0;i<list.length;i++)
		{
			DBCursor cursor=mongo.findCursor("city_"+list[i]+"_page","{}","{shopId:1}");
			int j=0;
			while(cursor.hasNext())
			{
				j++;
				BasicDBObject obj=(BasicDBObject)cursor.next();
				long shopId=obj.getLong("shopId");
				sendDp("http://www.dianping.com/shop/"+shopId);
				if(j>=count)
				{
					break;
				}
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		 KafkaCommonUtil.testFang(10);
		 KafkaCommonUtil.testPoiTj(1);
		 KafkaCommonUtil.testDp(3);
	}
}

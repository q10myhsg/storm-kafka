package com.fangcheng.kafka.Bean;

import java.util.HashMap;

import com.db.MongoDb;

public class MongoStatic {

	/**
	 * 获取 job名字
	 * @param jobId
	 * @return
	 */
	public static MongoDb getMongoDb(String jobId)
	{
		String str=mongoConfig.get(jobId);
		if(str==null)
		{
			System.out.println("不存在该ip的结尾号码，请确认配置信息");
			System.exit(1);
		}
		String[] strList=str.split(":");
		if(strList.length==3)
		{
			return new MongoDb(strList[0],Integer.parseInt(strList[1]),strList[2]);
		}else if(strList.length==6){
			return new MongoDb(strList[0],Integer.parseInt(strList[1]),strList[2],strList[3],strList[4],strList[5]);
		}else{
			System.out.println("ip对应的配置文件异常");
			System.exit(1);
		}
		return null;
	}
	
	public static HashMap<String,String> mongoConfig=new HashMap<String,String>();
	
	static{
		mongoConfig.put("fang","192.168.1.4:27017:demo");
		mongoConfig.put("fang11","192.168.1.11:27017:demo");
		mongoConfig.put("dianping","123.57.4.152:27017:dianping");
	}
}

package com.fangcheng.plugin.newBusiness.newBusinessFang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.commons.httpclient.ConnectTimeoutException;

import com.db.Redis;
import com.fangcheng.parse.interator.ClassLoaderJar;
import com.fangcheng.plugin.base.TimerConfigThread;

import clojure.main;

public class test {

	public static void main(String[] args) throws Exception {
//		try {
//			ClassLoaderJar.reloadJar("h:/nutch");
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String url="http://tongfangjiaoyu.fang.com";
//		String urlCode=AntGetUrl.doGetGzip(url,"gbk",false);
//		//System.out.println(urlCode);
//		//通过 获取 动态lib
//		
//		Class parseMethod=ClassLoaderJar.forName("com.fangcheng.www.fang.parseMethod.FangDescParseMethod");
//		//@SuppressWarnings("unchecked")
//		//Constructor constructor = parseMethod.getConstructor();
//		// System.out.println();
//		//Object entity =  constructor.newInstance();
//		//System.out.println(Object.class.getName());
//		//return FangDescParseMethod.runUrl(url, urlCode);
//		Method m1= parseMethod.getDeclaredMethod("runUrl",String.class,String.class);
//		System.out.println( (String)m1.invoke(parseMethod,url,urlCode));
		
		
//		IPProxy proxy=new IPProxy();
//		proxy.setIp("117.37.251.71");
//		proxy.setPort(80);
//		//http://nuodezhongxinbj.fang.com/
//		try{
//		String urlCode = AntGetUrlProxy.testDoGet("http://www.baidu.com",proxy);
//		System.out.println(urlCode);
//		}catch(ConnectTimeoutException e1)
//		{
//			System.out.println(e1.getMessage());
//		}catch(ConnectException e2)
//		{
//			System.out.println(e2.getMessage());
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
		 String configFilePath = "hdfs://master:9000/storm_plugin_conf/infoQueue.properties";
		HashMap<String,String> config = new HashMap<String, String>();
		TimerConfigThread.readConfig(config, configFilePath);
		MainProxy.setRedis(new Redis(config.get("proxyRedisIp"),Integer.parseInt(config.get("proxyRedisPort"))));
		String urlCode = AntGetUrlProxy.doGet("http://taipingshangwudasha.fang2.com/office/", "gbk", true,config);
		System.out.println(urlCode);
	}
}

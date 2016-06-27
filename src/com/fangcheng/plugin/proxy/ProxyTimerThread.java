package com.fangcheng.plugin.proxy;

import java.util.ArrayList;
import java.util.HashMap;

import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.plugin.newBusiness.newBusinessFang.IPProxy;
import com.fangcheng.plugin.newBusiness.newBusinessFang.MainProxy;

public class ProxyTimerThread extends Thread {
	// 創建 統一的配置文件目錄

	private HashMap<String, String> config = null;
	/**
	 * 代理ip地址
	 */
	private String configPath = null;
	/**
	 * 暂停时间
	 */
	public String proxyTimeSleep = "proxyTimeSleep";
	/**
	 * 最小redis数量
	 */
	private String proxyCountMinLimit = "proxyCountMinLimit";

	public ProxyTimerThread(HashMap<String, String> config, String configPath) {
		this.config = config;
		this.configPath = configPath;
	}

	public void run() {
		// 非 hadoop通用的方法
		while (true) {
			try {
				if (config.containsKey(proxyTimeSleep)) {
					Thread.sleep(Integer.parseInt(config.get(proxyTimeSleep)));
				} else {
					Thread.sleep(10000);
				}
			} catch (Exception e) {
			}
			// 需要判断redis中proxy的数量
			long size = MainProxy.getProxySize(config);
			
			long sizeConfig = Long.parseLong(config.get(proxyCountMinLimit));
//			System.out.println("读取大小:"+size+":"+sizeConfig);
			if (sizeConfig > size/2) {
				readProxyFile(config, configPath, sizeConfig - size/2);
			}

		}
	}

	/**
	 * 读取配置文件
	 * 
	 * @param config
	 * @param configPath
	 */
	public static void readProxyFile(HashMap<String, String> config,
			String configPath, long redisCount) {
		if (configPath == null) {
			return;
		}
//		System.out.println(configPath);
		ArrayList<String> list = HdfsFileGet.getFile(configPath);
//		for(String st:list)
//		{
//			System.out.println(st);
//		}
		if (list.size() > 0) {
			int zn = (int) (redisCount / list.size()+1);
			int count = 0;
			while (true) {
				for (String str : list) {
					IPProxy proxy = MainProxy.parseProxy(str);
					if (proxy != null) {
						MainProxy.addProxy(proxy, config);
					}
				}
				count++;
				if (count >= zn) {
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		HashMap<String, String> config = new HashMap<String, String>();
		String configFilePath = "hdfs://master:9000/storm_plugin_conf/infoQueue.properties";
		if (configFilePath != null) {
			ProxyTimerThread timer = new ProxyTimerThread(config,
					configFilePath);
			Thread thead = new Thread(timer);
			thead.start();
		}
	}
}

package com.fangcheng.plugin.base;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import com.db.Redis;

import clojure.main;

/**
 * 配置文件定时读取类
 * @author Administrator
 *
 */
public class TimerConfigThread extends Thread {
	// 創建 統一的配置文件目錄

	private HashMap<String, String> config = null;
	/**
	 * 配置文件地址
	 */
	private String configPath = null;
	

	
	public TimerConfigThread(HashMap<String, String> config, String configPath) {
		this.config = config;
		this.configPath = configPath;
	}

	public void run() {
		// 非 hadoop通用的方法
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
			}
			readConfig(config, configPath);
		}
	}

	/**
	 * 读取配置文件
	 * @param config
	 * @param configPath
	 */
	public static void readConfig(HashMap<String, String> config,
			String configPath) {
		if (configPath == null) {
			return;
		}
		ArrayList<String> list = HdfsFileGet.getFile(configPath);
		for (String str : list) {
			//System.out.println(str);
			int index = str.indexOf("=");
			//System.out.println(index);
			if (index <= 0 || index + 1 >= str.length()) {
				continue;
			}
			String key = str.substring(0, index).trim();
			String value = str.substring(index + 1, str.length()).trim();
			//System.out.println(key+":"+value);
			config.put(key, value);
		}
	}

	public static void main(String[] args) {
		HashMap<String, String> config = new HashMap<String, String>();
		String configFilePath = "hdfs://master:9000/storm_plugin_conf/infoQueue.properties";
		if (configFilePath != null) {
			TimerConfigThread timer = new TimerConfigThread(config,
					configFilePath);
			Thread thead = new Thread(timer);
			thead.start();
		}
	}
}

package com.fangcheng.kafka.Bean;


/**
 * 使用的配置文件地址
 * @author Administrator
 *
 */
public class ConfigStatic {

	/**
	 * hdfs地址
	 */
	public static String HDFS="hdfs://master:9000/storm_plugin_conf/";
	/**
	 * 通用配置文件地址
	 */
	public static String COMMON_PROPERTIES=HDFS+"infoQueue.properties";
	/**
	 * 代理文件的配置地址
	 */
	public static String PROXY_FILE=HDFS+"proxyTrue.txt";
}

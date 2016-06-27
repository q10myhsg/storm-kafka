package com.fangcheng.recommend.model.topology;

import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.util.JsonUtil;

public class Test {

	public static void main(String[] args) {
		//RecommendServiceConfig.init("hdfs://master:9000/storm_apps/onlineRecommend/config/config.xml");
		HdfsFileGet test=new HdfsFileGet();
		Config.readFileXml("hdfs://master:9000/storm_apps/onlineRecommend/config/config.xml");
		System.out.println(JsonUtil.getJsonStr(Config.configBean));
	}
}

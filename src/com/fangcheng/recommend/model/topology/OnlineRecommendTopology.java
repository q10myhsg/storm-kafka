package com.fangcheng.recommend.model.topology;

import storm.trident.TridentTopology;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;

import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.onlineModel.ModifyUserRank;
import com.fangcheng.recommend.model.onlineModel.SplitCluster;
import com.fangcheng.util.JsonUtil;

/**
 * 在线 流式 推荐模块
 * @author Administrator
 *
 */
public class OnlineRecommendTopology {
		public synchronized static void init(){
			if(com.fangcheng.recommend.model.config.Config.configBean==null){
				HdfsFileGet test=new HdfsFileGet();
				System.out.println(OutbreakDetector.class.getSimpleName()+"读取config");
				com.fangcheng.recommend.model.config.Config.readFileXml("hdfs://fcmaster-node:9000/storm_apps/onlineRecommend/config/config/config.xml");
				JsonUtil.getJsonStr(com.fangcheng.recommend.model.config.Config.configBean);
			}
		}
	
	   public static StormTopology buildTopology() {
	        TridentTopology topology = new TridentTopology();
	        UserLoggerEventSpout spout = new UserLoggerEventSpout();
	        storm.trident.Stream inputStream = topology.newStream("event",spout);
	        // Filter for error id events.
	        inputStream.each(new Fields("event"), new IdExistsFilter())
	        // update userWegith log
	        .each(new Fields("event"),new UpdateWeightAction(),new Fields("event2"))
	        // compute  this user  belong cluster id
	        .each(new Fields("event2"), new SplitCluster(), new Fields("cluster","userBean"))
	        
//	        .partitionAggregate(new MyAggregator(),new Fields("cluster2"))
//	        .parallelismHint(8)
	        //modify user feather data and simhash value
	        // .each(new Fields("cluster","userBean"), new SplitCluster(), new Fields())
	         
	        // Group occurrences in same city and hour
	        .partitionBy(new Fields("cluster"))
	        .each(new Fields("cluster","userBean"),new ModifyUserRank(), new Fields("primaryKey","oneCount"))
	        //.groupBy(new Fields("primaryKey"))
	        //    mystream.chainedAgg()
            //.partitionAggregate(new Count(), new Fields("count"))
            //.partitionAggregate(new Fields("b"), new Sum(), new Fields("sum"))
            //.chainEnd()
	        //count of cluster
	        //.persistentAggregate(new OutbreakTrendFactory(), new Count(), new Fields("count"))
	        //.newValuesStream()
	        //.each(new Fields("primaryKey","oneCount"), new OutbreakDetector(), new Fields("sum"));
	        .each(new  Fields("cluster","userBean"), new OutbreakDetector(), new Fields("sum"));
	       return  topology.build();
	    }

	public static void main(String[] args) throws Exception {
		//com.util.config.Config.init("H:\\eclipse\\workspaceML\\fcRecommendService\\WebContent\\build","\\config\\config.xml");
		//Config.r("hdfs://master:9000/storm_apps/onlineRecommend/config/config.xml");
		System.out.println("设置配置路径地址");
		//com.fangcheng.recommend.model.config.Config.path="hdfs://master:9000/storm_apps/onlineRecommend/config";
//		HdfsFileGet test=new HdfsFileGet();
//		System.out.println("初始化 配置文件");
//		com.fangcheng.recommend.model.config.Config.readFileXml("hdfs://fcmaster-node:9000/storm_apps/onlineRecommend/config/config/config.xml");
//		System.out.println(JsonUtil.getJsonStr(com.fangcheng.recommend.model.config.Config.configBean));
//		System.out.println("修改后的path:"+com.fangcheng.recommend.model.config.Config.path);
		Config conf = new Config();
		//System.out.println(JsonUtil.getJsonStr(com.fangcheng.recommend.model.config.Config.configBean));
		//System.out.println(JsonUtil.getJsonStr(com.fangcheng.recommend.model.config.ConfigData.areaSql));
		//conf.setDebug(true);
		String name = OnlineRecommendTopology.class.getSimpleName();
		//args=null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST, "fcnode-01");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf, buildTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf,buildTopology());
			Thread.sleep(1000000);
			cluster.shutdown();
		}
		
//		LocalCluster cluster = new LocalCluster();
//		cluster.submitTopology("", conf, buildTopology());
//		Thread.sleep(2000000);
//		cluster.shutdown();
	}
}

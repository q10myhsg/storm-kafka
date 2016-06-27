package com.fangcheng.stormKafka;

import java.util.HashMap;

import storm.kafka.KafkaSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.test.MyMongoDbTopology;
import com.test.SimpleBolt;
import com.test.MyMongoDbTopology.InsertIntoMongo;
import com.test.MyMongoDbTopology.SpoutMongo;


public class SpoutTest {

	
	public static void main(String[] args) throws Exception {
		String topic =TopicStatic.ERROR_LOG;//TopicStatic.TEST2;
		String groupId = "consumerTest3";
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-reader", new KafkaSpoutUtil(groupId,topic,new HashMap<String,String>()), 1); //
		// builder.setSpout("kafka-reader", new
		// KafkaSpout("Info","KafakTest_consumer",TopicStatic.TEST),1);
		builder.setBolt("word-splitter", new SimpleBolt(), 1).shuffleGrouping("kafka-reader");
		Config conf = new Config();
		conf.setDebug(true);
		String name=MyMongoDbTopology.class.getSimpleName();
		args=null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST,"master");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf,
					builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("word-count", conf, builder.createTopology());

			Thread.sleep(1000000);

			cluster.shutdown();
		}
	}
}

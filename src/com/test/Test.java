package com.test;

import java.util.Arrays;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

import com.fangcheng.kafka.Bean.TopicStatic;
import com.test.MyKafkaTopology.KafkaWordSplitter;
import com.test.MyMongoDbTopology.InsertIntoMongo;

public class Test {
	
	public static String getPath(Class obj)
	{
		return obj.getName();
	}

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
		String zks = "192.168.1.64:2181";
		String topic = "ERROR_LOG";//TopicStatic.DEBUGE_LOG;//TopicStatic.TEST2;
		//多个topic不能共用一个topic
		String zkRoot = "/storm/"+topic; // default
																	// zookeeper
																	// root
		// configuration for
		// storm
		String id = "0";
		BrokerHosts brokerHosts = new ZkHosts(zks);
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConf.forceFromStart = false;
		//spoutConf.startOffsetTime=-2;

		spoutConf.zkServers = Arrays.asList(new String[] { "192.168.1.64" });
		spoutConf.zkPort = 2181;

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 1); //
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
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("word-count", conf, builder.createTopology());

			Thread.sleep(1000000);

			cluster.shutdown();
		}
	}
}

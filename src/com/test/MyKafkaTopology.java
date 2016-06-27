package com.test;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.JsonMappingStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.StatusStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.util.JsonUtil;

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
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class MyKafkaTopology {

	public static class KafkaWordSplitter extends BaseRichBolt {
		private static final Log LOG = LogFactory
				.getLog(KafkaWordSplitter.class);
		private static final long serialVersionUID = 886149197481637894L;
		private OutputCollector collector;
		private KafkaUtil consumer = null;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
			consumer = new KafkaUtil();
		}

		@Override
		public void execute(Tuple input) {
			try {
				String line = input.getString(0);
				LOG.info("RECV[kafka -> splitter] " + line);
				KafkaTransmitBean kafkaTransmitBean = (KafkaTransmitBean) JsonUtil
						.getDtoFromJsonObjStr(line, KafkaTransmitBean.class);
				// LOG.info("解析完成");
				kafkaTransmitBean.topic = TopicStatic.TEST2;
				kafkaTransmitBean.partition = "1";
				kafkaTransmitBean.comment = "测试使用完成使用并反馈信息";
				StatusBean statusBean = new StatusBean();
				statusBean.execStatus = StatusStatic.EXEC_ING;
				kafkaTransmitBean.status = statusBean;
				consumer.sentMsgs(kafkaTransmitBean);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				kafkaTransmitBean.comment = "执行完成";
				kafkaTransmitBean.status.execStatus = StatusStatic.EXEC_SUCCESS;
				consumer.sentMsgs(kafkaTransmitBean);
				collector.ack(input);
			} catch (Exception e) {
				collector.fail(input);
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}

	}

	public static void main(String[] args) throws AlreadyAliveException,
			InvalidTopologyException, InterruptedException {
		// try {
		// ClassLoaderJar.reloadJar("./lib");
		// } catch (NoSuchMethodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String name = MyKafkaTopology.class.getSimpleName();
		String zks = "192.168.1.64:2181";
		String topic = TopicStatic.TEST;
		String zkRoot = "/storm/"+name; // default
																	// zookeeper
																	// root
		// configuration for
		// storm
		String id = "0";
		BrokerHosts brokerHosts = new ZkHosts(zks);
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConf.forceFromStart = false;

		spoutConf.zkServers = Arrays.asList(new String[] { "192.168.1.64" });
		spoutConf.zkPort = 2181;

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 1); // Kafka我们创建了一个5分区的Topic，这里并行度设置为1
		// builder.setSpout("kafka-reader", new
		// KafkaSpout("Info","KafakTest_consumer",TopicStatic.TEST),1);
		builder.setBolt("word-splitter", new KafkaWordSplitter(), 2)
				.globalGrouping("kafka-reader");
		// builder.setBolt("word-counter", new
		// WordCounter()).fieldsGrouping("word-splitter", new Fields("word"));

		Config conf = new Config();
		conf.setDebug(true);
		
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST,"master");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf,
					builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(3);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());
			Thread.sleep(600000);
			cluster.shutdown();
		}
	}
}

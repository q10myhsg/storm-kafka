package com.test;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.JsonMappingStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.StatusStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.util.JsonUtil;
import com.test.MyKafkaTopology.KafkaWordSplitter;

/**
 * 两个日志文件写入hdfs中
 * @author Administrator
 *
 */
public class LoggerToHdfsTopology {

	
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
			String line = input.getString(0);
			LOG.info("RECV[kafka -> splitter] " + line);
			KafkaTransmitBean kafkaTransmitBean = (KafkaTransmitBean) JsonUtil
					.getDtoFromJsonObjStr(line, KafkaTransmitBean.class);
			//LOG.info("解析完成");
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
			// String[] words = line.split("\\s+");
			// for(String word : words) {
			// LOG.info("EMIT[splitter -> counter] " + word);
			// collector.emit(input, new Values(word, 1));
			// }
			// collector.ack(input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}

	}
	public static void main(String[] args) {
		String zks = "192.168.1.64:2181";
		String topic = TopicStatic.TEST;
		String zkRoot = "/home/hduser/apps/storm-0.9.2-incubating"; // default zookeeper root
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
		builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 1);
		
	}
}

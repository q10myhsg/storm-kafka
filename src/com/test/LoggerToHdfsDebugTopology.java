package com.test;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy.TimeUnit;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;

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

import com.fangcheng.kafka.Bean.FieldStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.logger.base.JobFileNameFormat;
import com.fangcheng.logger.base.MyHdfsBolt;
import com.test.hdfs.StormToHDFSTopology.EventSpout;

/**
 * 两个日志文件写入hdfs中
 * 
 * @author Administrator
 *
 */
public class LoggerToHdfsDebugTopology {
	public static class SlitFiledsBolt extends BaseRichBolt {

		private static final Log LOG = LogFactory.getLog(EventSpout.class);
		private static final long serialVersionUID = 886149197481637894L;
		private OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
		}

		@Override
		public void execute(Tuple input) {
			try{
			//Utils.sleep(1);
			//DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			//Date d = new Date(System.currentTimeMillis());
			//String minute = df.format(d);
			String record = input.getString(0);
			String[] zn=record.split("\t");
			if(zn.length>0)
			{
				//LOG.info("EMIT[spout -> hdfs] " + zn[0] + " : " + record);
				collector.emit(new Values(zn[0], record));
			}
			collector.ack(input);
			}catch(Exception e)
			{
				collector.fail(input);
				e.printStackTrace();
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields(FieldStatic.JOB_NAME, FieldStatic.JOB_VALUE));
		}

	}

	public static void main(String[] args) throws AlreadyAliveException,
			InvalidTopologyException, InterruptedException {
		String name = LoggerToHdfsDebugTopology.class.getSimpleName();
		String zks = "192.168.1.64:2181";
		// 获取Debuge
		String topic = TopicStatic.DEBUGE_LOG;
		String zkRoot = "/consumers/"+name; // default zookeeper root
		// configuration for
		// storm
		String id = "0";
		BrokerHosts brokerHosts = new ZkHosts(zks);
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConf.forceFromStart = false;
//		spoutConf.startOffsetTime=0;
//		spoutConf.useStartOffsetTimeIfOffsetOutOfRange=true;
		spoutConf.zkServers = Arrays.asList(new String[] { "192.168.1.64" });
		spoutConf.zkPort = 2181;

		// use "|" instead of "," for field delimiter
		RecordFormat format = new DelimitedRecordFormat()
				.withFieldDelimiter(" : ");
		// 没多少条信息更新一次到hdfs中
		// sync the filesystem after every 1k tuples
		SyncPolicy syncPolicy = new CountSyncPolicy(100);
		// rotate files
		// 多久刷一次文件
		FileRotationPolicy rotationPolicy = new TimedRotationPolicy(1.0f,
				TimeUnit.MINUTES);

		JobFileNameFormat fileNameFormat = new JobFileNameFormat()
				.withPath("/storm_log/").withPrefix(TopicStatic.DEBUGE_LOG)
				.withExtension(".log");
//		FileNameFormat fileNameFormat = new DefaultFileNameFormat()
//		.withPath("/storm_log/").withPrefix(TopicStatic.DEBUGE_LOG)
//		.withExtension(".log");

		MyHdfsBolt hdfsBolt = new MyHdfsBolt().withFsUrl("hdfs://master:9000")
				.withFileNameFormat(fileNameFormat).withRecordFormat(format)
				.withRotationPolicy(rotationPolicy).withSyncPolicy(syncPolicy);
		
//		HdfsBolt hdfsBolt = new HdfsBolt().withFsUrl("hdfs://master:9000")
//				.withFileNameFormat(fileNameFormat).withRecordFormat(format)
//				.withRotationPolicy(rotationPolicy).withSyncPolicy(syncPolicy);
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 1);
		builder.setBolt("split-fields", new SlitFiledsBolt(), 2).shuffleGrouping("kafka-reader");
		builder.setBolt("hdfs-bolt", hdfsBolt, 4).fieldsGrouping(
				"split-fields", new Fields(FieldStatic.JOB_NAME));

		Config conf = new Config();
		args = null;
		
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST, "master");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopologyWithProgressBar(name, conf,
					builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());
			Thread.sleep(3600000);
			cluster.shutdown();
		}
	}
}

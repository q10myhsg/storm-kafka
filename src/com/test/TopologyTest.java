package com.test;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.fangcheng.logger.base.HdfsLoggerBasicBolt;
import com.fangcheng.logger.base.HdfsLoggerBasicSpout;

public class TopologyTest {

	public static class SpoutMongo extends HdfsLoggerBasicSpout {
		Log LOG = LogFactory.getLog(SpoutMongo.class);
		private SpoutOutputCollector collector;
		public int count=0;
		// 如果使用脚本则初始化使用此
		// public SplitSentence() {
		// super("python", "splitsentence.py");
		// }

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

		@Override
		public void nextTuple() {
				String msg="value:"+Math.random();
				// 调用发射方法
				//collector.emit(new Values(msg));
				count++;
				if(count%100==0)
				{
					debug(SpoutMongo.class,"spout:"+count);
					System.out.println("spout:"+count);
				}
				try {
					Thread.sleep(1000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				collector.emit(new Values(msg));
				// 模拟等待100ms
		}

		@Override
		public void open(Map arg0, TopologyContext arg1,
				SpoutOutputCollector arg2) {
			// TODO Auto-generated method stub
			this.collector = arg2;
			initLogger(TopologyTest.class);
		}
	}

	public static class InsertIntoMongo extends HdfsLoggerBasicBolt {
		Log LOG = LogFactory.getLog(SpoutMongo.class);
		public int count=0;

		@Override
		public void prepare(Map stormConf, TopologyContext context) {
			initLogger(TopologyTest.class);
		}
		
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}

		@Override
		public void execute(Tuple arg0, BasicOutputCollector arg1) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String input = arg0.getString(0);
			count++;
			if(count%100==0)
			{
				debug(SpoutMongo.class,"bolt:"+count);
				System.out.println("bolt:"+count);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("spout", new SpoutMongo(), 1);

		builder.setBolt("split", new InsertIntoMongo(), 1).shuffleGrouping(
				"spout");

		Config conf = new Config();
		//conf.setDebug(true);
		String name=TopologyTest.class.getSimpleName();
		//args=null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST,"master");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf,
					builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());

			Thread.sleep(10000000);

			cluster.shutdown();
		}
	}
}

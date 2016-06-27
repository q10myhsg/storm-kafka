package com.test;

import java.net.UnknownHostException;
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
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.fangcheng.logger.base.HdfsLoggerBasicBolt;
import com.fangcheng.logger.base.HdfsLoggerRichSpout;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class MyMongoDbTopology {

	public static class SpoutMongo extends HdfsLoggerRichSpout {
		Log LOG = LogFactory.getLog(SpoutMongo.class);
		private SpoutOutputCollector collector;
		DBCursor cursor = null;
		DB db = null;
		DBCollection collection=null;
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
			if (cursor.hasNext()) {
				String msg = cursor.next().toString();
				//LOG.info("input:" + msg);
				debug(SpoutMongo.class, "mongo");
				// 调用发射方法
				try {
					Thread.sleep(90);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				collector.emit(new Values(msg));
				// 模拟等待100ms
			}else{
				debug(SpoutMongo.class, "restart mongo");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cursor.close();
				cursor= collection.find();
			}
		}

		@Override
		public void open(Map arg0, TopologyContext arg1,
				SpoutOutputCollector arg2) {
			// TODO Auto-generated method stub
			initLogger(MyMongoDbTopology.class);
			this.collector = arg2;
			Mongo mongo = null;
			try {
				mongo = new Mongo("192.168.1.11", 27017);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db=mongo.getDB("demo");
			collection = db.getCollection("job51Company");
			this.cursor = collection.find();
		}
	}

	public static class InsertIntoMongo extends HdfsLoggerBasicBolt {
		Log LOG = LogFactory.getLog(SpoutMongo.class);
		DBCollection collection = null;

		@Override
		public void execute(Tuple tuple, BasicOutputCollector collector) {
			String input = tuple.getString(0);
			JsonNode json = JsonUtil.parse(input);
			//System.out.println("collection:" + collection);
			//System.out.println("jsonBolt:" + json);
			debug(InsertIntoMongo.class,input);
			StringBuffer sb = new StringBuffer("{companyCode:");
			sb.append(json.get("companyCode").textValue()).append("}");
			collection.insert((DBObject) JSON.parse(sb.toString()));

		}

		@Override
		public void prepare(Map config, TopologyContext context) {
			initLogger(MyMongoDbTopology.class);
			Mongo mongo = null;
			// System.out.println("初始化");
			try {
				mongo = new Mongo("192.168.1.11", 27017);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DB db = mongo.getDB("demo");
			collection = db.getCollection("job51CompanyBak");
			// System.out.println("初始化：" + collection);
			// System.exit(1);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}
	}

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("spout", new SpoutMongo(), 1);

		builder.setBolt("split", new InsertIntoMongo(), 2).shuffleGrouping(
				"spout");

		Config conf = new Config();
		//conf.setDebug(true);
		String name=MyMongoDbTopology.class.getSimpleName();
		//args=null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST,"master");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf,
					builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());

			Thread.sleep(1000000);

			cluster.shutdown();
		}
	}
}

package com.fangcheng.plugin.proxy;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import com.db.Redis;
import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.plugin.base.TimerConfigThread;
import com.fangcheng.plugin.newBusiness.newBusinessFang.IPProxy;
import com.fangcheng.plugin.newBusiness.newBusinessFang.MainProxy;
import com.fangcheng.stormKafka.KafkaSpoutUtil;

/**
 * 调用搜房页面的parse方法
 * 
 * @author Administrator
 *
 */
public class ProxyPlugin extends BaseBasicBolt {
	// Log LOG = LogFactory.getLog(TaskExecMethod.class);
	/**
	 * kafka api consumer
	 */
	public KafkaUtil consumer = null;
	
	/**
	 * 配置文件
	 */
	public HashMap<String, String> config = null;
	/**
	 * 如果为空则不需要家在配置文件
	 */
	public String configFilePath = "hdfs://fcmaster-node:9000/storm_plugin_conf/infoQueue.properties";
	/**
	 * 代理ip的物理地址
	 */
	public String proxyFilePath="hdfs://fcmaster-node:9000/storm_plugin_conf/proxy.txt";

	public ProxyPlugin() {
	}

	private OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		init();
	}
	public void init() {
		config = new HashMap<String, String>();
		TimerConfigThread.readConfig(config, configFilePath);
		consumer = new KafkaUtil();
		if (configFilePath != null) {
			TimerConfigThread timer = new TimerConfigThread(this.config,
					configFilePath);
			timer.start();
		}
		try {
			initDB();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		ProxyTimerThread proxyTimer=new ProxyTimerThread(this.config,proxyFilePath);
		proxyTimer.start();
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		String input = tuple.getString(0);
		if(input.length()>0)
		{
			IPProxy proxy=MainProxy.parseProxy(input);
			if(proxy!=null)
			{
				MainProxy.addProxy(proxy, config);
				//需要新增到代理配置文件中
			}
		}
	}
	/**
	 * 初始化数据库的使用
	 * 
	 * @throws Exception
	 */
	public void initDB()
	{
		MainProxy.setRedis(new Redis(config.get("proxyRedisIp"),Integer.parseInt(config.get("proxyRedisPort"))));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// declarer.declare(new Fields("word", "count"));
	}
	
	public static void main(String[] args) throws Exception {
		String name = ProxyPlugin.class.getSimpleName();
		String topic = TopicStatic.ADD_NEW_PROXY;
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka_" + topic,
				new KafkaSpoutUtil(name, topic, null), 1);
		builder.setBolt(
				"代理定时添加器",
				new ProxyPlugin(), 1)
				.globalGrouping("kafka_" + topic);
		Config conf = new Config();
		// conf.setDebug(true);
		//args = null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST, "fcnode-01");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf, builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());

			Thread.sleep(1000000);
			cluster.shutdown();
		}
	}
}

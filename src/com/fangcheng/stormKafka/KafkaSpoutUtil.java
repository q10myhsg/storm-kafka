package com.fangcheng.stormKafka;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import com.db.MysqlConnection;
import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.logger.base.HdfsLoggerBasicSpout;
import com.fangcheng.logger.base.HdfsLoggerRichSpout;
import com.test.MyKafkaTopology.KafkaWordSplitter;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/**
 * kafka 调用通用类
 * 
 * @author Administrator
 *
 */
public class KafkaSpoutUtil extends HdfsLoggerBasicSpout {

	//private static final Log LOG = LogFactory.getLog(KafkaSpoutUtil.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String groupId = "";
	private String topic = "";

	private String fieldsName = "kafkaInfo";
	private transient KafkaUtil kafkaUtil = null;
	private HashMap<String,String> kafkaConfigMap=null;

	
//	private ConcurrentHashMap<UUID,Values> pending;
	/**
	 * consumer 通用调用类
	 */
	ConsumerIterator<String, String> it = null;

	public KafkaSpoutUtil(String groupId, String topic, HashMap<String,String> kafkaConfigMap) {
		this.groupId = groupId;
		this.topic = topic;
		this.kafkaConfigMap = kafkaConfigMap;
	}

	private SpoutOutputCollector collector;

	@Override
	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2) {
		// TODO Auto-generated method stub
		initLogger(KafkaSpoutUtil.class);
		initKafkaConsumer();
		collector=arg2;
	}
//	public void open(Map config, TopologyContext context,
//			SpoutOutputCollector collector) {
//		this.collector = collector;
//		pending=new ConcurrentHashMap<UUID,Values>();
//		initKafkaConsumer();
//	}

	public void nextTuple() {
		
		try {
			if (it.hasNext()) {
				//System.out.println("value:" + it.next().message());
				String zn=it.next().message();
				//UUID uuid=UUID.randomUUID();
				Values value=new Values(zn);
				//pending.put(uuid, value);
				//this.collector.emit(value,uuid);
				this.collector.emit(value);
			}
		} catch (Exception e) {
			//是否需要重连？
			//initKafkaConsumer();
			//error(KafkaSpoutUtil.class,e.getMessage());
			//e.printStackTrace();
			//System.exit(1);
			initKafkaConsumer();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
//	@Override
//	public void ack(Object msgId)
//	{
//		//System.out.println("前:"+this.pending.size());
//		//System.out.println("kafka 获取数据成功 删除:"+msgId.toString());
//		//this.pending.remove((UUID)msgId);
//		//System.out.println("后:"+this.pending.size());
//	}
//	@Override
//	public void fail(Object msgId)
//	{
//		//System.out.println("kafka 获取数据失败 重复发送:"+msgId.toString());
//		//this.collector.emit(pending.get((UUID)msgId),msgId);
//	}
	/**
	 * 初始化消费者队列
	 */
	public void initKafkaConsumer()
	{
		KafkaStream stream = null;
		kafkaUtil=new KafkaUtil();
		//System.out.println(kafkaConfigMap);
		if(kafkaConfigMap!=null)
		{
			for(Entry<String,String> map:kafkaConfigMap.entrySet())
			{				
				kafkaUtil.props.props.put(map.getKey(),map.getValue());
			}
		}
		//如果需要修改参数则需要修改对应数据
//		System.out.println(kafkaUtil+"\t"+groupId+"\t"+topic);
		stream = kafkaUtil.getConnectioin(groupId, topic);
		if (stream == null) {
			//error(KafkaSpoutUtil.class,"stream流为空 请检查 topic相关信息");
			//System.exit(1);
		}
		it = stream.iterator();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields(fieldsName));
	}


}

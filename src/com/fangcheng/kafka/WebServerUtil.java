package com.fangcheng.kafka;

import java.util.HashMap;

import com.db.MysqlConnection;
import com.fangcheng.kafka.Bean.JsonMappingStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.StatusStatic;
import com.fangcheng.plugin.base.TagStatic;
import com.fangcheng.plugin.base.TaskCommonControl;
import com.fangcheng.plugin.base.TaskCommonPlugin;
import com.fangcheng.plugin.base.TimerConfigThread;
import com.fangcheng.util.JsonUtil;

import kafka.message.MessageAndMetadata;

public abstract class WebServerUtil  extends TaskCommonPlugin implements Runnable{
	public String groupId=null;
	public String topic=null;
	
	public KafkaTransmitBean kafkaBean=null;
	public WebServerUtil(Class jobName,Class boltName,String groupId,String topic,String parentTopicName)
	{
		super(jobName, boltName,topic, parentTopicName,false);
		this.groupId=groupId;
		this.topic=topic;
		this.consumer = new KafkaUtil(this);
		config = new HashMap<String, String>();
		TimerConfigThread.readConfig(config, configFilePath);
		initLogger(jobName);
		
	}
	
	
	public void run(MessageAndMetadata<String, String> msg)
	{
		KafkaTransmitBean kafkaBean=consumer(msg);
		if(kafkaBean==null)
		{
			return;
		}
		this.kafkaBean=kafkaBean;
		compute(kafkaBean);
	}
	/**
	 * 该方法需要被重写
	 * @param kafkaBean
	 */
	public void compute(KafkaTransmitBean kafkaBean)
	{
		execInfo(kafkaBean);
	}
	
	/**
	 * 该方法 需要被覆盖
	 * @param msg
	 */
	public KafkaTransmitBean  consumer(MessageAndMetadata<String, String> msg)
	{
		//debug(boltName,msg.message());
		KafkaTransmitBean kafkaBean=null;
		try{
//		System.out.println("key2:" + msg.key() + "\tinput2:"
//				+ msg.message());
		kafkaBean=(KafkaTransmitBean)JsonUtil.getDtoFromJsonObjStr(msg.message(),KafkaTransmitBean.class);
		}catch(Exception e)
		{
			error(boltName,"数据信息异常:"+msg.message());
			e.printStackTrace();
		}
		return kafkaBean;
	}
	/**
	 * 线程
	 */
	public void run()
	{
		consumer.getMsgs(groupId,topic);
	}
	
}

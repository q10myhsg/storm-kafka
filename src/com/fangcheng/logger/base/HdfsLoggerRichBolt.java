package com.fangcheng.logger.base;

import java.util.Date;

import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy.TimeUnit;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;

import backtype.storm.topology.base.BaseRichBolt;

import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.TopicStatic;

/**
 * 日志logger 写入hdfs
 * @author Administrator
 *
 */
public abstract class HdfsLoggerRichBolt extends BaseRichBolt{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static KafkaUtil consumer = null;
	
	private static String objName=null;
	/**
	 * 设定使用类 的标记名
	 * @param obj
	 */
	public static void initLogger(Class obj)
	{
		objName=obj.getSimpleName();
	}
	
	/**
	 * 发送错误日志的消息
	 * @param obj 所述类
	 * @param msg 消息
	 */
	public static void error(Class obj,String msg)
	{
		if(consumer==null)
		{
			consumer = new KafkaUtil();
		}
		consumer.sentMsgs(TopicStatic.ERROR_LOG,"0",objName+"\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		consumer.sentMsgs(TopicStatic.DEBUGE_LOG,"0",objName+"\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
	}
	
	/**
	 * 发送错误日志的消息
	 * @param obj 所述类
	 * @param tag 输入内容的标签
	 * @param msg 消息
	 */
	public static void error(Class obj,String tag,String msg)
	{
		if(consumer==null)
		{
			consumer = new KafkaUtil();
		}
		if(tag!=null)
		{
			consumer.sentMsgs(TopicStatic.ERROR_LOG,"0",objName+"\tnull\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		}else{
			consumer.sentMsgs(TopicStatic.ERROR_LOG,"0",objName+"\t"+tag+"\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		}
		if(tag!=null)
		{
			consumer.sentMsgs(TopicStatic.DEBUGE_LOG,"0",objName+"\tnull\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		}else{
			consumer.sentMsgs(TopicStatic.DEBUGE_LOG,"0",objName+"\t"+tag+"\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		}
	}

	/**
	 * 
	 * @param obj 作用的class
	 * @param msg 消息
	 */
	public static void debug(Class obj,String msg)
	{
		if(consumer==null)
		{
			consumer = new KafkaUtil();
		}
		consumer.sentMsgs(TopicStatic.DEBUGE_LOG,"0",objName+"\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
	}
	
	/**
	 * 
	 * @param obj 作用的class
	 * @param tag 输入内容的标签
	 * @param msg 消息
	 */
	public static void debug(Class obj,String tag,String msg)
	{
		if(consumer==null)
		{
			consumer = new KafkaUtil();
		}
		if(tag!=null)
		{
			consumer.sentMsgs(TopicStatic.DEBUGE_LOG,"0",objName+"\tnull\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		}else{
			consumer.sentMsgs(TopicStatic.DEBUGE_LOG,"0",objName+"\t"+tag+"\t"+new Date()+"\t"+obj.getName()+"\t"+msg+"\n");
		}
	}
	
	

	public static void main(String[] args) {
		System.out.println(HdfsLoggerRichBolt.class.getSimpleName());
		System.exit(1);
		RecordFormat format = new DelimitedRecordFormat()
				.withFieldDelimiter(" : ");

		// sync the filesystem after every 1k tuples
		SyncPolicy syncPolicy = new CountSyncPolicy(1000);

		// rotate files
		FileRotationPolicy rotationPolicy = new TimedRotationPolicy(1.0f,
				TimeUnit.MINUTES);
		//执行写入固定地址，如果想使用不同的地址需要求改 execute核
		FileNameFormat fileNameFormat = new DefaultFileNameFormat()
				.withPath("/storm/").withPrefix("app_").withExtension(".log");

		HdfsBolt hdfsBolt = new HdfsBolt().withFsUrl("hdfs://fcmaster-node:9000")
				.withFileNameFormat(fileNameFormat).withRecordFormat(format)
				.withRotationPolicy(rotationPolicy).withSyncPolicy(syncPolicy);
	}
}

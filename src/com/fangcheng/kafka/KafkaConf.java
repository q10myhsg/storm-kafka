package com.fangcheng.kafka;

import java.util.Properties;

import backtype.storm.spout.MultiScheme;
import backtype.storm.spout.RawMultiScheme;
import storm.kafka.BrokerHosts;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConf {
	
	/** 一个借口，实现类有ZkHosts，和StatisHosts    **/
//
//	public final BrokerHosts hosts;
//
//	public final String topic; // kafka topic name
//
//	public final String clientId; // 自己取一个唯一的ID吧
//
//	public int fetchSizeBytes = 1024 * 1024; // 每次从kafka读取的byte数，这个变量会在KafkaUtils的fetchMessage方法中看到
//
//	public int socketTimeoutMs = 10000; //  Consumer连接kafka server超时时间
//
//	public int fetchMaxWait = 10000;
//
//	public int bufferSizeBytes = 1024 * 1024;   //Consumer端缓存大小
//
//	public MultiScheme scheme = new RawMultiScheme(); // 数据发送的序列化和反序列化定义的Scheme，后续会专门有一篇介绍
//
//	public boolean forceFromStart = false;  // 和startOffsetTime，一起用，默认情况下，为false，一旦startOffsetTime被设置，就要置为true
//
//	public long startOffsetTime = kafka.api.OffsetRequest.EarliestTime(); // -2 从kafka头开始  -1 是从最新的开始 0 =无 从ZK开始
//
//	public long maxOffsetBehind = Long.MAX_VALUE;  // 每次kafka会读取一批offset存放在list中，当zk offset比当前本地保存的commitOffse相减大于这个值时，重新设置commitOffset为当前zk offset，代码见PartitionManager
//
//	public boolean useStartOffsetTimeIfOffsetOutOfRange = true;
//
//	public int metricsTimeBucketSizeInSecs = 60;

	/**
	 * ZooKeeper的最大超时时间，就是心跳的间隔，若是没有反映，那么认为已经死了，不易过大
	 */
	public final String ZOOKEEPR_SESSION_TIMEOUT_MS = "zookeeper.session.timeout.ms";
	/**
	 * ZooKeeper集群中leader和follower之间的同步实际那
	 */
	public final String ZOOKEEPER_SYNC_TIME_MS = "zookeeper.sync.time.ms";
	/**
	 * zooker链接超时时间
	 */
	public final String ZOOKEEPER_CONNECTIONTIMEOUT_MS = "zookeeper.connectiontimeout.ms";
	/**
	 * 自动提交对应的 周期时间
	 */
	public final String AUTO_COMMIT_INTERVAL_MS = "auto.commit.interval.ms";
	/**
	 * 对应的groupid 的consumer 在重启后 开始消费消息的位置
	 * smallest为zookeeper，largest为新的消息
	 * 输入的msg对应的类型 smallest largest anything
	 */
	public final String AUTO_OFFSET_RESET = "auto.offset.reset";
	/**
	 * 序列化类
	 */
	public final String SERIALIZEER_CLASS = "serializer.class";
	/**
	 * 配置key的序列化类
	 */
	public final String KEY_SERIALIZER_CLASS = "key.serializer.class";
	/**
	 * 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失 值为0,1,-1,可以参考
	 * request.required.acks 0, which means that the producer never waits for an
	 * acknowledgement from the broker (the same behavior as 0.7). This option
	 * provides the lowest latency but the weakest durability guarantees (some
	 * data will be lost when a server fails). 两个不同的消费者 会记录自己的检索位置 1, which
	 * means that the producer gets an acknowledgement after the leader replica
	 * has received the data. This option provides better durability as the
	 * client waits until the server acknowledges the request as successful
	 * (only messages that were written to the now-dead leader but not yet
	 * replicated will be lost). -1, which means that the producer gets an
	 * acknowledgement after all in-sync replicas have received the data. This
	 * option provides the best durability, we guarantee that no messages will
	 * be lost as long as at least one in sync replica remains.
	 */
	public final String REQUEST_REQUIRED_ACKS = "request.required.acks";
	/**
	 * 生产者的分区方法类
	 */
	public final String PARTITIONER_CLASS = "partitioner.class";
	/**
	 * 组id
	 */
	public final String GROUP_ID="group.id";
	/**
	 * 基础配置类
	 */
	public Properties props = null;
	public KafkaConf(String groupId){
		this();
		setGroupId(groupId);
	}
	
	public KafkaConf() {
		props = new Properties();
		// zookeeper 配置
//		props.put("zookeeper.connect", "master:2181,slave1:2181,slave2:2181");
////		props.put("zookeeper.connect", "192.168.85.11:2181");
//		// 如果为 produce则需要 broker项
//		props.put("metadata.broker.list", "master:9092,slave1:9092,slave2:9092");//,slave1:9092,slave2:9092
//		props.put("metadata.broker.list", "192.168.85.11:9092");
		
		props.put("zookeeper.connect", "fcnode-02:2181,fcnode-03:2181,fcnode-04:2181");
		// 如果为 produce则需要 broker项
		props.put("metadata.broker.list", "fcnode-11:9092,fcnode-12:9092");//,slave1:9092,slave2:9092
		// zk连接超时
		props.put(ZOOKEEPR_SESSION_TIMEOUT_MS, "4000");
		props.put("zookeeper.connectiontimeout.ms", "1000000");
		props.put(ZOOKEEPER_SYNC_TIME_MS, "200");
		props.put(AUTO_COMMIT_INTERVAL_MS, "1000");
		props.put(AUTO_OFFSET_RESET, "smallest");
		props.put(AUTO_COMMIT_INTERVAL_MS, "1000");

		// 序列化类
		props.put(SERIALIZEER_CLASS, "kafka.serializer.StringEncoder");
		// 配置key的序列化类
		props.put(KEY_SERIALIZER_CLASS, "kafka.serializer.StringEncoder");
		// 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
		// 值为0,1,-1,可以参考
		// http://kafka.apache.org/08/configuration.html
		props.put(REQUEST_REQUIRED_ACKS, "1");
	}
	/**
	 * 如果为消费者则需要设置groupId，不同的groupId会重复消费一个topic
	 * @param groupId
	 */
	public void setGroupId(String groupId)
	{
		if(groupId!=null)
		// group 代表一个消费组
		props.put(GROUP_ID, groupId);
	}
	
	public String getGroupId()
	{
		return props.getProperty(GROUP_ID);
	}
	/**
	 * 设置分区方法
	 * @param classPath
	 */
	public void setPartionerMethod(String classPath) {
		// 可选配置，如果不配置，则使用默认的partitioner
		// props.put("partitioner.class",
		// "com.catt.kafka.demo.PartitionerDemo");
		props.put(PARTITIONER_CLASS, classPath);
	}
	/**
	 * 创建 java 的消费者控制器
	 * @return
	 */
	public ConsumerConnector createJavaConsumerConnector()
	{
		ConsumerConfig config = new ConsumerConfig(props);
		return kafka.consumer.Consumer.createJavaConsumerConnector(config);
	}
}

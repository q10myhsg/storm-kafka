package com.fangcheng.kafka;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.util.JsonUtil;

/**
 * kafka 通用接口类
 * 
 * @author Administrator
 *
 */
public class KafkaUtil {

	/**
	 * 消费者连接器
	 */
	public ConsumerConnector consumer;

	/**
	 * 生产者连接器
	 */
	public Producer<String, String> producer = null;
	/**
	 * 封装的配置文件
	 */
	public KafkaConf props = null;
	/**
	 * jar文件目录
	 */
	public final String libPath = "./lib";

	/**
	 * 预加载jar s
	 */
	public KafkaUtil() {
	}

	public WebServerUtil server = null;

	public KafkaUtil(WebServerUtil server) {
		this.server = server;
	}

	/**
	 * 设置配置文件
	 * 
	 * @param props
	 */
	public void setProps(KafkaConf props) {
		this.props = props;
	}

	public void setPropsParam(String config, String param) {
		this.props.props.setProperty(config, param);
	}

	/**
	 * 获取消息
	 * 
	 * @param groupId
	 * @param topic
	 * @param msg
	 * @return
	 */
	public boolean getMsgs(String groupId, String topic) {
		return consumer(groupId, topic);
	}

	/**
	 * 获取消费者连接状态
	 * 
	 * @param groupId
	 * @param topic
	 * @return
	 */
	public KafkaStream<String, String> getConnectioin(String groupId,
			String topic) {
		if (props == null) {
			props = new KafkaConf(groupId);
			consumer = props.createJavaConsumerConnector();
		} else if (!groupId.equals(props.getGroupId())) {
			props.setGroupId(groupId);
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));

		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
		StringDecoder valueDecoder = new StringDecoder(
				new VerifiableProperties());

		Map<String, List<KafkaStream<String, String>>> consumerMap = null;
		while (true) {
			try {
				consumerMap = consumer.createMessageStreams(topicCountMap,
						keyDecoder, valueDecoder);
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
		return stream;
	}

	/**
	 * 发送一个object
	 * 
	 * @param kafkaTransmitBean
	 *            传递的基本类
	 * @return
	 */
	public boolean sentMsgs(KafkaTransmitBean kafkaTransmitBean) {
		initProduceConfig();
		boolean flag = true;
		while (true) {
			try {
				// System.out.println(JsonUtil.getJsonStr(kafkaTransmitBean));
				// System.out.println("producer:"+producer);
				producer.send(new KeyedMessage<String, String>(
						kafkaTransmitBean.topic, kafkaTransmitBean.partition,
						JsonUtil.getJsonStr(kafkaTransmitBean)));
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 发送消息
	 * 
	 * @param topic
	 *            主题
	 * @param key
	 *            主建
	 * @param msg
	 *            信息
	 * @return
	 */
	public boolean sentMsgs(String topic, String key, String msg) {
		initProduceConfig();
		boolean flag = true;
		while (true) {
			try {
				producer.send(new KeyedMessage<String, String>(topic, key, msg));
				break;
			} catch (Exception e) {
				e.printStackTrace();
				// flag = false;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 初始化配置
	 */
	public void initProduceConfig() {
		if (props == null) {
			props = new KafkaConf(null);
		}
		if (producer == null) {
			System.out.println(JsonUtil.getJsonStr(props.props));
			producer = new Producer<String, String>(new ProducerConfig(
					props.props));
		}
	}

	/**
	 * 消费者执行
	 * 
	 * @param groupId
	 *            组id
	 * @param topic
	 *            topic id
	 * @param msg
	 *            信息
	 * @return
	 */
	public boolean consumer(String groupId, String topic) {
		if (props == null) {
			props = new KafkaConf(groupId);
			consumer = props.createJavaConsumerConnector();
		} else if (!groupId.equals(props.getGroupId())) {
			props.setGroupId(groupId);
		}
		boolean result = consumer(topic);
		return result;
	}

	/**
	 * 消费者执行
	 * 
	 * @param topic
	 * @param msg
	 * @return
	 */
	public boolean consumer(String topic) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
		StringDecoder valueDecoder = new StringDecoder(
				new VerifiableProperties());
		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer
				.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
		KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
		boolean flag = false;
		try {
			flag = consumer(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 消费者执行
	 * 
	 * @param groupId
	 *            组id
	 * @param topic
	 *            topic id
	 * @param msg
	 *            信息
	 * @param partitionIndex
	 * @return
	 */
	public boolean consumer(String groupId, String topic, int partitionIndex) {
		if (props == null) {
			props = new KafkaConf(groupId);
			consumer = props.createJavaConsumerConnector();
		} else if (!groupId.equals(props.getGroupId())) {
			props.setGroupId(groupId);
		}
		boolean result = consumer(topic, partitionIndex);
		return result;
	}

	/**
	 * 消费者执行
	 * 
	 * @param topic
	 * @param msg
	 * @return
	 */
	public boolean consumer(String topic, int index) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));// 每次说去的数量
		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
		StringDecoder valueDecoder = new StringDecoder(
				new VerifiableProperties());
		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer
				.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
		KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
		boolean flag = false;
		try {
			flag = consumer(stream);
		} catch (Exception e) {

		}
		return flag;
	}

	/**
	 * 消费者执行的覆盖方法
	 * 
	 * @param stream
	 * @return
	 */
	public boolean consumer(KafkaStream<String, String> stream) {
		ConsumerIterator<String, String> it = stream.iterator();
		boolean flag = true;
		try {
			if (server == null) {
				while (it.hasNext()) {
					consumer(it.next());
				}
			} else {
				while (it.hasNext()) {
					server.run(it.next());
				}
			}
		} catch (Exception e) {
			return false;
		}
		return flag;
	}

	public void consumer(MessageAndMetadata<String, String> oneMsg) {
		System.out.println("key:" + oneMsg.key() + "\t:input:"
				+ oneMsg.message());
		try {
			Thread.sleep(0);
			// Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

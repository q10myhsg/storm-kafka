package com.fangcheng.recommend.model.topology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.bean.UserGraphBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 从 kafka 中获取 对应的 用户 行为 信息 触发流
 * 
 * @author Administrator
 *
 */
public class InputStreamEventEmitter implements Emitter<Long>, Serializable {

	private static final long serialVersionUID = 1L;
	AtomicInteger successfulTransactions = new AtomicInteger(0);
	private KafkaUtil kafkaUtil = null;

	private ConsumerIterator<String, String> it = null;

	@Override
	public void emitBatch(TransactionAttempt paramTransactionAttempt,
			Long paramX, TridentCollector paramTridentCollector) {
		// TODO Auto-generated method stub
		// 从kafka中获取数据
		System.out.println("初始化消息");
		OnlineRecommendTopology.init();
		// System.out.println(JsonUtil.getJsonStr(Config.configBean));
		kafkaUtil = new KafkaUtil();
		// initKafka();
		// 从 用户 触发日志获取相关信息
		System.out.println(JsonUtil.getJsonStr(Config.configBean));
		System.out.println("消息队列配置:" + Config.configBean.kafkaGroupId
				+ "\\\\\\\\\\\\\\\\\\\\\\"
				+ Config.configBean.kafkaUserActionTopic);
		while (true) {
			if (Config.configBean.kafkaUserActionTopic == null) {
				OnlineRecommendTopology.init();
			} else {
				break;
			}
		}
		//
		System.out.println("消息队列配置:" + "消息队列启动");
		while (true) {
			try {
				String str = nextMsg();
				// System.out.println("消息传入:" + str);
				JsonNode json = JsonUtil.parse(str);
				// 需要判断是什么内容
				// 1 对应用户的请求日志
				// 目前不处理，意义不大
				// 定位请求页的位置
				// 2对应用户的点击日志

				JsonNode userId = json.get("userId");
				if (userId == null) {
					continue;
				}
				int type = json.get("type").asInt();// 类别
				long id = json.get("id").asLong();// 实际id
				long refId = json.get("refId").asLong();// 有网络交流的id
				int city = json.get("city").asInt();// 城市
				int num = 1;
				int category = 0;
				int index = -1;
				if (json.has("count")) {
					num = json.get("count").asInt();// 交流的数值//存在正向交流 和逆向交流
				}
				if (json.has("cu")) {
					// 业态
					category = json.get("cu").asInt();
				}
				if (json.has("index")) {
					index = json.get("index").asInt();
				}
				int reCate=-1;
				if(json.has("reCate")){
					reCate=json.get("reCate").asInt();
				}
				List<Object> events = new ArrayList<Object>();
				UserGraphBean bean = new UserGraphBean(userId.asText(),reCate, type,
						city, category, index, id, refId, num);
				events.add(bean);
				paramTridentCollector.emit(events);
			} catch (Exception e) {
			}
		}
	}

	public String nextMsg() {
		// System.out.println("获取信息");
		while (true) {
			if (it == null) {
				it = initKafka();
				// System.out.println("it:初始化成功");
			}
			try {
				// System.out.println("准备获取数据");
				if (it.hasNext()) {
					return it.next().message();
				}
			} catch (Exception e) {
				e.printStackTrace();
				it = initKafka();
			}
		}
	}

	public ConsumerIterator<String, String> initKafka() {
		KafkaStream<String, String> stream = null;
		if (stream == null) {
			while (true) {
				try {
					stream = kafkaUtil.getConnectioin(
							Config.configBean.kafkaGroupId,
							Config.configBean.kafkaUserActionTopic);

					if (stream != null) {
						ConsumerIterator<String, String> it = stream.iterator();
						if (it == null) {
							System.out.println("stream:it:空");
						} else {
							return it;
						}
					} else {
						System.out.println("stream:空");
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public void success(TransactionAttempt paramTransactionAttempt) {
		// TODO Auto-generated method stub
		successfulTransactions.incrementAndGet();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}

package com.fangcheng.kafka;

import kafka.message.MessageAndMetadata;

import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.util.JsonUtil;

/**
 * kafka测试方法
 * 
 * @author Administrator
 *
 */
public class KafkaTest {
	
	public static String split=",";
	public static String getString(String... strList)
	{
		StringBuffer sb=new StringBuffer();
		int i=0;
		for(String str:strList)
		{
			i++;
			if(i==1)
			{
				sb.append(str);
			}else{
				sb.append(KafkaTest.split).append(str);
			}
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		
		//jar加载器
		
		KafkaUtil consumer = new KafkaUtil() {
			@Override
			public void consumer(MessageAndMetadata<String, String> msg) {
				System.out.println("key2:" + msg.key() + "\tinput2:"
						+ msg.message());
//				KafkaTransmitBean kafkaBean=(KafkaTransmitBean)JsonUtil.getDtoFromJsonObjStr(msg.message(),KafkaTransmitBean.class,JsonMappingStatic.kafkaTransmitBeanMap);
//				System.out.println("topic:"+kafkaBean.topic);
//				System.out.println("status:"+kafkaBean.status.execStatus);
				//System.out.println("结束");
				try {
					Thread.sleep(0);
					// Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		//consumer.sentMsgs("HDFS_LOG","1","sldjflsdjf");
		//System.exit(1);
		// 消费者处理消息
		String topic =TopicStatic.ADD_NEW_BUSINESS;//TopicStatic.DEBUGE_LOG;//TopicStatic.TEST2;
		//topic="test4";
		String groupId = "consumerTest3";
		//consumer.getMsgs(groupId, topic);
		String key = "0";
//		String temp="\"{\"baidu\":{\"lng\":116.508803,\"lat\":39.809128},\"id\":360,\"name\":\"北京华联力宝购物中心\",\"city\":\"北京\"}\\\"";
//		System.out.println(temp);
//		System.exit(1);
		for (int i = 0; i < 1; i++) {
			KafkaTransmitBean kafkaTransmitBean=new KafkaTransmitBean();
			kafkaTransmitBean.topic=topic;
		//	kafkaTransmitBean.topic=TopicStatic.ADD_NEW_BUSSINESS_FANG;
			
			
			kafkaTransmitBean.partition="1";
			kafkaTransmitBean.comment="测试使用";
			kafkaTransmitBean.jobId=-11;
			ParamsBean params=new ParamsBean();
			
//			params.jobType=JobStatic.FANG;
//			params.requestData=JsonUtil.init();
//			params.requestData.put(ParamsStatic.URL, "http://tongfangjiaoyu.fang2.com/");
			
//			params.jobType=JobStatic.DP;
//			params.requestData=new JSONObject();
//			params.requestData.put(ParamsStatic.ID,19696231);

			params.jobType=JobStatic.POI_CRAWLER;
			params.requestData=JsonUtil.parse("{\"baidu\":{\"lng\":116.508803,\"lat\":39.809128},\"id\":360,\"name\":\"北京华联力宝购物中心\",\"city\":\"北京\",\"city_code\":86999030}");
//			params.put(ParamsStatic.POI_TJ,"{\"baidu\":{\"lng\":116.508803,\"lat\":39.809128},\"id\":360,\"name\":\"北京华联力宝购物中心\",\"city\":\"北京\"}");

//			params.put(ParamsStatic.CITY,"北京");
			
			kafkaTransmitBean.params=params;
			StatusBean statusBean=new StatusBean();
			//statusBean.execStatus=StatusStatic.COMMIT_SUCCESS;
			
			kafkaTransmitBean.status=statusBean;
			
//			kafkaTransmitBean.jobControlId=2;
//			kafkaTransmitBean.jobControlParentId=1;
//			statusBean.execStatus=StatusStatic.SUCCESS;
//			kafkaTransmitBean.jobControlId=2;
//			kafkaTransmitBean.jobControlParentId=1;
//			kafkaTransmitBean.reback=true;
			//consumer.sentMsgs(kafkaTransmitBean);
			System.out.println(JsonUtil.getJsonStr(kafkaTransmitBean));
			//consumer.sentMsgs(kafkaTransmitBean);
//			String str=JsonUtil.getJsonStr(kafkaTransmitBean);
//			KafkaTransmitBean kafkaBean=(KafkaTransmitBean)JsonUtil.getDtoFromJsonObjStr(str,KafkaTransmitBean.class,JsonMappingStatic.kafkaTransmitBeanMap);
//			System.out.println(kafkaBean.reback);
		}

	}

}

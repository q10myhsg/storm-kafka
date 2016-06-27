package com.fangcheng.plugin.newBusiness;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.util.JsonUtil;

public class test2 {

	
	public static void main(String[] args) throws UnsupportedEncodingException {
		KafkaTransmitBean kafkaTransmitBean=new KafkaTransmitBean();
		kafkaTransmitBean.topic=TopicStatic.ADD_NEW_BUSINESS_FANG;
	//	kafkaTransmitBean.topic=TopicStatic.ADD_NEW_BUSSINESS_FANG;
		
		
		kafkaTransmitBean.partition="1";
		kafkaTransmitBean.comment="测试使用";
		kafkaTransmitBean.jobId=1000;
		ParamsBean params=new ParamsBean();
		kafkaTransmitBean.params=params;
		params.requestData=JsonUtil.init();
		params.requestData.put("city","北京");
		params.requestData.put("category","101");
		params.requestData.put("channel","dianping'sdfl'sdf");
		params.requestData.put(ParamsStatic.URL,"http://tongfangjiaoyu.fang.com/");
		
		
		StatusBean statusBean=new StatusBean();
		//statusBean.execStatus=StatusStatic.COMMIT_SUCCESS;
		
		kafkaTransmitBean.status=statusBean;
		
		String json=JsonUtil.getJsonStr(kafkaTransmitBean).replaceAll("'","\\\\'");
		String str="insert into KafkaInfoQueue(inputData) values('"+json+"')";
		System.out.println(str);
	}
}

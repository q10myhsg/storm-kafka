package com.fangcheng.plugin.newBusiness;

import java.util.HashMap;

import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.util.JsonUtil;

public class NewBusinessInter {

	
	/**
	 * kafka生产者
	 */
	public KafkaUtil produce=null;
	/**
	 * 共同的队列
	 */
	public String topic=TopicStatic.ADD_NEW_BUSINESS;
	public NewBusinessInter()
	{
		produce=new KafkaUtil();
	}
	/**
	 * 添加搜房数据
	 * @param url 对应的搜房url
	 * @return
	 */
	public boolean addOfficeBuilding(String url)
	{
		return sendMsg(JobStatic.FANG,ParamsStatic.URL,url);
	}
	
	/**
	 * 统一的发送请求接口
	 * 如果是 python php输入的参数一定要有 发送的 kafkatopic 为topic
	 * @param jobType
	 * @param strings
	 * @return
	 */
	public boolean sendMsg(String jobType,String...strings)
	{
		KafkaTransmitBean kafkaTransmitBean=new KafkaTransmitBean();
		kafkaTransmitBean.topic=topic;
		kafkaTransmitBean.partition="1";
//		kafkaTransmitBean.comment="测试使用";
		//kafkaTransmitBean.jobId=1000L;
		ParamsBean params=new ParamsBean();
		params.jobType=jobType;
		params.requestData=JsonUtil.init();
		if(strings.length%2!=0)
		{
			System.out.println("请输入偶数个参数");
			return false;
		}
		for(int i=0;i<strings.length;i+=2)
		{
			params.requestData.put(strings[i],strings[i+1]);
		}
		kafkaTransmitBean.params=params;
//		StatusBean statusBean=new StatusBean();
//		kafkaTransmitBean.status=statusBean;
		return produce.sentMsgs(kafkaTransmitBean);
	}
}

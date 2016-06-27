package com.fangcheng.kafka;

import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.plugin.base.TaskCommonPlugin;

public abstract class WebServerCommon extends TaskCommonPlugin{

	public WebServerCommon(Class jobName, Class blotName,
			String topicName,String parentTopicName) {
		super(jobName, blotName,topicName, parentTopicName,false);
		// TODO Auto-generated constructor stub
	}	

}

package com.fangcheng.kafka;

import com.db.MysqlConnection;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.util.JsonUtil;

public class WebServerDemo extends WebServerUtil{

	
	
	public WebServerDemo(Class jobName, Class boltName, String groupId,
			String topic, String parentTopicName) {
		super(jobName, boltName, groupId, topic, parentTopicName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initDB() throws Exception {
		// TODO Auto-generated method stub
		this.mysql=new MysqlConnection(config.get("innerMysqlIp"),
				MysqlStatic.mysqlPort, MysqlStatic.database,
				MysqlStatic.user, MysqlStatic.pwd);
	}

	@Override
	public boolean runJob(KafkaTransmitBean kafkaBean) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("test:"+JsonUtil.getJsonStr(kafkaBean));
		return true;
	}

	@Override
	public String getTag(KafkaTransmitBean kafkaBean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		WebServerDemo demo=new WebServerDemo(WebServerDemo.class,WebServerDemo.class, "analyze_test",TopicStatic.ADD_NEW_BUSINESS_ANALYZE,TopicStatic.ADD_NEW_BUSINESS);
		Thread thread =new Thread(demo);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}

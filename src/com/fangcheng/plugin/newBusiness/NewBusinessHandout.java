package com.fangcheng.plugin.newBusiness;

import java.util.ArrayList;
import java.util.HashMap;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.plugin.base.TaskCommonHandoutPlugin;
import com.fangcheng.plugin.base.TaskRelationBean;
import com.fangcheng.stormKafka.KafkaSpoutUtil;

/**
 * 新增商业体分发器
 * 
 * 
 * @author Administrator
 *
 */
public class NewBusinessHandout {

	/**
	 * 分发
	 * 
	 * @author Administrator
	 *
	 */
	public static class Handout extends TaskCommonHandoutPlugin {

		public Handout(Class jobClass, Class jobBoltClass,String kafkaThis, String kafkaSend,
				String controlString, HashMap<String, String> sonMap,ArrayList<TaskRelationBean>... taskRelationBean) {
			super(jobClass, jobBoltClass,kafkaThis, kafkaSend, controlString, sonMap,taskRelationBean);
			// TODO Auto-generated constructor stub
		}
	}

	public static void main(String[] args) throws Exception {
		// 发送消息到点评
		// TaskCommonControl.execInfoTaskSon(mysql,consumer,controlString+"_点评",kafkaBean,TopicStatic.ADD_NEW_BUSSINESS_DP);

		// TaskCommonControl.execInfoTaskSon(mysql,consumer,controlString+"_百度指数",kafkaBean,TopicStatic.ADD_NEW_BUSSINESS_INDEX_BAIDU);
		//
		// TaskCommonControl.execInfoTaskSon(mysql,consumer,controlString+"_室内地图",kafkaBean,TopicStatic.ADD_NEW_BUSSINESS_INDOOR_MAP);
		//
//		TaskCommonControl.execInfoTaskSon(mysql, consumer, controlString
//				+ "_搜房", kafkaBean, TopicStatic.ADD_NEW_BUSSINESS_FANG);
		//
		// TaskCommonControl.execInfoTaskSon(mysql,consumer,controlString+"_携程",kafkaBean,TopicStatic.ADD_NEW_BUSSINESS_CTRIP);
		//
		// TaskCommonControl.execInfoTaskSon(mysql,consumer,controlString+"_安居客",kafkaBean,TopicStatic.ADD_NEW_BUSSINESS_ANJUKE);
		
		String name = NewBusinessHandout.class.getSimpleName();
		String topic = TopicStatic.ADD_NEW_BUSINESS;
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka_"+topic, new KafkaSpoutUtil(name, topic, null), 1);
		HashMap<String,String> map=new HashMap<String,String>();
		//新增子任务添加任务到对应的map中
		map.put(JobStatic.DP,  TopicStatic.ADD_NEW_BUSINESS_DP);
		map.put(JobStatic.FANG,  TopicStatic.ADD_NEW_BUSINESS_FANG);
		map.put(JobStatic.CTRIP,  TopicStatic.ADD_NEW_BUSINESS_CTRIP);
		map.put(JobStatic.POI_CRAWLER,  TopicStatic.ADD_NEW_BUSINESS_POI_TJ);
		map.put(JobStatic.ANJUKE,  TopicStatic.ADD_NEW_BUSINESS_ANJUKE);
		map.put(JobStatic.HOMELINK,  TopicStatic.ADD_NEW_BUSINESS_HOMELINK);
//		ArrayList<TaskRelationBean> listAlz=new ArrayList<TaskRelationBean>();
//		//添加对应的二级 关联集合
//		TaskRelationBean alz=new TaskRelationBean();
//		alz.containJob=new HashSet<String>();
//		//添加关联上级类
//		alz.containJob.add(JobStatic.DP);
//		alz.jobName=JobStatic.ANALYZE;
//		alz.jobTopic=TopicStatic.ADD_NEW_BUSINESS_ANALYZE;
//		alz.rebackTopic=TopicStatic.ADD_NEW_BUSINESS;
//		//添加条件参数
//		alz.conditionParams=new ArrayList<HashMap<String,Object>>();
//		HashMap<String,Object> params=new HashMap<String,Object>();
//		params.put(ParamsStatic.URL, true);
//		params.put(ParamsStatic.CITY, false);
//		alz.conditionParams.add(params);
//		listAlz.add(alz);
//		builder.setBolt("分发器", new Handout(NewBusinessHandout.class,NewBusinessHandout.Handout.class,
//				TopicStatic.ADD_NEW_BUSINESS_STATUS,"新增商业体",map,listAlz), 1).globalGrouping(
//						"kafka_"+topic);
		builder.setBolt("分发器", new Handout(NewBusinessHandout.class,NewBusinessHandout.Handout.class,TopicStatic.ADD_NEW_BUSINESS,
				TopicStatic.ADD_NEW_BUSINESS_STATUS,"新增商业体",map), 1).globalGrouping(
						"kafka_"+topic);
		Config conf = new Config();
		// conf.setDebug(true);
		//args = null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST, "fcnode-01");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf, builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());

			Thread.sleep(1000000);

			cluster.shutdown();
		}
	}
}

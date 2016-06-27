package com.fangcheng.plugin.newBusiness.newBusinessFang;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

import com.db.MongoDb;
import com.db.Redis;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MongoStatic;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.parse.interator.ClassLoaderJar;
import com.fangcheng.plugin.base.TagStatic;
import com.fangcheng.plugin.base.TaskCommonPlugin;
import com.fangcheng.plugin.newBusiness.newBusinessFang.nutch.FangDescParseMethod;
import com.fangcheng.stormKafka.KafkaSpoutUtil;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

/**
 * 调用搜房页面的parse方法
 * 
 * @author Administrator
 *
 */
public class NewBusinessFang {
	/**
	 * 分发
	 * 
	 * @author Administrator
	 *
	 */
	public static class NewUrlParse extends TaskCommonPlugin {

		/**
		 * 
		 * @param jobName
		 *            job对应的名字
		 * @param parentTopicName
		 *            父亲的名字
		 * @param controlString
		 *            以及该类执行的的基本描述信息
		 */
		public NewUrlParse(Class jobName,String topicName, String parentTopicName,
				String controlString, boolean useJar) {
			super(jobName, NewUrlParse.class,topicName, parentTopicName, useJar);
			this.controlString = controlString;
		}
		

		@Override
		public void initDB() throws Exception {
			// TODO Auto-generated method stub
			MainProxy.setRedis(new Redis(config.get("proxyRedisIp"),Integer.parseInt(config.get("proxyRedisPort"))));
		}

		/**
		 * 具体的调用程序
		 * 
		 * @param kafkaBean
		 * @return
		 * @throws InterruptedException
		 */
		public boolean runJob(KafkaTransmitBean kafkaBean)
				throws InterruptedException {
			//System.out.println(JsonUtil.getJsonStr(kafkaBean));
			// 访问时间做多3秒一次
			System.out.println("进入。。。。。。。。。。。。。。。。。。。。。。。。。。");
			if(true){
				System.out.println("返回");
				return true;
			}
//			
//			if(true)
//			return true;
			Thread.sleep(Integer.parseInt(config.get("fangSleepTime")));
			if (inputStringTag == null) {
				// 没有新增内容
				return true;
			} else if (inputStringTag.equals(TagStatic.ADD_NEW_CITY)) {
				// 否则为爬取一个城市
				String cityString = kafkaBean.getParams().requestData
						.get(ParamsStatic.CITY).asText();
				if (cityString == null) {
					return true;
				}
				String[] cityS = cityString.split(",");
				// System.out.println("调用城市为:"+Arrays.toString(cityS));
			} else {
				// 否则为解析一个页面
				String parseContext = parseMethod(kafkaBean.params.requestData
						.get(ParamsStatic.URL).asText());
				//parseContext="{\"test\":\"sadf\"}";
				if (parseContext == null) {
					//System.out.println("提交信息异常");
					kafkaBean.execComment="页面下载程序异常";
					return false;
				} else {
					// 入库程序分两种类型 一种是直接入库 一种是增量入库
					// 插入数据
					// System.out.println("提交信息："+parseData);
//					MongoDb mongo = MongoStatic.getMongoDb("fang11");
					MongoDb mongo=new MongoDb(config.get("testMongoIp"),27017,"demo");
					boolean flag=intoDataBase(parseContext, mongo);
					mongo.close();
					if(!flag)
					{
						kafkaBean.execComment="入库程序异常";
					}
					return flag;
				}

			}
//			System.out.println("完成");
			return false;
		}

		/**
		 * 解析某一个页面的parse方法
		 * 
		 * @return
		 */
		public String parseMethod(String url) {
			try {

				// String url2="http://tongfangjiaoyu.fang.com/";
				String urlCode = AntGetUrlProxy.doGet(url, "gbk", true,config);
//				Class parseMethod = ClassLoaderJar
//						.forName("com.fangcheng.www.fang.parseMethod.FangDescParseMethod");
//				Method m1 = parseMethod.getDeclaredMethod("runUrl",
//						String.class, String.class);
//				// System.out.println(
//				// (String)m1.invoke(parseMethod,url,urlCode));
//				return (String) m1.invoke(parseMethod, url, urlCode);
				if(urlCode==null)
				{
					error(NewUrlParse.class, inputStringTag,"下载页面为空:"+url);
				}
				return FangDescParseMethod.runUrl(url, urlCode);
			} catch (Exception e) {
				e.printStackTrace();
				error(NewUrlParse.class, inputStringTag, e.toString());
				return null;
			}
		}

		public String fangStatic = "fang";
		public String fangUpdate = "fangUpdate";

		/**
		 * 入库程序 并更新数据库
		 * @param str
		 * @param mongo
		 * @return
		 */
		public boolean intoDataBase(String str, MongoDb mongo) {
			ObjectNode json = JsonUtil.parse(str);
			long fangCode = json.get("fangCode").asLong();
			HashSet<Long> fangUniCode = new HashSet<Long>();
			HashSet<String> fangHire = new HashSet<String>();
			HashSet<String> fangPrice = new HashSet<String>();
			BasicDBObject sec = new BasicDBObject();
			sec.append("fangCode", fangCode);
			BasicDBObject doc = new BasicDBObject();
			doc.append("hireTrendValue", 1);
			doc.append("priceTrendValue", 1);
			// 获取对应的数据
			DBCursor cursor = mongo.find(fangStatic, sec, doc);
			while (cursor.hasNext()) {
				fangUniCode.add(fangCode);
				BasicDBObject obj = (BasicDBObject) cursor.next();
				if (obj == null) {
					continue;
				}
				BasicDBList hireBean = (BasicDBList) obj.get("hireTrendValue");
				BasicDBList priceBean = (BasicDBList) obj
						.get("priceTrendValue");
				for (int i = 0; i < hireBean.size(); i++) {
					BasicDBObject hire = (BasicDBObject) hireBean.get(i);
					if (hire.getString("month").contains("-")) {
						// 添加时间
						fangHire.add(fangCode + hire.getString("month"));
					} else {
						if (hire.getString("month").length() == 1) {
							fangHire.add(fangCode + "2014-0"
									+ hire.getString("month"));
						} else {
							fangHire.add(fangCode + "2014-"
									+ hire.getString("month"));
						}
					}
				}
				for (int i = 0; i < priceBean.size(); i++) {
					BasicDBObject price = (BasicDBObject) priceBean.get(i);
					if (price.getString("month").contains("-")) {
						// 添加时间
						fangPrice.add(fangCode + price.getString("month"));
					} else {
						if (price.getString("month").length() == 1) {
							fangPrice.add(fangCode + "2014-0"
									+ price.getString("month"));
						} else {
							fangPrice.add(fangCode + "2014-"
									+ price.getString("month"));
						}
					}
				}
			}
			// 获取对应的时间节点
			// 获取update数据集
			doc = new BasicDBObject();
			doc.append("month", 1);
			doc.append("year", 1);
			cursor = mongo.find(fangUpdate, sec, doc);
			while (cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				if (obj == null) {
					continue;
				}
				// 添加增量的数据集
				// Long fangcode = Long.parseLong(obj.getString("fangCode"));
				int month = obj.getInt("month");
				int year = obj.getInt("year");
				String temp = fangCode + "" + year + "-" + month;
				// System.out.println(temp);
				fangHire.add(temp);
				fangPrice.add(temp);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (json.get("month").asInt() == 0) {// 如果为空则为当前月的时间
				json.put("month", Calendar.getInstance().get(Calendar.MONTH));
			}
			if (json.get("year").asInt() == 0) {
				json.put("year", Calendar.getInstance().get(Calendar.YEAR));
			}
			// 判断是否存在
			if (!fangUniCode.contains(fangCode)) {
				// 如果不包含则为新的房源
				mongo.insert(fangStatic, json.toString());
				// log.info("新增:" + bean.getFangCode());
				return true;
			}
			ArrayNode hireList = (ArrayNode)json.get("hireTrendValueEtl");
			ArrayNode priceList = (ArrayNode)json.get("priceTrendValue");
			//String date = "";
			HashSet<String> dateList = new HashSet<String>();
			for (int i = 0; i < hireList.size(); i++) {
				String month = hireList.get(i)
						.get("month").asText();
				String date1 = fangCode + month;
				if (fangHire.contains(date1)) {
					continue;
				}
				dateList.add(month);
				fangHire.add(date1);
			}
			for (int i = 0; i < priceList.size(); i++) {
				String month =priceList.get(i)
						.get("month").asText();
				String date1 = fangCode + month;
				if (fangPrice.contains(date1)) {
					continue;
				}
				dateList.add(month);
				fangPrice.add(date1);
			}
			// 获取有效的时间
			for (String monthStr : dateList) {
				String[] strList = monthStr.split("-");
				json.put("year", Integer.parseInt(strList[0]));
				json.put("month", Integer.parseInt(strList[1]));
				for (int i = 0; i < hireList.size(); i++) {
					ObjectNode obj = (ObjectNode) hireList.get(i);
					String month = obj.get("month").asText();
					if (monthStr.equals(month)) {
						// 为新的数据源
						json.put("hireValue", obj.get("money").asText());
						break;
					}
				}
				for (int i = 0; i < priceList.size(); i++) {
					ObjectNode obj = ((ObjectNode) priceList.get(i));
					String month = obj.get("month").asText();
					if (monthStr.equals(month)) {
						// 为新的数据源
						json.put("priceValue", obj.get("money").asText());
						break;
					}
				}
				mongo.insert(fangUpdate, json.toString());
			}
			return true;
		}

		@Override
		public String getTag(KafkaTransmitBean kafkaBean) {
			// TODO Auto-generated method stub
			String url = kafkaBean.params.requestData.get(ParamsStatic.URL).asText();
			if (url == null) {
				// 否则为爬取一个城市
				String cityString = kafkaBean.getParams().requestData
						.get(ParamsStatic.CITY).asText();
				if (cityString == null) {
					return null;
				}
				return TagStatic.ADD_NEW_CITY;
			}
			return TagStatic.ADD_NEW_URL;
		}

	}

	public static void main(String[] args) throws Exception {
//		String urlCode = AntGetUrlProxy.doGet("http://shenganghaoyuan.fang.com/office/", "gbk", true,null);
//		System.out.println(FangDescParseMethod.runUrl("http://shenganghaoyuan.fang.com/office/",urlCode));
//		System.exit(1);
		String name = NewBusinessFang.class.getSimpleName();
		String topic = TopicStatic.ADD_NEW_BUSINESS_FANG;
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka_" + topic,
				new KafkaSpoutUtil(name, topic, null), 1);
		builder.setBolt(
				"搜房执行程序",
				new NewUrlParse(NewBusinessFang.class,topic,
						TopicStatic.ADD_NEW_BUSINESS, "新增商业体-搜房", false), 1)
				.globalGrouping("kafka_" + topic);
		Config conf = new Config();
		// conf.setDebug(true);
		// args = null;
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

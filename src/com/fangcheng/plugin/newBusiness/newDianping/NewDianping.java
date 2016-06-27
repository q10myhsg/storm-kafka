package com.fangcheng.plugin.newBusiness.newDianping;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

import com.db.MongoDb;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.plugin.base.TaskCommonPlugin;
import com.fangcheng.stormKafka.KafkaSpoutUtil;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class NewDianping {
	public static class NewUrlParse extends TaskCommonPlugin {
		public NewUrlParse(Class jobName,String topicName, String parentTopicName,
				String controlString, boolean useJar) {
			super(jobName, NewUrlParse.class,topicName, parentTopicName, useJar);
			this.controlString = controlString;
		}
		public boolean runJob(KafkaTransmitBean kafkaBean) throws InterruptedException
		{
			System.out.println("another update request"+new Date());
			
			String id = (String)kafkaBean.params.requestData.get(ParamsStatic.ID).asText();
			boolean page_flag = process_page(id);
			boolean api_flag = process_api(id);
			return page_flag && api_flag;
		}
		public boolean process_api(String id){
			String api_url = "http://www.dianping.com/ajax/json/shop/wizard/BasicHideInfoAjaxFP?shopId="+id;
			try {
				String api_data = AntGetUrl.doGet(api_url, "utf-8");
				ObjectNode jo =JsonUtil.parse(api_data);
				int code = jo.get("code").asInt();
				if (code != 200){
					return false;
				}else{
					String data = jo.get("shopInfo").asText();
					BasicDBObject bdo = (BasicDBObject)JSON.parse(data);
					int cityId = bdo.getInt("cityId");
					String table_name = "city_"+cityId;
					MongoDb mongo = new MongoDb("192.168.1.11", 27017, "dianping");
		            DBCollection coll = mongo.getCollection(table_name);
		            coll.insert(bdo);
		            mongo.close();
		            return true;	
				}
			} catch (Exception e) {
				return false;
			}
		}
		//fetch -> parse -> put into database
		public boolean process_page(String id)
		{	
			String page_url = "http://www.dianping.com/shop/"+id;
			System.out.println("requesting:"+page_url);
			String html = null;
			try{
				html= AntGetUrl.doGetGzip(page_url, "utf-8",false);
			}catch(Exception e){
				return false;
			}
			if(html == null){
				return false;
			}
			
			//parse
			String type = HtmlParser.getType(html);
			System.out.println("type:"+type);
//			return true;
			String result = null;
			if (type == "yellow") {
				result = HtmlParser.parseYellow(html);
				System.out.println(result);
				return put_db(result,id);
			} else if (type == "mall"){
				result = HtmlParser.parseMall(html);
				System.out.println(result);
				return put_db(result,id);
			}else if (type == "pink") {
				result = HtmlParser.parsePink(html);
				System.out.println(result);
				return put_db(result,id);
			}else{
				return false;
			}
			
		}
		//string to DBObject
		private DBObject convert(String s){
			System.out.println(s);
			DBObject object = new BasicDBObject();
			String[] values = s.split("\\|");
			try{
				com.fangcheng.plugin.newBusiness.newDianping.Cat_Filter cat_filter = new com.fangcheng.plugin.newBusiness.newDianping.Cat_Filter(config);
				//parse cat_list
				DBObject cat_object = new BasicDBObject();
				String cat_string = values[1];
				String[] cats = cat_string.split(",");
				for (String cat : cats) {
					String type = cat_filter.filter(cat);
					cat_object.put(type, cat);
				}
				object.put("cat_list", cat_object);
				
				//parse closed
				if(values[2].equals("FALSE")){
					object.put("closed", false);
				}else{
					System.out.println("TRUE");
					object.put("closed", true);
				}
				
				//parse all_comments
				try {
					Integer all_comments = Integer.parseInt(values[3].trim());
					object.put("all_comments", all_comments);
				} catch (Exception e) {
					object.put("all_comments", "");
				}
				
				//parse views
				if(values[4] == ""){
					object.put("views", new String[0]);
				}else {
					object.put("views", values[4].split(","));
				}
				
				//parse stars
				if(values[5].equals("-")){
					object.put("stars", new BasicDBList());
				}else {
					object.put("stars", values[5].split(","));
				}
				
				if(values[6] == "-"){
					object.put("shops_inside", "");
				}
				else {
					object.put("shops_inside", parse_shops_inside(values[6]));
				}
				
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			return object;
		}
		public BasicDBList parse_shops_inside(String s){
			BasicDBList list = new BasicDBList();
			Pattern p = Pattern.compile("\\d{5}\\d*");
			Matcher matcher = p.matcher(s);
			while(matcher.find()) {
				list.add(matcher.group());
			}
			return list;
		}
		public boolean put_db(String result,String id){
			DBObject object = convert(result);
			object.put("_id", id);
			System.out.println(object);
//			MongoDb mongo = new MongoDb(config.get("aliMongoIp"), 27017, "dianping");
			MongoDb mongo = new MongoDb("192.168.1.11", 27017, "dianping");
            DBCollection coll = mongo.getCollection("test");
            coll.insert(object);
            mongo.close();
            return true;
		}

		@Override
		public String getTag(KafkaTransmitBean kafkaBean) {
//			String url=kafkaBean.params.requestData.get(ParamsStatic.URL).asText();
//			if(url==null)
//			{
//				//否则为爬取一个城市
//				String cityString=kafkaBean.getParams().requestData.get(ParamsStatic.CITY).asText();
//				if(cityString==null)
//				{
//					return null;
//				}
//				return TagStatic.ADD_NEW_CITY;
//			}
//			return TagStatic.ADD_NEW_URL;
			return null;
		}
		@Override
		public void initDB() throws Exception {
			// TODO Auto-generated method stub
			
		}
	}

	public static void main(String[] args) throws Exception {
//		NewUrlParse parse = new NewUrlParse(NewDianping.class,TopicStatic.ADD_NEW_BUSINESS_DP,TopicStatic.ADD_NEW_BUSINESS,"新增商业体-点评",false);
//		String s = "yellow|北京餐厅,朝阳区,三里屯,火锅,更多火锅,|FALSE|761|4665606,4683333,6008544,4697793,5255451,4217553,5323818,6209778,|4,15,46,117,133|asdfas234234asdfas564466asdf765446|";
//		System.out.println(parse.convert(s));
//		String html = AntGetUrl.doGetGzip("http://www.dianping.com/shop/2731984", "utf-8",false);
		
		
		
//		System.out.println(Runtime.getRuntime().exec("python C:\\Users\\liangbo\\Desktop\\test.py"));
//		ArrayList<String> list = ShellExec.execShellAndGet("python C:\\Users\\liangbo\\Desktop\\test.py");
//		for (String string : list) {
//			System.out.println(string);
//		}
//		
		
		String name = NewDianping.class.getSimpleName();
		String topic = TopicStatic.ADD_NEW_BUSINESS_DP;
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new KafkaSpoutUtil(name, topic, null), 1);
		builder.setBolt("info submit", new NewUrlParse(NewDianping.class,topic,TopicStatic.ADD_NEW_BUSINESS,"新增商业体-点评",false), 1).globalGrouping(
				"spout");
		Config conf = new Config();
		// conf.setDebug(true);
		//args = null -> local
		//args = null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST, "fcnode-01");
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(name, conf, builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());

			Thread.sleep(5000000);

			cluster.shutdown();
		}
	}
}

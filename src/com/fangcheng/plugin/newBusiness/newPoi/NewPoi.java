package com.fangcheng.plugin.newBusiness.newPoi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

import com.db.MongoDb;
import com.fangcheng.json.JSONException;
import com.fangcheng.json.JSONObject;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.plugin.base.TaskCommonPlugin;
import com.fangcheng.plugin.newBusiness.newPoi.Bean.SourcePOI;
import com.fangcheng.plugin.newBusiness.newPoi.crawler.Nearby;
import com.fangcheng.stormKafka.KafkaSpoutUtil;
import com.fangcheng.util.JsonUtil;
import com.google.gson.Gson;

/**
 * 调用POI统计方法
 * @author Administrator
 *
 */
public class NewPoi extends TaskCommonPlugin{
	/**
	 * 
	 * @param jobName
	 *            job对应的名字
	 * @param parentTopicName
	 *            父亲的名字
	 * @param controlString
	 *            以及该类执行的的基本描述信息
	 */
	public NewPoi(Class jobName,String topicName, String parentTopicName,
			String controlString, boolean useJar) {
		super(jobName, NewPoi.class,topicName, parentTopicName, useJar);
		this.controlString = controlString;
	}
	
	@Override
	public void initDB() throws Exception {
		// TODO Auto-generated method stub
		gson = new Gson();
		//getMap();
	}

//	public static Map<String,String> map = null;
	public static Gson gson = null;

	/**
	 * 具体的调用程序
	 * @param kafkaBean
	 * @return
	 * @throws InterruptedException 
	 */
	public boolean runJob(KafkaTransmitBean kafkaBean) throws InterruptedException,JSONException{
		String ret = null;
		//System.out.println(JsonUtil.getJsonStr(kafkaBean));
		String poi_tj = JsonUtil.getJsonStr(kafkaBean.getParams().requestData);
		
		List<SourcePOI> list_mall = new ArrayList<SourcePOI>();
//		JSONObject temp = new JSONObject(JSON.serialize(poi_tj));
		if(null == poi_tj){
			kafkaBean.execComment="入参不正确";
			return false;
		}
		list_mall.add(gson.fromJson(poi_tj, SourcePOI.class));
		ret = getPoixgp(list_mall);
		if(ret != null){
			kafkaBean.execComment=ret;
			return false;
		}
		ret = getPoiwenyang(list_mall);
		if(ret != null){
			kafkaBean.execComment=ret;
			return false;
		}
		return true;
	}
	@Override
	public String getTag(KafkaTransmitBean kafkaBean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 
	 * lng":116.43137140132,"lat":39.865405882696
	 * 
	 * */

	public String getPoiwenyang(List<SourcePOI> list_mall) {
		String ret = null;
		Iterator<SourcePOI> it = list_mall.iterator();
		String[] keywords = config.get("allkeys").split(",");
		JSONObject jo = null;
		/**
		 * poi数据最终表
		 */
		MongoDb mongo=new MongoDb(config.get("innerMongoIp"),27017,"demo");
		
		while (it.hasNext()) {
			try {
				jo = new JSONObject(gson.toJson(it.next()));
				//poi_info = mongo.getCollection("POI_"+jo.getInt("city_code"));
				//query.put("sourcePOI.id", jo.getInt("id"));
				for(int j=0;j<keywords.length;j++){
					String category = keywords[j];
					//query.put("keyword", category);
					//poi_info.remove(query);
					Nearby nb = new Nearby();
					Search s = new Search();
					nb.setLat(jo.getJSONObject("baidu").getFloat("lat"));
					nb.setLng(jo.getJSONObject("baidu").getFloat("lng"));
					nb.setRadius(5000);
					s.setSourcePOI(jo);
					s.setKeyword(category);
					s.setBg(nb);
					List<String> ja = s.excute();
					for (int i = 0; i < ja.size(); i++) {
						if(!ja.get(i).contains("sourcePOI")){
							mongo.close();
							return ja.get(i);
						}
						mongo.insert("POI_"+jo.getInt("city_code"),ja.get(i));
					}
				}
			} catch (Exception e) {
				String strEx=String.format("Exception name=%s \n",e.toString());
			   	StackTraceElement [] messages=e.getStackTrace();
			   	int length=messages.length;
			   	for(int i=0;i<length;i++){
			   		strEx=strEx+messages[i].toString()+"\n";
			   	}
			   	ret = "异常信息："+strEx;
			}
		}
		mongo.close();
		return ret;
	}
	/**
	 * 获取5A级景区、酒店、写字楼、小区、购物中心的poi数据
	 */
	public String getPoixgp(List<SourcePOI> list_mall){
		String ret = null;
		Poi_Info_Action poi = new Poi_Info_Action(config.get("innerMongoIp"),"demo",27017);
		poi.mydb = new MysqlDb(config.get("mysql134"),
		Integer.parseInt(config.get("mysql134Port")==null?"3306":config.get("mysql134Port")),
				config.get("mysql134Database")==null?MysqlStatic.database134:config.get("mysql134Database"),
						config.get("mysql134User")==null?MysqlStatic.user134:config.get("mysql134User"),
				config.get("mysql134Pwd")==null?MysqlStatic.pwd134:config.get("mysql134Pwd"));
		//poi.mydb.openssh();
		poi.mydb.getConnection();
		SourcePOI spoi = null;
		for(int i=0;i<list_mall.size();i++){
			spoi = list_mall.get(i);
			//String[] str = map.get(spoi.getCity()).split(",");
			//poi数据集合
			String all_mall = "mall_for_poi_back";
			//先删除添加购物中心的历史数据
			System.out.println(JsonUtil.getJsonStr(spoi));
			poi.removedatamallself("POI_"+spoi.getCity_code(), spoi);
			//处理5A级景区数据，插入around_poi  5A级景区
			ret = poi.fiveAscenic(spoi,"POI_"+spoi.getCity_code(),"fiveAscenic",spoi.getCity(),5000);
			if(ret != null)
				return ret;
			//处理携程数据，插入around_poi  酒店
			ret = poi.ctrip(spoi,"POI_"+spoi.getCity_code(),"ctrip_new",spoi.getCity(),5000);
			if(ret != null)
				return ret;
			//处理写字楼数据，插入around_poi  写字楼
			ret = poi.office(spoi,"POI_"+spoi.getCity_code(),"fang",spoi.getCity(),5000);
			if(ret != null)
				return ret;
			//处理小区数据，插入around_poi  小区
			ret = poi.house(spoi,"POI_"+spoi.getCity_code(),spoi.getCity_code()+"",spoi.getCity(),5000);
			if(ret != null)
				return ret;
			//将mall之间相距小于5000米的数据插入around_poi  购物中心
			ret = poi.mallself(spoi,all_mall,"POI_"+spoi.getCity_code(),"",spoi.getCity(),5000);
		}
		//关闭数据连接
		poi.mydb.close();
		poi.closeMo();
		poi.mydb.closessh();
		return ret;
	}
	
	public static void getMap(){
//		map = new HashMap<String,String>();
//		map.put("上海", "上海,shanghaiPOI,86999031");
//		map.put("北京", "北京,beijingPOI,86999030");
//		map.put("广州", "广州,guangzhouPOI,86016140");
//		map.put("南京", "南京,nanjingPOI,86007050");
//		map.put("深圳", "深圳,shenzhenPOI,86016125");
	}
	
	public static void main(String[] args){
//		RingConfig.default_config="./config/newpoi.properties";
//		RingConfig.startConfig();
		String name = NewPoi.class.getSimpleName();
		String topic = TopicStatic.ADD_NEW_BUSINESS_POI_TJ;
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka_"+topic, new KafkaSpoutUtil(name, topic, null), 1);
		builder.setBolt("poi_bolt", new NewPoi(NewPoi.class,topic,TopicStatic.ADD_NEW_BUSINESS,"POI数据统计",false), 1).globalGrouping("kafka_"+topic);
		Config conf = new Config();
		conf.setDebug(true);
		//args = null;
		if (args != null && args.length >= 0) {
			conf.put(Config.NIMBUS_HOST, "fcnode-01");
			conf.setNumWorkers(1);
			try {
				StormSubmitter.submitTopology(name, conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, conf, builder.createTopology());
			try {
				Thread.sleep(1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cluster.shutdown();
		}
	}

}


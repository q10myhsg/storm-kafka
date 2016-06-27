package com.fangcheng.plugin.newBusiness.newPoi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.db.MongoDb;
import com.fangcheng.json.JSONObject;
import com.fangcheng.plugin.newBusiness.newPoi.Bean.Around_poi;
import com.fangcheng.plugin.newBusiness.newPoi.Bean.Location;
import com.fangcheng.plugin.newBusiness.newPoi.Bean.SourcePOI;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.TimePrint;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.TransferPOI;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class Poi_Info_Action {
	
	public Gson gson = new Gson();
	/**
	 * 用于计算两个ip之间的距离时保留两位小数
	 */
	public DecimalFormat df = new DecimalFormat("#.00");
	/**
	 * 192.168.1.4的mongo
	 */
	public MongoDb mg4 = null;
	/**
	 * 4号机上的demo库
	 */
	public DB db4demo = null;
	/**
	 * 152号机上的dianping库
	 */
//	public DB db152dianping = null;
	/**
	 * poi数据对象
	 */
	public Around_poi around_poi_been = null;
	/**
	 * poi数据最终表
	 */
	public DBCollection poi_info = null;
	/**
	 * mall数据表
	 */
	public DBCollection mall_info = null;
	/**
	 * 通过点评数据获取购物中心的地址
	 */
	public double minLat, minLng, maxLat, maxLng;
	/**
	 * mysql数据库链接
	 */
	public MysqlDb mydb = null;
//	public DBCollection city_2 = null;
	
	public Poi_Info_Action(String ip4,String db4,int port){
		mg4 = new MongoDb(ip4,port);
		db4demo = mg4.getDb(db4);
//		db152dianping = mg152.getDb("dianping");
	}
	
	/**
	 * 统计mall和mall之间的poi数据
	 * 入参：新增mall数据表、全量mall数据表、poi数据表、mall的点评表、地市名称
	 */
	public String mallself(SourcePOI addmall,String allmall,String poi,String dianping,String cityname,int distince) {
		String ret = null;
		try {
			poi_info = db4demo.getCollection(poi);
			mall_info = db4demo.getCollection(allmall);
			//暂时不获取购物中心的地址
			//city_2 = db152dianping.getCollection(dianping);
			//删除poi表中的  购物中心  数据,不管是新增mall还是第一次统计都用全量mall的数据进行计算
			//removedatamallself(poi_info,"购物中心",addmall);
			//获取全量购物中心数据
			DBCursor cur_mall = getMallByCityname(allmall,cityname);
			//将mall数据导入到list中
			List<SourcePOI> list_mall = collToList(cur_mall);
			System.out.println("处理购物中心开始："+list_mall.size());
			List<SourcePOI> list_addmall = new ArrayList<SourcePOI>();
			list_addmall.add(addmall);
			dealmallself(list_addmall,list_mall,addmall.getId(),distince);
			dealmallself(list_mall,list_addmall,addmall.getId(),distince);
			//将新增mall数据插入备份表,生成全量mall数据表
			insertColl(mall_info,addmall);
			System.out.println("处理购物中心结束");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String strEx=String.format("Exception name=%s \n",e.toString());
		   	StackTraceElement [] messages=e.getStackTrace();
		   	int length=messages.length;
		   	for(int i=0;i<length;i++){
		   		strEx=strEx+messages[i].toString()+"\n";
		   	}
			return "异常信息："+strEx;
		}
		return ret;
	}
	public void dealmallself(List<SourcePOI> list_mall1,List<SourcePOI> list_mall2,int amllid,int distince){
		SourcePOI spoi = null;
		SourcePOI spoiii = null;
		Location location = null;
		for(int a=0;a<list_mall1.size();a++) {
			spoi = list_mall1.get(a);
			//根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
			getAround(spoi.getBaidu().getLng(), spoi.getBaidu().getLat(), distince);
    		for(int i = 0;i<list_mall2.size();i++){
    			spoiii = list_mall2.get(i);
    			if(spoiii.getBaidu().getLng()<=maxLng && spoiii.getBaidu().getLng()>=minLng && spoiii.getBaidu().getLat()<=maxLat && spoiii.getBaidu().getLat()>=minLat){
    				if(!spoi.getName().equals(spoiii.getName())){
	    				double d = Double.parseDouble(df.format(distance(spoi.getBaidu().getLng(),spoi.getBaidu().getLat(),spoiii.getBaidu().getLng(),spoiii.getBaidu().getLat())));
		    			if(d<=5000){
		    				around_poi_been = new Around_poi();
		    				location = new Location();
		    				location.setLng(spoi.getBaidu().getLng());
			    			location.setLat(spoi.getBaidu().getLat());
			    		    around_poi_been.setDistance(d);
			    		    around_poi_been.setAddress("");
//			    		    around_poi_been.setAddress(getMallAddress(spoi.getName()));
			    		    around_poi_been.setKeyword("购物中心");
			    		    around_poi_been.setName(spoi.getName());
			    		    around_poi_been.setPageNumber(1);
			    		    around_poi_been.setThrid_id(spoi.getId()+"");
			    		    around_poi_been.setSourcePOI(spoiii);
			    		    around_poi_been.setLocation(location);
			    		    around_poi_been.setId(amllid);
			    		    around_poi_been.setTimestamp(new TimePrint().getTime("yyyy-MM-dd",0));
			    		    poi_info.insert((DBObject)JSON.parse(gson.toJson(around_poi_been)));
		    			}
		            }
	    		}
    		}
        }
	}
	/**
	 * 统计5A级景区的poi数据
	 * 入参：新增mall数据、poi存储表、地市名称
	 */
	public String fiveAscenic(SourcePOI addmall,String poi,String fiveAscenicTable,String cityname,int distince) {
		String ret = null;
		try {
			//根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
			getAround(addmall.getBaidu().getLng(), addmall.getBaidu().getLat(), distince);
			poi_info = db4demo.getCollection(poi);
			//避免重复统计先删除mall的poi数据后再重新统计数据
			//removedata(poi_info,"5A级景区",addmall.getId());
			DBCollection fiveAscenic = db4demo.getCollection(fiveAscenicTable);
			//获取5A级景区
			DBCursor cur_scenic = getFiveAscenic(fiveAscenic,cityname);
			cur_scenic.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			JSONObject jobj = null;
			Location location = new Location();
			System.out.println("处理5A级景区start");
			while (cur_scenic.hasNext()) {
	            jobj = new JSONObject(JSON.serialize(cur_scenic.next()));
	    		if(null != jobj.getJSONObject("baidu")){
	    			double d = Double.parseDouble(df.format(distance(jobj.getJSONObject("baidu").getDouble("lng"),jobj.getJSONObject("baidu").getDouble("lat"),addmall.getBaidu().getLng(),addmall.getBaidu().getLat())));
	    			//double d = distance(jobj.getJSONObject("baidu").getDouble("lng"),jobj.getJSONObject("baidu").getDouble("lat"),list_mall.get(i).getBaidu().getLng(),list_mall.get(i).getBaidu().getLat());
	    			if(d<=5000){
		    			around_poi_been = new Around_poi();
			    		around_poi_been.setDistance(d);
			    		around_poi_been.setAddress("");
			    		around_poi_been.setKeyword("5A级景区");
			    		around_poi_been.setName(jobj.getString("name"));
			    		around_poi_been.setPageNumber(1);
			    		around_poi_been.setThrid_id("");
			    		around_poi_been.setSourcePOI(addmall);
			    		location.setLng(jobj.getJSONObject("baidu").getDouble("lng"));
			    		location.setLat(jobj.getJSONObject("baidu").getDouble("lat"));
			    		around_poi_been.setLocation(location);
			    		around_poi_been.setId(addmall.getId());
			    		around_poi_been.setTimestamp(new TimePrint().getTime("yyyy-MM-dd",0));
			    		poi_info.insert((DBObject)JSON.parse(gson.toJson(around_poi_been).replace("\"id\":0,", "")));
	    			}
	    		}
	        }
			System.out.println("处理5A级景区end");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String strEx=String.format("Exception name=%s \n",e.toString());
		   	StackTraceElement [] messages=e.getStackTrace();
		   	int length=messages.length;
		   	for(int i=0;i<length;i++){
		   		strEx=strEx+messages[i].toString()+"\n";
		   	}
			return "异常信息："+strEx;
		}
		return ret;
	}
	/**
	 * 统计小区的poi数据
	 * 入参:新增mall数据、poi存储表、小区数据表、地市名称
	 */
//	public String house(SourcePOI addmall,String poi,String xiaoqu,String cityname,int distince) {
//		String ret = null;
//		try {
//			//根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
//			getAround(addmall.getBaidu().getLng(), addmall.getBaidu().getLat(), distince);
//			poi_info = db4demo.getCollection(poi);
//			//避免重复统计先删除mall的poi数据后再重新统计数据
//			//removedata(poi_info,"小区",addmall.getId());
//			//获取小区数据
//			ResultSet cur_house = getHouse(xiaoqu);
//			if(null == cur_house)
//				return "获取小区数据异常";
//			int bb = 0;
//			Location location = new Location();
//			System.out.println("处理小区poi开始");
//			//id,t.name,address,longitude,latitude,price
//			while (cur_house.next()) {
//	            double d = Double.parseDouble(df.format(distance(cur_house.getDouble("longitude")/1000000.0,cur_house.getDouble("latitude")/1000000.0,addmall.getBaidu().getLng(),addmall.getBaidu().getLat())));
//		    	if(d<=5000){
//		    		around_poi_been = new Around_poi();
//			    	around_poi_been.setDistance(d);
//			    	around_poi_been.setAddress(cur_house.getString("address"));
//			    	around_poi_been.setKeyword("小区");
//			    	around_poi_been.setName(cur_house.getString("name"));
//			    	around_poi_been.setPageNumber((bb++)/20+1);
//			    	around_poi_been.setThrid_id(cur_house.getString("id"));
//			    	around_poi_been.setSourcePOI(addmall);
//			    	location.setLng(cur_house.getDouble("longitude")/1000000.0);
//			    	location.setLat(cur_house.getDouble("latitude")/1000000.0);
//			    	around_poi_been.setLocation(location);
//			    	around_poi_been.setId(addmall.getId());
//			    	around_poi_been.setTimestamp(new TimePrint().getTime("yyyy-MM-dd",0));
//			    	poi_info.insert((DBObject)JSON.parse(gson.toJson(around_poi_been).replace("\"id\":0,", "")));
//	    		}
//	        }
//			System.out.println("处理小区poi结束");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			String strEx=String.format("Exception name=%s \n",e.toString());
//			StackTraceElement [] messages=e.getStackTrace();
//			int length=messages.length;
//			for(int i=0;i<length;i++){
//				strEx=strEx+messages[i].toString()+"\n";
//			}
//			return "异常信息："+strEx;
//		}
//		return ret;
//	}
	public String house(SourcePOI addmall,String poi,String xiaoqu,String cityname,int distince) {
		String ret = null;
		try {
			//根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
			getAround(addmall.getBaidu().getLng(), addmall.getBaidu().getLat(), distince);
			poi_info = db4demo.getCollection(poi);
			//避免重复统计先删除mall的poi数据后再重新统计数据
			//removedata(poi_info,"小区",addmall.getId());
			//获取小区数据
			ResultSet cur_house = getHouse(xiaoqu);
			if(null == cur_house)
				return "获取小区数据异常";
			int bb = 0;
			Location location = new Location();
			System.out.println("处理小区poi开始:"+cur_house.getRow());
			//id,t.name,address,longitude,latitude,price
			while (cur_house.next()) {
	            double d = Double.parseDouble(df.format(distance(cur_house.getDouble("building_longitude")/1000000.0,cur_house.getDouble("building_latitude")/1000000.0,addmall.getBaidu().getLng(),addmall.getBaidu().getLat())));
		    	if(d<=5000){
		    		around_poi_been = new Around_poi();
			    	around_poi_been.setDistance(d);
			    	around_poi_been.setAddress(cur_house.getString("building_address"));
			    	around_poi_been.setKeyword("小区");
			    	around_poi_been.setName(cur_house.getString("building_name"));
			    	around_poi_been.setPageNumber((bb++)/20+1);
			    	around_poi_been.setThrid_id(cur_house.getString("building_id"));
			    	around_poi_been.setSourcePOI(addmall);
			    	location.setLng(cur_house.getDouble("building_longitude")/1000000.0);
			    	location.setLat(cur_house.getDouble("building_latitude")/1000000.0);
			    	around_poi_been.setLocation(location);
			    	around_poi_been.setId(addmall.getId());
			    	around_poi_been.setTimestamp(new TimePrint().getTime("yyyy-MM-dd",0));
			    	poi_info.insert((DBObject)JSON.parse(gson.toJson(around_poi_been).replace("\"id\":0,", "")));
	    		}
	        }
			System.out.println("处理小区poi结束");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String strEx=String.format("Exception name=%s \n",e.toString());
			StackTraceElement [] messages=e.getStackTrace();
			int length=messages.length;
			for(int i=0;i<length;i++){
				strEx=strEx+messages[i].toString()+"\n";
			}
			return "异常信息："+strEx;
		}
		return ret;
	}
	
	/**
	 * 统计写字楼的poi数据
	 * 入参:新增mall数据表、poi存储表、写字楼表、地市名称
	 */
	public String office(SourcePOI addmall,String poi,String office,String cityname,int distince) {
		String ret = null;
		try {
			//根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
			getAround(addmall.getBaidu().getLng(), addmall.getBaidu().getLat(), distince);
			poi_info = db4demo.getCollection(poi);
			//避免重复统计先删除mall的poi数据后再重新统计数据
			//removedata(poi_info,"写字楼",addmall.getId());
			DBCollection office_coll = db4demo.getCollection(office);
			//获取写字楼数据
			DBCursor cur_office = getOffice(office_coll,cityname);
			cur_office.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			int bb = 0;
			JSONObject jobj = null;
			Location location = new Location();
			System.out.println("处理写字楼数据start");
			while (cur_office.hasNext()) {
	            jobj = new JSONObject(JSON.serialize(cur_office.next()));
	    		double d = Double.parseDouble(df.format(distance(jobj.getJSONObject("location").getDouble("lng"),jobj.getJSONObject("location").getDouble("lat"),addmall.getBaidu().getLng(),addmall.getBaidu().getLat())));
	    		if(d<=5000){
	    			around_poi_been = new Around_poi();
		    		around_poi_been.setDistance(d);
		    		around_poi_been.setAddress(jobj.getString("address"));
		    		around_poi_been.setKeyword("写字楼");
		    		around_poi_been.setName(jobj.getString("officeBuildingName"));
		    		around_poi_been.setPageNumber((bb++)/20+1);
		    		around_poi_been.setThrid_id(jobj.getString("fangCode"));
		    		around_poi_been.setSourcePOI(addmall);
		    		location.setLng(jobj.getJSONObject("location").getDouble("lng"));
		    		location.setLat(jobj.getJSONObject("location").getDouble("lat"));
		    		around_poi_been.setLocation(location);
		    		around_poi_been.setId(addmall.getId());
		    		around_poi_been.setTimestamp(new TimePrint().getTime("yyyy-MM-dd",0));
		    		poi_info.insert((DBObject)JSON.parse(gson.toJson(around_poi_been).replace("\"id\":0,", "")));
	    		}
	        }
			System.out.println("处理写字楼数据end");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String strEx=String.format("Exception name=%s \n",e.toString());
			StackTraceElement [] messages=e.getStackTrace();
			int length=messages.length;
			for(int i=0;i<length;i++){
				strEx=strEx+messages[i].toString()+"\n";
			}
			return "异常信息："+strEx;
		}
		return ret;
	}
	/**
	 * 统计携程poi数据
	 * 入参:新增mall数据、poi存储表、携程数据表、地市名称
	 */
	public String ctrip(SourcePOI addmall,String poi,String ctrip,String cityname,int distince) {
		String ret = null;
		JSONObject jobj = null;
		try {
			//根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
			getAround(addmall.getBaidu().getLng(), addmall.getBaidu().getLat(), distince);
			StringBuffer sbf = new StringBuffer();
			poi_info = db4demo.getCollection(poi);
			//避免重复统计先删除mall的poi数据后再重新统计数据
			//removedata(poi_info,"酒店",addmall.getId());
			DBCollection ctrip_hotel = db4demo.getCollection(ctrip);
			//先将携程中的高德经纬度转换成百度经纬度
			ret = translateCtrip(ctrip_hotel);
			if(ret != null)
				return ret;
			//获取携程数据
			DBCursor cur_ctrip = getCtrip(ctrip_hotel,cityname);
			cur_ctrip.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			int bb = 0;
			Location location = new Location();
            System.out.println("处理携程数据start");
			while (cur_ctrip.hasNext()) {
	            jobj = new JSONObject(JSON.serialize(cur_ctrip.next()));
	    		//循环判断携程宾馆和mall距离
	    		double d = Double.parseDouble(df.format(distance(jobj.getJSONObject("baidulocation").getDouble("lng"),jobj.getJSONObject("baidulocation").getDouble("lat"),addmall.getBaidu().getLng(),addmall.getBaidu().getLat())));
	    		if(d<=5000){
	    			around_poi_been = new Around_poi();
	    			//"address" : ["北京", "近索道站", "延庆县八达岭长城景区滚天沟停车场内", "延庆县"]
		    		for(int a=0;a<jobj.getJSONArray("address").length();a++){
		    		    sbf.append(jobj.getJSONArray("address").getString(a));
		    		}
		    		around_poi_been.setDistance(d);
		    		around_poi_been.setAddress(sbf.toString());
		    		around_poi_been.setKeyword("酒店");
		    		around_poi_been.setName(jobj.getString("chineseName"));
		    	    around_poi_been.setPageNumber((bb++)/20+1);
		    	    around_poi_been.setThrid_id(jobj.getString("id"));
		    	    around_poi_been.setSourcePOI(addmall);
		   		    location.setLng(jobj.getJSONObject("baidulocation").getDouble("lng"));
		   			location.setLat(jobj.getJSONObject("baidulocation").getDouble("lat"));
		   		    around_poi_been.setLocation(location);
		   		    around_poi_been.setId(addmall.getId());
		   		    around_poi_been.setTimestamp(new TimePrint().getTime("yyyy-MM-dd",0));
		   			sbf.delete(0,sbf.length());
		   			poi_info.insert((DBObject)JSON.parse(gson.toJson(around_poi_been).replace("\"id\":0,", "")));
	   			}
	        }
			System.out.println("处理携程数据end");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String strEx=String.format("Exception name=%s \n",e.toString());
			StackTraceElement [] messages=e.getStackTrace();
			int length=messages.length;
			for(int i=0;i<length;i++){
				strEx=strEx+messages[i].toString()+"\n";
			}
			return "异常信息："+strEx;
		}
		return ret;
	}
	/**
	 * 避免重复计算mall的poi数据，在计算前先删除一下数据
	 */
	public void removedata(DBCollection poi_info,String keyword,int id){
		DBObject query = new BasicDBObject();
		query.put("keyword", keyword);
		if(0!=id)
			query.put("sourcePOI.id", id);
		poi_info.remove(query);
	}
	/**
	 * 避免重复计算mall的poi数据，在计算前先删除一下数据
	 */
	public void removedatamallself(String poi,SourcePOI addmall){
		poi_info = db4demo.getCollection(poi);
		DBObject query = new BasicDBObject();
		query.put("id", addmall.getId());
		poi_info.remove(query);
	}
	/**
	 * 获取携程数据
	 * 入参为携程数据集合、地市名称
	 */
	public DBCursor getCtrip(DBCollection ctrip_coll,String cityname){
		DBObject query = getquery("baidulocation.lng","baidulocation.lat");
		//DBObject query = new BasicDBObject();
		query.put("city", cityname);
		query.put("baidulocation", new BasicDBObject(QueryOperators.EXISTS,true));
        DBObject sort = new BasicDBObject();
        sort.put("historyPrice.0.standardPrice", -1);
		return ctrip_coll.find(query).sort(sort);
	}
	/**
	 * 转换携程数据
	 */
	public String translateCtrip(DBCollection ctrip_coll){
		String ret = null;
		DBObject query = new BasicDBObject();
		query.put("baidulocation", new BasicDBObject(QueryOperators.EXISTS,false));
		DBCursor cur_ctrip = ctrip_coll.find(query);
		System.out.println("size--------------:"+cur_ctrip.size());
		//将携程中腾讯经纬度转换成百度经纬度
        TransferPOI tp = null;
        JSONObject jobj = null;
        DBObject dbo = null;
        DBObject baidulocation = null;
        try {
			while(cur_ctrip.hasNext()){
				dbo = cur_ctrip.next();
				jobj = new JSONObject(JSON.serialize(dbo));
				tp = new TransferPOI();
	            tp.addLngLat(jobj.getJSONObject("location").getString("lng")+","+jobj.getJSONObject("location").getString("lat"));
	    		List<Double> l = tp.excuteDouble();
	    		if(l.size()==2){
	    			baidulocation = new BasicDBObject();
		    		baidulocation.put("lng", l.get(0));
		    		baidulocation.put("lat", l.get(1));
		    		dbo.put("baidulocation", baidulocation);
		    		ctrip_coll.save(dbo);
	    		}
			}
        } catch (Exception e) {
        	String strEx=String.format("Exception name=%s \n",e.toString());
			StackTraceElement [] messages=e.getStackTrace();
			int length=messages.length;
			for(int i=0;i<length;i++){
				strEx=strEx+messages[i].toString()+"\n";
			}
			return "异常信息："+strEx;
		}
		return ret;
	}
	/**
	 * 获取写字楼数据
	 * 入参为写字楼数据集合、地市名称
	 */
	public DBCursor getOffice(DBCollection office_coll,String cityname){
		DBObject query = getquery("location.lng","location.lat");
		//DBObject query = new BasicDBObject();
		query.put("fangListc.city", cityname);
        DBObject sort = new BasicDBObject();
        sort.put("priceTrendValue.0.money", -1);
		return office_coll.find(query).sort(sort);
	}
	/**
	 * 获取小区数据
	 * 入参:小区的集合
	 */
	public ResultSet getHouse(String fang){
		StringBuffer sb = new StringBuffer();
		sb.append("select building_id,building_name,building_address,building_longitude,building_latitude,building_price from building ");
		sb.append(" where area_id="+fang);
		sb.append(" and building_longitude<>0 ");
		sb.append(" and building_latitude<>0 ");
		sb.append(" and poi_type_id=1 ");
		sb.append(" and building_longitude>="+(int)(minLng*1000000));
		sb.append(" and building_longitude<="+(int)(maxLng*1000000));
		sb.append(" and building_latitude>="+(int)(minLat*1000000));
		sb.append(" and building_latitude<="+(int)(maxLat*1000000));
		sb.append(" order by building_price desc");
		//System.out.println(sb.toString());
		ResultSet result = mydb.sqlSelect(sb.toString());
		sb.delete(0, sb.toString().length());
		return result;
	}
	/**
	 * 获取小区数据
	 * 入参:小区的集合
	 */
	public DBCursor getHouse(DBCollection fang){
		DBObject query = getquery("location.longitude","location.latitude");
		//DBObject query = new BasicDBObject();
		query.put("location", new BasicDBObject(QueryOperators.NE,null));
		DBObject sort = new BasicDBObject();
        sort.put("price", -1);
		return fang.find(query).sort(sort);
	}
	/**
	 * 获取5A级景区
	 * 入参为5A级景区的集合、地市名称
	 */
	public DBCursor getFiveAscenic(DBCollection fiveAscenic,String cityname){
		DBObject query = getquery("baidu.lng","baidu.lat");
		//DBObject query = new BasicDBObject();
		query.put("city", cityname);
		return fiveAscenic.find(query);
	}
	/**
	 * 设置经纬度的查询条件
	 */
	public DBObject getquery(String lngfiled,String latfiled){
		DBObject query = new BasicDBObject();
		BasicDBList lnglat = new BasicDBList();  
		lnglat.add(new BasicDBObject(lngfiled, new BasicDBObject("$gte", minLng)));  
		lnglat.add(new BasicDBObject(lngfiled, new BasicDBObject("$lte", maxLng)));  
		lnglat.add(new BasicDBObject(latfiled, new BasicDBObject("$gte", minLat)));  
		lnglat.add(new BasicDBObject(latfiled, new BasicDBObject("$lte", maxLat)));  
		query.put("$and", lnglat);
		return query;
	}
	/**
	 * 从dianping数据获取mall的地址
	 * 入参为mall的名字
	 */
//	public String getMallAddress(String mallName){
//		DBObject query = new BasicDBObject();
//		query.put("shopInfo.shopName", mallName);
//		BasicDBObject limit = new BasicDBObject();
//	    limit.put("shopInfo.address", 1);
//	    DBCursor city_dbc = city_2.find(query,limit);
//	    if(city_dbc.hasNext()){
//	    	DBObject dbo = city_dbc.next();
//	    	return ((DBObject)dbo.get("shopInfo")).get("address").toString();
//	    }else{
//	    	return "";
//	    }
//	}
	/**
	 * 将集合中的数据导入到list中
	 */
	public List<SourcePOI> collToList(DBCursor cur_mall){
		List<SourcePOI> list_mall = new ArrayList<SourcePOI>();
		while (cur_mall.hasNext()) {
			list_mall.add(gson.fromJson(cur_mall.next().toString(), SourcePOI.class));
		}
		return list_mall;
	}
	/**
	 * 将输入插入表
	 */
	public void insertColl(DBCollection allmall,SourcePOI addmall){
		DBObject dbo = (DBObject)JSON.parse(gson.toJson(addmall));
		dbo.put("_id", addmall.getId());
		WriteResult wr = allmall.save(dbo);
		System.out.println(wr.getN());
	}
	/**
	 * 根据地市名称获取购物中心数据
	 */
	public DBCursor getMallByCityname(String mall,String city){
		DBObject query = new BasicDBObject();
		query.put("city", city);
		return db4demo.getCollection(mall).find(query);
	}
	/**
	 * 根据keyword删除poi表中的数据
	 */
	public void delByKey(DBCollection poi_info,String keyword){
		DBObject query = new BasicDBObject();
		query.put("keyword", keyword);
		poi_info.remove(query);
	}
	/**
	 * 关闭mongo链接
	 */
	public void closeMo(){
		if(null != mg4)
			mg4.close();
	}
	
	public List<SourcePOI> getMall(String path){
		System.out.println(path);
		List<SourcePOI> list = new ArrayList<SourcePOI>();
		try {
            String encoding="utf-8";
            File file=new File(path);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	//System.out.println(lineTxt);
                    list.add(gson.fromJson(lineTxt, SourcePOI.class));
                }
                read.close();
	    }else{
	        System.out.println("找不到指定的文件");
	    }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错");
	        e.printStackTrace();
	    }
		
		return list;
	}
	
	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public double distance(double long1, double lat1, double long2, double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2*R*Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)*Math.cos(lat2) * sb2 * sb2));
		return d;
	}
	/**
	 * 根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
	 */
	public double[] getAround(double lon, double lat, int raidus) {

		Double latitude = lat;
		Double longitude = lon;

		Double degree = (24901 * 1609) / 360.0;
		double raidusMile = raidus;

		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		minLat = latitude - radiusLat;
		maxLat = latitude + radiusLat;

		Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		minLng = longitude - radiusLng;
		maxLng = longitude + radiusLng;
		// System.out.println("["+minLat+","+minLng+","+maxLat+","+maxLng+"]");
		return new double[] { minLat, minLng, maxLat, maxLng };
	}
//	public void backCtrip(DBCollection ctrip_coll111,DBCollection ctrip_coll){
//		DBCursor cur_ctrip = ctrip_coll.find();
//		System.out.println("size--------------:"+cur_ctrip.size());
//        try {
//			while(cur_ctrip.hasNext()){
//		    	ctrip_coll111.insert(cur_ctrip.next());
//			}
//        } catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public static void main(String[] args) {
		
		Poi_Info_Action poi = new Poi_Info_Action("192.168.1.4","demo",27017);
		DBCollection ctrip_hotel = poi.db4demo.getCollection("ctrip_new");
		poi.translateCtrip(ctrip_hotel);
		
		
	}
}

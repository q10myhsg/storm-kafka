package com.fangcheng.recommend.model.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.db.MongoDb;
import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.bean.TempClass;
import com.fangcheng.recommend.model.bean.UserGraphBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.recommend.model.onlineModel.StoreMaxNNModel;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 更新用户权重及历史
 * 
 * @author Administrator
 *
 */
public class UpdateWeightAction extends BaseFunction {

	public static final long serialVersionUID = 1L;
	public static final Logger LOG = LoggerFactory
			.getLogger(UpdateWeightAction.class);

	private MongoDb mongo = null;

//	private int loggerCount = 50;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		OnlineRecommendTopology.init();
		
		mongo = new MongoDb(Config.configBean.mongo.ip,
				Config.configBean.mongo.port, Config.configBean.mongo.defaultDb,Config.configBean.mongo.database,Config.configBean.mongo.user,Config.configBean.mongo.pwd);
	}

	@Override
	public void execute(TridentTuple paramTridentTuple,
			TridentCollector paramTridentCollector) {
		// TODO Auto-generated method stub
		UserGraphBean userBean = (UserGraphBean) paramTridentTuple.getValue(0);
		// System.out.println("权重进入:"+JsonUtil.getJsonStr(paramTridentTuple.getValue(0)));
		// 判断修改mongodb的userid对应的权重信息
		// 从mongo中获取数据
		if (userBean.userId == null) {
		} else {
			BasicDBObject query = new BasicDBObject();
			query.put("uid", userBean.userId);
			// id
			query.put("i", userBean.id);
			// 城市
			query.put("c", userBean.city);
			// 分类
			query.put("t", userBean.cu);
			// 从weight source 处理
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			// DBObject
			// mongoObj=mongo.findOne(Config.configBean.recommandWeightSource,query);
			BasicDBObject obj2 = new BasicDBObject();
			BasicDBObject obj3 = new BasicDBObject();
			// 数值自增
			// 对整个weight重新调整s
			obj3.put("w" + userBean.reCate, 1);
			obj2.put("$inc", obj3);
			mongo.update(Config.configBean.recommandWeightSource, query, obj2,
					true, false);
			// 新增 对应的数据近期数据集
			// 只能查出来修改了
			BasicDBObject obj4 = new BasicDBObject();
			obj4.put("i", userBean.id);
			obj4.put("uid",userBean.userId);
			DBObject wObj = mongo.findOne(Config.configBean.reUserLoggerData,
					obj4);
			// String str="";
			ObjectNode json = JsonUtil.init();
			json.put("i", userBean.id);
			json.put("uid",userBean.userId);
			boolean flag = false;
			ArrayList<TempClass> list = new ArrayList<TempClass>();
			if (wObj != null) {
				ArrayNode array = (ArrayNode) JsonUtil.parse(wObj.get("v"));
				for (int i = 0; i < array.size(); i++) {
					ObjectNode temp = (ObjectNode) array.get(i);
					TempClass te = new TempClass();
					te.i = temp.get("i").asLong();
					te.a = temp.get("a").asInt();
					te.s = temp.get("s").asDouble();
					if (te.i == userBean.refId) {
						te.a += userBean.num;
						te.s +=getScore();
						list.add(0, te);
						flag = true;
					} else {
						list.add(te);
					}
				}
			}
			if (!flag) {
				TempClass te = new TempClass();
				te.i = userBean.refId;
				te.a = userBean.num;
				te.s =getScore();
				list.add(te);
			}
			json.replace("v", (ArrayNode) JsonUtil.parse(list));
			// json.put("v", list);
			// 更新最终的用户浏览历史排序
			// System.out.println(json.toString());
			mongo.update(Config.configBean.reUserLoggerData, obj4,
					json.toString(), true, false);
			// List<TempClass> temp=new
			// result.put("v",List<>);
			List<Object> values = new ArrayList<Object>();
			values.add(userBean);
			paramTridentCollector.emit(values);
		}
		
	}
	public double getScore(){
		 return Math
			.log((System.currentTimeMillis() - StoreMaxNNModel.timeInit));
	}

	public void runUpdate(UserGraphBean userBean) {
		BasicDBObject query = new BasicDBObject();
		// id
		query.put("i", userBean.id);
		query.put("uid",userBean.userId);
		// 城市
		query.put("c", userBean.city);
		// 分类
		query.put("t", userBean.cu);
		// 从weight source 处理
//		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		// DBObject
		// mongoObj=mongo.findOne(Config.configBean.recommandWeightSource,query);
		BasicDBObject obj2 = new BasicDBObject();
		BasicDBObject obj3 = new BasicDBObject();
		// 数值自增
		// 对整个weight重新调整s
		obj3.put("w" + userBean.reCate, 1);
		obj2.put("$inc", obj3);
		mongo.update(Config.configBean.recommandWeightSource, query, obj2,
				true, false);
		// 新增 对应的数据近期数据集
		// 只能查出来修改了
		BasicDBObject obj4 = new BasicDBObject();
		obj4.put("uid",userBean.userId);
		obj4.put("i", userBean.id);
		DBObject wObj = mongo.findOne(Config.configBean.reUserLoggerData, obj4);
//		String str = "";
		ObjectNode json = JsonUtil.init();
		json.put("i", userBean.id);
		json.put("uid",userBean.userId);
		boolean flag = false;
		ArrayList<TempClass> list = new ArrayList<TempClass>();
		if (wObj != null) {
			ObjectNode array = (ObjectNode) JsonUtil.parse(wObj.get("v"));
			for (int i = 0; i < array.size(); i++) {
				ObjectNode temp = (ObjectNode) array.get(i);
				TempClass te = new TempClass();
				te.i = temp.get("i").asLong();
				te.a = temp.get("a").asInt();
				te.s = temp.get("s").asDouble();
				if (te.i == userBean.refId) {
					te.a += userBean.num;
					te.s +=getScore();
					list.add(0, te);
					flag = true;
				} else {
					list.add(te);
				}
			}
		}
		if (!flag) {
			TempClass te = new TempClass();
			te.i = userBean.refId;
			te.a = userBean.num;
			te.s = getScore();
			list.add(te);
		}
		json.replace("v", (ArrayNode) JsonUtil.parse(list));
		// json.put("v", list);
		// 更新最终的用户浏览历史排序
		// System.out.println(json.toString());
		mongo.update(Config.configBean.reUserLoggerData, obj4, json.toString(),
				true, false);
	}

	public static void main(String[] args) {
		 System.out.println(Math.log((System.currentTimeMillis() - StoreMaxNNModel.timeInit)));
	}

}

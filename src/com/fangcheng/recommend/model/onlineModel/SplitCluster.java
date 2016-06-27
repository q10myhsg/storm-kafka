package com.fangcheng.recommend.model.onlineModel;

import java.util.ArrayList;
import java.util.LinkedList;
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
import com.fangcheng.recommend.model.bean.UserGraphBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.recommend.model.topology.OnlineRecommendTopology;
import com.fangcheng.recommend.model.topology.OutbreakDetector;
import com.fangcheng.recommend.model.topology.UpdateWeightAction;
import com.fangcheng.util.JsonUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SplitCluster extends BaseFunction {

	public static final long serialVersionUID = 1L;
	public static final Logger LOG = LoggerFactory
			.getLogger(SplitCluster.class);
	public static final String mallName = "DynamicClusterModel_mall";
	public static final String brandName = "DynamicClusterModel_brand";
	private DynamicClusterModel mallModel = null;
	private DynamicClusterModel brandModel = null;
	private int count = 3000;
	private MongoDb mongo = null;
	private int dbmsCount = 3;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
	
		OnlineRecommendTopology.init();
		mallModel = new DynamicClusterModel(mallName, dbmsCount);
		// 加载模块
		boolean flag = mallModel.read();
		mongo = new MongoDb(Config.configBean.mongo.ip,
				Config.configBean.mongo.port, Config.configBean.mongo.defaultDb,Config.configBean.mongo.database,Config.configBean.mongo.user,Config.configBean.mongo.pwd);
		if (!flag) {
			// 如果模型获取失败则 重新加载一次数据生成模型
			LinkedList<BasicDBObject> data = new LinkedList<BasicDBObject>();
			DBObject obj = new BasicDBObject();
			obj.put("t", 0);
			DBCursor cursor = mongo.find(Config.configBean.userFeatureT, obj);
			while (cursor.hasNext()) {
				data.add((BasicDBObject) cursor.next());
				if (data.size() > count) {
					break;
				}
			}
			mallModel = new DynamicClusterModel(data, mallName, dbmsCount);
		}
		// 周期性存储数据 及周期性 更新模型
		ModelToolTimer timer = new ModelToolTimer(mallModel, true);
		timer.start();

		brandModel = new DynamicClusterModel(brandName, dbmsCount);
		// 加载模块
		flag = brandModel.read();
		if (!flag) {
			// 如果模型获取失败则 重新加载一次数据生成模型
			LinkedList<BasicDBObject> data = new LinkedList<BasicDBObject>();
			DBObject obj = new BasicDBObject();
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("$ne", 0);
			obj.put("t", obj2);
			DBCursor cursor = mongo.find(Config.configBean.userFeatureT, obj);
			while (cursor.hasNext()) {
				data.add((BasicDBObject) cursor.next());
				if (data.size() > count) {
					break;
				}
			}
			brandModel = new DynamicClusterModel(data, brandName, dbmsCount);
		}
		// 周期性存储数据 及周期性 更新模型
		ModelToolTimer timer2 = new ModelToolTimer(brandModel, true);
		timer2.start();
	}

	@Override
	public void execute(TridentTuple paramTridentTuple,
			TridentCollector paramTridentCollector) {
		// TODO Auto-generated method stub
		try {
			UserGraphBean userGraphBean = (UserGraphBean) paramTridentTuple
					.getValue(0);
			// 获取对应的分类
			// 从此处获取用户的属性
//			System.out.println(paramTridentTuple
//					.getValue(0));
//			System.out.println("userGraph:"
//					+ JsonUtil.getJsonStr(userGraphBean));
			BasicDBObject obj = new BasicDBObject();
			obj.put("i", userGraphBean.id);
			obj.put("c", userGraphBean.city);
			if(userGraphBean.type>1)
			{//为brand
				obj.put("t",userGraphBean.cu);
			}
			DBCursor cursor = mongo.find(Config.configBean.userFeatureT, obj);
			BasicDBObject data = null;
			while (cursor.hasNext()) {
				BasicDBObject dbobj = (BasicDBObject) cursor.next();
				data = dbobj;
				break;
			}
			if(data==null){
				//说明 该user可能 是新入的数据，也有可能是根本没有属性
				//需要生成
				return;
			}
			int clusterId = -1;
			if (userGraphBean.type < 2) {
				clusterId = mallModel.addFeather(data);
			} else {
				clusterId = brandModel.addFeather(data);
			}
			if (clusterId < 0) {
				return;
			}
//			System.out.println("传入内容为：" + JsonUtil.getJsonStr(userGraphBean));
//			System.out.println("划分后的分类号:" + clusterId);
			// Emit the value.
			List<Object> values = new ArrayList<Object>();
			values.add(Integer.toString(clusterId));
			values.add(userGraphBean);
			// LOG.debug("");
			paramTridentCollector.emit(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// 加载模块
		int count = 3000;
		int dbmsCount = 3;

		DynamicClusterModel mallModel = new DynamicClusterModel(
				"DynamicClusterModel", dbmsCount);

		boolean flag = mallModel.read();
		MongoDb mongo = new MongoDb(Config.configBean.mongo.ip, Config.configBean.mongo.port,Config.configBean.mongo.database);
		if (!flag) {
			// 如果模型获取失败则 重新加载一次数据生成模型
			LinkedList<BasicDBObject> data = new LinkedList<BasicDBObject>();
			DBObject obj = new BasicDBObject();
			obj.put("t", 0);
			DBCursor cursor = mongo.find(Config.configBean.userFeatureT, obj);
			while (cursor.hasNext()) {
				data.add((BasicDBObject) cursor.next());
				if (data.size() > count) {
					break;
				}
			}
			mallModel = new DynamicClusterModel(data, mallName, dbmsCount);
		}
		flag = mallModel.write();
		System.out.println("写入模型:" + flag);

		DynamicClusterModel brandModel = new DynamicClusterModel(brandName,
				dbmsCount);
		// 加载模块
		flag = brandModel.read();
		if (!flag) {
			// 如果模型获取失败则 重新加载一次数据生成模型
			LinkedList<BasicDBObject> data = new LinkedList<BasicDBObject>();
			DBObject obj = new BasicDBObject();
			// BasicDBObject obj2=new BasicDBObject();
			DBCursor cursor = mongo.findCursor(Config.configBean.userFeatureT, "{t:{$ne:0}}",
					"{}");
			while (cursor.hasNext()) {
				data.add((BasicDBObject) cursor.next());
				if (data.size() > count) {
					break;
				}
			}
			brandModel = new DynamicClusterModel(data, brandName, dbmsCount);
		}
		flag = brandModel.write();
		System.out.println("写入模型:" + flag);
	}
}

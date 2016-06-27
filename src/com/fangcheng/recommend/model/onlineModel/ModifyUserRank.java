package com.fangcheng.recommend.model.onlineModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.db.MongoDb;
import com.db.MysqlConnection;
import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.bean.UserGraphBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.recommend.model.topology.OnlineRecommendTopology;
import com.fangcheng.util.JsonUtil;

public class ModifyUserRank extends BaseFunction {

	
	public static final long serialVersionUID = 1L;
	public static final Logger LOG = LoggerFactory
			.getLogger(ModifyUserRank.class);
	private StoreMaxNNModel model = null;
	private MongoDb mongo = null;
	private MysqlConnection mysql = null;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		OnlineRecommendTopology.init();
		//System.out.println(JsonUtil.getJsonStr(Config.configBean));
		mongo = new MongoDb(Config.configBean.mongo.ip,
				Config.configBean.mongo.port, Config.configBean.mongo.defaultDb,Config.configBean.mongo.database,Config.configBean.mongo.user,Config.configBean.mongo.pwd);
		mysql = new MysqlConnection(Config.configBean.mysql.ip,
				Config.configBean.mysql.port, Config.configBean.mysql.database,
				Config.configBean.mysql.user, Config.configBean.mysql.pwd);
	}

	@Override
	public void execute(TridentTuple paramTridentTuple,
			TridentCollector paramTridentCollector) {
		// TODO Auto-generated method stub
		String cluster = paramTridentTuple.getString(0);
	//	System.out.println("传入cluster:" + cluster);
		// 出现多个cluster到一个node上
		ModelToolTimer timer = null;
		if (model == null || !model.modelNameTailName.equals(cluster)) {// 加载模块
			//System.out.println("从新加载");
			if (timer != null) {
				timer.isStop = true;
			}
			if (model != null) {
				model.write();
			}
			model = new StoreMaxNNModel(mongo, mysql, cluster);
			model.modelNameTailName = cluster;
			// 加载模块
			model.read();
			// 周期性存储数据 及周期性 更新模型
			timer = new ModelToolTimer(model, false);
			timer.start();
		}
	//	System.out.println("maxModel传入参数:" + paramTridentTuple.getValue(1));
		UserGraphBean userGraphBean = (UserGraphBean) paramTridentTuple
				.getValue(1);
		// 更新到对应的更新数据库中
		model.updateDb(userGraphBean.city, userGraphBean.cu, userGraphBean.id,
				userGraphBean.refId, userGraphBean.num, userGraphBean.type);
		// Emit the value.
		 List<Object> values = new ArrayList<Object>();
		 values.add(userGraphBean.city+":"+userGraphBean.cu+":"+userGraphBean.id+":"+userGraphBean.refId);
		 values.add(userGraphBean.num);
		 paramTridentCollector.emit(values);
	}

	public static void main(String[] args) {
		StoreMaxNNModel model = null;
		MongoDb mongo = null;
		MysqlConnection mysql = null;
		mongo = new MongoDb(Config.configBean.mongo.ip,
				Config.configBean.mongo.port, Config.configBean.mongo.database);
		mysql = new MysqlConnection(Config.configBean.mysql.ip,
				Config.configBean.mysql.port, Config.configBean.mysql.database,
				Config.configBean.mysql.user, Config.configBean.mysql.pwd);
		// 周期性存储数据 及周期性 更新模型
		ModelToolTimer timer = null;
		String cluster = "1";
		if (model == null || model.modelNameTailName != cluster) {// 加载模块
			//System.out.println("创建:");
			if (timer != null) {
				timer.isStop = true;
			}
			model = new StoreMaxNNModel(mongo, mysql, cluster);
			model.modelNameTailName = cluster;
			// 加载模块
			model.read();
			timer = new ModelToolTimer(model, false);
			timer.start();
		}
		String temp = "{\"city\":86999030,\"count\":1,\"cu\":10000,\"id\":6,\"num\":1,\"refId\":5,\"set\":[],\"setRef\":[],\"type\":0}";
		System.out.println("maxModel传入参数:" + temp);
		// UserGraphBean
		// userGraphBean=(UserGraphBean)JsonUtil.getDtoFromJsonObjStr(temp,
		// UserGraphBean.class);
		for (int i = 0; i < 100; i++) {
			UserGraphBean userGraphBean = new UserGraphBean("test",0,0, 86999030, 10000,1,
					6, 5, 1);
			// 更新到对应的更新数据库中
			model.updateDb(userGraphBean.city, userGraphBean.cu,
					userGraphBean.id, userGraphBean.refId, userGraphBean.num,
					userGraphBean.type);
			System.out.println(model.getValue(userGraphBean.cu,
					userGraphBean.id, userGraphBean.refId, userGraphBean.type));
		}
	}

}

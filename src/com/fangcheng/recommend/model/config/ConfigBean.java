package com.fangcheng.recommend.model.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ConfigBean implements Serializable {

	/**
	 * mysql的连接参数
	 */
	public DbBean mysql = new DbBean();
	/**
	 * mongo 的基本连接参数
	 */
	public DbBean mongo = new DbBean();
	/**
	 * 用户对应的 不同的推荐源的权重表
	 */
	public String recommandWeightSource = null;
	/**
	 * 给mall推荐的 数据源 和对应的权重
	 */
	public HashMap<Integer, KeyValue> useMallSource = new HashMap<Integer, KeyValue>();
	public HashMap<String,Integer> useMallMap=new HashMap<String,Integer>();
	/**
	 * 给brand 推荐的 不同数据源 和 对应的权重
	 */
	public HashMap<Integer, KeyValue> useBrandSource = new HashMap<Integer, KeyValue>();
	public HashMap<String,Integer> useBrandMap=new HashMap<String,Integer>();
	/**
	 * 获取数据源的时候所使用的数据源
	 */
	public HashSet<Integer> usePluginMall = new HashSet<Integer>();
	/**
	 * 对应mongo中的id
	 */
	public DBObject mallUseId = new BasicDBObject();
	/**
	 * mallUseId上述使用的id
	 */
	public BasicDBList mallvalues = new BasicDBList();
	/**
	 * 获取数据源使用功能的插件id
	 */
	public HashSet<Integer> usePluginBrand = new HashSet<Integer>();
	/**
	 * 对应mongo中id
	 */
	public DBObject brandUseId = new BasicDBObject();
	/**
	 * brandUseId上述使用的id
	 */
	public BasicDBList brandvalues = new BasicDBList();
	/**
	 * 对应的增量日志推送 到kafka对应的topic
	 */
	public String kafkaUserTopic = null;
	/**
	 * 每一次推送的数据都会被记录
	 */
	public String kafkaReLoggerTopic=null;
	/**
	 * 用户点击日志
	 */
	public String kafkaUserActionTopic=null;
	/**
	 * 统一的日志传输到hdfs中
	 */
	public String kafkaLoggerToHdfs=null;
	/**
	 * 用户历史点击
	 */
	public String reUserLoggerData="reUserLoggerService";
	/**
	 * 推荐源使用的表
	 */
	public String reServiceDataTable = "reService";
	/**
	 * 相似度的源使用表
	 */
	public String reSimServiceDataTable = "reSimService";
	/**
	 * mall及品牌基础feature属性表
	 */
	public String userFeature="userFeature";
	/**
	 * mall 及brandfeature 基础属性表 为转换后的表
	 * 参考 com.fangcheng.dataBuild.UserFeatureData的生成原理
	 */
	public String userFeatureT ="userFeatureT";
	/**
	 * kafka对应的消费者id
	 */
	public String kafkaGroupId="recommend";

	public ConfigBean() {
		mallUseId.put("$in", mallvalues);
		brandUseId.put("$in", brandvalues);
	}

	public DbBean getMysql() {
		return mysql;
	}

	public void setMysql(DbBean mysql) {
		this.mysql = mysql;
	}

	public DbBean getMongo() {
		return mongo;
	}

	public void setMongo(DbBean mongo) {
		this.mongo = mongo;
	}

	public String getRecommandWeightSource() {
		return recommandWeightSource;
	}

	public void setRecommandWeightSource(String recommandWeightSource) {
		this.recommandWeightSource = recommandWeightSource;
	}

	public HashMap<Integer, KeyValue> getUseMallSource() {
		return useMallSource;
	}

	public void setUseMallSource(HashMap<Integer, KeyValue> useMallSource) {
		this.useMallSource = useMallSource;
	}

	public HashMap<Integer, KeyValue> getUseBrandSource() {
		return useBrandSource;
	}

	public void setUseBrandSource(HashMap<Integer, KeyValue> useBrandSource) {
		this.useBrandSource = useBrandSource;
	}

	public void addUseBrandSource(int key, KeyValue useBrandSource) {
		this.useBrandSource.put(key, useBrandSource);
		this.usePluginBrand.add(key);
		brandvalues.add(key);

	}

	public void removeBrandSource(Integer key) {
		this.usePluginBrand.remove(key);
		brandvalues.remove(key);
	}

	public void removeMallSource(Integer key) {
			this.usePluginMall.remove(key);
			mallvalues.remove(key);
	}

	public void addUseMallSource(Integer key, KeyValue useMallSource) {
		this.useMallSource.put(key, useMallSource);
		this.usePluginMall.add(key);
		mallvalues.add(key);
	}

}

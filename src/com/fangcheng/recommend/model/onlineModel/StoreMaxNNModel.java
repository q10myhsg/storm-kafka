package com.fangcheng.recommend.model.onlineModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;

import com.db.MongoDb;
import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.fangcheng.recommend.model.bean.DataModel;
import com.fangcheng.recommend.model.bean.StoreBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.util.HdfsUtil;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * topNN模型
 * 
 * 因为用户太少 需要使用多层传播 放大
 * @author Administrator
 *
 */
public class StoreMaxNNModel extends DataModel {
	public int onlineId = 2;
	/**
	 * 推荐 mongo 表
	 */
	public String collectionRe =Config.configBean.reServiceDataTable;
	/**
	 * 相似 mongo 表
	 */
	public String collectionSim = Config.configBean.reSimServiceDataTable;
	/**
	 * 存储大小
	 */
	public int size = 30;
	/**
	 * 2015-07-15
	 */
	public static long timeInit = 1436900000000L;
	/**
	 * 用户对应物品数量
	 */
	public int topN = 5;
	/**
	 * 物品对应的用户数量
	 */
	public int topNN = 3;
	/**
	 * 相似用户数量
	 */
	public int topSimNN = 20;
	/**
	 * 最终获取20个
	 */
	public int topNNN = 20;
	public MongoDb mongo = null;

	public MysqlConnection mysql = null;

	/**
	 * 直接获取可以获取 id对应的历史搜索最优数据 key 对应 品牌或者mall的业态 key2 为 id值
	 */
	public HashMap<Integer, HashMap<Long, StoreListBean>> store = new HashMap<Integer, HashMap<Long, StoreListBean>>();
	/**
	 * 相似用户点击历史 key 为id
	 */
	public HashMap<Long, StoreListBean> storeSim = new HashMap<Long, StoreListBean>();

	public StoreMaxNNModel(MongoDb mongo, MysqlConnection mysql,
			String clusterId) {
		this.mongo = mongo;
		this.mysql = mysql;
		modelName = "StoreMaxNNModel";
		modelNameTailName = clusterId;
	}

	/**
	 * 更新 online方法对应的数据库状态
	 * 
	 * @param id
	 *            点击用户
	 * @param idrel
	 *            被点击物品
	 * @param size
	 *            大小
	 * @param type
	 *            类型 0 mall-brand 1 mall-mall 2 brand-brand 3 brand-mall
	 */
	public void updateDb(int city, int category, long id, long idrel,
			int powerNum, int type) {
		if (type == 0 || type == 3) {// 执行推荐
			HashMap<Integer, ArrayList<StoreBean>> recommendData = null;
			if (type == 0) {
				recommendData = get(category, id, idrel, powerNum, true);
			} else {
				recommendData = get(category, id, idrel, powerNum, true);
			}
			//System.out.println("推荐结果:" + JsonUtil.getJsonStr(recommendData));
			for (Entry<Integer, ArrayList<StoreBean>> ma : recommendData
					.entrySet()) {
				updateDb(city, ma.getKey(), id, ma.getValue(), true);
			}
		} else {// 执行相似
			HashMap<Integer, ArrayList<StoreBean>> similaryData = get(0, id,
					idrel, powerNum, false);
			//System.out.println("推荐结果:" + JsonUtil.getJsonStr(similaryData));
			for (Entry<Integer, ArrayList<StoreBean>> ma : similaryData
					.entrySet()) {
				updateDb(city, ma.getKey(), id, ma.getValue(), false);
			}
		}
	}

	/**
	 * 更新数据到 mongo中
	 * 
	 * @param mongo
	 * @param data
	 * @param isRecommend
	 * @param 如果为相似度的点击
	 *            则可以没有category
	 */
	public void updateDb(int city, int category, long id,
			ArrayList<StoreBean> data, boolean isRecommend) {
		if (isRecommend) {
			DBObject _id = new BasicDBObject();
			_id.put("id", this.onlineId);
			_id.put("t", city);
			_id.put("c", category);
			_id.put("i", id);
			// _id.put("_id", new
			// ObjectId("re_"+onlineId+"_"+id+"_"+city+"_"+category));
			ObjectNode obj = JsonUtil.init();
			// 首先获取数据
			//System.out.println("request:"+collectionRe+"\t"+_id);
			BasicDBObject mongoObj =null ;
			//System.out.println("mongo:"+mongo);
			DBObject objMo=mongo.findOne(
					collectionRe, _id);
			if(objMo!=null){
				mongoObj=(BasicDBObject)objMo;
			}
			ArrayList<StoreBean> list = null;
			if (mongoObj != null) {
				// 整合数据集
				//System.out.println(mongoObj.getString("r"));
				list = (ArrayList<StoreBean>)JsonUtil.getDtoArrFromJsonArrListStr(mongoObj.getString("r"),StoreBean.class);
				list = getSortCombine(list, data, (int) (this.size * 1.5));
			} else {
				list = data;
			}
			obj.put("id", this.onlineId);
			obj.put("i", id);
			obj.put("t", city);
			obj.put("c", category);
			obj.put("r", JsonUtil.getJsonStr(data));

			//System.out.println("新增:");
			//System.out.println(obj.toString());
			mongo.update(Config.configBean.mongo.database, collectionRe, _id,
					obj.toString());
		} else {
			DBObject _id = new BasicDBObject();
			ObjectNode obj = JsonUtil.init();
			_id.put("id", this.onlineId);
			_id.put("t", city);
			_id.put("i", id);
			BasicDBObject mongoObj =null;
			DBObject objMo=mongo.findOne(
					collectionSim, _id);
			if(objMo!=null){
				mongoObj=(BasicDBObject)objMo;
			}
			ArrayList<StoreBean> list = null;
			if (mongoObj != null) {
				// 整合数据集
				list = (ArrayList<StoreBean>)JsonUtil.getDtoArrFromJsonArrListStr(mongoObj.getString("s"),StoreBean.class);
				list = getSortCombine(list, data, (int) (this.size * 1.5));
			} else {
				list = data;
			}
			obj.put("id", this.onlineId);
			obj.put("i", id);
			obj.put("t", city);
			obj.put("s", JsonUtil.getJsonStr(data));

			// _id.put("_id", new ObjectId("re_"+onlineId+"_"+id+"_"+city));
			//System.out.println("新增:");
		//	System.out.println(obj.toString());
			mongo.update(Config.configBean.mongo.database, collectionSim, _id,
					obj.toString());
		}
	}

	/**
	 * 合并两个数据集合 并排序 获取规定数量
	 * 
	 * @param list
	 * @param data
	 * @param count
	 * @return
	 */
	public ArrayList<StoreBean> getSortCombine(ArrayList<StoreBean> list,
			ArrayList<StoreBean> data, int count) {
		if (list.size() == 0) {
			list = data;
		} else {
			HashMap<Long, StoreBean> val = new HashMap<Long, StoreBean>();
			for (StoreBean v : list) {
				val.put(v.i, v);
			}
			for (StoreBean v : data) {
				StoreBean v2 = val.get(v.i);
				if (v2 == null) {
					val.put(v.i, v);
				} else {
					v2.addMean(v);
				}
			}
			// 重排序
			list = new ArrayList<StoreBean>();
			for (Entry<Long, StoreBean> map : val.entrySet()) {
				list.add(map.getValue());
			}
			Collections.sort(list);
		}
		int zn = list.size() - count;
		while (zn > 0) {
			list.remove(list.size() - 1);
			zn--;
		}
		return list;
	}

	/**
	 * 获取当前值的数值
	 * 
	 * @param useCateId
	 * @param id
	 * @param idrel
	 * @param isRecommend
	 * @return
	 */
	public StoreBean getValue(int usseCateId, long id, long idrel, int type) {
		if (type == 0 || type == 3) {// 执行推荐
			HashMap<Long, StoreListBean> storeCate = store.get(usseCateId);
			if (storeCate == null) {
				storeCate = new HashMap<Long, StoreListBean>();
			}
			StoreListBean sto = storeCate.get(id);
			return sto.getVal(idrel);
		} else {
			StoreListBean sto = storeSim.get(id);
			return sto.getVal(idrel);
		}
	}

	/**
	 * @param useCateId
	 *            对于业态所使用的业态 或者对应 被点击用户
	 * @反向索引结构/倒排
	 * @param id
	 * @param size
	 *            如果为0表示值查询 不update
	 * @return key 对应业态 如果没有则 为-1 value 业态对应的推荐
	 */
	public HashMap<Integer, ArrayList<StoreBean>> get(int useCateId, long id,
			long idrel, int powerNum, boolean isRecommend) {
		HashMap<Integer, ArrayList<StoreBean>> endsValue = new HashMap<Integer, ArrayList<StoreBean>>();
		StoreBean bean = new StoreBean(idrel, powerNum);
		StoreBean bean2 = new StoreBean(id, powerNum);
		// 获取排序值
		if (isRecommend) {
			// 获取 idrel的分类情况
			// 需要从mysql中获取对应的数据
			// 获取被点击用户的所有业态
			// ArrayList<Integer> cateList = getCategory(useCateId);
			// for (Integer cate : cateList) {
			//System.out.println("store:size:" + store.size());
			HashMap<Long, StoreListBean> storeCate = store.get(useCateId);
			//System.out.println("分类:" + useCateId + ":" + storeCate);
			if (storeCate == null) {
				storeCate = new HashMap<Long, StoreListBean>();
				store.put(useCateId, storeCate);
			}
			//System.out.println("store:size:" + store.size());
			StoreListBean sto = storeCate.get(id);
			//System.out.println("id:" + id + "\tnew:" + sto);
			if (powerNum > 0) {
				if (sto == null) {
					sto = new StoreListBean();
					// 正向为1
					sto.add(bean, 1);
					storeCate.put(id, sto);
				} else {
					sto.add(bean, 1);
				}
				StoreListBean sto2 = storeCate.get(idrel);
				// 方向为0.1
				if (sto2 == null) {
					sto2 = new StoreListBean();
					sto2.add(bean2, 0.1);
					storeCate.put(idrel, sto2);
				} else {
					sto2.add(bean2, 0.1);
				}
			}

			ArrayList<StoreBean> reV = getRecommend(useCateId, id, topN,
					topSimNN, topNNN);
			if (reV != null && reV.size() > 0) {
				endsValue.put(useCateId, reV);
			}
			// }
			return endsValue;
		} else {
			StoreListBean sto = storeSim.get(id);
			if (powerNum > 0) {
				if (sto == null) {
					sto = new StoreListBean();
					sto.add(bean, 1);
					storeSim.put(id, sto);
				} else {
					sto.add(bean, 1);
				}
				StoreListBean sto2 = storeSim.get(idrel);
				if (sto2 == null) {
					sto2 = new StoreListBean();
					sto2.add(bean2, 0.1);
					storeSim.put(idrel, sto2);
				} else {
					sto2.add(bean2, 0.1);
				}
			}
			ArrayList<StoreBean> simV = getSimilary(id, topN, topSimNN, topNNN);
			if (simV != null && simV.size() > 0) {
				endsValue.put(-1, simV);
			}
			return endsValue;
		}
	}

	/**
	 * 获取某一个物品的业态
	 * 
	 * @param useCateId
	 * @return
	 */
	public ArrayList<Integer> getCategory(long useCateId) {
		MysqlSelect select = mysql
				.sqlSelect("select category_one_id from brand_shop where category_one_id>0 and brand_id="
						+ useCateId + " group by category_one_id");
		ResultSet set = select.resultSet;
		ArrayList<Integer> list = new ArrayList<Integer>();
		if (set == null) {
			return list;
		}
		try {
			while (set.next()) {
				int category = set.getInt(1);
				list.add(category);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取和id关联的所有其他id
	 * 
	 * @param category
	 *            使用的业态
	 * @param id
	 * @param topN
	 *            获取topn个物品对应的用户
	 * @param 获取最优的用户
	 * @return
	 */
	public ArrayList<StoreBean> getRecommend(int category, long id, int topN,
			int topNUser, int count) {
		// 获取id对应的物品
		HashMap<Long, StoreBean> result = new HashMap<Long, StoreBean>();
		HashMap<Long, StoreListBean> storeCategory = this.store.get(category);
		if (storeCategory == null) {
			return null;
		}
		StoreListBean storeList = storeCategory.get(id);
		if (storeList == null) {
			return null;
		}
		LinkedList<StoreBean> list = storeList.list;
		//System.out.println("id:" + id + "\tsize:" + list.size());
		// 该user已经点击过的物品列表
		HashMap<Long, StoreBean> map = storeList.map;
		int item1Count = topN;
		Iterator<StoreBean> iteratorItem1 = (Iterator<StoreBean>) list
				.iterator();
		while (iteratorItem1.hasNext()) {
			StoreBean bean = iteratorItem1.next();
			// 获取bean对应的 user
			StoreListBean userBean = storeCategory.get(bean.i);
//			System.out.println("item:" + bean.i + "\tsize:"
//					+ userBean.list.size());
			if (userBean == null) {
				continue;
			}
			// 获取最优的user
			LinkedList<StoreBean> userList = userBean.list;
			int userCount = topNUser;
			Iterator<StoreBean> iteratorUser = (Iterator<StoreBean>) userList
					.iterator();
			while (iteratorUser.hasNext()) {
				StoreBean user2Bean = iteratorUser.next();
				//System.out.println("物品存在的用户:" + user2Bean.i);
				if (user2Bean.i == id) {
					//System.out.println("用户重复");
					// 如果user重复则继续
					continue;
				}
				// 获取每个user对应的item
				StoreListBean item2Bean = storeCategory.get(user2Bean.i);
				if (item2Bean == null) {
					//System.out.println("用户为物品");
					continue;
				}
//				System.out.println("item-user-item:size:"
//						+ item2Bean.list.size());
				int item2Count = topN;
				Iterator<StoreBean> item2Iter = (Iterator<StoreBean>) userList
						.iterator();
				while (item2Iter.hasNext()) {
					StoreBean item3Bean = item2Iter.next();
					// 如果是该用户已经点击过的物品则过滤
					if (map.containsKey(item3Bean.i)) {
						continue;
					}
					StoreBean storM = result.get(item3Bean.i);
					// 将数据累积多个用户的 历史典籍
					if (storM == null) {
						result.put(item3Bean.i, item3Bean.clone());
					} else {
						storM.add(item3Bean);
					}
					// 如果 累积物品达到数量则跳出
					item2Count--;
					if (item2Count < 0) {
						break;
					}
				}
				// 如果累积用户达到数量则跳出
				userCount--;
				if (userCount < 0) {
					break;
				}
			}
			// 如果点击物品达到数量则跳出
			item1Count--;
			if (item1Count < 0) {
				break;
			}
		}
		// 重排序
		
		
		//通过相似用户获取 item列表
		StoreListBean simList=this.storeSim.get(id);
		if(simList!=null){
			Iterator<StoreBean> simIter=simList.list.iterator();
			int i=0;
			while(simIter.hasNext()){
				//获取相似用户的物品
				StoreBean be=simIter.next();
				StoreListBean be2=storeCategory.get(be.i);
				if(be2!=null){
					int j=0;
					Iterator<StoreBean>  items=be2.list.iterator();
					while(items.hasNext()){
						j++;
						StoreBean item=items.next();
						StoreBean beItem=result.get(item.i);
						if(beItem==null){
							result.put(item.i,item);
						}else{
							beItem.add(item);
						}
						if(j>=topNN){
							break;
						}
					}
				}
				i++;
				if(i>=topN){
					break;
				}
			}
		}
		//重排序
		ArrayList<StoreBean> endList = new ArrayList<StoreBean>();
		for (Entry<Long, StoreBean> m : result.entrySet()) {
			endList.add(m.getValue());
		}
		Collections.sort(endList);
		// 获取前topNNN个数据
		ArrayList<StoreBean> end = new ArrayList<>(
				endList.size() > topNNN ? topNNN : endList.size());
		for (StoreBean be : endList) {
			end.add(be);
		}
		// 返回重排序的数据
		return end;
	}

	/**
	 * 获取和id关联的所有其他id
	 * 
	 * @param id
	 * @param topN
	 *            获取topn个物品对应的用户
	 * @param 获取最优的用户
	 * @return
	 */
	public ArrayList<StoreBean> getSimilary(long id, int topN, int topNUser,
			int count) {
		// 获取id对应的物品
		HashMap<Long, StoreBean> result = new HashMap<Long, StoreBean>();
		StoreListBean storeList = this.storeSim.get(id);
		if (storeList == null) {
			return new ArrayList<StoreBean>();
		}
		//不适用其他用户 方法为获取 全部数据中包含该用户的数据
		LinkedList<StoreBean> list = storeList.list;
		// 该user已经点击过的user
		HashMap<Long, StoreBean> map = storeList.map;
		int item1Count = topN;
		Iterator<StoreBean> iteratorItem1 = (Iterator<StoreBean>) list
				.iterator();
		while (iteratorItem1.hasNext()) {
			StoreBean bean = iteratorItem1.next();
			// 获取bean对应的 user
			StoreListBean userBean = this.storeSim.get(bean.i);
			if (userBean == null) {
				continue;
			}
			// 获取最优的user
			LinkedList<StoreBean> userList = userBean.list;
			int userCount = topNUser;
			Iterator<StoreBean> iteratorUser = (Iterator<StoreBean>) userList
					.iterator();
			while (iteratorUser.hasNext()) {
				StoreBean user2Bean = iteratorUser.next();
				if (user2Bean.i == id) {
					// 如果user重复则继续
					continue;
				}
				// 获取每个user对应的user
				// 如果是该用户已经点击过的物品则过滤
				if (map.containsKey(user2Bean.i)) {
					continue;
				}
				StoreBean storM = result.get(user2Bean.i);
				// 将数据累积多个用户的 历史典籍
				if (storM == null) {
					result.put(user2Bean.i, user2Bean.clone());
				} else {
					storM.add(user2Bean);
				}
				// 如果累积用户达到数量则跳出
				userCount--;
				if (userCount < 0) {
					break;
				}
			}
			// 如果点击物品达到数量则跳出
			item1Count--;
			if (item1Count < 0) {
				break;
			}
		}
		// 重排序
		ArrayList<StoreBean> endList = new ArrayList<StoreBean>();
		for (Entry<Long, StoreBean> m : result.entrySet()) {
			endList.add(m.getValue());
		}
		Collections.sort(endList);
		// 返回重排序的数据
		// 获取前topNNN个数据
		ArrayList<StoreBean> end = new ArrayList<>(
				endList.size() > topNNN ? topNNN : endList.size());
		for (StoreBean be : endList) {
			end.add(be);
		}
		// 返回重排序的数据
		return end;
	}

	public class StoreListBean implements Serializable {
		public LinkedList<StoreBean> list = new LinkedList<StoreBean>();
		HashMap<Long, StoreBean> map = new HashMap<Long, StoreBean>();

		/**
		 * 添加属性
		 * 
		 * @param bean
		 */
		public void add(StoreBean bean, double weight) {
			bean.s = bean.s * weight;
			StoreBean val = map.get(bean.i);
			if (val == null) {
//				System.out.println("新增bean:id:" + bean.i + "\t:mapSize:"
//						+ map.size());
				map.put(bean.i, bean);
				list.add(bean);
			} else {
				//System.out.println("重复新增bean:id:" + bean.i);
				val.add(bean);
			}
			sort();
		}

		public StoreBean getVal(Long id) {
			return map.get(id);
		}

		public void sort() {
			Collections.sort(list);
		}

		/**
		 * 移除超过的数量
		 */
		public void remove() {
			int zn = this.list.size() - size;
			if (zn > 0) {
				while (zn > 0) {
					// 移除最后一个
					StoreBean bean = this.list.pollLast();
					map.remove(bean.i);
					zn--;
				}
			}
		}

		public LinkedList<StoreBean> getList() {
			return list;
		}

		public void setList(LinkedList<StoreBean> list) {
			this.list = list;
		}

		public HashMap<Long, StoreBean> getMap() {
			return map;
		}

		public void setMap(HashMap<Long, StoreBean> map) {
			this.map = map;
		}

	}

	

	@Override
	public void updateModel() {
		// TODO Auto-generated method stub
		return;
	}

	/**
	 * 模型写文件
	 * 
	 * @param hdfsModelFile
	 */
	@Override
	public boolean write() {
		// 需要重命名原来的数据文件
		HdfsUtil hdfs = new HdfsUtil();
		try {
			hdfs.renameFile(modelFile + modelName + modelNameTailName
					+ extendName, modelFile + modelName + modelNameTailName
					+ getDataString() + extendName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FSDataOutputStream output = hdfs.getWriteFile(modelFile + modelName
					+ modelNameTailName + extendName);
			ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new GZIPOutputStream(output)));
			oos.writeInt(size);
			oos.writeLong(timeInit);
			oos.writeInt(topN);
			oos.writeInt(topNN);
			oos.writeInt(topSimNN);
			oos.writeInt(topNNN);
			oos.writeObject(JsonUtil.getJsonStr(store));
			oos.writeObject(JsonUtil.getJsonStr(storeSim));
			oos.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// 使用java 自己的 不用kroy
		return true;
	}

	@Override
	/**
	 * 模型读取
	 */
	public boolean read() {
		HdfsUtil hdfs = new HdfsUtil();
		try {
//			System.out.println(modelFile + modelName
//					+ modelNameTailName + extendName);
			FSDataInputStream input = hdfs.getReadFile(modelFile + modelName
					+ modelNameTailName + extendName);
			if(input==null){
				return false;
			}
			ObjectInputStream obj = new ObjectInputStream(
					new BufferedInputStream(new GZIPInputStream(input)));
			this.size = obj.readInt();
			this.timeInit = obj.readLong();
			this.topN = obj.readInt();
			this.topNN = obj.readInt();
			this.topSimNN = obj.readInt();
			this.topNNN = obj.readInt();
			String storeString = obj.readObject().toString();
			// System.out.println("读取json:" + storeString);
			this.store = new HashMap<Integer, HashMap<Long, StoreListBean>>();
			ObjectNode cateObj = JsonUtil.parse(storeString);
			Iterator<String> key = cateObj.fieldNames();
			while(key.hasNext()){
				String k=key.next();
				ObjectNode idObj = (ObjectNode)cateObj.get(k);
				HashMap<Long, StoreListBean> list = new HashMap<Long, StoreListBean>();
				this.store.put(Integer.parseInt(k), list);
				for (int l = 0; l < idObj.size(); l++) {
					ObjectNode id2Obj = (ObjectNode)(idObj.get(l));
					Iterator<String> id2Key = id2Obj.fieldNames();
					while(id2Key.hasNext()){
						String id2K=id2Key.next();
						StoreListBean mz = new StoreListBean();
						ObjectNode enObj = (ObjectNode)(id2Obj
								.get(id2K));
						ArrayNode array = (ArrayNode)(enObj
								.get("list"));
						for (int i = 0; i < array.size(); i++) {
							ObjectNode o = (ObjectNode)array.get(i);
							long id = o.get("i").asLong();
							int num = o.get("a").asInt();
							double score = o.get("s").asDouble();
							mz.add(new StoreBean(id, num, score), 1);
						}
						list.put(Long.parseLong(id2K), mz);
					}

				}
			}
			// for(Entry<Integer,HashMap<Long,StoreListBean>>
			// m:store.entrySet()){
			// for(Entry<Long,StoreListBean> b:m.getValue().entrySet()){
			// System.out.println(m.getKey()+":"+b.getValue());
			// }
			// }
			this.storeSim = new HashMap<Long, StoreListBean>();

			String storeSimString = obj.readObject().toString();
			// System.out.println("读取json:" + storeString);
			cateObj = JsonUtil.parse(storeString);
			key = cateObj.fieldNames();
			while(key.hasNext()){
				String k=key.next();
				ObjectNode id2Obj = (ObjectNode)(cateObj.get(k));
				Iterator<String> id2Key = id2Obj.fieldNames();
				while(id2Key.hasNext()){
					String id2K=id2Key.next();
					StoreListBean mz = new StoreListBean();
					ObjectNode enObj = (ObjectNode)(id2Obj.get(id2K));
					ArrayNode array = (ArrayNode)(enObj.get("list"));
					for (int i = 0; i < array.size(); i++) {
						ObjectNode o = (ObjectNode)(array.get(i));
						long id = o.get("i").asLong();
						int num = o.get("a").asInt();
						double score = o.get("s").asDouble();
						mz.add(new StoreBean(id, num, score), 1);
					}
					this.storeSim.put(Long.parseLong(id2K), mz);
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		StoreMaxNNModel model = new StoreMaxNNModel(null, null, "1");
		System.out
				.println(Math.log((int) (System.currentTimeMillis() - model.timeInit)));
	}
}

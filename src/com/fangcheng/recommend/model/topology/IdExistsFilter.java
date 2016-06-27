package com.fangcheng.recommend.model.topology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFilter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.db.MongoDb;
import com.db.MysqlConnection;
import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.bean.UserGraphBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.recommend.model.config.ConfigData;
import com.fangcheng.recommend.model.config.DbBean;
import com.fangcheng.recommend.model.onlineModel.DynamicClusterModel;

/**
 * 用于过滤 有问题的 id
 * @author Administrator
 *
 */
public class IdExistsFilter extends BaseFilter {
	 private static final Logger LOG =
	            LoggerFactory.getLogger(IdExistsFilter.class);
	 
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}
	/**
 	* 有效的mall
 	*/
	private HashSet<Long> setMall=new HashSet<Long>();
	/**
	 * 有效的brand
	 */
	private HashSet<Long> setBrand=new HashSet<Long>();
	@Override
	public void prepare(Map paramMap,
			TridentOperationContext paramTridentOperationContext) {
		// TODO Auto-generated method stub
		//获取数据集
		OnlineRecommendTopology.init();
		flushData();
	}
	public void flushData(){
		String sql=ConfigData.mallId;
		DbBean mysql1=Config.configBean.mysql;
		//System.out.println(mysql1.ip+":"+mysql1.port+":"+mysql1.database+":"+mysql1.user+":"+mysql1.pwd);
		MysqlConnection mysql=new MysqlConnection(mysql1.ip,mysql1.port,mysql1.database,mysql1.user,mysql1.pwd);
//		System.out.println(sql);
		ResultSet data =mysql.sqlSelect(sql).resultSet;
		try {
			while(data.next()){
				setMall.add(data.getInt(1)*1L);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sql=ConfigData.brandId;

		data =mysql.sqlSelect( sql).resultSet;
		try {
			while(data.next()){
				setBrand.add(data.getInt(1)*1L);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysql=null;
	}
	
	public class Timer extends Thread{
		
		public Timer(){};
		public void run(){
			try {
				//停2小时
				Thread.sleep(7200000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flushData();
		}
	}

	@Override
	public boolean isKeep(TridentTuple paramTridentTuple) {
		// TODO Auto-generated method stub
		UserGraphBean user = (UserGraphBean) paramTridentTuple
				.getValue(0);
		//如果用户不存在 则剔除  mall id 和 brand id 错误
		if(user.type==0){
			if(setMall.contains(user.id)&&setMall.contains(user.refId)){
				return true;
			}else{
				//如果不存在则过滤
				return false;
			}
		}else if(user.type==1){
			if(setMall.contains(user.id)&&setBrand.contains(user.refId)){
				return true;
			}else{
				//如果不存在则过滤
				return false;
			}
		}else if(user.type==2){
			if(setBrand.contains(user.id)&&setBrand.contains(user.refId)){
				return true;
			}else{
				//如果不存在则过滤
				return false;
			}
		}else if(user.type==3){
			if(setBrand.contains(user.id)&&setMall.contains(user.refId)){
				return true;
			}else{
				//如果不存在则过滤
				return false;
			}
		}
		return false;
	
	}

}

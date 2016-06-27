package com.fangcheng.recommend.model.topology;

import java.text.SimpleDateFormat;
import java.util.Map;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.db.MysqlConnection;
import com.fangcheng.plugin.base.HdfsFileGet;
import com.fangcheng.recommend.model.bean.UserGraphBean;
import com.fangcheng.recommend.model.config.Config;
import com.fangcheng.util.JsonUtil;

public class OutbreakDetector extends BaseFunction {

	private static final long serialVersionUID=1L;
	
	public static final int THRESHOLD=1000;
	public MysqlConnection mysql=null;
	public SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	
	public String sql="insert into fangcheng_data_team.RecommendClusterLogger(dateStr,cluster,city,category,userId,idS,idRefS,countNum) values(";
	public String sql2=")   on duplicate key update countNum=countNum+values(countNum)";
	
	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		OnlineRecommendTopology.init();
		mysql=new MysqlConnection(Config.configBean.mysql.ip,Config.configBean.mysql.port,
				Config.configBean.mysql.database,Config.configBean.mysql.user,Config.configBean.mysql.pwd);
	}
	@Override
	public void execute(TridentTuple paramTridentTuple,
			TridentCollector paramTridentCollector) {
		// TODO Auto-generated method stub
		Integer key=Integer.parseInt((String)paramTridentTuple.getValue(0));
		UserGraphBean userGraphBean=(UserGraphBean)paramTridentTuple.getValue(1);
		//insert into RecommendClusterLogger(dateStr,cluster,city,category,idS,idRefS,countNum)
		//values(now(),1,1,1,1,1,1)  on duplicate key update countNum=countNum+values(countNum)
		
	//	System.out.println("入库程序:"+key+":"+JsonUtil.getJsonStr(userGraphBean));
//		String ids=(String)paramTridentTuple.getValue(2);
		//存在延时 就不考虑相关的队列堆积延时问题使用当前时间
		mysql.sqlUpdate(sql+"now(),"+key+","+userGraphBean.city+","+userGraphBean.cu+",'"+userGraphBean.userId+"',"+userGraphBean.id+","+userGraphBean.refId+","+userGraphBean.count+sql2);
	//	System.out.println(sql+"now(),"+key+","+userGraphBean.city+","+userGraphBean.cu+",'"+userGraphBean.userId+"',"+userGraphBean.id+","+userGraphBean.refId+","+userGraphBean.count+sql2);
	}
	public static void main(String[] args) {
		MysqlConnection mysql=new MysqlConnection("iZ25qfnqo66Z",3306,"fcMysql","root","zjroot");
	}
}

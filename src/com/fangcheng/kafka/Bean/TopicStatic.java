package com.fangcheng.kafka.Bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.fangcheng.util.ShellExec;
import com.google.common.collect.ArrayListMultimap;


/**
 * 执行消息的静态类
 * @author Administrator
 * 本静态类可以存储在数据库中
 *
 */
public class TopicStatic {
	/**
	 * 统一的日志接口 会将对应的数据直接推送到hdfs中
	 */
	public static final String HDFS_LOG="HDFS_LOG";
	
	/**
	 * 所有日志信息
	 */
	public static final String DEBUGE_LOG="DEBUG_LOG";
	/**
	 * 错误日志信息
	 */
	public static final String ERROR_LOG="ERROR_LOG";
	
	//测试基本类
	public static final String TEST="test4";
	
	public static final String TEST2="test5";

	//点评相关
	/**
	 * 添加一个新的城市
	 */
	public static final String ADD_NEW_CITY_DIANPING="ADD_NEW_CITY_DIANPING";
	
	/**
	 * 提那几一个 新的品牌
	 */
	public static final String ADD_NEW_BRAND_DIANPING="ADD_NEW_BRAND_DIANPING";
	/**
	 * 添加一个点评的新的page
	 */
	public static final String ADD_NEW_PAGE="ADD_NEW_PAGE";
	
	/**
	 * 输入接口
	 * 新增商业体
	 */
	public static final String ADD_NEW_BUSINESS="ADD_NEW_BUSINESS";
	/**
	 * 输出接口
	 */
	public static final String ADD_NEW_BUSINESS_STATUS="ADD_NEW_BUSINESS_STATUS";
	/**
	 * 新增商业体对应的状态目录
	 */
	//public static final String ADD_NEW_BUSSINESS_INTER_STATUS="ADD_NEW_BUSSINESS_CONTROL_STATUS";
	
	public static final String ADD_NEW_BUSINESS_DP="ADD_NEW_BUSINESS_DP";
	public static final String ADD_NEW_BUSINESS_INDEX_BAIDU="ADD_NEW_BUSINESS_INDEX_BAIDU";
	public static final String ADD_NEW_BUSINESS_INDOOR_MAP="ADD_NEW_BUSINESS_INNER_MAP";
	public static final String ADD_NEW_BUSINESS_FANG="ADD_NEW_BUSINESS_FANG";
	public static final String ADD_NEW_BUSINESS_CTRIP="ADD_NEW_BUSINESS_CTRIP";
	public static final String ADD_NEW_BUSINESS_ANJUKE="ADD_NEW_BUSINESS_ANJUKE";
	public static final String ADD_NEW_BUSINESS_HOMELINK="ADD_NEW_BUSINESS_HOMELINK";
	public static final String ADD_NEW_BUSINESS_POI_TJ="ADD_NEW_BUSINESS_POI_TJ";
	//分析城市相关
	public static final String ADD_NEW_BUSINESS_ANALYZE="ADD_NEW_BUSSINES_ANALYZE";
	//etl相关
	/**
	 *添加一个城市对应的点评信息 etl
	 */
	public static final String ADD_NEW_CITY_DIANPING_ETL="ADD_NEW_CITY_DIANPING_ETL";
	/**
	 * 添加一个新的品牌 etl
	 */
	public static final String ADD_NEW_BRAND_DIANPING_ETL="ADD_NEW_BRAND_DIANPING_ETL";
	/**
	 * 添加一个 新的page etl
	 */
	public static final String ADD_NEW_PAGE_ETL="ADD_NEW_PAGE_ETL";
	
	
	//推荐
	public static final String GET_RECOMMEND_RESULT="GET_RECOMMEND_RESULT";
	/**
	 * 用户请求日志
	 */
	public static final String RE_USER_REQUEST_INFO="RE_USER_REQUEST_INFO";
	/**
	 * 用户 推荐请求地址
	 */
	public static final String RE_USER_ACTION_INFO="RE_USER_ACTION_INFO";
	/**
	 * 推荐服务 返回的数据集
	 */
	public static final String RE_RE_DATA_INFO="RE_RE_DATA_INFO";
	
	//nutch爬虫的reback任务
	//public static final String NUTCH_JOB_REBACK="NUTCH_JOB_REBACK";
	
	
	
	//其他消息任务
	/**
	 * 添加新的代理{ip:xxx,port:}  或者 ip:port
	 */
	public static final String ADD_NEW_PROXY="ADD_NEW_PROXY";
	
	
	//其他挖掘任务
	
	/**
	 * 讲相关配置信息更新到数据库中
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void updateToMysql() throws IllegalArgumentException, IllegalAccessException
	{
		MysqlConnection mysql=new MysqlConnection("192.168.1.4",3306,"fcMysql","root","zjroot");
		String table="KafkaTopicInfo";
		MysqlSelect selc=mysql.sqlSelect("show tables like '"+table+"'");
		try {
			if(selc.resultSet.next())
			{
				//如果存在则需要 删除并重写
				mysql.sqlUpdate("truncate table "+table);
			}else{
				System.out.println("请先创建 kafakaTopicInfo的表");
				System.exit(1);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Field[] fields=TopicStatic.class.getDeclaredFields();
		for (Field f : fields) {
              
                String sql="insert into "+ table+"(kafkaTopic,javaName,UpdateDate) values(\""+f.get(f.getName()).toString()
                		+"\",\""+f.getName()+"\",now()) on duplicate key update kafkaTopic=values(kafkaTopic),javaName=values(javaName),"
        				+ "UpdateDate=values(UpdateDate)";
                System.out.println(sql);
                mysql.sqlUpdate(sql);
           //需要创建 kafka对应的topic信息
                //查询是否已经存在
                ArrayList<String> list=ShellExec.execShellAndGet("/home/hduser/soft/kafka/bin/kafka-topics.sh --describe --zookeeper 192.168.1.64:2181 --topic "+f.get(f.getName()));
                if(list.size()<=0)
                {
                	System.out.println(f.get(f.getName())+"\t無");
                	ShellExec.execShell("/home/hduser/soft/kafka/bin/kafka-topics.sh --create --zookeeper 192.168.1.64:2181 --replication-factor 1 --partitions 1  --topic "+f.get(f.getName()));
                }else{
                	System.out.println(f.get(f.getName())+"\tyou");
                	for(String str:list)
                	{
                		System.out.println(str);
                	}
                }
		}
		
	}
	
	/**
	 * 讲相关配置信息更新到数据库中 及 kafka
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void deleteAllTopic() throws IllegalArgumentException, IllegalAccessException
	{
		MysqlConnection mysql=new MysqlConnection("192.168.1.4",3306,"fcMysql","root","zjroot");
		String table="KafkaTopicInfo";
		MysqlSelect selc=mysql.sqlSelect("show tables like '"+table+"'");
		try {
			if(selc.resultSet.next())
			{
				//如果存在则需要 删除并重写
				mysql.sqlUpdate("truncate table "+table);
			}else{
				System.out.println("请先创建 kafakaTopicInfo的表");
				System.exit(1);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Field[] fields=TopicStatic.class.getDeclaredFields();
		for (Field f : fields) {
              
                String sql="delete from "+ table+" where kafkaTopic='"+f.get(f.getName()).toString()+"'";
                System.out.println(sql);
                mysql.sqlUpdate(sql);
           //需要创建 kafka对应的topic信息
                //查询是否已经存在
                ArrayList<String> list=ShellExec.execShellAndGet("/home/hduser/soft/kafka/bin/kafka-topics.sh --describe --zookeeper 192.168.1.64:2181 --topic "+f.get(f.getName()));
                if(list.size()>=0)
                {
                	System.out.println(f.get(f.getName())+"\t無");
                	ShellExec.execShell("/home/hduser/soft/kafka/bin/kafka-topics.sh --delete --zookeeper 192.168.1.64:2181 --topic "+f.get(f.getName()));
                }
                
		}
		
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		TopicStatic.updateToMysql();
		//TopicStatic.deleteAllTopic();
	}
}

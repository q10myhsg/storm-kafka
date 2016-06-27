package com.fangcheng.restart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.fangcheng.kafka.Bean.MysqlStatic;


/**
 * 消息队列的任务信息重新提交
 * @author Administrator
 *
 */
public class Restart {

	/**
	 * 获取所有被提交 及执行中的程序
	 * @param kafkaTopic
	 * @return
	 */
	public static String getSql(String kafkaTopic)
	{
		return "select inputData from KafkaInfoQueue where  statusInt=11 or statusInt=2  and jobName=\""+kafkaTopic+"\"";
	}
	
	public static ArrayList<String> readData(String kafkaTopic,MysqlConnection mysql) throws SQLException
	{
		MysqlSelect select=mysql.sqlSelect(getSql(kafkaTopic));
		if(select ==null)
		{
			return null;
		}
		ResultSet set=select.resultSet;
		if(set==null)
		{
			return null;
		}
		ArrayList<String> list=new ArrayList<String>();
		while(set.next())
		{
			list.add(set.getString(1));
		}
		return list;
	}

	public static void main(String[] args) {
		MysqlConnection mysql = new MysqlConnection("192.168.1.4",
				MysqlStatic.mysqlPort, MysqlStatic.database, MysqlStatic.user,
				MysqlStatic.pwd);
		try {
			ArrayList<String> list=Restart.readData("ADD_NEW_BUSINESS_POI_TJ", mysql);
			for(int i=0;i<list.size();i++)
			{
				System.out.println(list.get(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

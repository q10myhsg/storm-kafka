package com.fangcheng.kafka.Bean;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;

import com.db.MysqlConnection;
import com.db.MysqlSelect;

/**
 * 执行状态的静态类
 * @author Administrator
 *  本静态类可以存储在数据库中
 *
 */
public class StatusStatic {

	/**
	 * 撤销失败
	 */
	public static final int REVOKE_ERROR=-5;
	/**
	 * 撤销成功
	 */
	public static final int REVOKE_SUCCESS=-4;
	/**
	 * 撤销执行中
	 */
	public static final int REVOKE_EXEC_ING=-3;
	/**
	 * 撤销被提交
	 */
	public static final int REVOKE_COMMIT=-2;
	/**
	 * 撤销
	 */
	public static final int REVOKE=-1;
	/**
	 * 失败
	 */
	public static final int ERROR=0;
	/**
	 * 成功
	 */
	public static final int SUCCESS=1;
	/**
	 * 提交成功
	 */
	public static final int COMMIT_SUCCESS=2;
	/**
	 * 提交失败
	 */
	public static final int COMMIT_ERROR=3;
	/**
	 * 提交冲突
	 * 重复提交,流程错误
	 */
	public static final int COMMINT_CONFLICT=4;
	/**
	 * 创建id异常
	 */
	public static final int COMMINT_ERROR_CREATE_ID_EXCEPTION=5;
	/**
	 * 执行中
	 */
	public static final int EXEC_ING=11;
	/**
	 * 执行等待中
	 */
	public static final int EXEC_WAIT=12;

	/**
	 * 拖延
	 */
	public static final int EXEC_DELAY=15;
	
	/**
	 * 近期已被执行 无需执行
	 */
	public static final int EXEC_HAS_BEEN=16;
	
	/**
	 * 执行完成并成功
	 */
	public static final int EXEC_SUCCESS=13;
	
	/**
	 * 执行结束单过程失败
	 */
	public static final int EXEC_ERROR=14;
	
	
	public static final int SUCCESS_SON_COMMIT=23;
	
	public static final int SUCCESS_SON_EXEC_ING=24;
	
	
	public static HashMap<Integer,String> mapName=null;
	/**
	 * 判断任务是否已经结束
	 * @param status
	 * @return
	 */
	public static boolean  isEnd(int status)
	{
		if(status==SUCCESS||status==ERROR ||status==REVOKE)
		{
			return true;
		}
		return false;
	}

	/**
	 * 静态初始化方法
	 */
	static
	{
		mapName=new HashMap<Integer,String>();
		Field[] fields=StatusStatic.class.getDeclaredFields();
		for (Field f : fields) {
	         if(f.getName().equals("mapName"))
             {
           	  continue;
             }
                String status=f.getName();
                String val=null;
                try {
					val=f.get(f.getName()).toString();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               //System.out.println(status+"\t"+val);
               mapName.put(Integer.parseInt(val),status);
		}
	}
	
	/**
	 * 讲相关配置信息更新到数据库中
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void updateToMysql() throws IllegalArgumentException, IllegalAccessException
	{
		MysqlConnection mysql=new MysqlConnection("192.168.1.4",3306,"fcMysql","root","zjroot");
		String table="KafkaTopicStatusInfo";
		MysqlSelect selc=mysql.sqlSelect("show tables like '"+table+"'");
		try {
			if(selc.resultSet.next())
			{
				//如果存在则需要 删除并重写
				mysql.sqlUpdate("truncate table "+table);
			}else{
				System.out.println("请先创建 KafkaTopicStatusInfo的表");
				System.exit(1);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Field[] fields=StatusStatic.class.getDeclaredFields();
		for (Field f : fields) {
              if(f.getName().equals("mapName"))
              {
            	  continue;
              }
                String sql="insert into "+ table+"(status,statusInt,UpdateDate) values(\""+f.getName()
                		+"\","+f.get(f.getName()).toString()+",now()) on duplicate key update status=values(status),statusInt=values(statusInt),"
        				+ "UpdateDate=values(UpdateDate)";
                System.out.println(sql);
                mysql.sqlUpdate(sql);
		}
		
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		StatusStatic.updateToMysql();
	//	System.out.println(StatusStatic.mapName.get(StatusStatic.ERROR));
	}
}

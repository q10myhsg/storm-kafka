package com.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.StatusStatic;
import com.fangcheng.util.JsonUtil;

public class StringFormat {

	/**
	 * 时间的格式化
	 */
	public static	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
	/**
	 * 获取基本的插入 KafaInfoQueue表的string
	 * @param bean
	 * @param comment
	 * @return
	 */
	public static String getInsertString(KafkaTransmitBean bean,String comment,String kafkaTransmitBeanToString,boolean useInputData)
	{
		Date startTime=new Date(System.currentTimeMillis());
		//System.out.println("insert into KafkaInfoQueue(jobName,status,statusInt,taskDeep,comment,startTime) values('"+bean.topic+"','"+StatusStatic.mapName.get(bean.status.execStatus)+"',"+bean.status.execStatus+","+bean.taskDeep+",'"+comment+"',str_to_date('"+formatter.format(startTime)+"','%Y-%m-%d %H:%i:%s'))");
		if(bean.isNutchBack())
		{
			String temp=JsonUtil.getJsonStr(bean).replaceAll("'","\\\\'");
			return "insert into KafkaInfoQueue(jobName,status,statusInt,taskDeep,comment,inputData,startTime,endTime) values('"+bean.topic+"','"+StatusStatic.mapName.get(bean.status.execStatus)+"',"+bean.status.execStatus+","+bean.taskDeep+",'"+comment+"','"+temp+"',str_to_date('"+formatter.format(startTime)+"','%Y-%m-%d %H:%i:%s'),str_to_date('"+formatter.format(startTime)+"','%Y-%m-%d %H:%i:%s'))";
		}
		else if(kafkaTransmitBeanToString==null)
		{
			return "insert into KafkaInfoQueue(jobName,status,statusInt,taskDeep,comment,startTime) values('"+bean.topic+"','"+StatusStatic.mapName.get(bean.status.execStatus)+"',"+bean.status.execStatus+","+bean.taskDeep+",'"+comment+"',str_to_date('"+formatter.format(startTime)+"','%Y-%m-%d %H:%i:%s'))";
		}else{
			if(useInputData)
			{
				String temp=JsonUtil.getJsonStr(bean).replaceAll("'","\\\\'");
				return "insert into KafkaInfoQueue(jobName,status,statusInt,taskDeep,comment,inputData,startTime) values('"+bean.topic+"','"+StatusStatic.mapName.get(bean.status.execStatus)+"',"+bean.status.execStatus+","+bean.taskDeep+",'"+comment+"','"+temp+"',str_to_date('"+formatter.format(startTime)+"','%Y-%m-%d %H:%i:%s'))";
			}else{
				return "insert into KafkaInfoQueue(jobName,status,statusInt,taskDeep,comment,startTime) values('"+bean.topic+"','"+StatusStatic.mapName.get(bean.status.execStatus)+"',"+bean.status.execStatus+","+bean.taskDeep+",'"+comment+"',str_to_date('"+formatter.format(startTime)+"','%Y-%m-%d %H:%i:%s'))";
			}
		}
	}
	public static String getUpdateString(long jobId,KafkaTransmitBean bean)
	{
		return "update KafkaInfoQueue set inputData='"+JsonUtil.getJsonStr(bean).replaceAll("'","\\\\'")+"' where id="+jobId;
	}
	
	
	/**
	 * 更新 任务器状态
	 * @param bean
	 * @return
	 */
	public static String getUpdateString(KafkaTransmitBean bean)
	{
//		System.out.println("update KafkaInfoQueue set statusInt="+bean.status.execStatus+" where ID="+bean.jobControlId);
		if(bean.execComment==null)
		{
			return "update KafkaInfoQueue set statusInt="+bean.status.execStatus+",status='"+StatusStatic.mapName.get(bean.status.execStatus)+"',updateDate=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+bean.jobControlId;
		}else{
			return "update KafkaInfoQueue set statusInt="+bean.status.execStatus+",status='"+StatusStatic.mapName.get(bean.status.execStatus)+"',execComment=\""+bean.execComment+"\",updateDate=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+bean.jobControlId;
		}
	}
	
	
	
	public static String getUpdateEndTimeString(KafkaTransmitBean bean)
	{
		if(bean.execComment==null)
		{
			return "update KafkaInfoQueue set statusInt="+bean.status.execStatus+",status='"+StatusStatic.mapName.get(bean.status.execStatus)+"',endTime=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+bean.jobControlId;
		}else{
			return "update KafkaInfoQueue set statusInt="+bean.status.execStatus+",execComment=\""+bean.execComment+"\",status='"+StatusStatic.mapName.get(bean.status.execStatus)+"',endTime=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+bean.jobControlId;
		}
	}
	
	/**
	 * 更新 任务器状态
	 * @param bean
	 * @return
	 */
	public static String getUpdateStringEndTime(long jobControlId,int execStatus,String execComment)
	{
//		System.out.println("update KafkaInfoQueue set statusInt="+bean.status.execStatus+" where ID="+bean.jobControlId);
	//	System.out.println("update KafkaInfoQueue set statusInt="+execStatus+" , endTime=date('"+formatter.format(new Date())+"') where ID="+jobControlId);
		if(execComment==null)
		{
			return "update KafkaInfoQueue set statusInt="+execStatus+",status='"+StatusStatic.mapName.get(execStatus)+"',endTime=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+jobControlId;
		}else{
			return "update KafkaInfoQueue set statusInt="+execStatus+",status='"+StatusStatic.mapName.get(execStatus)+"',endTime=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s'),execComment=\""+execComment+"\" where ID="+jobControlId;
		}
	}
	/**
	 * 撤销任务
	 * @param bean
	 * @param execStatus 执行状态
	 * @return
	 */
	public static String getRevokeString(int execStatus,long jobId)
	{
		return "update KafkaInfoQueue set statusInt="+execStatus+",status='"+StatusStatic.mapName.get(execStatus)+"',endTime=str_to_date('"+formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+jobId;
	}
	/**
	 * 获取 id的 孩子id
	 * @param parentId
	 * @return
	 */
	public static String getIdSonString(long parentId)
	{
		return "select a.jobIdSon,b.jobName from KafkaInfoQueueDesc as a,KafkaInfoQueue as b where b.ID=jobId and jobId="+ parentId;
	}

	/**
	 * 获取基本的插入 KafaInfoQueueDesc表的string
	 * @param bean
	 * @param comment
	 * @return
	 */
	public static String getInsertDescString(KafkaTransmitBean bean)
	{
		//System.out.println("insert into KafkaInfoQueueDesc(jobId,jobIdSon) values("+bean.jobControlParentId+","+bean.jobControlId+")");
		return "insert into KafkaInfoQueueDesc(jobId,jobIdSon) values("+bean.jobControlParentId+","+bean.jobControlId+")";
	}
	/**
	 * 获取job在数据库中的执行状态
	 * @param jobId
	 * @return
	 */
	public static String getFatherIdStatus(long jobId)
	{
		return "select b.statusInt,count(1) from KafkaInfoQueueDesc as a LEFT JOIN KafkaInfoQueue as b on a.jobIdSon=b.ID where jobId="+jobId+" group by b.statusInt";
	}
	/**
	 * 获取某一个jobId的状态
	 * @param jobId
	 * @return
	 */
	public static String getStatus(long jobId)
	{
		return "select statusInt from KafkaInfoQueue as a where a.ID="+jobId;
	}
	
	/**
	 * 获取某一个jobId的状态
	 * @param jobId
	 * @return
	 */
	public static String getSonToParentStatus(long jobId)
	{
		return "select b.statusInt from (select jobId,jobIdSon from KafkaInfoQueueDesc as c where c.jobIdSon="+jobId+") as a "
				+" LEFT JOIN KafkaInfoQueue as b on a.jobId=b.ID ";
	}
	/**
	 * 通过子类获取父类对应的所有状态
	 * @param jobId
	 * @return
	 */
	public static String getIdStatus(long jobSonId)
	{
//		System.out.println("select a.jobId,b.statusInt,count(1) from (select jobId,jobIdSon from KafkaInfoQueueDesc as c where c.jobIdSon="+jobSonId+") as a "
//				+" LEFT JOIN KafkaInfoQueue as b on a.jobIdSon=b.ID group by a.jobId,b.statusInt");
		return "select a.jobId,b.statusInt,count(1),comment from (select d.jobId,d.jobIdSon from KafkaInfoQueueDesc as c,KafkaInfoQueueDesc as d where c.jobId=d.jobId and c.jobIdSon="+jobSonId+") as a "
				+" LEFT JOIN KafkaInfoQueue as b on a.jobIdSon=b.ID group by a.jobId,b.statusInt";
		
	}
	public static void main(String[] args) {
		Date startTime=new Date(System.currentTimeMillis());
		System.out.println(formatter.format(startTime));
	}
}

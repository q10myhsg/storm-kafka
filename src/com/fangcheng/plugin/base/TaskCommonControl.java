package com.fangcheng.plugin.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import ch.qos.logback.core.status.Status;

import com.db.MysqlConnection;
import com.db.StringFormat;
import com.db.TaskMysqlStatusBean;
import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.StatusStatic;

public class TaskCommonControl {

	
	/**
	 * 控制器任务 调度
	 * @param kafkaBean
	 * @param topic 返回的信息  自己的topic 在结束后会恢复
	 */
	public static long execInfoTask(MysqlConnection mysql,KafkaUtil consumer,String controlString,KafkaTransmitBean kafkaBean,String topic,String kafakBeanToString)
	{
		String sourceTopic=kafkaBean.getTopic();
		//System.out.println();
		// 创建 mysql control 并获取control值
		//System.out.println("创建一次");
		kafkaBean.jobControlId = mysql.sqlUpdate(
				StringFormat.getInsertString(kafkaBean, controlString,kafakBeanToString,true),
				"KafkaInfoQueue");
		if(kafkaBean.jobControlId<=0L)
		{
			//如果为 创建 id异常 也就是 sql api 或者 数据库异常
			kafkaBean.status.execStatus=StatusStatic.COMMINT_ERROR_CREATE_ID_EXCEPTION;
			consumer.sentMsgs(kafkaBean);
			return kafkaBean.jobControlId;
		}
		kafkaBean.setTopic(topic);
		long parentId = kafkaBean.jobControlId;
		// 添加控制器状态kafka
		consumer.sentMsgs(kafkaBean);
		kafkaBean.setTopic(sourceTopic);
		return parentId;
	}
	/**
	 * 撤销任务
	 * @param kafkaBean
	 * @param topic
	 * @return
	 */
	public static void revokeInfoTask(MysqlConnection mysql,KafkaUtil consumer,KafkaTransmitBean kafkaBean,String topic)
	{
		HashMap<Long,String> result=new HashMap<Long,String>();
		ResultSet set=mysql.sqlSelect(StringFormat.getIdSonString(kafkaBean.jobControlId)).resultSet;
		try {
			while(set.next())
			{
				result.put(set.getLong(1),set.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result.size()>0)
		{
			//修改control任务状态为 提交
			mysql.sqlUpdate(StringFormat.getRevokeString(StatusStatic.REVOKE_COMMIT,kafkaBean.jobControlId));
			for(Entry<Long,String> id:result.entrySet())
			{
				//修改对应的子任务为提交状态
				mysql.sqlUpdate(StringFormat.getRevokeString(StatusStatic.REVOKE_COMMIT,id.getKey()));
				//提交对应的消息
				kafkaBean.jobControlId=id.getKey();
				kafkaBean.topic=id.getValue();
				consumer.sentMsgs(kafkaBean);
			}
			mysql.sqlUpdate(StringFormat.getRevokeString(StatusStatic.REVOKE_COMMIT,kafkaBean.jobControlId));
			//返回最终的执行状态
			kafkaBean.topic=topic;
			kafkaBean.status.execStatus=StatusStatic.REVOKE_COMMIT;
			consumer.sentMsgs(kafkaBean);
		}else{
			//因没有对应任务
			kafkaBean.topic=topic;
			//不需要提交
			kafkaBean.status.execStatus=StatusStatic.REVOKE_SUCCESS;
			consumer.sentMsgs(kafkaBean);
		}
	}
	/**
	 * 更新任务状态 并提交信息给relTopic
	 * @param mysql
	 * @param consumer
	 * @param kafkaBean
	 * @param relTopic
	 * @param jobControlId 对应的处理修改状态的id
	 * @param execStatus
	 */
	public static void updateInfoTask(MysqlConnection mysql,KafkaUtil consumer,KafkaTransmitBean kafkaBean,String relTopic,long jobControlId,int execStatus)
	{
		kafkaBean.status.execStatus = execStatus;
		kafkaBean.jobControlId = jobControlId;
		// 更新主控制器状态
		String updateSql=StringFormat.getUpdateString(kafkaBean);
		mysql.sqlUpdate(updateSql);
		//System.out.println(updateSql);
		// 发送给消息队列
		kafkaBean.setReBackTopic(kafkaBean.getTopic());
		// 统一的返回状态
		kafkaBean.setTopic(relTopic);
		consumer.sentMsgs(kafkaBean);
		kafkaBean.setTopic(kafkaBean.getReBackTopic());
		kafkaBean.setReBackTopic(null);
	}
	
	/**
	 * 修改 任务并判断parentid对应的任务是否结束
	 * @param mysql
	 * @param kafkaBean
	 * @param jobControlId 对应的处理修改状态的id
	 * @param parentId 对应需要统计的父类状态
	 * @param execStatus
	 * @return 是否执行结束
	 */
	public static TaskMysqlStatusBean updateInfoTaskAndQuery(MysqlConnection mysql,KafkaTransmitBean kafkaBean,long jobControlId,long parentId,int execStatus)
	{
		kafkaBean.status.execStatus = execStatus;
		kafkaBean.jobControlId = jobControlId;
		// 更新主控制器状态
		mysql.sqlUpdate(StringFormat.getUpdateString(kafkaBean));
		// 发送给消息队列
		// 统一的返回状态
		boolean flag=false;
		int status=0;
		if(execStatus!=StatusStatic.ERROR&&execStatus!=StatusStatic.SUCCESS)
		{
		}else{
			//更新状态
			ResultSet result=mysql.sqlSelect(StringFormat.getFatherIdStatus(parentId)).resultSet;
			try {
				while(result.next())
				{//判断是否结束
					int key=result.getInt(1);
					int count=result.getInt(2);
					if(key==StatusStatic.SUCCESS||key==StatusStatic.ERROR)
					{
						if(count<=0)
						{
							flag=false;
							break;
						}else{
							status=key;
							flag=true;
						}
					}else if(count>0){
						flag=false;
						break;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(flag)
		{
			//如果是 则修改对应状态
			StringFormat.getUpdateStringEndTime(parentId,status,null);
		}
		TaskMysqlStatusBean bean=new TaskMysqlStatusBean();
		bean.execStatus=status;
		bean.jobId=parentId;
		bean.isOk=flag;
		return bean;
	}
	
	/**
	 * 是否已经结束了 因为 任务交互时间过快导致
	 * @param mysql
	 * @param jobControlId 通过孩子 获取 父亲的状态
	 * @return
	 */
	public static boolean taskIsEnd(MysqlConnection mysql,long jobControlId)
	{
		int status=-1000;
		ResultSet result=mysql.sqlSelect(StringFormat.getSonToParentStatus(jobControlId)).resultSet;
		try {
			while(result.next())
			{//判断是否结束
				status=result.getInt(1);
				//System.out.println("父亲的状态:"+status);
			}
			result.close();
		}catch(Exception e)
		{
			e.printStackTrace();
			return true;
		}
		if(StatusStatic.isEnd(status))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 修改 任务并判断parentid对应的任务是否结束 只更新parent的状态
	 * @param mysql
	 * @param kafkaBean
	 * @param jobControlId 对应的处理修改状态的id
	 * @param parentId 对应需要统计的父类状态
	 * @param execStatus
	 * @return 是否执行结束
	 */
	public static TaskMysqlStatusBean updateInfoTaskAndQuery(MysqlConnection mysql,KafkaTransmitBean kafkaBean,long jobControlId)
	{
		// 发送给消息队列
		// 统一的返回状态
		boolean flag=false;
		long parentId=0L;
		int status=0;
		boolean isError=false;
		boolean isRevoke=false;
		boolean isTrue=false;
			//更新状态
			ResultSet result=mysql.sqlSelect(StringFormat.getIdStatus(jobControlId)).resultSet;
			try {
				while(result.next())
				{//判断是否结束
					parentId=result.getInt(1);
					int key=result.getInt(2);
					int count=result.getInt(3);
					//String comment=result.getString(4);
					//System.out.println("son:"+jobControlId+"\tparent:"+parentId+"\tstatus:"+key+"\tcount:"+count+"\t"+comment);
					//因为存在3中状态
					if(StatusStatic.isEnd(key))
					{
						if(count<=0)
						{
							flag=false;
							break;
						}else{
							if(key==StatusStatic.ERROR)
							{//是否失败
								isError=true;
							}
							if(key==StatusStatic.REVOKE)
							{
								//是否revoke
								isRevoke=true;
							}
							if(key==StatusStatic.SUCCESS)
							{
								isTrue=true;
							}
							status=key;
							flag=true;
						}
					}else if(count>0){
						flag=false;
						break;
					}
				}
				result.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(flag)
		{
			if(isError)
			{
				status=StatusStatic.ERROR;
			}else if(isRevoke){
				if(isTrue)
				{//判断是否revoke成功
					status=StatusStatic.REVOKE_EXEC_ING;
				}else{
					status=StatusStatic.REVOKE_SUCCESS;
				}
			}
			//如果是 则修改对应状态
			mysql.sqlUpdate(StringFormat.getUpdateStringEndTime(parentId,status,null));
		}else{
			//System.out.println("没有结束");
		}
		TaskMysqlStatusBean bean=new TaskMysqlStatusBean();
		bean.execStatus=status;
		bean.jobId=parentId;
		bean.isOk=flag;
		if(parentId==0L)
		{
			bean.comment="不存在子项对应的数据";
			bean.isOk=true;
		}
		
		return bean;
	}
	/**
	 * 控制器子任务调度
	 * @param kafkaBean
	 * @param jobType 描述任务的 类型
	 * @param topic
	 */
	public static void execInfoTaskSon(MysqlConnection mysql,KafkaUtil consumer,String controlString,String jobType,KafkaTransmitBean kafkaBean,String topic,String kafakBeanToString)
	{
		String nameTemp=kafkaBean.name;
		kafkaBean.setName(jobType);
		kafkaBean.setTopic(topic);
		kafkaBean.status.startTime = System.currentTimeMillis();
		String temp=StringFormat.getInsertString(kafkaBean, controlString,kafakBeanToString,false);
		//System.out.println("temp:"+temp);
		do{
		kafkaBean.jobControlId = mysql.sqlUpdate(temp,
				"KafkaInfoQueue");
		}while(kafkaBean.jobControlId<=0L);
		temp=StringFormat.getUpdateString(kafkaBean.jobControlId,kafkaBean);
		//System.out.println(temp);
		mysql.sqlUpdate(temp);
		consumer.sentMsgs(kafkaBean);
		temp=StringFormat.getInsertDescString(kafkaBean);
		//System.out.println("desc:"+temp);
		mysql.sqlUpdate(temp);
		kafkaBean.setName(nameTemp);
	}
	/**
	 * 控制器添加子任务 但是任务是从nutch中出来的 相对独立的程序
	 * @param mysql
	 * @param consumer
	 * @param controlString
	 * @param jobType
	 * @param kafkaBean
	 * @param topic
	 * @param kafakBeanToString
	 */
	public static void execInfoTaskNutchSon(MysqlConnection mysql,KafkaUtil consumer,KafkaTransmitBean kafkaBean,String kafakBeanToString)
	{
		//创建 一个 新的对象
		String nameTemp=kafkaBean.name;
		kafkaBean.setName(nameTemp);
		kafkaBean.setTopic(kafkaBean.reBackTopic);
		if(kafkaBean.status==null)
		{
			kafkaBean.status=new StatusBean();
		}
		//直接插入并修改为成功  该nutchjob不存在 父子类关系
		kafkaBean.status.execStatus=StatusStatic.SUCCESS;
		kafkaBean.status.startTime = System.currentTimeMillis();
		kafkaBean.status.endTime = System.currentTimeMillis();
		//插入的同事
		String temp=StringFormat.getInsertString(kafkaBean,"NUTCH_JOB",kafakBeanToString,true);
		//System.out.println("temp:"+temp);
		do{
		kafkaBean.jobControlId = mysql.sqlUpdate(temp,
				"KafkaInfoQueue");
		}while(kafkaBean.jobControlId<=0L);
		//发送消息回Nutch
	}
	
	/**
	 * 撤销 只提交消息
	 * @param kafkaBean
	 * @param topic
	 */
	public static void revokeInfoTaskSon(MysqlConnection mysql,KafkaUtil consumer,KafkaTransmitBean kafkaBean,String topic)
	{
		kafkaBean.setTopic(topic);
		kafkaBean.status.endTime=System.currentTimeMillis();
		consumer.sentMsgs(kafkaBean);
	}
	
	public static void main(String[] args) {
		MysqlConnection mysql= new MysqlConnection("192.168.1.4",
				MysqlStatic.mysqlPort, MysqlStatic.database, MysqlStatic.user,
				MysqlStatic.pwd);
		KafkaTransmitBean kafkaBean =new KafkaTransmitBean();
		TaskCommonControl.updateInfoTaskAndQuery(mysql,kafkaBean,20L);
	}
}

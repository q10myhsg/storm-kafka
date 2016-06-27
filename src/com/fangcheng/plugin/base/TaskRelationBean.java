package com.fangcheng.plugin.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.TopicStatic;

/**
 * 任务连接属性
 * 使用为第二阶段使用的方法
 * @author Administrator
 *
 */
public class TaskRelationBean  implements Serializable{

	/**
	 * 只要包含就会执行
	 * 如果任意的上级类包含则执行
	 * 优先级高于conditionJob
	 */
	public HashSet<String> containJob=null;
	/**
	 * 包含job对应的参数
	 * 不是包含关系 全等关系
	 */
	public HashMap<String,Object> containParams=null;
	/**
	 * JobStatic.DP
	 */
	public String jobName=null;
	/**
	 * TopicStatic.ADD_NEW_BUSSINESS_DP
	 */
	public String jobTopic=null;
	/**
	 * 必须包含这个参会执行 执行等级 高于 contain
	 * 多个条件  一个条件的多个要素
	 */
	public ArrayList<ArrayList<String>> conditionJob=null;
	/**
	 * 每一个条件对应的 参数 如果参数为空则 只需要满足 条件值即可
	 * object 支持 string boolean类型
	 * 并且为null 或者 和conditionjob一样长
	 *  不是包含关系 全等关系
	 */
	public ArrayList<HashMap<String,Object>> conditionParams=null;
	
	/**
	 * 返回使用的 topic
	 */
	public String rebackTopic=null;
	
	/**
	 * 判断是否存在
	 * 如果存在则一定需要创建
	 * @return 是否创建了
	 */
	public boolean judgeConditon(KafkaTransmitBean bean)
	{
		if(containJob==null)
		{
			return false;
		}
		if(!containJob.contains(bean.name))
		{
			return false;
		}
		//需要在数据库中创建
		return true;
	}
	/**
	 * 判断是否有
	 * 如果有则需要判断 在数据库中是否存在
	 * @return 是否创建了
	 */
	public boolean judgeConditionJob(KafkaTransmitBean bean)
	{
		//如果containJob存在则为
		if(containJob!=null)
		{
			return false;
		}
		if(conditionJob==null)
		{
			return true;
		}
		String str=bean.params.jobType;
		HashSet<String> m=new HashSet<String>();
		if(str==null)
		{
			return false;
		}else{
			String[] stList=str.split(",");
			for(String s:stList)
			{
				m.add(s);
			}
		}
		for(int i=0;i<conditionJob.size();i++)
		{
			//判断满足的规则
			for(String s:conditionJob.get(i))
			{
				if(!m.contains(s))
				{
					continue;
				}
			}
		  boolean flag=judgeParams(bean,i);
		  if(flag)
		  {
			  return true;
		  }
		}
		return false;
	}
	/**
	 * 判断参数 是否满足要求
	 * @param bean
	 * @return
	 */
	public boolean judgeParams(KafkaTransmitBean bean,int index)
	{
		if(bean.params==null)
		{
			if(conditionParams==null)
			{
				return true;
			}
			return false;
		}else{
			if(conditionParams==null)
			{
				return true;
			}else{
				boolean flag=false;
				HashMap<String,Object> map =conditionParams.get(index);
				{
					int count=map.size();
					int countL=0;
					//判断是否存在
					//条件参数了 用于过滤使用
					for(Entry<String,Object> entry:map.entrySet())
					{
						if(entry.getValue() instanceof String)
						{
							String temp=bean.params.requestData.get(entry.getKey()).asText();
							if(temp==null)
							{
								break;
							}else if(temp.equals((String)entry.getValue())){
								countL++;
								continue;
							}else{
								break;
							}
						}else if(entry.getValue() instanceof Boolean){
							
						}else{
							//如果格式错误返回false;
							return false;
						}
					}
					if(countL==count)
					{
						return true;
					}
				}
				return flag;
			}
		}
	}
	
	public HashSet<String> getContainJob() {
		return containJob;
	}
	public void setContainJob(HashSet<String> containJob) {
		this.containJob = containJob;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobTopic() {
		return jobTopic;
	}
	public void setJobTopic(String jobTopic) {
		this.jobTopic = jobTopic;
	}
	public ArrayList<ArrayList<String>> getConditionJob() {
		return conditionJob;
	}
	public void setConditionJob(ArrayList<ArrayList<String>> conditionJob) {
		this.conditionJob = conditionJob;
	}
	public ArrayList<HashMap<String, Object>> getConditionParams() {
		return conditionParams;
	}
	public void setConditionParams(
			ArrayList<HashMap<String, Object>> conditionParams) {
		this.conditionParams = conditionParams;
	}
	public String getRebackTopic() {
		return rebackTopic;
	}
	public void setRebackTopic(String rebackTopic) {
		this.rebackTopic = rebackTopic;
	}
	
	
}

package com.fangcheng.kafka.Bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 在kafka中传递的参数
 * @author Administrator
 *
 */
public class KafkaTransmitBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一的jobid
	 */
	public int jobId=0;
	/**
	 * 用于传递实际的 JobStatic
	 */
	public String name=null;
	/**
	 *	控制器父亲
	 */
	public long jobControlParentId=0L;
	/**
	 * 内部控制器id
	 */
	public long jobControlId=0L;
	/**
	 * 控制器sonid
	 */
	public long jobControlSonId=0L;
	/**
	 * 是否是直接从nutch返回的没有在mysql中初始化信息的方法
	 */
	public boolean nutchBack=false;
	/**
	 * kafkaBean 对应的topic值
	 * 如果topic 和对应的 kafka的名字不同，则为 子项目反馈回来的信息
	 */
	public String topic=null;
	/**
	 * 返回的topic
	 */
	public String reBackTopic=null;
	/**
	 * 是否为返回信息
	 */
	public boolean reback=false;
	/**
	 * 任务深度
	 */
	public int taskDeep=0;
	/**
	 * partition key
	 */
	public String partition="0";
	/**
	 * 其他参数
	 */
	public ParamsBean params=null;
	/**
	 * 备注 job备注信息
	 */
	public String comment=null;
	/**
	 * 执行备注 标注异常 信息或者希望传输信息
	 */
	public String execComment=null;
	/**
	 * 状态
	 */
	public StatusBean status=null;
	/**
	 * 父亲的名字
	 * 上一层调度器传递过来的controlString
	 */
	public String parentName=null;
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public StatusBean getStatus() {
		return status;
	}
	public void setStatus(StatusBean status) {
		this.status = status;
	}
	
	public String getPartition() {
		return partition;
	}
	public void setPartition(String partition) {
		this.partition = partition;
	}
	public long getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	public ParamsBean getParams() {
		return params;
	}
	public void setParams(ParamsBean params) {
		this.params = params;
	}
	public long getJobControlId() {
		return jobControlId;
	}
	public void setJobControlId(long jobControlId) {
		this.jobControlId = jobControlId;
	}
	public long getJobControlSonId() {
		return jobControlSonId;
	}
	public void setJobControlSonId(long jobControlSonId) {
		this.jobControlSonId = jobControlSonId;
	}
	public long getJobControlParentId() {
		return jobControlParentId;
	}
	public void setJobControlParentId(long jobControlParentId) {
		this.jobControlParentId = jobControlParentId;
	}
	public int getTaskDeep() {
		return taskDeep;
	}
	public void setTaskDeep(int taskDeep) {
		this.taskDeep = taskDeep;
	}
	public boolean isReback() {
		return reback;
	}
	public void setReback(boolean reback) {
		this.reback = reback;
	}
	public String getReBackTopic() {
		return reBackTopic;
	}
	public void setReBackTopic(String reBackTopic) {
		this.reBackTopic = reBackTopic;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isNutchBack() {
		return nutchBack;
	}
	public void setNutchBack(boolean nutchBack) {
		this.nutchBack = nutchBack;
	}
	public String getExecComment() {
		return execComment;
	}
	public void setExecComment(String execComment) {
		this.execComment = execComment;
	}
	
	
	
}

package com.fangcheng.kafka.Bean;

import java.io.Serializable;

/**
 * 执行状态基本类
 * @author Administrator
 *
 */
public class StatusBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 执行状态对应statusStatic中的状态
	 * 如果为-1 则为撤销任务
	 */
	public int execStatus=0;
	/**
	 * 执行描述
	 */
	public String comment=null;
	/**
	 * 延期时间 为毫秒
	 */
	public long delayTime=0;
	/**
	 * 启动时间
	 */
	public long startTime=0L;
	/**
	 * 更新时间
	 */
	public long updateTime=0L;
	/**
	 * 结束时间
	 */
	public long endTime=0L;
	
	public int getExecStatus() {
		return execStatus;
	}
	public void setExecStatus(int execStatus) {
		this.execStatus = execStatus;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public long getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	
}

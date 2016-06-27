package com.db;

/**
 * jobid在mysql对应的执行状态
 * @author Administrator
 *
 */
public class TaskMysqlStatusBean {

	/**
	 * jobId
	 */
	public long jobId=0L;
	/**
	 * 执行状态
	 */
	public int execStatus=0;
	/**
	 * 是否为成功
	 */
	public boolean isOk=false;
	/**
	 * 备注信息
	 */
	public String comment=null;
	
}

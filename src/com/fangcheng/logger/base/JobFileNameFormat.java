package com.fangcheng.logger.base;

import backtype.storm.task.TopologyContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.storm.hdfs.bolt.format.FileNameFormat;

import com.fangcheng.kafka.Bean.Config;

public class JobFileNameFormat implements FileNameFormat {
	private String componentId;
	private int taskId;
	private String path;
	private String prefix;
	private String extension;
	/**
	 * 后缀时间格式
	 */
	private String timeStamp;
	/**
	 * 时间的格式化
	 */
	public 	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public JobFileNameFormat() {
		this.path = "/storm";
		this.prefix = "";
		this.extension = ".txt";
	}

	public JobFileNameFormat withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public JobFileNameFormat withExtension(String extension) {
		this.extension = extension;
		return this;
	}

	public JobFileNameFormat withPath(String path) {
		this.path = path;
		return this;
	}

	public void prepare(Map conf, TopologyContext topologyContext) {
		this.componentId = topologyContext.getThisComponentId();
		this.taskId = topologyContext.getThisTaskId();
		// 获取jobname
		rotationTime();
	}

	/**
	 * 是否翻转
	 * 
	 * @param rotation
	 * @return
	 */
	public String getName(boolean rotation,String jobName) {
		// componentId=hdfs
		// rotation=bolt-3-0
		//System.out.println("是否旋转:"+rotation+"\t"+jobName);
		if (!rotation) {
			return this.prefix + "-" + jobName + "-" + timeStamp
					+ this.extension;
		} else {
			rotationTime();
			return this.prefix + "-" + jobName + "-" + timeStamp
					+ this.extension;
		}
	}

	/**
	 * 调整文件的日期格式
	 */
	public void rotationTime() {
		Date d = new Date();
		this.timeStamp = formatter.format(d);
	//	System.out.println("date:"+d+"\t"+timeStamp);
	}

	public String getPath() {
		return this.path;
	}

	// 无效
	@Override
	public String getName(long paramLong1, long paramLong2) {
		// TODO Auto-generated method stub
		return null;
	}
}
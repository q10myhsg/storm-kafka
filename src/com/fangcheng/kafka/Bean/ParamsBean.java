package com.fangcheng.kafka.Bean;

import java.io.Serializable;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 具体的参数bean
 * @author Administrator
 *
 */
public class ParamsBean implements Serializable{
	/**
	 * job名字
	 */
	public String jobType=null;
	/**
	 * 请求数据
	 */
	public ObjectNode requestData =null;
	/**
	 * 返回数据
	 */
	public ObjectNode responseData=null;
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public ObjectNode getRequestData() {
		return requestData;
	}
	public void setRequestData(ObjectNode requestData) {
		this.requestData = requestData;
	}
	public ObjectNode getResponseData() {
		return responseData;
	}
	public void setResponseData(ObjectNode responseData) {
		this.responseData = responseData;
	}
	
	
	
}

package com.fangcheng.kafka.Bean;

import java.io.Serializable;


/**
 * 日志转换类
 * @author Administrator
 *
 */
public class LoggerTransmitBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 使用的job名字
	 * 同意的job
	 */
	public static String useJobPath=null;
	
	/**
	 * 执行类地址
	 */
	public String infoClassPath=null;
	/**
	 * 输入的数据内容
	 */
	public String inputData=null;
	/**
	 * 需要打印的信息
	 */
	public String infoData=null;
	/**
	 * 执行时间
	 */
	public String execDate=null;
	/**
	 * 
	 * @param obj 执行类
	 * @param inputData 消息信息
	 * @param infoData 打印信息
	 */
	public LoggerTransmitBean(Class obj,String inputData,String infoData)
	{
		this.infoClassPath=obj.getSimpleName();
		this.inputData=inputData;
		this.infoData=infoData;
	}
	
	public String getUseJobPath() {
		return useJobPath;
	}
	public void setUseJobPath(String useJobPath) {
		this.useJobPath = useJobPath;
	}
	public String getInfoClassPath() {
		return infoClassPath;
	}
	public void setInfoClassPath(String infoClassPath) {
		this.infoClassPath = infoClassPath;
	}
	public String getInputData() {
		return inputData;
	}
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}
	public String getInfoData() {
		return infoData;
	}
	public void setInfoData(String infoData) {
		this.infoData = infoData;
	}
	public String getExecDate() {
		return execDate;
	}
	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}
}

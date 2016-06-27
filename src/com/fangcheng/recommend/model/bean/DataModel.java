package com.fangcheng.recommend.model.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DataModel {

	/**
	 * hdfs 存储地址
	 */
	public String modelFile = "/storm_apps/onlineRecommend/model/";
	/**
	 * 具体名字
	 */
	public String modelName = "DynamicClusterModel";
	/**
	 * 扩展名
	 */
	public String extendName = ".model";
	/**
	 * 名字的尾部编号
	 */
	public String modelNameTailName="";
	/**
	 * 
	 */
	public long timeUpdateModel=0L;
	/**
	 * 每30分钟作为一次延时
	 */
	public long timeDelay=1800000L;
	/**
	 * model 更新
	 */
	public abstract void updateModel();
	/**
	 * 模型写入
	 * @result 是否成功
	 */
	public abstract boolean write();
	/**
	 * 模型读取
	 * @result 是否成功
	 */
	public abstract boolean read();
	
	/**
	 * 每天一次
	 */
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 获取文件时间戳
	 * 
	 * @return
	 */
	public String getDataString() {
		return df.format(new Date());
	}
	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<100;i++){
			System.out.println(df.format(new Date()));
		}
	}
}

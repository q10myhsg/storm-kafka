package com.fangcheng.recommend.model.config;

public class DbBean {

	
	/**
	 * ip
	 */
	public String ip=null;
	/**
	 * 端口
	 */
	public int port;
	
	public String defaultDb="admin";
	/**
	 * 使用的数据库
	 */
	public String database=null;
	/**
	 * 用户
	 */
	public String user=null;
	/**
	 * 密码
	 */
	public String pwd=null;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getDefaultDb() {
		return defaultDb;
	}
	public void setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb;
	}
	
	
}

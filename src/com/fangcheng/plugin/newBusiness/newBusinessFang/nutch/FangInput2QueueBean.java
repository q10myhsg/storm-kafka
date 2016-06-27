package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.io.Serializable;

public class FangInput2QueueBean extends FangInputQueueBean implements Serializable,Comparable<FangInput2QueueBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 搜房code
	 */
	private long fangCode=0L;
	
	/**
	 * 房源的url
	 */
	private String fangSonSourceUrl="";
	
	/**
	 * 自己的url
	 */
	private String url="";
	/**
	 * 对应的location
	 */
	private LationLngLat location=null;
	
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LationLngLat getLocation() {
		return location;
	}

	public void setLocation(LationLngLat location) {
		this.location = location;
	}

	@Override
	public int compareTo(FangInput2QueueBean o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getFangSonSourceUrl() {
		return fangSonSourceUrl;
	}

	public void setFangSonSourceUrl(String fangSonSourceUrl) {
		this.fangSonSourceUrl = fangSonSourceUrl;
	}

	public long getFangCode() {
		return fangCode;
	}

	public void setFangCode(long fangCode) {
		this.fangCode = fangCode;
	}
	
}

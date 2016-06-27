package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.io.Serializable;

/**
 * 存储经纬度信息
 * @author Administrator
 *
 */
public class LationLngLat implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 经度
	 */
	private Double lng=0D;
	
	/**
	 * 纬度
	 */
	private Double lat=0D;
	public LationLngLat()
	{
		
	}
	public LationLngLat(Double lng, Double lat) {
		// TODO Auto-generated constructor stub
		this.lng=lng;
		this.lat=lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	
}

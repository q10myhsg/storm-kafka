package com.fangcheng.plugin.newBusiness.newPoi.crawler;

public class Boundary {	
	String type="";
	float lat=0f;
	float lng=0f;
	int radius=5000;
	
	public String getType() {
		return type;
	}
	public void setTradiusype(String type) {
		this.type = type;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "location="+lat+","+ lng+"&r="+radius;
	}
}

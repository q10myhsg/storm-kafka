package com.fangcheng.plugin.newBusiness.newPoi.Bean;

public class SourcePOI {
	
	private Baidu baidu;
	private int id;
	private String name;
	//private long dianpingid;
	private Tencent tencent;
	private String city;
	private int city_code;
	
	public int getCity_code() {
		return city_code;
	}
	public void setCity_code(int city_code) {
		this.city_code = city_code;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Baidu getBaidu() {
		return baidu;
	}
	public void setBaidu(Baidu baidu) {
		this.baidu = baidu;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public long getDianpingid() {
//		return dianpingid;
//	}
//	public void setDianpingid(long dianpingid) {
//		this.dianpingid = dianpingid;
//	}
	public Tencent getTencent() {
		return tencent;
	}
	public void setTencent(Tencent tencent) {
		this.tencent = tencent;
	}
}

package com.fangcheng.recommend.model.config;

import java.io.Serializable;

public class KeyValue implements Serializable{

	public String name;
	public double value;
	
	public KeyValue(String name,double value){
		this.name=name;
		this.value=value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	
}

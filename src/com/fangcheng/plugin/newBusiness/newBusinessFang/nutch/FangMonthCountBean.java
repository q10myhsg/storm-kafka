package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.io.Serializable;

/**
 * 搜房 时间对应数量
 * 月份
 * @author Administrator
 *
 */
public class FangMonthCountBean implements Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 月份
	 */
	private String month="";
	/**
	 * 钱
	 */
	private double money=0D;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	
	
}

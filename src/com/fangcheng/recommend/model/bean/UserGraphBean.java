package com.fangcheng.recommend.model.bean;

import java.io.Serializable;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
/**
 * 用户交流 实体
 * @author Administrator
 *
 */
public class UserGraphBean implements Serializable{

	/**
	 * 唯一id
	 */
	public String uuid=null;
	/**
	 * 用户id
	 */
	public String userId=null;
	/**
	 * 版本号
	 */
	public double version=0d;
	/**
	 * 类型
	 * 0 mall-mall
	 * 1 mall-brand
	 * 2 brand-brand
	 * 3 brand-mall
	 */
	public int type=0;
	/**
	 * id
	 */
	public long id=0l;
	/**
	 * 关联id
	 */
	public long refId=0l;
	/**
	 * 对应的位置
	 */
	public int index=0;
	/**
	 * 交流量
	 */
	public int num=0;
	/**
	 * 对应的 分类编号
	 */
	public int cu=0;
	/**
	 * 数量
	 */
	public int count=1;
	/**
	 * 城市
	 */
	public int city;
	/**
	 * 对推荐分类
	 * offline 1
	 * online 2
	 * person 3
	 */
	public int reCate;
	/**
	 * 唯一id
	 */
	public HashSet<Long> set=new HashSet<Long>();
	/**
	 * refId 唯一
	 */
	public HashSet<Long> setRef=new HashSet<Long>();
	
	
	public UserGraphBean(String userId,int reCate,int type,int city,int category,int index,long id,long refId,int num){
		this.userId=userId;
		this.reCate=reCate;
		this.index=index;
		this.city=city;
		this.type=type;
		this.id=id;
		this.refId=refId;
		this.cu=category;
		this.num=num;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getRefId() {
		return refId;
	}
	public void setRefId(long refId) {
		this.refId = refId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCu() {
		return cu;
	}
	public void setCu(int cu) {
		this.cu = cu;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}

	public HashSet<Long> getSet() {
		return set;
	}

	public void setSet(HashSet<Long> set) {
		this.set = set;
	}

	public HashSet<Long> getSetRef() {
		return setRef;
	}

	public void setSetRef(HashSet<Long> setRef) {
		this.setRef = setRef;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getReCate() {
		return reCate;
	}

	public void setReCate(int reCate) {
		this.reCate = reCate;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
	
	
}

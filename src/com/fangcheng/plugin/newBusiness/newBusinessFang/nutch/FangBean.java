package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fangcheng.plugin.newBusiness.newBusinessFang.nutch.FangInput2QueueBean;
import com.fangcheng.plugin.newBusiness.newBusinessFang.nutch.FangMonthCountBean;
import com.fangcheng.plugin.newBusiness.newBusinessFang.nutch.LationLngLat;

public class FangBean implements Serializable,Cloneable{
	
	/**
	 * 列表页写字楼的信息
	 */
	FangInput2QueueBean FangListc=null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 搜房对应的id
	 */
	private Long fangCode=0L;
	/**
	 * 浏览次数
	 */
	private int scanCount=0;
	/**
	 * 认证业主数
	 */
	private int approveOwner=0;
	/**
	 * logo地址
	 */
	private String logoUrl="";
	/**
	 * 所在区域
	 */
	private String area="";
	/**
	 * 写字楼名
	 */
	private String officeBuildingName="";
	
	/**
	 * 地址
	 */
	private String address="";
	/**
	 * cbd类型
	 */
	private String cbdCategory="";
	/**
	 * 出售中
	 */
	private int saleCount=0;
	/**
	 * 出租中
	 */
	private int hireCount=0;
	/**
	 * 物业费
	 */
	private String tenementFee="";
	/**
	 * 物业费类型  元/㎡*月
	 */
	private String tenementFeeCategory="";
	
	/**
	 * 竣工时间
	 * HHHH-mm
	 */
	private String completeTime="";

	/**
	 * 本月评估价
	 */
	private String currentMonthSalePrice="";
	/**
	 * 本月评估价类型
	 */
	private String currentMonthSalePriceCategory="";
	
	/**
	 * 處理過的租金價格
	 */
	private String currentMonthHirePriceEtl="";
	/**
	 * 本月出售评估价
	 */
	private String currentMonthHirePrice="";
	/**
	 * 本月出售评估价类型
	 */
	private String currentMonthHirePriceCategory="";
	/**
	 * 物业类型
	 */
	private String tenement="";
	/**
	 * 总层数
	 */
	private String layerCount="";
	/**
	 * 建筑面积
	 * 平方米
	 */
	private String buildArea="";
	/**
	 * 得房率 存在暂无资料
	 */
	private String  receiveRate="";
	/**
	 * 物业公司类型
	 */
	private String tenementCompany="";
	/**
	 * 停车位数量
	 */
	private String parkingCount="";
	/**
	 * 电梯数量
	 */
	private String elevatorCount="";
	/**
	 * 详情页链接rul
	 */
	private String disUrl="";
	
	/**
	 * 楼盘简介
	 */
	private String buildAbstruct="";
	
	/**
	 *  高层环线位置
	 */
	private String hightCycleStation="";
	/**
	 * 项目特色		
	 */
	private String projectFeather="";
	/**
	 * 建筑类型
	 */
	private String buildCategory="";
	/**
	 * 是否分隔
	 */
	private String isSplit="";
	/**
	 * 是否涉外
	 */
	private String isInvolveOut="";
	/**
	 * 空调
	 */
	private String airCondition="";
	/**
	 * 装修情况
	 */
	private String fitmentStatus="";
	/**
	 * 占地面积
	 */
	private String floorSpace="";
	/**
	 * 标准层面积
	 */
	private String standerSpace="";
	/**
	 * 开间面积
	 */
	private String openSpace="";
	/**
	 * 开发商
	 */
	private String developer="";
	/**
	 * 公交站 及公交
	 */
	private List<String> busAndStation=new LinkedList<String>();
	
	/**
	 * 外景图url
	 */
	private String outdoorImgUrl="";
	/**
	 * 交通图url
	 */
	private String trafficImgUrl="";
	/**
	 * 实景图url
	 */
	private String factImgUrl="";
	/**
	 * 平面图url
	 */
	private String plantImgUrl="";
	
	/**
	 * 经纬度信息
	 */
	private LationLngLat location=null;
	/**
	 * 地铁相关信息
	 */
	private List<String> subway=new LinkedList<String>();
	
	/**
	 * 租金 月对应值
	 */
	private List<FangMonthCountBean> hireTrendValueEtl=new LinkedList<FangMonthCountBean>();
	/**
	 * 租金 月对应值
	 */
	private List<FangMonthCountBean> hireTrendValue=new LinkedList<FangMonthCountBean>();
	/**
	 * 房价 月对应值
	 */
	private List<FangMonthCountBean> priceTrendValue=new LinkedList<FangMonthCountBean>();
	
	/**
	 * 月份
	 */
	private int month=Calendar.getInstance().get(Calendar.MONTH);
	/**
	 * 年
	 */
	private int year=Calendar.getInstance().get(Calendar.YEAR);
	/**
	 * 增量的租金
	 */
	private double hireValue=0d;
	/**
	 * 增量的房价
	 */
	private double priceValue=0d;
	/**
	 * 爬取时间
	 */
	private String crawlerTime=DateFormat.parse(new Date());
	
	public double getHireValue() {
		return hireValue;
	}
	public void setHireValue(double hireValue) {
		this.hireValue = hireValue;
	}
	public double getPriceValue() {
		return priceValue;
	}
	public void setPriceValue(double priceValue) {
		this.priceValue = priceValue;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public LationLngLat getLocation() {
		return location;
	}
	public void setLocation(LationLngLat location) {
		this.location = location;
	}
	public List<FangMonthCountBean> getHireTrendValue() {
		return hireTrendValue;
	}
	public void setHireTrendValue(List<FangMonthCountBean> hireTrendValue) {
		this.hireTrendValue = hireTrendValue;
	}
	public void addHireTrendValue(FangMonthCountBean hireTrendValue) {
		this.hireTrendValue.add(hireTrendValue);
	}
	public List<FangMonthCountBean> getPriceTrendValue() {
		return priceTrendValue;
	}
	public void setPriceTrendValue(List<FangMonthCountBean> priceTrendValue) {
		this.priceTrendValue = priceTrendValue;
	}
	public void addPriceTrendValue(FangMonthCountBean priceTrendValue) {
		this.priceTrendValue.add(priceTrendValue);
	}
	public FangInput2QueueBean getFangListc() {
		return FangListc;
	}
	public void setFangListc(FangInput2QueueBean fangListc) {
		FangListc = fangListc;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getOfficeBuildingName() {
		return officeBuildingName;
	}
	public void setOfficeBuildingName(String officeBuildingName) {
		this.officeBuildingName = officeBuildingName;
	}
	public String getAddress() {
		return address;
	}
	
	public String getElevatorCount() {
		return elevatorCount;
	}
	public void setElevatorCount(String elevatorCount) {
		this.elevatorCount = elevatorCount;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCbdCategory() {
		return cbdCategory;
	}
	public void setCbdCategory(String cbdCategory) {
		this.cbdCategory = cbdCategory;
	}
	
	public int getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}
	public int getHireCount() {
		return hireCount;
	}
	public void setHireCount(int hireCount) {
		this.hireCount = hireCount;
	}
	
	public String getTenementFee() {
		return tenementFee;
	}
	public void setTenementFee(String tenementFee) {
		this.tenementFee = tenementFee;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}

	public String getCurrentMonthSalePrice() {
		return currentMonthSalePrice;
	}
	public void setCurrentMonthSalePrice(String currentMonthSalePrice) {
		this.currentMonthSalePrice = currentMonthSalePrice;
	}
	public String getCurrentMonthHirePrice() {
		return currentMonthHirePrice;
	}
	public void setCurrentMonthHirePrice(String currentMonthHirePrice) {
		this.currentMonthHirePrice = currentMonthHirePrice;
	}
	public String getTenement() {
		return tenement;
	}
	public void setTenement(String tenement) {
		this.tenement = tenement;
	}
	public String getLayerCount() {
		return layerCount;
	}
	public void setLayerCount(String layerCount) {
		this.layerCount = layerCount;
	}
	public String getBuildArea() {
		return buildArea;
	}
	public void setBuildArea(String buildArea) {
		this.buildArea = buildArea;
	}
	public String getReceiveRate() {
		return receiveRate;
	}
	public void setReceiveRate(String receiveRate) {
		this.receiveRate = receiveRate;
	}
	public String getTenementCompany() {
		return tenementCompany;
	}
	public void setTenementCompany(String tenementCompany) {
		this.tenementCompany = tenementCompany;
	}
	public String getParkingCount() {
		return parkingCount;
	}
	public void setParkingCount(String parkingCount) {
		this.parkingCount = parkingCount;
	}
	public String getDisUrl() {
		return disUrl;
	}
	public void setDisUrl(String disUrl) {
		this.disUrl = disUrl;
	}
	public String getBuildAbstruct() {
		return buildAbstruct;
	}
	public void setBuildAbstruct(String buildAbstruct) {
		this.buildAbstruct = buildAbstruct;
	}
	public String getHightCycleStation() {
		return hightCycleStation;
	}
	public void setHightCycleStation(String hightCycleStation) {
		this.hightCycleStation = hightCycleStation;
	}
	public String getProjectFeather() {
		return projectFeather;
	}
	public void setProjectFeather(String projectFeather) {
		this.projectFeather = projectFeather;
	}
	public String getBuildCategory() {
		return buildCategory;
	}
	public void setBuildCategory(String buildCategory) {
		this.buildCategory = buildCategory;
	}
	public String getIsSplit() {
		return isSplit;
	}
	public void setIsSplit(String isSplit) {
		this.isSplit = isSplit;
	}
	public String getIsInvolveOut() {
		return isInvolveOut;
	}
	public void setIsInvolveOut(String isInvolveOut) {
		this.isInvolveOut = isInvolveOut;
	}
	public String getAirCondition() {
		return airCondition;
	}
	public void setAirCondition(String airCondition) {
		this.airCondition = airCondition;
	}
	public String getFitmentStatus() {
		return fitmentStatus;
	}
	public void setFitmentStatus(String fitmentStatus) {
		this.fitmentStatus = fitmentStatus;
	}
	public String getFloorSpace() {
		return floorSpace;
	}
	public void setFloorSpace(String floorSpace) {
		this.floorSpace = floorSpace;
	}
	public String getStanderSpace() {
		return standerSpace;
	}
	public void setStanderSpace(String standerSpace) {
		this.standerSpace = standerSpace;
	}
	public String getOpenSpace() {
		return openSpace;
	}
	public void setOpenSpace(String openSpace) {
		this.openSpace = openSpace;
	}
	public String getDeveloper() {
		return developer;
	}
	public void setDeveloper(String developer) {
		this.developer = developer;
	}
	public List<String> getBusAndStation() {
		return busAndStation;
	}
	public void setBusAndStation(List<String> busAndStation) {
		this.busAndStation = busAndStation;
	}
	public void addBusAndStation(String busAndStation) {
		this.busAndStation.add(busAndStation);
	}
	public List<String> getSubway() {
		return subway;
	}
	public void setSubway(List<String> subway) {
		this.subway = subway;
	}
	public void addSubway(String subway) {
		this.subway.add(subway);
	}
	public String getOutdoorImgUrl() {
		return outdoorImgUrl;
	}
	public void setOutdoorImgUrl(String outdoorImgUrl) {
		this.outdoorImgUrl = outdoorImgUrl;
	}
	public String getTrafficImgUrl() {
		return trafficImgUrl;
	}
	public void setTrafficImgUrl(String trafficImgUrl) {
		this.trafficImgUrl = trafficImgUrl;
	}
	public String getFactImgUrl() {
		return factImgUrl;
	}
	public void setFactImgUrl(String factImgUrl) {
		this.factImgUrl = factImgUrl;
	}
	public String getPlantImgUrl() {
		return plantImgUrl;
	}
	public void setPlantImgUrl(String plantImgUrl) {
		this.plantImgUrl = plantImgUrl;
	}
	
	public Long getFangCode() {
		return fangCode;
	}
	public void setFangCode(Long fangCode) {
		this.fangCode = fangCode;
	}
	public int getScanCount() {
		return scanCount;
	}
	public void setScanCount(int scanCount) {
		this.scanCount = scanCount;
	}
	public int getApproveOwner() {
		return approveOwner;
	}
	public void setApproveOwner(int approveOwner) {
		this.approveOwner = approveOwner;
	}
	public String getCurrentMonthSalePriceCategory() {
		return currentMonthSalePriceCategory;
	}
	public void setCurrentMonthSalePriceCategory(
			String currentMonthSalePriceCategory) {
		this.currentMonthSalePriceCategory = currentMonthSalePriceCategory;
	}
	public String getCurrentMonthHirePriceCategory() {
		return currentMonthHirePriceCategory;
	}
	public void setCurrentMonthHirePriceCategory(
			String currentMonthHirePriceCategory) {
		this.currentMonthHirePriceCategory = currentMonthHirePriceCategory;
	}
	public String getTenementFeeCategory() {
		return tenementFeeCategory;
	}
	public void setTenementFeeCategory(String tenementFeeCategory) {
		this.tenementFeeCategory = tenementFeeCategory;
	}
	public String getCrawlerTime() {
		return crawlerTime;
	}
	public void setCrawlerTime(String crawlerTime) {
		this.crawlerTime = crawlerTime;
	}
	public String getCurrentMonthHirePriceEtl() {
		return currentMonthHirePriceEtl;
	}
	public void setCurrentMonthHirePriceEtl(String currentMonthHirePriceEtl) {
		this.currentMonthHirePriceEtl = currentMonthHirePriceEtl;
	}
	public List<FangMonthCountBean> getHireTrendValueEtl() {
		return hireTrendValueEtl;
	}
	public void setHireTrendValueEtl(List<FangMonthCountBean> hireTrendValueEtl) {
		this.hireTrendValueEtl = hireTrendValueEtl;
	}
	
	
}

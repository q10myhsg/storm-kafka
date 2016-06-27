package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.io.Serializable;

/**
 * 搜房网对应的 解析页面信息
 * @author Administrator
 *
 */
public class FangInputQueueBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 对应写字楼url
	 */
	private String url="";
	/**
	 * 列表也logoUrl
	 */
	private String logoUrl="";
	/**
	 * 城市
	 */
	private String city="";
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
	 * 写字楼类型
	 */
	private String officeBuildingCategory="";
	
	/**
	 * 售价 
	 */
	private String salePrice="";
	/**
	 * 售价的类型 元/每平
	 */
	private String salePriceCategory="";
	/**
	 * 租金 
	 */
	private String hirePrice="";
	
	/**
	 * 租金类型 元/㎡*天
	 */
	private String hirePriceCategory="";
	/**
	 * 物业费
	 */
	private String tenementFee="";
	
	/**
	 * 物业费类型 元/㎡*月
	 */
	private String tenementFeeCategory="";
	/**
	 * 竣工时间
	 * HHHH-mm
	 */
	private String completeTime="";
	
	/**
	 * 地图url
	 */
	private String mapUrl="";
	/**
	 * 出售房源 数
	 */
	private int saleHouseSource=0;
	/**
	 * 出售房源 url
	 */
	private String saleHouseSourceUrl="";
	/**
	 * 出租房源数
	 */
	private int hireHouseSouce=0;
	/**
	 * 出租房源 url
	 */
	private String hireHouseSouceUrl=""; 
	/**
	 * 相册数
	 */
	private int photoAlbum=0;
	/**
	 * 相册url
	 */
	private String photoAlbumUrl="";
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCbdCategory() {
		return cbdCategory;
	}
	public void setCbdCategory(String cbdCategory) {
		this.cbdCategory = cbdCategory;
	}
	public String getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	public String getHirePrice() {
		return hirePrice;
	}
	public void setHirePrice(String hirePrice) {
		this.hirePrice = hirePrice;
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
	public String getMapUrl() {
		return mapUrl;
	}
	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}
	public int getSaleHouseSource() {
		return saleHouseSource;
	}
	public void setSaleHouseSource(int saleHouseSource) {
		this.saleHouseSource = saleHouseSource;
	}
	public String getSaleHouseSourceUrl() {
		return saleHouseSourceUrl;
	}
	public void setSaleHouseSourceUrl(String saleHouseSourceUrl) {
		this.saleHouseSourceUrl = saleHouseSourceUrl;
	}
	public int getHireHouseSouce() {
		return hireHouseSouce;
	}
	public void setHireHouseSouce(int hireHouseSouce) {
		this.hireHouseSouce = hireHouseSouce;
	}
	public String getHireHouseSouceUrl() {
		return hireHouseSouceUrl;
	}
	public void setHireHouseSouceUrl(String hireHouseSouceUrl) {
		this.hireHouseSouceUrl = hireHouseSouceUrl;
	}
	public int getPhotoAlbum() {
		return photoAlbum;
	}
	public void setPhotoAlbum(int photoAlbum) {
		this.photoAlbum = photoAlbum;
	}
	
	public String getPhotoAlbumUrl() {
		return photoAlbumUrl;
	}
	public void setPhotoAlbumUrl(String photoAlbumUrl) {
		this.photoAlbumUrl = photoAlbumUrl;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getOfficeBuildingCategory() {
		return officeBuildingCategory;
	}
	public void setOfficeBuildingCategory(String officeBuildingCategory) {
		this.officeBuildingCategory = officeBuildingCategory;
	}
	public String getSalePriceCategory() {
		return salePriceCategory;
	}
	public void setSalePriceCategory(String salePriceCategory) {
		this.salePriceCategory = salePriceCategory;
	}
	public String getHirePriceCategory() {
		return hirePriceCategory;
	}
	public void setHirePriceCategory(String hirePriceCategory) {
		this.hirePriceCategory = hirePriceCategory;
	}
	
	public String getTenementFeeCategory() {
		return tenementFeeCategory;
	}
	public void setTenementFeeCategory(String tenementFeeCategory) {
		this.tenementFeeCategory = tenementFeeCategory;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}

package com.fangcheng.plugin.newBusiness.newPoi.Bean;

public class Around_poi {
	
	private Double distance;
	private String address;
	private Location location;
	private int id;
	private String keyword;
	private String name;
	private int pageNumber;
	private SourcePOI sourcePOI;
	private String thrid_id;
	private String timestamp;
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SourcePOI getSourcePOI() {
		return sourcePOI;
	}
	public void setSourcePOI(SourcePOI sourcePOI) {
		this.sourcePOI = sourcePOI;
	}
	public String getThrid_id() {
		return thrid_id;
	}
	public void setThrid_id(String thrid_id) {
		this.thrid_id = thrid_id;
	}
}

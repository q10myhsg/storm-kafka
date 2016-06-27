package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import com.fangcheng.json.JSONException;
import com.fangcheng.json.JSONObject;

public class LngLat
{
	double lng;
	double lat;	
	String name;
	JSONObject json = new JSONObject();
	public LngLat(String str)
	{
		String[] arr =str.split(",");
		setLng(Double.parseDouble(arr[0].trim()));
		setLat(Double.parseDouble(arr[1].trim()));
	}
	
	public LngLat(JSONObject jo)
	{
		try {
			name = jo.getString("name");
			lng = jo.getDouble("lng");
			lat = jo.getDouble("lat");
			json=jo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public double getLng() {
		return lng;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
		try {
			json.put("lng", lng);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double getLat() {
		return lat;
	}	
	public void setLat(double lat) {
		this.lat = lat;
		try {
			json.put("lat", lat);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		return lng+ ","+lat;
	}
	public JSONObject getJSONObject() throws JSONException	
	{
		return json;		
	}
}
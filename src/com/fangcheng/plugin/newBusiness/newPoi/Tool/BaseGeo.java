package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import com.fangcheng.json.JSONException;
import com.fangcheng.json.JSONObject;

public class BaseGeo {
	
	public String url="";
	public String key=Common.getKey();
	public String getUrl() {
		return url;  
	}
	public void setUrl(String url) {

		this.url = url;
	}
	public String getKey() {
		return Common.getKey();
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String construct()
	{
		return "?ak="+getKey()+"&output=json";
	}
	
	public boolean isAvailable(JSONObject jo)
	{
		boolean ret = false;
		String status="status";
		try {
			if(jo.getInt(status)!=0)
			{
				return ret;
			}
//			else if(jo.getInt("total")>0)
//			{
				ret = true;
//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

}

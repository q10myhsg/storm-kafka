package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fangcheng.json.JSONArray;
import com.fangcheng.json.JSONException;
import com.fangcheng.json.JSONObject;

public class TransferPOI  extends BaseGeo{

/**
 * from	源坐标类型	
取值为如下：

1：GPS设备获取的角度坐标;

2：GPS获取的米制坐标、sogou地图所用坐标;

3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标

4：3中列表地图坐标对应的米制坐标

5：百度地图采用的经纬度坐标

6：百度地图采用的米制坐标
sourcePoi.put(s, value);
7：mapbar地图坐标;

8：51地图坐标
否	默认为1，即GPS设备获取的坐标
 * 
 * 
 * 
 * */
	
	//default
	public static final int GPS=1;	
	public static final int GPS_METER=2;
	public static final int MARS=3;
	public static final int MARS_METER=4;
	public static final int BAIDU=5;
	public static final int BAIDU_METER=6;
	public static final int MAPBAR=7;
	public static final int MAP51=8;

	
	int from =MARS;
	int to =BAIDU;
	
	public TransferPOI() {
		setUrl(Common.transferUrl);
	}
	
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	List<LngLat> lnglat= new LinkedList<LngLat>();

	public List<String>  excute() throws JSONException, ClientProtocolException, IOException, InterruptedException{
		List<String> ret=new LinkedList<String>();
		CloseableHttpClient httpclient = HttpClients.createDefault();		

		try {
			HttpGet httpget = new HttpGet(construct());		
			CloseableHttpResponse response = httpclient.execute(httpget);		
			HttpEntity e =response.getEntity();
			InputStream is =e.getContent();
			String s =StringUtil.inputStream2String(is);
			JSONObject jo =new JSONObject(s);
			if(isAvailable(jo))
			{
				JSONArray temp=jo.getJSONArray("result");				
				for(int i=0;i<temp.length();i++)
				{
					JSONObject j=temp.getJSONObject(i);
					JSONObject baiduPoi=new JSONObject();
					baiduPoi.put("lng", j.getDouble("x"));
					baiduPoi.put("lat", j.getDouble("y"));
					JSONObject sourcePoi=lnglat.get(i).getJSONObject();					
					JSONObject joo =  new JSONObject ();
					joo.put("source", sourcePoi);
					joo.put("baidu", baiduPoi);
					ret.add(joo.toString());
				}
			}
			response.close();
			httpget.completed();
			return ret;
				
		} finally {		    
		    httpclient.close();
		}		
//		return ret;
	}
	public List<Double>  excuteDouble() throws JSONException, ClientProtocolException, IOException, InterruptedException{
		List<Double> ret=new LinkedList<Double>();
		CloseableHttpClient httpclient = HttpClients.createDefault();		

		try {
			HttpGet httpget = new HttpGet(construct());		
			CloseableHttpResponse response = httpclient.execute(httpget);		
			HttpEntity e =response.getEntity();
			InputStream is =e.getContent();
			String s =StringUtil.inputStream2String(is);
			JSONObject jo =new JSONObject(s);
			if(isAvailable(jo))
			{
				JSONArray temp=jo.getJSONArray("result");				
				for(int i=0;i<temp.length();i++)
				{
					JSONObject j=temp.getJSONObject(i);
					JSONObject baiduPoi=new JSONObject();
					baiduPoi.put("lng", j.getDouble("x"));
					baiduPoi.put("lat", j.getDouble("y"));
					JSONObject sourcePoi=lnglat.get(i).getJSONObject();					
					JSONObject joo =  new JSONObject ();
					joo.put("source", sourcePoi);
					joo.put("baidu", baiduPoi);
					ret.add(j.getDouble("x"));
					ret.add(j.getDouble("y"));
					//ret.add(joo.toString());
				}
			}
			response.close();
			httpget.completed();
			return ret;
				
		} finally {		    
		    httpclient.close();
		}		
//		return ret;
	}
	public void addLngLat(String str)
	{
		lnglat.add(new LngLat(str));
	}
	
	public void addLngLat(LngLat ll)
	{
		if(ll.getLat()>10&&ll.getLng()>10)			
		{
			lnglat.add(ll);
		}		
	}
	
	public List<LngLat> getLnglat()
	{
		return lnglat;
	}
	
	public String construct()
	{
		String ret=super.construct();
		StringBuffer sb = new StringBuffer("&coords=");
		Iterator<LngLat> it = lnglat.iterator();
	
		for(int i=0;i<lnglat.size();i++)	
		{
			sb.append(it.next().toString());
			if(lnglat.size()-1!=i)
			{
				sb.append(";");
			}
		}
		
		sb.append("&from="+from);
		sb.append("&to="+to);
		ret+=sb;
		ret=url+ret;	
		System.out.println(ret);
		return ret;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TransferPOI tp = new TransferPOI();
		tp.addLngLat("114.21892734521,29.575429778924");
		try {
			List<String> l = tp.excute();
			Iterator<String> it = l.iterator();
			while(it.hasNext())
			{
				System.out.println(it.next());
			}
		} catch (JSONException | IOException
				| InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


package com.fangcheng.plugin.newBusiness.newPoi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;

import com.fangcheng.json.JSONArray;
import com.fangcheng.json.JSONException;
import com.fangcheng.json.JSONObject;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.BaseGeo;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.Common;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.MyFileReader;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.MyFileWriter;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.StringUtil;
import com.fangcheng.plugin.newBusiness.newPoi.Tool.TimePrint;
import com.fangcheng.plugin.newBusiness.newPoi.crawler.Boundary;
import com.fangcheng.plugin.newBusiness.newPoi.crawler.Nearby;

@SuppressWarnings("deprecation")
public class Search extends BaseGeo {
	String query = "";
	String oriQuery = "";
	boolean bool = true;
	public Search() {
		setUrl(Common.searchUrl);
	}

	public void setKeyword(String key) {
		oriQuery = key;
		query = URLEncoder.encode(key);
	}

	Boundary bd;
	String name;
	int page_size = Common.pageSize;
	int page_index = 0;
	JSONObject sourcePOI;

	public JSONObject getSourcePOI() {
		return sourcePOI;
	}

	public void setSourcePOI(JSONObject sourcePOI) {
		this.sourcePOI = sourcePOI;
	}

	public List<String> excute() throws JSONException, ClientProtocolException,IOException, InterruptedException {
		List<String> ret = new LinkedList<String>();
		//add by wenyang 20150906 for remove warning: * violates RFC 2109: host minus domain may not contain any dots		
//		CloseableHttpClient httpclient = HttpClients.createDefault();	
//		httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);		
		
		BasicCookieStore cookieStore = new BasicCookieStore();
		
		CookieSpecProvider easySpecProvider = new CookieSpecProvider() {  
			public CookieSpec create(HttpContext context) {
			return new BrowserCompatSpec() {  
			@Override  
			public void validate(Cookie cookie, CookieOrigin origin)  
			throws MalformedCookieException {  
				}  
			};  
			} 
			}; 
			
			Registry<CookieSpecProvider> r = RegistryBuilder  
					.<CookieSpecProvider> create()  
					.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())  
					.register(CookieSpecs.BROWSER_COMPATIBILITY,  
					new BrowserCompatSpecFactory())  
					.register("easy", easySpecProvider).build(); 
					  
					RequestConfig requestConfig = RequestConfig.custom()  
					.setCookieSpec("easy").setSocketTimeout(10000)  
					.setConnectTimeout(10000).build();
					  
					CloseableHttpClient httpclient = HttpClients.custom()  
					.setDefaultCookieSpecRegistry(r)  
					.setDefaultRequestConfig(requestConfig)  
					.setDefaultCookieStore(cookieStore)  
					.build();  
		
	
		bool = true;
		try {
			while (page_index >= 0 && bool) {
				ret.addAll(singleRequest(httpclient));
			}
			page_index = 0;
		} finally {
			httpclient.close();
		}
		return ret;
	}

	private List<String> singleRequest(CloseableHttpClient httpclient)throws ClientProtocolException, IOException, JSONException,InterruptedException {
		List<String> ret = new LinkedList<String>();
		HttpGet httpget = new HttpGet(construct());
		CloseableHttpResponse response = httpclient.execute(httpget);
		HttpEntity e = response.getEntity();
		InputStream is = e.getContent();
		String s = StringUtil.inputStream2String(is);
//		if(s.contains("天配额超限")){
//			ret.add(s);
//			response.close();
//			httpget.completed();
//			bool = false;
//			return ret;
//		}
		JSONObject jo = new JSONObject(s);
		int pageNumber = 0;
		// System.out.println(s);
		if (isAvailable(jo)) {
			JSONArray temp = jo.getJSONArray("results");
			if (page_size * (page_index + 1) < jo.getInt("total")) {
				page_index++;
				pageNumber = page_index;
			} else {
				pageNumber = page_index;
				page_index = -1;
			}
			for (int i = 0; i < temp.length(); i++) {
				JSONObject j = temp.getJSONObject(i);
				j.put("id", sourcePOI.getInt("id"));
				j.put("sourcePOI", sourcePOI);
				j.put("pageNumber", pageNumber);
				j.put("keyword", this.oriQuery);
				j.put("distance", Common.distance(sourcePOI.getJSONObject("baidu").getDouble("lng"),sourcePOI.getJSONObject("baidu").getDouble("lat"), j.getJSONObject("location").getDouble("lng"), j.getJSONObject("location").getDouble("lat")));
				j.put("timestamp", new TimePrint().getTime("yyyy-MM-dd",0));
				j.remove("uid");
				j.remove("street_id");
				j.remove("detail");
				ret.add(j.toString());
				// System.out.println(j.toString());
//				{
//		            "name":"日坛路",
//		            "location":{
//		                "lat":39.914282,
//		                "lng":116.449078
//		            },
//		            "address":"1路;9路;43路;99路;120路;126路;403路;639路;666路;668快;673路;804路;夜1路;夜24路",
//		            "street_id":"11504429319b901c5cbe86da",
//		            "detail":0,
//		            "uid":"11504429319b901c5cbe86da"
//		        }
			}
		}else{
			ret.add(s);
			bool = false;
		}
		response.close();
		httpget.completed();
		Thread.sleep(10);
		return ret;
	}

	public Boundary getBg() {
		return bd;
	}

	public void setBg(Boundary bg) {
		this.bd = bg;
	}

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

	public int getPage_index() {
		return page_index;
	}

	public void setPage_index(int page_index) {
		this.page_index = page_index;
	}

	public String getQuery() {
		return this.query;
	}

	@Override
	public String construct() {
		String keys = super.construct();
		String temp = "&" + bd.toString();
		temp += "&query=" + this.query;
		temp = url+ keys + temp + constructPage();
		//System.out.println(temp);
		return temp;
	}

	public String constructPage() {
		String ret = "&page_size=" + page_size + "&page_num=" + page_index;
		return ret;
	}

	/**
	 * 
	 * lng":116.43137140132,"lat":39.865405882696
	 * 
	 * */

	public static void main(String[] args) {
		MyFileReader mfr = new MyFileReader("resources/mallgeo_baidu.txt");
		MyFileReader mfrKeywords = new MyFileReader("resources/allkeywords.txt");
		Iterator<String> it = mfr.getContent().iterator();
		List<String> keywords = mfrKeywords.getContent();
		while (it.hasNext()) {
			String line = it.next();
			JSONObject jo;
			try {
				jo = new JSONObject(line);
				Iterator<String> itt = keywords.iterator();
				while (itt.hasNext()) {
					String category = itt.next();
					Nearby nb = new Nearby();
					Search s = new Search();
					nb.setLat(jo.getJSONObject("baidu").getFloat("lat"));
					nb.setLng(jo.getJSONObject("baidu").getFloat("lng"));
					nb.setRadius(5000);
					s.setSourcePOI(jo);
					s.setKeyword(category);
					s.setBg(nb);
					List<String> ja = s.excute();
					MyFileWriter mfw = new MyFileWriter(jo.getString("name") + "_" + category + ".txt");
					for (int i = 0; i < ja.size(); i++) {
						mfw.write(ja.get(i) + "\r\n");
					}
					mfw.close();
				}

			} catch (JSONException | IOException | InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}

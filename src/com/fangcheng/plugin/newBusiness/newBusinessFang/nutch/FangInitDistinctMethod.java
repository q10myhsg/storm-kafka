package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fangcheng.plugin.newBusiness.newBusinessFang.AntGetUrlProxy;

/**
 * 从页面解析出各个城市对应的url
 * @author root
 *
 */
public class FangInitDistinctMethod {

	public final static String fangUrl="office.fang.com/loupan/house/";
	public static String getLoopUrl(String url,int count)
	{
		String urlCode=null;
		int zn=0;
		while(true)
		{
			zn++;
		try {
			urlCode = AntGetUrlProxy.doGet(url, "gbk",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(zn>=count)
			{
				break;
			}
		}
		if(urlCode!=null&& !urlCode.equals(""))
		{
			break;
		}
		}
		return urlCode;
	}
	/**
	 * 为使用所有热门城市 其中不包含香港的
	 * @return
	 */
	public static HashSet<String> getAllUrl()
	{
		String urlCode=null;
		
		urlCode=getLoopUrl(fangUrl,5);
		
		HashSet<String> set=new HashSet<String>();
		if(urlCode==null)
		{
			return set;
		}
		HashMap<String,String> cityNameMapping=getCityHotPage(urlCode);
		for(Entry<String,String> cityMapping:cityNameMapping.entrySet())
		{
			if(cityMapping.getKey().equals("香港"))
			{
				continue;
			}
			System.out.println("使用城市:"+cityMapping.getKey());
			getPageList(cityMapping.getValue(),set);
		}
		return set;
	}
	/**
	 * 使用被选择的城市
	 * @param cityNameList
	 * @return
	 */
	public static HashSet<String> getAllUrl(List<String> cityNameList)
	{
		//获取全部城市对应
		String urlCode=null;
	urlCode=getLoopUrl(fangUrl,5);
		
		HashSet<String> set=new HashSet<String>();
		if(urlCode==null)
		{
			return set;
		}
		HashMap<String,String> cityNameMapping=getCityHotPage(urlCode);
		for(String cityName:cityNameList)
		{
			String cityUrl=cityNameMapping.get(cityName);
			if(cityUrl==null)
			{
				continue;
			}
			System.out.println("使用城市:"+cityName);
			getPageList(cityUrl,set);
		}
		return set;
	}
	/**
	 * 获取 fang天下中全部的城市对应的url
	 * @param fangUrlCode
	 * @return
	 */
	public static HashMap<String,String> getCityHotPage(String fangUrlCode)
	{
		HashMap<String,String> cityUrlMap=new HashMap<String,String>();
		Pattern p_1 = Pattern
				.compile("<div class=\"city[\\s\\S]*?</div>");
		Matcher m_1 = p_1.matcher(fangUrlCode);
		if(m_1.find())
		{
		//	System.out.println(m_1.group(0));
		Pattern p_ = Pattern
				.compile("<a[\\s]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>");
		Matcher m_ = p_.matcher(m_1.group(0));
		while (m_.find()) {
			String cityUrlTemp=m_.group(1).trim();
			String cityUrl = cityUrlTemp+"loupan/house/";
			String cityName = m_.group(2).trim();
			System.out.println(cityName+"\t"+cityUrl);
			cityUrlMap.put(cityName, cityUrl);
		}
		}
		return cityUrlMap;
	}
	/**
	 * 获取每个城市热门的写字楼全部页内容
	 * @param url
	 * @return
	 */
	public static void getPageList(String url,HashSet<String> set)
	{
		//获取最长页数其当前也内容
		//获取全部城市对应
				String urlCode=null;
				urlCode=getLoopUrl(url,5);
				if(urlCode==null)
				{
					return;
				}
				//解析頁面長度
				Document doc = Jsoup.parse(urlCode);
				String pageLength=doc.select(".fy_text").text();
				int pageCount=0;
				if(pageLength.contains("/"))
				{
					pageCount=Integer.parseInt(pageLength.substring(pageLength.indexOf("/")+1).trim());
				}else{
					//頁面不存在
					return;
				}
				if(pageCount==0)
				{
					return;
				}
				//執行 其他
				//獲取第一頁
				getPageFangUrl(doc,set);
				for(int i=2;i<pageCount;i++)
				{
					String urlNext=url+"i3"+i+"/";
					System.out.println("使用頁:"+urlNext);
					String urlCodeNext=null;
					urlCodeNext=getLoopUrl(urlNext,5);
					if(urlCode==null)
					{
						continue;
					}
					//解析頁面長度
					Document docNext = Jsoup.parse(urlCodeNext);
					getPageFangUrl(docNext,set);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}
	
	public static void getPageFangUrl(Document doc,HashSet<String> set)
	{
		Elements buildingListDoc=doc.select(".list.rel");
		for(int i=0;i<buildingListDoc.size();i++)
		{
			Element building=buildingListDoc.get(i);
			//建築的url
			String buildingUrl=building.getElementsByAttribute("title").select("a").attr("href");
			if(buildingUrl.contains("office-xm")||buildingUrl.contains("/2/")||buildingUrl.contains("/esf/"))
			{
				continue;
			}
			System.out.println(buildingUrl);
			set.add(buildingUrl);
		}
	}
	
	public static void main(String[] args) {
		FangInitDistinctMethod.getAllUrl();
	}
}

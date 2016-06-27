package com.fangcheng.parse.interator;


/**
 * parse 方法的通用接口
 * @author Administrator
 *
 */
public interface ParseCommonInter {

	/**
	 * 传入 url 及对应的解析页面 获取对应的信息
	 * @param url
	 * @param content
	 * @return
	 */
	public String parse(String url,String content);
	/**
	 * 传入 download的数据并解析
	 * @param content
	 * @return
	 */
	public String parseContent(String content);
	/**
	 * 传入url 并接卸处对应的数据
	 * @param url
	 * @return
	 */
	public String parseUrl(String url);
	
}

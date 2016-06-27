package com.fangcheng.plugin.newBusiness.newBusinessFang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class AntGetUrl {

	public static int limitTime=5000;
	/**
	 * 被gzip压缩过的网页获取方式
	 * 
	 * @param urlL
	 * @param code
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String doGetGzip(String urlLT, String code, boolean isProxy)
			throws Exception {
		// int lp = 0;
		StringBuilder sb = new StringBuilder();
		String urlL = "";
		//IPProxy proxy = null;
		if (urlLT.equals("")) {
			return "";
		} else if (urlLT.contains("http://")) {
			urlL = urlLT;
		} else {
			urlL = "http://" + urlLT;
		}
		try {
			// System.out.println(lp+"\t"+urlL);
			URL url = new URL(urlL);
			//proxy = setProxy(isProxy);
			URLConnection connection2 = url.openConnection();
			// HttpURLConnection httpUrlConnection = (HttpURLConnection)
			// connection2;
			HttpURLConnection connection = (HttpURLConnection) connection2;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(limitTime);
			connection.setReadTimeout(limitTime);

			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alwive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			InputStream is = connection.getInputStream();// url.openStream();
			InputStream in = null;
			try {
				in = new GZIPInputStream(is);
			} catch (Exception e) {
				in = is;
			}
			InputStreamReader isr = new InputStreamReader(in, code);
			char[] buffer = new char[1024];
			int pos = 0;
			sb = new StringBuilder();
			//releaseProxy(proxy, true);
			try {
				while ((pos = isr.read(buffer)) != -1) {
					sb.append(new String(buffer, 0, pos));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in.close();
			isr.close();
			is.close();
		}catch(IllegalArgumentException e)
		{
		//	releaseProxy(proxy, false);
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
		//	releaseProxy(proxy, false);
		//	log.error("异常url:" + urlLT);
			throw e;
		}
		catch (Exception e) {
		//	releaseProxy(proxy, false);
		//	log.error("异常url:" + urlLT);
		} finally {

		}
		return sb.toString();
	}


	/**
	 * 页面获取程序
	 * 
	 * @param url
	 *            页面地址
	 * @param unicode
	 *            页面编码
	 * @return 返回String
	 * @throws Exception
	 */
	public static String doGet(String url, String unicode) throws Exception {
		// 在此检测url是否为空，是否是合适的url的格式
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		int lp = 0;
		while (true) {
			try {
				if (url.equals("")) {
					return "";
				} else if (url.startsWith("http")) {
				} else {
					url = "http://" + url;
				}
				if (url.contains("http://www2.")) {
					return "";
				}
				URL realUrl = new URL(url);
				// System.out.println("url:"+url);
				// 打开和URL之间的连接
				URLConnection connection = realUrl.openConnection();
				HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
				// 设置通用的请求属性
				httpUrlConnection.setDoInput(true);
				httpUrlConnection.setDoOutput(true);
				httpUrlConnection.setUseCaches(false);
				httpUrlConnection.setConnectTimeout(limitTime);
				httpUrlConnection.setReadTimeout(limitTime);
				// 设置本次连接禁止重定向
				// httpUrlConnection.setInstanceFollowRedirects(false);

				httpUrlConnection.setRequestProperty("accept", "*/*");
				httpUrlConnection.setRequestProperty("connection",
						"Keep-Alwive");
				httpUrlConnection
						.setRequestProperty("user-agent",
								"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				httpUrlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
				// Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1);
				// Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1;
				// Trident/4.0; .NET CLR 2.0.50727)
				// 建立实际的连接

				httpUrlConnection.connect();
				// String redirectUrl =
				// connection.getHeaderField(HttpProtocolConstants.HEADER_LOCATION);
				// System.out.println("redirectUrl"+redirectUrl);
				// String redirectUrl =
				// httpUrlConnection.getHeaderField("Location");
				// System.out.println("redirectUrl"+redirectUrl);
				//
				// String code = new
				// Integer(httpUrlConnection.getResponseCode()).toString();
				//
				// String message = httpUrlConnection.getResponseMessage();
				//
				// System.out.println("getResponseCode code ="+ code);
				//
				// System.out.println("getResponseMessage message ="+
				// message);

				// 获取所有响应头字段
				// Map<String, List<String>> map =
				// connection.getHeaderFields();
				// String requestCookie="";
				// 遍历所有的响应头字段
				// if (null != map
				// && false == map.isEmpty())
				// {
				// for (Map.Entry<String, List<String>> entry :
				// map.entrySet())
				// {
				// String key = entry.getKey();
				// String value =
				// java.util.Arrays.toString(entry.getValue().toArray());
				// if (null != key
				// && "Set-Cookie".equals(key.trim()))
				// {
				// requestCookie = value;
				// requestCookie = requestCookie.replace("[", "");
				// requestCookie = requestCookie.replace("]", "");
				// }
				//
				// System.out.println(key + " : " + value);
				// }
				// }
				// 定义 BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), unicode));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}

				// 使用finally块来关闭输入流
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				break;
			}
			catch(IllegalArgumentException e)
			{
				return "";
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					lp++;
					if (lp >= 5) {
						throw e;
						// break;
					}
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		return result.toString();
	}

}

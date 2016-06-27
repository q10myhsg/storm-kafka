package com.fangcheng.recommend.model.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.fangcheng.util.FileUtil;

/**
 * 读取数据信息
 * 
 * @author Administrator
 *
 */
public class ConfigData {

	/**
	 * mall 的名字
	 */
	public static String mallIdToNameSql = null;
	/**
	 * brand 的名字
	 */
	public static String brandIdToNameSql = null;
	/**
	 * 分类对应的名字
	 */
	public static String categoryIdToNameSql = null;
	/**
	 * 城市对应的名字
	 */
	public static String cityIdToNameSql = null;
	/**
	 * 城市对应商圈数
	 */
	public static String areaSql = null;
	/**
	 * 最优的 推荐数据
	 */
	public static String bestSql = null;
	/**
	 * 最优mall
	 */
	public static String bestMallSql = null;
	/**
	 * 最优brand
	 */
	public static String bestBrandSql = null;
	/**
	 * 只有mallId
	 */
	public static String mallId = null;
	/**
	 * 只有brandId;
	 */
	public static String brandId = null;

	static {
		// mall recommand 数据
		// hdfs读取文件
		mallIdToNameSql = get(Config.path + "/mallIdToName.sql");
		brandIdToNameSql = get(Config.path + "/brandIdToName.sql");
		categoryIdToNameSql = get(Config.path + "/categoryIdToName.sql");
		cityIdToNameSql = get(Config.path + "/cityIdToName.sql");
		areaSql = get(Config.path + "/area.sql");
		bestSql = get(Config.path + "/best.sql");
		bestMallSql = get(Config.path + "/bestMall.sql");
		bestBrandSql = get(Config.path + "/bestBrand.sql");
		mallId = get(Config.path + "/mallId.sql");
		brandId = get(Config.path + "/brandId.sql");
	}

	public static String get(String configPath) {
		System.out.println("读取sql配置地址:"+configPath);
		if (configPath.contains("hdfs://")) {
			return getUrlFile(configPath, " ", "#");
		} else {
			FileUtil fileUtil = new FileUtil(configPath, "utf-8", false);
			return fileUtil.readAllAndClose2(" ", "#");
		}
	}

	/**
	 * line byte length 40960 start with # continue line
	 * 
	 * @param configPath
	 * @return
	 */
	public static String getUrlFile(String configPath, String split,
			String paramFilter) {
		StringBuffer list = new StringBuffer();
		if (configPath == null) {
			return list.toString();
		}

		while (true) {
			InputStream in = null;
			try {
				// String hdfsPathUrl="hdfs://master:9000/storm_plugin_conf/";
				URL url = new URL(configPath);
				in = url.openStream();
				byte[] b = new byte[40960];
				BufferedReader in2 = new BufferedReader(new InputStreamReader(
						in));
				String param = null;
				while ((param = in2.readLine()) != null) {

					param = param.trim();
					if (param.startsWith(paramFilter) || param.length() <= 0) {
						continue;
					}
					list.append(param + split);
				}

			} catch (FileNotFoundException fe) {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e) {

				}
				return list.toString();
			} catch (Exception e) {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {

				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				continue;
			}
			return list.toString();
		}
	}
}

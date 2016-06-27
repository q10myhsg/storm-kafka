package com.fangcheng.plugin.newBusiness.newBusinessFang.nutch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式化类
 * @author Administrator
 *
 */
public class DateFormat {
//	/private static Logger log = Logger.getLogger(DateFormat.class);
	
	public static SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");//"yyyy-MM-dd HH:mm:ss"
	
	
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	
	/**
	 * 对时间 做秒+运算
	 * @param date
	 * @param count 为 数量
	 * @param 时间格式
	 * @return
	 */
	public static Date addSecond(Date date,int count,int category)
	{
		 Calendar c = Calendar.getInstance();  
         c.setTime(date);  
//         c.add(c.YEAR, 1);//属性很多也有月等等，可以操作各种时间日期  
//         c.add(c.DATE, -1);  
        c.add(category,count);
         Date dateTemp = c.getTime();
         return dateTemp;
	}
	

	/**
	 * 将 yyyy-MM-dd HH:mm:ss 字符串转时间
	 * @param str
	 * @return
	 */
	public static Date strToDate(String str)
	{
		 Date date = null;
		   try {
		    date = format.parse(str);
		   } catch (ParseException e) {
		    e.printStackTrace();
		   }
		   return date;
	}
	
	
	/**
	 * 时间格式化为 年月日
	 * @param date
	 * @return
	 */
	public static String parse(Date date)
	{
		return df.format(date);
	}
	/**
	 * 
	 * @param str
	 */
	public static String transcateDate(String str)
	{
		return str.replaceAll("[年]","-").replaceAll("[月]","");
	}
	
	public static void main(String[] args) {
		Date date=new Date();
		System.out.println(DateFormat.parse(date));
		System.out.println(DateFormat.parse(date));
		System.out.println(date);
		System.out.println(DateFormat.addSecond(date,3600000,1));
	}
}

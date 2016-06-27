package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePrint {
	
	public Calendar cal = Calendar.getInstance() ;
	public SimpleDateFormat format ;
	
    //时间模板一定要按正常格式来编写(yyyy-MM-dd HH:mm:ss)MM和HH一定要大写
    public String getTime(String timemoban,int num){
    	format = new SimpleDateFormat(timemoban);
		if(timemoban.endsWith("yyyy")){
			cal.add(Calendar.YEAR, -num);
		}else if(timemoban.endsWith("MM")){
			cal.add(Calendar.MONTH, -num);
		}else if(timemoban.endsWith("HH")){
			cal.add(Calendar.HOUR_OF_DAY, -num);
		}else{
			cal.add(Calendar.DAY_OF_MONTH,-num);
		}
		
        return format.format(cal.getTime());
	}
    
}

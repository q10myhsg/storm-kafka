package com.fangcheng.plugin.newBusiness.newDianping;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.collections.map.HashedMap;

//map cat value to cat type
//北京餐厅  -> cat_1
public class Cat_Filter{
	private HashSet<String> cat_1s = new HashSet<String>();
	private HashSet<String> cat_2s = new HashSet<String>();
	private HashSet<String> districts = new HashSet<String>();
	private HashSet<String> businessareas = new HashSet<String>();
	private HashMap<String,String> config = null;
	
	public String filter(String term){
		if (districts.contains(term)){
			return "district";
		}else if (businessareas.contains(term)) {
			return "businessarea";
		}else if (cat_1s.contains(term)) {
			return "cat_1";
		}else if (cat_2s.contains(term)) {
			return "cat_2";
		}else {
			return "cat_3";
		}		
	}
	private void prepare() throws Exception{
		String driver = "com.mysql.jdbc.Driver";
//        String url = "jdbc:mysql://"+config.get("innerMysqlIp")+":3306/fcMysql";
		String url = "jdbc:mysql://192.168.1.4:3306/fcMysql";
        String user = "root"; 
        String password = "zjroot";
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement statement = conn.createStatement();
        
        String sql_district = "SELECT DISTINCT(dPBAArea) FROM fcMysql.BussinessArea WHERE dPCityId IN (1,2,4,5,7);";
        ResultSet rsd = statement.executeQuery(sql_district);
        while (rsd.next()) {
			districts.add(rsd.getString("dPBAArea"));
		}
        
        String sql_businessarea = "SELECT distinct(dPBA) FROM fcMysql.BussinessArea where dPCityId in (1,2,4,5,7) and dPBA is not null;";
        ResultSet rsb = statement.executeQuery(sql_businessarea);
        while (rsb.next()) {
			businessareas.add(rsb.getString("dPBA"));
		}
        
        String sql_cat1 = "SELECT distinct(concat(dPCityName,dPBACategory)) FROM fcMysql.BussinessCategory where dPCityId in (1,2,4,5,7);";
        ResultSet rsc1 = statement.executeQuery(sql_cat1);
        while (rsc1.next()) {
			cat_1s.add(rsc1.getString("(concat(dPCityName,dPBACategory))"));
		}
        HashSet<String> ms = new HashSet<String>(5);
        ms.add("北京美食");
        ms.add("上海美食");
        ms.add("广州美食");
        ms.add("深圳美食");
        ms.add("南京美食");
        
        HashSet<String> ct = new HashSet<String>(5);
        ct.add("北京餐厅");
        ct.add("上海餐厅");
        ct.add("广州餐厅");
        ct.add("深圳餐厅");
        ct.add("南京餐厅");
        //删除所有XX美食
        cat_1s.removeAll(ms);
        //加上所有XX餐厅
        cat_1s.addAll(ct);
        
        
        String sql_cat2 = "SELECT distinct(dPBA) FROM fcMysql.BussinessCategory where dPCityId in (1,2,4,5,7);";
        ResultSet rsc2 = statement.executeQuery(sql_cat2);
        while (rsc2.next()) {
			cat_2s.add(rsc2.getString("dPBA"));
		}
          
        
	}
	public Cat_Filter(HashMap<String,String> config) throws Exception{
		this.config=config;
		prepare();
	}


}

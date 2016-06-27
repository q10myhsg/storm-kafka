package com.db;

import com.fangcheng.util.JsonUtil;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class test {

	public static class test2{
		public String val="asdfasd";
		public String[] valList=new String[]{"a","b"};
		public test test=null;
		public test3 test3=null;
		public test2(){
			test3=new test3();
		}
	}
	
	public static class test3{
		public String val="asdfasd";
		public String[] valList=new String[]{"a","b"};
		public test test=null;
	}
	
	public static void main(String[] args) {
		//MysqlConnection mysql=new MysqlConnection("192.168.1.4",3306,"fcMysql","root","zjroot");
		//System.out.println(mysql.sqlUpdate("insert into KafkaInfoQueue(jobName) values('b') ","KafkaInfoQueue"));
		System.out.println(JsonUtil.getJsonStr(new test2()));
		System.out.println(JsonUtil.getJsonStr(JsonUtil.getDtoFromJsonObjStr(JsonUtil.getJsonStr(new test2()),test2.class)));
		
		
//		try  
//	    {  
//	      String str = "{\"data\":{\"birth_day\":7,\"birth_month\":6},\"errcode\":0,\"msg\":\"ok\",\"ret\":0}";  
//	  
//	      ObjectMapper mapper = new ObjectMapper();  
//	      JsonNode root = mapper.readTree(str);  
//	  
//	      JsonNode data = root.path("data");  
//	  
//	      JsonNode birth_day = data.path("birth_day");  
//	      System.out.println(birth_day.asInt());  
//	        
//	      JsonNode birth_month = data.path("birth_month");  
//	      System.out.println(birth_month.asInt());  
//	  
//	      JsonNode msg = root.path("msg");  
//	      System.out.println(msg.textValue());
//	      ((ObjectNode)root).set("tes2",(ArrayNode)JsonUtil.parse(new String[]{"a","b","c"}));
//	      System.out.println(root.toString());
//	    }  
//	    catch (IOException e)  
//	    {  
//	    }  
		
		MongoDb mongoUtl=new MongoDb("123.57.141.237",27017,"fangcheng_v2");
		DBCollection coll=mongoUtl.getCollection("demand");
		DBCursor cursor=coll.find();
		int count=0;
		while(cursor.hasNext()){
			count++;
			System.out.println(cursor.next().toString());
			if(count>20){
				break;
			}
		}
	}
}

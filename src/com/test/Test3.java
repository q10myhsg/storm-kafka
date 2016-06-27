package com.test;

import com.db.MysqlConnection;

public class Test3 {

	public static void main(String[] args) {
//		MysqlConnection mysql=new MysqlConnection("10.172.184.101",3306,"fangcheng_global","fangcheng_admin","wlj9$w6MW");
		MysqlConnection mysql=new MysqlConnection("192.168.1.249",3306,"python","root","hadoop");
//		mysql.sqlUpdate("insert into KafkaInfoQueue(jobName,status,statusInt,taskDeep,comment,inputData,startTime) values('ADD_NEW_BUSINESS','COMMIT_SUCCESS',2,1,'新增商业体','测试输入',str_to_date('2015-09-11 12:00:11','%Y-%m-%d %H:%i:%s'))");
		mysql.sqlUpdate("insert into test(info) values('收代理费就死定了')");
	}
}

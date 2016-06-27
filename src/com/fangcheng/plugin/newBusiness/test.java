package com.fangcheng.plugin.newBusiness;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import com.db.MysqlConnection;
import com.db.StringFormat;
import com.fangcheng.kafka.Bean.MysqlStatic;

public class test {
	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		// outer: for (int i = 0; i < 3; i++) {
		// System.out.print("Pass " + i + ":");
		// for (int j = 0; j < 4; j++) {
		// if (i + j > 10)
		// break outer; // exit both loops
		// System.out.print(j + " ");
		// }
		// System.out.println("This will notprint");
		// }
		// System.out.println("Loops complete.");
		//
		// loop: for (int i = 0; i < 3; i++) {
		// if (i == 2) {
		// break loop;
		// }
		// System.out.println(i);
		// }
		//
		// System.out.println("end");

//		MysqlConnection mysql = new MysqlConnection(MysqlStatic.mysqlIp,
//				MysqlStatic.mysqlPort, MysqlStatic.database, MysqlStatic.user,
//				MysqlStatic.pwd);
//
//		String str = "select a.jobId,b.statusInt,count(1) from (select jobId,jobIdSon from KafkaInfoQueueDesc as c where c.jobIdSon=2) as a  LEFT JOIN KafkaInfoQueue as b on a.jobIdSon=b.ID group by a.jobId,b.statusInt";
////		for (int i = 0; i < 10000; i++) {
////			ResultSet set = mysql.sqlSelect(str).resultSet;
////			while (set.next()) {
////				System.out.println(set.getString(1) + "\t" + set.getString(2)
////						+ "\t" + set.getString(3));
////			}
////			Thread.sleep(1000);
////		}
//		
//		System.out.println(StringFormat.formatter.format(new Date()));
//		
//		String str2="update KafkaInfoQueue set statusInt="+12+" , endTime=str_to_date('"+StringFormat.formatter.format(new Date())+"','%Y-%m-%d %H:%i:%s') where ID="+1;
//		System.out.println(str2);
//		mysql.sqlUpdate(str2);
		
		
		
		String hdfsPathUrl="hdfs://master:9000/storm_plugin_conf/";
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
		URL url = new URL(hdfsPathUrl + "infoQueue.properties");
		//url=new URL("hdfs://master:9000/storm_log/ERROR_LOG-NewPoi-2015-06-09.log");
		//url = new URL("hdfs://master:9000/mr_apps/apache-nutch-1.9.job");
		InputStream in = url.openStream();
		/**
		 * @param in
		 *            表示输入流
		 * @param out
		 *            表示输出流
		 * @param buffSize
		 *            表示缓冲大小
		 * @param close
		 *            在传输结束后是否关闭流
		 */
		// IOUtils.copyBytes(in, System.out, 1024, true);
		// byte[] b = new byte[4096];
		// int n=in.read(b);

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			System.out.println(new String(b, 0, n));
			out.append(new String(b, 0, n));
		}
		
	}

}

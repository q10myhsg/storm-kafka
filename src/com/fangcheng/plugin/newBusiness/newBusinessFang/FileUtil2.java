package com.fangcheng.plugin.newBusiness.newBusinessFang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * 文件读写操作类
 * 
 * @author Administrator
 *
 */
public class FileUtil2<T> {
	Logger log = Logger.getLogger(FileUtil2.class);
	/**
	 * 文件指针
	 */
	public File file = null;
	/**
	 * 文件类型
	 */
	private String code = "utf-8";
	private OutputStreamWriter write;
	private BufferedWriter writer;
	private Object object = new Object();

	/**
	 * 文件
	 * @param filename
	 * @param code
	 */
	public FileUtil2(String filename,String code)
	{
		System.out.println(filename+"\t"+code);
		//文件名需要重制定
		String fileName2=filename;
		file=new File(fileName2);
		if(!file.exists())
		{
			log.info("文件不存在:"+fileName2);
			//创建文件
			try {
				log.info("创建文件:"+fileName2);
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{

			int zn=0;
			while(true)
			{
				zn++;
				File fileTemp=new File(fileName2.substring(0,fileName2.lastIndexOf("."))+
						"_bak_"+zn+fileName2.substring(fileName2.lastIndexOf(".")));
				if(fileTemp.exists())
				{
					continue;
				}else{
					log.info("转移文件："+fileName2+"\t"+fileTemp.getAbsolutePath());
					file.renameTo(fileTemp);
					file=new File(fileName2);
					break;
				}
			}
			//file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.code=code;
		try {
			write =new OutputStreamWriter(new FileOutputStream(file,true),this.code);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
	    writer=new BufferedWriter(write);    
	}
	/**
	 * 文件
	 * @param filename
	 * @param code
	 */
	public FileUtil2(String filename,String code,boolean flag)
	{
		System.out.println(filename+"\t"+code);
		//文件名需要重制定
		String fileName2=filename;
		file=new File(fileName2);
	}
	
	
	public LinkedList<String> readAndClose()
	{
		LinkedList<String> result=new LinkedList<String>();
		BufferedReader reader = null;
		try{
		 
		 InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");  
         reader = new BufferedReader(read);
         String tempString = null;
         System.out.println("开始读取文件");
         // 一次读入一行，直到读入null为文件结束
         //是否为注释
	         while ((tempString = reader.readLine()) != null)
	         {
	        	// System.out.println(tempString);
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 result.add(tempString.trim());
	        	 
	         }
         try {
	 			reader.close();
	 		} catch (IOException e1) {
	 			// TODO Auto-generated catch block
	 			e1.printStackTrace();
	 		}
	 		try {
	 			read.close();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		}
         catch(Exception e)
         {
        	 e.printStackTrace();
         }
		finally{
	       
		}
		return result;
	}
	
	public void write(LinkedList<String> list)
	{
		if(list==null)
		{
			return;
		}
		synchronized (object) {
			while(list.size()>0)
			{
				String str=list.pollFirst();
				if(str==null)
				{
					continue;
				}
				try {
					write.write(str+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入文件的内容
	 * 
	 * @param queue
	 *            队列
	 * @param 放入队列中的数量
	 */
	public void write(String str) {
		synchronized (object) {
				// 如果异常则不管
				if (str == null || str.length() < 1) {
					return;
				}
				try {
					//log.info("写入文件内容：" + str);
					writer.write(str);
					writer.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入文件的内容
	 * 
	 * @param queue
	 *            队列
	 * @param 放入队列中的数量
	 */
	public void write(HashMap<T,T> map) {
		synchronized (object) {
				for(Entry<T, T> entry:map.entrySet())
				{
					try {
						write.write(entry.getKey()+"="+entry.getValue()+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 写入文件的内容
	 * 
	 * @param queue
	 *            队列
	 * @param 放入队列中的数量
	 */
	public void write(Hashtable<T,T> map,LinkedList<String> sort) {
		synchronized (object) {
//				for(Entry<T, T> entry:map.entrySet())
//				{
//					try {
//						write.write(entry.getKey()+"="+entry.getValue()+"\n");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
			try{
			for(String str:sort)
			{
				Object obj=map.get(str);
				if(obj==null)
				{
					write.write(str+"\t= ");
				}else{
					write.write(str+"\t= "+obj);
				}
				write.write("\n");
				
			}
			}catch (Exception e)
			{
				
			}
			
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入文件的内容
	 * 
	 * @param queue
	 *            队列
	 * @param 放入队列中的数量
	 */
	public void wirte(LinkedBlockingQueue<String> queue, int size) {
		synchronized (object) {
			for (int i = 0; i < size; i++) {
				// 如果异常则不管
				String str = queue.poll();
				if (str == null || str.length() < 1) {
					continue;
				}
				try {
					//log.info("写入文件内容：" + str);
					writer.write(str);
					writer.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 写入文件的内容
	 * 
	 * @param queue
	 *            队列
	 * @param 放入队列中的数量
	 */
	public void wirte(LinkedList<String> queue, int size) {
		synchronized (object) {
			for (int i = 0; i < size; i++) {
				// 如果异常则不管
				String str = queue.poll();
				if (str == null || str.length() < 1) {
					continue;
				}
				try {
					//log.info("写入文件内容：" + str);
					writer.write(str);
					writer.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭当前文件io
	 */
	public void close() {
		try {
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		String filename="d:\\sdf\\sdf\\wrqwer.txt";
//		System.out.println(filename.substring(0,filename.indexOf("."))+"-"+DateFormat.dateTime+"."+
//				filename.substring(filename.indexOf(".")+1));
	}

}

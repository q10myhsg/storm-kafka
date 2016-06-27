package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MyFileWriter {
	
	 FileWriter fw ;
	 private static final int lineCount=40000;
	 private  int count=0;
	 private String rootPath="";
	public MyFileWriter(String path)
	{
			try {
				rootPath=path;
				fw = new FileWriter(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	public void write(String line)
	{
		try {
			count++;			
			fw.write(line);
			fw.flush();
			if(count==lineCount)
			{
				close();
				count=0;
				fw= new FileWriter(rootPath+new Date().getTime());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close()
	{
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

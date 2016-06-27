package com.fangcheng.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * hdfs 操作库
 * @author Administrator
 *
 */
public class HdfsUtil {

	
	 //initialization  
    static Configuration conf = new Configuration();  
    static FileSystem hdfs;  
    static {  
//        String path = "/usr/java/hadoop-1.0.3/conf/";  
//        conf.addResource(new Path(path + "core-site.xml"));  
//        conf.addResource(new Path(path + "hdfs-site.xml"));  
//        conf.addResource(new Path(path + "mapred-site.xml"));  
//        path = "/usr/java/hbase-0.90.3/conf/";  
//        conf.addResource(new Path(path + "hbase-site.xml"));
      String path = "H:\\eclipse\\workspaceML\\stormKafka\\";
      //path ="/home/hduser/apps/hadoop/etc/hadoop/";
      //如果本地使用需要添加//集群则不需要
//      conf.addResource(new Path(path + "core-site.xml"));  
//      conf.addResource(new Path(path + "hdfs-site.xml"));  
//      conf.addResource(new Path(path + "mapred-site.xml")); 
        try {  
            hdfs = FileSystem.get(conf);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    //create a direction  
    public void createDir(String dir) throws IOException {  
        Path path = new Path(dir);  
        hdfs.mkdirs(path);  
        System.out.println("new dir \t" + conf.get("fs.default.name") + dir);  
    }     
      
    //copy from local file to HDFS file  
    public void copyFile(String localSrc, String hdfsDst) throws IOException{  
        Path src = new Path(localSrc);        
        Path dst = new Path(hdfsDst);  
        hdfs.copyFromLocalFile(src, dst);  
          
        //list all the files in the current direction  
        FileStatus files[] = hdfs.listStatus(dst);  
        System.out.println("Upload to \t" + conf.get("fs.default.name") + hdfsDst);  
        for (FileStatus file : files) {  
            System.out.println(file.getPath());  
        }  
    }  
      
    //create a new file  
    public void createFile(String fileName, String fileContent) throws IOException {  
        Path dst = new Path(fileName);  
        byte[] bytes = fileContent.getBytes();  
        FSDataOutputStream output = hdfs.create(dst);  
        output.write(bytes);  
        System.out.println("new file \t" + conf.get("fs.default.name") + fileName);
        output.close();
    }
    /**
     * 获取文件的控制柄
     * @param fileName
     * @return
     * 使用完成需要关闭
     */
    public FSDataOutputStream getWriteFile(String fileName) throws IOException{
    	System.out.println("获取写入地址:"+fileName);
    	 Path dst = new Path(fileName);
    	 return  hdfs.create(dst);
    }
    
    public FSDataInputStream getReadFile(String fileName) throws IOException{
    	Path dst=new Path(fileName);
    	if(hdfs.exists(dst)){
    		return hdfs.open(dst);
    	}else{
    		return null;
    	}
    }
    /**
     * 关闭
     * @param output
     */
    public void close(FSDataOutputStream output){
    	try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
      
    //list all files  
    public void listFiles(String dirName) throws IOException {  
        Path f = new Path(dirName);  
        FileStatus[] status = hdfs.listStatus(f);  
        System.out.println(dirName + " has all files:");  
        for (int i = 0; i< status.length; i++) {  
            System.out.println(status[i].getPath().toString());  
        }  
    }  
  
    //judge a file existed? and delete it!  
    public void deleteFile(String fileName) throws IOException {  
        Path f = new Path(fileName);  
        boolean isExists = hdfs.exists(f);  
        if (isExists) { //if exists, delete  
            boolean isDel = hdfs.delete(f,true);  
            System.out.println(fileName + "  delete? \t" + isDel);  
        } else {  
            System.out.println(fileName + "  exist? \t" + isExists);  
        }  
    } 
    /**
     * 修改命
     * @param fileName
     * @param renameFile
     * @throws IOException
     */
    public void renameFile(String fileName,String renameFile) throws IOException{
    	Path f=new Path(fileName);
    	Path f2=new Path(renameFile);
    	boolean isExists=hdfs.exists(f);
    	if(isExists){
    			hdfs.rename(f,f2);
    	}
    }
  
    public static void main(String[] args) throws IOException {  
    	HdfsUtil ofs = new HdfsUtil();
    	
        System.out.println("\n=======create dir=======");  
        String dir = "/test";  
        ofs.createDir(dir);  
        System.out.println("\n=======copy file=======");  
        String src = "H:\\eclipse\\workspaceML\\stormKafka\\core-site.xml";  
        ofs.copyFile(src, dir);  
        System.out.println("\n=======create a file=======");  
        String fileContent = "Hello, world! Just a test.";  
        ofs.createFile(dir+"/word.txt", fileContent);
        ofs.renameFile(dir+"/word.txt", dir+"/word_re.txt");
    }  
}

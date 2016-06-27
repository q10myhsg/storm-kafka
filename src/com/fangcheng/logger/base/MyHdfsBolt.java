package com.fangcheng.logger.base;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.tuple.Tuple;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream.SyncFlag;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.hdfs.common.rotation.RotationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.fangcheng.kafka.Bean.FieldStatic;

public class MyHdfsBolt extends MyAbstractHdfsBolt {
	//private static final Logger LOG = LoggerFactory.getLogger(MyHdfsBolt.class);

	public MyHdfsBolt() {
		//this.offset = 0L;
	}

	public MyHdfsBolt withFsUrl(String fsUrl) {
		this.fsUrl = fsUrl;
		return this;
	}

	public MyHdfsBolt withConfigKey(String configKey) {
		this.configKey = configKey;
		return this;
	}

	public MyHdfsBolt withFileNameFormat(JobFileNameFormat fileNameFormat) {
		this.fileNameFormat = fileNameFormat;
		return this;
	}

	public MyHdfsBolt withRecordFormat(RecordFormat format) {
		this.format = format;
		return this;
	}

	public MyHdfsBolt withSyncPolicy(SyncPolicy syncPolicy) {
		this.syncPolicy = syncPolicy;
		return this;
	}

	public MyHdfsBolt withRotationPolicy(FileRotationPolicy rotationPolicy) {
		this.rotationPolicy = rotationPolicy;
		return this;
	}

//	public MyHdfsBolt addRotationAction(RotationAction action) {
//		this.rotationActions.add(action);
//		return this;
//	}

	public void doPrepare(Map conf, TopologyContext topologyContext,
			OutputCollector collector) throws IOException {
		//LOG.info("Preparing HDFS Bolt...");
		this.fs = FileSystem.get(URI.create(this.fsUrl), this.hdfsConfig);
	}

//	public void setJobName(String jobName) {
//		if (jobName == null) {
//			return;
//		} else if (!this.oneJobName.equals(jobName)) {
//			try {
//				System.out.println("job修改:"+oneJobName+":"+jobName+"\t创建目录");
//				this.currentFile = createOutputFile(jobName);
//				this.oneJobName = jobName;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 *FILED HAVE SOME TUPLE
	 */
	public void execute(Tuple tuple, BasicOutputCollector arg1) {
		try {
			String jobName = tuple.getStringByField(FieldStatic.JOB_NAME);
			// 初始化jobName
			//System.out.println("獲取到的field:" + jobName);
			//System.out.println(this.currentFile.toUri().getPath());
			//System.out.println(this.oneJobName);
			String jobValue = tuple.getStringByField(FieldStatic.JOB_VALUE);
			// byte[] bytes = this.format.format(tuple);
			byte[] bytes = jobValue.getBytes();
			Long offset=0L;
			if(this.writeLockMap.get(jobName)==null)
			{
				this.writeLockMap.put(jobName,new Object());
			}
			synchronized (writeLockMap.get(jobName)) {
				FSDataOutputStream out=outMap.get(jobName);
				offset=offsetMap.get(jobName);
				if(out==null)
				{
					out=createOutputFile(jobName);
					outMap.put(jobName,out);
					offset=0L;
					writeLockMap.put(jobName,new Object());
				}
				out.write(bytes);
				offset += bytes.length;
				offsetMap.put(jobName,offset);
				if (this.syncPolicy.mark(tuple,offset)) {
					if (out instanceof HdfsDataOutputStream)
						((HdfsDataOutputStream)out)
								.hsync(EnumSet
										.of(HdfsDataOutputStream.SyncFlag.UPDATE_LENGTH));
					else {
						out.hsync();
					}
					this.syncPolicy.reset();
				}
			}

		//	this.collector.ack(tuple);
//
//			if (this.rotationPolicy.mark(tuple,offset)) {
//				rotateOutputFile();
//				this.offset = 0L;
//				this.rotationPolicy.reset();
//			}
		} catch (IOException e) {
			//LOG.warn("write/sync failed.", e);
			//this.collector.fail(tuple);
			//System.exit(1);
		}
	}

	/**
	 *  all output close
	 */
	void closeOutputFile() throws IOException {
		for(Entry<String,FSDataOutputStream> outM:outMap.entrySet())
		{	
			
			closeOutputFile(outM.getValue());

		}
	}
	
	void closeOutputFile(String jobName) throws IOException
	{
		synchronized (writeLockMap.get(jobName)) {
		FSDataOutputStream out=this.outMap.get(jobName);
		if(out!=null)
		{
			out.close();
		}
		}
	}
	void closeOutputFile(FSDataOutputStream out) throws IOException
	{
		if(out!=null)
		{
			out.close();
		}
	}

	FSDataOutputStream createOutputFile(String jobName) throws IOException {
		if(jobName==null)
		{
			return null;
		}
//		this.oneJobName=jobName;
		FSDataOutputStream out=null;
		Path path = null;
			path = new Path(this.fileNameFormat.getPath(),
					this.fileNameFormat.getName(this.rotation, jobName));
			//System.out.println("outPath:"+path.getName());
			this.rotation = false;
			if (this.fs.exists(path)) {
				this.closeOutputFile(jobName);
				out = fs.append(path);
			//	System.out.println("追擊阿內容");
			} else {
				out = this.fs.create(path);
			//	System.out.println("創建目錄");
			}
		//	LOG.info("fs是否存在目錄:" + this.fs.exists(path) + "\t" + path.getName());
		return out;
	}
	
	
	

	@Override
	Path createOutputFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}
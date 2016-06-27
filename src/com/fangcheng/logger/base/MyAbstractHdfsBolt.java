package com.fangcheng.logger.base;
/*     */ import backtype.storm.task.OutputCollector;
/*     */ import backtype.storm.task.TopologyContext;
/*     */ import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
/*     */ import backtype.storm.topology.base.BaseRichBolt;

/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
import java.util.HashMap;
/*     */ import java.util.Map;
import java.util.Map.Entry;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;

/*     */ import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
/*     */ import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
/*     */ import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
/*     */ import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy.TimeUnit;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
/*     */ import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
/*     */ import org.apache.storm.hdfs.common.rotation.RotationAction;
/*     */ import org.apache.storm.hdfs.common.security.HdfsSecurityUtil;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
public abstract class MyAbstractHdfsBolt extends BaseBasicBolt
{
 // private static final Logger LOG = LoggerFactory.getLogger(MyAbstractHdfsBolt.class);
  //protected ArrayList<RotationAction> rotationActions;
//  protected Path currentFile;
  protected OutputCollector collector;
  protected transient FileSystem fs;
  protected SyncPolicy syncPolicy;
  protected FileRotationPolicy rotationPolicy;
  protected JobFileNameFormat fileNameFormat;

  protected RecordFormat format;
  protected boolean rotation;
  protected String fsUrl;
  protected String configKey;
  protected transient HashMap<String,Object> writeLockMap;
  //protected transient Object writeLock;
  protected transient Timer rotationTimer;
  protected transient Configuration hdfsConfig;
  //key -> job name
  // 
	public transient HashMap<String,FSDataOutputStream> outMap;
	//public transient HashMap<String,Path> outPathMap=new HashMap<String,Path>();
	//key -> job name
	//value -> file end offset
	public transient HashMap<String,Long> offsetMap;
  public MyAbstractHdfsBolt()
  {
    //this.rotationActions = new ArrayList();
    //this.rotation = 0;
  }

  protected void rotateOutputFile()
    throws IOException
  {
   // LOG.info("Rotating output file...");
    //long start = System.currentTimeMillis();
      this.rotation = true;
     // Path newFile=null;
      for(Entry<String,FSDataOutputStream> outM:outMap.entrySet())
      {
    	    synchronized (this.writeLockMap.get(outM.getKey())) {
    	  //CLOSE output stream
    	    	if(outM.getValue()!=null)
    	    	{
    	    		outM.getValue().close();
    	    		//create new output stream
    	    		closeOutputFile(outM.getKey());
    	    		outM.setValue(null);
    	    	}
    	  //outM.setValue(createOutputFile(outM.getKey()));
      }
//      System.out.println("jobName:"+jobName);
//      	if(jobName!=null){
//      		newFile= createOutputFile(jobName);
//            LOG.info("Performing {} file rotation actions.", Integer.valueOf(this.rotationActions.size()));
//            for (RotationAction action : this.rotationActions) {
//              action.execute(this.fs, this.currentFile);
//            }
//            }else if(!oneJobName.equals("") ){
//            	newFile=createOutputFile(oneJobName);
//                LOG.info("Performing {} file rotation actions.", Integer.valueOf(this.rotationActions.size()));
//                for (RotationAction action : this.rotationActions) {
//                  action.execute(this.fs, this.currentFile);
//            }
//         this.currentFile = newFile;
      
      
      	}
//    }
//    long time = System.currentTimeMillis() - start;
//    LOG.info("File rotation took {} ms.", Long.valueOf(time));
  }
  
  @Override
	public void prepare(Map conf, TopologyContext topologyContext) { 

//  public final void prepare(Map conf, TopologyContext topologyContext, OutputCollector collector)
//  {
 //   this.writeLock = new Object();
	  this.writeLockMap=new HashMap<String,Object>();
	  this.outMap=new HashMap<String,FSDataOutputStream>();
	  this.offsetMap=new HashMap<String,Long>();
	  
		 format = new DelimitedRecordFormat()
		.withFieldDelimiter(" : ");
		 // 没多少条信息更新一次到hdfs中
		 // rotate files
 	// 多久刷一次文件
 	rotationPolicy = new TimedRotationPolicy(1.0f,
		TimeUnit.MINUTES);
    if (this.syncPolicy == null) throw new IllegalStateException("SyncPolicy must be specified.");
    if (this.rotationPolicy == null) throw new IllegalStateException("RotationPolicy must be specified.");
    if (this.fsUrl == null) {
      throw new IllegalStateException("File system URL must be specified.");
    }

    this.collector = collector;
    this.fileNameFormat.prepare(conf, topologyContext);
    this.hdfsConfig = new Configuration();
    Map map = (Map)conf.get(this.configKey);
    if (map != null) {
      for (Object key : map.keySet()) {
        this.hdfsConfig.set((String) key, String.valueOf(map.get((String)key)));
      }
    }

    try
    {
      HdfsSecurityUtil.login(conf, this.hdfsConfig);
      doPrepare(conf, topologyContext, collector);
//      this.currentFile = createOutputFile(null);
    }
    catch (Exception e) {
      throw new RuntimeException("Error preparing HdfsBolt: " + e.getMessage(), e);
    }

    if (this.rotationPolicy instanceof TimedRotationPolicy) {
      long interval = ((TimedRotationPolicy)this.rotationPolicy).getInterval();
      this.rotationTimer = new Timer(true);
      TimerTask task = new TimerTask()
      {
        public void run() {
          try {
           rotateOutputFile();
          } catch (IOException e) {
            //MyAbstractHdfsBolt.LOG.warn("IOException during scheduled file rotation.", e);
          }
        }
      };
      this.rotationTimer.scheduleAtFixedRate(task, interval, interval);
    }
  }

  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
  {
  }

  abstract void closeOutputFile()
    throws IOException;
  abstract void closeOutputFile(String jobName)
		    throws IOException;
  abstract FSDataOutputStream createOutputFile(String jobName) throws IOException;

  abstract Path createOutputFile()
    throws IOException;

  abstract void doPrepare(Map paramMap, TopologyContext paramTopologyContext, OutputCollector paramOutputCollector)
    throws IOException;
}
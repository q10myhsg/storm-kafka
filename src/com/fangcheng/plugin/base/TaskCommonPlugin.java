package com.fangcheng.plugin.base;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import com.db.MysqlConnection;
import com.db.TaskMysqlStatusBean;
import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.JsonMappingStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.StatusStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.logger.base.HdfsLoggerBasicBolt;
import com.fangcheng.logger.base.HdfsLoggerRichBolt;
import com.fangcheng.parse.interator.ClassLoaderJar;
import com.fangcheng.restart.Restart;
import com.fangcheng.util.JsonUtil;

/**
 * 调用搜房页面的parse方法
 * 
 * @author Administrator
 *
 */
public abstract class TaskCommonPlugin extends HdfsLoggerBasicBolt {
	// Log LOG = LogFactory.getLog(TaskExecMethod.class);
	/**
	 * kafka api consumer
	 */
	public KafkaUtil consumer = null;
	/**
	 * mysql存储器 连接固定的服务器
	 */
	public transient MysqlConnection mysql = null;
	/**
	 * 类的描述信息
	 */
	public String controlString = null;
	/**
	 * 输入数据的描述信息
	 */
	public String inputStringTag = null;
	/**
	 * 父亲的名字
	 */
	public String parentTopicName = null;
	/**
	 * 任务名
	 */
	public Class jobName = null;
	/**
	 * 类的基本class
	 */
	public Class boltName = null;
	/**
	 * 是否使用 外部 jar
	 */
	public boolean useJar = false;
	/**
	 * 配置文件
	 */
	public HashMap<String, String> config = null;
	/**
	 * 是否为第一次执行
	 */
	public boolean isFirst = true;
	/**
	 * 如果为空则不需要家在配置文件
	 */
	public String configFilePath = "hdfs://fcmaster-node:9000/storm_plugin_conf/infoQueue.properties";

	public String topicName = null;

	public TaskCommonPlugin(Class jobName, Class blotName, String topicName,
			String parentTopicName, boolean useJar) {
		this.topicName = topicName;
		this.jobName = jobName;
		this.boltName = blotName;
		this.parentTopicName = parentTopicName;
		this.useJar = useJar;
		if (parentTopicName == null) {
			System.out.println("不存在父亲请确认是否使用该 控件");
			System.exit(1);
		}
	}

	private OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		initLogger(jobName);
		init();
		if (isFirst) {
			try {
				// 第一次调用并初始化 调用mysql中的数据
				ArrayList<String> list = Restart.readData(topicName, mysql);
				isFirst = false;
				for (String str : list) {
					KafkaTransmitBean kafkaBeanLogger = null;
					kafkaBeanLogger = (KafkaTransmitBean) JsonUtil
							.getDtoFromJsonObjStr(str, KafkaTransmitBean.class);
					if (kafkaBeanLogger == null) {
						error(boltName, inputStringTag, "输入参数不可序列化:" + str);
						continue;
					}
					inputStringTag = getTag(kafkaBeanLogger);
					debug(boltName, inputStringTag, str);
					// 判断是否为返回的信息
					if (kafkaBeanLogger.reback) {
						execRebackInfo(kafkaBeanLogger);
					} else {
						// 为执行操作
						execInfo(kafkaBeanLogger);
					}
				}
			} catch (Exception e) {

			}
		}
	}

	// @Override
	// public void prepare(Map stormConf, TopologyContext context,
	// OutputCollector collector) {
	// this.collector=collector;
	// initLogger(jobName);
	// init();
	// }

	public void init() {
		config = new HashMap<String, String>();
		TimerConfigThread.readConfig(config, configFilePath);

		consumer = new KafkaUtil();
		
		this.mysql = new MysqlConnection(config.get("innerMysqlIp"),
				Integer.parseInt(config.get("innerMysqlPort")==null?"3306":config.get("innerMysqlPort")),
						config.get("innerMysqlDatabase")==null?MysqlStatic.database:config.get("innerMysqlDatabase"),
								config.get("innerMysqlUser")==null?MysqlStatic.user:config.get("innerMysqlUser"),
						config.get("innerMysqlPwd")==null?MysqlStatic.pwd:config.get("innerMysqlPwd"));
		if (useJar) {
			try {
				 ClassLoaderJar.reloadJar("h:/nutch");
//				ClassLoaderJar.reloadJar("/home/hduser/storm_apps/nutch_jar");
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (configFilePath != null) {
			TimerConfigThread timer = new TimerConfigThread(this.config,
					configFilePath);
			timer.start();
		}
		try {
			initDB();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		inputStringTag = null;
		String input = tuple.getString(0);
		// JSONObject json = JSONObject.fromObject(input);
		// System.out.println(input);
		KafkaTransmitBean kafkaBean = null;
		try {
			kafkaBean = (KafkaTransmitBean) JsonUtil.getDtoFromJsonObjStr(
					input, KafkaTransmitBean.class);
			if (kafkaBean == null || kafkaBean.params == null) {
				error(boltName, inputStringTag, "输入参数异常:" + input);
			} else {

				// 正常执行
				inputStringTag = getTag(kafkaBean);
				debug(boltName, inputStringTag, input);
				// 判断是否为返回的信息
				if (kafkaBean.reback) {
					execRebackInfo(kafkaBean);
				} else {
					// 为执行操作
					execInfo(kafkaBean);
				}
			}
		} catch (Exception e) {
			if (kafkaBean == null) {
				error(boltName, inputStringTag, "输入参数不可序列化:" + input);
			} else {
				error(boltName, inputStringTag, "输入参数不可序列化:" + e.toString());
			}
		}
		// collector.ack(tuple);
	}

	/**
	 * 处理返回的信息
	 * 
	 * @param kafkaBean
	 */
	public void execRebackInfo(KafkaTransmitBean kafkaBean) {
		// 判断返回信息类型
		if (kafkaBean.params == null) {
			return;
		}
		// 提交并修改mysql数据库
		TaskMysqlStatusBean status = TaskCommonControl.updateInfoTaskAndQuery(
				mysql, kafkaBean, kafkaBean.jobControlId,
				kafkaBean.jobControlParentId, kafkaBean.status.execStatus);
		// 如果执行状态为ok则返回对应信息给发送者
		if (status.isOk) {
			kafkaBean.setTopic(parentTopicName);
			kafkaBean.jobControlId = status.jobId;
			kafkaBean.status.execStatus = status.execStatus;
			consumer.sentMsgs(kafkaBean);
		}
	}

	/**
	 * 添加商业体具体执行方法
	 * 
	 * @param kafkaBean
	 */
	public void execInfo(KafkaTransmitBean kafkaBean) {
		StatusBean status = new StatusBean();
		status.startTime = System.currentTimeMillis();
		status.execStatus = StatusStatic.COMMIT_SUCCESS;
		// 需要判断是否为撤销操作
		// 需要判断是否重复提交
		// 目前没哟标准的流程控制人为重复提交的准确度量
		// 除非通过数据库的固定匹配方法
		// 如果jobid存在则为重复提交
		kafkaBean.setStatus(status);
		// 创建 mysql control 并获取control值
		// 因从父类传过来的id，所以使用 sonId
		long thisId = kafkaBean.jobControlId;
		// 没有子任务调用所有不需要触发子任务
		// TaskCommonControl.execInfoTaskSon(mysql,consumer,controlString+"_搜房",kafkaBean,TopicStatic.ADD_NEW_BUSSINESS_FANG);
		// 描述为当前 程序 正在执行中 并把执行的信息发送给 也可以不发送信息
		TaskCommonControl.updateInfoTask(mysql, consumer, kafkaBean,
				parentTopicName, thisId, StatusStatic.EXEC_ING);
		// 调用此类别对应的程序
		boolean jobFlag = false;

		try {
			jobFlag = runJob(kafkaBean);
		} catch (Exception e) {
			e.printStackTrace();
			if(kafkaBean.execComment==null)
			{
				kafkaBean.execComment=e.getMessage();
			}
			error(boltName, TagStatic.JOB_RUN_EXECPTION, e.toString());
			error(boltName,null,"content:"+JsonUtil.getJsonStr(kafkaBean));
		}
		if (jobFlag == false) {// 如果失败则提交失败的状态
			TaskCommonControl.updateInfoTask(mysql, consumer, kafkaBean,
					parentTopicName, thisId, StatusStatic.EXEC_ERROR);
		} else {
			// 如果成功则提交成功的组昂太
			TaskCommonControl.updateInfoTask(mysql, consumer, kafkaBean,
					parentTopicName, thisId, StatusStatic.EXEC_SUCCESS);
		}

	}

	/**
	 * 初始化数据库的使用
	 * 
	 * @throws Exception
	 */
	public abstract void initDB() throws Exception;

	/**
	 * 具体的调用程序 具体的调度方法需要重写
	 * 
	 * @param kafkaBean
	 * @return 是否成功
	 * @throws InterruptedException
	 */
	public abstract boolean runJob(KafkaTransmitBean kafkaBean)
			throws Exception;

	/**
	 * 获取输入类的标签
	 * 
	 * @param kafkaBean
	 */
	public abstract String getTag(KafkaTransmitBean kafkaBean) throws Exception;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// declarer.declare(new Fields("word", "count"));
	}
}

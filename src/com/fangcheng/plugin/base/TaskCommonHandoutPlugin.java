package com.fangcheng.plugin.base;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Tuple;
import ch.qos.logback.classic.pattern.Util;

import com.db.MysqlConnection;
import com.db.StringFormat;
import com.db.TaskMysqlStatusBean;
import com.fangcheng.kafka.KafkaUtil;
import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.JsonMappingStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.MysqlStatic;
import com.fangcheng.kafka.Bean.ParamsBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.StatusBean;
import com.fangcheng.kafka.Bean.StatusStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.logger.base.HdfsLoggerBasicBolt;
import com.fangcheng.logger.base.HdfsLoggerRichBolt;
import com.fangcheng.plugin.base.TaskCommonControl;
import com.fangcheng.stormKafka.KafkaSpoutUtil;
import com.fangcheng.util.JsonUtil;
import com.mongodb.Mongo;
import com.test.MyMongoDbTopology.SpoutMongo;

/**
 * 分发器
 * 任务内部会有 nutch job 完成的统一获取接口
 * @author Administrator
 *
 */
public class TaskCommonHandoutPlugin extends HdfsLoggerBasicBolt {

	/**
	 * kafka api consumer
	 */
	private KafkaUtil consumer = null;
	/**
	 * mysql存储器
	 */
	private transient MysqlConnection mysql = null;

	private String controlString = "添加新商业体控制器";

	private Class jobClass = null;

	private Class jobBoltClass;

	/**
	 * 该任务挂在的是 最终的返回信息 kafka的topic
	 */
	private String kafkaSend = null;
	/**
	 * 本地使用的kafka获取topic
	 */
	private String kafkaThis = null;

	private OutputCollector collector;
	/**
	 * 某人深度为1
	 */
	private int deep = 1;
	/**
	 * 子内容对应的连接 key对应 描述名字 value 对应使用的 topic 0-对应 深度为2 1-对应深度为3
	 */
	private HashMap<String, String> sonMap = null;

	/**
	 * 深度3 映射深度2所使用的映射方法 当 深度为2的满足全部执行成功后 会启动深度3对应的任务集
	 */
	private ArrayList<TaskRelationBean>[] taskRelationBean = null;
	
	/**
	 * 统一的配置信息
	 */
	private HashMap<String,String> config=null;
	
	/**
	 * 如果为空则不需要家在配置文件
	 */
	public String configFilePath = "hdfs://fcmaster-node:9000/storm_plugin_conf/infoQueue.properties";

	/**
	 * 
	 * @param jobClass
	 *            类名
	 * @param jobBoltClass
	 *            继承 handout 的类
	 * @param kafkaGet
	 *            获取源
	 * @param kafkaSend
	 *            发送返回源
	 * @param controlString
	 *            类描述
	 */
	public TaskCommonHandoutPlugin(Class jobClass, Class jobBoltClass,
			String kafkaThis, String kafkaSend, String controlString,
			HashMap<String, String> sonMap,
			ArrayList<TaskRelationBean>... taskRelationBean) {
		this.jobClass = jobClass;
		this.jobBoltClass = jobBoltClass;
		this.kafkaSend = kafkaSend;
		this.controlString = controlString;
		this.sonMap = sonMap;
		this.taskRelationBean = taskRelationBean;
		this.kafkaThis = kafkaThis;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		initLogger(jobClass);
		consumer = new KafkaUtil();
		config = new HashMap<String, String>();
		TimerConfigThread.readConfig(config, configFilePath);
		this.mysql = new MysqlConnection(config.get("innerMysqlIp"),
				Integer.parseInt(config.get("innerMysqlPort")==null?"3306":config.get("innerMysqlPort")),
						config.get("innerMysqlDatabase")==null?MysqlStatic.database:config.get("innerMysqlDatabase"),
								config.get("innerMysqlUser")==null?MysqlStatic.user:config.get("innerMysqlUser"),
						config.get("innerMysqlPwd")==null?MysqlStatic.pwd:config.get("innerMysqlPwd"));
	}

	// @Override
	// public void prepare(Map stormConf, TopologyContext context,
	// OutputCollector collector) {
	// initLogger(jobClass);
	// this.collector = collector;
	// consumer = new KafkaUtil();
	// this.mysql = new MysqlConnection(MysqlStatic.mysqlIp,
	// MysqlStatic.mysqlPort, MysqlStatic.database,
	// MysqlStatic.user, MysqlStatic.pwd);
	// }

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String input = tuple.getString(0);
		// System.out.println("新数据录入:"+tuple.getMessageId());
		KafkaTransmitBean kafkaBean = null;
		try {
			kafkaBean = (KafkaTransmitBean) JsonUtil.getDtoFromJsonObjStr(
					input, KafkaTransmitBean.class);
			debug(jobBoltClass, input);

			if (kafkaBean == null || kafkaBean.params == null) {
				error(jobBoltClass, "输入参数异常:" + input);
			}
			// 判断是否为返回的信息
			if (kafkaBean.reback) {
				execRebackInfo(kafkaBean, tuple);
			} else {
				// 判断是否为子程序 返回的数据
				if (kafkaBean.reBackTopic != null
						&& !kafkaBean.reBackTopic.equals("")) {
					// 如果返回时本地的topic那么说明为 nutch直接触发的
					if (kafkaBean.nutchBack) {
						//记录了nutch上的job任务
						String rebackTopic=kafkaBean.reBackTopic;
						newTaskAndReback(kafkaBean,tuple,input,rebackTopic);
					} else {
						String rebackTopic = kafkaBean.reBackTopic;
						kafkaBean.reBackTopic = null;
						updateInfo(kafkaBean, tuple, input, rebackTopic);
					}
				} else {
					// 否则为新添加的信息
					addNewInfo(kafkaBean, tuple, input);
				}
			}
		} catch (Exception e) {
			if (kafkaBean == null) {
				error(jobBoltClass, "输入参数不可序列化:" + input);
			} else {

			}
		}
		// try {
		// Thread.sleep(400);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// collector.ack(tuple);
	}

	/**
	 * 目前此方法只支持 直接执行，不能等待后续的程序撤销
	 * 
	 * @param kafkaBean
	 */
	public void execRebackInfo(KafkaTransmitBean kafkaBean, Tuple tuple) {
		// 提交并修改mysql数据库
		TaskMysqlStatusBean status = TaskCommonControl.updateInfoTaskAndQuery(
				mysql, kafkaBean, kafkaBean.jobControlId,
				kafkaBean.jobControlParentId, kafkaBean.status.execStatus);
		// 如果执行状态为ok则返回对应信息给发送者
		if (status.isOk) {
			kafkaBean.setTopic(kafkaSend);
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
	public void addNewInfo(KafkaTransmitBean kafkaBean, Tuple tuple,
			String kafkaBeanToString) {
		StatusBean status = new StatusBean();
		status.startTime = System.currentTimeMillis();
		status.execStatus = StatusStatic.COMMIT_SUCCESS;
		kafkaBean.taskDeep = deep;
		ParamsBean jobTypesMap =kafkaBean.params;
		// 如果调用的jobType不存在 或者本身就没有定位 对应的额job则 直接返回
		if (jobTypesMap.jobType == null || sonMap == null) {
			return;
		}
		// 需要判断是否重复提交
		// 目前没哟标准的流程控制人为重复提交的准确度量
		// 除非通过数据库的固定匹配方法
		if (kafkaBean.jobControlId != 0) {
			return;
		}
		// 如果jobid存在则为重复提交
		kafkaBean.setStatus(status);

		// 创建 mysql control 并获取control值
		// 添加控制器状态kafka
		// System.out.println("执行一次");
		long parentId = TaskCommonControl.execInfoTask(mysql, consumer,
				controlString, kafkaBean, kafkaSend, kafkaBeanToString);
		if (parentId <= 0L) {
			return;
		}
		String parentName = kafkaBean.parentName;
		kafkaBean.parentName = this.controlString;
		kafkaBean.jobControlParentId = parentId;
		kafkaBean.taskDeep = deep + 1;
		if (sonMap != null) {
			String[] jobTypes =jobTypesMap.jobType
					.split(",");
			boolean flag = false;
			for (String jobType : jobTypes) {// 如果为全部的job都 使用则执行已经注册的全部数据
				if (jobType.equals(JobStatic.ALL)) {
					flag = true;
					break;
				}
			}
			if (flag) {
				for (Entry<String, String> map : sonMap.entrySet()) {
					TaskCommonControl.execInfoTaskSon(mysql, consumer,
							controlString + "_" + map.getKey(), map.getKey(),
							kafkaBean, map.getValue(), kafkaBeanToString);
				}
			} else {
				for (String jobType : jobTypes) {
					String job = sonMap.get(jobType);
					if (job == null) {
						continue;
					}
					TaskCommonControl.execInfoTaskSon(mysql, consumer,
							controlString + "_" + jobType, jobType, kafkaBean,
							job, kafkaBeanToString);
				}

			}
		}
		kafkaBean.parentName = parentName;
		// // 结束 描述为正在执行中
		TaskCommonControl.updateInfoTask(mysql, consumer, kafkaBean, kafkaSend,
				parentId, StatusStatic.EXEC_ING);
		// 判断规则
	}
	/**
	 * nutch job 依然是返回该类的统一返回 reback kafka topic
	 * 如果返回的是需要新增一个 nutch任务 并直接返回执行成功则调用此方法
	 * @param kafkaBean
	 * @param tuple
	 * @param kafkaBeanToString
	 * @param rebackTopic
	 */
	public void newTaskAndReback(KafkaTransmitBean kafkaBean, Tuple tuple,
			String kafkaBeanToString, String rebackTopic)
	{
		//需要创建一个任务 并标记为nutchjob的任务集合 及子集为 rebackTopic的
		TaskCommonControl.execInfoTaskNutchSon(mysql, consumer, kafkaBean,kafkaBeanToString);
		kafkaBean.topic=this.kafkaSend;
		consumer.sentMsgs(kafkaBean);
	}
	/**
	 * 更新返回的信息 主要作用是 修改parent信息
	 * 
	 * @param kafkaBean
	 */
	public void updateInfo(KafkaTransmitBean kafkaBean, Tuple tuple,
			String kafkaBeanToString, String rebackTopic) {
		// 判断返回信息类型

		// //如果调用全部的则需要将所有的 3-n级子任务初始化了 并且该任务必须为已经完成状态
		if (taskRelationBean != null
				&&( kafkaBean.status.execStatus == StatusStatic.EXEC_SUCCESS || kafkaBean.status.execStatus==StatusStatic.EXEC_ERROR)) {
			// 获取 任务的深度状态
			int taskDeep = kafkaBean.taskDeep;
			// 首先判断是否存在子任务继续
			if (taskRelationBean.length >= taskDeep - 1) {
				// 获取下一级别的状态
				ArrayList<TaskRelationBean> bean = taskRelationBean[taskDeep - 2];
				if (bean == null) {
					// 如果已经不存在该级别任务则去判断状态
				} else {
					taskDeep++;
					// 一个任务完成可能涉及多个子任务的启动
					for (TaskRelationBean be : bean) {// 判断是否满足 过滤条件
						boolean isOk = be.judgeConditon(kafkaBean);
						if (isOk) {
						} else {
							// 判断第二关系
							isOk = be.judgeConditionJob(kafkaBean);
						}
						if (isOk) {
							// 首先需要修改 状态 成功为 成功等待孩子

							// 调整父节点位置
							kafkaBean.jobControlParentId = kafkaBean.jobControlId;
							// 调整状态为 提交
							kafkaBean.status.execStatus = StatusStatic.COMMIT_SUCCESS;
							kafkaBean.taskDeep = taskDeep;
							// 从数据库中创建子程序 //并发送信息给 子程序任务
							TaskCommonControl.execInfoTaskSon(mysql, consumer,
									controlString + "_" + taskDeep + "_",
									be.jobName, kafkaBean, be.jobTopic,
									kafkaBeanToString);
						}
					}
				}
				return;
			} else {
				// 否则表示不存在子任务 那么就需要判断自己这个任务是否执行完成 则继续下面的
			}
		}
		if (kafkaBean.status.execStatus == StatusStatic.EXEC_SUCCESS)
		{
			// 如果为 执行成功 则需要修改为成功
			//目前子任务不支持其他状态 其中返回会有 已经开始执行
			String temp=StringFormat.getUpdateStringEndTime(
					kafkaBean.jobControlId,StatusStatic.SUCCESS,kafkaBean.execComment);
			mysql.sqlUpdate(temp);
		}else if(kafkaBean.status.execStatus == StatusStatic.EXEC_ERROR) 
		{
			String temp=StringFormat.getUpdateStringEndTime(
					kafkaBean.jobControlId,StatusStatic.ERROR,kafkaBean.execComment);
			mysql.sqlUpdate(temp);
		}else{
			return;
		}
		boolean flag = TaskCommonControl.taskIsEnd(mysql,
				kafkaBean.jobControlId);
		if (flag) {
			debug(jobBoltClass, "程序执行过快，不需要重复提交给上级信息了:子任务id为:"
					+ kafkaBean.jobControlId);
			return;
		}
		// 修改该任务的状态 并判断上级任务是否执行完成
		// 提交并修改mysql数据库 只判断新信息是否导致数据库成功
		TaskMysqlStatusBean status = TaskCommonControl.updateInfoTaskAndQuery(
				mysql, kafkaBean, kafkaBean.jobControlId);
		// 如果执行状态为ok则返回对应信息给发送者
		if (status.isOk) {
			if (status.comment == null) {
				if (kafkaBean.taskDeep == 2) {
					// 如果为二级任务则表示最终的任务已经结束
					kafkaBean.setTopic(kafkaSend);
					kafkaBean.jobControlId = status.jobId;
					kafkaBean.status.execStatus = status.execStatus;
					kafkaBean.execComment = status.comment;
					consumer.sentMsgs(kafkaBean);
				} else {
					// 如果为2级以上的任务 则需要将任务提交给上一级
					// 因为目前只会涉及3级任务所以直接返回信息给上级即可
					// 则本身 topic存储的就是此任务 所以不需要求改 调整深度
					kafkaBean.taskDeep--;
					kafkaBean.jobControlId = status.jobId;
					kafkaBean.reBackTopic = rebackTopic;
					kafkaBean.status.execStatus = status.execStatus;
					kafkaBean.execComment = status.comment;
					consumer.sentMsgs(kafkaBean);
				}
			} else {
				// 信息为错误
				error(jobBoltClass, "子任务id:" + kafkaBean.jobControlId
						+ "不存在，请确认相关任务是否存在:" + status.comment);
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// declarer.declare(new Fields("word", "count"));
	}

}

package com.fangcheng.kafka;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import com.fangcheng.kafka.Bean.JobStatic;
import com.fangcheng.kafka.Bean.JsonMappingStatic;
import com.fangcheng.kafka.Bean.KafkaTransmitBean;
import com.fangcheng.kafka.Bean.ParamsBean;
import com.fangcheng.kafka.Bean.ParamsStatic;
import com.fangcheng.kafka.Bean.TopicStatic;
import com.fangcheng.plugin.base.TimerConfigThread;
import com.fangcheng.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class KafakCommonUtilServer extends Thread {

	/**
	 * 公共的队列容器
	 */
	public LinkedBlockingQueue<InfoBean> queue = new LinkedBlockingQueue<InfoBean>();
	/**
	 * 基本类
	 */
	public KafkaTransmitBean kafkaBean = null;
	/**
	 * 获取kafka的返回数据
	 */
	private final static String topic = TopicStatic.ADD_NEW_BUSINESS_STATUS;
	/**
	 * 平台消费者id
	 */
	private final static String groupId = "PlatConsumer2";
	private KafkaUtil consumer = null;

	public KafakCommonUtilServer() {
		consumer = new KafkaUtil();
	}

	/**
	 * 线程
	 */
	public void run() {
		while (true) {
			KafkaStream<String, String> stream = consumer.getConnectioin(
					groupId, topic);
			ConsumerIterator<String, String> it = stream.iterator();
			try {
				while (it.hasNext()) {
					consumer(it.next());
					Thread.sleep(0);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean consumer(MessageAndMetadata<String, String> msg) {
		// System.out.println("key:" + msg.key() + "\t:input:"
		// + msg.message());
		KafkaTransmitBean kafkaBean = getBean(msg);
		InfoBean info = parse(kafkaBean);
		if (info == null) {
			return false;
		}
		// System.out.println("解析后的数据:"+JsonUtil.getJsonStr(info));
		this.queue.add(info);
		return true;
	}

	/**
	 * 获取传输的bean
	 * 
	 * @param msg
	 * @return
	 */
	public KafkaTransmitBean getBean(MessageAndMetadata<String, String> msg) {
		KafkaTransmitBean kafkaBean = null;
		try {
			kafkaBean = (KafkaTransmitBean) JsonUtil.getDtoFromJsonObjStr(
					msg.message(), KafkaTransmitBean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kafkaBean;
	}

	public static class InfoBean implements Serializable {
		/**
		 * 平台自己的id
		 */
		public long jobId = 0L;
		/**
		 * 发送job类型
		 */
		public String jobType = null;
		/**
		 * 执行状态
		 */
		public int status = 0;
		/**
		 * 是否为周期性任务
		 */
		public boolean isCycle = false;
		/**
		 * 是否为新增的ids;
		 */
		public ArrayList<Long> isNewID = null;
		/**
		 * 具体的参数集
		 */
		public HashMap<String, Object> params = new HashMap<String, Object>();

		public long getJobId() {
			return jobId;
		}

		public void setJobId(long jobId) {
			this.jobId = jobId;
		}

		public String getJobType() {
			return jobType;
		}

		public void setJobType(String jobType) {
			this.jobType = jobType;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public HashMap<String, Object> getParams() {
			return params;
		}

		public void setParams(HashMap<String, Object> params) {
			this.params = params;
		}

	}

	/**
	 * 解析bean 类别
	 * 
	 * @param kafkaBean
	 * @return
	 */
	public InfoBean parse(KafkaTransmitBean kafkaBean) {
		InfoBean info = new InfoBean();
		if (kafkaBean == null || kafkaBean.getParams() == null) {
			return null;
		}
		boolean flag = false;
		if (kafkaBean.status != null) {
			info.status = kafkaBean.status.execStatus;
		}
		String jobType = kafkaBean.getParams().jobType;
		if (jobType != null) {
			info.jobType = jobType;
			flag = true;

			// 判断是否为新增的批量id
			ParamsBean params=kafkaBean.getParams();
			if(params!=null&&params.responseData!=null)
			{
				ArrayNode newIdS =(ArrayNode)kafkaBean.getParams().responseData.get(ParamsStatic.NEW_ID);
			if (newIdS != null) {
				info.isNewID=new ArrayList<Long>();
				for(int n=0;n<newIdS.size();n++)
				{
					info.isNewID.add(newIdS.get(n).asLong());
				}
				return info;
			}
			}
			// 否则为 新增商业体触发行为
			String[] param = null;
			if (kafkaBean.isNutchBack()) {
				info.isCycle = true;
			} else {
				if (info.jobType.equals(JobStatic.FANG)) {
					param = KafkaCommonUtil.getFangParam();
				} else if (info.jobType.equals(JobStatic.POI_CRAWLER)) {
					param = KafkaCommonUtil.getPoiTjParam();
				} else if (info.jobType.equals(JobStatic.DP)) {
					param = KafkaCommonUtil.getDpParam();
				} else {
					System.out.println("输入job类型错误");
					return null;
				}
			}
			input(kafkaBean, info, param);
			return info;
		}
		return null;
	}

	/**
	 * 填装器
	 * 
	 * @param kafkaBean
	 * @param info
	 * @param param
	 * @return
	 */
	public void input(KafkaTransmitBean kafkaBean, InfoBean info, String[] param) {
		if (param == null) {
			return;
		}

		ParamsBean params = kafkaBean.getParams();
		if(params!=null&& params.responseData!=null)
		for (String str : param) {
			String val=params.responseData.get(str).textValue();
			if (val == null) {
				return;
			}
			info.params.put(str, val);
			if (info.jobType.equals(JobStatic.FANG)) {
				printString("搜房", info, str, val);
			} else if (info.jobType.equals(JobStatic.POI_CRAWLER)) {
				printString("统计", info, str, val);
			} else if (info.jobType.equals(JobStatic.DP)) {
				printString("点评", info, str, val);
			} else {
				return;
			}
		}
	}

	public void printString(String jobName, InfoBean info, String str,
			Object obj) {
		System.out.println(jobName + ":status:" + getStatusString(info.status)
				+ ":" + str + ":" + obj.toString());
	}

	/**
	 * 获取状态值对应的 意义
	 * 
	 * @param status
	 * @return
	 */
	public String getStatusString(int status) {
		if (status == 0) {
			return "失败";
		} else if (status == 1) {
			return "成功";
		} else if (status == 2) {
			return "提交成功";
		} else if (status == 11) {
			return "已经开始执行";
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		KafakCommonUtilServer thread = new KafakCommonUtilServer();
		thread.start();
	}
}

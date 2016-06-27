package com.fangcheng.recommend.model.config;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fangcheng.util.JsonUtil;

/**
 * 从配置文件中读取相关的参数信息
 * 
 * @author Administrator
 *
 */
public class Config {
	public static ConfigBean configBean = null;

	/**
	 * 配置文件或者 tomcate中的工程当前目录地址
	 */
	public static String path = "hdfs://fcmaster-node:9000/storm_apps/onlineRecommend/config";
	/**
	 * 配置文件的绝对路径
	 */
	public static String configFilePath = null;

	/**
	 * 相对路径
	 */
	public static String configAbsPath = "/config/config.xml";
	public static boolean isInit = false;
	static {
		if (isInit) {
			try {
				// 从配置文件中读取文件
				String temp = Config.class.getResource("/").getPath();
				System.out.println("temp:" + temp);
				if (temp.contains("build/classes/")) {
					path = temp
							.substring(0, temp.lastIndexOf("build/classes/"))
							+ "WebContent";
				} else if (temp.indexOf("WEB-INF") <= 0) {
					// 则为resin
					path = temp.substring(0, temp.lastIndexOf("/"))
							+ "/webapps/fcRecommendService";
				} else if (temp.contains("webapps")) {
					path = temp.substring(0, temp.indexOf("/WEB-INF"));
				} else {
				}
				path += "/build";
				configFilePath = path + configAbsPath;
				readFileXml(configFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized static void init(String direct, String configPath) {
		path = direct;
		readFileXml(direct + configPath);
	}

	/**
	 * 读取配置文件
	 */
	public static void readFileXml(String configPath) {
		System.out.println("读取config文件:" + configPath);
		configBean = new ConfigBean();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = db.parse(configPath);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// System.exit(1);
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// System.exit(1);
			return;
		}
		Element elmtInfo = doc.getDocumentElement();
		NodeList nodes = elmtInfo.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node result = nodes.item(i);
			// System.out.println(i+":"+nodes.getLength()+":current:"+result.getNodeName());
			// 多个任务的遍历
			if (result.getNodeName().equals("#text")) {
				continue;
			}
			if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("mysqlConfig")) {

				setMysqlConfig(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("mongoConfig")) {

				setMongoConfig(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("recommandWeightSource")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.recommandWeightSource = getProperties(temp2, "name");
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("useMallSource")) {

				addMallSource(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("useBrandSource")) {

				addBrandSource(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("kafkaUserTopic")) {

				setKafkaUserTopic(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("kafkaReLoggerTopic")) {

				setKafkaReLoggerTopic(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("reServiceData")) {

				setServiceData(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("reSimServiceData")) {

				setSimServiceData(result);
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("kafkaUserActionTopic")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.kafkaUserActionTopic = getProperties(temp2,
						"name");

			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("userFeature")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.userFeature = getProperties(temp2, "name");
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("userFeatureT")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.userFeatureT = getProperties(temp2, "name");
			} else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("kafkaGroupId")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.kafkaGroupId = getProperties(temp2, "name");
			}else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("kafkaLoggerToHdfs")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.kafkaLoggerToHdfs=getProperties(temp2,"name");
			}else if (result.getNodeType() == Node.ELEMENT_NODE
					&& result.getNodeName().equals("reUserLoggerData")) {
				NamedNodeMap temp2 = result.getAttributes();
				Config.configBean.reUserLoggerData=getProperties(temp2,"name");
			}
		}
	}

	/**
	 * 加载属性到 mysql的配置中
	 * 
	 * @param attributes
	 */
	public static void setMysqlConfig(Node result) {
		NamedNodeMap attributes = result.getAttributes();
		Config.configBean.mysql.ip = getProperties(attributes, "ip");
		Config.configBean.mysql.port = getPropertiesInt(attributes, "port");
		Config.configBean.mysql.database = getProperties(attributes, "database");
		Config.configBean.mysql.user = getProperties(attributes, "user");
		Config.configBean.mysql.pwd = getProperties(attributes, "pwd");
	}

	/**
	 * 加载属性到 mysql的配置中
	 * 
	 * @param attributes
	 */
	public static void setMongoConfig(Node result) {
		NamedNodeMap attributes = result.getAttributes();
		Config.configBean.mongo.ip = getProperties(attributes, "ip");
		Config.configBean.mongo.port = getPropertiesInt(attributes, "port");
		Config.configBean.mongo.database = getProperties(attributes, "database");
		Config.configBean.mongo.user = getProperties(attributes, "user");
		Config.configBean.mongo.pwd = getProperties(attributes, "pwd");
		Config.configBean.mongo.defaultDb=getProperties(attributes,"defaultDb");
	}

	/**
	 * 设置 kafka对应的相关topic信息
	 * 
	 * @param result
	 */
	public static void setKafkaUserTopic(Node result) {
		NamedNodeMap attributes = result.getAttributes();
		Config.configBean.kafkaUserTopic = getProperties(attributes, "name");

	}

	/**
	 * 设置 kafka对应的相关topic信息
	 * 
	 * @param result
	 */
	public static void setKafkaReLoggerTopic(Node result) {
		NamedNodeMap attributes = result.getAttributes();
		Config.configBean.kafkaReLoggerTopic = getProperties(attributes, "name");

	}

	public static String getProperties(NamedNodeMap attributes, String name) {
		Node node = attributes.getNamedItem(name);
		if (node == null) {
			return null;
		} else {
			return node.getNodeValue();
		}
	}

	public static int getPropertiesInt(NamedNodeMap attributes, String name) {
		Node node = attributes.getNamedItem(name);
		if (node == null) {
			return 0;
		} else {
			return Integer.parseInt(node.getNodeValue());
		}
	}

	/**
	 * 权重表对应的table内容
	 * 
	 * @param result
	 */
//	public static void setWegithtable(Node result) {
//		NodeList nodes = result.getChildNodes();
//		for (int i = 0; i < nodes.getLength(); i++) {
//			Node node = nodes.item(i);
//			if (node.getNodeName().equals("#text")) {
//				continue;
//			}
//			if (node.getNodeType() == Node.ELEMENT_NODE
//					&& node.getNodeName().equals("weight")) {
//				Config.configBean.recommandWeightSource = node.getNodeValue();
//				break;
//			}
//		}
//	}

	/**
	 * 添加mall的推荐数据源表
	 * 
	 * @param result
	 */
	public static void addMallSource(Node result) {
		childs(result, true);
	}

	/**
	 * 添加brand的推荐数据源
	 * 
	 * @param result
	 */
	public static void addBrandSource(Node result) {
		childs(result, false);
	}

	/**
	 * 推荐服务源
	 * 
	 * @param result
	 */
	public static void setServiceData(Node result) {
		NamedNodeMap attributes = result.getAttributes();
		Config.configBean.reServiceDataTable = attributes.getNamedItem("name")
				.getNodeValue();
	}

	/**
	 * 相似服务源
	 * 
	 * @param result
	 */
	public static void setSimServiceData(Node result) {
		NamedNodeMap attributes = result.getAttributes();
		Config.configBean.reSimServiceDataTable = attributes.getNamedItem(
				"name").getNodeValue();
	}

	/**
	 * 获取所有的全部子集
	 * 
	 * @param result
	 * @return
	 */
	public static void childs(Node result, boolean isMall) {
		ArrayList<KeyValue> list = new ArrayList<KeyValue>();
		ArrayList<Integer> name = new ArrayList<Integer>();
		NodeList beanList = result.getChildNodes();
		double sum = 1E-10;
		for (int p = 0; p < beanList.getLength(); p++) {
			// 对于每一个analysis都要建立对应的Bean
			Node bean = beanList.item(p);
			if (bean.getNodeName().equals("#text")) {
				continue;
			}
			NamedNodeMap attributes = bean.getAttributes();
			// String value2=bean.getTextContent();
			KeyValue kv = new KeyValue(bean.getNodeName(),
					Double.parseDouble(attributes.getNamedItem("defaultWeight")
							.getNodeValue()));
			sum += kv.value;
			name.add(Integer.parseInt(attributes.getNamedItem("id")
					.getNodeValue()));
			list.add(kv);
		}
		// 标准化
		int i = 0;
		if (isMall) {
			for (KeyValue val : list) {
				val.value /= sum;
				Config.configBean.addUseMallSource(name.get(i), val);
				Config.configBean.useMallMap.put(val.name, name.get(i));
				i++;
			}
		} else {
			for (KeyValue val : list) {
				val.value /= sum;
				Config.configBean.addUseBrandSource(name.get(i), val);
				Config.configBean.useBrandMap.put(val.name, name.get(i));
				i++;
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(JsonUtil.getJsonStr(Config.configBean));
	}
}

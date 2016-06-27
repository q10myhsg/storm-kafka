package com.fangcheng.kafka.Bean;

import java.util.ArrayList;

public class ParamsStatic {

	/**
	 * 返回信息对应的主题
	 */
	public static final String TOPIC="topic";
	/**
	 * 使用ID
	 */
	public static final String ID="id";
	/**
	 * 所使用的url 内容为 string
	 */
	public static final String URL="url";
	/**
	 * 使用的城市 存储的内容为int[] 如果本项目为null则表示全量，否则 如果为int[].length ==0 则为不使用 
	 */
	public static final String CITY="city";
	/**
	 * 
	 * 使用的keyWORD 内容为 string
	 */
	public static final String KEYWORD="keyword";
	/**
	 * 使用的job类型
	 */
	public static final String JOBTYPE="jobType";
	/**
	 * 使用的分类 可以对应 点评的 是 mall 还是 brand
	 */
	public static final String CATEOGYR="";
	/**
	 * POI统计使用参数
	 */
	public static final String POI_CRAWLER="poi";
	/**
	 * 用于标记全部的新增数据 id
	 * 以,为分隔符
	 */
	public static final String NEW_ID="new_id";
	/**
	 * 更新的id
	 */
	public static final String UPDATE_ID="update_id";
	/**
	 * 新增的数量
	 */
	public static final int NEW_ID_COUNT=500;
	
	/**
	 * 新增的id ,分割
	 * @param newID
	 * @return
	 */
	public static ArrayList<ArrayList<Long>> getNewId(ArrayList<Long> newID)
	{
		ArrayList<ArrayList<Long>> list=new ArrayList<ArrayList<Long>>();
		//StringBuffer sb=null;
		ArrayList<Long> temp=null;
		for(int i=0;i<newID.size();i++)
		{
			if(i%NEW_ID_COUNT==0)
			{
				temp=new ArrayList<Long>();
				list.add(temp);
				temp.add(newID.get(i));
			}else{
				temp.add(newID.get(i));
			}
		}
		return list;
	}
	/**
	 * 从字符串中获取新增的id
	 * @param string
	 * @return
	 */
//	public static ArrayList<Long> getNewIdRev(String string)
//	{
//		ArrayList<Long> list=new ArrayList<Long>();
//		String[] st=string.split(",");
//		for(String str:st)
//		{
//			list.add(Long.parseLong(str));	
//		}
//		return list;
//	}
}

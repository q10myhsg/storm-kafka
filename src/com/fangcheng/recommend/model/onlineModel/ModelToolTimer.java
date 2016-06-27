package com.fangcheng.recommend.model.onlineModel;

import java.util.Calendar;
import java.util.Date;

import com.fangcheng.recommend.model.bean.DataModel;

public class ModelToolTimer extends Thread {
	public DataModel model = null;
	public boolean isUpdateModel = false;
	public boolean isStop = false;

	public Calendar cal = null;
	/**
	 * @param model
	 *            分类模块
	 * @param timeTimer
	 *            时间周期
	 */
	public ModelToolTimer(DataModel model, boolean isUpdateModel) {
		this.isUpdateModel = isUpdateModel;
		this.model = model;
		cal=Calendar.getInstance();
		this.date=getDate();
	}

	public int date = 0;

	@Override
	public void run() {
		while (true) {
			if (isStop) {
				break;
			}
			try {
				// 每分钟刷一次
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(model);
//			if (model != null) {
//				System.out.println(model + "\t" + model.timeUpdateModel + "\t"
//						+ isUpdateModel);
//				System.out.println(date+"\tdata:"+getDate());
//				date=12;
//			} else {
//				System.out.println("空");
//			}
			// 判断是否需要更新模型
			if (model != null && model.timeUpdateModel != 0L && isUpdateModel) {
				long current = System.currentTimeMillis();
				if (current - model.timeUpdateModel > model.timeDelay) {
					// 调用更新程序
					model.updateModel();
				}
			}
			// 每一个周期写一次模型写入hdfs
			int date2 =getDate();
			if (date2 != date) {
				date = date2;
				if (model != null) {
					model.write();
					System.out.println("写入文件");
				}
			}
		}
	}
	public int getDate(){
		
		return  cal.get(Calendar.DAY_OF_MONTH);
	}
}

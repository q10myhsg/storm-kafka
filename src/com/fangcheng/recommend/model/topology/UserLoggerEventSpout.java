package com.fangcheng.recommend.model.topology;

import java.util.Map;

import storm.trident.spout.ITridentSpout;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;

public class UserLoggerEventSpout implements ITridentSpout<Long>{

	private static final long serialVersionUID=1L;
	
	SpoutOutputCollector collector;
	
	BatchCoordinator<Long> coordinator= new DefaultCoordinator();
	Emitter<Long> emitter=new InputStreamEventEmitter();
	@Override
	public Map getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public storm.trident.spout.ITridentSpout.BatchCoordinator<Long> getCoordinator(
			String paramString, Map paramMap,
			TopologyContext paramTopologyContext) {
		// TODO Auto-generated method stub
		return coordinator;
	}

	@Override
	public storm.trident.spout.ITridentSpout.Emitter<Long> getEmitter(
			String paramString, Map paramMap,
			TopologyContext paramTopologyContext) {
		// TODO Auto-generated method stub
		return emitter;
	}

	@Override
	public Fields getOutputFields() {
		// TODO Auto-generated method stub
		return new Fields("event");
	}
	
}

package com.test.storm;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import storm.trident.spout.ITridentSpout;

public class DiagnosisEventSpout implements ITridentSpout<Long>{

	private static final long serialVersionUID=1L;
	
	SpoutOutputCollector collector;
	
	BatchCoordinator<Long> coordinator= new DefaultCoordinator();
	Emitter<Long> emitter=new DiagnosisEventEmitter();
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

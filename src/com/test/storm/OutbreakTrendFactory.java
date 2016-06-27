package com.test.storm;

import java.util.Map;

import backtype.storm.task.IMetricsContext;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

public class OutbreakTrendFactory implements StateFactory {
	@Override
	public State makeState(Map paramMap, IMetricsContext paramIMetricsContext,
			int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		 return new OutbreakTrendState(new OutbreakTrendBackingMap());
	}
}
package com.fangcheng.recommend.model.topology;

import com.fangcheng.recommend.model.bean.UserGraphBean;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

public class Count implements CombinerAggregator<Integer>{

	@Override
	public Integer combine(Integer paramT1, Integer paramT2) {
		// TODO Auto-generated method stub
		return paramT1+paramT2;
	}

	@Override
	public Integer init(TridentTuple paramTridentTuple) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Integer zero() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}

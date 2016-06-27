package com.test.storm;

import com.fangcheng.recommend.model.bean.UserGraphBean;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;


public class Count implements CombinerAggregator<Long>{

	@Override
	public Long combine(Long paramT1, Long paramT2) {
		// TODO Auto-generated method stub
		return paramT1+paramT2;
	}

	@Override
	public Long init(TridentTuple paramTridentTuple) {
		// TODO Auto-generated method stub
		return 1L;
	}

	@Override
	public Long zero() {
		// TODO Auto-generated method stub
		return 0L;
	}

	
}

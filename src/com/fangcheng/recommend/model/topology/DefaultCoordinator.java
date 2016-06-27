package com.fangcheng.recommend.model.topology;

import java.io.Serializable;

import storm.trident.spout.ITridentSpout.BatchCoordinator;

public class DefaultCoordinator implements BatchCoordinator<Long>,Serializable{

	@Override
	public Long initializeTransaction(long paramLong, Long paramX1, Long paramX2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void success(long paramLong) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReady(long paramLong) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}

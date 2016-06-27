package com.test.storm;

import backtype.storm.tuple.Fields;
import storm.trident.fluent.IAggregatableStream;
import storm.trident.operation.Aggregator;
import storm.trident.operation.Function;

public class Stream implements IAggregatableStream{

	@Override
	public IAggregatableStream each(Fields paramFields1,
			Function paramFunction, Fields paramFields2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fields getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAggregatableStream partitionAggregate(Fields paramFields1,
			Aggregator paramAggregator, Fields paramFields2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public storm.trident.Stream toStream() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

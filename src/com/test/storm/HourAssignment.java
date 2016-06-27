package com.test.storm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class HourAssignment extends BaseFunction{

//	private static final long serialVersionUID=1L;
//
//	@Override
//	public void execute(TridentTuple paramTridentTuple,
//			TridentCollector paramTridentCollector) {
//		// TODO Auto-generated method
//		DiagnosisEvent diagnosis=(DiagnosisEvent)paramTridentTuple.getValue(0);
//		String city=(String)paramTridentTuple.getValue(1);
//		long timestamp=diagnosis.time;
//		long hourSinceEpoch=timestamp/1000/60/60;
//		String key=city+":"+diagnosis.diagnosisCode+":"+hourSinceEpoch;
//		List<Object> values=new ArrayList<Object>();
//		values.add(hourSinceEpoch);
//		values.add(key);
//		paramTridentCollector.emit(values);
//	}

	
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
            LoggerFactory.getLogger(HourAssignment.class);

    @Override
    public void execute(TridentTuple tuple,
                        TridentCollector collector) {
        DiagnosisEvent diagnosis = (DiagnosisEvent) tuple.getValue(0);
        String city = (String) tuple.getValue(1);
        long timestamp = diagnosis.time;
        long hourSinceEpoch = timestamp / 1000 / 60 / 60;
        System.out.println("Key = [" + city + ":" + hourSinceEpoch + "]");
        LOG.debug("Key = [" + city + ":" + hourSinceEpoch + "]");
        String key = city + ":" + diagnosis.diagnosisCode + ":" + hourSinceEpoch;
        List<Object> values = new ArrayList<Object>();
        values.add(hourSinceEpoch);
        values.add(key);
        collector.emit(values);
    }
}

package com.test.storm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

public class DiagnosisEventEmitter implements Emitter<Long>,Serializable{

	private static final long serialVersionUID=1L;
	AtomicInteger successfulTransactions=new AtomicInteger(0);
	@Override
	public void emitBatch(TransactionAttempt paramTransactionAttempt,
			Long paramX, TridentCollector paramTridentCollector) {
		// TODO Auto-generated method stub
		for(int i=0;i<1000;i++)
		{
			List<Object> events=new ArrayList<Object>();
			double lat=new Double(-30+(int)(Math.random()*75));
			double lng=new Double(-120+(int)(Math.random()*70));
			long time=System.currentTimeMillis();
			String diag=new Integer(320+(int)(Math.random()*7)).toString();
			DiagnosisEvent event=new DiagnosisEvent(lat,lng,time,diag);
			events.add(event);
			paramTridentCollector.emit(events);
		}
	}

	@Override
	public void success(TransactionAttempt paramTransactionAttempt) {
		// TODO Auto-generated method stub
		successfulTransactions.incrementAndGet();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	
	
}

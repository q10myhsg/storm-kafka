package com.test.storm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFilter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class DiseaseFilter extends BaseFilter {
	 private static final Logger LOG =
	            LoggerFactory.getLogger(DiseaseFilter.class);
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare(Map paramMap,
			TridentOperationContext paramTridentOperationContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isKeep(TridentTuple paramTridentTuple) {
		// TODO Auto-generated method stub
		DiagnosisEvent diagnosis = (DiagnosisEvent) paramTridentTuple
				.getValue(0);
		Integer code = Integer.parseInt(diagnosis.diagnosisCode);
		if (code.intValue() <= 322) {
			System.out.println("Emitting disease [" + diagnosis.diagnosisCode + "]");
            LOG.debug("Emitting disease [" + diagnosis.diagnosisCode + "]");
            return true;
        } else {
        	System.out.println("Filtering disease [" + diagnosis.diagnosisCode + "]");
            LOG.debug("Filtering disease [" + diagnosis.diagnosisCode + "]");
            return false;
        }
	}

}

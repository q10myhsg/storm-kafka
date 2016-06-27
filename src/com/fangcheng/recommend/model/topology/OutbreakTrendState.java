package com.fangcheng.recommend.model.topology;

import storm.trident.state.map.NonTransactionalMap;

public class OutbreakTrendState extends NonTransactionalMap<Integer> {
    protected OutbreakTrendState(OutbreakTrendBackingMap outbreakBackingMap) {
        super(outbreakBackingMap);
    }
}
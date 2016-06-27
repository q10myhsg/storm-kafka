package com.fangcheng.recommend.model.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.state.map.IBackingMap;

import com.fangcheng.util.JsonUtil;

public class OutbreakTrendBackingMap implements
        IBackingMap<Integer> {
    private static final Logger LOG =
    LoggerFactory.getLogger(OutbreakTrendBackingMap.class);
    Map<String,Integer> storage = new ConcurrentHashMap<String, Integer>();
    @Override
    public List<Integer> multiGet(List<List<Object>> keys){
        List<Integer> values = new ArrayList<Integer>();
        for (List<Object> key : keys) {
            Integer value = storage.get(key.get(0));
            if (value==null){
                values.add(new Integer(0));
            } else {
                values.add(value);
            }
        }
        return values;
    }
    @Override
    public void multiPut(List<List<Object>> keys, List<Integer> vals) {
//    	System.out.println("groupby1:"+JsonUtil.getJsonStr(keys));
//    	System.out.println("groupby2:"+JsonUtil.getJsonStr(vals));
        for (int i=0; i < keys.size(); i++) {
        	//List<Object> zn=keys.get(i);
            storage.put((String) keys.get(i).get(0), (Integer)vals.get(i));
        }
    }
}
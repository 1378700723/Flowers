package org.guyou.util;

import java.util.HashMap;
import java.util.Map;

public class NumberHashMap<K, V extends Number> extends HashMap<K, Number> {
	private static final long serialVersionUID = -4586285812385217705L;

    public NumberHashMap() {
    	super();
    }
    
    public NumberHashMap(int initialCapacity) {
    	super(initialCapacity);
    }
    
    public NumberHashMap(int initialCapacity, float loadFactor) {
    	super(initialCapacity, loadFactor);
    }
    
    public NumberHashMap(Map<K,Number> m) {
    	super(m);
    }
	
	@Override
	public Number get(Object key) {
        if(super.containsKey(key)){
        	return super.get(key);
        }
		return new Integer(0);
    }
	
	@Override
	public Number put(K key,Number value){
		if((value instanceof Byte)
				|| (value instanceof Short)
				|| (value instanceof Integer)
				|| (value instanceof Float)
				|| (value instanceof Long)
				|| (value instanceof Double)){
			return super.put(key, value);
		}
		return super.put(key, Double.valueOf(value.toString()));
	}
}
/**
 * 
 */
package org.guyou.util;

import java.util.HashMap;

/**
 * @author 朱施健
 * 
 */
public class MyLazyMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -6816252018583575505L;
	private ObjectFactory<V> _vFactory;

	public MyLazyMap(ObjectFactory<V> vFactory) {
		_vFactory = vFactory;
	}

	@Override
	public V get(Object key) {
		if(!containsKey(key)){
			super.put((K)key, _vFactory.createElement());
		}
		return super.get(key);
	}
	
	public void destroy(){
		_vFactory = null;
		super.clear();
	}
}

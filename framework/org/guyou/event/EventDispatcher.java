/**
 * created by zhusj
 */
package org.guyou.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhusj
 * 事件派发器
 * 用于派发消息时间，系统内部事件
 */
public abstract class EventDispatcher<E extends Event>{
	
	//事件列表
	private Map<String,IEventHandler<E>> _eventMap = null;

	public EventDispatcher() {
		_eventMap = new ConcurrentHashMap<String,IEventHandler<E>>();
	}
	
	/**
	 * 注册监听器
	 * @param type 监听名称
	 * @param callback 监听器实例
	 */
	public void addEventListener(String type, IEventHandler<E> callback) {
		_eventMap.put(type, callback);
	}
	
	/**
	 * 移除监听
	 * @param type 监听名称
	 */
	public IEventHandler<E> removeEventListener(String type) {
		return _eventMap.remove(type);
	}
	
	/**
	 * 是否有制定名称的事件
	 * @param type
	 * @return
	 */
	public boolean hasEventListener(String type) {
		return _eventMap.containsKey(type);
	}

	/**
	 * 派发事件
	 * @param e 事件
	 */
	public abstract boolean dispatchEvent(E e,boolean isRightNow) throws Exception;

	public IEventHandler<E> getEventHandler(String type) {
		return _eventMap.get(type);
	}
}

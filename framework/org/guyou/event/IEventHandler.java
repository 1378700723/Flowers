/**
 * created by zhusj
 */
package org.guyou.event;


/**
 * @author zhusj
 *
 */
public interface IEventHandler<E extends Event>{
	/**
	 * 处理事件
	 * @param e
	 */
	public void doExecute(E e) throws Exception;
}

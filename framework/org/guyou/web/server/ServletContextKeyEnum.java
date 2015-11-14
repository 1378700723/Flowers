/**
 * create by 朱施健
 */
package org.guyou.web.server;


/**
 * @author 朱施健
 *
 */
public class ServletContextKeyEnum {
	/**
	 * 验证session的URI
	 */
	public static final String CHECK_SESSION_URI_LIST = "CHECK_SESSION_URI_LIST"+System.currentTimeMillis();
	/**
	 * 自定义session编号
	 */
	public static final String USER_DEFINED_SESSION_CLEAN_TIMER = "USER_DEFINED_SESSION_CLEAN_TIMER"+System.currentTimeMillis();
	
	/**
	 * 业务线程池容器
	 */
	public static final String THREADPOOL_EXECUTOR_CONTAINER = "THREADPOOL_EXECUTOR_CONTAINER"+System.currentTimeMillis();
	
	/**
	 * 逻辑线程池名称
	 */
	public static final String LOGIC_THREADPOOL_NAME = "LOGIC_THREADPOOL_NAME"+System.currentTimeMillis();
}

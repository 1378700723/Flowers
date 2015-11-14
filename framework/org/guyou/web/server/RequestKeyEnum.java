/**
 * create by 朱施健
 */
package org.guyou.web.server;

/**
 * @author 朱施健
 *
 */
public class RequestKeyEnum {
	/**
	 * 客户端类型
	 */
	public static final String CLIENT_TYPE = "CLIENT_TYPE_"+System.currentTimeMillis();
	/**
	 * 请求触发时间点
	 */
	public static final String REQUEST_TRIGGER_TIME = "REQUEST_TRIGGER_TIME_"+System.currentTimeMillis();
	/**
	 * 请求执行时间点
	 */
	public static final String REQUEST_EXCUTE_TIME = "REQUEST_EXCUTE_TIME_"+System.currentTimeMillis();
}

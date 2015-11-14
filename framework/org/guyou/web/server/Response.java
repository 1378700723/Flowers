/**
 * create by 朱施健
 */
package org.guyou.web.server;

import org.guyou.util.StringUtil;

/**
 * @author 朱施健
 *
 */
public final class Response {
	final Object returnObj;
	final ResponseType type;
	
	private Response(Object returnObj,ResponseType type){
		this.returnObj = returnObj;
		this.type = type;
	}
	
	public static Response forward(String forward){
		if(StringUtil.isNullValue(forward)) throw new NullPointerException("定向目标地址为空");
		return new Response(forward,ResponseType.forward);
	}
	
	public static Response stationary(Object responseObj){
		if(responseObj==null) throw new NullPointerException("页面响应内容为空");
		return new Response(responseObj,ResponseType.stationary);
	}
	
	public static Response redirect(String redirect){
		if(redirect==null) throw new NullPointerException("跳转目标地址为空");
		return new Response(redirect,ResponseType.redirect);
	}
	
	enum ResponseType{
		forward,redirect,stationary;
	}
}

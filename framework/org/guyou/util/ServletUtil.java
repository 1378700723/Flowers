/**
 * 
 */
package org.guyou.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 朱施健
 *
 */
public class ServletUtil {
	//android : 所有android设备 mac os : iphone ipad windows phone:Nokia等windows系统的手机
	private static final String[] DEVICE_ARRAY = new String[] {"android", "mac os","windows phone"};
	
	public static String getUrl(HttpServletRequest request) {
		StringBuffer sb = request.getRequestURL();
		String queryString = request.getQueryString();
		if (!StringUtil.isNullValue(queryString)) {
			sb.append("?").append(queryString);
		}
		return sb.toString();
	}

	public static String getWebRootPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath();
	}

	public static String getUrlType(HttpServletRequest request, String type)
			throws UnsupportedEncodingException {
		String url = URLDecoder.decode(request.getQueryString(), "UTF-8");
		String gettype = "";
		String[] urllist = null;
		try {
			urllist = url.split("&");
		} catch (NullPointerException e) {
			return "";
		}
		for (int s = 0; s < urllist.length; s++) {
			int index = urllist[s].indexOf("=");
			String key = index==-1?urllist[s]:urllist[s].substring(0,index);
			String value = index==-1?"":urllist[s].substring(index+1);
			if (key.equals(type)) {
				gettype = value;
				break;
			}
		}
		return gettype;
	}
	
	public static Map<String,String> getUrlType(String queryString)
			throws UnsupportedEncodingException {
		String url = URLDecoder.decode(queryString, "UTF-8");
		Map<String,String> r = new HashMap<String, String>();
		
		String[] urllist = null;
		try {
			urllist = url.split("&");
		} catch (NullPointerException e) {
			return r;
		}
		for (int s = 0; s < urllist.length; s++) {
			int index = urllist[s].indexOf("=");
			String key = index==-1?urllist[s]:urllist[s].substring(0,index);
			String value = index==-1?"":urllist[s].substring(index+1);
			r.put(key,value);
		}
		return r;
	}

	
	public static enum ClientType{
		PC_BROWSER("PC_BROWSER"),
		PC_APPLICATION("PC_APPLICATION"),
		MOBILE_BROWSER("MOBILE_BROWSER"),
		MOBILE_APPLICATION("MOBILE_APPLICATION"),
		;
		public final String user_agent;
		ClientType(String user_agent){
			this.user_agent = user_agent;
		}
		
		@Override
		public String toString(){
			return user_agent;
		}
	}
	
	/**
	 * 获得http客户端类型
	 * @param request
	 * @return
	 */
	public static ClientType getHttpClientType(HttpServletRequest request,String user_agent) {
		if (user_agent == null) return ClientType.PC_APPLICATION;
		user_agent = user_agent.toLowerCase();
		for (int i = 0; i < DEVICE_ARRAY.length; i++) {
			if (user_agent.contains(DEVICE_ARRAY[i])) {
				return ClientType.MOBILE_BROWSER;
			}
		}
		if(user_agent.contains("mobile_app")){
			return ClientType.MOBILE_APPLICATION;
		}
		if(user_agent.contains("mozilla")){
			return ClientType.PC_BROWSER;
		}
		return ClientType.PC_APPLICATION;
	}
}

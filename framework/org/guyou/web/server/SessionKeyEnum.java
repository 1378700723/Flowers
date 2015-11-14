/**
 * create by 朱施健
 */
package org.guyou.web.server;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author 朱施健
 *
 */
public class SessionKeyEnum {
	/**
	 * 自定义session编号
	 */
	public static final String USER_DEFINED_SESSION_ID = "USER_DEFINED_SESSION_ID";
	
	/**
	 * 客户数据
	 */
	public static final String CUSTOMER_USER_DATA = "CUSTOMER_USER_DATA";
	
	/**
	 * 管理员数据
	 */
	public static final String ADMIN_USER_DATA = "ADMIN_USER_DATA";
	
	
	private static Set<String> ____keys_set = null;
	public static final Set<String> keys() {
		if(____keys_set==null){
			try {
				Field[] fs = SessionKeyEnum.class.getDeclaredFields();
				____keys_set = new HashSet<String>();
				for (Field f : fs) {
					if(f.toString().contains("public static final") && f.getType()==String.class && !f.getName().startsWith("_")){
						____keys_set.add((String)f.get(SessionKeyEnum.class));
					}
				}
			} catch (IllegalArgumentException e) {
				Logger.getLogger(SessionKeyEnum.class).error("获得SessionKeys异常",e);
			} catch (IllegalAccessException e) {
				Logger.getLogger(SessionKeyEnum.class).error("获得SessionKeys异常",e);
			}
		}
		return ____keys_set;
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		System.out.println(StringUtils.join(keys()));
		System.out.println(StringUtils.join(keys()));
	}
}

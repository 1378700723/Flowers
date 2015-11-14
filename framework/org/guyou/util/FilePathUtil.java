/**
 * created by jinmiao
 */
package org.guyou.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @description:
 * 游戏配置文件路径工具
 * @author zhusj
 * @date 2011-7-13
 * @version 1.0
 * @keyword:
 */
public class FilePathUtil {
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle("dataFilePath");
	
	
	/**
	 * 获得文件路径
	 * @param key
	 * @return String
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

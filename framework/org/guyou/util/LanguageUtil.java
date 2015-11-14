/**
 * created by jinmiao
 */
package org.guyou.util;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author zhusj
 * 多语言工具
 */
public class LanguageUtil {
	
	private static final Logger log = Logger.getLogger(LanguageUtil.class);
	
	private static String _resource;
	private static Map<String,String> _config = null;
	
	protected static void init(String resource) throws URISyntaxException, FileNotFoundException{
		if(_resource!=null) return ;
		_resource = resource;
		_config = FileUtil.getResourceBundleByRelativePath(resource);
	}
	
	/**
	 * 获得游戏应用设置参数
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		String value =  _config.get(key);
		if(value==null){
			log.error("资源文件<"+_resource+">中不存在["+key+"]指定的配置");
			return '!' + key + '!';
		}
		return value;
	}
	
	
	/**
	 * 给一句话字段赋值
	 * @param str
	 * @param vars
	 * @return
	 */
	public static String pushVar(String str, Object... vars) {
		for (int i = 0; i < vars.length; i++) {
			str = str.replace("{"+i+"}", vars[i].toString());
		}
		return str;
	}
}

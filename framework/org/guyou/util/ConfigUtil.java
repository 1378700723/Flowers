/**
 * 
 */
package org.guyou.util;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * @author zhushijian
 * 
 */
public class ConfigUtil {
	
	private static final Logger log = Logger.getLogger(ConfigUtil.class);
	
	private static String _resource;
	private static Map<String,String> _config = null;
	
	public static void configure(String contextPath,String resource) throws URISyntaxException, FileNotFoundException{
		if(_resource!=null) return ;
		_resource = resource;
		_config = FileUtil.getResourceBundleByRelativePath(resource);
		
		for (Entry<String,String> e : _config.entrySet()) {
			if(e.getValue().contains("${ContextPath}")){
				e.setValue(e.getValue().replace("${ContextPath}", contextPath));
			}
		}
	}
	
	/**
	 * 获得设置参数
	 * @param key
	 * @return
	 */
	public static String getConfigParam(String key) {
		String value =  _config.get(key);
		if(value==null){
			log.error("资源文件<"+_resource+">中不存在["+key+"]指定的配置");
			return '!' + key + '!';
		}
		return value;
	}
}

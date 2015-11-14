package org.guyou.util.excel;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * excel解析父类
 * @author 王烁
 * @date 2012-11-14
 * @version 1.0
 *
 */
public abstract class BaseParse<T extends IExcelBean> {

	private static final Logger log = Logger.getLogger(BaseParse.class);
	protected Map<String,T> data = null;
	protected List<T> dataList = null;
	protected Map<String, Map<Object, List<T>>> indexByFieldValues = new HashMap<String, Map<Object, List<T>>>();
	protected File xlsFile;
	protected long lastModified;
	
	private List<T> getCache(String fieldName, Object value) {
		Map<Object, List<T>> xxx = indexByFieldValues.get(fieldName);
		if (xxx == null) return null;
		
		return xxx.get(value);
	}
	
	private void insertCache(String fieldName, Object value, List<T> list) {
		Map<Object, List<T>> xxx = indexByFieldValues.get(fieldName);
		if (xxx == null) indexByFieldValues.put(fieldName, xxx = new HashMap<Object, List<T>>());
		
		xxx.put(value, list);
	}
	
	/**
	 * 通过id单一获取模板行数据
	 * @param id 第一列
	 * @return 如果返回空则代表excel中没有对应id的数据行
	 */
	protected T _getTemplate(String id) {
		
		T bean = data.get(id);
		
		if(bean == null) {
			log.error(this.getClass().getName() + "中没有找到 id为:" + id + "的数据",new NullPointerException(this.getClass().getName() + "中没有找到 id为:" + id + "的数据"));
		}
		return bean;
	}
	
	/**
	 * 获得一批模板数据
	 * @param ids 需要获取模板的id
	 * @return 如果返回空 则代表 表 参数 出现异常
	 */
	protected List<T> _getTemplates(String... ids) {
		
		List<T> list = new ArrayList<T>();
		
		if(ids == null || ids.length <= 0) return list;
		
		for(int i = 0 ; i < ids.length ;i++) {
			
			T bean = data.get(ids[i]);
			
			if(bean == null) {
				log.error(this.getClass().getName() + "中没有找到 id为:" + ids[i] + "的数据",new NullPointerException(this.getClass().getName() + "中没有找到 id为:" + ids[i] + "的数据"));
				continue;
			}
			
			list.add(bean);
		}
		
		return list;
	}
	
	/**
	 * 获得excel中的全部信息
	 * @return excel中的全部信息
	 */
	protected Map<String,T> _getAllTemplates() {
		return data;
	}
	
	protected List<T> _getAllTemplateList() {
		return dataList;
	}
	
	/**
	 * 通过指定字段名称 对excel的数据进行筛选
	 * @param clazz 
	 * @param filedName 要筛选的属性名称
	 * @param value 匹配的值
	 * @return
	 * @throws IllegalArgumentException 调用属性时 参数不匹配异常(不会发生)
	 * @throws IllegalAccessException 调用属性时 访问权限异常(当访问到私有属性变量时发生)
	 * @throws SecurityException 安全沙箱异常
	 * @throws NoSuchFieldException 传入的 filedName 在对象中不包含异常
	 */
	protected List<T> _getTemplateByField(Class<?> clazz, String filedName, Object value) {
		List<T> list = new ArrayList<T>();
		
		if(value == null || clazz == null || filedName == null || filedName.equals("")) return list;
		
		boolean isCache = false;
		isCache = value instanceof String || value instanceof Integer 
				|| value instanceof Short || value instanceof Byte || value instanceof Float || value instanceof Double;
	
		try {
			//获得指定的属性对象
			Field resultField = clazz.getField(filedName);
			if (isCache) {
				List<T> inCache = getCache(filedName, value);
				if (inCache != null) { 
					list.addAll(inCache);
					return list;
				}
			}
			
			for ( T template : dataList ) {
				Object tempValue = resultField.get(template);
				if(tempValue == null) continue;
				if(value.toString().equals(tempValue.toString())) {
					list.add(template);
				}
			}
			
			if (isCache) {
				List<T> inCache = new ArrayList<T>(list.size());
				inCache.addAll(list);
				insertCache(filedName, value, inCache);
			}
			return list;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 通过指定字段名称 对excel的数据进行筛选
	 * @param clazz 
	 * @param filedName 要筛选的属性名称
	 * @param value 匹配的值
	 * @return
	 * @throws IllegalArgumentException 调用属性时 参数不匹配异常(不会发生)
	 * @throws IllegalAccessException 调用属性时 访问权限异常(当访问到私有属性变量时发生)
	 * @throws SecurityException 安全沙箱异常
	 * @throws NoSuchFieldException 传入的 filedName 在对象中不包含异常
	 */
	protected List<T> _getTemplateByField(Class<T> clazz, FieldValueCondition[] condititons) {
	
		List<T> list = new ArrayList<T>();
		
		if(clazz == null && !FieldValueCondition.paramsCheck(condititons) ) {
			return list;
		}
		
		try {
			int len = condititons.length;
			Field[] resultField = new Field[len];
			for (int i = 0; i < len; i++) {
				resultField[i] = clazz.getField(condititons[i].Field);
			}
			
			for ( T template : dataList ) {
				Object[] tempValue = new Object[len];
				boolean flag = false;
				for (int i = 0; i < len; i++) {
					tempValue[i] = resultField[i].get(template);
					if(tempValue[i]==null){
						flag = true;
						break;
					}
				}
				if(flag) continue;
				flag = true;
				for (int i = 0; i < len; i++) {
					if(!condititons[i].value.toString().equals(tempValue[i].toString())) {
						flag = false;
						break;
					}
				}
				if(flag) list.add(template);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return list;
	}
}

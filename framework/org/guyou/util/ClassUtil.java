/**
 * created by 朱施健 
 */
package org.guyou.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author 朱施健
 * 
 */
public class ClassUtil {
	
	private static final Logger log = Logger.getLogger(ClassUtil.class);
	
	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * @return
	 */
	private static Set<Class<?>> getClasses(String pack) {
		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = null;
										if("".equals(packageName)){
											className = name.substring(packageName.length() + 1, name.length() - 6);
										}else{
											className = packageName + '.' + name.substring(packageName.length() + 1, name.length() - 6);
										}
										if(className==null || "".equals(className)) continue;
										try {
											Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
											// 添加到classes
											classes.add(clazz);
										} catch (Throwable e) {
											log.error("获得Class时异常", e);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						log.error("在扫描用户定义视图时从jar包获取文件出错", e);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			@Override
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile("".equals(packageName) ? file.getName() : (packageName + "." + file.getName()), file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = null;
				if("".equals(packageName)){
					className = file.getName().substring(0, file.getName().length() - 6);
				}else{
					className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				}
				if(className==null || "".equals(className)) continue;
				try {
					Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
					// 添加到classes
					classes.add(clazz);
				} catch (Throwable e) {
					log.error("获得Class时异常", e);
				}
			}
		}
	}
	
	public static Set<Class<?>> getClasses(String... packages){
		if(packages.length==0 || ArrayUtils.contains(packages, "")) return getClasses("");
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for (String pack : packages) {
			if(pack!=null){
				classes.addAll(getClasses(pack));
			}
		}
		return classes;
	}
	
	public static Set<Class<?>> getSubClassList(Class<?> superClass,String... packages){
		Set<Class<?>> set = getClasses(packages);
		for (Iterator<Class<?>> iterator = set.iterator(); iterator.hasNext();) {
			Class<?> clazz = iterator.next();
			if(!superClass.isAssignableFrom(clazz) || superClass==clazz){
				iterator.remove();
			}
		}
		return set;
	}
	
	public static Set<Class<?>> getSubClassList(Class<?> superClass,Collection<Class<?>> classList){
		Set<Class<?>> set = new LinkedHashSet<Class<?>>();
		for (Iterator<Class<?>> iterator = classList.iterator(); iterator.hasNext();) {
			Class<?> clazz = iterator.next();
			if(superClass.isAssignableFrom(clazz) && superClass!=clazz){
				set.add(clazz);
			}
		}
		return set;
	}
	
	public static Set<Class<?>> getAnnotationClassList(Class<?> annotationClass,String... packages){
		Set<Class<?>> set = getClasses(packages);
		for (Iterator<Class<?>> iterator = set.iterator(); iterator.hasNext();) {
			Class<?> clazz = iterator.next();
			boolean flag = false;
			Annotation[] annotations = clazz.getAnnotations();
			for(Annotation annotation : annotations){
	            //判斷當前注解對象是否為自定義注解
	            if(annotation.annotationType() == annotationClass){
	            	flag = true;
	            	break;
	            }
	        }
			if(!flag || annotationClass==clazz){
				iterator.remove();
			}
		}
		return set;
	}
	
	public static Set<Class<?>> getAnnotationClassList(Class<?> annotationClass,Collection<Class<?>> classList){
		Set<Class<?>> set = new LinkedHashSet<Class<?>>();
		for (Iterator<Class<?>> iterator = classList.iterator(); iterator.hasNext();) {
			Class<?> clazz = iterator.next();
			boolean flag = false;
			Annotation[] annotations = clazz.getAnnotations();
			for(Annotation annotation : annotations){
	            //判斷當前注解對象是否為自定義注解
	            if(annotation.annotationType() == annotationClass){
	            	flag = true;
	            	break;
	            }
	        }
			if(flag && annotationClass!=clazz){
				set.add(clazz);
			}
		}
		return set;
	}
	
	public static void setFieldValue(Object obj,String fieldName,Object value){
		boolean isClass = (obj instanceof Class);
		try {
			Class<?> clazz = isClass ? (Class<?>)obj : obj.getClass();
			Field field = getField(clazz,fieldName);
			if(field==null){
				throw new NoSuchFieldException();
			}
			if(!field.isAccessible())field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			log.error("设置对象属性值时异常", e);
			e.printStackTrace();
		}
	}
	
	public static Object getFieldValue(Object obj,String fieldName){
		boolean isClass = (obj instanceof Class);
		try {
			Class<?> clazz = isClass ? (Class<?>)obj : obj.getClass();
			Field field = getField(clazz,fieldName);
			if(field==null){
				throw new NoSuchFieldException();
			}
			if(!field.isAccessible())field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			log.error("设置对象属性值时异常", e);
		}
		return null;
	}
	
	public static void setFieldValue(Object obj,Class<?> fieldClass,Object value){
		boolean isClass = (obj instanceof Class);
		try {
			Class<?> clazz = isClass ? (Class<?>)obj : obj.getClass();
			List<Field> fieldList = new ArrayList<Field>();
			getField(clazz,fieldClass,fieldList);
			if(fieldList.size()!=1){
				throw new NoSuchFieldException(clazz.getName()+"类中定义为"+fieldClass.getName()+"的属性不存在或者不唯一!");
			}
			Field field = fieldList.get(0);
			if(!field.isAccessible())field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			log.error("设置对象属性值时异常", e);
		}
	}
	
	public static Object getFieldValue(Object obj,Class<?> fieldClass){
		boolean isClass = (obj instanceof Class);
		try {
			Class<?> clazz = isClass ? (Class<?>)obj : obj.getClass();
			List<Field> fieldList = new ArrayList<Field>();
			getField(clazz,fieldClass,fieldList);
			if(fieldList.size()!=1){
				throw new NoSuchFieldException(clazz.getName()+"类中定义为"+fieldClass.getName()+"的属性不存在或者不唯一!");
			}
			Field field = fieldList.get(0);
			if(!field.isAccessible())field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			log.error("设置对象属性值时异常", e);
		}
		return null;
	} 
	
	
	private static void getField(Class<?> clazz,Class<?> fieldClass,List<Field> fieldList){
		try {
			if(!clazz.isInterface() && clazz!=Object.class){
				Field[] fields = clazz.getDeclaredFields();
				for ( Field field : fields ) {
					if(fieldClass.isAssignableFrom(field.getType())){
						fieldList.add(field);
					}
				}
				getField(clazz.getSuperclass(),fieldClass,fieldList);
			}
		} catch ( Exception e ) {
			log.error("获得对象Field时异常", e);
		}
	}
	
	public static Field getField(Class<?> clazz,String fieldName){
		try {
			Field field = clazz.getDeclaredField(fieldName);
			return field;
		} catch ( Exception e ) {
			Class<?> superClazz = clazz.getSuperclass();
			if(superClazz.isInterface() || superClazz == Object.class){
				return null;
			}else{
				return getField(superClazz,fieldName);
			}
		}
	}
	
	public static Object excuteFunction(Object obj,String funcitonName,Object... parameter ){
		boolean isClass = (obj instanceof Class);
		Class<?> clazz = isClass ? (Class<?>)obj : obj.getClass();
		try{
			Class<?>[] parameterTypes = new Class<?>[parameter.length];
			for ( int i = 0 ; i < parameter.length ; i++ ) {
				parameterTypes[i] = paramConvert(parameter[i].getClass());
			}
			Method m = getMethod(clazz,funcitonName,parameterTypes);
			if(m==null){
				throw new NoSuchMethodException();
			}
			if(!m.isAccessible()) m.setAccessible(true);
			return m.invoke(obj, parameter);
		}catch(Exception e){
			if(e instanceof InvocationTargetException){
				Throwable tr = ((InvocationTargetException)e).getTargetException();
				log.error("执行对象的方法时异常!", tr);
			}else{
				log.error("执行对象的方法时异常!", e);
			}
			return null;
		}
	}
	
	public static Method getMethod(Class<?> clazz,String functionName,Class<?>[] parameterTypes){
		try {
			Method function = clazz.getDeclaredMethod(functionName, parameterTypes);
			return function;
		} catch ( Exception e ) {
			if(parameterTypes.length>0){
				Method[] fList = clazz.getDeclaredMethods();
				loop:
				for (Method md : fList) {
					Class<?>[] paramTypes = md.getParameterTypes();
					if(!md.getName().equals(functionName)) continue loop;
					if(paramTypes.length!=parameterTypes.length) continue loop;
					for (int i = 0; i < parameterTypes.length;i++) {
						if(!paramTypes[i].isAssignableFrom(parameterTypes[i])) continue loop;
					}
					return md;
				}
			}
			
			Class<?> superClazz = clazz.getSuperclass();
			if(superClazz == Object.class){
				return null;
			}else{
				return getMethod(superClazz,functionName,parameterTypes);
			}
		}
	}
	
	public static <T> T newInstance(Class<T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if(!constructor.isAccessible()) constructor.setAccessible(true);
			Class<?>[] types = constructor.getParameterTypes();
			Object[] params = new Object[types.length];
			for (int i = 0; i < types.length; i++) {
				if(types[i]==byte.class){
					params[i] = (byte)0;
				}else if(types[i]==short.class){
					params[i] = (short)0;
				}else if(types[i]==int.class){
					params[i] = 0;
				}else if(types[i]==float.class){
					params[i] = 0f;
				}else if(types[i]==double.class){
					params[i] = 0d;
				}else if(types[i]==long.class){
					params[i] = 0L;
				}else if(types[i]==boolean.class){
					params[i] = false;
				}
			}
			return (T)constructor.newInstance(params);
		}
		return null;
	}
	
	public static boolean isBaseDataCalss(Class<?> clazz){
		return clazz==Byte.class || clazz==byte.class
				|| clazz==Short.class || clazz==short.class
				|| clazz==Integer.class || clazz==int.class
				|| clazz==Long.class || clazz==long.class
				|| clazz==Float.class || clazz==float.class
				|| clazz==double.class || clazz==double.class
				|| clazz==Boolean.class || clazz==boolean.class
				|| clazz==Character.class || clazz==char.class
				|| clazz==String.class;
				
	}
	
	public static Class<?> paramConvert(Class<?> clazz) {
		if (clazz==Integer.class) {
			return int.class;
		} else if (clazz==Long.class) {
			return long.class;
		} else if (clazz==Float.class) {
			return float.class;
		} else if (clazz==Double.class) {
			return double.class;
		} else if (clazz==Byte.class) {
			return byte.class;
		} else if (clazz==Short.class) {
			return short.class;
		} else if (clazz==Boolean.class) {
			return boolean.class;
		} else if (clazz==Character.class) {
			return char.class;
		} else {
			return clazz;
		}
	}
	
	private static String getClasspath(){
		String osName = System.getProperty("os.name");
		String classpath = osName.indexOf("Linux")!=-1 ? System.getProperty("java.class.path").replace(":", ";") : System.getProperty("java.class.path");
		return classpath;
	}
}

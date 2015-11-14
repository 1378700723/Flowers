package org.guyou.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

public class PoolManager {
	private static Logger logger = Logger.getLogger(PoolManager.class);
	private static PoolManager instance;
	private Hashtable<String,BasicDataSource> pools = new Hashtable<String,BasicDataSource>();
	private Set<String> dbNames = new HashSet<String>();

	private PoolManager(String dbPropertyName){
		try {
			ResourceBundle rb = ResourceBundle.getBundle(dbPropertyName, Locale.getDefault());
			createPools(rb);
		} catch (Exception e) {
			logger.error("Can not read the properties file. Make sure db.properties is in the CLASSPATH",e);
		}
	}
	
	private static synchronized PoolManager getInstance() {
		if (instance == null) {
			throw new NullPointerException("DbPoolManager实例不存在");
		}
		return instance;
	}
	
	public static void init(String dbpropertyName) {
		if(dbpropertyName==null || dbpropertyName.length()==0) throw new NullPointerException("初始化数据库连接池时未指定配置文件");
		instance = new PoolManager(dbpropertyName);
	}

	public static Connection getConnection(String name) {
		BasicDataSource bds =null;
		try {
			bds = getInstance().pools.get(name);
			Connection conn = null;
			if (bds != null) {
				conn = bds.getConnection();
			}else{
				logger.info("未设置"+name+"数据库配置...\n" +
				 "请在properties配置文件中添加如下形式的配置：\n" +
				 name+".url=jdbc:mysql://ip/databaseName?useunicode=true&characterEncoding=utf8\n" +
				 name+".drivers=XXX.Driver\n" +
				 name+".user=username\n" +
				 name+".password=password\n" +
				 name+".maxconn=maxconn\n");
			}
			return conn;
		} catch (Exception e) {
			logger.error("连接池中已经没有连接了...",e);
			Connection conn=null;
			try {
				conn=DriverManager.getConnection(bds.getUrl(),bds.getUsername(),bds.getPassword());	 
				return conn;
			} catch (Exception e1) {
				logger.error("获取数据库连接异常!!",e1);
				return null;
			}
		}
	}

	public static Connection getConnection() throws SQLException {
		BasicDataSource bds =null;
		try {
			bds = getInstance().pools.get("defaultDB");
			Connection conn = null;
			if (bds != null) {
				conn = bds.getConnection();
			}else{
				logger.info("未设置默认数据库配置...\n" +
							 "请在properties配置文件中进行如下形式的配置：\n" +
							 "defaultDB.url=jdbc:mysql://ip/databaseName?useunicode=true&characterEncoding=utf8\n" +
							 "defaultDB.drivers=XXX.Driver\n" +
							 "defaultDB.user=username\n" +
							 "defaultDB.password=password\n" +
							 "defaultDB.maxconn=maxconn\n");
			}
			return conn;
		} catch (SQLException e) {
			logger.error("连接池中已经没有连接了...");
			Connection conn=null;
			try {
				conn=DriverManager.getConnection(bds.getUrl(),bds.getUsername(),bds.getPassword());
				return conn;
			} catch (SQLException e1) {
				logger.error("创建额外连接时异常",e1);
				throw e1;
			}
		}
	}

	public synchronized static void release() {
		for ( Iterator<Entry<String,BasicDataSource>> iterator = getInstance().pools.entrySet().iterator(); iterator.hasNext() ; ) {
			Entry<String,BasicDataSource> e = iterator.next();
			try {
				e.getValue().close();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
				logger.error("销毁数据库连接池["+e.getKey()+"]时异常",e1);
			}
		}
	}

	private void createPools(ResourceBundle rb) {
		Enumeration<String> propNames = rb.getKeys();
		while (propNames.hasMoreElements()) {
			String name = propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.indexOf("."));
				if(pools.contains(poolName)){
					continue;
				}
				//defaultDB.url=jdbc:mysql://192.168.0.90:3306/hchl2?createDatabaseIfNotExist=true&useunicode=true&characterEncoding=utf8
				String url = rb.getString(name);
				if (url == null) {
					continue;
				}
				int whIndex = url.lastIndexOf("?");
				String dbName = url.substring(url.lastIndexOf("/")+1,whIndex==-1 ? url.length() : whIndex );
				
				if(dbNames.contains(dbName)){
					continue;
				}
					
				String drivername = getValueFromResourceBundleByKey(rb,poolName + ".drivers");
				String user = getValueFromResourceBundleByKey(rb,poolName + ".user");
				String password = getValueFromResourceBundleByKey(rb,poolName + ".password");
				String maxconn = getValueFromResourceBundleByKey(rb,poolName + ".maxconn");
				String InitialSize = getValueFromResourceBundleByKey(rb,poolName + ".InitialSize");
				String maxIdle = getValueFromResourceBundleByKey(rb,poolName + ".maxIdle");
				String minIdle = getValueFromResourceBundleByKey(rb,poolName + ".minIdle");
				String maxWait = getValueFromResourceBundleByKey(rb,poolName + ".maxWait");
				String removeAbandoned = getValueFromResourceBundleByKey(rb,poolName + ".removeAbandoned");
				String removeAbandonedTimeout = getValueFromResourceBundleByKey(rb,poolName + ".removeAbandonedTimeout");
				String logAbandoned = getValueFromResourceBundleByKey(rb,poolName + ".logAbandoned");

				try {
					BasicDataSource dataSource = null;
					Properties p = new Properties();
					p.setProperty("driverClassName", drivername);
					p.setProperty("url", url);
					p.setProperty("password", password);
					p.setProperty("username", user);
					p.setProperty("maxActive", maxconn);
					p.setProperty("InitialSize", InitialSize.length()>0?InitialSize:"30");
					p.setProperty("maxIdle", maxIdle.length()>0?maxIdle:"50");
					p.setProperty("minIdle", minIdle.length()>0?minIdle:"30");
					p.setProperty("maxWait", maxWait.length()>0?maxWait:"9000");// maxWait代表当Connection用尽了，多久之后进行回收丢失连接
					p.setProperty("removeAbandoned", removeAbandoned.length()>0?removeAbandoned:"true");// 是否自动回收超时连接
					p.setProperty("removeAbandonedTimeout", removeAbandonedTimeout.length()>0?removeAbandonedTimeout:"6");// 超时时间(以秒数为单位)
					p.setProperty("logAbandoned", logAbandoned.length()>0?logAbandoned:"false");
					p.setProperty("testOnBorrow", "true");
					p.setProperty("testOnReturn", "true");
					p.setProperty("testWhileIdle", "true");
					p.setProperty("validationQuery","select count(*) from dual");
					dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
					
					Connection conn = null;
					if (dataSource != null) {
						conn = dataSource.getConnection();
						conn.close();
					}
					pools.put(poolName, dataSource);
					dbNames.add(dbName);
				} catch (Exception ex) {
					logger.error("createPools exception!!",ex);
				} finally {

				}
			}
		}
	}
	
	private String getValueFromResourceBundleByKey(ResourceBundle rb,String key) {
		String value="";
		try{
			if(rb.containsKey(key)){
				value=rb.getString(key);
			}	
		}catch (Exception ex) {
		}
		return value;
	}

	public static String getConNum(String name) {
		BasicDataSource bds = getInstance().pools.get(name);
		return "NumActive=" + bds.getNumActive() + "  NumIdle="
				+ bds.getNumIdle() + "  NumTestsPerEvictionRun="
				+ bds.getNumTestsPerEvictionRun();
	}

	public static int getMaxCons(String name) {
		BasicDataSource bds = getInstance().pools.get(name);
		return bds.getNumActive();
	}
}

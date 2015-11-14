package org.guyou.db;

/**
 * @author zhushijian
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ConnectionFactory {
	
	public synchronized static Connection getConnection() throws SQLException{
		return PoolManager.getConnection();
	}

	public synchronized static Connection getConnection(boolean commit)
			throws SQLException {
		Connection con = PoolManager.getConnection();
		con.setAutoCommit(commit);
		return con;
	}

	public synchronized static Connection getConnection(String dbname,
			boolean commit) throws SQLException {
		Connection con = PoolManager.getConnection(dbname);
		con.setAutoCommit(commit);
		return con;
	}

	public synchronized static Connection getConnection(String dbname,
			boolean commit, boolean isUseDefault) throws SQLException {
		Connection con = PoolManager.getConnection(dbname);
		if (null == con && isUseDefault) {
			con = PoolManager.getConnection();
		}
		con.setAutoCommit(commit);
		return con;
	}
	
	public static Connection getConnection(String drivers,String url,String user,String password){
		try {
			Class.forName(drivers).newInstance();
			Connection con = DriverManager.getConnection(url, user, password);
			return con;
		} catch ( InstantiationException e ) {
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DbConnection getConnection(String dbPropertyName){
		ResourceBundle rb = ResourceBundle.getBundle(dbPropertyName, Locale.getDefault());
		Enumeration<String> propNames = rb.getKeys();
		while (propNames.hasMoreElements()) {
			String name = propNames.nextElement();
			if (name.contains("defaultDB.url")) {
				//defaultDB.url=jdbc:mysql://192.168.0.90:3306/hchl2?createDatabaseIfNotExist=true&useunicode=true&characterEncoding=utf8
				String url = rb.getString(name);
				int whIndex = url.lastIndexOf("?");
				String dbName = url.substring(url.lastIndexOf("/")+1,whIndex==-1 ? url.length() : whIndex );
				String drivername = rb.getString("defaultDB.drivers");
				String user = rb.getString("defaultDB.user");
				String password = rb.getString("defaultDB.password");
				return new DbConnection(dbName,getConnection(drivername, url, user, password));
			}
		}
		throw new NullPointerException();
	}
}

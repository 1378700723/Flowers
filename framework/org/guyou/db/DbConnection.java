/**
 * 
 */
package org.guyou.db;

import java.sql.Connection;

/**
 * @author 朱施健
 *
 */
public class DbConnection {
	private String dbName;
	private Connection con;
	
	public DbConnection(String dbName,Connection con){
		this.dbName = dbName;
		this.con = con;
	}

	public String getDbName() {
		return dbName;
	}

	public Connection getConnection() {
		return con;
	}
}

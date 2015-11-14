package org.guyou.db;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.guyou.util.StringUtil;


/**
 * @author zhushijian
 * @version 1.0
 */
public class DbUtil{

	private static Logger logger = Logger.getLogger(DbUtil.class);
	
	private static final String SQL_PARAM_NULL = "SQL_PARAM_NULL";

	public static int getObjectType(Object param){
		int value=0;
		if(param==null){
			value=Types.NULL;
		}else if (param instanceof Integer) {
            value = Types.INTEGER;
        } else if (param instanceof String) {
        	value = Types.VARCHAR;
        } else if (param instanceof Double) {
        	value = Types.DOUBLE;
        } else if (param instanceof Float) {
        	value = Types.FLOAT;
        } else if (param instanceof Long) {
        	value = Types.BIGINT;
        } else if (param instanceof Boolean) {
        	value = Types.BOOLEAN;
        } else if (param instanceof Date) {
        	value = Types.DATE;
        }else if (param instanceof Blob) {
        	value = Types.BLOB;
        }else if (param instanceof byte[]) {
        	value = Types.BLOB;
        }else if (param instanceof Clob) {
        	value = Types.CLOB;
        }else if (param instanceof Timestamp) {
        	value = Types.TIMESTAMP;
        }else if (param instanceof BigDecimal) {
        	value = Types.DECIMAL;
        }else if (param instanceof Array) {
        	value = Types.ARRAY;
        }
		return value;
	}
	
	/**
	 * @param int i
	 * @param Object
	 * @param int intType
	 * @exception SQLException
	 */
	public static void setParameter(PreparedStatement ps,int i, Object obj)
			throws SQLException {
		if(obj==null){
			ps.setNull(i, Types.NULL);
			return ;
		}
		int intType=getObjectType(obj);
		switch ( intType ) {
			case Types.CHAR :
				ps.setString(i, (String)obj);
				break;
			case Types.VARCHAR :
				ps.setString(i, (String)obj);
				break;
			case Types.LONGVARCHAR :
				ps.setString(i, (String)obj);
				break;
			case Types.DATE :
				ps.setDate(i, (Date)obj);
				break;
			case Types.TIME :
				ps.setTime(i, (Time)obj);
				break;
			case Types.TIMESTAMP :
				ps.setTimestamp(i, (Timestamp)obj);
				break;
			case Types.BOOLEAN :
				ps.setBoolean(i, (Boolean)obj);
				break;
			case Types.TINYINT :
				ps.setShort(i, (Byte)obj);
				break;
			case Types.SMALLINT :
				ps.setShort(i, (Short)obj);
				break;
			case Types.INTEGER :
				ps.setInt(i, (Integer)obj);
				break;
			case Types.BIGINT :
				ps.setLong(i, (Long)obj);
				break;
			case Types.FLOAT :
				ps.setFloat(i, (Float)obj);
				break;
			case Types.DOUBLE :
				ps.setDouble(i, (Double)obj);
				break;
			case Types.DECIMAL :
				ps.setBigDecimal(i, (java.math.BigDecimal)obj);
				break;
			case Types.BLOB :
				ps.setBytes(i,(byte[])obj);
				break;				
			default :
				logger.error("Statement第"+i+"列没有设置值!!");
				break;
		}
	}
	
	public static Object getParameter(ResultSet rs,int i)
		throws SQLException {
		Object obj=null;
		int columnType=rs.getMetaData().getColumnType(i);
		switch ( columnType ) {
			case Types.CHAR :
				obj=StringUtil.getNotNull(rs.getString(i)).replace("``", "'");
				if(obj==null) obj="";
				break;
			case Types.VARCHAR :
				obj=StringUtil.getNotNull(rs.getString(i)).replace("``", "'");
				if(obj==null) obj="";
				break;
			case Types.LONGVARCHAR :
				obj=StringUtil.getNotNull(rs.getString(i)).replace("``", "'");
				if(obj==null) obj="";
				break;
			case Types.DATE :
				obj=rs.getDate(i);
				break;
			case Types.TIME :
				obj=rs.getTime(i);
				break;
			case Types.TIMESTAMP :
				obj=rs.getTimestamp(i);
				break;
			case Types.BOOLEAN :
				break;
			case Types.TINYINT :
				obj=rs.getInt(i);
				if(obj==null) obj=0;
				break;
			case Types.SMALLINT :
				obj=rs.getInt(i);
				if(obj==null) obj=0;
				break;
			case Types.INTEGER :
				obj=rs.getInt(i);
				if(obj==null) obj=0;
				break;
			case Types.BIGINT :
				obj=rs.getLong(i);
				if(obj==null) obj=0L;
				break;
			case Types.FLOAT:
				obj=rs.getFloat(i);
				if(obj==null) obj=0.0F;
				break;
			case Types.REAL:
				obj=rs.getFloat(i);
				if(obj==null) obj=0.0F;
				break;
			case Types.DOUBLE :
				obj=rs.getDouble(i);
				if(obj==null) obj=0.0D;
				break;
			case Types.DECIMAL :
				obj=rs.getBigDecimal(i);
				if(obj==null) obj=0;
				break;
			case Types.BLOB :
				obj=rs.getBytes(i);
				break;	
			case Types.BINARY :
				obj=rs.getBytes(i);
				break;	
			case Types.VARBINARY :
				obj=rs.getBytes(i);
				break;	
			case Types.LONGVARBINARY :
				obj=rs.getBytes(i);
				break;	
			default :
				break;
		}
		return obj;
	}
	
	public static void closeCon(Connection con){
		try{
			if(con!=null && !con.isClosed()){
				con.close();
			}
		}catch(SQLException e){
			logger.error("connection close error!!", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error("rollback error!!",e1);
			}
		}
	}

	
	public static List<Map<String,Object>> executeQuery(Connection con, String sql,Object[] objs, int iFrom,
			int iTo) throws SQLException {
		int num=0;
		for(int i=0;i<sql.length();i++){
			char c=sql.charAt(i);
			if(c=='?'){
				num++;
			}
		}
		List<Map<String,Object>> alField = new ArrayList<Map<String,Object>>();
		if(num==objs.length){
			int ii = 0;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = con.prepareStatement(sql);
				for(int i=1;i<=objs.length;i++){
					setParameter(ps, i, objs[i-1]);
				}
				rs = ps.executeQuery();
				if (!alField.isEmpty()) {
					alField.clear();
				}
				int kkk = 0;
				ResultSetMetaData rsmd = rs.getMetaData();
				Map<String,Object> alTemp = null;
				while (rs.next()) {
					ii++;
					if (ii < iFrom || ii > iTo) {
						continue;
					}
					kkk = kkk + 1;
					alTemp = new HashMap<String,Object>();
					for (int rsmdi = 1; rsmdi <= rsmd.getColumnCount(); rsmdi++) {
						alTemp.put(rsmd.getColumnLabel(rsmdi),getParameter(rs,rsmdi));
					}
					alField.add(alTemp);
					alTemp = null;
				}
				return alField;
			} finally {
				if(rs!=null){
					rs.close();
				}
				if(ps!=null){
					ps.close();
				}
			}
		}else{
			logger.error("参数个数错误...\n"
					+"SQL语句 ('?'个数"+num+"个)=> "+sql+"\n"
					+"参数(param个数"+objs.length+"个)=> ["+StringUtils.join(objs, ",")+"]\n");
			return alField;
		}
	}
	
	public static List<Map<String,Object>> executeQuery(Connection con, String sql)
			throws SQLException {
		List<Map<String,Object>> alField = new ArrayList<Map<String,Object>>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (!alField.isEmpty()) {
				alField.clear();
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			Map<String,Object> alTemp = null;
			while (rs.next()) {
				alTemp = new HashMap<String,Object>();
				for (int rsmdi = 1; rsmdi <= rsmd.getColumnCount(); rsmdi++) {
					alTemp.put(rsmd.getColumnLabel(rsmdi),getParameter(rs,rsmdi));
				}

				alField.add(alTemp);
				alTemp = null;
			}
			return alField;
		} finally {
			if(rs!=null){
				rs.close();
			}
			if(ps!=null){
				ps.close();
			}
		}
	}
	
	public static List<Map<String,Object>> executeQuery(Connection con, String sql,Object[] objs)
			throws SQLException {
		int num=0;
		for(int i=0;i<sql.length();i++){
			char c=sql.charAt(i);
			if(c=='?'){
				num++;
			}
		}
		List<Map<String,Object>> alField = new ArrayList<Map<String,Object>>();
		if(num==objs.length){
		
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = con.prepareStatement(sql);
				for(int i=1;i<=objs.length;i++){
					setParameter(ps, i, objs[i-1]);
				}
				rs = ps.executeQuery();
				if (!alField.isEmpty()) {
					alField.clear();
				}
				ResultSetMetaData rsmd = rs.getMetaData();
				Map<String,Object> alTemp = null;
				while (rs.next()) {
					alTemp = new HashMap<String,Object>();
					for (int rsmdi = 1; rsmdi <= rsmd.getColumnCount(); rsmdi++) {
						alTemp.put(rsmd.getColumnLabel(rsmdi),getParameter(rs,rsmdi));
					}
	
					alField.add(alTemp);
					alTemp = null;
				}
				return alField;
			} finally {
				if(rs!=null){
					rs.close();
				}
				if(ps!=null){
					ps.close();
				}
			}
		}else{
			logger.error("参数个数错误...\n"
					+"SQL语句 ('?'个数"+num+"个)=> "+sql+"\n"
					+"参数(param个数"+objs.length+"个)=> ["+StringUtils.join(objs, ",")+"]\n");
			return alField;
		}
	}

	
	/**
	 * @param sql
	 * @return HashMap
	 */
	public static Map<String,Object> executeFind(Connection con, String sql) throws SQLException {
		Map<String,Object> alTemp = new HashMap<String,Object>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			if(rs.next()){
				for (int rsmdi = 1; rsmdi <= rsmd.getColumnCount(); rsmdi++) {
					alTemp.put(rsmd.getColumnLabel(rsmdi),getParameter(rs,rsmdi));
				}
			}
			return alTemp;
		} finally {
			if(rs!=null){
				rs.close();
			}
			if(ps!=null){
				ps.close();
			}
		}
	}
	
	public static Map<String,Object> executeFind(Connection con, String sql,Object[] objs) throws SQLException {
		int num=0;
		for(int i=0;i<sql.length();i++){
			char c=sql.charAt(i);
			if(c=='?'){
				num++;
			}
		}
		Map<String,Object> alTemp = new HashMap<String,Object>();
		if(num==objs.length){
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = con.prepareStatement(sql);
				for(int i=1;i<=objs.length;i++){
					setParameter(ps, i, objs[i-1]);
				}
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				if(rs.next()){
					for (int rsmdi = 1; rsmdi <= rsmd.getColumnCount(); rsmdi++) {
						alTemp.put(rsmd.getColumnLabel(rsmdi),getParameter(rs,rsmdi));
					}
				}
				return alTemp;
			} finally {
				if(rs!=null){
					rs.close();
				}
				if(ps!=null){
					ps.close();
				}
			}
		}else{
			logger.error("参数个数错误...\n"
					+"SQL语句 ('?'个数"+num+"个)=> "+sql+"\n"
					+"参数(param个数"+objs.length+"个)=> ["+StringUtils.join(objs, ",")+"]\n");
			return alTemp;
		}
	}
	
	public static boolean executeUpdate(Connection con, String sql) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			return true;
		} finally{
			if(ps!=null){
				ps.close();
			}
		}
	}
	
	public static boolean executeUpdate(Connection con, SqlItemBuilder sqlItemBuilder) throws SQLException {
		if(sqlItemBuilder instanceof SinglePuritySqlBuilder){
			return executeUpdate(con,sqlItemBuilder.getSql());
		}else{
			return executeUpdate(con,sqlItemBuilder.getSql(),sqlItemBuilder.getParameters());
		}
	}
	
	public static boolean executeUpdate(Connection con, String sql,Object[] objs) throws SQLException {
		int num=0;
		for(int i=0;i<sql.length();i++){
			char c=sql.charAt(i);
			if(c=='?'){
				num++;
			}
		}
		
		if(num==objs.length){
			PreparedStatement ps = null;
			try {
				ps = con.prepareStatement(sql);
				for(int j=1;j<=num;j++){
					setParameter(ps,j,objs[j-1]);
				}
				ps.executeUpdate();
				return true;
			} finally{
				if(ps!=null){
					ps.close();
				}
			}
		}else{
			logger.error("参数个数错误...\n"
					+"SQL语句 ('?'个数"+num+"个)=> "+sql+"\n"
					+"参数(param个数"+objs.length+"个)=> ["+StringUtils.join(objs, ",")+"]\n");
			return false;
		}
	}
	
	public static boolean executeBatchUpdate(Connection con, String sql,Object[][] objs) throws SQLException {
		int num=0;
		for(int i=0;i<sql.length();i++){
			char c=sql.charAt(i);
			if(c=='?'){
				num++;
			}
		}
		
		if(num==objs[0].length){
			PreparedStatement ps = null;
			try {
				ps = con.prepareStatement(sql);
				for (int j=0;j<objs.length;j++) {
					for(int k=1;k<=objs[j].length;k++){
						setParameter(ps,k,objs[j][k-1]);
					}
					ps.addBatch();
		        }
				ps.executeBatch();
				ps.clearBatch();
				return true;
			} finally{
				if(ps!=null){
					ps.close();
				}
			}
		}else{
			logger.error("参数个数错误...\n"
					+"SQL语句 ('?'个数"+num+"个)=> "+sql+"\n"
					+"参数(param个数"+objs[0].length+"个)\n");
			return false;
		}
	}
	
	/**
	 * 获得所有表格
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public static List<String> getTableList(DbConnection con) throws SQLException{
		List<String> r = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.getConnection().prepareStatement("show tables in "+con.getDbName());
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String dbName = rs.getString(1);
				PreparedStatement ps2 = null;
				ResultSet rs2 = null;
				try {
					ps2 = con.getConnection().prepareStatement("show create table "+con.getDbName()+"."+dbName);
					rs2 = ps2.executeQuery();
					ResultSetMetaData rsmd = rs2.getMetaData();
					if (rs2.next()) {
						String lable = rsmd.getColumnLabel(1);
						if(lable.equalsIgnoreCase("table")){
							r.add(rs2.getString(1));
						}
					}
				} finally {
					if(rs2!=null){
						rs2.close();
					}
					if(ps2!=null){
						ps2.close();
					}
				}
			}
		}finally{
			if(rs!=null){
				rs.close();
			}
			if(ps!=null){
				ps.close();
			}
		}
		return r;
	}
	
	/**
	 * 清除数据库
	 * @param con
	 * @param tables
	 * @return
	 * @throws SQLException
	 */
	public static void clearDatas(DbConnection con,List<String> tables) throws SQLException{
		Statement stmt = null;
		try {
			con.getConnection().setAutoCommit(false);
			stmt = con.getConnection().createStatement();
			for ( String table : tables ) {
				stmt.addBatch("truncate "+table);
			}
			stmt.executeBatch();
			stmt.clearBatch();
			con.getConnection().commit();
		} finally{
			if(stmt!=null){
				stmt.close();
			}
		}
	}
	
	
	public static boolean executeBatchUpdate(Connection con, SqlItemBuilder sqlItemBuilder) throws SQLException {
		if(!(sqlItemBuilder instanceof BatchSqlItemBuilder)) throw new SQLException("SqlItemBuilder不是批处理Builder!");
		return executeBatchUpdate(con,sqlItemBuilder.getSql(),(Object[][])sqlItemBuilder.getParameters());
	}
	
	/**
	 * @param sql
	 * @return HashMap
	 */
	public static int getRows(Connection con, String sql) throws SQLException {
		int result = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result=rs.getInt(1);
			}
			return result;
		} finally {
			if(rs!=null){
				rs.close();
			}
			if(ps!=null){
				ps.close();
			}
		}
	}
	
	public static int getRows(Connection con, String sql,Object[] objs) throws SQLException {
		int num=0;
		for(int i=0;i<sql.length();i++){
			char c=sql.charAt(i);
			if(c=='?'){
				num++;
			}
		}
		int result = 0;
		if(num==objs.length){
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = con.prepareStatement(sql);
				for(int i=1;i<=objs.length;i++){
					setParameter(ps, i, objs[i-1]);
				}
				rs = ps.executeQuery();
				if(rs.next()){
					result=rs.getInt(1);
				}
				return result;
			}finally {
				if(rs!=null){
					rs.close();
				}
				if(ps!=null){
					ps.close();
				}
			}
		}else{
			logger.error("参数个数错误...\n"
					+"SQL语句 ('?'个数"+num+"个)=> "+sql+"\n"
					+"参数(param个数"+objs.length+"个)=> ["+StringUtils.join(objs, ",")+"]\n");
			return result;
		}
	}
	
	public static SqlItemBuilder createSingleSqlItemBuilder(SqlOperateType operate,String tableNames){
		return new SingleSqlItemBuilder(operate,tableNames);
	}
	
	public static SqlItemBuilder createBatchSqlItemBuilder(SqlOperateType operate,String tableNames){
		return new BatchSqlItemBuilder(operate,tableNames);
	}
	
	public static SqlItemBuilder createSinglePuritySqlBuilder(SqlOperateType operate,String tableNames){
		return new SinglePuritySqlBuilder(operate,tableNames);
	}
	
	public static SqlItemBuilder createSinglePuritySqlBuilder(SqlItemBuilder bulider){
		return new SinglePuritySqlBuilder((SingleSqlItemBuilder) bulider);
	}
	
	public static interface SqlItemBuilder{
		public SqlItemBuilder appendBasicSqlItem(String expression) throws SQLException;
		public SqlItemBuilder appendBasicSqlItem(String columnName,Object value) throws SQLException;
		public SqlItemBuilder appendWhereSqlItem(String conditions) throws SQLException;
		public SqlItemBuilder appendWhereSqlItem(String columnName,Object value) throws SQLException;
		public String getSql() throws SQLException;
		public Object[] getParameters() throws SQLException;
		public void destroy() ;
	}
	
	private static class SinglePuritySqlBuilder extends SingleSqlItemBuilder{
		SinglePuritySqlBuilder(SqlOperateType operate, String tableNames) {
			super(operate, tableNames);
		}
		
		SinglePuritySqlBuilder(SingleSqlItemBuilder builder){
			super(builder._operate, builder._tableNames);
			super._columnList = builder._columnList;
			super._where_columnList = builder._where_columnList;
		}
		
		@Override
		public String getSql() throws SQLException{
			StringBuilder sql = new StringBuilder(super.getSql());
			Object[] objs = super.getParameters();
			for ( int i = 0 ; i < objs.length ; i++ ) {
				Object param = objs[i];
				String  value = null;
				if(param==null){
					value="null";
				}else if (param instanceof Integer) {
		            value = param.toString();
		        } else if (param instanceof String) {
		        	value = "'"+param.toString().replace("'", "``")+"'";
		        } else if (param instanceof Double) {
		        	value = param.toString();
		        } else if (param instanceof Float) {
		        	value = param.toString();
		        } else if (param instanceof Long) {
		        	value = param.toString();
		        } else if (param instanceof Boolean) {
		        	value = (Boolean)param ? "1":"0";
		        }else{
		        	value = "";
		        }
				int index = sql.indexOf("?");
				sql.replace(index, index+1, value);
			}
			
			String r = sql.toString();
			if(super._operate==SqlOperateType.UPDATE || super._operate==SqlOperateType.DELETE){
				if(sql.indexOf(" and ")==-1){
					System.err.println("SQL: ["+r +"] 没有where条件");
				}
			}
			return r;
		}
	}
	
	private static class SingleSqlItemBuilder implements SqlItemBuilder{
		
		private Map<String,Object> _columnList = new LinkedHashMap<String,Object>();
		private Map<String,Object> _where_columnList = new LinkedHashMap<String,Object>();
		
		private String _tableNames;
		private SqlOperateType _operate;
		
		SingleSqlItemBuilder(SqlOperateType operate,String tableNames){
			_operate = operate;
			_tableNames = tableNames;
		}
		
		@Override
		public SqlItemBuilder appendBasicSqlItem(String expression) throws SQLException{
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE || _operate==SqlOperateType.DELETE) throw new SQLException("'insert','replace','delete'没有表达式!");
			_columnList.put(expression, SQL_PARAM_NULL);
			return this;
		}
		
		@Override
		public SqlItemBuilder appendBasicSqlItem(String columnName,Object value) throws SQLException{
			if(_operate==SqlOperateType.DELETE) throw new SQLException("'delete'没有基本条件!");
			_columnList.put(columnName, value);
			return this;
		}
		
		
		@Override
		public SqlItemBuilder appendWhereSqlItem(String conditions) throws SQLException{
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE) throw new SQLException("'insert'和'replace'没有'where'条件!");
			_where_columnList.put(conditions, SQL_PARAM_NULL);
			return this;
		}
		
		@Override
		public SqlItemBuilder appendWhereSqlItem(String columnName,Object value) throws SQLException{
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE) throw new SQLException("'insert'和'replace'没有'where'条件!");
			_where_columnList.put(columnName, value);
			return this;
		}
		
		
		@Override
		public String getSql() throws SQLException{
			StringBuilder sql = new StringBuilder();
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE){
				sql.append(_operate.operate).append(" into ").append(_tableNames+"(");
				Iterator<String> it = _columnList.keySet().iterator();
				while ( it.hasNext() ) {
					sql.append(it.next()).append(",");
				}
				StringUtil.deleteEndsMark(sql,",");
				sql.append(") values (");
				for ( int i = 0 ; i < _columnList.size() ; i++ ) {
					sql.append("?,");
					
				}
				StringUtil.deleteEndsMark(sql,",");
				sql.append(")");
			}else if(_operate==SqlOperateType.UPDATE) {
				sql.append("update ").append(_tableNames).append(" set ");
				Iterator<Entry<String, Object>> it = _columnList.entrySet().iterator();
				while ( it.hasNext() ) {
					Entry<String, Object> e = it.next();
					if(SQL_PARAM_NULL.equals(e.getValue())){
						sql.append(e.getKey()).append(",");
					}else{
						sql.append(e.getKey()).append("=?,");
					}
				}
				StringUtil.deleteEndsMark(sql,",");
				if(_where_columnList.size()>0){
					sql.append(" where 1=1");
					it = _where_columnList.entrySet().iterator();
					while ( it.hasNext() ) {
						Entry<String, Object> e = it.next();
						if(SQL_PARAM_NULL.equals(e.getValue())){
							sql.append(" and ").append(e.getKey());
						}else{
							sql.append(" and ").append(e.getKey()).append("=?");
						}
					}
				}
			}else if(_operate==SqlOperateType.DELETE) {
				sql.append("delete from ").append(_tableNames).append("");
				if(_where_columnList.size()>0){
					sql.append(" where 1=1");
					Iterator<Entry<String, Object>> it = _where_columnList.entrySet().iterator();
					while ( it.hasNext() ) {
						Entry<String, Object> e = it.next();
						if(SQL_PARAM_NULL.equals(e.getValue())){
							sql.append(" and ").append(e.getKey());
						}else{
							sql.append(" and ").append(e.getKey()).append("=?");
						}
					}
				}
			}
			return sql.toString();
		}
		
		@Override
		public Object[] getParameters() throws SQLException{
			List<Object> list = new ArrayList<Object>();
			for ( Object object : _columnList.values() ) {
				if(!SQL_PARAM_NULL.equals(object)) list.add(object);
			}
			for ( Object object : _where_columnList.values() ) {
				if(!SQL_PARAM_NULL.equals(object)) list.add(object);
			}
			Object[] objs = list.toArray(new Object[0]);
			list.clear();
			return objs;
		}
		
		@Override
		public void destroy() {
			_columnList.clear();
			_columnList = null;
			_where_columnList.clear();
			_where_columnList = null;
			_tableNames = null;
			_operate = null;
		}
	}
	
	private static class BatchSqlItemBuilder implements SqlItemBuilder{
		
		private Map<String,List<Object>> _columnList = new LinkedHashMap<String,List<Object>>();
		private Map<String,List<Object>> _where_columnList = new LinkedHashMap<String,List<Object>>();
		
		private String _tableNames;
		private SqlOperateType _operate;
		
		BatchSqlItemBuilder(SqlOperateType operate,String tableNames){
			_operate = operate;
			_tableNames = tableNames;
		}
		
		@Override
		public SqlItemBuilder appendBasicSqlItem(String expression) throws SQLException{
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE || _operate==SqlOperateType.DELETE) throw new SQLException("'insert','replace','delete'没有表达式!");
			if(!_columnList.containsKey(expression)) _columnList.put(expression, new ArrayList<Object>());
			_columnList.get(expression).add(SQL_PARAM_NULL);
			return this;
		}
		
		@Override
		public SqlItemBuilder appendBasicSqlItem(String columnName,Object value) throws SQLException{
			if(_operate==SqlOperateType.DELETE) throw new SQLException("'delete'没有基本条件!");
			if(!_columnList.containsKey(columnName)) _columnList.put(columnName, new ArrayList<Object>());
			_columnList.get(columnName).add(value);
			return this;
		}
		
		
		@Override
		public SqlItemBuilder appendWhereSqlItem(String conditions) throws SQLException{
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE) throw new SQLException("'insert'和'replace'没有'where'条件!");
			if(!_where_columnList.containsKey(conditions)) _where_columnList.put(conditions, new ArrayList<Object>());
			_where_columnList.get(conditions).add(SQL_PARAM_NULL);
			return this;
		}
		
		@Override
		public SqlItemBuilder appendWhereSqlItem(String columnName,Object value) throws SQLException{
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE) throw new SQLException("'insert'和'replace'没有'where'条件!");
			if(!_where_columnList.containsKey(columnName)) _where_columnList.put(columnName, new ArrayList<Object>());
			_where_columnList.get(columnName).add(value);
			return this;
		}
		
		
		@Override
		public String getSql() throws SQLException{
			StringBuilder sql = new StringBuilder();
			if(_operate==SqlOperateType.INSERT || _operate==SqlOperateType.REPLACE){
				sql.append(_operate.operate).append(" into ").append(_tableNames+"(");
				Iterator<String> it = _columnList.keySet().iterator();
				while ( it.hasNext() ) {
					sql.append(it.next()).append(",");
				}
				StringUtil.deleteEndsMark(sql,",");
				sql.append(") values (");
				for ( int i = 0 ; i < _columnList.size() ; i++ ) {
					sql.append("?,");
					
				}
				StringUtil.deleteEndsMark(sql,",");
				sql.append(")");
			}else if(_operate==SqlOperateType.UPDATE) {
				sql.append("update ").append(_tableNames).append(" set ");
				Iterator<Entry<String, List<Object>>> it = _columnList.entrySet().iterator();
				while ( it.hasNext() ) {
					Entry<String, List<Object>> e = it.next();
					if(SQL_PARAM_NULL.equals(e.getValue().get(0))){
						sql.append(e.getKey()).append(",");
					}else{
						sql.append(e.getKey()).append("=?,");
					}
				}
				StringUtil.deleteEndsMark(sql,",");
				if(_where_columnList.size()>0){
					sql.append(" where 1=1");
					
					it = _where_columnList.entrySet().iterator();
					while ( it.hasNext() ) {
						Entry<String, List<Object>> e = it.next();
						if(SQL_PARAM_NULL.equals(e.getValue().get(0))){
							sql.append(" and ").append(e.getKey());
						}else{
							sql.append(" and ").append(e.getKey()).append("=?");
						}
					}
				}
			}else if(_operate==SqlOperateType.DELETE) {
				sql.append("delete from ").append(_tableNames).append("");
				if(_where_columnList.size()>0){
					sql.append(" where 1=1");
					Iterator<Entry<String, List<Object>>> it = _where_columnList.entrySet().iterator();
					while ( it.hasNext() ) {
						Entry<String, List<Object>> e = it.next();
						if(SQL_PARAM_NULL.equals(e.getValue().get(0))){
							sql.append(" and ").append(e.getKey());
						}else{
							sql.append(" and ").append(e.getKey()).append("=?");
						}
					}
				}
			}
			String r = sql.toString();
			if(_operate==SqlOperateType.UPDATE || _operate==SqlOperateType.DELETE){
				if(sql.indexOf(" and ")==-1){
					System.err.println("SQL: ["+r +"] 没有where条件");
				}
			}
			return r;
		}
		
		@Override
		public Object[] getParameters() throws SQLException{
			List<List<Object>> list = new ArrayList<List<Object>>();
			list.addAll(_columnList.values());
			list.addAll(_where_columnList.values());
			
			Iterator<List<Object>> it = list.iterator();
			while ( it.hasNext() ) {
				List<Object> l = it.next();
				if(SQL_PARAM_NULL.equals(l.get(0))) it.remove();
			}
			
			Object[][] objs = new Object[list.get(0).size()][list.size()];
			for ( int i = 0 ; i < objs.length ; i++ ) {
				for ( int j = 0 ; j < objs[i].length ; j++ ) {
					objs[i][j] =  list.get(j).get(i);
				}
			}
			list.clear();
			return objs;
		}
		
		@Override
		public void destroy() {
			_columnList.clear();
			_columnList = null;
			_where_columnList.clear();
			_where_columnList = null;
			_tableNames = null;
			_operate = null;
		}
	}
	
	public static enum SqlOperateType{
		INSERT("insert"),
		UPDATE("update"),
		REPLACE("replace"),
		DELETE("delete");
		String operate;
		SqlOperateType(String opt){
			operate = opt;
		}
		@Override
		public String toString(){return operate;}
	}
}

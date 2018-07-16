package com.ziya05.ScaleDataImport;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ziya05.ScaleDataImport.Bean.OptionBean;
import com.ziya05.ScaleDataImport.Bean.QuestionBean;

public class MySqlDao {
	public Map<Integer, String> getMap(Class<?> cl, String whereCondition) throws ClassNotFoundException, SQLException {
		MySqlFor mySqlFor = cl.getAnnotation(MySqlFor.class);
		if (mySqlFor == null) {
			throw new IllegalArgumentException("对象没有实现mysql注解！");
		}
		
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		String tableName = mySqlFor.name();
		String keyName = null;
		String valueName = null;
		 
		Field[] fds = cl.getDeclaredFields();
		for(Field fd : fds) {
			fd.setAccessible(true);
			
			MySqlFor f = fd.getAnnotation(MySqlFor.class);
			if (f.iskey()) {
				keyName = f.name();
			} else if (f.isvalue()) {
				valueName = f.name();
			}
			
			if (keyName != null && valueName != null) {
				break;
			}
		}
		
		String sql = String.format("select %s, %s from %s %s", keyName, valueName, tableName, whereCondition);
		Connection conn = this.getConn();
		Statement stmt = conn.createStatement();
		
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			Integer key = rs.getInt(keyName);
			String value = rs.getString(valueName);
			map.put(key, value);
		}
		
		rs.close();
		conn.close();
		
		return map;
	} 
	
	public int insert(Object obj) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		List<Object> objects = new ArrayList<Object>();
		String sql = this.getSqlByObject(obj, objects);
		
		Connection conn = this.getConn();
		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		
		for (int i = 0; i < objects.size(); i++) {
			pstmt.setObject(i + 1, objects.get(i));
		}
		
		pstmt.execute();
		ResultSet rs = pstmt.getGeneratedKeys();
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		conn.close();
		
		return id;
	}
	
	public void batchInsertQuestion(List<QuestionBean> lst) 
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Connection conn = this.getConn();
		conn.setAutoCommit(false);
		
		for (Object obj : lst) {
			List<Object> objects = new ArrayList<Object>();
			String sql = this.getSqlByObject(obj, objects);
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < objects.size(); i++) {
				pstmt.setObject(i + 1, objects.get(i));
			}
			
			pstmt.execute();
		}
		
		conn.commit();
		
		for (QuestionBean question : lst) {
			List<OptionBean> optionLst = question.getOptionItems();
			
			for (Object obj : optionLst) {
				List<Object> objects = new ArrayList<Object>();
				String sql = this.getSqlByObject(obj, objects);
				
				PreparedStatement pstmt = conn.prepareStatement(sql);
				for (int i = 0; i < objects.size(); i++) {
					pstmt.setObject(i + 1, objects.get(i));
				}
				
				pstmt.execute();
			}
		}
		
		conn.commit();
		conn.setAutoCommit(true);
		conn.close();
	}
	
	public void batchInsert(List<Object> lst) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Object> objects = new ArrayList<Object>();
		String sql = this.getSqlByObject(lst.get(0), objects);
		
		Connection conn = this.getConn();
		conn.setAutoCommit(false);
		
		for (Object obj : lst) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < objects.size(); i++) {
				pstmt.setObject(i + 1, objects.get(i));
			}
			
			pstmt.execute();
		}
		
		conn.commit();
		conn.setAutoCommit(true);
		conn.close();		
	}
	
	private String getSqlByObject(Object obj, List<Object> objects) throws IllegalArgumentException, IllegalAccessException {
		Class<?> cl = obj.getClass();
		MySqlFor mySqlFor = cl.getAnnotation(MySqlFor.class);
		if (mySqlFor == null) {
			throw new IllegalArgumentException("对象没有实现mysql注解！");
		}
		
		String tableName = mySqlFor.name();
		
		StringBuilder sb = new StringBuilder("insert into " + tableName + "(");
		StringBuilder vs = new StringBuilder("values(");
		
		Field[] fds = cl.getDeclaredFields();
		for (Field fd : fds) {
			fd.setAccessible(true);
			
			MySqlFor f = fd.getAnnotation(MySqlFor.class);
			if (f != null) {
				sb.append(f.name() + ",");
				vs.append("? ,");
				
				Object o = fd.get(obj);
				objects.add(o);
			}
		}
		
		String sql = sb.toString().substring(0, sb.length() - 1) + ") " 
		+ vs.toString().substring(0, vs.length() - 1) + ")";
		
		return sql;
	}
	
	public void deleteScale(int id) throws ClassNotFoundException, SQLException {
		deleteTable(id, "scaleId", "ResultBase");
		deleteTable(id, "scaleId", "ResultFactor");
		deleteTable(id, "scaleId", "TesteeBase");
		deleteTable(id, "scaleId", "TesteeData");
		deleteTable(id, "scaleId", "TesteePersonalInfo");
		deleteTable(id, "scaleId", "TesteeDataText");
		
		deleteTable(id, "scaleId", "GlobalJump");
		deleteTable(id, "scaleId", "FactorMap");
		deleteTable(id, "scaleId", "Relation");
		deleteTable(id, "scaleId", "`Group`");
		deleteTable(id, "scaleId", "Level");
		deleteTable(id, "scaleId", "Factor");
		
		deleteTable(id, "scaleId", "`Option`");
		deleteTable(id, "scaleId", "Question");
		
		deleteTable(id, "scaleId", "ScalePersonalConfig");
		
		deleteTable(id, "id", "Scale");
	}
	
	public int getLastestScaleId() throws ClassNotFoundException, SQLException {
		Connection conn = this.getConn();
		String sql = "select id from scale order by id desc";
		Statement stmt = conn.createStatement();
		
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		
		int scaleId = rs.getInt("id");
		rs.close();
		conn.close();
		
		return scaleId;
	}
	
	private void deleteTable(int id, String idName, String tableName) throws ClassNotFoundException, SQLException {
		Connection conn = this.getConn();
		String sql = String.format("delete from %s where %s = %d", tableName, idName, id);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.execute();
		
		conn.close();
		
	}
	
	private Connection getConn() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/scale?characterEncoding=utf8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
		Connection conn = DriverManager.getConnection(url, "scale", "scale-01");
		
		return conn;
	}	
}

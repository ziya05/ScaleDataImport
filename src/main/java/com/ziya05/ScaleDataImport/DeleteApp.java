package com.ziya05.ScaleDataImport;

import java.sql.SQLException;

public class DeleteApp {
	public static void main( String[] args ) throws ClassNotFoundException, SQLException
    {
		delete(99);
		//deleteAll();
    }
	
	private static void delete(int id) throws ClassNotFoundException, SQLException {
		MySqlDao dao = new MySqlDao();
		dao.deleteScale(id);
		
		System.out.println(String.format("第 %d 个量表删除成功！", id));
	}
	
	private static void deleteAll() throws ClassNotFoundException, SQLException {
		MySqlDao dao = new MySqlDao();
		
		for(int i = 0; i <= 40; i++) {
			dao.deleteScale(i);
			
			System.out.println(String.format("第 %d 个量表删除成功！", i));
		}
	}
}

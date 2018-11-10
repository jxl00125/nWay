package com.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.dbpool.ConnectionPoolManager;
import com.dbpool.IConnectionPool;


public class Dbutil {
	
	private IConnectionPool pool;
	public Connection getConnection(String name){
		pool = ConnectionPoolManager.getInstance().getPool(name);
		Connection conn = null;
		if(pool != null && pool.isActive()){
			conn = pool.getConnection();
		}
		return conn;
	
	}
	
	
	
	public void releaseConn(String name , Connection conn) throws SQLException{
		
		pool = ConnectionPoolManager.getInstance().getPool(name);
		if(pool != null && pool.isActive()){
				pool.releaseConn(conn);
		}
	}

}

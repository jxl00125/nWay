/**
 * 
 */
package com.dbpool;

/**
 * <p>Title: ConnectionPoolManager.java</p>
 * <p>Description: 安全生产一体化管理手册应用平台</p>
 * @author JIANGLONG
 * @version 1.0 创建时间：2013-11-4 上午09:54:55
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
/**
 * 连接管理类
 * @author Ran
 *
 */
public class ConnectionPoolManager {
	
	
	// 连接池存放
	public Hashtable<String,IConnectionPool> pools = new Hashtable<String, IConnectionPool>();
	public Logger logger =Logger.getLogger(ConnectionPoolManager.class);
	// 初始化
	private ConnectionPoolManager(){
		init();
	}
	// 单例实现
	public static ConnectionPoolManager getInstance(){
		return Singtonle.instance;
	}
	private static class Singtonle {
		private static ConnectionPoolManager instance =  new ConnectionPoolManager();
	}
	
	
	// 初始化所有的连接池
	public void init(){
		for(int i =0;i<DBInitInfo.beans.size();i++){
			DBbean bean = DBInitInfo.beans.get(i);
			ConnectionPool pool = new ConnectionPool(bean);
			if(pool != null){
				pools.put(bean.getPoolName(), pool);
				logger.info("Info:Init connection successed ->" +bean.getPoolName());
			}
		}
	}
	
	// 获得连接,根据连接池名字 获得连接
	public Connection  getConnection(String poolName){
		Connection conn = null;
		if(pools.size()>0 && pools.containsKey(poolName)){
			conn = getPool(poolName).getConnection();
		}else{
			logger.error("Error:Can't find this connecion pool ->"+poolName);
		}
		return conn;
	}
	
	// 关闭，回收连接
	public void close(String poolName,Connection conn){
			IConnectionPool pool = getPool(poolName);
			try {
				if(pool != null){
					pool.releaseConn(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	// 清空连接池
	public void destroy(String poolName){
		IConnectionPool pool = getPool(poolName);
		if(pool != null){
			pool.destroy();
		}
	}
	
	// 获得连接池
	public IConnectionPool getPool(String poolName){
		IConnectionPool pool = null;
		if(pools.size() > 0){
			 pool = pools.get(poolName);
		}
		return pool;
	}
}


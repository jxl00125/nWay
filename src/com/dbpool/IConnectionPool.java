/**
 * 
 */
package com.dbpool;

/**
 * <p>Title: IConnectionPool.java</p>
 * <p>Description: 安全生产一体化管理手册应用平台</p>
 * @author JIANGLONG
 * @version 1.0 创建时间：2013-11-4 上午09:53:56
 */

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionPool {
	// 获得连接
	public Connection  getConnection();
	// 获得当前连接
	public Connection getCurrentConnecton();
	// 回收连接
	public void releaseConn(Connection conn) throws SQLException;
	// 销毁清空
	public void destroy();
	// 连接池是活动状态
	public boolean isActive();
	// 定时器，检查连接池
	public void cheackPool();
}


/**
 * 
 */
package com.dbpool;

/**
 * <p>Title: DBInitInfo.java</p>
 * <p>Description: 安全生产一体化管理手册应用平台</p>
 * @author JIANGLONG
 * @version 1.0 创建时间：2013-11-4 上午09:55:25
 */

import java.util.ArrayList;
import java.util.List;

import com.util.ConfigServer;
/**
 * 初始化，模拟加载所有的配置文件
 * @author Ran
 *
 */
public class DBInitInfo {
	public  static List<DBbean>  beans = null;
	static{
		beans = new ArrayList<DBbean>();
		// 这里数据 可以从xml 等配置文件进行获取
		DBbean beanOracle = new DBbean();
		ConfigServer cf =ConfigServer.getServer();
		beanOracle.setDriverName(cf.getInitProperty("DRIVER"));
		beanOracle.setUrl(cf.getInitProperty("URL"));
		beanOracle.setUserName(cf.getInitProperty("DBUSER"));
		beanOracle.setPassword(cf.getInitProperty("DBPASSWORD"));
		beanOracle.setMinConnections(5);
		beanOracle.setMaxConnections(20);
		beanOracle.setPoolName(cf.getInitProperty("POOLNAME"));
		beans.add(beanOracle);
	}
}


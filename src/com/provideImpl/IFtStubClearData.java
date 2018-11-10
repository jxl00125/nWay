package com.provideImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.core.bean.VDocument;
import com.util.Dbutil;
import com.util.Md5CheckUtil;
import com.util.ParseXml;

/**
 * 清空维护索引表中重复数据
 * @author liugang
 *
 */
public class IFtStubClearData {
	
	static Logger logger = Logger.getLogger(IFtStubClearData.class);
	final static String SUCCESS_MSG = "success";
	final static String ERROR_MSG = "error";
	final static String FALSE_MSG = "error";
	
	public String clearData(String viewname, String key){

		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			logger.info("开始清空重复数据");
			msg = clearRepeatingData(viewname);
		}
		Date end = new Date();
		logger.info("添加索引结束" + (end.getTime() - start.getTime())
				+ " total milliseconds");
		return msg;
	
		
	}
	
	private String clearRepeatingData(String viewname){
		
		VDocument vd = ParseXml.docments.get(viewname);
		clearTagData(viewname);
		String dbname = vd.getDbname();// 获取数据源信息
		String indextable = vd.getIndextable();// 获取维护索引表名信息
		// 清空维护索引表中重复数据
		String sql = "delete from " +indextable+" a "+
		 			" where (a.docid, a.sql_type,a.trs_flag) in (select docid, sql_type,trs_flag "+
		 			" from " +indextable+
		 			" group by docid, sql_type,trs_flag "+
		 			" having count(*) > 1) "+
		 			" and rowid not in (select min(rowid) "+
		 			" from  "+indextable+
		 			" group by docid, sql_type,trs_flag "+
		 			" having count(*) > 1) ";
		
		if (null == dbname)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname);
		Dbutil db = new Dbutil();
		Connection connection = db.getConnection(dbname);
		if (connection == null)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname
					+ ",并请检查数据库服务是否开启");
		PreparedStatement prep = null;
		try {
			prep = connection.prepareStatement(sql);
			//prep.execute();
			prep.executeUpdate(sql);
			connection.commit();
			prep.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			//return ERROR_MSG;
			e.printStackTrace();
		}finally{
			try {
				if(connection!=null){
					//connection.close();
					db.releaseConn(dbname, connection);
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				//return ERROR_MSG;
				e.printStackTrace();
			}
		}
		
		
		return SUCCESS_MSG;
	}

	
	private String clearTagData(String viewname){

		VDocument vd = ParseXml.docments.get(viewname);
		String dbname = vd.getDbname();// 获取数据源信息
		String indextable = vd.getIndextable();// 获取维护索引表名信息
		// 清空维护索引表中重复数据
		String sql = "delete from " +indextable+" a "+
		 			" where a.trs_flag=1";
		
		if (null == dbname)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname);
		Dbutil db = new Dbutil();
		Connection connection = db.getConnection(dbname);
		if (connection == null)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname
					+ ",并请检查数据库服务是否开启");
		PreparedStatement prep = null;
		try {
			prep = connection.prepareStatement(sql);
			//prep.execute();
			prep.executeUpdate(sql);
			prep.close();
			connection.commit();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			//return ERROR_MSG;
			e.printStackTrace();
		}finally{
			try {
				if(connection!=null){
					db.releaseConn(dbname, connection);
					}
				//if(connection!=null)
				//	connection.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
				//return ERROR_MSG;
				e.printStackTrace();
			}
		}
		
		
		return SUCCESS_MSG;
	
	}
}

package com.provideImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.core.bean.VDocument;
import com.core.index.FIndexWriter;
import com.util.Dbutil;
import com.util.IdFilterSqlUtil;
import com.util.Md5CheckUtil;
import com.util.ParseXml;

public class IFtsStubDeleteIndex {

	static Logger logger = Logger.getLogger(IFtsStubDeleteIndex.class);
	final static String MODE = "3";
	final static String FALSE_MSG = "";
	final static String SUCCESS_MSG = "success";

	public String deleteIndex(String viewname, String key) {
		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			logger.info("开始删除索引");
			msg = deleteIndex(viewname);
		}
		Date end = new Date();
		logger.info("删除索引结束" + (end.getTime() - start.getTime())
				+ " total milliseconds");
		return msg;
	}

	private String deleteIndex(String viewname) {

		VDocument vd = ParseXml.docments.get(viewname);
		String path = vd.getPath();
		String keyname = vd.getIndexkey();// 获取维护索引主键信息
		FIndexWriter fw = new FIndexWriter(path);
		IndexWriter iw = fw.getIndexWriter(false);
		String dbname = vd.getDbname();// 获取数据源信息
		String indextable = vd.getIndextable();// 获取维护索引表名信息
		String sql = "select * from "+indextable +" where  sql_type="+MODE+ " and  trs_flag=0";// 获取维护索引视图标记为删除的索引记录信息
		if (null == dbname)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname);
		// 获取所有数据
		
		Connection connection=null;
		PreparedStatement prep = null;
		ResultSet rs=null;
		Dbutil db = new Dbutil();
		try {
			System.out.println(sql);
			 connection = db.getConnection(dbname);
			if (connection == null)
				logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname
						+ ",并请检查数据库服务是否开启");
			prep = connection.prepareStatement(sql);
			rs = prep.executeQuery();
			//int count = 0;
			StringBuffer keyvalues = new StringBuffer();
			while (rs.next()) {
				String keyvalue = rs.getString(keyname);
				keyvalues.append(keyvalue+",");
				Term term = new Term(keyname, keyvalue);
				iw.deleteDocuments(term);
			}
			String values = keyvalues.toString();
			if(values!=null&&!"".equals(values)){
				values = values.substring(0, values.length()-1);
				//String updatesql ="update " +indextable+" set trs_flag=1 where sql_type="+MODE+ " and " +IdFilterSqlUtil.make(keyname, values).getSql();
				updateData(viewname,keyname,values);
			}
			
			rs.close();
			prep.close();
			iw.commit();
			iw.close();
			logger.info("本次已删除索引数:"+keyvalues.length()+" 已删除索引"+keyname+"["+values+"]");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
			if(iw!=null){
				try {
					iw.close();
				} catch (CorruptIndexException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return FALSE_MSG;
		}finally{
			try {
				if(iw!=null){
					iw.close();
				}
				
				if(connection!=null){
					//connection.close();
					db.releaseConn(dbname, connection);
					}
			} catch (SQLException e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				return FALSE_MSG;
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}

		return SUCCESS_MSG;
	}
	
	private void updateData(String viewname,String keyname,String values){
		
		VDocument vd = ParseXml.docments.get(viewname);
		String indextable = vd.getIndextable();// 获取维护索引表名信息
		String dbname = vd.getDbname();// 获取数据源信息
		String updatesql ="update " +indextable+" set trs_flag=1 where sql_type="+MODE+ " and " +IdFilterSqlUtil.make(keyname, values).getSql();
		int _svalue[]= IdFilterSqlUtil.make(keyname, values).getValues();
		if (null == dbname)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname);
		Dbutil db = new Dbutil();
		Connection connection = db.getConnection(dbname);
		if (connection == null)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname
					+ ",并请检查数据库服务是否开启");
		PreparedStatement prep = null;
		try {
			prep = connection.prepareStatement(updatesql);
			if(_svalue!=null){
				for(int i=0;i<_svalue.length;i++){
					prep.setInt(i+1, _svalue[i]);
				}
			}
			
			prep.execute();
			connection.commit();
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				
				if(connection!=null){
					//connection.close();
					db.releaseConn(dbname, connection);
					}
					
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

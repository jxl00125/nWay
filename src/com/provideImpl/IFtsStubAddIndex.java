package com.provideImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.core.bean.VDocument;
import com.core.bean.VField;
import com.core.index.FIndexWriter;
import com.util.Dbutil;
import com.util.IdFilterSqlUtil;
import com.util.Md5CheckUtil;
import com.util.ParseXml;

public class IFtsStubAddIndex {
	
	static Logger logger = Logger.getLogger(IFtsStubAddIndex.class);
	final static String MODE = "1";
	final static String SUCCESS_MSG = "success";
	final static String FALSE_MSG = "";
	final static String DATATYPE_4 = "4";//数字
	final static String DATATYPE_12 = "12";//字符串
	final static String DATATYPE_93 = "93";//时间
	final static String DATATYPE_2005 = "2005";//html
	
	final static int PAGE_SIZE = 5000;//记录数
	
	protected static String XML_HEADER = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
	"<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>";
	
	public String addIndex(String viewname, String key){
		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			logger.info("开始添加索引");
			msg = addIndex(viewname);
		}
		Date end = new Date();
		logger.info("添加索引结束" + (end.getTime() - start.getTime())
				+ " total milliseconds");
		return msg;
	}
	
	/**
	 * 获取标记为添加主键ID，根据主键ID获取视图
	 * @param viewname
	 * @return
	 */
	private String addIndex(String viewname){
		String msg = "";
		VDocument vd = ParseXml.docments.get(viewname);
		String path = vd.getPath();
		String keyname = vd.getIndexkey();// 获取维护索引主键信息
		FIndexWriter fw = new FIndexWriter(path);
		IndexWriter iw = fw.getIndexWriter(false);
		String dbname = vd.getDbname();// 获取数据源信息
		String indextable = vd.getIndextable();// 获取维护索引表名信息
		String sql = "select b.*  from "+indextable +" a , "+ viewname +" b where   sql_type="+MODE+ " and  trs_flag=0 and a."+keyname+"=b."+keyname;// 获取维护索引视图标记为删除的索引记录信息
		System.out.println("==>sql="+sql);
		if (null == dbname)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname);
		// 获取所有数据
		Dbutil db = new Dbutil();
		Connection connection = db.getConnection(dbname);
		if(connection==null)
			logger.error("请配置视图所在数据源：配置文件[document.xml];视图名称：" + viewname+",并请检查数据库服务是否开启");
		PreparedStatement prep = null;
		try {
			prep = connection.prepareStatement(sql);
			ResultSet rs = prep.executeQuery();
			int count = 0;
			StringBuffer keyvalues = new StringBuffer();
			while (rs.next()) {
				Document doc = new Document();
				count++;
				if (count > 100000)
					break;
				List<VField> fieldlist = vd.getFieldlist();
				String fieldname = null;
				String index = null;
				String datatype =null;
				String store = null;
				String value = null;
				
				
				keyvalues.append(rs.getString(keyname)+",");
				for (VField vField : fieldlist) {
					

					fieldname = vField.getName();
					index = vField.getIndex();
					store = vField.getStore();
					datatype =vField.getDatatype(); 
					value = rs.getString(fieldname);
					
					if(value !=null && value !=""){
						if("doctype".equals(fieldname)){
							value = value.replace(".", "");
							//System.out.println(fieldname + "===>"+value);
						}
						//System.out.println(fieldname + "===>"+value);
						//替换html的< > / 
						if("doctitle".equals(fieldname) || "doccontent".equals(fieldname)){
							String string = value;
							string = string.replaceAll("&", "&amp;");
			        		string = string.replaceAll("<", "&lt;");
			        		string = string.replaceAll(">", "&gt;");
			        		string = string.replaceAll("\"", "&quot;");
			        		string = string.replaceAll("'", "&#39;");
			        		value = string;
						}
					}
					
					if(value==null||"".equals(value))
						continue;
					// Field.Store.YES;Field.Store.NO;
					// Field.Index.ANALYZED;Field.Index.ANALYZED_NO_NORMS,Field.Index.NO,Field.Index.NOT_ANALYZED;Field.Index.NOT_ANALYZED_NO_NORMS
					Field field = null;
					if(DATATYPE_12.equals(datatype)){
						
						if ("YES".equalsIgnoreCase(store)) {
							if ("ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES, Field.Index.ANALYZED);
							} else if ("ANALYZED_NO_NORMS".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES,
										Field.Index.ANALYZED_NO_NORMS);
							} else if ("NO".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES, Field.Index.NO);
							} else if ("NOT_ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES, Field.Index.NOT_ANALYZED);
							} else if ("NOT_ANALYZED_NO_NORMS"
									.equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES,
										Field.Index.NOT_ANALYZED_NO_NORMS);
							}

						} else if ("NO".equalsIgnoreCase(store)) {

							if ("ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.ANALYZED);
							} else if ("ANALYZED_NO_NORMS".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.ANALYZED_NO_NORMS);
							} else if ("NO".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.NO);
							} else if ("NOT_ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.NOT_ANALYZED);
							} else if ("NOT_ANALYZED_NO_NORMS"
									.equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.NOT_ANALYZED_NO_NORMS);
							}
						}
						
						doc.add(field);	
					}else if(DATATYPE_4.equals(datatype)){
						String n_value = rs.getString(fieldname);
						if(n_value==null)
							continue;
						NumericField  nfield = new NumericField(fieldname, 
										Field.Store.YES,
										true).setLongValue(Long.parseLong(n_value));
						doc.add(nfield);
					}
					else if(DATATYPE_93.equals(datatype)){
						Date n_value = rs.getDate(fieldname);
						if(n_value==null)
							continue;
						NumericField  nfield = new NumericField(fieldname, 
										Field.Store.YES,
										true).setLongValue(n_value.getTime());
						doc.add(nfield);
					}else if(DATATYPE_2005.equals(datatype)){
						value = getTextData(value);
						if ("YES".equalsIgnoreCase(store)) {
							if ("ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES, Field.Index.ANALYZED);
							} else if ("ANALYZED_NO_NORMS".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES,
										Field.Index.ANALYZED_NO_NORMS);
							} else if ("NO".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES, Field.Index.NO);
							} else if ("NOT_ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES, Field.Index.NOT_ANALYZED);
							} else if ("NOT_ANALYZED_NO_NORMS"
									.equalsIgnoreCase(index)) {
								field = new Field(fieldname, value,
										Field.Store.YES,
										Field.Index.NOT_ANALYZED_NO_NORMS);
							}

						} else if ("NO".equalsIgnoreCase(store)) {

							if ("ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.ANALYZED);
							} else if ("ANALYZED_NO_NORMS".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.ANALYZED_NO_NORMS);
							} else if ("NO".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.NO);
							} else if ("NOT_ANALYZED".equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.NOT_ANALYZED);
							} else if ("NOT_ANALYZED_NO_NORMS"
									.equalsIgnoreCase(index)) {
								field = new Field(fieldname, value, Field.Store.NO,
										Field.Index.NOT_ANALYZED_NO_NORMS);
							}
						}
						
						doc.add(field);	
					}
					
				
					
				}
				iw.addDocument(doc);
				
			}
			iw.close();
			rs.close();
			prep.close();
			String values = keyvalues.toString();
			if(values!=null&&!"".equals(values)){
				values = values.substring(0, values.length()-1);
				String updatesql ="update " +indextable+" set trs_flag=1 where  "+IdFilterSqlUtil.make(keyname, values).getSql();
				logger.info(updatesql);
				updateData(viewname,updatesql,IdFilterSqlUtil.make(keyname, values).getValues());
			}
			logger.info("本次已添加索引数:"+keyvalues.length()+" 已添加索引"+keyname+"["+values+"]");
			msg = SUCCESS_MSG;
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			//e.printStackTrace();
		} finally{
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
		return msg;	
	}

	private void updateData(String viewname,String sql, int values[]){
		
		VDocument vd = ParseXml.docments.get(viewname);
		String dbname = vd.getDbname();// 获取数据源信息
		//String indextable = vd.getIndextable();// 获取维护索引表名信息
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
			if(values!=null){
				for(int i=0;i<values.length;i++){
					prep.setInt(i+1, values[i]);
				}
			}
			prep.execute();
			connection.commit();
			
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				//db.releaseConn(dbname, connection);
				if(connection!=null){
					//connection.close();
					db.releaseConn(dbname, connection);
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 将数据源(mDataSource)格式化成为标准的xml结构，使dom4j能识别
	 * */
	public String formatterSource(String mDataSource){
		mDataSource = mDataSource.toUpperCase();                                        //全部转换成大写
		//添加声明头
		mDataSource = XML_HEADER + mDataSource+"</body></html>";
		mDataSource = mDataSource.toUpperCase();     //全部转换成大写
		
		return mDataSource;
	}
	
	public String getTextData(String mDataSource){
		
		mDataSource =formatterSource(mDataSource);
		org.jsoup.nodes.Document doc = Jsoup.parse(mDataSource);
		StringBuffer  sb = new StringBuffer();
		Iterator<Element> iter = doc.children().iterator();
		String temp = "";
		Element element = null;
		while(iter.hasNext()){
			element =iter.next();
			sb.append(element.text());
		}
		
		char [] s_c = new char[sb.length()];
		
        for(int i =0;i<sb.length();i++) {
          if (sb.charAt(i) > 0xFFFD)
            {
        	  s_c [i] = ' ';
            } 
            else if (sb.charAt(i) < 0x20 && sb.charAt(i) != '\t' & sb.charAt(i) != '\n' & sb.charAt(i) != '\r')
            {
            	s_c [i] = ' ';
            }
            else if(sb.charAt(i) >= 0x80 && sb.charAt(i) <= 0x9f){
            	s_c [i] = ' ';
            }else{
            	s_c [i] = sb.charAt(i);
            }
        }
        return new String(s_c);
		//return sb.toString();
	}

}

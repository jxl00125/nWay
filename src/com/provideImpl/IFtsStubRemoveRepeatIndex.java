package com.provideImpl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.core.bean.VDocument;
import com.core.index.FIndexWriter;
import com.util.Dbutil;
import com.util.IdFilterSqlUtil;
import com.util.Md5CheckUtil;
import com.util.ParseXml;
import com.util.StringUtil;

public class IFtsStubRemoveRepeatIndex {
	
	static Logger logger = Logger.getLogger(IFtsStubRemoveRepeatIndex.class);
	final static String MODE = "1";
	final static String SUCCESS_MSG = "success";
	final static String FALSE_MSG = "";
	public static final String[] LIKE_SEARCH_REPLACE_STR = { "'", "\"", " ", 
	    "+", "-", "/", "\\", "*", "=", ".", "(", ")", ",", "<", ">", ",", 
	    "&", "%", "?" };
	public static final String[] TRSSERVER_KEYWORDS_DEFINE = { "'", "\"", " ", 
	    "+", "-", "/", "\\", "*", "=", ".", "(", ")", ",", "<", ">", ",", 
	    "&", "!", "@", "$", "^" };
	
	public String removeRepeatIndex(String viewname, String key){
		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			logger.info("索引去重开始");
			msg = removeRepeatIndex(viewname);
		}
		Date end = new Date();
		logger.info("索引去重结束" + (end.getTime() - start.getTime())
				+ " total milliseconds");
		return msg;
	}
	
	/**
	 * 获取标记为添加主键ID，根据主键ID获取视图
	 * @param viewname
	 * @return
	 */
	private String removeRepeatIndex(String viewname){
		String msg = "";
		VDocument vd = ParseXml.docments.get(viewname);
		String keyname = vd.getIndexkey();// 获取维护索引主键信息
		String dbname = vd.getDbname();// 获取数据源信息
		String indextable = vd.getIndextable();// 获取维护索引表名信息
		String sql = "select b.*  from "+indextable +" a , "+ viewname +" b where   sql_type="+MODE+ " and  trs_flag=0 and a."+keyname+"=b."+keyname;// 获取维护索引视图标记为删除的索引记录信息
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
				count++;
				if (count > 100000)
					break;
				String docid = rs.getString("docid");
				String page_time = rs.getString("page_time");
				String doctitle = rs.getString("doctitle");
				Date publishtime = rs.getDate("publishtime");
				String docchannel = rs.getString("docchannel");
				if(page_time==null)
					continue;
				//检查索引数据中是否有重复数据
				if(checkData( viewname,docid, doctitle, docchannel, publishtime)){
					keyvalues.append(rs.getString(keyname)+",");
					String values = keyvalues.toString();
					if(values!=null&&!"".equals(values)){
						values = values.substring(0, values.length()-1);
						String updatesql ="update " +indextable+" set trs_flag=1 where  "+IdFilterSqlUtil.make(keyname, values).getSql();
						logger.info(updatesql);
						updateData(viewname,updatesql,IdFilterSqlUtil.make(keyname, values).getValues());
						logger.info("本次重复数据:"+keyname+"["+values+"]");
					}
					
				};
				//keyvalues.append(rs.getString(keyname)+",");
			}
			rs.close();
			prep.close();
			msg = SUCCESS_MSG;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return FALSE_MSG;
			//e.printStackTrace();
		} finally{
			try {
				if(connection!=null){
					db.releaseConn(dbname, connection);
					}
			} catch (SQLException e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				return FALSE_MSG;
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
	
	
	public boolean checkData(String viewname,String docid,String doctitle,String docchannel,Date publishtime){
		
		VDocument vd = ParseXml.docments.get(viewname);
		String keyname = vd.getIndexkey();// 获取维护索引主键信息
		String path = vd.getPath();
		Directory dir = null;
		File file = new File(path);
		IndexWriter iw = null;
		try {
			dir = FSDirectory.open(file);
			IndexReader reader =  IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Similarity similarity = new DefaultSimilarity();
			searcher.setSimilarity(similarity);
			
			FIndexWriter fw = new FIndexWriter(path);
			iw = fw.getIndexWriter(false);
			
			String KeyWord ="\""+replaceSpecialChar(doctitle)+"\"";//域值
			List<String> sProcessWhere =new ArrayList<String>();
			sProcessWhere.add("doctitle+="+KeyWord);
			sProcessWhere.add("docchannel+="+docchannel);
			BooleanQuery booleanQuery = makequery(sProcessWhere);
			Sort sort = new Sort(new SortField("publishtime", SortField.LONG,true));
			String StartTime = null;
			String EndTime = null;
			
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar =  Calendar.getInstance();
				 calendar.setTime(new Date());
				 EndTime = format1.format(calendar.getTime())+" 23:59:59";
				 calendar.set(calendar.DAY_OF_MONTH, calendar.get(calendar.DAY_OF_MONTH)-6);
				 StartTime = format1.format(calendar.getTime())+" 00:00:00";
			
			Filter filter = null;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			if(StartTime!=null&&!"".equals(StartTime)&&EndTime!=null&&!"".equals(EndTime)&&!"null".equals(StartTime)&&!"null".equals(EndTime)){
				 filter = NumericRangeFilter.newLongRange("crtime", format.parse(StartTime).getTime(), format.parse(EndTime).getTime(), true, true);
			}
			TopFieldCollector c = TopFieldCollector.create(sort, searcher.maxDoc(), false, false, false, false);
			searcher.search(booleanQuery, filter,c);
			TopDocs topDocs = c.topDocs();
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			//List<String> docids = new ArrayList<String>();
			long l_publishtime = publishtime.getTime();
	            for(ScoreDoc scDoc : scoreDocs){
	            	Document doc  = searcher.doc(scDoc.doc);
	            	String n_docid = doc.get(keyname);
	            	if(docid.equals(n_docid))
	            		continue;
	            	String n_publishtime = doc.get("publishtime");
	            	if(Long.parseLong(n_publishtime)>l_publishtime){
	            		//删除库中索引
	            		Term term = new Term(keyname, n_docid);
	    				iw.deleteDocuments(term);
	    				//iw.close();
	            		//return false;
	            	}else{
	            		return true;
	            	}
	            }
	            searcher.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}finally{
			try {
				if(iw!=null){
					iw.commit();
					iw.close();
				}
			}catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		} 
		
		return false;
	}
	
	private BooleanQuery makequery(List<String> sProcessWhere) {
		BooleanQuery booleanQuery = new BooleanQuery();//布尔查询
		try {
			IKAnalyzer analyzer = new IKAnalyzer();
			Query nquery =null;
			for (String query : sProcessWhere) {
				String[] datas = query.split("=");
				String key = datas[0];
				String relation =key.substring(key.length()-1);
				key = key.substring(0,key.length()-1);
				String [] fields = key.split(",");
				MultiFieldQueryParser queryParser  =new MultiFieldQueryParser(Version.LUCENE_36,  fields,  analyzer);
				String value = datas[1];
				nquery = queryParser.parse(value);
				if("+".equals(relation)){
					booleanQuery.add(nquery, BooleanClause.Occur.MUST);
				}else if("-".equals(relation)){
					booleanQuery.add(nquery, BooleanClause.Occur.MUST_NOT);
				}else if("*".equals(relation)){
					booleanQuery.add(nquery, BooleanClause.Occur.SHOULD);
				}
			}
		//语义检查
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return booleanQuery;
	}
	
	/**
	 * 处理特殊字符
	 * @param _sSearchWord
	 * @return
	 */
	public static String replaceSpecialChar(String _sSearchWord)
	  {
	    if ((_sSearchWord == null) || (_sSearchWord.length() <= 0))
	      return _sSearchWord;
	    if ((LIKE_SEARCH_REPLACE_STR == null) || 
	      (LIKE_SEARCH_REPLACE_STR.length <= 0)) {
	      return _sSearchWord;
	    }
	    _sSearchWord = StringUtil.replaceStr(_sSearchWord, "\\", " ");
	    for (int i = 0; i < LIKE_SEARCH_REPLACE_STR.length; i++) {
	      String strChar = LIKE_SEARCH_REPLACE_STR[i];
	      if (strChar.equalsIgnoreCase("\\"))
	        continue;
	      _sSearchWord = StringUtil.replaceStr(_sSearchWord, strChar, "\\" + strChar);
	    }
	    return _sSearchWord;
	  }

}

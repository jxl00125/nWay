package com.provideImpl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.core.bean.SParm;
import com.core.bean.VDocument;
import com.core.bean.VField;
import com.core.index.FIndexSearcher;
import com.core.util.RedHighlighter;
import com.util.ParseXml;
import com.util.StringUtil;

public class IFtsStubSeacheIndex {
	
	private final static int SYS_TYPE=1;
	private final static int USER_TYPE=0;
	
	private final static String RETRIEVAL_TYPE_10="10";//智能检索
	private final static String RETRIEVAL_TYPE_20="20";//模糊检索
	private final static String RETRIEVAL_TYPE_30="30";//精确检索
	
	final static String CHANNEL_FIELD_NAME = "docchannel";//栏目检索字段
	final static String PROVINCE_FIELD_NAME = "province";//省份字段
	final static String QCODES_FIELD_NAME = "qcodes";//企业资质编码检索字段
	final static String INFOSOURCES_FIELD_NAME = "infosource";//数据来源检索字段
	final static String INFOTYPE_FIELD_NAME = "infotype";//信息类型检索字段
	
	final static String DOCCHANNEL = "docchannelid";//文档目录
	final static String CRUSERNAME= "userid";//上传员工
	final static String DEPARTMENT= "departmentid";//所属部门
	final static String DOCTYPE= "doctype";//文档类型
	
	private String docchannel =null;//文档目录
	private String crusername =null;//上传员工
	private String department =null;//所属部门
	private String doctype =null;//文档类型
	
	
	
	public static final String[] LIKE_SEARCH_REPLACE_STR = { "'", "\"", " ", 
	    "+", "-", "/", "\\", "*", "=", ".", "(", ")", ",", "<", ">", ",", 
	    "&", "%", "?" };
	  public static final String[] TRSSERVER_KEYWORDS_DEFINE = { "'", "\"", " ", 
	    "+", "-", "/", "\\", "*", "=", ".", "(", ")", ",", "<", ">", ",", 
	    "&", "!", "@", "$", "^" };
	private String m_Retrieval = "";//检索方式
	private String m_SearchWord = null;//检索词
	private String m_SearchType = RETRIEVAL_TYPE_10;//检索方式
	private String m_LastSearch = "";//最后检索条件
	private String m_CurrSearch = "";//当前检索条件
	private int m_PageSize = 10;//返回记录
	private int m_PageIndex = 1;//当前页码
	private String m_OrderBy = null;//排序字段
	private String OrderBySelf = "1";//默认相关性排序;
	private String StartTime = "";//开始时间
	private String EndTime = "";//结束时间
	
	private String IsAdvSearch = null;//高级检索
	private String Allwords = null;//包含所有关键词
	private String OnlyOneWords = null;//包含任意关键词
	private String Nowords = null;//不包含关键词
	
	private String ChannelIds =null;//栏目信息
	private String Provinces = null;//地区（省份）信息
	private String Qcodes = null ;//企业资质编码信息（用户自定义查询）
	private String Infosources = null;//数据来源（用户自定义查询）
	private String Infotypes = null;//信息类型（用户自定义查询）
	
	private List<String>  corefield = null;//核心检索字段
	private String path = null;//索引存储路径
	private Map<String,SParm > paraMaps=null;
	private VDocument vd = null;

	public String Search(String viewname, String key, String xml) {

		// 1、获取检索视图信息
		// 2、解析检索xml信息
		// 3、拼接查询条件
		// 4、执行检索，获取检索结果信息
		// 5、组装结果集
		String resultxml ="";
		vd = ParseXml.docments.get(viewname);
		initData(xml);
		
		//处理检索词
		//checkSearchWord();
		List<String> sProcessWhere = processSearchWord();
		String s_CurrSearch="";
		for (String string : sProcessWhere) {
			string = string.replace("\"", "%100");
			s_CurrSearch=s_CurrSearch+string+";";
		}
		
		if(!"".equals(s_CurrSearch)){
			m_CurrSearch = s_CurrSearch.substring(0,s_CurrSearch.length()-1);
		}
		
		resultxml =openDocs(viewname,sProcessWhere);
		
		return resultxml;
	}

	/**
	 * 初始化检索条件
	 * @param xml
	 */
	private void initData(String xml) {
		paraMaps = ParseXml.parseXml(xml);
		String mRetrieval ="";
		if(paraMaps.get("Retrieval")!=null){
			 mRetrieval = paraMaps.get("Retrieval").getValue();	
		}else{
			mRetrieval = RETRIEVAL_TYPE_10;
		}
		String mSearchWord = paraMaps.get("SearchWord").getValue();
		String mSearchType = paraMaps.get("SearchType").getValue();
		String PageSize = paraMaps.get("PageSize").getValue();
		String mPageIndex = paraMaps.get("PageIndex").getValue();
		String mOrderBy = paraMaps.get("OrderBy").getValue();
		String mOrderBySelf =paraMaps.get("OrderBySelf").getValue();
		String mLastSearch = paraMaps.get("LastSearch").getValue();
		String mCurrSearch = paraMaps.get("CurrSearch").getValue();
		String IsAdvSearch = paraMaps.get("IsAdvSearch").getValue();
		String Allwords = paraMaps.get("Allwords").getValue();
		String OnlyOneWords = paraMaps.get("OnlyOneWords").getValue();
		
		String Nowords = paraMaps.get("Nowords").getValue();
		String Stime = paraMaps.get("Stime").getValue();
		String Etime = paraMaps.get("Etime").getValue();
		/*String ChannelIds = paraMaps.get("ChannelIds").getValue();
		String Qcodes = paraMaps.get("Qcodes").getValue();
		String Provinces = paraMaps.get("Provinces").getValue();
		
		String Infosources = paraMaps.get("Infosources").getValue();
		String Infotypes = paraMaps.get("Infotypes").getValue();*/
		String docchannel = paraMaps.get("docchannel").getValue();
		String crusername = paraMaps.get("crusername").getValue();
		String department = paraMaps.get("department").getValue();
		String doctype = paraMaps.get("doctype").getValue();
		
		this.docchannel = docchannel;
		this.crusername = crusername;
		this.department = department;
		this.doctype = doctype;
		
		
		this.m_Retrieval = mRetrieval;
		this.m_SearchWord = mSearchWord;
		this.m_SearchType = mSearchType;
		this.OrderBySelf = mOrderBySelf;
		this.IsAdvSearch = IsAdvSearch;
		this.Allwords = Allwords;
		this.OnlyOneWords = OnlyOneWords;
		
		this.Nowords = Nowords;
		this.StartTime = Stime;
		this.EndTime = Etime;
		/*this.ChannelIds = ChannelIds;
		this.Qcodes = Qcodes;
		this.Provinces = Provinces;
		
		this.Infosources = Infosources;
		this.Infotypes = Infotypes;*/
		
		this.m_LastSearch = mLastSearch;
		this.m_CurrSearch = mCurrSearch;
		
		if(mPageIndex!=null&&!"".equals(mPageIndex)){
			this.m_PageIndex = Integer.parseInt(mPageIndex);
		}
		
		if(PageSize!=null&&!"".equals(PageSize)){
			this.m_PageSize = Integer.parseInt(PageSize);
		}
		
		if(vd!=null){
			corefield = new ArrayList<String>();
			List<VField> listField =  vd.getFieldlist();
			for (VField vField : listField) {
				String core = vField.getCore();
				String name = vField.getName();
				if("true".equalsIgnoreCase(core)){
					corefield.add(name);
				}
			}
			
		}
	
	}
	
	/**
	 * 处理检索词
	 * @return
	 */
	private List<String> processSearchWord(){
		List<String> sProcessWhere =new ArrayList<String>();
		
		
		//高级检索
		if("1".equals(this.IsAdvSearch)){
			String querys ="";
			if("any".equals(this.m_SearchType)){
				for (String field : corefield) {
					querys=querys+field+",";
				}
				if(!"".equals(querys)){
					querys = querys.substring(0,querys.length()-1);
				}
				
			}else {
				for (String field : corefield) {
					if(m_SearchType.equals(field)){
						querys=querys+field;
					}
				}
			}
			
			if(Allwords!=null&&!"".equals(Allwords)&&!"null".equals(Allwords)){
				String swords[] = Allwords.split(" ");
				
				for (String word : swords) {
					if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
					sProcessWhere.add(querys+"+="+toLikeSearch(word));
					}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
						sProcessWhere.add(querys+"+="+replaceSpecialChar(word));
					}else if(RETRIEVAL_TYPE_10.equals(m_Retrieval)){
						sProcessWhere.add(querys+"+="+replaceSpecialChar(word));
					}
				}
			}
			
			if(OnlyOneWords!=null&&!"".equals(OnlyOneWords)&&!"null".equals(OnlyOneWords)){
				String swords[] = OnlyOneWords.split(" ");
				for (String word : swords) {
					if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
					sProcessWhere.add(querys+"*="+toLikeSearch(word));
					}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
						sProcessWhere.add(querys+"*="+replaceSpecialChar(word));
					}else if(RETRIEVAL_TYPE_10.equals(m_Retrieval)){
						sProcessWhere.add(querys+"*="+replaceSpecialChar(word));
					}
				}
			}
			
			if(Nowords!=null&&!"".equals(Nowords)&&!"null".equals(Nowords)){
				String swords[] = Nowords.split(" ");
				for (String word : swords) {
					if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
					sProcessWhere.add(querys+"-="+toLikeSearch(word));
					}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
						sProcessWhere.add(querys+"-="+replaceSpecialChar(word));
					}else if(RETRIEVAL_TYPE_10.equals(m_Retrieval)){
						sProcessWhere.add(querys+"-="+replaceSpecialChar(word));
					}
				}
			}
			
			//一般检索
			//String querys ="";
			if(m_SearchWord!=null&&!"".equals(m_SearchWord)&&!"null".equals(m_SearchWord)){
			
			if("any".equals(this.m_SearchType)){
				//------
				for (String field : corefield) {
					querys=querys+field+",";
				}
				if(!"".equals(querys)){
					querys = querys.substring(0,querys.length()-1);
				}
				
				if(m_SearchWord!=null&&!"".equals(m_SearchWord)&&!"null".equals(m_SearchWord)){
						if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
							sProcessWhere.add(querys+"+="+toLikeSearch(m_SearchWord));//精确检索
						}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
							sProcessWhere.add(querys+"+="+replaceSpecialChar(m_SearchWord));//模糊检索
						}else {
							sProcessWhere.add(querys+"+="+replaceSpecialChar(m_SearchWord));//智能检索：未实现同义词、近义词现在和模糊检索一样
						} 
				}
				if("1".equals(m_LastSearch)){
					
					if(m_CurrSearch!=null&&!"".equals(m_CurrSearch)){
						m_CurrSearch = m_CurrSearch.replace("%100", "\"");
						String[] currsearchs = m_CurrSearch.split(";");
						
						for (String currsearch : currsearchs) {
							sProcessWhere.add(currsearch);
							//处理检索词
							String cf = corefield.toString().substring(1,corefield.toString().length()-1);
							cf = cf.replace(" ", "");
							if(currsearch.contains(cf.trim())){
								m_SearchWord =m_SearchWord+currsearch.split("=")[1];
							}
						
						}
					}
				}
			}else {
				for (String field : corefield) {
					if(m_SearchType.equals(field)){
						if(m_SearchWord!=null&&!"".equals(m_SearchWord)&&!"null".equals(m_SearchWord)){
							
							if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
								sProcessWhere.add(querys+field+"+="+ toLikeSearch(m_SearchWord));//精确检索
							}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
								sProcessWhere.add(querys+field+"+="+ replaceSpecialChar(m_SearchWord));//模糊检索
							}else {
								sProcessWhere.add(querys+field+"+="+replaceSpecialChar(m_SearchWord));//智能检索：未实现同义词、近义词现在和模糊检索一样
							} 
							
						}
						if("1".equals(m_LastSearch)){//二次检索
							if(m_CurrSearch!=null&&!"".equals(m_CurrSearch)){
								m_CurrSearch = m_CurrSearch.replace("%100", "\"");
								String[] currsearchs = m_CurrSearch.split(";");
								for (String currsearch : currsearchs) {
									sProcessWhere.add(currsearch);
									//处理检索词
									if(currsearch.contains(field)){
										m_SearchWord =m_SearchWord+currsearch.split("=")[1];
									}
								}
							}
						}
					}
				}
			}
			}
			System.out.println("querys="+querys.toString());
			System.out.println("sProcessWhere="+sProcessWhere.toString());
		}else{
			//一般检索
			String querys ="";
			if(m_SearchWord!=null&&!"".equals(m_SearchWord)&&!"null".equals(m_SearchWord)){
			
			if("any".equals(this.m_SearchType)){
				
				for (String field : corefield) {
					querys=querys+field+",";
				}
				if(!"".equals(querys)){
					querys = querys.substring(0,querys.length()-1);
				}
				if(m_SearchWord!=null&&!"".equals(m_SearchWord)&&!"null".equals(m_SearchWord)){
						if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
							sProcessWhere.add(querys+"+="+toLikeSearch(m_SearchWord));//精确检索
						}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
							sProcessWhere.add(querys+"+="+replaceSpecialChar(m_SearchWord));//模糊检索
						}else {
							sProcessWhere.add(querys+"+="+replaceSpecialChar(m_SearchWord));//智能检索：未实现同义词、近义词现在和模糊检索一样
						} 
				}
				if("1".equals(m_LastSearch)){
					
					if(m_CurrSearch!=null&&!"".equals(m_CurrSearch)){
						m_CurrSearch = m_CurrSearch.replace("%100", "\"");
						String[] currsearchs = m_CurrSearch.split(";");
						
						for (String currsearch : currsearchs) {
							sProcessWhere.add(currsearch);
							//处理检索词
							String cf = corefield.toString().substring(1,corefield.toString().length()-1);
							cf = cf.replace(" ", "");
							if(currsearch.contains(cf.trim())){
								m_SearchWord =m_SearchWord+currsearch.split("=")[1];
							}
						
						}
					}
				}
			}else {
				for (String field : corefield) {
					if(m_SearchType.equals(field)){
						if(m_SearchWord!=null&&!"".equals(m_SearchWord)&&!"null".equals(m_SearchWord)){
							
							if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
								sProcessWhere.add(querys+field+"+="+ toLikeSearch(m_SearchWord));//精确检索
							}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
								sProcessWhere.add(querys+field+"+="+ replaceSpecialChar(m_SearchWord));//模糊检索
							}else {
								sProcessWhere.add(querys+field+"+="+replaceSpecialChar(m_SearchWord));//智能检索：未实现同义词、近义词现在和模糊检索一样
							} 
							
						}
						if("1".equals(m_LastSearch)){//二次检索
							if(m_CurrSearch!=null&&!"".equals(m_CurrSearch)){
								m_CurrSearch = m_CurrSearch.replace("%100", "\"");
								String[] currsearchs = m_CurrSearch.split(";");
								for (String currsearch : currsearchs) {
									sProcessWhere.add(currsearch);
									//处理检索词
									if(currsearch.contains(field)){
										m_SearchWord =m_SearchWord+currsearch.split("=")[1];
									}
								}
							}
						}
					}
				}
			}
			}
		}
		//添加用户自定义查询条件
		Set<String> set = paraMaps.keySet();
		Iterator<String> ita = set.iterator();
		while (ita.hasNext()) {
			String keyname = ita.next();
			SParm parm = paraMaps.get(keyname);
			String type = parm.getType();
			String name = parm.getName();
			String value = parm.getValue();
			
			if(null!=type&&!"".equals(type)){
				int ntype = Integer.parseInt(type);
				if(USER_TYPE==ntype&&value!=null&&!"".equals(value)){
					//sProcessWhere.add(name+"+="+"\""+value+"\"");
					sProcessWhere.add(name+"+="+value);
				}
			}
		}
		return sProcessWhere;
	}

	/**
	 * 执行检索
	 * @param viewname
	 * @param sProcessWhere
	 * @return
	 */
	private String openDocs(String viewname, List<String> sProcessWhere){
		StringBuffer resultxml =  new StringBuffer();
		resultxml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n");
		try {
			vd = ParseXml.docments.get(viewname);
			path = vd.getPath();
			FIndexSearcher  fis = new FIndexSearcher(path);
			IndexSearcher indexsearcher = fis.getIndexSearcher();
			//TopScoreDocCollector results = TopScoreDocCollector.create(indexsearcher.maxDoc(), false);
			BooleanQuery booleanQuery =makequery(sProcessWhere);
			//indexsearcher.search(booleanQuery, results);
			Sort sort = new Sort(new SortField("crtime", SortField.LONG,true));
			//Sort sort = new Sort(new SortField("publishtime", SortField.LONG,true),new SortField("crtime",SortField.LONG,false));
			Filter filter = null;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			if(this.StartTime!=null&&!"".equals(this.StartTime)&&this.EndTime!=null&&!"".equals(this.EndTime)&&!"null".equals(this.StartTime)&&!"null".equals(this.EndTime)){
				 filter = NumericRangeFilter.newLongRange("crtime", format.parse(StartTime).getTime(), format.parse(EndTime).getTime(), true, true);
			}
			
			//TopFieldDocs topFieldDocs = indexsearcher.search(booleanQuery, filter, indexsearcher
			//		.maxDoc(), sort);
			
			int startindex = (m_PageIndex-1)*m_PageSize;
			int endindex = m_PageSize;
			//TopDocs tds1 = results.topDocs();
			//int totals = tds1.totalHits;
			int hm = startindex + m_PageSize; 
			
			  
			TopDocs tds = null;
			if("93".equals(OrderBySelf)){
				//安时间排序
				TopFieldCollector c = TopFieldCollector.create(sort, hm, false, false, false, false);
				indexsearcher.search(booleanQuery,filter,c);
				tds = c.topDocs(startindex,endindex);
			}else if("1".equals(OrderBySelf)){
				//按符合度排序
				TopScoreDocCollector results = TopScoreDocCollector.create(indexsearcher.maxDoc(), false);
				indexsearcher.search(booleanQuery,filter,results);
				tds = results.topDocs(startindex,endindex);
			}else if("2".equals(OrderBySelf)){
				//按上传员工排序
				//创建排序对象.
				//SortField sf1 = new SortField("publishdate", SortField.STRING, true);
				Sort s = new Sort(new SortField("crusername",SortField.STRING ,true));
				TopFieldCollector c = TopFieldCollector.create(s, hm, false, false, false, false);
				indexsearcher.search(booleanQuery,filter,c);
				tds = c.topDocs(startindex,endindex);
			}else if("3".equals(OrderBySelf)){
				//按所属部门排序
				//创建排序对象.
				//SortField sf1 = new SortField("publishdate", SortField.STRING, true);
				Sort se = new Sort(new SortField("department",SortField.STRING ,true));
				TopFieldCollector c = TopFieldCollector.create(se, hm, false, false, false, false);
				indexsearcher.search(booleanQuery,filter,c);
				tds = c.topDocs(startindex,endindex);
			}
			
			int totals = tds.totalHits;
			//TopDocs tds = results.topDocs();
			ScoreDoc[] sd = tds.scoreDocs;
			resultxml.append("<pageBean pagesize=\""+m_PageSize+"\" currentpage=\""+m_PageIndex+"\" processwhere=\""+m_CurrSearch+"\" searchtype=\""+m_SearchType+"\" totalrecord=\""+totals+"\">\n");
			resultxml.append("\t<results>\n");
			
			for (ScoreDoc scoreDoc : sd) {
				Document document = indexsearcher.doc(scoreDoc.doc);
				List<VField> listfield = vd.getFieldlist();
				resultxml.append("\t\t<document>\n");
				for (VField vField : listfield) {
					String name = vField.getName();
					String core = vField.getCore();
					String value = document.get(name);
					if("true".equalsIgnoreCase(core)){
						if("1".equals(this.IsAdvSearch)){
							String searchword = Allwords+""+OnlyOneWords;
							if("any".equals(this.m_SearchType)){
								value = RedHighlighter.getBestFragment(searchword, name, document.get(name));
							}else{
								if(name.equals(this.m_SearchType)){
									value = RedHighlighter.getBestFragment(searchword, name, document.get(name));
								}
							}
						
						}else{
							if("any".equals(this.m_SearchType)){
								value = RedHighlighter.getBestFragment(m_SearchWord, name, document.get(name));
							}else{
								if(name.equals(this.m_SearchType)){
									value = RedHighlighter.getBestFragment(m_SearchWord, name, document.get(name));
								}
							}
							
						}
					}
					//构造xml
					resultxml.append("\t\t\t<field name=\""+name+"\"> <![CDATA["+value+"]]></field>\n");
					//resultxml.append("\t\t\t<field name=\""+name+"\" value=\""+value+"\"/>\n");
				}
				
				
				float score = scoreDoc.score;
				//构造相似度xml
				//resultxml.append("\t\t\t<field name=\"similarity\" value=\""+score+"\"/>\n");
				resultxml.append("\t\t\t<field name=\"similarity\"> <![CDATA["+score+"]]> </field>\n");
				resultxml.append("\t\t</document>\n");
			}
			resultxml.append("\t</results>\n");
			resultxml.append("</pageBean>");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return removeIllegalXmlCharacters(resultxml.toString());
	}
	
	public static boolean isLegalXMLCharacter(int ch) {  
		        if (ch <= 0xD7FF) {  
		           if (ch >= 0x20) {  
		                return true;  
		            } else {  
		                return ch == '\n' || ch == '\r' || ch == '\t';  
		            }  
		        }else{  
		            return (ch >= 0xE000 && ch <= 0xFFFD) || (ch >= 0x10000 && ch <= 0x10FFFF);  
		        }  
		    }  
		    /** 
		     * 去掉xml字符串中的非法字符 
		     *  
		     * @param xml 
		     * @return 
		     */  
	public static String removeIllegalXmlCharacters(String xml) {  
		        if (xml == null || xml.length() <= 0) {  
		            return "";  
		        }  
		        int len = xml.length();  
		        StringBuilder sb = new StringBuilder(len);  
		        for (int i = 0; i < len; i++) {  
		            char ch = xml.charAt(i);  
		            if (isLegalXMLCharacter(ch)) {  
		                sb.append(ch);  
		            }  
		        }  
		       return sb.toString();  
	}  

		
	
	/**
	 * 组装查询条件
	 * @param sProcessWhere
	 * @return
	 */
	private BooleanQuery makequery(List<String> sProcessWhere) {
		BooleanQuery booleanQuery = new BooleanQuery();//布尔查询
		try {
			IKAnalyzer analyzer = new IKAnalyzer();
			if(RETRIEVAL_TYPE_30.equals(m_Retrieval)){
				analyzer.setUseSmart(true);
			}else if(RETRIEVAL_TYPE_20.equals(m_Retrieval)){
				analyzer.setUseSmart(false);
			}else if(RETRIEVAL_TYPE_10.equals(m_Retrieval)){
				analyzer.setUseSmart(true);
			}
			//Analyzer  analyzer = new StandardAnalyzer(Version.LUCENE_36);
			//Analyzer  analyzer = new IKAnalyzer();
			//analyzer.setUseSmart(false);
		Query nquery =null;
		for (String query : sProcessWhere) {
			String[] datas = query.split("=");
			String key = datas[0];
			String relation =key.substring(key.length()-1);
			key = key.substring(0,key.length()-1);
			String [] fields = key.split(",");
			MultiFieldQueryParser queryParser  =new MultiFieldQueryParser(Version.LUCENE_36,  fields,  analyzer);
			//String value = "\""+datas[1]+"\"";
			String value = datas[1];
			nquery = queryParser.parse(value);
			
			
			if("+".equals(relation)){
				booleanQuery.add(nquery, BooleanClause.Occur.MUST);
				/*
				for (String field : fields) {
					 PhraseQuery pq = new PhraseQuery();
					 pq.add(new Term(field,value));
					booleanQuery.add(pq, BooleanClause.Occur.MUST);
				}*/
				
			}else if("-".equals(relation)){
				booleanQuery.add(nquery, BooleanClause.Occur.MUST_NOT);
				/*
				for (String field : fields) {
					 //Term term=new Term(field,value);
					 //TermQuery tquery=new TermQuery(term);
					 PhraseQuery pq = new PhraseQuery();
					 pq.add(new Term(field,value), 0);
					booleanQuery.add(pq, BooleanClause.Occur.MUST_NOT);
				}*/
			}else if("*".equals(relation)){
				booleanQuery.add(nquery, BooleanClause.Occur.SHOULD);
				/*
				for (String field : fields) {
					 Term term=new Term(field,value);
					 TermQuery tquery=new TermQuery(term);
					booleanQuery.add(tquery, BooleanClause.Occur.SHOULD);
				}*/
			}
		}
		
		/**/
		//文档目录检索
		if(this.docchannel!=null&&!"".equals(docchannel)){
			String[] n_ChannelIds = docchannel.split(",");
			QueryParser qp = new QueryParser(Version.LUCENE_36,  DOCCHANNEL,  analyzer);
			String docchannel ="";
			for (String ChannelId : n_ChannelIds) {
				docchannel = docchannel +ChannelId+" or ";
			}
			docchannel = docchannel.trim().substring(0, docchannel.length()-3);
			Query query = qp.parse(docchannel);
			BooleanClause clause= new BooleanClause(query, BooleanClause.Occur.MUST);
			booleanQuery.add(clause);
		}
		
		/**/
		//上传员工检索
		if(this.crusername!=null&&!"".equals(crusername)){
			String[] n_crusername = crusername.split(",");
			QueryParser qp = new QueryParser(Version.LUCENE_36,  CRUSERNAME,  analyzer);
			String crusername ="";
			
			for (String province : n_crusername) {
				crusername = crusername +province+" or ";
			}
			crusername = crusername.trim().substring(0, crusername.length()-3);
			Query query = qp.parse(crusername);
			
			 BooleanClause clause= new BooleanClause(query, BooleanClause.Occur.MUST);
			 booleanQuery.add(clause);
		}
		
		//所属部门检索
		/**/
		if(this.department!=null&&!"".equals(department)){
			String[] n_department = department.split(",");
			QueryParser qp = new QueryParser(Version.LUCENE_36,  DEPARTMENT,  analyzer);
			String department ="";
			for (String department_ : n_department) {
				department = department +department_+" or ";
			}
			department = department.trim().substring(0, department.length()-3);
			Query query = qp.parse(department);
			
			BooleanClause clause= new BooleanClause(query, BooleanClause.Occur.MUST);
			booleanQuery.add(clause);
		}
		
		//文档类型检索
		/**/
		if(this.doctype!=null&&!"".equals(doctype)){
			String[] n_doctype = doctype.split(",");
			QueryParser qp = new QueryParser(Version.LUCENE_36,  DOCTYPE,  analyzer);
			String doctype ="";
			for (String doctype_ : n_doctype) {
				doctype = doctype +doctype_+" or ";
			}
			doctype = doctype.trim().substring(0, doctype.length()-3);
			Query query = qp.parse(doctype);
			
			BooleanClause clause= new BooleanClause(query, BooleanClause.Occur.MUST);
			booleanQuery.add(clause);
		}
		
		//信息类型检索
		/**/
		/*if(this.Infotypes!=null&&!"".equals(Infotypes)){
			String[] n_Qcodes = Infotypes.split(",");
			QueryParser qp = new QueryParser(Version.LUCENE_36,  INFOTYPE_FIELD_NAME,  analyzer);
			String Qcodes ="";
			for (String Qcode : n_Qcodes) {
				Qcodes = Qcodes +Qcode+" or ";
			}
			Qcodes = Qcodes.trim().substring(0, Qcodes.length()-3);
			Query query = qp.parse(Qcodes);
			
			BooleanClause clause= new BooleanClause(query, BooleanClause.Occur.MUST);
			booleanQuery.add(clause);
		}*/
		
		
		/*
		//时间范围查询
		if(this.StartTime!=null&&!"".equals(this.StartTime)&&this.EndTime!=null&&!"".equals(this.EndTime)){
		}*/
		
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
	
	/**
	 * 精确检索
	 * @param _sSearchWord
	 * @return
	 */
	private String toLikeSearch(String _sSearchWord)
	  {
	    if ((_sSearchWord == null) || (_sSearchWord.trim().length() <= 0)){
	    	 return _sSearchWord;
	    }
	    //_sSearchWord = replaceSpecialChar(_sSearchWord);
	    _sSearchWord = "\"" + _sSearchWord + "\"";
	    return _sSearchWord;
	  }
	/**
	 * 中文字符串截取
	 * @param s
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public static String bSubstring(String s, int length) throws Exception {  
		  
	    byte[] bytes = s.getBytes("Unicode");  
	    int n = 0; // 表示当前的字节数  
	    int i = 2; // 要截取的字节数，从第3个字节开始  
	    for (; i < bytes.length && n < length; i++) {  
	        // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节  
	        if (i % 2 == 1) {  
	            n++; // 在UCS2第二个字节时n加1  
	        } else {  
	            // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节  
	            if (bytes[i] != 0) {  
	                n++;  
	            }  
	        }  
	  
	    }  
	    // 如果i为奇数时，处理成偶数  
	    /* 
	     * if (i % 2 == 1){ // 该UCS2字符是汉字时，去掉这个截一半的汉字 if (bytes[i - 1] != 0) i = 
	     * i - 1; // 该UCS2字符是字母或数字，则保留该字符 else i = i + 1; } 
	     */  
	    // 将截一半的汉字去掉，不加入到结果字符串  
	    if (i % 2 == 1) {  
	        i = i - 1;  
	    }  
	    return new String(bytes, 0, i, "Unicode"); 
	}
	public String getSearchWord() {
		return m_SearchWord;
	}

	public void setSearchWord(String mSearchWord) {
		m_SearchWord = mSearchWord;
	}

	public String getSearchType() {
		return m_SearchType;
	}

	public void setSearchType(String mSearchType) {
		m_SearchType = mSearchType;
	}

	public String getLastSearch() {
		return m_LastSearch;
	}

	public void setLastSearch(String mLastSearch) {
		m_LastSearch = mLastSearch;
	}

	public String getCurrSearch() {
		return m_CurrSearch;
	}

	public void setCurrSearch(String mCurrSearch) {
		m_CurrSearch = mCurrSearch;
	}

	public int getPageSize() {
		return m_PageSize;
	}

	public void setPageSize(int mPageSize) {
		m_PageSize = mPageSize;
	}

	public int getPageIndex() {
		return m_PageIndex;
	}

	public void setPageIndex(int mPageIndex) {
		m_PageIndex = mPageIndex;
	}

	public String getOrderBy() {
		return m_OrderBy;
	}

	public void setOrderBy(String mOrderBy) {
		m_OrderBy = mOrderBy;
	}

	public String getIsAdvSearch() {
		return IsAdvSearch;
	}

	public void setIsAdvSearch(String isAdvSearch) {
		IsAdvSearch = isAdvSearch;
	}

	public String getAllwords() {
		return Allwords;
	}

	public void setAllwords(String allwords) {
		Allwords = allwords;
	}

	public String getOnlyOneWords() {
		return OnlyOneWords;
	}

	public void setOnlyOneWords(String onlyOneWords) {
		OnlyOneWords = onlyOneWords;
	}

	public String getStartTime() {
		return StartTime;
	}

	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	public String getNowords() {
		return Nowords;
	}

	public void setNowords(String nowords) {
		Nowords = nowords;
	}

	public String getChannelIds() {
		return ChannelIds;
	}

	public void setChannelIds(String channelIds) {
		ChannelIds = channelIds;
	}

	public String getQcodes() {
		return Qcodes;
	}

	public void setQcodes(String qcodes) {
		Qcodes = qcodes;
	}

	public String getProvinces() {
		return Provinces;
	}

	public void setProvinces(String provinces) {
		Provinces = provinces;
	}

	public String getOrderBySelf() {
		return OrderBySelf;
	}

	public void setOrderBySelf(String orderBySelf) {
		OrderBySelf = orderBySelf;
	}

	public String getInfosources() {
		return Infosources;
	}

	public void setInfosources(String infosources) {
		Infosources = infosources;
	}

	public String getInfotypes() {
		return Infotypes;
	}

	public void setInfotypes(String infotypes) {
		Infotypes = infotypes;
	}

	public String getDocchannel() {
		return docchannel;
	}

	public void setDocchannel(String docchannel) {
		this.docchannel = docchannel;
	}

	public String getCrusername() {
		return crusername;
	}

	public void setCrusername(String crusername) {
		this.crusername = crusername;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	
	public static void main(String[] args) {
		
		System.out.println(replaceSpecialChar("酒店*项目"));
	}
}

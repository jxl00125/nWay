package com.provideImpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.util.Md5CheckUtil;

public class IFtStubFullTextHit {
	
	static Logger logger = Logger.getLogger(IFtStubClearData.class);
	final static String SUCCESS_MSG = "success";
	final static String ERROR_MSG = "error";
	final static String FALSE_MSG = "error";
	
	final static String ALL_WORD_AND = "10";
	
	final static String ALL_WORD_OR = "20";
	
	final static String DATA_TYPE_01 = "01";//精确匹配
	
	final static String DATA_TYPE_02 = "02";//关键词匹配
	
	final static String DATA_TYPE_10 = "10";//未匹配
	final static String FIELD_NAME = "textcontend";//检索字段名称
	final static String[] SEGMENTS_STR = { "：","，","、",";","。","；","（1）","（2）","（3）","（4）","（5）","（6）","（7）","（8）","（9）","或","和","及","与","且","或者","并且"};//分割符
	
	final static String[] CONTAINS_LEVEL_STR = { "甲级","乙级","丙级","丁级","特级","一级","二级","三级","四级" ,"五级" ,"壹级" ,"貮级" ,"叁级","肆级","伍级","综合资质"};//包含字符,对句继续分割
	
	public static String[] NCONTAINS_STR = { "及以上","及其以上","燃气、轨道除外","燃气工程、轨道交通工程除外","发展和改革委员会","住房和城乡规划建设部" ,"港口与航道工程","电子与智能化工程","港口与海岸工程","水工金属结构制作与安装工程","爆破与拆除工程","设计与施工一体化","批发配送与物流","石油机械制造与修理","制盐与盐化工程","家电电子与日用机械","石油机械制造与修理"};//包含字符不分，整句解析
	public static String[] NCONTAINSN_STR = { "#bona_it_01","#bona_it_02","#bona_it_03","#bona_it_04","#bona_it_05","#bona_it_06","#bona_it_07","#bona_it_08","#bona_it_09","#bona_it_10","#bona_it_11","#bona_it_12","#bona_it_13","#bona_it_14","#bona_it_15","#bona_it_16" ,"#bona_it_17","#bona_it_18","#bona_it_19"};//包含字符不分，整句解析
	
	final static String[] FILTER_STR = { "建造师","项目经理","项目负责人","总负责人","专业负责人","技术负责人","总监","监理工程师","勘察负责人","设计负责人","总设计师" };//剔除关键词后30个字符
	
	final static String[] SEGMENTS_CONTAINS_STR = { "：","，","、","或","和","及","与"};//分割符
	
	public static String[] FILTER_REPLAC_STR = { "一级建造师","二级建造师","一级注册建造师" };//替换文本
	
	public static String[] SEGMENTS_REPLAC_STR = { "，","。","；","1.","2.","3.","4.","5.","6.","7.","8.","9.","1、","2、","3、","4、","5、","6、","7、","8、","9、","(1)","(2)","(3)","(4)","(5)","(6)","(7)","(8)","(9)","1)","2)","3)","4)","5)","6)","7)","8)","9)"};//分割符
	
	final static int KEY_WORD_LENG = 50;
	
	final static int LENG = 30;
	
	private List<String> list = new ArrayList<String>();//需解析短语
	
	private List<String> prelist = new ArrayList<String>();//预处理短语
	
	final static int SLOP = 15;
	
	public String chectTextHistData(String text, String dtype,String keywords,String fullkeywords,String key){

		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			//logger.info("开始执行匹配分析");
			list = new ArrayList<String>();
			prelist = new ArrayList<String>();
			msg = chectTextHistDatas(text,dtype ,keywords,fullkeywords);
		}
		Date end = new Date();
			//logger.info("匹配分析结束" + (end.getTime() - start.getTime())+ " total milliseconds");
		return msg;
	}
	
	public String stopSegments(String text ,String dtype,String key){
		//过滤文本特殊字符
		text = text.replace(" ", "");
		text = text.replace("（", "");
		text = text.replace("）", "");
		text = text.replace("(", "");
		text = text.replace(")", "");
		//屏蔽项目人员职业资格的关键词
		text = filterText(text);
		
		//全屏蔽
		text = repalceAllText(text);
		
		//分句屏蔽
		text = repalceText(text);
		
		preText(text);//处理文本按照标点符文分，过滤不要的文本信息
		
		//屏蔽项目人员职业资格的关键词
		/*
		for(int i=0;i<list.size();i++){
			String temp = list.get(i);
			temp=convertText(temp);
			list.set(i, temp);
		}
		*/
		StringBuffer sb = new StringBuffer();
		
		for (String str : list) {
			sb.append(str+"#bxkc");
		}
		return sb.toString();
	}
	
	private String repalceAllText(String text) {
		String temptext =null;
		if(text.contains("注册建造师类别及等级")){
			int i = text.indexOf("注册建造师类别及等级");
			temptext = text.substring(i);
			if(temptext.contains("企业资质")){
				return temptext;
			}else{
				return text.substring(0,i);
			}
		}else if(text.contains("从业人员资格要求")){
			int i = text.lastIndexOf("从业人员资格要求");
			temptext = text.substring(i);
			System.out.println(temptext);
			if(!temptext.contains("承包资质")){
				 return text.substring(0,i);
			}
			
		}else if(text.contains("人员资质要求")){
			int i = text.indexOf("人员资质要求");
			temptext = text.substring(i);
			if(!temptext.contains("承包资质")){
				 return text.substring(0,i);
			}
		}
		return text;
	}
	private String repalceText(String text) {
		//分割语句
		StringBuffer sb = new StringBuffer();
		for (String segment : SEGMENTS_REPLAC_STR) {
			text = text.replace(segment, segment+"#bxkc");
		}
		String[] texts = text.split("#bxkc");
		for (String s_text : texts) {
			if(checkText(s_text)){
				sb.append(s_text);
			}
		}
		return sb.toString();
	}
	
	private boolean checkText(String text){
		
		if(text.contains("项目经理")||text.contains("项目负责人")||text.contains("建造师")){
			if(text.contains("承包")){
				return true;
			}else{
				return false;
			}
		}
		
		return true;
	}
	
	private String filterText(String text) {
		// TODO Auto-generated method stub
		for (String rs : FILTER_REPLAC_STR) {
			text = text.replace(rs, "");
		}
		return text;
	}



	private String chectTextHistDatas(String text, String keyword ,String keywords,String fullkeywords){
		if(keywords==null||"".equals(keywords)||text==null||"".equals(text)){
			return DATA_TYPE_10;
		}
		
		if(text!=null&&!"".equals(text)){
			String [] s_list = text.split("#bxkc");
			list =  new ArrayList<String>();
			for (String str : s_list) {
				list.add(str);
			}
		}
		/*
		//过滤文本特殊字符
		text = text.replace(" ", "");
		text = text.replace("（", "");
		text = text.replace("）", "");
		text = text.replace("(", "");
		text = text.replace(")", "");
		
		preText(text);//处理文本按照标点符文分，过滤不要的文本信息
		//屏蔽项目人员职业资格的关键词
		for(int i=0;i<list.size();i++){
			String temp = list.get(i);
			temp=convertText(temp);
			list.set(i, temp);
			
		}
		*/
		
		boolean t_falg = true;
		if(keyword!=null&&!"".equals(keyword)){
			
			if("true".equals(keyword)){
				t_falg = true;//按顺序匹配
			}else if("false".equals(keyword)) {
				t_falg = false;//按间距匹配
			}
		}
		//
		t_falg = false;
		
		String n_keywords[] = keywords.split(";");
		 // 实例化IKAnalyzer分词器
		 //Analyzer analyzer = new IKAnalyzer();
		 IKAnalyzer analyzer = new IKAnalyzer();
		 //使用智能分词
		 analyzer.setUseSmart(false);
		 //Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		 Directory directory = null;
		 IndexWriter iwriter = null;
		 IndexSearcher isearcher = null;
		 try {
		 // 建立内存索引对象
		 directory = new RAMDirectory();
		 IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,analyzer);
		 iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		 iwriter = new IndexWriter(directory, iwc);
		 
		 if(list!=null&&list.size()>0){
			 
			for (String str : list) {
				 Document doc = new Document();
				 doc.add(new Field(FIELD_NAME, str, Field.Store.YES,
				 Field.Index.ANALYZED));
				 iwriter.addDocument(doc);
			}
		 }else{
			 Document doc = new Document();
			 doc.add(new Field(FIELD_NAME, text, Field.Store.YES,
			 Field.Index.ANALYZED));
			 iwriter.addDocument(doc);
		 }
		 
		 iwriter.close();
		 // 实例化搜索器
		 IndexReader reader =  IndexReader.open(directory);
		 isearcher = new IndexSearcher(reader);
		 Similarity similarity = new DefaultSimilarity();
		 isearcher.setSimilarity(similarity);
		 
		 //精确检索
		 QueryParser qp =new QueryParser(Version.LUCENE_36,FIELD_NAME, analyzer);
		 qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		 Query query = qp.parse("\""+fullkeywords+"\"");//不分词
		 
		 TopDocs topDocs = isearcher.search(query, 1);

		 if(topDocs.totalHits!=0){
			 return DATA_TYPE_01;
		 }
		 
		 //关键词坡度检索（资质关键顺序检索）
		 for(int i=0;i<n_keywords.length;i++){
			 	String dtype  = n_keywords[i].split("_")[0];
			 	String words = n_keywords[i].split("_")[1];
			 	if(ALL_WORD_AND.equals(dtype)){
			 		String n_words[] = words.split(" ");
			 		int j=0;
			 		for (String word : n_words) {
			 			if(word!=null&&!"".equals(word)){
			 				j++;
			 			}
					}
			 		
			 		SpanQuery[] sqs =new SpanTermQuery[j];
			 		 j=0;
			 		for (String word : n_words) {
			 			if(word!=null&&!"".equals(word)){
			 				sqs[j]= new SpanTermQuery(new Term(FIELD_NAME,word));
			 				j++;
			 			}
					}
			 		
			 		SpanNearQuery snq_query=new SpanNearQuery(sqs,SLOP,t_falg);
			 		TopDocs topDocskeword =  isearcher.search(snq_query,1);
					 if(topDocskeword.totalHits!=0){
						 System.out.println("pre#########################");
						 for (String str : prelist) {
							 	//System.out.println(str);
						}
						 System.out.println("pre***************");
						 
						 
						 
						 System.out.println("#########################");
						 for (String str : list) {
							 	//System.out.println(str);
						}
						 System.out.println("__________________________");
						 System.out.println(text);
						 System.out.println(words);
						 ScoreDoc[] sd = topDocskeword.scoreDocs;
						 for (ScoreDoc scoreDoc : sd) {
							 Document document = isearcher.doc(scoreDoc.doc);
							 System.out.println("命中语句：-->"+document.get(FIELD_NAME));
						 }
						 return DATA_TYPE_02;
					 }
			 	}
		 }

		 } catch (CorruptIndexException e) {

			 e.printStackTrace();

		 } catch (LockObtainFailedException e) {

			 e.printStackTrace();

		 } catch (IOException e) {

			 e.printStackTrace();

		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		 if (isearcher != null) {

		 try {
			 isearcher.close();

		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 }

		 if (directory != null) {
		 try {
			 directory.close();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 }
		 }
		
		return DATA_TYPE_10;
	}

	

	private String convertText(String text) {
		// TODO Auto-generated method stub
		String temp ="";
		for (int i = 0; i < FILTER_STR.length; i++) {
			 String strChar = FILTER_STR[i];
		      if(text.contains(strChar)){
		    	 int j =  text.indexOf(strChar);
		    	 temp = text.substring(0,j);
		    	 int length = text.length();
		    	 if((j+LENG)<length){
		    		 text = temp + text.substring(j+30);
		    	 }else{
		    		 text = temp ;
		    	 }
		    	 break;
		      };
		}
		
		return text;
	}

	/**
	 * 预处理文本
	 * @param text
	 * @return
	 */
	private void preText(String text) {
		// TODO Auto-generated method stub
		//屏蔽关键词
		/*
		for (int i = 0; i < NCONTAINS_STR.length; i++) {
		      String strChar = NCONTAINS_STR[i];
		      if(text.contains(strChar)){
		    	  text = text.replace(strChar, NCONTAINSN_STR[i]);
		      };
		}
		*/
		//分割语句
		for (String segment : SEGMENTS_STR) {
			text = text.replace(segment, segment+"#bxkc");
		}
		//System.out.println(text);
		//System.out.println("********************************");
		String[] texts = text.split("#bxkc");
		
		//添加预处理语句
		for (String text_s : texts) {
			//System.out.println(text_s);
			prelist.add(text_s);
		}
		
		if(texts!=null){
			StringBuffer sb  = new StringBuffer();
			for (int i=0;i<texts.length ;i++) {
					//还原文本语句
					texts[i] = reductionText(texts[i]);
					if(verificationText(texts[i])){
						i=isContainLevel(texts[i],i);//句中要包含级别信息
					}else{
						list.add(texts[i]);
					}
			}
		}
	}
	
	public String reductionText(String text){
		
		for (int i = 0; i < NCONTAINS_STR.length; i++) {
		      String strChar = NCONTAINS_STR[i];
		      if(text.contains(NCONTAINSN_STR[i])){
		    	  text = text.replace(NCONTAINSN_STR[i],strChar );
		      };
		}
		
		return text;
	}
	
	public int isContainLevel(String text,int i){
		int t = i;
		boolean flag = false;
		for (String str : CONTAINS_LEVEL_STR) {
			if(text.contains(str)){
				list.add(text);
				flag = true;
				break;
			}
		}
		if(!flag){
			i++;
			if(i<prelist.size()){
				t = isContainLevel(text+prelist.get(i),i);
			}else{
				list.add(text);
			}
		}
		return t;
	}
	
	/**
	 * 验证短语后面是否包含："，","、","或","和","及","与"
	 * @param text
	 * @return
	 */
	public boolean verificationText(String text){
			String temp = "";
			for (String str : SEGMENTS_CONTAINS_STR) {
				if(text.length()>str.length()){
					temp = text.substring(text.length()-str.length());
				}
				if(str.equals(temp)){
					return true;
				}
			}
			
			return false;
	}

	public boolean chenckText(String text){
		
		 for (int i = 0; i < NCONTAINS_STR.length; i++) {
		      String strChar = NCONTAINS_STR[i];
		      if(text.contains(strChar)){
		    	  return true;
		      };
		    }
		
		return true;
	}

	
	
}

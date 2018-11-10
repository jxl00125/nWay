package com.provideImpl;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.util.Md5CheckUtil;

public class IFtStubCheckTextHit {
	
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
	
	final static int KEY_WORD_LENG = 50;
	
	public String chectTextHistData(String text, String keyword,String keywords,String fullkeywords,String key){

		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			//logger.info("开始执行匹配分析");
			msg = chectTextHistDatas(text,keyword ,keywords,fullkeywords);
		}
		Date end = new Date();
		//logger.info("匹配分析结束" + (end.getTime() - start.getTime())
		//		+ " total milliseconds");
		return msg;
	}
	
	private String chectTextHistDatas(String text, String keyword ,String keywords,String fullkeywords){
		if(keywords==null||"".equals(keywords)||text==null||"".equals(text)){
			return DATA_TYPE_10;
		}
		//过滤文本特殊字符
		text = text.replace(" ", "");
		text = text.replace("（", "");
		text = text.replace("）", "");
		text = text.replace("(", "");
		text = text.replace(")", "");
		
		//过滤文本(定位文本)
		String t_text = null;
		if(keyword!=null&&!"".equals(keyword)){
			t_text =null; //filterTextData(text,keyword);
		}
		
		if(t_text!=null&&!"".equals(t_text)){
			text = t_text;
		}
		
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
		 
		 Document doc = new Document();
		 //System.out.println(text);
		 doc.add(new Field(FIELD_NAME, text, Field.Store.NO,
		 Field.Index.ANALYZED));
		 iwriter.addDocument(doc);
		 iwriter.close();
		 // 实例化搜索器
		 IndexReader reader =  IndexReader.open(directory);
		 isearcher = new IndexSearcher(reader);
		 Similarity similarity = new DefaultSimilarity();
		 isearcher.setSimilarity(similarity);
		 
		 //精确检索
		 QueryParser qp =new QueryParser(Version.LUCENE_36,FIELD_NAME, analyzer);
		 qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		 //Query query = qp.parse(fullkeywords);
		 Query query = qp.parse("\""+fullkeywords+"\"");//不分词
		 
		 TopDocs topDocs = isearcher.search(query, 1);

		 if(topDocs.totalHits!=0){
			 return DATA_TYPE_01;
		 }
		 
		 //QueryParser wordsqp =new QueryParser(Version.LUCENE_36,FIELD_NAME, analyzer);
	 	 //wordsqp.setDefaultOperator(QueryParser.AND_OPERATOR);
		 
		 String [] fields = {FIELD_NAME};
		 
		 MultiFieldQueryParser queryParser  =new MultiFieldQueryParser(Version.LUCENE_36,  fields,  analyzer);
		 //关键词检索
		 BooleanQuery booleanQuery = new BooleanQuery();//布尔查询
		 Query nquery =null;
		 
		 for(int i=0;i<n_keywords.length;i++){
			 	String dtype  = n_keywords[i].split("_")[0];
			 	String words = n_keywords[i].split("_")[1];
			 	if(ALL_WORD_AND.equals(dtype)){
			 		String n_words[] = words.split(" ");
			 		for (String word : n_words) {
			 			if(word!=null&&!"".equals(word)){
			 				 //nquery = queryParser.parse(word);
			 				nquery = queryParser.parse("\""+word+"\"");//不分词
				 			booleanQuery.add(nquery, BooleanClause.Occur.MUST);
			 			}
					}
			 		
			 	}else if(ALL_WORD_OR.equals(dtype)){
			 		
			 		String n_words[] = words.split(" ");
			 		for (String word : n_words) {
			 			if(word!=null&&!"".equals(word)){
			 			   //nquery = queryParser.parse(word);
			 			   nquery = queryParser.parse("\""+word+"\"");//不分词
			 			   booleanQuery.add(nquery, BooleanClause.Occur.SHOULD);
			 			}
			 			
					}
			 	}
		 }
		 
		 TopDocs topDocskeword =  isearcher.search(booleanQuery,1);
		 if(topDocskeword.totalHits!=0){
			 return DATA_TYPE_02;
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

	private String filterTextData(String text,String keyword){
		 // 实例化IKAnalyzer分词器
		 Analyzer analyzer = new IKAnalyzer();
		 Directory directory = null;
		 IndexWriter iwriter = null;
		 IndexSearcher isearcher = null;
		 String word =null;
		 // 建立内存索引对象
		 try {
		 directory = new RAMDirectory();
		 IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,analyzer);
		 iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		 iwriter = new IndexWriter(directory, iwc);
		 Document doc = new Document();
		 doc.add(new Field(FIELD_NAME, text, Field.Store.NO,
		 Field.Index.ANALYZED));
		 iwriter.addDocument(doc);
		 iwriter.close();
		 // 实例化搜索器
		 IndexReader reader =  IndexReader.open(directory);
		 isearcher = new IndexSearcher(reader);
		 Similarity similarity = new DefaultSimilarity();
		 isearcher.setSimilarity(similarity);
		 QueryParser qp =new QueryParser(Version.LUCENE_36,"textcontend", analyzer);
		 qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		 Query query = qp.parse(keyword);
		 TopDocs topDocs = isearcher.search(query, 1);
		 
		 if(topDocs.totalHits!=0){
			 	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
				Document targetDoc = isearcher.doc(scoreDocs[0].doc);
				System.out.println(targetDoc.getField(FIELD_NAME).getIndexOptions().toString());
				word = targetDoc.get(FIELD_NAME);
				System.out.println(text);
				System.out.println(word+"321"+keyword);
				word = word.substring(word.indexOf(keyword)-KEY_WORD_LENG, word.indexOf(keyword)+KEY_WORD_LENG);
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
		
		return word;
		
	}
	
	
}

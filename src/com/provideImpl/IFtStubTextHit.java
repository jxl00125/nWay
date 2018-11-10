package com.provideImpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

public class IFtStubTextHit {
	
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
	
	final static int SLOP = 15;
	
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
	
	
	
	public String wordSegments(String text ,String dtype,String key){
		IKAnalyzer analyzer = new IKAnalyzer();
		// 使用智能分词
		if("10".equals(dtype)){
			analyzer.setUseSmart(true);
		}else if("20".equals(dtype)){
			analyzer.setUseSmart(false);
		}
		StringBuffer sb = new StringBuffer();
		TokenStream tokenStream = analyzer.tokenStream("content",
				new StringReader(text));
		tokenStream.addAttribute(CharTermAttribute.class);
		int i =0;
		try {
			while (tokenStream.incrementToken()) {
				CharTermAttribute charTermAttribute = tokenStream
						.getAttribute(CharTermAttribute.class);
				i++;
				//System.out.println(i+"序号："+charTermAttribute.toString());
				sb.append(charTermAttribute.toString()+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return ERROR_MSG;
			//e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private String chectTextHistDatas(String text, String keyword ,String keywords,String fullkeywords){
		//System.out.println(text);
		if(keywords==null||"".equals(keywords)||text==null||"".equals(text)){
			return DATA_TYPE_10;
		}
		
		//过滤文本特殊字符
		text = text.replace(" ", "");
		text = text.replace("（", "");
		text = text.replace("）", "");
		text = text.replace("(", "");
		text = text.replace(")", "");
		
		boolean t_falg = true;
		if(keyword!=null&&!"".equals(keyword)){
			
			if("true".equals(keyword)){
				t_falg = true;//按顺序匹配
			}else if("false".equals(keyword)) {
				t_falg = false;//按间距匹配
			}
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
		 doc.add(new Field(FIELD_NAME, text, Field.Store.YES,
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
						 System.out.println(text);
						 System.out.println(words);
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

package com.core.util;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.util.StringUtil;

public class RedHighlighter {
	
	public static String getBestFragment(String keyWord,String field,
			String word) {
		String c ="";
		try {
			Query query = null;
			if(null==word||"".equals(word))
			return word;
			
			if("doctitle".equals(field)){
				word = StringUtil.truncateStr(word, 120);
			}else if("doccontent".equals(field)){
				word = StringUtil.truncateStr(word, 120);
			}else {
				word =StringUtil.truncateStr(word, 200);
			}
			
			if(keyWord!=null&&!"".equals(keyWord)){
				QueryParser queryParser  =new QueryParser(Version.LUCENE_36,  field,  new IKAnalyzer());
				query = queryParser.parse(keyWord);
				SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
						"%10font color='red'%20", "%10/font%20");
				Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
						new QueryScorer(query));
				highlighter.setTextFragmenter(new SimpleFragmenter(150));
				c = highlighter.getBestFragment(new IKAnalyzer(),field, word);
				
				if(c==null){
					 c = word; 
				 }
			}else{
				c = word;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	

}

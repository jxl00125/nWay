package com.test;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

	public class TestIKAnalyzer2 {
	    public static void main(String[] args) throws IOException {
	        //String text = "2012年欧洲杯四强赛";
	    	String text = "房屋建筑工程施工总承包企业一级资质";
	    	//String text = "总承包";
	        Analyzer analyzer = new IKAnalyzer(false);
	        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
	        CharTermAttribute term= tokenStream.addAttribute(CharTermAttribute.class);  
	        tokenStream.reset();
	        while(tokenStream.incrementToken()){  
	            System.out.print(term.toString() + "/");  
	        }  
	        tokenStream.end();
	        tokenStream.close();
	        
	        String sb = "为：500.00";
	        char [] s_c = sb.toCharArray();
			
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
	        
	        System.out.println(new String(s_c).toString());
	        
	    }
	    
	    
	    
	}

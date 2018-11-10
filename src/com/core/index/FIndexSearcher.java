package com.core.index;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;

public class FIndexSearcher {

private String FilePath;

  private static IndexSearcher searcher=null;
  
  private static long time=0;
  
  public  static long reopentiem=300000;//1800000;
  
	
	public FIndexSearcher(String sFilePath){
		FilePath= sFilePath;
	}
	
	public IndexSearcher getIndexSearcher(){
		Directory dir = null;
		File file = new File(FilePath);
		try {
			long nowtime = new Date().getTime();
			boolean flag = true;
			if(searcher==null){
				//dir = FSDirectory.open(file);
				dir = NIOFSDirectory.open(file);
				IndexReader reader =  IndexReader.open(dir,true);
				searcher = new IndexSearcher(reader);
				Similarity similarity = new DefaultSimilarity();
				searcher.setSimilarity(similarity);
				time = new Date().getTime();
				flag = false; 
			}
			if(flag){
				if(nowtime-time>reopentiem){
					dir = NIOFSDirectory.open(file);
					IndexReader reader =  IndexReader.open(dir,true);
					searcher = new IndexSearcher(reader);
					Similarity similarity = new DefaultSimilarity();
					searcher.setSimilarity(similarity);
					time = new Date().getTime();
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searcher;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	
}

package com.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TestBxkcSearch {
	
	public static void main(String[] args) {
		
		String path ="D://trsserver//bxkcdata//v_document"; //vd.getPath();
		Directory dir = null;
		File file = new File(path);
			try {
			dir = FSDirectory.open(file);
			IndexReader reader =  IndexReader.open(dir);//1420  1274 146
			IndexSearcher searcher = new IndexSearcher(reader);
			System.out.println(reader.maxDoc());  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}

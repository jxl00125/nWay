package com.core.index;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class FIndexWriter {
	
	private String FilePath;
	
	public FIndexWriter(String sFilePath){
		FilePath= sFilePath;
	}
	
	
	
	/**获取维护索引操作句柄
	 * @param type
	 * @return
	 */
	public IndexWriter getIndexWriter(boolean type){

		// 打开索引文件
		Directory dir = null;
		File file = new File(FilePath);// 这个是索引库
		if (!file.exists()) {
			file.mkdirs();
		}
		IndexWriter writer =null;
		try {
			dir = FSDirectory.open(file);
			IKAnalyzer analyzer = new IKAnalyzer();
			analyzer.setUseSmart(true);
			/* 先是创建索引库在追加索引到索引库，true代表创建，false代表追加 */
			boolean flag = type;
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,
					analyzer);
			if (flag) {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			} else {
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			}
			 writer = new IndexWriter(dir, iwc);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return writer;
	
	}

}

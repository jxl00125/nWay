package com.provideImpl;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.core.bean.VDocument;
import com.core.index.FIndexWriter;
import com.util.Md5CheckUtil;
import com.util.ParseXml;

public class IFtsStubDeleteChnnelIndex {

	static Logger logger = Logger.getLogger(IFtsStubDeleteChnnelIndex.class);
	final static String MODE = "3";
	final static String FALSE_MSG = "";
	final static String SUCCESS_MSG = "success";

	public String deleteIndex(String viewname, String key,String chnnelid) {
		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			logger.info("开始分类删除索引");
			msg = deleteIndex(viewname,chnnelid);
		}
		Date end = new Date();
		logger.info("删除分类索引结束" + (end.getTime() - start.getTime())
				+ " total milliseconds");
		return msg;
	}

	private String deleteIndex(String viewname,String chnnelid) {

		VDocument vd = ParseXml.docments.get(viewname);
		String path = vd.getPath();
		FIndexWriter fw = new FIndexWriter(path);
		IndexWriter iw = fw.getIndexWriter(false);
		// 获取所有数据
		try {
			Term term = new Term("docchannel", chnnelid);
			iw.deleteDocuments(term);
			iw.commit();
			iw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
			if(iw!=null){
				try {
					iw.close();
				} catch (CorruptIndexException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return FALSE_MSG;
		}finally{
			try {
				if(iw!=null){
					iw.close();
				}
			}  catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}

		return SUCCESS_MSG;
	}
}

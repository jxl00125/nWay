package com.provideImpl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.util.Md5CheckUtil;

public class IFtStubSynchroDicData {
	
	static Logger logger = Logger.getLogger(IFtStubClearData.class);
	final static String SUCCESS_MSG = "success";
	final static String ERROR_MSG = "error";
	final static String FALSE_MSG = "error";
	
	final static String DIC_TYPE_Q = "ext_q.dic";
	final static String DIC_TYPE_IND = "ext_ind.dic";
	final static String DIC_TYPE_SYS = "ext_sys.dic";
	
	public String synchroDicData(byte [] datas, String type,String key){

		String msg = null;
		Date start = new Date();
		if (Md5CheckUtil.checkAmsAndAcs(key)) {
			logger.info("词典同步开始");
			msg = synchroDicDatas(datas,type);
		}
		Date end = new Date();
			logger.info("词典同步完成" + (end.getTime() - start.getTime())
					+ " total milliseconds");
		return msg;
	}
	
	public String synchroDicDatas(byte [] datas, String type){
		
		FileOutputStream outFile = null;
		DataOutputStream out = null;
		try {
			String path = this.getClass().getClassLoader().getResource("").getPath();
			path = path.substring(1);
			if(DIC_TYPE_Q.equals(type)){
				outFile = new FileOutputStream(path+DIC_TYPE_Q);
			}else if(DIC_TYPE_IND.equals(type)){
				outFile = new FileOutputStream(path+DIC_TYPE_IND);
			}else {
				outFile = new FileOutputStream(path+DIC_TYPE_SYS);
			}
			
			out = new DataOutputStream(outFile);
		    out.write(datas);
		    out.close();
		    outFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(outFile!=null){
				try {
					outFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	    
		
		return SUCCESS_MSG;
	}
	
	
}

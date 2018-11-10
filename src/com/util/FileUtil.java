package com.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

public class FileUtil {
	private final static Logger logger = Logger.getLogger(FileUtil.class);
	private static final int BUFFER_SIZE=1024*1024*10; 
	public static final String FLAG_UPLOAD="U0";
	public static final String FLAG_PROTECTED = "P0";
	private File file;
	private FileOutputStream outPut;
	private OutputStreamWriter writer;
	private static FileUtil fileUtil;
	private FileUtil(){}	
	/**
	 * 
	 * @return
	 */
	public static synchronized FileUtil getInstance(){
		if (fileUtil==null)
			fileUtil=new FileUtil();
		return fileUtil;
	}
	
	/**
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */   
    public static boolean copy(File src, File dst) {   
        boolean result=false;   
        InputStream in = null;   
        OutputStream out = null;   
        try {   
            in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);   
            out = new BufferedOutputStream(new FileOutputStream(dst),   
                    BUFFER_SIZE);   
            byte[] buffer = new byte[BUFFER_SIZE];   
            int len = 0;   
            while ((len = in.read(buffer)) > 0) {   
                out.write(buffer, 0, len);   
            }   
            result=true;   
        } catch (Exception e) {   
        	logger.error(e);  
            result=false;   
        } finally {   
            if (null != in) {   
                try {   
                    in.close();   
                } catch (IOException e) {   
                	logger.error(e);    
                }   
            }   
            if (null != out) {   
                try {   
                	out.flush();
                    out.close();   
                } catch (IOException e) {   
                	logger.error(e);   
                }   
            }   
        }   
        return result;   
    }         
    /**
     * 
     * @param srcFiles
     * @param uploadFileName
     * @return
     * @throws Exception
     */
    public static boolean fileUpload(File[] srcFiles,String[] uploadFileName) throws Exception {    	
         
        for (int i = 0,length=srcFiles.length; i < length; i++) {      	        	
              
            File dstFile = new File(uploadFileName[i]);   
            if(FileUtil.copy(srcFiles[i], dstFile)){   
            	logger.info(":"+uploadFileName[i]);
            }else {
            	return false;
            }
        }
        return true;
    }  
    
    /**
	 * 
	 * @param filePath
	 * @param context
	 */
	public void createFile(String filePath,String context){				
		try {		
			file = new File(filePath);
			if (file.exists()){
				file.delete();
			}
			writer=new OutputStreamWriter(new FileOutputStream (filePath),"GBK");   
			writer.write(context);
		} catch (IOException e) {
			logger.info("\r\n"+e.getMessage());
			e.printStackTrace();			
		}finally{
			if (outPut!=null){
				try{
					outPut.flush();
					outPut.close();
				}catch(IOException ioe){}				
			}
			if (writer!=null){
				try{
					writer.flush();
					writer.close();
				}catch(IOException ioe){}
			}
		}
		
	}
	
	public static String extractFileExt(String _sFilePathName)
	  {
	    int nPos = _sFilePathName.lastIndexOf('.');
	    return nPos >= 0 ? _sFilePathName.substring(nPos + 1) : "";
	  }

	
	
//	public String getConfigPath(String _PathFalg){
//		
//		if(FileUtil.FLAG_PROTECTED.equals(_PathFalg)){
//			return FilesMan.SAVAPATH+"BNP0";
//			
//		}else if(FileUtil.FLAG_UPLOAD.equals(_PathFalg)){
//			
//			return FilesMan.SAVAPATH+"BNU0";
//		}
//		
//		return null;
//		
//	}
	
//	public synchronized  String getNextFileName(String _Pathfalg,String _sFileExt){
//		
//		String FilePath = getConfigPath(_Pathfalg);
//		Date date = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String datestr = sdf.format(date);
//		FilePath= FilePath+File.separator+_Pathfalg+datestr.substring(0,6)+File.separator+_Pathfalg+datestr;
//		File mfile = new File(FilePath);
//		
//		if(!mfile.exists()){
//			mfile.mkdirs();
//		}
//		
//		String sRandom = StringUtil.numberToStr(Math.round(Math.random() * 10000.0D), 4, '0');
//		
//		String sFileName =_Pathfalg+datestr+sRandom;
//		for (int i = 0; i < 2; i++) {
//		      if (i > 0) {
//		        sFileName = sFileName + StringUtil.numberToStr(
//		          Math.round(Math.random() * 100.0D), 2, '0');
//		      }
//		      
//		      File file = new File(FilePath+File.separator+sFileName+_sFileExt);
//		      if(!file.exists())
//					return sFileName+_sFileExt;
//		    }
//		
//		return null;
//	}
	
	
	
//	public String getFilePath(String _sFileName){
//		
//		String _PathFalg = _sFileName.substring(0,2);
//		String FilePath = getConfigPath(_PathFalg)+File.separator+_sFileName.substring(0,8)
//		+File.separator+_sFileName.substring(0,10)+File.separator;
//		
//		return FilePath;
//		
//	}
	
	public static boolean fileExists(String _sPathFileName)
	  {
	    File file = new File(_sPathFileName);
	    return file.exists();
	  }
	
	public static boolean makeDir(String _sDir, boolean _bCreateParentDir)
	  {
	    boolean zResult = false;
	    File file = new File(_sDir);
	    if (_bCreateParentDir)
	      zResult = file.mkdirs();
	    else
	      zResult = file.mkdir();
	    if (!zResult)
	      zResult = file.exists();
	    return zResult;
	  }
	
	 public static String extractFileName(String _sFilePathName)
	  {
	    return extractFileName(_sFilePathName, File.separator);
	  }
	 
	 public static String extractFileName(String _sFilePathName, String _sFileSeparator)
	  {
	    int nPos = -1;
	    if (_sFileSeparator == null) {
	      nPos = _sFilePathName.lastIndexOf(File.separatorChar);
	      if (nPos < 0)
	        nPos = _sFilePathName
	          .lastIndexOf(File.separatorChar == '/' ? 92 : '/');
	    }
	    else {
	      nPos = _sFilePathName.lastIndexOf(_sFileSeparator);
	    }

	    if (nPos < 0) {
	      return _sFilePathName;
	    }

	    return _sFilePathName.substring(nPos + 1);
	  }
	 
	 public static String mapResouceFullPath(String _resource)
	  {
	    java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(
	      _resource);
	    if (url == null) {
	    	//加入异常信息
	    	logger.info("没有找到资源文件："+_resource);
	    }
	    String sPath = null;
	    try {
	      sPath = url.getFile();
	      if (sPath.indexOf('%') >= 0)
	      {
	        String enc = System.getProperty("file.encoding", "UTF-8");
	        sPath = java.net.URLDecoder.decode(url.getFile(), enc);
	      }
	    }
	    catch (Exception ex) {
	    	logger.info("资源文件："+_resource+"转换失败");
	    	ex.printStackTrace();
	    }
	    return sPath;
	    
	  }
	
	public static void main(String[] args) {
		
	}
	

}

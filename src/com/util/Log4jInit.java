package com.util;

import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.provideImpl.IFtStubFullTextHit;

public class Log4jInit extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1894576229192137069L;
	
	public void destroy(){
		super.destroy();
	}
	public void init() throws ServletException{
		//String path = getServletContext().getRealPath("");
			//System.setProperty("acs", path);
			ConfigServer.getServer();
			ParseXml.parse("document.xml");
			try {
				parserXml("data.xml");
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//logger.error("初始化服务端配置文件异常");
			}
			super.init();
	}
	
	private static void parserXml(String fileName) throws DocumentException {
		SAXReader saxr = new SAXReader();
		Document document = saxr.read(InitCliet.class.getClassLoader()
				.getResourceAsStream(fileName));
		Element rootElement = document.getRootElement();
		for (Iterator i = rootElement.elementIterator(); i.hasNext();) {
			Element tag = (Element) i.next();
			if (tag.getName().equalsIgnoreCase("url")){
			String N_CONTAINSN_STR = tag.getText();
			IFtStubFullTextHit.NCONTAINS_STR = N_CONTAINSN_STR.split(";");
			IFtStubFullTextHit.NCONTAINSN_STR  = new String [IFtStubFullTextHit.NCONTAINS_STR.length];
			for(int j=0;j<IFtStubFullTextHit.NCONTAINSN_STR.length;j++){
				String sRandom = StringUtil.numberToStr(Math.round(Math.random() * 10000.0D), 4, '0');
				IFtStubFullTextHit.NCONTAINSN_STR[j] ="#bona_it_"+sRandom;
			}
			}
		}
	}
	
}

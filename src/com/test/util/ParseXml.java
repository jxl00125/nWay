package com.test.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.test.bean.Doc;
import com.test.bean.PageBean;

public class ParseXml {
	static Logger logger = Logger.getLogger(ParseXml.class);
	public static PageBean parseXml(String xml) {

		SAXReader reader = new SAXReader();
		Document doc;
		PageBean pb = new PageBean();
		List<Doc> dos = new ArrayList<Doc>();
		try {
			doc = reader.read(new ByteArrayInputStream(xml.getBytes()));
			//doc = reader.read(xml);
			List<Element> parameterNodes = doc.selectNodes("pageBean");
			for (Element element : parameterNodes) {
				String pagesize = element.attributeValue("pagesize");
				String currentpage = element.attributeValue("currentpage");
				String totalrecord = element.attributeValue("totalrecord");
				String processwhere = element.attributeValue("processwhere");
				String searchtype = element.attributeValue("searchtype");
				if(currentpage!=null&&!"".equals(currentpage)){
					pb.setCurrentpage(Integer.parseInt(currentpage));
				}
				if(pagesize!=null&&!"".equals(pagesize)){
					pb.setPagesize(Integer.parseInt(pagesize));
				}
				if(totalrecord!=null&&!"".equals(totalrecord)){
					pb.setTotalrecord(Integer.parseInt(totalrecord));
				}
				if(searchtype!=null&&!"".equals(searchtype)){
					pb.setSearchtype(searchtype);
				}
				
				if(processwhere!=null&&!"".equals(processwhere)){
					pb.setProcesswhere(processwhere);
				}
			}
			List<Element> docNodes = doc.selectNodes("pageBean/results/document");
			for (Element element : docNodes) {
				Doc document = new Doc();
				Map<String,String> FieldMaps = new HashMap<String,String>();
				
				List<Element> fieldNodes = element.elements("field");
				
				for (Element element2 : fieldNodes) {
					String name = element2.attributeValue("name");
					String value = value = element2.getText();
					//element2.attributeValue("value");
					value=value.replace("%10", "<");
					value=value.replace("%20", ">");
					FieldMaps.put(name, value);
				}
				document.setFieldMaps(FieldMaps);
				dos.add(document);
			}
			pb.setDos(dos);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return pb;
	}
	
	
	public static void main(String[] args) {
		
	}


}

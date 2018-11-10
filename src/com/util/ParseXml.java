package com.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.core.bean.SParm;
import com.core.bean.VDocument;
import com.core.bean.VField;

public class ParseXml {
	public static Map<String, VDocument> docments = new HashMap<String, VDocument>();
	static Logger logger = Logger.getLogger(ParseXml.class);

	public static void parse(String file) {
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(ParseXml.class.getClassLoader()
			/* 48 */.getResourceAsStream(file));

			logger.info("开始解析document.xml");
			// 解析索引视图库信息
			List<Element> documentNodes = doc.selectNodes("documents/document");
			for (Element node : documentNodes) {
				VDocument vd = new VDocument();
				String name = node.attributeValue("name");
				String path = node.attributeValue("path");
				String indexkey = node.attributeValue("keyindex");
				String dbname = node.attributeValue("dbname");
				vd.setName(name);
				vd.setPath(path);
				vd.setIndexkey(indexkey);
				vd.setDbname(dbname);
				// 获取索引视图域信息
				List<Element> fnodes = node.elements("field");
				for (Element element : fnodes) {
					VField vf = new VField();
					String fname = element.attributeValue("name");
					String store = element.attributeValue("store");
					String index = element.attributeValue("index");
					String core = element.attributeValue("core");
					String datatype = element.attributeValue("datatype");
					
					if (fname == null || "".equals(fname) || store == null
							|| "".equals(store) || index == null
							|| "".equals(index)) {
						logger.error("解析视图文件：document.xml出错，索引域属性值不能为空");
						System.exit(1);
					}
					vf.setName(fname);
					vf.setStore(store);
					vf.setIndex(index);
					vf.setCore(core);
					vf.setDatatype(datatype);
					vd.getFieldlist().add(vf);
				}
				docments.put(name, vd);
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	public static Map<String, SParm> parseXml(String xml) {

		SAXReader reader = new SAXReader();
		Document doc;
		Map<String, SParm> paraMap = new HashMap<String, SParm>();
		try {
			doc = reader.read(new ByteArrayInputStream(xml.getBytes("GB2312")));
			//doc = reader.read(ParseXml.class.getClassLoader()
				//	.getResourceAsStream("seach.xml"));
			
			List<Element> parameterNodes = doc.selectNodes("search/parameter");
			for (Element element : parameterNodes) {
				String pname = element.attributeValue("name");
				String pvalue = element.attributeValue("value");
				String pdesc = element.attributeValue("desc");
				String ptype = element.attributeValue("type");
				SParm parm = new SParm();
				parm.setDesc(pdesc);
				parm.setName(pname);
				parm.setType(ptype);
				parm.setValue(pvalue);
				paraMap.put(pname, parm);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return paraMap;
	}
}

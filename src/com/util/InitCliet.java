package com.util;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class InitCliet {
	static Logger logger = Logger.getLogger(InitCliet.class);

	public static String URL = "";
	private static InitCliet instance = null;

	static {
		logger.debug("初始化配置文件");
		try {
			ConfigServer.getServer();
			parserXml("service.xml");
		} catch (DocumentException e) {
			logger.error("初始化客户端xml配置文件异常");
		}
	}

	public static synchronized InitCliet getInstance() {
		if (instance == null)
			instance = new InitCliet();
		return instance;
	}

	private static void parserXml(String fileName) throws DocumentException {
		SAXReader saxr = new SAXReader();
		Document document = saxr.read(InitCliet.class.getClassLoader()
				.getResourceAsStream(fileName));
		Element rootElement = document.getRootElement();
		for (Iterator i = rootElement.elementIterator(); i.hasNext();) {
			Element tag = (Element) i.next();
			if (tag.getName().equalsIgnoreCase("url"))
				URL = tag.getText();
		}
	}

}

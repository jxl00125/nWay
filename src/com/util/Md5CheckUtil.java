package com.util;

import org.apache.log4j.Logger;

public class Md5CheckUtil {
	
	static Logger logger = Logger.getLogger(Md5CheckUtil.class);
	public static boolean checkAmsAndAcs(String AmsMd5) {

		if (AmsMd5.equals("2246SFFBF-0325E066-234F-48DB-96F6-C275DB993A24")) {
			return true;
		}
		logger.info("MD5码不一致");
		return false;
	}

}

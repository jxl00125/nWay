package com.util;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigServer  {
	public static final String INI_FILENAME = "BNEDOCApp.ini";
	private Properties m_oINIProperties = null;
	/**
	 * 分隔符
	 */
	private String[] SEGMENTS_STR  = null;
	
	/**
	 * 级别符
	 */
	private String[] CONTAINS_LEVEL_STR = null;
	
	/**
	 * 过滤字符
	 */
	private String[] FILTER_STR = null;
	
	/**
	 * 隐藏字符
	 */
	private String [] CONTAINSN_STR = null;
	
	/**
	 * 还原隐藏字符
	 */
	private String [] NCONTAINS_STR = null;
	
	private ConfigServer(){
		loadIniProperties();
	}
	
	private static class Singtonle {
		private static ConfigServer instance =  new ConfigServer();
	}
	
	public static ConfigServer getServer() {

		return Singtonle.instance;
	}


	private void loadIniProperties() {
		if (this.m_oINIProperties != null) {
			
			return;
		}
		
		this.m_oINIProperties = new Properties();

		FileInputStream isFile = null;
		try {
			isFile = new FileInputStream(FileUtil
					.mapResouceFullPath(INI_FILENAME));
			this.m_oINIProperties.load(isFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (isFile != null)
				try {
					isFile.close();
				} catch (Exception localException1) {
				}
		}

	}

	public boolean doStart() {
		loadIniProperties();
		return true;
	}

	protected void doShutdown() {
		clear();
	}

	public synchronized void clear() {
		this.m_oINIProperties.clear();
		this.m_oINIProperties = null;
	}

	public String getInitProperty(String _sKey) {
		return (String)getProperties().get(_sKey);
	}

	private Properties getProperties() {
		if (this.m_oINIProperties == null)
			loadIniProperties();
		return this.m_oINIProperties;
	}

}

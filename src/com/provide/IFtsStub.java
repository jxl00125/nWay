package com.provide;

public abstract interface IFtsStub {
	public static final String KEY = "2246SFFBF-0325E066-234F-48DB-96F6-C275DB993A24";
	public abstract String connect(String username,String password);
	public abstract void disconnect();
	public abstract void destroy();
	public abstract String createGlobalindex(String viewname,String key);
	public abstract String createGlobalAppendindex(String viewname,String key);
	public abstract String Search(String viewname, String key, String xml);
	public abstract String Incrementalindex(String viewname,String key);
	
	public abstract String deleteindex(String viewname,String key,String chnnelid);
	
	/**
	 * 文本匹配
	 * @param text 文本信息
	 * @param keyword 资质级别信息
	 * @param keywords 资质关键词
	 * @param fullkeywords 资质项名称
	 * @param key 秘钥
	 * @return
	 */
	public abstract String checkTextHitKeyWord(String text,String keyword,String keywords,String fullkeywords,String key);
	
	/**
	 * 文本匹配按照，。分隔数据
	 * @param text
	 * @param dtype
	 * @param keywords
	 * @param fullkeywords
	 * @param key
	 * @return
	 */
	public abstract String checkFullTextHitKeyWords(String text,String dtype,String keywords,String fullkeywords,String key);
	
	/**文本匹配
	 * @param text
	 * @param falg
	 * @param keywords
	 * @param fullkeywords
	 * @param key
	 * @return
	 */
	public abstract String checkTextHitKeyWords(String text,String falg,String keywords,String fullkeywords,String key);
	
	/**
	 * 同步词典信息
	 * @param datas
	 * @param type
	 * @param key
	 * @return
	 */
	public abstract String synchroData(byte [] datas, String type,String key);
	
	/**
	 * 文本分词
	 * @param text
	 * @param dtype
	 * @param key
	 * @return
	 */
	public abstract String wordSegments( String text, String dtype,String key);
	
	/**
	 * 文本分句
	 * @param text
	 * @param dtype
	 * @param key
	 * @return
	 */
	public abstract String stopSegments( String text, String dtype,String key);
}

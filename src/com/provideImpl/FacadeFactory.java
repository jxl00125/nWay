package com.provideImpl;

import com.provide.IFtsStub;
import com.util.ConfigServer;

public class FacadeFactory implements IFtsStub {
	
	 
	public String connect(String username, String password) {
		ConfigServer cs = ConfigServer.getServer();
		String user = cs.getInitProperty("USER");
		String psd = cs.getInitProperty("PASSWORD");
		
		if(username.equals(user)&&password.equals(psd)){
			return KEY;
		}else{
			return null;
		}
	}

	 
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	 
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 创建全局索引
	 * @see com.provide.IFtsStub#createGlobalindex(java.lang.String, java.lang.String)
	 */
	 
	public String createGlobalindex(String viewname, String key) {
		IFtsStubCreateGlobalIndex cgi = new IFtsStubCreateGlobalIndex();
		return cgi.createGlobalIndex(viewname, key);
	}
	
	/* (non-Javadoc)
	 * 添加索引
	 * @see com.provide.IFtsStub#createGlobalAppendindex(java.lang.String, java.lang.String)
	 */
	 
	public String createGlobalAppendindex(String viewname, String key) {
		IFtsStubCreateGlobalAppendIndex cgi = new IFtsStubCreateGlobalAppendIndex();
		return cgi.createGlobalIndex(viewname, key);
	}

	/* (non-Javadoc)
	 * 提供检索服务
	 * @see com.provide.IFtsStub#Search(java.lang.String, java.lang.String, java.lang.String)
	 */
	 
	public String Search(String viewname, String key, String xml) {
		IFtsStubSeacheIndex  si = new IFtsStubSeacheIndex();
		return si.Search(viewname, key, xml);
	}

	/* (non-Javadoc)
	 * 更新索引
	 * @see com.provide.IFtsStub#Incrementalindex(java.lang.String, java.lang.String)
	 */
	 
	public String Incrementalindex(String viewname, String key) {
		
		//清除维护索引数据中的重复数据
		IFtStubClearData cd = new IFtStubClearData();
		//删除标记为删除的索引
		IFtsStubDeleteIndex di = new IFtsStubDeleteIndex();
		
		//添加标记为增加的索引
		IFtsStubAddIndex ai = new IFtsStubAddIndex();
		
		//清除已同步数据
		String clearmsg = cd.clearData(viewname, key);
		//删除索引
		String deletemsg = di.deleteIndex(viewname, key);
		//添加索引
		String addmsg = ai.addIndex(viewname, key);
		
		if(clearmsg.equals(cd.SUCCESS_MSG)&&deletemsg.equals(di.SUCCESS_MSG)&&addmsg.equals(ai.SUCCESS_MSG))
			return di.SUCCESS_MSG;
		
		return null;
	}

	/* (non-Javadoc)
	 * 文本匹配（关键词全匹配）
	 * @see com.provide.IFtsStub#checkTextHitKeyWord(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	 
	public String checkTextHitKeyWord(String text,String keyword, String keywords,
			String fullkeywords,String key) {
		// TODO Auto-generated method stub
		IFtStubCheckTextHit ct = new IFtStubCheckTextHit();
		
		return ct.chectTextHistData(text,keyword ,keywords, fullkeywords, key);
	}

	/* (non-Javadoc)
	 * 文本匹配（间隔匹配，有序匹配、无序匹配）
	 * @see com.provide.IFtsStub#checkTextHitKeyWords(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	 
	public String checkTextHitKeyWords(String text, String keyword,
			String keywords, String fullkeywords, String key) {
		// TODO Auto-generated method stub
		IFtStubTextHit th = new IFtStubTextHit();
		return th.chectTextHistData(text,keyword ,keywords, fullkeywords, key);
	}

	 
	public String synchroData(byte[] datas, String type, String key) {
		// TODO Auto-generated method stub
		IFtStubSynchroDicData th = new IFtStubSynchroDicData();
		return th.synchroDicData(datas, type, key);
	}

	 
	public String wordSegments(String text, String dtype, String key) {
		// TODO Auto-generated method stub
		IFtStubTextHit th = new IFtStubTextHit();
		return th.wordSegments(text, dtype, key);
	}

	 
	public String checkFullTextHitKeyWords(String text, String dtype,
			String keywords, String fullkeywords, String key) {
		// TODO Auto-generated method stub
		IFtStubFullTextHit th = new IFtStubFullTextHit();
		return th.chectTextHistData(text,dtype ,keywords, fullkeywords, key);
	}

	 
	public String deleteindex(String viewname, String key, String chnnelid) {
		// TODO Auto-generated method stub
		IFtsStubDeleteChnnelIndex dci = new IFtsStubDeleteChnnelIndex();
		return dci.deleteIndex(viewname, key, chnnelid);
	}

	 
	public String stopSegments(String text, String dtype, String key) {
		// TODO Auto-generated method stub
		IFtStubFullTextHit th = new IFtStubFullTextHit();
		return th.stopSegments(text, dtype, key);
	}

}

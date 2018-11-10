package com.client;


import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.provide.IFtsStub;
import com.util.InitCliet;

public class IFtsClient {
	
	private static Logger logger =Logger.getLogger(IFtsClient.class);
	
	/**对连接用户进行验证
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String connect(String username,String password)throws Exception{
		
		IFtsStub ifs = getIFtsStub();
		
		return ifs.connect(username, password);
	}
	
	
	/**
	 * 对外提供"创建全局索引"服务
	 * @param viewname：视图名称
	 * @param key 验证是否合法用户
	 * @return
	 * @throws Exception
	 */
	public String createGlobalindex(String viewname,String key) throws Exception{
		IFtsStub ifs = getIFtsStub();
		String msg = ifs.createGlobalindex(viewname, key);
		return msg;
	}
	
	/**
	 * 对外提供"添加索引"服务
	 * @param viewname
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String createGlobalAppendindex(String viewname,String key) throws Exception{
		IFtsStub ifs = getIFtsStub();
		String msg = ifs.createGlobalAppendindex(viewname, key);
		return msg;
	}
	
	
	/**
	 * 对外提供标题检索、内容检索、附件检索、二次检索、高级检索服务
	 * @param viewname
	 * @param key
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public String Search(String viewname, String key, String xml)throws Exception{
		
		IFtsStub ifs = getIFtsStub();
		String result =ifs.Search(viewname, key, xml);
		
		return result;
	}
	
	
	/**对外提高维护增量索引服务
	 * @param viewname
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String Incrementalindex(String viewname,String key)throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.Incrementalindex(viewname, key);
		return result;
	}
	
	
	public String deleteIndex(String viewname,String key,String chnnelid)throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.deleteindex(viewname,key,chnnelid);
		return result;
	}
	
	
	/**
	 * 对外提供文本比对（检查关键词是否在文本）
	 * @param text
	 * @param keywords
	 * @param fullkeywords
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String checkTextHitKeyWord(String text,String keyword, String keywords,
			String fullkeywords,String key)throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.checkTextHitKeyWord(text,keyword, keywords, fullkeywords, key);
		return result;
	}
	
	/**对外提供文本比对（检查关键词是否在文本有先后顺序）
	 * @param text
	 * @param keyword
	 * @param keywords
	 * @param fullkeywords
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String checkTextHitKeyWords(String text,String keyword, String keywords,
			String fullkeywords,String key)throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.checkTextHitKeyWords(text,keyword, keywords, fullkeywords, key);
		return result;
	}
	
	/**
	 * 对外提供文本比对（检查关键词是否在文本有先后顺序）,按照，。分割文本信息并建立内存索引
	 * @param text
	 * @param keyword
	 * @param keywords
	 * @param fullkeywords
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String checkFullTextHitKeyWords(String text,String keyword, String keywords,
			String fullkeywords,String key)throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.checkFullTextHitKeyWords(text,keyword, keywords, fullkeywords, key);
		return result;
	}
	
	
	/**
	 * 同步词典信息
	 * @param datas
	 * @param type
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String synchroData(byte [] datas,String type,String key)
		throws Exception{
			IFtsStub ifs = getIFtsStub();
			String result = ifs.synchroData(datas, type, key);
			return result;
	}
	
	/**
	 * 文本分词
	 * @param text
	 * @param dtype
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String wordSegments(String text,String dtype,String key)
	throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.wordSegments(text, dtype, key);
		return result;
	}
	
	/**
	 * 文本分段
	 * @param text
	 * @param dtype
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String stopSegments(String text,String dtype,String key)
	throws Exception{
		IFtsStub ifs = getIFtsStub();
		String result = ifs.stopSegments(text, dtype, key);
		return result;
	}
	
	public IFtsStub getIFtsStub()throws Exception {
		HessianProxyFactory factory = new HessianProxyFactory();
		InitCliet.getInstance();
		String url = InitCliet.URL;
		IFtsStub ifs =null;
		ifs = (IFtsStub) factory.create(IFtsStub.class, url);
		return ifs;
	}
	
	public static void main(String[] args) {
		
		IFtsClient fc = new IFtsClient();
		//String text = "1 本次招标要求投标人须具备建筑工程或房屋建筑工程施工总承包三级或二级或一级或特级资质，具有已竣工的规模在5000平方米及以上或建安合同额在800万元及以上的房屋建筑工程（类似项目描述）业绩，并在人员、设备、资金等方面具有相应的施工能力，其中，投标人拟派项目经理须具备建筑工程专业一级注册建造师执业资格，具备有效的安全生产考核合格证书（B本）。 2 本次招标 不接受 （接受或不接受）联合体投标。";
		
		//String keywords = "10_甲级 建筑 总承包";
		//String fullkeywords = "房屋综合建筑工程施工总承包资质特级";
		//String text = "关于“池州铜冠花园供电工程招标公告” 投标人资格要求有关内容调整的通知 项目编号 所属地区 铜陵市 项目名称 关于“池州铜冠花园供电工程招标公告” 投标人资格要求有关内容调整的通知 发布时间 2015年11月18日 截止时间 2015年11月23日 关于“池州铜冠花园供电工程招标公告” 投标人资格要求有关内容调整的通知 各潜在投标人：经招标人研究决定，2015年11月12日发布的池州铜冠花园供电工程招标公告中三、投标人资格要求“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		String keywords = "10_电力工程 施工 总承包三级";
		String text = "投标人资格要求“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		String fullkeywords = "电力工程施工总承包资质三级";
		text ="九、本公告所有条款解释权归水磨沟区政府采购中心所有；欢迎符合资格要求并有供货能力的供应商踊跃报价。 水区政府采购中心电话：0991-4684212 联系人：董全清 马艳红 地址：乌市七道湾南路 168号水区财政局 315室 六、供应商要求：1、具备符合本次招标要求的中华人民共和国独立法人资格的企业。 2、具有合法的生产或销售（代理）标的物同类型货物的资格和能力。 3、具有为本次招标采购的货物提供长期售后服务的能力。";
		try {
			String key = fc.connect("liugang", "123456");
			fc.createGlobalindex("v_docudoment", key);
			//fc.Incrementalindex("v_docudoment", key);
			//System.out.println(fc.stopSegments(text, null, key));
			//fc.deleteIndex("v_docudoment", key,"105");
			//System.out.println(fc.checkTextHitKeyWords(text,"", keywords, fullkeywords,key));
			
			//fc.synchroData("123".getBytes(), "ext_q.dic", key);
			
			//System.out.println(fc.wordSegments("电力工程施工总承包三级", "10", key));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

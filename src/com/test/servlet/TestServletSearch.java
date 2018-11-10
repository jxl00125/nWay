package com.test.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.client.IFtsClient;
import com.test.bean.Doc;
import com.test.bean.PageBean;
import com.test.util.ParseXml;

public class TestServletSearch extends HttpServlet {
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dooption(req, resp);
		//super.doGet(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dooption(req, resp);
		//super.doPost(req, resp);
	}
	
	
	public void dooption(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		 int PageSize =1000;
		 int PageIndex =94;
		 String SearchType ="any";
		 String SearchWord = "";
		 int IsAdvSearch = 0;
		 String Allwords="";
		 String OnlyOneWords ="";
		 String OrderBy = "";
		 String LastSearch ="0";
		 String CurrSearch ="";
		 String Retrieval = "10";
		 	/**
			 *不包含以下关键词 
			 */
		 	String Nowords="";
			/**
			 * 分类号
			 */
			 String ChannelIds="" ;
			/**
			 * 扩展维度编号
			 */
			 String SourceIds="" ;
			/**
			 * 排序方式
			 */
			 String OrderBySelf ="93";
			
		 /**
			 * 地区（省份）信息
			 */
			 String  provinces="";
			
			
			/**
			 * 时间类型：近一天、近三天、近一周、近一月、自定义
			 */
		 String dtype="";
			/**
			 * 自定义开始时间
			 */
		 String stime="";
			/**
			 * 自定义结束时间
			 */
		 String etime="";
			
			/**
			 * 自定义查询
			 * 企业资质信息
			 */
		 String qcodes ="";
			/**
			 * 自定义查询
			 * 数据来源
			 */
		 String infosources ="";
			
			/**
			 * 自定义查询
			 * 信息类型
			 */
		 String infotypes ="";
		 
		 String n_PageSize = req.getParameter("PageSize");
		 String n_PageIndex = req.getParameter("PageIndex");
		 String n_SearchType = req.getParameter("SearchType");
		 String n_SearchWord = req.getParameter("SearchWord");
		 String n_IsAdvSearch = req.getParameter("IsAdvSearch");
		 String n_Allwords = req.getParameter("Allwords");
		 String n_OnlyOneWords = req.getParameter("OnlyOneWords");
		 String n_ChannelIds = req.getParameter("ChannelIds");
		 String n_SourceIds = req.getParameter("SourceIds");
		 
		 String n_LastSearch = req.getParameter("LastSearch");
		 String n_CurrSearch = req.getParameter("CurrSearch");
		 
		 String n_Retrieval = req.getParameter("Retrieval");
		 String n_Nowords = req.getParameter("Nowords");
		 
		 String n_provinces = req.getParameter("provinces");
		 String n_dtype = req.getParameter("dtype");
		 String n_stime = req.getParameter("stime");
		 String n_etime = req.getParameter("etime");
		 String n_qcodes = req.getParameter("qcodes");
		 String n_infosources = req.getParameter("infosources");
		 String n_infotypes = req.getParameter("infotypes");
		 
		 if(SourceIds==null||"".equals(SourceIds)){
			 SourceIds = "";
		 }
		 
		 if(LastSearch==null||"".equals(LastSearch)){
			 LastSearch = "0";
		 }
		 if(CurrSearch==null||"".equals(CurrSearch)){
			 CurrSearch = "";
		 }
		 if(qcodes==null||"".equals(qcodes)){
			 qcodes="";
		 }
		 if(provinces==null||"".equals(provinces)){
			 provinces="";
		 }
		 if(infotypes==null||"".equals(infotypes)){
			 infotypes="";
		 }
		 if(infosources==null||"".equals(infosources)){
			 infosources="";
		 }
		 
		 if(Retrieval==null||"".equals(Retrieval)){
				Retrieval = "10";
			}
		 
		 /*
		 if(null==dtype ||"".equals(dtype)){
			 dtype ="30";
		 }*/
		 
		 DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar calendar =  Calendar.getInstance();
		 
		 if("10".equals(dtype)){
			 calendar.setTime(new Date());
			 stime = format.format(calendar.getTime())+" 00:00:00";
			 etime = format.format(calendar.getTime())+" 23:59:59";
			 
		 }else if("20".equals(dtype)){
			 calendar.setTime(new Date());
			 etime = format.format(calendar.getTime())+" 23:59:59";
			 calendar.set(calendar.DAY_OF_MONTH, calendar.get(calendar.DAY_OF_MONTH)-2);
			 stime = format.format(calendar.getTime())+" 00:00:00";
		 }else if("30".equals(dtype)){
			 calendar.setTime(new Date());
			 etime = format.format(calendar.getTime())+" 23:59:59";
			 calendar.set(calendar.DAY_OF_MONTH, calendar.get(calendar.DAY_OF_MONTH)-6);
			 stime = format.format(calendar.getTime())+" 00:00:00";
			 
		 }else if("40".equals(dtype)){
			 calendar.setTime(new Date());
			 etime = format.format(calendar.getTime())+" 23:59:59";
			 
			 calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)-1);
			 stime = format.format(calendar.getTime())+" 00:00:00";
			 
		 }else if("110".equals(dtype)){
			 stime = stime+" 00:00:00";
			 etime = etime+" 23:59:59";
		 }else{
			 stime=""; 
			 etime="";
		 }
		 
		// stime = "2014-05-23"+" 00:00:00";
		// etime = "2014-08-19"+" 23:59:59";
		 
		 
		 if(n_PageSize!=null&&!"".equals(n_PageSize)){
			 PageSize = Integer.parseInt(n_PageSize);
		 }
		 if(n_PageIndex!=null&&!"".equals(n_PageIndex)){
			 PageIndex = Integer.parseInt(n_PageIndex);
		 }
		 if(n_SearchType!=null&&!"".equals(n_SearchType)){
			 SearchType = n_SearchType;
		 }
		 if(n_SearchWord!=null&&!"".equals(n_SearchWord)){
			 SearchWord = n_SearchWord;
		 }
		 if(n_IsAdvSearch!=null&&!"".equals(n_IsAdvSearch)){
			 IsAdvSearch = Integer.parseInt(n_IsAdvSearch);
		 }
		 if(n_Allwords!=null&&!"".equals(n_Allwords)){
			 Allwords = n_Allwords;
		 }
		 if(n_OnlyOneWords!=null&&!"".equals(n_OnlyOneWords)){
			 OnlyOneWords = n_OnlyOneWords;
		 }
		 if(n_ChannelIds!=null&&!"".equals(n_ChannelIds)){
			 ChannelIds = n_ChannelIds;
		 }
		 if(n_SourceIds!=null&&!"".equals(n_SourceIds)){
			 SourceIds = n_SourceIds;
		 }
		 
		 if(n_LastSearch!=null&&!"".equals(n_LastSearch)){
			 LastSearch = n_LastSearch;
		 }
		 if(n_CurrSearch!=null&&!"".equals(n_CurrSearch)){
			 CurrSearch = n_CurrSearch;
		 }
		 
		 if(n_Retrieval!=null&&!"".equals(n_Retrieval)){
			 Retrieval = n_Retrieval;
		 }
		 if(n_Nowords!=null&&!"".equals(n_Nowords)){
			 Nowords = n_Nowords;
		 }
		 if(n_provinces!=null&&!"".equals(n_provinces)){
			 provinces = n_provinces;
		 }
		 if(n_dtype!=null&&!"".equals(n_dtype)){
			 dtype = n_dtype;
		 }
		 if(n_stime!=null&&!"".equals(n_stime)){
			 stime = n_stime;
		 }
		 if(n_etime!=null&&!"".equals(n_etime)){
			 etime = n_etime;
		 }
		 
		 if(n_qcodes!=null&&!"".equals(n_qcodes)){
			 qcodes = n_qcodes;
		 }
		 if(n_infosources!=null&&!"".equals(n_infosources)){
			 infosources = n_infosources;
		 }
		 if(n_infotypes!=null&&!"".equals(n_infotypes)){
			 infotypes = n_infotypes;
		 }
		 	// stime = "2014-05-23"+" 00:00:00";
			// etime = "2014-08-19"+" 23:59:59";
			// Retrieval="30";
		 	 SearchWord="";
		 	 ChannelIds= "52";
		 	 
		 	 PageSize =1000;
			 PageIndex =95;
			 provinces ="广东";
			 StringBuffer sbxml = new StringBuffer();
			 sbxml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n");
			 sbxml.append("<search>\n");
			 sbxml.append("\t<parameter name=\"SearchWord\"  desc=\"检索词\"   type=\"1\"  value=\""+SearchWord+"\" />\n");
			 sbxml.append("\t<parameter name=\"Retrieval\"  desc=\"检索方式\"   type=\"1\"  value=\""+Retrieval+"\" />\n");//10：智能检索；20：模糊检索；30：精确检索
			 sbxml.append("\t<parameter name=\"SearchType\"  desc=\"检索类型\"   type=\"1\"  value=\""+SearchType+"\" />\n");
			 sbxml.append("\t<parameter name=\"LastSearch\"  desc=\"二次检索\"   type=\"1\"  value=\""+LastSearch+"\" />\n");
			 sbxml.append("\t<parameter name=\"CurrSearch\"  desc=\"二次检索条件\"   type=\"1\"  value=\""+CurrSearch+"\" />\n");
			 sbxml.append("\t<parameter name=\"OrderBySelf\"  desc=\"自定义排序\"   type=\"1\"  value=\""+OrderBySelf+"\" />\n");
			 sbxml.append("\t<parameter name=\"OrderBy\"  desc=\"排序字段\"   type=\"1\"  value=\""+OrderBy+"\" />\n");
			 sbxml.append("\t<parameter name=\"PageSize\"  desc=\"页面记录数\"   type=\"1\"  value=\""+PageSize+"\" />\n");
			 sbxml.append("\t<parameter name=\"PageIndex\"  desc=\"当前页码\"   type=\"1\"  value=\""+PageIndex+"\" />\n");
			 sbxml.append("\t<parameter name=\"IsAdvSearch\"  desc=\"是否是高级检索\"   type=\"1\"  value=\""+IsAdvSearch+"\" />\n");
			 sbxml.append("\t<parameter name=\"Allwords\"  desc=\"包含所有的词\"   type=\"1\"  value=\""+Allwords+"\" />\n");
			 sbxml.append("\t<parameter name=\"OnlyOneWords\"  desc=\"包含任意的词\"   type=\"1\"  value=\""+OnlyOneWords+"\" />\n");
			 sbxml.append("\t<parameter name=\"Nowords\"  desc=\"不包含的词\"   type=\"1\"  value=\""+Nowords+"\" />\n");
			 
			 sbxml.append("\t<parameter name=\"Stime\"  desc=\"开始时间\"   type=\"2\"  value=\""+stime+"\" />\n");
			 sbxml.append("\t<parameter name=\"Etime\"  desc=\"结束时间\"   type=\"2\"  value=\""+etime+"\" />\n");
			 sbxml.append("\t<parameter name=\"ChannelIds\"    desc=\"栏目信息\"   type=\"2\"  value=\""+ChannelIds+"\" />\n");
			 sbxml.append("\t<parameter name=\"Qcodes\"   desc=\"企业资质信息\"   type=\"2\"  value=\""+qcodes+"\" />\n");
			 sbxml.append("\t<parameter name=\"Provinces\"   desc=\"地区信息\"   type=\"2\"  value=\""+provinces+"\" />\n");
			 sbxml.append("\t<parameter name=\"Infosources\"   desc=\"数据来源\"   type=\"2\"  value=\""+infosources+"\" />\n");
			 sbxml.append("\t<parameter name=\"Infotypes\"   desc=\"信息类型\"   type=\"2\"  value=\""+infotypes+"\" />\n");
			 sbxml.append("</search>");
			 System.out.println(sbxml);
		IFtsClient client = new IFtsClient();
		try {
			String key = client.connect("liugang", "123456");
			String xml = client.Search("v_docudoment", key, sbxml.toString());
			PageBean pb = ParseXml.parseXml(xml);
			List<Doc> docs = pb.getDos();
			req.setAttribute("pb", pb);
			req.getRequestDispatcher("/document_list.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

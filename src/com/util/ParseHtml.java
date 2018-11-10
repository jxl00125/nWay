package com.util;


import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class ParseHtml {
	protected static Logger log = Logger.getLogger(ParseHtml.class);
	protected static String XML_HEADER = "<!DOCTYPE body "
			+ "["
			+ "<!ENTITY nbsp \" \">"			
			+ "<!ENTITY mdash \"-\">"
			+ "<!ENTITY ldquo \"“\">"
			+ "<!ENTITY rdquo \"”\">"
			+ "<!ENTITY lt \"《\">"
			+ "<!ENTITY gt \"》\">"+
			"<!ENTITY lt \"（ \">" +
			"<!ENTITY gt \"）\">" +
			"<!ENTITY le \"&#8804;\">" +
			"<!ENTITY ge \"&#8805;\">" +
			"<!ENTITY times \"&#215;\">"
			+ "]>";
	
	private String mDataSource;
	private String mResult;
	
	public ParseHtml(){}
	
	public ParseHtml(String datasource){
		setText(datasource);
	}
	
	
	public String getText(){
		org.jsoup.nodes.Document doc = Jsoup.parse(mDataSource);
		StringBuffer  sb = new StringBuffer();
		Iterator<Element> iter = doc.children().iterator();
		String temp = "";
		Element element = null;
		while(iter.hasNext()){
			//sb.append(readtext(iter.next()));
			element =iter.next();
			temp = element.text();
			sb.append(temp);
		}
		return sb.toString();
	}
	
	
	public String readtext(Element element){
		StringBuffer  sb = new StringBuffer();
		Iterator<Element> iter = element.children().iterator();
		sb.append(element.text());
		while(iter.hasNext()){
			sb.append(read(iter.next()));
		}
		return sb.toString();
	}

	public void setText(String mDataSource){
		log.debug("格式化文本信息");
		this.mDataSource = mDataSource;		
		formatterSource();
		init();
	}
	
	protected void init(){
		Pattern pattern = Pattern.compile("[A-Z]+=\"[^\"]*\"");
		Matcher matcher = pattern.matcher(mDataSource);
		while(matcher.find()){
			log.info(matcher.group());
		}
	}
	
	/*
	 * 将数据源(mDataSource)格式化成为标准的xml结构，使dom4j能识别
	 * */
	public void formatterSource(){
		mDataSource = mDataSource.toUpperCase();                                        //全部转换成大写
		mDataSource = mDataSource.replaceAll("NOWRAP", "");				                //去掉nowrap属性
		mDataSource = mDataSource.replaceAll("<HR[^>]*>", "");			                //去掉所有<hr>标签
		mDataSource = mDataSource.replaceAll("<BR[^>]*>", "_BONAIT_");
		mDataSource = mDataSource.replaceAll("[A-Z]+=\"[^\"]*\"", "");                  //去掉所有标签的属性,如<a href="http://www.baidu.com">百度</a>转换成<a >百度</a>
		mDataSource = mDataSource.replaceAll("<[/]?A[^>]*>", "");                       //去掉所有<a></a>标签
		mDataSource = mDataSource.replaceAll("<[/]?STRONG[^>]*>", ""); 
		mDataSource = mDataSource.replaceAll("<[/]?FONT[^>]*>", "");                    //去掉所有<font></font>标签
		mDataSource = mDataSource.replaceAll("<[/]?U[^>]*>", "");                    //去掉所有<font></font>标签
		mDataSource = mDataSource.replaceAll("<[/]?SPAN[^>]*>", "");                    //去掉所有<span></span>标签
		mDataSource = mDataSource.replaceAll("<[/]?IMG[^>]*>", "");                     //去掉所有<img></img>
		mDataSource = mDataSource.replaceAll("<[/]?INPUT[^>]*>", "");                   //去掉所有<input>标签
		mDataSource = mDataSource.replaceAll("<[/]?B[^>R]*>", ""); 
		mDataSource = mDataSource.replaceAll("<SCRIPT[^>]*>[\\d\\D]*?</SCRIPT>", "");   //去掉所有js脚本及标签
		mDataSource = mDataSource.replaceAll("<STYLE[^>]*>[\\d\\D]*?</STYLE>", "");   //去掉所有js脚本及标签
		mDataSource = mDataSource.replaceAll("O:", "");                                 //去掉所有的O:
		mDataSource = mDataSource.replaceAll("<[/]?ST1:[^>]*>", "");                    //去掉所有的<ST1: >标签
		mDataSource = mDataSource.replaceAll("<\\?XML[^>]*>", "");                      //去掉原声明根元素
		mDataSource = mDataSource.replaceAll("<\\!DOCTYPE[^>]*>", "");                  //去掉原声明根元素
		
		mDataSource = mDataSource.replaceAll("<[/]?LABEL[^>]*>", ""); 					//去掉<lable></lable>
		mDataSource=mDataSource.replaceAll("<TD[ ]*>\\s*[ ]*<P[ ]*>","<TD>");				//青海省<td>内容为空但是，有<p>标签，将<td><p>替换成<td>,其中<p>标签作废,海南有换行符,加\\s*
		mDataSource=mDataSource.replaceAll("<TD[ ]*>[ ]*[	]*<DIV[ ]*>","<TD>");				//将<td><div>替换成<td>
		mDataSource = mDataSource.replaceAll("&MIDDOT;", "·");
		mDataSource = mDataSource.replaceAll("&NBSP;", " ");
		mDataSource = mDataSource.replaceAll("&MDASH;", "-");
		mDataSource = mDataSource.replaceAll("&LDQUO;", "“");
		mDataSource = mDataSource.replaceAll("&RDQUO;", "”");
		mDataSource = mDataSource.replaceAll("&LT;", "(");
		mDataSource = mDataSource.replaceAll("&GT;", ")");
		
		
		//添加根元素
		if(!mDataSource.contains("<BODY>")){
			mDataSource = "<body>" + mDataSource + "</body>";
		}
		
		//添加声明头
		mDataSource = XML_HEADER + mDataSource;
		mDataSource = mDataSource.toUpperCase();     //全部转换成大写
	}
	
	public String execute(){
		org.jsoup.nodes.Document doc = Jsoup.parse(mDataSource);
		String str = "";
		Iterator<Element> iter = doc.children().iterator();
		while(iter.hasNext()){
			str += read(iter.next());
		}
		//河南存在所有的都是<BR>标签的情况
		if(str.trim().length()==0)
		{
			str=doc.body().text();
		}
		str=str.replaceAll("(_BONAIT_)+", "\n");
		str=str.replaceAll("\\(\\!--\\[IF \\!SUPPORTLISTS\\]--\\)", "");//重庆源代码中含有此注释
		str=str.replaceAll("\\(\\!--\\[ENDIF\\]--\\)", "");//重庆源代码中含有此注释
		str=str.replaceAll("？*","");//广西源代码中有问号
		str=str.replaceAll("U2LNBM\\S*","");//山东源代码中有问号
		return str;
	}
	
//toString	
	
	public String toString(Element element,String text){
		////System.out.println("test toString :--------------->" + element.tagName());
		if(element.tagName().equals("tr") || element.tagName().equals("p") || element.tagName().equals("div") || 
				element.tagName().equals("strong") || element.tagName().equals("td")){
			
			if(element.tagName().equals("tr")){
				////System.out.println("tgName:" + element.tagName() + " ownText:" + element.ownText());
				text = calculateNoEmptyTdCount(element,text);
				String elementText = element.ownText().trim();
	
	//			if(elementText.trim().length() == 0 || elementText.trim().equals("？")){
	//				return text;
	//			}
				text += elementText + "\n"; //这样是先取td文本，再取得tr文本
				
			}
			
			if(element.tagName().equals("p")){
				////System.out.println("test toString <p>:--------------->" + element.tagName());
				String elementText = element.ownText().trim();
				////System.out.println("<p>text--------------------->"+elementText);
				text += elementText + "\n";
			//	text += elementText ;
		//		text=text.replaceAll(" ", "");
			}
			
			if(element.tagName().equals("div")){
				////System.out.println("test toString <p>:--------------->" + element.tagName());
				String elementText = element.ownText().trim();
				//System.out.println("<div>text--------------------->"+elementText);
				if(elementText.length()>20000)
				{
					text="";
				}
				else{
				text += elementText + "\n";
				}
			//	text=text.replaceAll(" ", "");
			}
			
			if(element.tagName().equals("strong")){
				////System.out.println("strong------------------->>>>>>>>>>");
			}
		
		}
		else{
			String elementText = element.ownText().trim();
			////System.out.println("other text--------------------->"+element.tagName()+elementText);
			text += elementText + "\n";
		}
		
		//需要判断td里面有没有子标签和文本为不为空
		//针对不同的标签写判断语句，熟悉不同的标签里面都会出现哪些子标签。
		
		Iterator<Element> iter = element.children().iterator();
		while(iter.hasNext()){
			////System.out.println(" while in toString LAST :--------------->" + element.tagName());
			text = toString(iter.next(),text);
		}
	//	text=text.replaceAll(" ", "");
		return text;
	}
	
	
	public String read(Element element){
		String str = "";
		////System.out.println("test read :--------------->" + element.tagName());
		if(element.tagName().equals("table")){
			str += paserTable(element);
		}
		else if(element.tagName().equals("div") || element.tagName().equals("p")
				|| element.tagName().equals("h1")|| element.tagName().equals("h2")|| element.tagName().equals("h3")
				|| element.tagName().equals("h4")|| element.tagName().equals("h5")|| element.tagName().equals("h6")){
			str = paserDivOrP(element);
		}
		else{	
			Iterator<Element> iter = element.children().iterator();
			while(iter.hasNext()){
				str += read(iter.next());
			}
		}
		
		return str;
	}
	
	public String paserTable(Element element){
		String str = "";
		Iterator<Element> iter = element.children().iterator();
		while(iter.hasNext()){
			Element subElement = iter.next();
			String subText = element.ownText();
			subText = toString(subElement,subText);
			str += subText;
			////System.out.println("tgName:" + subElement.tagName() + " text:" + subElement.text());
		}
		
		return str;
	}
	
	public String paserDivOrP(Element element){
		String str = "";
		Iterator<Element> iter = element.children().iterator();
		
		if(!iter.hasNext())
		{
			String strr = element.ownText();
			//System.out.println("strr"+strr);
			return strr+"\n";
		}
		
		while(iter.hasNext()){
			Element subElement = iter.next();
			String subText = element.ownText();
			subText = toString(subElement,subText);
			str += subText;
			////System.out.println("tgName:" + subElement.tagName() + " text:" + subElement.text());
		}
		
		return str;
	}
	
	public String calculateNoEmptyTdCount(Element trElement, String text){
		////System.out.println("test calculateNoEmptyTdCount:--------------->" + trElement.tagName());
		int tdCount = 0;
		Iterator<Element> iter = trElement.children().iterator();
		while(iter.hasNext()){
			Element tdElement = iter.next();
//			if(tdElement.text().trim().length() == 0 || tdElement.text().trim().equals("？")){
//				continue;
//			}
			if(tdElement.tagName().equals("td")&&tdElement.ownText().replaceAll(" ","").length()!=0){
				tdCount++;
				if(tdCount%2 == 1){
					//text += tdElement.text()+":";  //当td含有子标签的时候用该语句测试
					text += tdElement.ownText()+"     ";
					////System.out.println("td:----------------------------->" + tdElement.ownText());
				}else{
					//text += tdElement.text()+"\n";  //当td含有子标签的时候用该语句测试
					if(tdElement.ownText().trim().equals("："))
					{
						text += tdElement.ownText();
					}
					else
					{
						text += tdElement.ownText()+"\n";
					}
					////System.out.println("td 换行----------------------------->" + tdElement.ownText());
				}
			}
		}
		return text;
	}
	
	public String getDataSource() {
		return mDataSource;
	}
	
	public void setDataSource(String dataSource) {
		this.mDataSource = dataSource;
	}
	
	public String getResult() {
		return mResult;
	}
	
	public void setResult(String result) {
		this.mResult = result;
	}
		
}

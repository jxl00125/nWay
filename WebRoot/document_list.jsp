<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.test.bean.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
PageBean pb = (PageBean)request.getAttribute("pb");
List<Doc> docs = pb.getDos();

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>全文检索结果列表</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="./css/main.css"/>
    <link rel="stylesheet" type="text/css" href="./css/subpage.css"/>
    <link rel="stylesheet" id='skin' type="text/css" href="./js/skin/vista/ymPrompt.css" />
    <script type="text/javascript" src="./js/ymPrompt.js"></script>
	<script type="text/javascript" src="./js/jquery.min1.8.2.js"></script>
    <script type="text/javascript">
    
    var Alert="";
    function init(){
		//初始化弹出框默认配置
		ymPrompt.setDefaultCfg(
			{
			maskAlpha:'0.1',
			maskAlphaColor:'#00f',
			autoClose:false}
			);
	}
	function handler(tp){
			if(tp=='ok'){
				ymPrompt.close();
			}
			if(tp=='cancel'){
				ymPrompt.close();
			}
			if(tp=='close'){
				ymPrompt.close();
			}
		}
		
	function Alert2(message){
			ymPrompt.alert(message,null,null,'',handler);
		}
    
    //提交检索数据
	function doValidate(_oSearchForm){
	
		if(_oSearchForm==null)
			_oSearchForm = document.getElementById("ReSearchFrm");
		var sSearchWord = _oSearchForm.SearchWord.value;
		if(sSearchWord==null||sSearchWord.length<=0){
			ymPrompt.alert('请您输入检索词',null,null,'',handler);
			return false;
		}
		if(sSearchWord.length>200){
			ymPrompt.alert("检索词长度不能超过200汉字，当前输入的检索词长度为"+sSearchWord.length,null,null,'',handler);
			return false;
		}
		_oSearchForm.submit();
		
	}
	
	
	function ssearch(){
		document.getElementById("LastSearch").value="1";
		var _oSearchForm = document.getElementById("ReSearchFrm");
		doValidate(_oSearchForm);
	}
	
    
    
    </script>
  </head>
  
  <body onload="init();">
  
  <form id="ReSearchFrm" name="ReSearchFrm" method="get" action="/fts/search" onsubmit="doValidate(this); return false;" target="_self"> 
	<input type="text" id="SearchWordInput" name="SearchWord" class="swap_value" size="50"/>
						<input type="hidden" name="SearchType" value="<%=pb.getSearchtype()%>"/>
						<input type="hidden" name="PageIndex" value="<%=pb.getCurrentpage() %>"/>
						<input type="hidden" name="PageSize" value="<%=pb.getPagesize() %>"/>
						<input type="hidden" name="LastSearch" value="0"/>
						<input type="hidden" name="CurrSearch" value="<%=pb.getProcesswhere() %>"/>
						<!-- 高级检索 -->
						<input type="hidden" name="IsAdvSearch" value="0"/>
						<input type="hidden" name="Allwords" value=""/>
						<input type="hidden" name="OnlyOneWords" value=""/>
						<!-- 用户子定义 -->
						<input type="hidden" name="ChannelId" value=""/>
						<input type="hidden" name="SourceId" value=""/>
						<input type="button" id="go" alt="检索" title="检索" value="检索" onclick="doValidate(document.ReSearchFrm);return false;"/> 
						<input type="button" id="go" alt="检索" title="检索" value="二次检索" onclick="ssearch();return false;"/>				
  </form> 
    <div class="subsearch_list" >
			<ul>
  				  <%
  				  	for(int i=0;i<docs.size();i++){
  				  		Doc doc = docs.get(i);
  				  		Map<String,String> FieldMaps = doc.getFieldMaps();
  				   %>  
				<li>  
					<h2 style="font-size: 12px;"><a href="${pageContext.request.contextPath}/ekp/views/search/show.action?DocId=" target="_blank">
					<%=FieldMaps.get("doctitle") %>	</a>&nbsp;&nbsp;&nbsp;
					<div style="font-size: 12px;">
					  	问：<%=FieldMaps.get("doccontent") %>			
					</div>
					
					<div style="font-size: 12px;">
					  	答：<%=FieldMaps.get("chnldesc") %>			
					</div>
					<div style="font-size: 12px;">
					 相似度：<%=FieldMaps.get("similarity") %>
					</div>
				</li>
				<%} %>
            </ul>
            </div>
  </body>
</html>

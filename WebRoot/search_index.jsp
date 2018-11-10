<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>全文检索示例</title>
<link rel="stylesheet" type="text/css" href="./css/main.css"/>
<link rel="stylesheet" type="text/css" href="./css/subpage.css"/>
<!-- 弹出框 bigen -->
<link rel="stylesheet" id='skin' type="text/css" href="./js/skin/vista/ymPrompt.css" />
<script type="text/javascript" src="./js/ymPrompt.js"></script>
<script type="text/javascript" src="./js/jquery.min1.8.2.js"></script>
<script language="JavaScript">
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
		//选择检索类别
		var sSearchType = getRadioValues("SearchType");
		if(sSearchType == "dh"){
			existByDH(sSearchWord);
			return false;
		}else{
			_oSearchForm.SearchType.value = sSearchType;
		}
		//设置检索的门类
		var sChannelIds= getCheckedValues("chnlsCheckbox")+"";
		if(sChannelIds && chnlsCheckbox.length > 0){
			_oSearchForm.ChannelId.value = sChannelIds;
		}
		_oSearchForm.submit();
		
	}
	//判断档案是否存在
	function existByDH(dh){
	    var sPostStr =dh.trim();
	    var url="/bnedoc/ekp/views/search/searchByDh.action";
	    var oDHTip = document.getElementById("DHExist");
		$.post(url,{dh:sPostStr},
		function(data,statusText){
    		if(data=='2'&&statusText=='success'){
    			oDHTip.innerHTML = "";
			    var _oSearchForm = document.getElementById("ReSearchFrm");
			    _oSearchForm.SearchType.value="dh";
			    _oSearchForm.submit();
    		}else{
    		 oDHTip.innerHTML = "(档案不存在)";
    		}
		},  "html"); 
	}

	//获取当前选中的radio的值
	function getRadioValues(_sRadioName){
		var oRadioList = document.getElementsByName(_sRadioName);
		if(oRadioList==null||oRadioList.length<=0)
			return "";
		
		for(var i=0;i<oRadioList.length;i++){
			if(oRadioList[i].checked)
				return oRadioList[i].value;
		}
		return "";
	}

	//获取当前选中的checkbox的值
	function getCheckedValues(_sCheckName){
		var oCheckList = document.getElementsByName(_sCheckName);
		if(oCheckList==null||oCheckList.length<=0)
			return "";
		var sCheckValues = "";
		for(var i=0;i<oCheckList.length;i++){
			if(!oCheckList[i].checked)continue;
			sCheckValues = sCheckValues+oCheckList[i].value+",";
		}
		if(sCheckValues!=null&&sCheckValues.length>1){
			sCheckValues = sCheckValues.substring(0,sCheckValues.length-1);
		}
		return sCheckValues;
	}
	// 选择按“档号”检索时，隐藏选择门类的checkbox
	function showOrHideChnlCheckbox(eve){
		if(eve){
			var typeId = eve.id;
			var chnlCheckbox = document.getElementById("ChnlCheckbox");
			if("SearchType_DH" == typeId){
				chnlCheckbox.style.display = "none";
			}else{
				chnlCheckbox.style.display = "";
			}
		}
	}

     //点击热词提交表单数据
	function reSearchHotWords(hotWords){
		var oForm = document.getElementById("do_ReSearchFrm");
		if(oForm==null)
			return false;
		oForm.SearchWord.value = hotWords;
		oForm.submit();
	}
	
	
	function doAdvSearch(){
		
		ymPrompt.win({message:'setadvsearch.jsp',width:860,height:430,title:'高级检索',showMask: true,handler:advsearch,iframe:true,btn:[['确认','save'],['取消','cancel']]})
		
	}
	
	function advsearch(tp){
			
			if("save"==tp){
				var wind =ymPrompt.getPage().contentWindow;
				_oSearchForm = document.getElementById("ReSearchFrm");
				var Allwords = wind.document.getElementById("Allwords").value;
				var Orwords = wind.document.getElementById("Orwords").value;
				_oSearchForm.IsAdvSearch.value = "1";
				_oSearchForm.OnlyOneWords.value = Orwords;
				_oSearchForm.Allwords.value = Allwords;
				alert(Orwords);
				var submit_btn=false;
				//验证数据
				var submit_btn = true; //checbyresultData(wind);
				if(submit_btn){
					_oSearchForm.submit();
					 ymPrompt.close();
					   
				
				}
			}
			
			if("cance"==tp||"close"==tp){
				ymPrompt.close();
			}	
		}
	
	
	</script>
</head>
<body onload="init();">
<div class="contaiter">

	<div class="toolbg tab" >
	</div>
    <div class="addpage" id="con_one_1">
		<div class="retrieval_bor">
			<div class="retrieval_search"  id="con_one_1">
				<div class="retrieval_title"><img src="./images/retrieval_title.gif" width="116" height="34" /></div>
				<div class="search_box retrieval_search_width"> 
					<table width="450" border="0" cellspacing="0" cellpadding="0">
  						<tr>
    						<td width='390'>
					<form id="ReSearchFrm" name="ReSearchFrm" method="get" action="/fts/search" onsubmit="doValidate(this); return false;" target="_self"> 
						<input type="text" id="SearchWordInput" name="SearchWord" class="swap_value" size="50"/>
						<input type="hidden" name="SearchType" value="any"/>
						<input type="hidden" name="PageIndex" value="1"/>
						<input type="hidden" name="PageSize" value="10"/>
						<!-- 高级检索 -->
						<input type="hidden" name="IsAdvSearch" value="0"/>
						<input type="hidden" name="Allwords" value=""/>
						<input type="hidden" name="OnlyOneWords" value=""/>
						<!-- 用户子定义 -->
						<input type="hidden" name="ChannelId" value=""/>
						<input type="hidden" name="SourceId" value=""/>
						<input type="button" id="go" alt="检索" title="检索" value="" onclick="doValidate(document.ReSearchFrm);return false;"/> 
					</form> 
						</td>
						<td>
					<h2><a style="font-size:14px;" href="#" onclick="doAdvSearch();return false;" style="cursor:hand;">高级检索</a></h2>
					</td>
					</tr>
					</table>
				</div>
				<div class="clr"></div>
				<div style="padding-left:120px; padding-top:10px; line-height:22px;">
					<input type="radio" name="SearchType" id="SearchType_any" value="any" checked="checked" onclick="showOrHideChnlCheckbox(this);"/><strong class="f_blue">全文检索</strong>&nbsp;&nbsp;
					<input type="radio" name="SearchType" id="SearchType_title" value="doctitle"  onclick="showOrHideChnlCheckbox(this);"/><strong class="f_blue">标题</strong>&nbsp;&nbsp;
				</div>				
				<div id="ChnlCheckbox" class="pdTL15 border1" style="">
					<div class="width70 left"><strong>选择分类：</strong></div>
					<div class="right width415">
					<ul class="mllist right" style="width:410px;">
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="242">规划建设</input>
				   </li>
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="243">安全生产</input>
				   </li>
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="244">市场营销</input>
				   </li>
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="246">人力资源</input>
				   </li>
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="251">财务管理</input>
				   </li>
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="241">物资管理</input>
				   </li>
					 	
				   <li>
				   <input type="checkbox" name="chnlsCheckbox" id="chnl_301" value="245">信息管理</input>
				   </li>
					</ul>
					</div>
					<div class="clr"></div>
				</div>


				 <div class="clr"></div>
           
            <div class="clr2"></div>
            <div>
              <span>检索热词：</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <span>
	   
		<a href="#" onclick="reSearchHotWords('广东');return false;">广东</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   
		<a href="#" onclick="reSearchHotWords('合同');return false;">合同</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   
		<a href="#" onclick="reSearchHotWords('档案');return false;">档案</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   
		<a href="#" onclick="reSearchHotWords('电力');return false;">电力</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   
		<a href="#" onclick="reSearchHotWords('电网');return false;">电网</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	             
              </span>
            </div>
			</div>
		</div>
		<div class="addpage_bottom"></div>
	</div>
    
	


<div class="footer_csg">版权所有 ：广州博纳信息技术有限公司</div>
<div id="bottom_div1"></div>
  <form id="do_ReSearchFrm" name="do_ReSearchFrm" action="/search" style="display:none" target="_self" onsubmit="return false;">
	  <input name="SearchWord" type="hidden" value="" size="50"/>
	  <input type="hidden" name="SearchType" value="content"/>
	  <input type="hidden" name="ChannelId" value=""/>
	  <input type="hidden" name="SourceId" value=""/>
	  <input type="hidden" name="PageIndex" value="1"/>
	  <input type="hidden" name="PageSize" value="10"/>
  </form>
<!--------跳到指定项----------->
</body>
</html>



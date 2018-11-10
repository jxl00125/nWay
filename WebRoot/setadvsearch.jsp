<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>高级检索-</title>
	<!-- ~Style Begin~ -->
	<link href="./css/2010_itil_style.css" rel="stylesheet" type="text/css">
	<!-- ~Style End~ -->
	<style type="text/css">
	.border{
		border:1px solid silver;
	}
	.calendar_btn{
		border: red 1px solid; BACKGROUND: none transparent scroll repeat 0% 0%;  cursor: pointer;
	}
	</style>
    
	<style>
		.ext-ie8 .XCombox{
			position:relative;
		}
		.ext-ie8 .XCombox input{
			position:absolute;
			left:0px;
			z-index:2;
			border:1px solid #E0E0E0;
			border-right:0px;
			top:-10px;
			width: 42px; 
			height:21px;
		}
		.ext-ie8 .XCombox select{
			position:absolute;
			left:0px;
			z-index:1;
			height:22px;
			border:1px solid #E0E0E0;
			top:-10px;
			width:60px;
		}
		.ext-ie6 .XCombox input{
			width: 44px; 

		}
		.ext-ie6 .XCombox select{
			width:60px;
			margin-left:-44px;
		}

	</style>
	<SCRIPT LANGUAGE="JavaScript">
	
   

	function existByDH(_DH){
	
	}	

	//显示更多高级设置
	var m_currShow = false;
	function moreSetting(_sMoreSettingId){
		
		
	}

	//加载主分类描述
	function loadChnlsNames(_sChannelIds,_sDomId){
		
	}

	//加载扩展维度分类描述
	function loadClassinfoNames(_nRootId,_sClassInfos){
		
		
	}
	

	//初始化页面
	var m_RootClassInfoIds = new Array();
	function initPage(){
		
	}

	function selectChnls(_nSiteId){
		
	}
	
	function selectClassInfo(_rootClassInfoId,_sClassInfoName){
		
	}

	function arrJoin(_arr,_splitStr,_excludeStr){
		if(_arr==null||_arr.length<=0)
			return "";
		var sStr = "";
		_excludeStr = _excludeStr+"";
		for(var i=0;i<_arr.length;i++){
			if(_arr[i]==null||_arr[i].trim().length<=0)
				continue;
			if(_excludeStr!=null&&_excludeStr.length>0){
				if(_arr[i]==_excludeStr)
					continue;
			}
			sStr = sStr+_arr[i]+",";
		}
		if(sStr.length>1)
			sStr = sStr.substring(0,(sStr.length-1));
		return sStr;
	}

	//验证
	function allowSubmit(_oForm){
		var oInputList = document.getElementsByTagName("input");
		if(oInputList==null||oInputList.length<=0)
			return true;
		var bAllow = false;
		var sValidateNames = "";
		for(var i=0;i<oInputList.length;i++){
			var oInput = oInputList[i];
			if(oInput.getAttribute("validate")!="true")
				continue;
			var sValue = oInput.value;
			if(sValue!=null&&sValue.trim().length>0){
				bAllow = true;
			}
			sValidateNames = sValidateNames+"["+oInput.getAttribute("Desc")+"]"
		}
		if(!bAllow){
			//top.window.Ext.Msg.warn("请填写档号或者包含的关键词中一项!");
		}
		return bAllow;
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
	var m_DisabledTagName = ["input","a","select"];
	var m_ANumberDisdExName = ["dh"];
	function disableAll(_bDisabled,_arrExcludeArr){
		if(m_DisabledTagName==null||m_DisabledTagName.length<=0)
			return;
		for(var i=0;i<m_DisabledTagName.length;i++){
			var oTagList = $("advSearchFrm").getElementsByTagName(m_DisabledTagName[i]);
			if(oTagList==null||oTagList.length<=0)
				continue;
			for(var iTag = 0;iTag<oTagList.length;iTag++){
				var oTag = oTagList[iTag];
				var bExclude = false;
				var sName = oTag.getAttribute("name")||"";
				for(var iExclude=0;iExclude<_arrExcludeArr.length;iExclude++){
					if(sName.toLowerCase()==_arrExcludeArr[iExclude]){
						bExclude = true;
						break;
					}
				}
				if(bExclude)
					continue;
				oTag.disabled = _bDisabled;
			}
		}
	}

	function listenerDH(_oInput){
		if(_oInput==null)
			return;
		if(_oInput.value.trim().length>0){
			disableAll(true,m_ANumberDisdExName);
		}else{
			disableAll(false,m_ANumberDisdExName);
			var oDHTip = document.getElementById("DHExist");
			oDHTip.innerHTML = "";
		}
	}
	function setTime(_nValue,_DomId){
		var oDom = document.getElementById(_DomId);
		if(_nValue==null||_nValue.trim()==""){
			oDom.value = "";
			return;
		}
		if(!isInteger(_nValue)){
			return;
		}
		oDom.value = _nValue;
	}
	function isInteger(str){
		var regu = /^[-]{0,1}[0-9]{1,}$/;
		return regu.test(str);
	}

	function selectAppendix(_optionValue){
		if(_optionValue.toLowerCase()!="appendix"){
			selectAppendixDisable(false);
		}else{
			selectAppendixDisable(true);
		}
	}

	var m_Appendix_Disabled = ["TransformName","ValidateYear","SelectTime"];
	function selectAppendixDisable(_bDisabled){
		if(m_Appendix_Disabled==null||m_Appendix_Disabled.length<=0)
			return;
		for(var i=0;i<m_Appendix_Disabled.length;i++){
			var oDomList = document.getElementsByName(m_Appendix_Disabled[i]);
			if(oDomList==null||oDomList.length<=0)
				continue;
			for(var domIndex=0;domIndex<oDomList.length;domIndex++){
				oDomList[domIndex].disabled = _bDisabled;
			}
		}
	}
	function doSearchHelp(){
		
	}

	//判断归档年度是否为4位的整数
	function checkFilingYear(input){
		var filing_year = input.value;
		try{
			if(filing_year.trim() != ""){
				var nYear = parseInt(filing_year);
				if(isNaN(nYear) || nYear < 1900 || nYear > 2050){
					throw new Error("输入不能合法的年份！");
				}
			}
		}catch(err){
			alert("请输入1900~2050之间的年份！");
			input.value = "";
			input.focus();
		}
	}
	//-->
	</SCRIPT>
</head>

<body topmargin="0" leftmargin="0" class="tab25" style="overflow-y:auto;">
<Form id="advSearchFrm" name="advSearchFrm">
	 <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" background="../images/2010_itil_table6_16.jpg" style="margin-top:5px;">
		<tr>
		 <td>
		<img src="../images/2010_itil_table6_14.jpg" width="6" height="6">
		</td>
		<td></td>
		<td align="right">
		<a href="#" onclick="doSearchHelp();return false;" class="blue1" style="display:none;">[使用帮助]</a>
		<img src="../images/2010_itil_table6_19.jpg" width="6" height="6">
		</td>
		</tr>
	 </table>
      <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg10">
        <tr>
          <td align="center" class="pad_6" width="21%">
			  <table border="0" cellspacing="0" cellpadding="0">
			  <tr>
			  <td class="tb" style="padding-left:3px;">档号</td>
			  </tr>
			  </table>
		  </td>
		  <td style="padding-left:15px">
			  <table width="84%" border="0" cellspacing="0" cellpadding="0">
			  <td width="70" style="padding-left:2px;">输入档号</td>
			  <td>
			  <input type="text" name="DH" class="bor9" value="" validate="true" Desc="输入档号" onkeyup="listenerDH(this);">
			  (精确检索)<span id="DHExist" style="color:red"></span>
			  </td>
			   </tr>
			  </table>
		  </td>
        </tr>
      </table>
      <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg11">
        <tr>
          <td align="center" class="pad_6">
		  <table width="85%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="17%" height="24" class="tb">关键词</td>
                <td width="22%">
				包含以下<span class="tb">全部</span>的关键词                  </td>
                <td width="56%">
				<input type="text" name="Allwords" id="Allwords" class="bor9" value="" validate="true" Desc="包含以下全部的关键词">
				(多个词之间用<span class="tred3">空格</span>符隔开)
				</td>
              </tr>
              <tr>
                <td height="24">&nbsp;</td>
                <td>
				包含以下<span class="tb">任意</span>的关键词 
				</td>
                <td>
				<input type="text" name="Orwords" id="Orwords" class="bor9" value="" validate="true" Desc="包含以下任意的关键词">
                  (多个词之间用<span class="tred3">空格</span>符隔开) 
				</td>
              </tr>
               <tr>
                <td height="24">&nbsp;</td>
                <td>
				<span class="tb">不包含</span>以下的关键词
				</td>
                <td>
				<input type="text" name="Nowords" class="bor9" value="" validate="false" Desc="不包含以下的关键词">
                  (多个词之间用<span class="tred3">空格</span>符隔开) 
				</td>
              </tr> 
			  <input type="hidden" name="Nowords" class="bor9" value="" validate="false" Desc="不包含以下的关键词">
          </table>
		  </td>
        </tr>
      </table>	  
	  <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg11">
        <tr>
          <td align="center" class="pad_6">
		  <table width="85%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="18%" height="30" class="tb">关键词位置</td>
				<td width="15%">关键词位于</td>
				<td>
					<select name="SearchType" onchange="selectAppendix(this.value)">
					  <option value="any" >
					  任何地方</option>
					  <option value="title" >
					  题名</option>
					  <option value="Appendix" >
					  附件</option><!--
					  <option value="author" >
					  作者</option>-->
					  <option value="content" >
					  正文</option>
					</select>中
				</td>
				<td width="7%"></td>
		    </tr>
		  </table>
		  </td>
		 </tr>
	  </table>
      <input name="DocStatusValue" type="hidden" value=""></td>                		  
	  <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg10">
        <tr>
          <td align="center" class="pad_6">
		  <table width="85%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="18%" height="22" class="tb">档案门类</td>
                <td width="18%">搜索档案的所属门类</td>
                <td>
				<input type="hidden" name="ChannelIds" value="">
				<a id="SelectChannel" href="#" class="blue1" onclick="selectChnls();return false;">
				
				</a>
				<span id="ChnlDesc"></span>
				</td>
              </tr>
			  <input type="hidden" name="ClassinfoIds" value="">
			 	
			 	
					
            </table>
		  </td>
		</tr>
	   </table>
	   <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg11">
        <tr>
          <td align="center" class="pad_6">
		  <table width="85%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="18%" height="24" class="tb">主办部门</td>
				<td>
					<input type="text" id="zbbm" name="zbbm" class="bor9" value="" validate="false" Desc="输入部门名称" size="45" />
					(多个词之间用<span class="tred3">英文逗号</span>符隔开)
				</td>
				<td width="7%"></td>
		    </tr>
			<tr>
				<td width="18%" height="24" class="tb">责任者</td>
				<td>
					<input type="text" id="zrz" name="zrz" class="bor9" value="" validate="false" Desc="输入责任者" size="45" />
					(多个词之间用<span class="tred3">英文逗号</span>符隔开)
				</td>
				<td width="7%"></td>
		    </tr>
			<tr>
				<td width="18%" height="24" class="tb">项目名称</td>
				<td>
					<input type="text" id="fldXMMC" name="fldXMMC" class="bor9" value="" validate="false" Desc="输入项目名称" size="45" />
					(多个词之间用<span class="tred3">英文逗号</span>符隔开)
				</td>
				<td width="7%"></td>
		    </tr>
			<tr>
				<td width="18%" height="24" class="tb">&nbsp;</td>
				<td>
					归档年度：<input type="text" id="filing_year" name="filing_year" class="bor9" value="" validate="false" Desc="归档年度" size="5" onblur="checkFilingYear(this);"/>
					保管期限：<input type="text" id="retention" name="retention" class="bor9" value="" validate="false" Desc="保管期限" size="5"/>
					载体类型：<input type="text" id="ztlx" name="ztlx" class="bor9" value="" validate="false" Desc="载体类型" size="5"/>
					密级：<input type="text" id="security_class" name="security_class" class="bor9" value="" validate="false" Desc="密级" size="5"/>
				</td>				
		    </tr>
		  </table>
		  </td>
		 </tr>
	  </table>
      <table style="display:none;" width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg11">
        <tr>
          <td align="center" class="bor15_t bor15_b"><table width="98%" border="0" cellspacing="0" cellpadding="0">
              <tr onClick="moreSetting('MoreSetting');return false;" style="cursor:hand">
                <td width="2%">
				<img id="MoreSettingImg" src="../images/2010_itil_table6_29_1.jpg" id="img_more" width="9" height="9"></td>
                <td width="98%" height="24" class="tb" >
				<a id="MoreSettingA" href="#" class="blue1"><span id="moreTip">展开更多高级检索项</span></a>
				</td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <table id="MoreSetting" width="98%" id="tab_more" style="" border="0" align="center" cellpadding="0" cellspacing="0" class="bor22_r bor22_l bg11">
        <tr>
          <td align="center" class="bor15_b pad_6" width="120">
		  <table width="100%" border="0" cellspacing="0" cellpadding="0" style="display:none;">
            <tr>
              <td width="22%" height="30" class="tb" align="center">
			  匹配方式
			  <input name="OptionValue" type="hidden" value="">
			  </td>
              <td width="3%">
			  <input name="Option" type="radio" value="ideosingle" >
			  </td>
              <td width="10%">按字查全</td>
              <td width="3%">
			  <input name="Option" type="radio" value="PRIORDEFCOL" >
			  </td>
              <td width="10%">按词查准</td>
              <td>&nbsp;</td>
			  <td width="15%"></td>
              </tr>
          </table>
          <table width="100%" border="0" cellspacing="0" cellpadding="0"  style="display:none;">
              <tr>
                <td width="22%" height="30" class="tb" align="center">
				智能扩展
				<input name="ExtensionValue" type="hidden" value="">
				</td>
                <td width="3%">
				<input type="checkbox" name="Extension" value="ALLKAX" >
				</td>
                <td width="8%">自动扩展</td>
                <td width="3%">
				<input type="checkbox" name="Extension" value="KAXST" >
				</td>
                <td width="12%">按同义词扩展</td>
                <td width="3%">
				<input type="checkbox" name="Extension" value="KAXECX" >
				</td>
                <td width="16%">半角/全角字符扩展</td>
                <td width="3%">
				<input type="checkbox" name="Extension" value="KAXCST" >
				</td>
				<td width="17%">中文简体/繁体扩展</td>
				<td width="15%"></td>
		      </tr>
		  </table>
          </td>
        </tr>
      </table>
</Form>
<SCRIPT LANGUAGE="JavaScript">
<!--
setTimeout(function(){
initPage();
},10);
//-->
</SCRIPT>
</body>
</html>

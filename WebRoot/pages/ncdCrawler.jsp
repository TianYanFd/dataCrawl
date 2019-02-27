<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'ncdCrawler.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <style type="text/css">
    .mask {
            position: absolute; top: 0px; filter: alpha(opacity=60); background-color: #575757;     
            z-index: 1002; left: 0px;     
            opacity:0.5; -moz-opacity:0.5;
          }
    .td{border:solid #add9c0; border-width:0px 1px 1px 0px;width:120;height:30;}
    .tdcon{border:solid #add9c0; border-width:0px 1px 1px 0px;width:500;height:30;}
	.table{border:solid #add9c0; border-width:1px 0px 0px 1px;}
  </style>
  <script src="pages/js/jquery-1.7.2.min.js"></script>
  <script src="pages/js/msgbox.js"></script>
  <script language="javascript" type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
  <body onload="init();">
  	所在位置-->>同业存单爬取<br>
    <form id="ncd">
    	<div id="mask" class="mask">
    		<br/><br/>
    		<label style="color:blue;fontsize:56;text-align:center">正在全力抓取数据，请稍等......</label>
    	</div> 
    	<br/><br/><br/>
    	<div style="width:840px;margin:0;align: left;">
    		<table class="table" align="left">
    			<tr>
    				<td class="td">
    					起止页码
    				</td>
    				<td class="tdcon">
    					<input type="text" name="startPage" id="startPage" placeholder="请输入起始页码" onkeyup="this.value=this.value.replace(/\D/g,'')"  onafterpaste="this.value=this.value.replace(/\D/g,'')"/>
    					-
    					<input type="text" name="endPage" id="endPage" placeholder="请输入截至页码" onkeyup="this.value=this.value.replace(/\D/g,'')"  onafterpaste="this.value=this.value.replace(/\D/g,'')"/>
    					<label style="color:red">*</label>
    				</td>
    			</tr>
    			<tr>
    				<td class="td">
    					起止日期
    				</td>
    				<td class="tdcon">
    					<input type="text" name="startDate" id="startDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" placeholder="请输入起始日期"/>
    					-
    					<input type="text" name="endDate" id="endDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" placeholder="请输入截至日期"/>
    					<label style="color:red">*</label>
    				</td>
    			</tr>
    			<tr></tr>
    			<tr style="text-align:right">
    				<td class="td" colspan="2">
    					<input type="button" value="爬  取" onclick="crawler();">&nbsp;
    				</td>
    			</tr>
    		</table>
    	</div>
    </form>
  </body>
  <script type="text/javascript">
  	function crawler(){
  		//起止页码校验
  		var startPage = document.getElementById("startPage").value;
  		var endPage = document.getElementById("endPage").value;
  		if(null == startPage || ""==startPage || null == endPage || ""==endPage){
  			alert("请输入起始和截至页码!");
  			return;
  		}
  		if(parseInt(startPage) < 1 || parseInt(endPage) < 1){
  			alert("起止页码只能为正整数!");
  			return;
  		}
  		if(parseInt(startPage) > parseInt(endPage)){
  			alert("startPage:" + startPage);
  			alert("endPage:" + endPage);
  			alert("起始页码不能大于截至页码!");
  			return;
  		}
  		
  		//起止日期校验
  		var startDate = document.getElementById("startDate").value;
  		var endDate = document.getElementById("endDate").value;
  		if(null == startDate || ""==startDate || null == endDate || ""==endDate){
  			alert("请输入起始和截至日期!");
  			return;
  		}
  		if(startDate > endDate){
  			alert("起始日期不能晚于截至日期!");
  			return;
  		}
  		
  		//显示遮罩防止重复提交
  		showMask();
  		$.ajax({
			type: "POST",
            url: "/dataCrawl/ncdcrawler.do",
            data:{'startDate':startDate,'endDate':endDate,'startPage':startPage,'endPage':endPage},
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            success: function (msg) {
            	$.MsgBox.Confirm("爬取结果", msg, hideMask);
            },
            error: function (msg) {
            	$.MsgBox.Confirm("爬取结果", msg, hideMask);
            }
		});
  	}
    //显示遮罩层    
    function showMask(){
        $("#mask").css("height",$(document).height());
        $("#mask").css("width",$(document).width());
        $("#mask").show();
    }  
    //隐藏遮罩层  
    function hideMask(){
        $("#mask").hide();
    } 
    //页面初始化时,隐藏遮罩
    function init(){
    	$("#mask").hide();
    }
  </script>
</html>

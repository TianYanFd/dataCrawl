<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'proxyServers.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <style type="text/css">
    .td{border:solid #add9c0; border-width:0px 1px 1px 0px;height:26;}
	.table{border:solid #add9c0; border-width:1px 0px 0px 1px;}
	.even{background:#CCCCCC;}
	.odd{background:#FFFFFF;} 
  </style>
  <script src="pages/js/jquery-1.7.2.min.js"></script>
  <script language="javascript" type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
  <body>
    所在位置-->>代理服务器<br>
    <form id="proxy">
    	<br/><br/>
    	<div style="width:620px;margin:10px 5px 15px 290px;align: left;">
    		<table>
    			<tr style="align:left;">
    				<td>
    					分析日期:<input type="text" name="analyDate" id="analyDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" placeholder="请输入分析日期"/>
    					<label style="color:red">*</label>
    				</td>
    				<td width="10"></td>
    				<td>
    					<input id="onsubmit" type="button" value="分  析" onclick="analysis();" />
    				</td>
    			</tr>
    		</table>
    	</div>
    	<div style="width:620px;margin:0;">
    		 <table id="myTb" class="table" align="center">
    		 	<tr style="height:40;background-color:#FFFACD">
    				<td class="td" style="width:120">服务器</td>
    				<td class="td" style="width:60">端口号</td>
    				<td class="td" style="width:80">运营商</td>
    				<td class="td" style="width:240">归属地</td>
    				<td class="td" style="width:60">成功次数</td>
    				<td class="td" style="width:60">失败次数</td>
    			</tr>
    		</table>
    	</div>
    </form>
  </body>
  <script type="text/javascript">
  	function analysis(){
  		//清空表格，仅保留表头
  		cleartable();
  		//解析日期
  		var analyDate = document.getElementById("analyDate").value;
  		if(null == analyDate || ""==analyDate){
  			alert("请输入分析日期!");
  			return;
  		}
  		//发送AJAX请求
  		$.ajax({
			type: "POST",
            url: "/dataCrawl/proxyAnalysis.do",
            data:{'analyDate':analyDate},
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            success: function (data) {
            	//发声异常，弹出异常信息
            	if(data.indexOf("kkk") > -1){
            		alert(data);
            		return;
            	}
            	var list = eval(data);
            	//代理服务器尚未加载
            	if(null == list){
            		alert("代理服务器可用性分析失败(* ￣︿￣),请三分钟后再来尝试!");
            		return;
            	}
            	//追加表格体
            	for(var i=0;i<list.length;i++){
            		var proxy = list[i];
            		var tbBody = "";
            		//运营商字典转换
            		var yys = "";
            		var attr = proxy.telecomType;
            		if(attr == 'CT'){
            			yys = '中国电信';
            		}else if(attr == 'CU'){
            			yys = '中国联通';
            		}else if(attr == 'CM'){
            			yys = '中国移动';
            		}else{
            			yys = '中国广电';
            		}
                	tbBody += "<tr>"
                		+ "<td class='td' style='width:120'>" + proxy.host + "</td>"
                		+ "<td class='td' style='width:60'>" + proxy.port + "</td>"
                		+ "<td class='td' style='width:80'>" + attr + "</td>"
                		+ "<td class='td' style='width:240'>" + proxy.attribution + "</td>"
                		+ "<td class='td' style='width:60'>" + proxy.sucCount + "</td>"
                		+ "<td class='td' style='width:60'>" + proxy.failCount + "</td>"
                		+ "</tr>";             
                	$("#myTb").append(tbBody);
            	}
            },
            error: function (msg) {
            	cleartable();
            	alert(msg);
            }
		});
  	}
  	
  	//清空表格，只保留表头
  	function cleartable(){
  		$("#myTb").html("");
  		var tbHead = "";
    	tbHead += "<tr style='height:40;background-color:#FFFACD'>" 
    		+ "<td class='td' style='width:120'>服务器</td>"
    		+ "<td class='td' style='width:60'>端口</td>"
    		+ "<td class='td' style='width:80'>运营商</td>"
    		+ "<td class='td' style='width:240'>归属地</td>"
    		+ "<td class='td' style='width:60'>成功</td>"
    		+ "<td class='td' style='width:60'>失败</td>"
    		+ "</tr>";
    	$("#myTb").append(tbHead);
  	}
  </script>
</html>

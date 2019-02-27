<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'ncdList.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
	<!--  <link rel="stylesheet" href="bootstrap/3.3.5/css/bootstrap.min.css">-->
	<link rel="stylesheet" href="pages/css/bootstrap.min.css">
  </head>
  <style type="text/css">
    .tdterm{border:none #add9c0; border-width:0px 1px 1px 0px;height:30;}
    .tdfilename{border:solid #add9c0; border-width:0px 1px 1px 0px;height:26;width:520;}
    .tdtype{border:solid #add9c0; border-width:0px 1px 1px 0px;height:26;width:120;}
	.table{border:solid #add9c0; border-width:1px 0px 0px 1px;align:center;}
	.even{background:#CCCCCC;}
	.odd{background:#FFFFFF;} 
  </style>
  <script src="pages/js/jquery-1.7.2.min.js"></script>
  <script language="javascript" type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
  <script type="text/javascript">
  	$(document).ready(function(){
      // 要设置的Select的id
      getSelectState("type", "${type}");
  	});
 	// 设置页面Select对象的选中状态
    function getSelectState(selectId, optionValue){
        var sel = document.getElementById(selectId);
        for(var i=0;i<sel.length;i++) {
         if(sel.options[i].value == optionValue) {
            sel.selectedIndex = i;
            break;
         }
        }
    }
  	
  	function queryNcds(pageNum, pageSize){
  		var fileName = document.getElementById("fileName").value;
  		if(null != fileName && ""!= fileName){
  			fileName = encodeURI(encodeURI(fileName));
  		}
  		var issueDate = document.getElementById("issueDate").value;
  		var type = document.getElementById("type").value;
  		$("#query_result").load("/dataCrawl/queryNcds.do?pageNum="+pageNum+"&pageSize="+pageSize + "&fileName=" + fileName + "&issueDate=" + issueDate+ "&type=" + type);
  	}
  </script>
  <body id="query_result">
  	所在位置-->>存单列表<br/>
    <form action="/dataCrawl/queryNcds.do">
    	<br/><br/><br/>
    	<table>
    		<tr><!-- 查询条件 -->
    			<td>
    				<div>
    					<table>
    						<tr>
    							<td class="tdterm" colspan="3" >
    								文件名:<input type="text" name="fileName" id="fileName" value="${fileName}" placeholder="请输入文件名" style="width:500;"/>
    							</td>
    						</tr>
    						<tr>
    							<td>
    								类型:
    								<select id="type" name="type">
    									<option value="1" selected="selected">
											同业存单</option>
										<option value="0">
											发行情况公告</option>
    								</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    							</td>
    							<td class="tdterm">
    								发布日期:<input type="text" name="issueDate" id="issueDate" value="${issueDate}" 
    									onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" placeholder="请输入发布日期"/>&nbsp;&nbsp;
    							</td>
    							<td class="tdterm" style="text-align:right;">
    								<input type="button" value="查  询" onclick="queryNcds(1,10);">
    							</td>
    						</tr>
    						<tr >
    							<td colspan="3" class="tdterm" style="text-align:right;">
    							</td>
    						</tr>
    					</table>
    				</div>
    			</td>
    		</tr>
    		<tr><!-- 分页区 -->
    			<td>
    				<div id="page_div">
					<!-- 页数 -->
						<table>
							<tr>
								<td>
									<div class="message" style="text-align:right;">
										共<i class="blue">${pagehelper.total}</i>记录,&nbsp;
										<i class="blue">${pagehelper.pageNum}/${pagehelper.pages}</i>&nbsp;页
									</div>
								</td>
								<td></td>
								<td>
									<div style="text-align:right;">
										<ul class="pagination">
											<c:if test="${!pagehelper.isFirstPage}">
												<li><a href="javascript:queryNcds(1, ${pagehelper.pageSize});">&lt;&lt;</a></li>
												<li><a href="javascript:queryNcds(${pagehelper.prePage}, ${pagehelper.pageSize});">&lt;</a></li>
											</c:if>
											<c:forEach items="${pagehelper.navigatepageNums}" var="navigatepageNum">
												<c:if test="${navigatepageNum==pagehelper.pageNum}">
													<li class="active"><a href="javascript:queryNcds(${navigatepageNum}, ${pagehelper.pageSize});">${navigatepageNum}</a></li>
												</c:if>
												<c:if test="${navigatepageNum!=pagehelper.pageNum}">
													<li><a href="javascript:queryNcds(${navigatepageNum}, ${pagehelper.pageSize});">${navigatepageNum}</a></li>
												</c:if>
											</c:forEach>
											<c:if test="${!pagehelper.isLastPage}">
												<li><a href="javascript:queryNcds(${pagehelper.nextPage}, ${pagehelper.pageSize});">&gt;</a></li>
												<li><a href="javascript:queryNcds(${pagehelper.pages}, ${pagehelper.pageSize});">&gt;&gt;</a></li>
											</c:if>
										</ul>
									</div>
								</td>
							</tr>
						</table>
					</div>
    			</td>
    		</tr>
    		<tr><!-- 查询结果 -->
    			<td>
    				<div class="container" id="query_result_area" style="margin:0">
						<div class="row clearfix">
							<div class="col-md-12 column">
								<table class="table" id="personList_table">
									<thead>
										<tr>
											<td class="tdfilename">文件名</td>
											<td class="tdtype">发布日期</td>
											<td class="tdtype">类型</td>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${pagehelper.list }" var="ncd">
											<tr>
												<td class="tdfilename">${ncd.fileName }</td>
												<td class="tdtype">${ncd.issueDateStr }</td>
												<td class="tdtype">
													<c:if test="${ncd.type == 1}">
														同业存单
													</c:if>
													<c:if test="${ncd.type == 0}">
														发行情况公告
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
    			</td>
    		</tr>

    	</table>
    </form>
  </body>
</html>

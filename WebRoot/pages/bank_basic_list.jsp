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
    
    <title>My JSP 'bank_basic_list.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
	<link rel="stylesheet" href="pages/css/bootstrap.min.css">
  </head>
  <style type="text/css">
    .tdterm{border:none #add9c0; border-width:0px 1px 1px 0px;height:30;}
    .tdname{border:solid #add9c0; border-width:0px 1px 1px 0px;height:20;width:450;}
    .tdtype{border:solid #add9c0; border-width:0px 1px 1px 0px;height:20;width:460;}
    .tddate{border:solid #add9c0; border-width:0px 1px 1px 0px;height:20;width:220;}
	.table{border:solid #add9c0; border-width:1px 0px 0px 1px;align:left;}
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
  	
  	function queryBankList(pageNum, pageSize){
  		var fullName = document.getElementById("fullName").value;
  		if(null != fullName && ""!= fullName){
  			fullName = encodeURI(encodeURI(fullName));
  		}
  		var type = document.getElementById("type").value;
  		$("#query_result").load("/dataCrawl/queryBankList.do?pageNum="+pageNum+"&pageSize="+pageSize + "&fullName=" + fullName + "&type=" + type);
  	}
  </script>
  <body id="query_result">
  	所在位置-->银行列表<br/>
    <form action="/dataCrawl/queryBankList.do">
    	<br/><br/><br/>
    	<table>
    		<tr><!-- 查询条件 -->
    			<td>
    				<div>
    					<table>
    						<tr>
    							<td class="tdterm" colspan="3" >
    								银行名称:<input type="text" name="fullName" id="fullName" value="${fullName}" placeholder="请输入银行名称" style="width:500;"/>
    							</td>
    						</tr>
    						<tr>
    							<td>
    								类型:
    								<select id="type" name="type">
    									<option value="0" selected="selected">
											请选择</option>
    									<option value="1">
											中央银行</option>
										<option value="2">
											政策性银行</option>
										<option value="3">
											大型股份制商业银行</option>
										<option value="4">
											全国性股份制商业银行</option>
										<option value="5">
											城市商业银行</option>
										<option value="6">
											民营银行</option>
										<option value="7">
											农村商业银行</option>
										<option value="8">
											农村合作银行</option>
										<option value="9">
											农村信用社</option>
										<option value="10">
											村镇银行</option>
										<option value="11">
											外资银行</option>
    								</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    							</td>
    							<td class="tdterm" style="text-align:right;">
    								<input type="button" value="查  询" onclick="queryBankList(1,10);">
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
												<li><a href="javascript:queryBankList(1, ${pagehelper.pageSize});">&lt;&lt;</a></li>
												<li><a href="javascript:queryBankList(${pagehelper.prePage}, ${pagehelper.pageSize});">&lt;</a></li>
											</c:if>
											<c:forEach items="${pagehelper.navigatepageNums}" var="navigatepageNum">
												<c:if test="${navigatepageNum==pagehelper.pageNum}">
													<li class="active"><a href="javascript:queryBankList(${navigatepageNum}, ${pagehelper.pageSize});">${navigatepageNum}</a></li>
												</c:if>
												<c:if test="${navigatepageNum!=pagehelper.pageNum}">
													<li><a href="javascript:queryBankList(${navigatepageNum}, ${pagehelper.pageSize});">${navigatepageNum}</a></li>
												</c:if>
											</c:forEach>
											<c:if test="${!pagehelper.isLastPage}">
												<li><a href="javascript:queryBankList(${pagehelper.nextPage}, ${pagehelper.pageSize});">&gt;</a></li>
												<li><a href="javascript:queryBankList(${pagehelper.pages}, ${pagehelper.pageSize});">&gt;&gt;</a></li>
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
											<td class="tdname">银行名称</td>
											<td class="tddate">成立日期</td>
											<td class="tdtype">类型</td>
											<td class="tddate">总资产(亿元)</td>
											<td class="tddate">资产统计时间</td>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${pagehelper.list }" var="bank">
											<tr>
												<td class="tdname">${bank.fullName }</td>
												<td class="tddate">${bank.establishDateStr }</td>
												<td class="tdtype">
													<c:if test="${bank.type == 1}">
														中央银行
													</c:if>
													<c:if test="${bank.type == 2}">
														政策性银行
													</c:if>
													<c:if test="${bank.type == 3}">
														大型股份制商业银行
													</c:if>
													<c:if test="${bank.type == 4}">
														全国性股份制商业银行
													</c:if>
													<c:if test="${bank.type == 5}">
														城市商业银行
													</c:if>
													<c:if test="${bank.type == 6}">
														民营银行
													</c:if>
													<c:if test="${bank.type == 7}">
														农村商业银行
													</c:if>
													<c:if test="${bank.type == 8}">
														农村合作银行
													</c:if>
													<c:if test="${bank.type == 9}">
														农村信用社
													</c:if>
													<c:if test="${bank.type == 10}">
														村镇银行
													</c:if>
													<c:if test="${bank.type == 11}">
														外资银行
													</c:if>
												</td>
												<td class="tddate">${bank.totalAssets }</td>
												<td class="tddate">${bank.assetsTime }</td>
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

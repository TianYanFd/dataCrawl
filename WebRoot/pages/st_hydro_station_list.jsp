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
  	
  	function queryHydroList(pageNum, pageSize){
  		var fullName = document.getElementById("fullName").value;
  		if(null != fullName && ""!= fullName){
  			fullName = encodeURI(encodeURI(fullName));
  		}
  		var area = document.getElementById("area").value;
  		$("#query_result").load("/dataCrawl/queryHydroList.do?pageNum="+pageNum+"&pageSize="+pageSize + "&fullName=" + fullName + "&area=" + area);
  	}
  </script>
  <body id="query_result">
  	所在位置-->水电站列表<br/>
    <form action="/dataCrawl/queryHydroList.do">
    	<br/><br/><br/>
    	<table>
    		<tr><!-- 查询条件 -->
    			<td>
    				<div>
    					<table>
    						<tr>
    							<td class="tdterm" colspan="3" >
    								水电站名称:<input type="text" name="fullName" id="fullName" value="${fullName}" placeholder="请输入水电站名称" style="width:500;"/>
    							</td>
    						</tr>
    						<tr>
    							<td>
    								地区:
    								<select id="area" name="area">
    									<option value="0" selected="selected">
											请选择</option>
    									<option value="11">
											黑龙江</option>
										<option value="12">
											辽河</option>
										<option value="13">
											鸭绿江</option>
										<option value="14">
											滦河</option>
										<option value="15">
											海河</option>
										<option value="21">
											黄河</option>
										<option value="22">
											淮河</option>
										<option value="23">
											长江</option>
										<option value="31">
											钱塘江</option>
										<option value="32">
											瓯江</option>
										<option value="33">
											闽江</option>
										<option value="34">
											九龙江</option>
										<option value="35">
											韩江</option>
										<option value="36">
											东江</option>
										<option value="37">
											北江</option>
										<option value="41">
											珠江</option>
										<option value="42">
											湖北省</option>
										<option value="43">
											湖南省</option>
										<option value="44">
											广东省</option>
										<option value="45">
											广西壮族自治区</option>
										<option value="46">
											海南省</option>
										<option value="50">
											重庆市</option>
										<option value="51">
											四川省</option>
										<option value="52">
											贵州省</option>
										<option value="53">
											云南省</option>
										<option value="54">
											西藏自治区</option>
										<option value="61">
											陕西省</option>
										<option value="62">
											甘肃省</option>
										<option value="63">
											青海省</option>
										<option value="64">
											宁夏回族自治区</option>
										<option value="65">
											新疆维吾尔族自治区</option>
										<option value="71">
											台湾省</option>
										<option value="81">
											香港特别行政区</option>
										<option value="82">
											澳门特别行政区</option>
    								</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    							</td>
    							<td class="tdterm" style="text-align:right;">
    								<input type="button" value="查  询" onclick="queryHydroList(1,10);">
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
												<li><a href="javascript:queryHydroList(1, ${pagehelper.pageSize});">&lt;&lt;</a></li>
												<li><a href="javascript:queryHydroList(${pagehelper.prePage}, ${pagehelper.pageSize});">&lt;</a></li>
											</c:if>
											<c:forEach items="${pagehelper.navigatepageNums}" var="navigatepageNum">
												<c:if test="${navigatepageNum==pagehelper.pageNum}">
													<li class="active"><a href="javascript:queryHydroList(${navigatepageNum}, ${pagehelper.pageSize});">${navigatepageNum}</a></li>
												</c:if>
												<c:if test="${navigatepageNum!=pagehelper.pageNum}">
													<li><a href="javascript:queryHydroList(${navigatepageNum}, ${pagehelper.pageSize});">${navigatepageNum}</a></li>
												</c:if>
											</c:forEach>
											<c:if test="${!pagehelper.isLastPage}">
												<li><a href="javascript:queryHydroList(${pagehelper.nextPage}, ${pagehelper.pageSize});">&gt;</a></li>
												<li><a href="javascript:queryHydroList(${pagehelper.pages}, ${pagehelper.pageSize});">&gt;&gt;</a></li>
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
											<td class="tdfilename">电站名称</td>
											<td class="tdtype">所属流域</td>
											<td class="tdtype">首投日期</td>
											<td class="tdtype">装机容量(MW)</td>
											<td class="tdtype">机组数(台)</td>
											<td class="tdtype">业主</td>
											<td class="tdtype">电网</td>
											<td class="tdtype">地址</td>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${pagehelper.list }" var="item">
											<tr>
												<td class="tdfilename">${item.name }</td>
												<td class="tdtype">${item.riverId }</td>
												<td class="tdtype">${item.comDateStr }</td>
												<td class="tdtype">${item.capacity }</td>
												<td class="tdtype">${item.installNum }</td>
												<td class="tdtype">${item.owner }</td>
												<td class="tdtype">${item.grid }</td>
												<td class="tdtype">${item.address }</td>
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

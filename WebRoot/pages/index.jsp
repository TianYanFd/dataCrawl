<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" href="pages/css/zTreeStyle.css" type="text/css">
  </head>
  <style type="text/css">
    .menu{border:solid #add9c0; border-width:0px 1px 0px 0px;width:260;height:600;}
  </style>
  <script type="text/javascript" src="pages/js/jquery-1.7.2.min.js"></script>
  <script type="text/javascript" src="pages/js/jquery.ztree.core.js"></script>
  <script type="text/javascript">
  	var zTree;
	var demoIframe;

	var setting = {
		view: {
			dblClickExpand: false,
			showLine: false,
			selectedMulti: false,
			fontCss : {color:"#080808"}
		},
		data: {
			simpleData: {
				enable:true,
				idKey: "id",
				pIdKey: "pId",
				rootPId: ""
			}
		},
		callback: {
			beforeClick: function(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("tree");
				if (treeNode.isParent) {
					zTree.expandNode(treeNode);
					return false;
				} else {
					demoIframe.attr("SRC",treeNode.file + ".jsp");
					return true;
				}
			}
		}
	};
	//准备节点数据
	var zNodes =[
		{id:1, pId:0, name:"[core] 核心功能", open:true,icon:"pages/images/base.gif"},
	    {id:101, pId:1, name:"存单爬取", file:"pages/ncdCrawler",icon:"pages/images/crawler.gif"},
	    {id:102, pId:1, name:"存单列表", file:"pages/ncdList",icon:"pages/images/ncdlist.gif"},
	    {id:103, pId:1, name:"银行列表", file:"pages/bank_basic_list",icon:"pages/images/bank.gif"},
	    {id:104, pId:1, name:"煤电列表", file:"pages/st_coal_station_list",icon:"pages/images/coal.gif"},
	    {id:105, pId:1, name:"水电站列表", file:"pages/st_hydro_station_list",icon:"pages/images/coal.gif"},
	    
	    {id:2, pId:0, name:"[manager] 系统管理", open:false,icon:"pages/images/setting.gif"},
	    {id:201, pId:2, name:"代理服务器", file:"pages/proxyAnalysis",icon:"pages/images/proxy.gif"},
	];
  	
	$(document).ready(function(){
		var t = $("#tree");
		t = $.fn.zTree.init(t, setting, zNodes);
		demoIframe = $("#testIframe");
		demoIframe.bind("load", loadReady);
		var zTree = $.fn.zTree.getZTreeObj("tree");
		zTree.selectNode(zTree.getNodeByParam("id", 1));

	});

	function loadReady() {
		var bodyH = demoIframe.contents().find("body").get(0).scrollHeight,
		htmlH = demoIframe.contents().find("html").get(0).scrollHeight,
		maxH = Math.max(bodyH, htmlH), 
		minH = Math.min(bodyH, htmlH),
		h = demoIframe.height() >= maxH ? minH:maxH ;
		if (h < 800) h = 800;
		demoIframe.height(h);
	}
  </script>
  <body>
    <div style="text-align:left;margin:0px 5px 15px 0px">
    	<table>
    	<tr>
    		<td style="width:260px;height:300px:text-align:left;" class="menu" valign="top">
    			<!-- 菜单区 -->
    			<ul id="tree" class="ztree" style="border: 0px solid #617775;overflow-y: hidden;width:260px;"></ul>
    		</td>
    		<td style="width:970px;text-align: left;">
    			<!-- 内容区 -->
    			<IFRAME ID="testIframe" Name="testIframe" FRAMEBORDER=0 SCROLLING=AUTO width=100%  height=800px SRC="pages/welcome.jsp"></IFRAME>
    		</td>
    	</tr>
    </table>
    </div>
  </body>
</html>

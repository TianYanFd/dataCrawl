package com.nstars.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import com.nstars.service.NcdCrawlerService;
import com.nstars.service.NcdCrawlerWithProxyService;
import com.nstars.util.Config;
import com.nstars.util.ProxyServerUtil;
import com.nstars.model.Notice;
import com.nstars.model.ProxyServer;

/**
 * 同业存单爬虫action
 * 
 * 中国债券网>信息披露>债券信息披露>债券发行>同业存单>更多
 * 
 * @author author
 * @date 2017/02/04
 */
@Controller
public class NcdCrawlerController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private NcdCrawlerService crawlerService;
	
	@Autowired
	private NcdCrawlerWithProxyService crawlerWithProxyService;
		
	/**
	 * 爬取同业存单，并入库
	 * 
	 * @ResponseBody 设置该参数，指定response的type为比如json或xml，本文采用json序列化方式传输数据
	 * 
	 * @param request
	 * @param response
	 * @param startPage
	 * @param endPage
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ncdcrawler.do")
	public @ResponseBody void crawlNcdInfos(HttpServletRequest request,HttpServletResponse response,String startPage,String endPage,String startDate,String endDate) throws Exception {
		Map<String,Object> param = new HashMap<String,Object>();
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		//返回页面结果
		String str = null;
		//非空检验
		if(StringUtils.isBlank(startPage) || StringUtils.isBlank(endPage) || StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
			logger.debug("页面数据不完整!");			
			response.getWriter().print("页面数据不完整!");
			return;
		}
		//设置参数
		param.put("startPage", startPage);
		param.put("endPage", endPage);
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		try{
			boolean proxyFlag = Config.isProxy;
			if(proxyFlag){
				//启用代理
				str = crawlerWithProxyService.crawlNcdInfosWithProxy(param,true);
			}else{
				//不使用代理
//				crawlerService.crawlNcdInfos(param);
				str = crawlerWithProxyService.crawlNcdInfosWithProxy(param,false);
			}
			logger.info("Crawling success!");
			logger.info(str);	
			response.getWriter().print(str);
			return;
		}catch(Exception e){
			logger.info(e.toString());
			response.getWriter().print(e.toString());
			return;
		}
		
	}
	
	
	/**
	 * 代理服务可用性分析
	 * 
	 * @param request
	 * @param response
	 * @param query
	 * @return String 接收结果的JSP
	 * @throws Exception
	 */
	@RequestMapping(value = "/proxyAnalysis.do")
    public @ResponseBody void proxyAnalysis(HttpServletRequest request,HttpServletResponse response,String analyDate)
    throws Exception {
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		try{
		//输入校验
		if(null == analyDate || "".equals(analyDate) ){
			logger.info("分析日期不能为空!");			
			response.getWriter().print("分析日期不能为空!");
			return;
		}
		//分析
		List<ProxyServer> proxysList = crawlerWithProxyService.proxyAnalysis(analyDate);
		String proxyList = null;
		//json化返回结果
		if(null != proxysList && proxysList.size() > 0){
			List<JsonObject> jsons = new ArrayList<JsonObject>();
			for(int i=0;i<proxysList.size();i++){
				ProxyServer proxy = proxysList.get(i);
				JsonObject json = new JsonObject();
				json.addProperty("host", proxy.getHost());
				json.addProperty("port", proxy.getPort());
				json.addProperty("telecomType", proxy.getTelecomType());
				json.addProperty("attribution", proxy.getAttribution());
				json.addProperty("sucCount", proxy.getSucCount());
				json.addProperty("failCount", proxy.getFailCount());
				jsons.add(json);
			}
			proxyList = jsons.toString();
		}
		response.getWriter().print(proxyList);
		}catch(Exception e){
			response.getWriter().print("kkk:" + e.toString());
		}
	}
	
	/**
	 * 查询同业存单信息
	 * 
	 * @param request
	 * @param response
	 * @param fileName 文件名
	 * @param issueDate 发布日期
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryNcds.do")
    public ModelAndView queryNcds(HttpServletRequest request,HttpServletResponse response,String fileName,String issueDate,String type)
    throws Exception {
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		String pageNum = request.getParameter("pageNum");
		String pageSize = request.getParameter("pageSize");
		int num = 1;
		int size = 2;
		if (pageNum != null && !"".equals(pageNum)) {
			num = Integer.parseInt(pageNum);
		}
		if (pageSize != null && !"".equals(pageSize)) {
			size = Integer.parseInt(pageSize);
		}

		PageHelper.startPage(num, size);
		PageHelper.orderBy("id desc");
		//查询
		if(null != fileName && !"".equals(fileName)){
			fileName = URLDecoder.decode(fileName, "utf-8");
		}
		List<Notice> noticeList = crawlerWithProxyService.queryNcdByNameAndDate(fileName, issueDate,type);
		PageInfo<Notice> pagehelper = new PageInfo<Notice>(noticeList);

		// 返回ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pagehelper", pagehelper);
		//查询条件回填
		modelAndView.addObject("fileName", fileName);
		modelAndView.addObject("issueDate", issueDate);
		modelAndView.addObject("type", type);
		
		// 指定视图
		modelAndView.setViewName("/ncdList");
		return modelAndView;
	}
	
	/**
	 * 验证代理服务可用性
	 * 
	 * @param request
	 * @param response
	 * @param proxyServerVO 代理服务器对象
	 * @return String 接收结果的JSP
	 * @throws Exception
	 */
	@RequestMapping(value = "/verifyProxy.do")
    public @ResponseBody void verifyProxy(HttpServletRequest request,HttpServletResponse response,String ip,int port)
    throws Exception {
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		try{
			//输入校验
			if(0 == port || StringUtils.isBlank(ip)){
				logger.info("verifyProxy param is null or illegale......");			
				response.getWriter().print("参数为空或格式非法!");
				return;
			}
			//分析
			boolean flag = ProxyServerUtil.verifyValidityWithJVM(ip, port);
			response.getWriter().print(flag);
		}catch(Exception e){
			response.getWriter().print("verifyProxy exception:" + e.toString());
		}
	}
	
	/**
	 * 手工采集代理服务
	 * 备用方式
	 * 
	 * @param request
	 * @param response
	 * @return String 接收结果的JSP
	 * @throws Exception
	 */
	@RequestMapping(value = "/crawlProxy.do")
    public @ResponseBody void crawlProxy(HttpServletRequest request,HttpServletResponse response)
    throws Exception {
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		try{
			//分析
			ProxyServerUtil proxy = new ProxyServerUtil();
			proxy.crawlProxyServer();
			response.getWriter().print("采集成功。");
		}catch(Exception e){
			logger.error("手工采集代理服务器失败!");
			response.getWriter().print("采集失败!!!");
		}
	}

}

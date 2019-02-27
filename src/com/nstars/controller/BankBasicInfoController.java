package com.nstars.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nstars.service.BankBasicInfoService;
import com.nstars.model.BankBasicInfoDomain;

/**
 * 银行基本信息action
 * 
 * 
 * 
 * @author xls
 * @date 2018/05/04
 */
@Controller
public class BankBasicInfoController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private BankBasicInfoService bankBasicInfoService;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 查询银行基本信息列表
	 * 
	 * @param request
	 * @param response
	 * @param fullName 银行名称
	 * @param issueDate 发布日期
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryBankList.do")
    public ModelAndView queryBankList(HttpServletRequest request,HttpServletResponse response,String fullName,Integer type)
    throws Exception {
		logger.info("BankBasicInfoController.queryBankList beg......");
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
		if(null != fullName && !"".equals(fullName)){
			fullName = URLDecoder.decode(fullName, "utf-8");
		}
		BankBasicInfoDomain queryDom = new BankBasicInfoDomain();
		if(!StringUtils.isBlank(fullName)){
			queryDom.setFullName(fullName);
		}
		if(null != type && type != 0){
			queryDom.setType(type);
		}
		List<BankBasicInfoDomain> bankList = bankBasicInfoService.queryByList(queryDom);
		if(!CollectionUtils.isEmpty(bankList)){
			for(BankBasicInfoDomain t:bankList){
				Date establishDate = t.getEstablishDate();
				if(null != establishDate){
					t.setEstablishDateStr(sdf.format(establishDate));
				}
			}
		}
		PageInfo<BankBasicInfoDomain> pagehelper = new PageInfo<BankBasicInfoDomain>(bankList);

		// 返回ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pagehelper", pagehelper);
		//查询条件回填
		modelAndView.addObject("fullName", fullName);
		modelAndView.addObject("type", type);
		
		// 指定视图
		modelAndView.setViewName("/bank_basic_list");
		logger.info("BankBasicInfoController.queryBankList end......");
		return modelAndView;
	}

}

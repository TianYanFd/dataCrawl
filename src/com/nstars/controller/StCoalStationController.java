package com.nstars.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import com.nstars.model.StCoalStationDomain;
import com.nstars.service.StCoalStationService;

/**
 * 煤电厂信息action
 * 
 * 
 * 
 * @author xls
 * @date 2018/05/04
 */
@Controller
public class StCoalStationController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private StCoalStationService stCoalStationService;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
	private HashMap<Integer, String> keyWords = new HashMap<Integer, String>(){{  
		put(11,"北京市");
		put(12,"天津市");
		put(13,"河北省");
		put(14,"山西省");
		put(15,"内蒙古自治区");
		put(21,"辽宁省");
		put(22,"吉林省");
		put(23,"黑龙江省");
		put(31,"上海市");
		put(32,"江苏省");
		put(33,"浙江省");
		put(34,"安徽省");
		put(35,"福建省");
		put(36,"江西省");
		put(37,"山东省");
		put(41,"河南省");
		put(42,"湖北省");
		put(43,"湖南省");
		put(44,"广东省");
		put(45,"广西壮族自治区");
		put(46,"海南省");
		put(50,"重庆市");
		put(51,"四川省");
		put(52,"贵州省");
		put(53,"云南省");
		put(54,"西藏自治区");
		put(61,"陕西省");
		put(62,"甘肃省");
		put(63,"青海省");
		put(64,"宁夏回族自治区");
		put(65,"新疆维吾尔族自治区");
		put(71,"台湾省");
		put(81,"香港特别行政区");
		put(82,"澳门特别行政区");
	}};
	
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
	@RequestMapping(value = "/queryCoalList.do")
    public ModelAndView queryCoalList(HttpServletRequest request,HttpServletResponse response,String fullName,Integer area)
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
		StCoalStationDomain queryDom = new StCoalStationDomain();
		if(!StringUtils.isBlank(fullName)){
			queryDom.setName(fullName);
		}
		if(null != area && area != 0){
			queryDom.setAddress(keyWords.get(area));
		}
		List<StCoalStationDomain> coalList = stCoalStationService.queryByList(queryDom);
		if(!CollectionUtils.isEmpty(coalList)){
			for(StCoalStationDomain t:coalList){
				Date comDate = t.getComDate();
				if(null != comDate){
					t.setComDateStr(sdf.format(comDate));
				}
			}
		}
		PageInfo<StCoalStationDomain> pagehelper = new PageInfo<StCoalStationDomain>(coalList);

		// 返回ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pagehelper", pagehelper);
		//查询条件回填
		modelAndView.addObject("fullName", fullName);
		modelAndView.addObject("area", area);
		
		// 指定视图
		modelAndView.setViewName("/st_coal_station_list");
		logger.info("BankBasicInfoController.queryBankList end......");
		return modelAndView;
	}


}

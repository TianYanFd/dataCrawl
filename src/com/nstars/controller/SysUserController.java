package com.nstars.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nstars.service.SysUserService;
import com.nstars.vo.LoginVO;
import com.nstars.model.SysUserDomain;

/**
 * 用户信息action
 * 
 * 
 * 
 * @author xls
 * @date 2018/05/08
 */
@Controller
public class SysUserController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private SysUserService sysUserService;
	
	/**
	 * 验证用户登录信息
	 * 
	 * @param request
	 * @param response
	 * @param name 用户名
	 * @param pwd 密码
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/login.do")
    public @ResponseBody LoginVO login(HttpServletRequest request,HttpServletResponse response,String name,String pwd)
    throws Exception {
		logger.info("SysUserController.login beg......");
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		LoginVO vo = new LoginVO();
		vo.setName(name);
		vo.setPwd(pwd);
		//非空校验
		if(StringUtils.isBlank(name) || StringUtils.isBlank(pwd)){
			logger.error("SysUserController.login name or pwd is null......");
			vo.setErrMsg("用户名或密码错误!");
			vo.setStatus(0);
			return vo;
		}
		//根据用户名查询密码
		SysUserDomain queryDom = new SysUserDomain();
		queryDom.setName(name);
		List<SysUserDomain> userList = sysUserService.queryByList(queryDom);
		if(CollectionUtils.isEmpty(userList)){
			//该用户不存在
			logger.error("SysUserController.login name:{"+ name +"} is not exist......");
			vo.setErrMsg("用户名或密码错误!");
			vo.setStatus(0);
			return vo;
		}
		String pwdDb = userList.get(0).getPwd();
		if(!pwd.equals(pwdDb)){
			//该用户不存在
			logger.error("SysUserController.login name:{"+ name +"} ,pwd is error......");
			vo.setErrMsg("用户名或密码错误!");
			vo.setStatus(0);
			return vo;
		}else{
			//成功登录
			vo.setStatus(1);
			return vo;
		}
	}
	
	/**
	 * 跳转到主界面index.jsp
	 * 
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/jump.do")
    public ModelAndView jump(HttpServletRequest request,HttpServletResponse response)
    throws Exception {
		logger.info("SysUserController.jump beg......");
		//设置返回编码
		response.setContentType("text/text;charset=utf-8");
		// 指定视图
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/index");
		logger.info("BankBasicInfoController.jump end......");
		return modelAndView;
	}

}

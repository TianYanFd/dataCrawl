package com.nstars.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nstars.dao.SysUserDao;
import com.nstars.model.SysUserDomain;
import com.nstars.service.SysUserService;

/**
 * 用户登录接口服务实现
 * 
 * 
 * @author xls
 * @date 2018/05/08
 */
@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService{
	
	@Autowired
	private SysUserDao sysUserDao;

	@Override
	public List<SysUserDomain> queryByList(SysUserDomain t) {
		return sysUserDao.queryByList(t);
	}

}

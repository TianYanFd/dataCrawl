package com.nstars.service;

import java.util.List;

import com.nstars.model.SysUserDomain;

public interface SysUserService {
	
	public List<SysUserDomain> queryByList(SysUserDomain t);

}

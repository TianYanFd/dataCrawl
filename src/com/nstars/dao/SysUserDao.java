package com.nstars.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.SysUserDomain;

public interface SysUserDao {
	
	public List<SysUserDomain> queryByList(@Param("item") SysUserDomain t);

}

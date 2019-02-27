package com.nstars.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.BankBasicInfoDomain;

public interface BankBasicInfoDao {
	
	public List<BankBasicInfoDomain> queryByList(@Param("bank") BankBasicInfoDomain t);

}

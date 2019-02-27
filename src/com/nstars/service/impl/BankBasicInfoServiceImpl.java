package com.nstars.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nstars.dao.BankBasicInfoDao;
import com.nstars.model.BankBasicInfoDomain;
import com.nstars.service.BankBasicInfoService;

/**
 * 银行基本信息接口服务实现
 * 
 * 
 * @author xls
 * @date 2018/05/04
 */
@Service("bankBasicInfoService")
public class BankBasicInfoServiceImpl implements BankBasicInfoService{
	
	@Autowired
	private BankBasicInfoDao bankBasicInfoDao;

	@Override
	public List<BankBasicInfoDomain> queryByList(BankBasicInfoDomain t) {
		return bankBasicInfoDao.queryByList(t);
	}

}

package com.nstars.service;

import java.util.List;

import com.nstars.model.BankBasicInfoDomain;

public interface BankBasicInfoService {
	
	public List<BankBasicInfoDomain> queryByList(BankBasicInfoDomain t);
	
}

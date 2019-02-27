package com.nstars.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nstars.dao.StCoalStationDao;
import com.nstars.model.StCoalStationDomain;
import com.nstars.service.StCoalStationService;

/**
 * 煤电厂信息接口服务实现
 * 
 * 
 * @author xls
 * @date 2018/05/04
 */
@Service("stCoalStationService")
public class StCoalStationServiceImpl implements StCoalStationService{

	@Autowired
	private StCoalStationDao stCoalStationDao;
	
	@Override
	public List<StCoalStationDomain> queryByList(StCoalStationDomain t) {
		return stCoalStationDao.queryByList(t);
	}
	

}

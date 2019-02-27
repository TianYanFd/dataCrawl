package com.nstars.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nstars.dao.StHydroStationDao;
import com.nstars.model.StHydroStationDomain;
import com.nstars.service.StHydroStationService;

/**
 * 水电站信息接口服务实现
 * 
 * 
 * @author xls
 * @date 2018/05/08
 */
@Service("stHydroStationService")
public class StHydroStationServiceImpl implements StHydroStationService{

	@Autowired
	private StHydroStationDao stHydroStationDao;
	
	@Override
	public List<StHydroStationDomain> queryByList(StHydroStationDomain t) {
		return stHydroStationDao.queryByList(t);
	}
	

}

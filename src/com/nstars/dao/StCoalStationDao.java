package com.nstars.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.StCoalStationDomain;

public interface StCoalStationDao {
	
	public List<StCoalStationDomain> queryByList(@Param("coal") StCoalStationDomain t);

}

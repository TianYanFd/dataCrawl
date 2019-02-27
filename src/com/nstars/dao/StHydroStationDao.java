package com.nstars.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.StHydroStationDomain;

public interface StHydroStationDao {
	
	public List<StHydroStationDomain> queryByList(@Param("hydro") StHydroStationDomain t);

}

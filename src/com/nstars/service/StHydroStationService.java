package com.nstars.service;

import java.util.List;

import com.nstars.model.StHydroStationDomain;

public interface StHydroStationService {
	
	public List<StHydroStationDomain> queryByList(StHydroStationDomain t);

}

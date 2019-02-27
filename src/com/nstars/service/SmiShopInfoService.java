package com.nstars.service;

import com.nstars.model.SmiShopInfo;

public interface SmiShopInfoService {
	/**
	 * 根据店铺的编码和密码查询店铺信息
	 * @param shopNo 店铺编码
	 * @param password 密码
	 * @return 店铺信息
	 * @throws Exception 
	 */
	public SmiShopInfo getShopByNoAndPwd(String shopNo, String password) throws Exception;


}

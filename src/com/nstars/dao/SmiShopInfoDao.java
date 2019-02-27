package com.nstars.dao;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.SmiShopInfo;

public interface SmiShopInfoDao {
	/**
	 * 根据门店编码和密码获取门店信息
	 * 
	 * @param password
	 *            门店密码
	 * @param shopNo
	 *            门店编码
	 */
	public SmiShopInfo getByNoAndPwd(@Param("password") String password, @Param("shopNo") String shopNo);


}

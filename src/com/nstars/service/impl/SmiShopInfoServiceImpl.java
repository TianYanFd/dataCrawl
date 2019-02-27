package com.nstars.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.nstars.dao.SmiShopInfoDao;
import com.nstars.model.SmiShopInfo;
import com.nstars.service.SmiShopInfoService;
import org.springframework.stereotype.Service;

/**
 * 影院店铺信息接口服务实现
 * 
 * @author author
 * @date 2016/02/16
 */
@Service("smiShopInfoService")
public class SmiShopInfoServiceImpl implements SmiShopInfoService{
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	public SmiShopInfoDao shopInfoDao;

	@Override
	public SmiShopInfo getShopByNoAndPwd(String shopNo, String password)
			throws Exception {
		if (null==shopNo||"".equals(shopNo)) {
			logger.warn("门店编码为null或空，请检查！");
			throw new Exception("门店编码为空");
		}
		
		return shopInfoDao.getByNoAndPwd(password, shopNo);

	}

}

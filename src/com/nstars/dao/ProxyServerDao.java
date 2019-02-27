package com.nstars.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.ProxyServer;

public interface ProxyServerDao {
	
	/**
	 * 获取所有代理服务器
	 * 
	 */
	public List<ProxyServer> getAllProxyServers();

	/**
	 * 获取所有有效的代理服务器
	 * 
	 */
	public List<ProxyServer> getAllEffectiveProxyServers();
	
	/**
	 * 更新代理服务器的可以性状态为不可用
	 * 
	 * @param proxyServer 代理服务器
	 */
	public void updateBySective(@Param("proxyServer") ProxyServer proxyServer);
	
	/**
	 * 更新代理服务器的成功失败次数
	 * 
	 * @param proxyServer 代理服务器
	 */
	public void updateProxyTimes(@Param("proxyServer") ProxyServer proxyServer);
	
	/**
	 * 查询代理服务器，根据ip、port
	 * 
	 * @param proxyServer 代理服务器
	 */
	public List<ProxyServer> queryProxyBySective(@Param("proxyServer") ProxyServer proxyServer);
	
	/**
	 * 新增代理服务器
	 * 
	 * @param proxyServer 代理服务器
	 */
	public void insertProxyServer(@Param("proxyServer") ProxyServer proxyServer);
	
	/**
	 * 统计数据库中有效状态的代理服务器的数量
	 * 
	 * @return 有效的代理服务器的数量
	 */
	public Integer countProxyServerEffective();
	
}

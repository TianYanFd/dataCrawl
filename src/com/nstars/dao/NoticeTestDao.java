package com.nstars.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.NoticeTest;
import com.nstars.model.ProxyServer;

public interface NoticeTestDao {
	/**
	 * 根据文件名和发布日期，查询同业存单
	 * 
	 * @param fileName 文件名
	 * @param issueDate 发布日期
	 */
	public NoticeTest getByFileNameAndIssueDate(@Param("fileName") String fileName, @Param("issueDate") Date issueDate);
	
	/**
	 * 保存同业存单
	 * 
	 * @param notice 同业存单
	 */
	public void insertNoticeTest(@Param("noticeTest") NoticeTest noticeTest);
	
	/**
	 * 根据发布日期，查询同业存单
	 * 
	 * @param issueDate 发布日期
	 */
	public List<NoticeTest> getByIssueDate(@Param("issueDate") Date issueDate);
	
	/**
	 * 获取所有代理服务器
	 * 
	 */
	public List<ProxyServer> getAllProxyServers();
	
	/**
	 * 查询同业存单信息
	 * 
	 */
	public List<NoticeTest> queryNcdByNameAndDate(@Param("noticeTest") NoticeTest noticeTest);
}

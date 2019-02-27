package com.nstars.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nstars.model.Notice;

public interface NcdCrawlerDao {
	/**
	 * 根据文件名和发布日期，查询同业存单
	 * 
	 * @param fileName 文件名
	 * @param issueDate 发布日期
	 */
	public Notice getByFileNameAndIssueDate(@Param("fileName") String fileName, @Param("issueDate") Date issueDate);
	
	/**
	 * 保存同业存单
	 * 
	 * @param notice 同业存单
	 */
	public void insertNotice(@Param("notice") Notice notice);
	
	/**
	 * 根据发布日期，查询同业存单
	 * 
	 * @param issueDate 发布日期
	 */
	public List<Notice> getByIssueDate(@Param("issueDate") Date issueDate);
	
	/**
	 * 查询同业存单信息
	 * 
	 */
	public List<Notice> queryNcdByNameAndDate(@Param("notice") Notice notice);

}

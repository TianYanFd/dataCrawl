package com.nstars.service;

import java.util.List;
import java.util.Map;

import com.nstars.model.Notice;
import com.nstars.model.ProxyServer;

public interface NcdCrawlerWithProxyService {
	
	public String crawlNcdInfosWithProxy(Map<String,Object> param,boolean isProxy) throws Exception;
	
	public List<ProxyServer> proxyAnalysis(String analyDate) throws Exception;
	
	public List<Notice> queryNcdByNameAndDate(String fileName,String issueDate,String type) throws Exception;
	
	public void crawlNcdWithNoParameter() throws Exception;

}
package com.nstars.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nstars.constant.DataConstants;
import com.nstars.crawl.AbstractHttpClient;
import com.nstars.dao.NcdCrawlerDao;
import com.nstars.model.Notice;
import com.nstars.model.Page;
import com.nstars.service.NcdCrawlerService;
/**
 * 同业存单爬取信息接口服务实现
 * 已作废
 * 
 * @author author
 * @date 2017/02/04
 */
@Service("crawlerService")
public class NcdCrawlerServiceImpl implements NcdCrawlerService{
	private Logger logger = Logger.getLogger(this.getClass());
	private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	public NcdCrawlerDao crawlerDao;
	
	/**
	 * 爬取同业存单，总方法
	 * 
	 * @param param
	 * @return 
	 * @throws Exception
	 */
	@Override
	public String crawlNcdInfos(Map<String, Object> param) throws Exception {
		//解析参数
		int startPage = Integer.parseInt(param.get("startPage").toString());
		int endPage = Integer.parseInt(param.get("endPage").toString()) + 1;
		Date startDate = sdf.parse(param.get("startDate").toString());
		Date endDate = sdf.parse(param.get("endDate").toString());
		//动态url的头和尾
//		String urlHead = Config.urlHead;
//		String urlTail = Config.urlTail;
		//循环爬取处理每一页
		for(int i=startPage;i<endPage;i++){
			//1.拼接URL
			String url = DataConstants.NCD_URL + i;
			//2.发送请求，爬取页面数据
			Page page = crawlPageByURL(url,i);
			//3.解析页面数据
			List<Notice> notices = null;
			if(null != page){
				notices = extractNoticeFromPage(page);
			}
			//4.入库操作
			if(null != notices && notices.size() > 0){
				for(int j=0;j<notices.size();j++){
					Notice notice = notices.get(j);
					//判断是否超出日期范围
					if(verifyNoticeByDate(notice,startDate,endDate)){
						Notice nt = crawlerDao.getByFileNameAndIssueDate(notice.getFileName(), notice.getIssueDate());
						if(null == nt){
							crawlerDao.insertNotice(notice);
						}else{
							logger.info("同业存单已存在:" + nt.toString());
							continue;
						}
					}else{
						logger.info("同业存单超出规定日期范围:" + notice.toString());
						continue;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 爬取单页同业存单信息
	 * 
	 * @param url 即将抓取网页的URL
	 * @return 
	 * @throws Exception
	 */
	public Page crawlPageByURL(String url,int i) throws Exception{
		int times = 1;
		while(true){
			AbstractHttpClient httpClient = new AbstractHttpClient();
			logger.info("第" + times + "次抓取页码:" + i + "......");
			Page page = httpClient.getWebPage(url, "UTF-8");
			int status = page.getStatusCode();
			if(status == HttpStatus.SC_OK){
				if (page.getHtml().contains("中国货币网")){
					logger.info("抓取页面成功!");
					return page;
				}else {
					//发送异常，没有正确返回目标url
					logger.info("send exception!");
					times ++;
					if(times > 10){
						return null;
					}
					Thread.sleep(100);
					continue;
				}
			}else if(status == 404 || status == 401 ||
					status == 410){
				logger.info("there is no page on the server!!!");
				times ++;
				if(times > 10){
					return null;
				}
				Thread.sleep(100);
				continue;
			}else {
				logger.info("crawl fail!");
				times ++;
				if(times > 10){
					return null;
				}
				Thread.sleep(100);
				continue;
			}
		}
	}
	
	/**
	 * 提取同业存单对象集，通过Page
	 * 
	 * @param page 抓取的页面对象
	 * @return 
	 * @throws Exception
	 */
	public List<Notice> extractNoticeFromPage(Page page) throws Exception{
		Document doc = Jsoup.parse(page.getHtml());
        List<Notice> notices = new ArrayList<Notice>();
        Elements tableElts = doc.getElementsByTag("table");
        if(null !=tableElts && tableElts.size() > 2){
        	Element tbElt = tableElts.get(0);
        	//总共10个tr元素，第一行为空白行，最后一行为超链接与分页行
        	//Elements brElts = tbElt.getElementsByTag("tr");
        	Elements brElts = tbElt.getElementsByTag("tr");
        	 if(null !=brElts && brElts.size() > 2){
         		for(int i=2;i<17;i++){
         			if(i%2 != 0) continue;
         			Notice notice = new Notice();
         			Element brElt = brElts.get(i);
         			//获取<td>
                	Elements tdElts = brElt.getElementsByTag("td");
                	if(null !=tdElts && tdElts.size() == 2){
                		//第一个<td>
                		Element ftdElt = tdElts.get(0);
                		//获取<a>
                		Elements aElts = ftdElt.getElementsByTag("a");
                		if(null == aElts || aElts.size() < 1) break;
                		String noticeName = aElts.get(0).html();
                    	notice.setFileName(noticeName);
                    	if(noticeName.contains("发行情况公告")){
                    		notice.setType("0");
                    	}else{
                    		notice.setType("1");
                    	}
                    	//第二个<td>
                    	String issueDateStr = tdElts.get(1).html();//公告日期
                    	try {
                			notice.setIssueDate(new Date(sdf.parse(issueDateStr.trim()).getTime()));
                		} catch (ParseException e) {
                			logger.info(e.toString());
                			throw new Exception(e);
                		}
                    	notice.setLoadTime(new Timestamp(new Date().getTime()));
                    	notices.add(notice);
                	}else{
                		logger.info("表格列检验失败,页面数据损坏!损坏数据为第" + (i+1) + "行,页面URL:" + page.getUrl());
                		throw new Exception("表格列检验失败,页面数据损坏!损坏数据为第" + (i+1) + "行,页面URL:" + page.getUrl());
                	}
         		}
        	 }else{
        		logger.info("表格行检验失败,页面数据损坏!页面URL:" + page.getUrl());
        		throw new Exception("表格行检验失败,页面数据损坏!页面URL:" + page.getUrl());
        	 }
        }else{
        	logger.info("表格整体校验失败,页面数据损坏!页面URL:" + page.getUrl());
        	throw new Exception("表格整体校验失败,页面数据损坏!页面URL:" + page.getUrl());
        }
        return notices;
	}
	
	/**
	 * 判断爬取的同业存单是否符合查询条件，主要是起止日期
	 * 
	 * @param notice 同业存单对象
	 * @param startDate 抓取起始日期
	 * @param endDate 抓取截至日期
	 * @return true-范围内,false-超出范围
	 * @throws Exception
	 */
	public boolean verifyNoticeByDate(Notice notice,Date startDate,Date endDate) throws Exception{
		Date issueDate = notice.getIssueDate();
    	if(!(issueDate.compareTo(startDate) < 0) && !(issueDate.compareTo(endDate) > 0)){
    		return true;
    	} 
    	return false;
	}

}

package com.nstars.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nstars.constant.DataConstants;
import com.nstars.dao.NcdCrawlerDao;
import com.nstars.dao.NoticeTestDao;
import com.nstars.dao.ProxyServerDao;
import com.nstars.model.Notice;
import com.nstars.model.NoticeTest;
import com.nstars.model.ProxyServer;
import com.nstars.service.NcdCrawlerWithProxyService;
import com.nstars.util.Config;
import com.nstars.util.CrawlerUtils;
import com.nstars.util.DateUtil;
import com.nstars.util.ProxyServerUtil;
/**
 * 同业存单爬取信息(使用代理)接口服务实现
 * 
 * @author author
 * @date 2017/02/09
 */
@Service("crawlerWithProxyService")
public class NcdCrawlerWithProxyServiceImpl implements NcdCrawlerWithProxyService{
	
	private Logger logger = Logger.getLogger(this.getClass());
	private DateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat sdfm = new SimpleDateFormat("yyyyMMdd");
	@Autowired
	public NcdCrawlerDao crawlerDao;
	
	@Autowired
	public NoticeTestDao noticeTestDaoDao;
	
	@Autowired
	private ProxyServerDao proxyServerDao;
	
	/**
	 * 爬取同业存单(使用代理服务器)，总方法
	 * 
	 * @param param 爬取条件(页面输入)
	 * @return 爬取结果(暂时不用)
	 * @throws Exception
	 */
	@Override
	public String crawlNcdInfosWithProxy(Map<String, Object> param,boolean isProxy) throws Exception {
		//爬取同业存单数量
		int crawlerAmt = 0;
		//当前正在爬取页码
		int pageIndex = 1;
		//采集到的同业存单名称
		StringBuffer crawlerFiles = new StringBuffer();
		//返回字符串
		StringBuilder retSb = new StringBuilder();
		try{
			//爬取开始时间
			String startTime = sdfTime.format(new Date());
			//-1.解析参数
			int startPage = Integer.parseInt(param.get("startPage").toString());
			int endPage = Integer.parseInt(param.get("endPage").toString()) + 1;
			Date startDate = sdf.parse(param.get("startDate").toString());
			Date endDate = sdf.parse(param.get("endDate").toString());
			//动态url的头和尾
			//获取起止日期的遍历，记录日志用
			Map<String,Integer> dataMap = DateUtil.traverTwoDays(param.get("startDate").toString(), param.get("endDate").toString());
			//循环爬取处理每一页
			for(int i=startPage;i<endPage;i++){
				pageIndex = i;
				StringBuilder urlSb = new StringBuilder();
				urlSb.append(DataConstants.NCD_URL)
					.append("&pageSize=30")
					.append("&sDate=").append(param.get("startDate"))
					.append("&eDate=").append(param.get("endDate"))
					.append("&pageNo=").append(pageIndex);
				String pageHtml = null;
				logger.info("页码:" + pageIndex + ",开始抓取......");
				//3.发送请求，爬取页面数据
				if(isProxy){
					pageHtml = CrawlerUtils.sendPostWithJVM(urlSb.toString(),"UTF-8",proxyServerDao);
				}else{
					pageHtml = CrawlerUtils.sendPost(urlSb.toString(),true,"UTF-8",proxyServerDao);
				}
				
				//如果页面抓取失败，重新获取连接并抓取页面
				if(checkListJsonIsLegal(pageHtml)){
					logger.info("页码:" + pageIndex + "抓取成功");
					//4.解析页面数据
					List<Notice> notices = extractNoticeFromPage(pageHtml);
					//5.入库操作
					if(!CollectionUtils.isEmpty(notices)){
						for(int j=0;j<notices.size();j++){
							Notice notice = notices.get(j);
							//判断是否超出日期范围
							if(verifyNoticeByDate(notice,startDate,endDate)){
								Notice nt = crawlerDao.getByFileNameAndIssueDate(notice.getFileName(), notice.getIssueDate());
								if(null == nt){
									crawlerDao.insertNotice(notice);
									//只统计同业存单
									if("1".equals(notice.getType())){
										crawlerAmt++;
										crawlerFiles.append("<br//>");
										crawlerFiles.append(notice.toShortString());
									}
									
									//统计结果加一
									for(Map.Entry<String, Integer> entry : dataMap.entrySet()){
										String key = entry.getKey();
										int value = entry.getValue();
										if(key.equals(notice.getIssueDateStr())){
											dataMap.put(key, (value + 1));
										}
									}
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
				}else{
					//打印日志
					logger.info("页码:" + pageIndex + "抓取失败");
					throw new Exception("远程服务器返回数据为空或不完整!");
				}
			}
			//爬取结束时间
			String endTime = sdfTime.format(new Date());
			//爬取结果统计写文件
			writeResultToFile(dataMap,startTime,endTime,param);
		}catch(Exception e){
			//发生异常，将爬取结果、异常返回前台
			retSb.append("爬取第").append(pageIndex).append("页发生异常,共入库").append(crawlerAmt).append("条同业存单");
			if(crawlerAmt > 0){
				retSb.append(",详情如下:").append(crawlerFiles.toString()).append("异常信息如下:").append(e.toString());
			}else{
				retSb.append(",异常信息如下:").append(e.toString());
			}
			logger.info(retSb.toString());
			return retSb.toString();
		}
		retSb.append("爬取成功,共入库").append(crawlerAmt).append("条同业存单");
		if(crawlerAmt > 0){
			retSb.append(",详情如下:").append(crawlerFiles.toString());
		}
		return retSb.toString();
	}
	
	/**
	 * 使用代理服务器爬取网页，返回页面HTML
	 * 
	 * @param url 网页URL
	 * @param proxy 代理服务器
	 * @return 页面HTML
	 * @throws Exception
	 */
	public String crawlPageByURL(String url,ProxyServer proxy){
		try{
			String host = proxy.getHost();
			String port = proxy.getPort() + "";
			//设置代理服务器
	        System.setProperty("http.proxySet", "true");
	        System.setProperty("http.proxyHost", host);
	        System.setProperty("http.proxyPort", port);
	        //连接远程服务器
	        URLConnection connection = new URL(url).openConnection();
	        //连接超时时间(单位:毫秒)
	        connection.setConnectTimeout(30000);
	        //读取数据超时时间(单位:毫秒)
	        connection.setReadTimeout(30000);
	        Scanner cin = new Scanner(connection.getInputStream(),"utf-8");
	        StringBuilder builder = new StringBuilder();
	        while (cin.hasNext()) {
	            builder.append(cin.nextLine());
	        }
	        cin.close();
			return builder.toString();
		}catch(Exception e){
			logger.info("抓取异常,代理服务器:" + proxy.toString() + ",异常信息如下:" + e);
			//更新该代理服务器的失败次数
			proxy.setAttribution("fail");
			proxyServerDao.updateProxyTimes(proxy);
			return "crawl exception,the exception is as following:" + e.toString();
		}
	}
	
	/**
	 * 提取同业存单对象列表，通过页面HTML
	 * 
	 * @param pageJson 抓取的页面字符串
	 * @return 该页的同业存单对象列表
	 * @throws Exception
	 */
	public List<Notice> extractNoticeFromPage(String pageJson) throws Exception{
		List<Notice> list = new ArrayList<Notice>();
        if(StringUtils.isBlank(pageJson)){
            logger.error("NcdCrawlerWithProxyServiceImpl.extractNoticeFromPage pageJson is null or empty......");
            return list;
        }
        try{
            // 转化为JSONObject对象
            JSONObject jsonObj = JSON.parseObject(pageJson);
            // 截取有用的records
            if(null != jsonObj){
                JSONArray records = jsonObj.getJSONArray("records");
                if (null != records && records.size() > 0) {
                	Timestamp createTime = new Timestamp(new Date().getTime());
                    for(int i=0;i<records.size();i++){
                        JSONObject record = records.getJSONObject(i);
                        if(null != record){
                        	Notice dom = new Notice();
                            // 发布日期
                            String publishdateStr = record.getString("publishdateStr");
                            if(!StringUtils.isBlank(publishdateStr)){
                            	dom.setIssueDateStr(publishdateStr);
                                dom.setIssueDate(sdf.parse(publishdateStr));
                            }
                            // 全称
                            String title = record.getString("title");
                            if(!StringUtils.isBlank(title)){
                            	dom.setFileName(title);
                                // 类型
                            	if(title.contains("发行情况公告")){
                            		dom.setType("0");
                            	}else{
                            		dom.setType("1");
                            	}
                            }
                            // 创建时间
                            dom.setLoadTime(createTime);
                            list.add(dom);
                        }
                    }
                }else{
                    logger.error("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal records is null or empty......");
                    return list;
                }
            }else{
                logger.error("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal jsonObj is null......");
                return list;
            }
        }catch(Exception e){
            logger.info("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal exception:{}",e);
            return list;
        }
        return list;
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

	/**
	 * 代理服务可用性分析
	 * 
	 * @param 
	 * @return 代理服务器可用性分析结果
	 * @throws Exception
	 */
	@Override
	public List<ProxyServer> proxyAnalysis(String analyDate) throws Exception {
		//1.从缓存中获取代理服务器
		List<ProxyServer> proxys = ProxyServerUtil.proxys;
		if(null != proxys && proxys.size() > 0){
			for(int i=0;i<proxys.size();i++){
				proxys.get(i).setSucCount(0);
				proxys.get(i).setFailCount(0);
			}
		}else{
			return null;
		}
		//2.获取日志路径前缀
		String logPath = Config.logPath;
		//3.获取日志绝对路径
		String filePath = null;
		int today = Integer.parseInt(sdfm.format(new Date()));
		int analysisDate = Integer.parseInt(analyDate.replace("-", ""));
		if(analysisDate < today){
			filePath = logPath + "\\crawler.log." + analyDate;
		}else if(analysisDate == today){
			filePath = logPath + "\\crawler.log.";
		}else{
			logger.error("不能分析未来的日志文件,分析日期:" + analyDate);
			throw new Exception("不能分析未来的日志文件,分析日期:" + analyDate);
		}
		logger.info("日志文件路径:" + filePath);
		//4.遍历日志内容,统计各服务器成功失败次数
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if(file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) !=null){
					//解析每行数据
					if(lineTxt.contains("抓取成功")){
						for(int i=0;i<proxys.size();i++){
							ProxyServer pro = proxys.get(i);
							if(lineTxt.contains(pro.getHost())){
								if(pro.getSucCount() != null){
									pro.setSucCount(pro.getSucCount() + 1);
								}else{
									pro.setSucCount(1);
								}
							}
						}
					}else if(lineTxt.contains("抓取失败")){
						for(int i=0;i<proxys.size();i++){
							ProxyServer pro = proxys.get(i);
							if(lineTxt.contains(pro.getHost())){
								if(pro.getFailCount() != null){
									pro.setFailCount(pro.getFailCount() + 1);
								}else{
									pro.setFailCount(1);
								}
							}
						}
					}else{//不需要解析的行
						continue;
					}
				}
				read.close();
			}else{
				logger.info("找不到日志文件,路径:" + filePath);
				throw new Exception("找不到日志文件,路径:" + filePath);
			}
		} catch (Exception e) {
			logger.info("解析日志文件出现异常!异常信息如下:" + e.toString());
			throw new Exception("解析日志文件出现异常!异常信息如下:" + e.toString());
		}
		return proxys;
	}

	/**
	 * 查询同业存单信息
	 * 
	 * @param fileName 文件名
	 * @param issueDate 发布日期
	 * @return 
	 * @throws Exception
	 */
	@Override
	public List<Notice> queryNcdByNameAndDate(String fileName, String issueDate,String type)
			throws Exception {
		Notice notice = new Notice();
		//构建查询对象
		if(null != fileName && !"".equals(fileName)){
			notice.setFileName(fileName);
		}
		if(null != issueDate && !"".equals(issueDate)){
			notice.setIssueDate(sdf.parse(issueDate));
		}
		if(null != type && !"".equals(type)){
			notice.setType(type);
		}
		return crawlerDao.queryNcdByNameAndDate(notice);
	}
	
	/**
	 * 将本次爬取统计结果按天写入日志文件
	 * 
	 * @param rest 爬取统计结果
	 * @return 
	 * @throws Exception
	 */
	public void writeResultToFile(Map<String,Integer> rest,String startTime,String endTime,Map<String, Object> param) throws Exception{
		//获取日志文件路径
		String path = Config.crawlerPath;
		File pathFile = new File(path);
		if(!pathFile.exists()){
			pathFile.mkdirs();
		}
		//获取日志文件
		String filePath = path + "\\crawler_result.log." + DateUtil.getCurrentYearMonthStr(); 
		//判断追加内容或新建文件
		File file = new File(filePath);
		BufferedWriter fw = null;
		if(!file.exists()){//新建
			file.createNewFile();
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
			fw.append("#########################################");
		}else{//追加
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
		}
		fw.newLine();
		fw.append("爬取条件:起止页码:" + param.get("startPage").toString() + "-" + param.get("endPage").toString() + ",起止日期:" + param.get("startDate").toString() + "-" + param.get("endDate").toString());
		fw.newLine();
		fw.append("爬取耗时:" + startTime + " 至 " + endTime);
		fw.newLine();
		
		//这里将map.entrySet()转换成list
        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(rest.entrySet());
        //通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //升序排序
            public int compare(Entry<String, Integer> o1,  
                    Entry<String, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        
        //遍历排序的结果
        for(Map.Entry<String,Integer> entry:list){   
        	if(entry.getValue() > 0){
				fw.append(entry.getKey() + ",入库数据" + entry.getValue() + "条;");
				fw.newLine();
			}
        }
		fw.append("#########################################");
		fw.flush();
		fw.close();	
	}

	/**
	 * 每隔五分钟爬取前事业(测试方法,已废弃)
	 * 
	 * @param 
	 * @return 
	 * @throws Exception
	 */
	@Override
	public void crawlNcdWithNoParameter() throws Exception {
		logger.info("定时器调度爬取,开始......");
		//存放爬取结果
		//爬取同业存单数量
		int crawlerAmt = 0;
		//当前正在爬取页码
		int crawlerPage = 1;
		try{
			//-1.解析参数
			int startPage = 1;
			int endPage = 11;
			//动态url的头和尾
			String urlHead = "http://www.chinamoney.com.cn/fe/jsp/CN/chinamoney/notice/beNewDraftByTremList.jsp?pagingPage_il_=";
			String urlTail = "&";
			//循环爬取处理每一页
			for(int i=startPage;i<endPage;i++){
				crawlerPage = i;
				//1.随机获取代理
				ProxyServer proxy = ProxyServerUtil.getProxyFromCache();
				//2.拼接URL
				String url = urlHead + i + urlTail;
				logger.info("页码:" + i + ",代理服务器:" + proxy.toString() + ",开始抓取......");
				//3.发送请求，爬取页面数据
				String pageHtml = crawlPageByURL(url,proxy);
				//如果页面抓取失败，重新获取连接并抓取页面
				int crawTimes = 1;
				while(true){
					if(pageHtml.contains("table")){
						logger.info("页码:" + i + ",第" + crawTimes + "次抓取成功,代理服务器:" + proxy.toString());
						break;
					}else{
						//打印日志
						logger.info("页码:" + i + ",第" + crawTimes + "次抓取失败,代理服务器:" + proxy.toString());
						//判断次数是否超标(10次)
						if(crawTimes > 10){
							logger.error("页码:" + i + ",10次抓取均失败,放弃抓取!!!");
							throw new Exception("页码:" + i + ",10次抓取均失败,放弃抓取!!!");
						}
						crawTimes++;
						//十次尝试
						proxy = ProxyServerUtil.getProxyFromCache();
						logger.info("页码:" + i + ",代理服务器:" + proxy.toString() + ",开始抓取......");
						pageHtml = crawlPageByURL(url,proxy);
					}
				}
				//4.解析页面数据
				List<NoticeTest> noticeTests = null;
				if(null != pageHtml){
					noticeTests = extractNoticeTestFromPage(pageHtml);
				}
				//5.入库操作
				if(null != noticeTests && noticeTests.size() > 0){
					for(int j=0;j<noticeTests.size();j++){
						NoticeTest noticeTest = noticeTests.get(j);
						//判断是否库中存在
						NoticeTest nt = noticeTestDaoDao.getByFileNameAndIssueDate(noticeTest.getFileName(), noticeTest.getIssueDate());
						if(null == nt){
							noticeTestDaoDao.insertNoticeTest(noticeTest);
						}else{
							logger.info("数据已存在:" + nt.toString());
							continue;
						}
					}
				}
			}
		}catch(Exception e){
			//发生异常，将爬取结果、异常打印
			logger.info("爬取第" + crawlerPage  + "页发生异常,异常信息如下:");
			logger.info(e.toString());
			logger.info("定时器调度爬取,异常结束(˙︿˙)......");
			return;
		}
		logger.info("爬取成功,共入库" + crawlerAmt + "条数据!");
		logger.info("定时器调度爬取,正常结束......");
		return;
	}
	
	/**
	 * 提取数据对象列表，通过页面HTML(测试，已废弃)
	 * 
	 * @param page 抓取的页面字符串
	 * @return 该页的数据对象列表
	 * @throws Exception
	 */
	public List<NoticeTest> extractNoticeTestFromPage(String page) throws Exception{
		Document doc = Jsoup.parse(page);
        List<NoticeTest> notices = new ArrayList<NoticeTest>();
        Elements tableElts = doc.getElementsByTag("table");
        if(null !=tableElts && tableElts.size() == 2){
        	Element tbElt = tableElts.get(0);
        	//总共10个tr元素，第一行为空白行，最后一行为超链接与分页行
        	//Elements brElts = tbElt.getElementsByTag("tr");
        	Elements brElts = tbElt.getElementsByTag("tr");
        	 if(null !=brElts && brElts.size() > 2){
         		for(int i=2;i<17;i++){
         			if(i%2 != 0) continue;
         			NoticeTest notice = new NoticeTest();
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
                			notice.setIssueDateStr(issueDateStr.trim());
                		} catch (ParseException e) {
                			logger.info(e.toString());
                			throw new Exception(e);
                		}
                    	notice.setLoadTime(new Timestamp(new Date().getTime()));
                    	notices.add(notice);
                	}else{
                		logger.info("页面解析失败:表格列校验失败,第" + (i+1) + "行数据损坏!");
                		throw new Exception("页面解析失败:表格列校验失败,第" + (i+1) + "行数据损坏!");
                	}
         		}
        	 }else{
        		logger.info("页面解析失败:表格行数校验失败!");
        		throw new Exception("页面解析失败:表格行数校验失败!");
        	 }
        }else{
        	logger.info("页面解析失败:表格整体校验失败!");
        	throw new Exception("页面解析失败:表格整体校验失败!");
        }
        return notices;
	}

    /**
     * 校验采集的列表页HTML是否合规
     *
     * 
     * @param listJson 爬取的列表页面JSON串
     * @return boolean 是否完整，true-完整，false-不完整
     * 
     * */
    private boolean checkListJsonIsLegal(String listJson){
        boolean flag = true;
        if(StringUtils.isBlank(listJson)){
            logger.error("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal listJson is null or empty......");
            flag = false;
            return flag;
        }
        List<Notice> list = new ArrayList<Notice>();
        try{
            // 转化为JSONObject对象
            JSONObject jsonObj = JSON.parseObject(listJson);
            // 截取有用的records
            if(null != jsonObj){
                JSONArray records = jsonObj.getJSONArray("records");
                if (null != records && records.size() > 0) {
                    for(int i=0;i<records.size();i++){
                        JSONObject record = records.getJSONObject(i);
                        if(null != record){
                        	Notice dom = new Notice();
                            // 发布日期
                            String publishdateStr = record.getString("publishdateStr");
                            if(!StringUtils.isBlank(publishdateStr)){
                            	dom.setIssueDateStr(publishdateStr);
                                dom.setIssueDate(sdf.parse(publishdateStr));
                            }
                            // 全称
                            String title = record.getString("title");
                            if(!StringUtils.isBlank(title)){
                            	dom.setFileName(title);
                            }
                            list.add(dom);
                        }else{
                            logger.error("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal record is null......");
                            flag = false;
                            return flag;
                        }
                    }
                }else{
                    logger.error("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal records is null or empty......");
                    flag = false;
                    return flag;
                }
            }else{
                logger.error("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal jsonObj is null......");
                flag = false;
                return flag;
            }
        }catch(Exception e){
            logger.info("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal exception:{}",e);
            flag = false;
            return flag;
        }
        if(CollectionUtils.isEmpty(list)){
            logger.info("NcdCrawlerWithProxyServiceImpl.checkListJsonIsLegal list is empoty");
            flag = false;
            return flag;
        }
        return flag;
    }
}

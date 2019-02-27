package com.nstars.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nstars.constant.DataConstants;
import com.nstars.dao.ProxyServerDao;
import com.nstars.model.ProxyServer;

public class CrawlerUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(CrawlerUtils.class);
		
    private static final String[][] HEADERS = new String[][]{
        {"Connection", "keep-alive"},
        {"Cache-Control", "max-age=0"},
        {"Upgrade-Insecure-Requests", "1"},
        {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"},
        {"Accept-Encoding", "gzip, deflate, sdch"},
        {"Accept-Language", "zh-CN,zh;q=0.8"},
    };


    private final static String[] USER_AGENT = new String[]{
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2623.110 Safari/537.36",
        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36"
    };
    
	
    /**
     * 解析西刺代理列表页HTML
     * 
     * @param pageHtml 爬取的html列表页面
     * @return List<AnnouCatalogDomain> 解析出的Domain集合
     * 
     * */
    public static List<ProxyServer> extractDomainListFromPageHtml(String pageHtml) {
        logger.info("CrawlerUtils.extractDomainListFromPageHtml beg......");
        List<ProxyServer> lst = new ArrayList<ProxyServer>();
        // 非空校验
        if (StringUtils.isBlank(pageHtml)) {
            logger.info("CrawlerUtils.extractDomainListFromPageHtml param is null");
            return lst;
        }
        Timestamp createTime = new Timestamp(new Date().getTime());
        // 转化为DOC对象
        Document doc = Jsoup.parse(pageHtml);
        // 截取有用数据Elements
        Elements ipList = doc.select("table[id=ip_list]");
        if (null != ipList && ipList.size() >0) {
            // 数据table下标为0
            Element table = ipList.get(0);
            if(null != table){
                Elements trList = table.select("tr");
                if(null != trList && trList.size() > 0){
                    // 循环处理列表的每行数据
                    for (int i = 1; i < trList.size(); i++) {
                    	try{
                            Element element = trList.get(i);
                            // 组装Domain对象
                            ProxyServer dom = new ProxyServer();
                            // 标题、链接
                            Elements tdEls = element.select("td");
                            if(null != tdEls && tdEls.size() ==10){
                                // ip
                                String host = tdEls.get(1).text();
                                if(StringUtils.isBlank(host)){
                                	continue;
                                }
                                dom.setHost(host);
                                // port
                                String port = tdEls.get(2).text();
                                if(StringUtils.isBlank(port)){
                                	continue;
                                }
                                dom.setPort(Integer.parseInt(port));
                                // 地址
                                String addr = tdEls.get(3).text();
                                dom.setAttribution(addr);
                                // 类型
                                dom.setTelecomType(tdEls.get(5).text());
                                // 速度
                                String spedStr = tdEls.get(6).select("div").get(0).attr("title");
                                if (!StringUtils.isBlank(spedStr) && Double.parseDouble(spedStr.replace("秒", "").replace("分", "")) > 1) {
                                	//连接速度大于1秒的丢弃
                                    continue;
                                }
                                dom.setLoadTime(createTime);
                                dom.setUpdateTime(createTime);
                                //可用性-有效
                                dom.setIsEffective(1);
                                dom.setSucCount(0);
                                dom.setFailCount(0);
                                lst.add(dom);
                            }else{
                            	logger.info("CrawlerUtils.extractDomainListFromPageHtml td is null or size != 10");
                            }                    		
                    	}catch(Exception e){
                    		logger.error("extractDomainListFromPageHtml exception:{}",e);
                    		continue;
                    	}
                    }
                }else{
                    logger.info("CrawlerUtils.extractDomainListFromPageHtml tr is null or size == 0");
                }
            }else{
                logger.info("CrawlerUtils.extractDomainListFromPageHtml table is null......");
            }
        } else {
            logger.info("CrawlerUtils.extractDomainListFromPageHtml ipList is null or size == 0......");
        }
        logger.info("CrawlerUtils.extractDomainListFromPageHtml end......");
        return lst;
    }
    
    /**
     * 抓取网页
     * 
     * @param url URL链接
     * @return String 爬取结果HTML
     * 
     * */
    public static String crawlProxyFromXici(String url){
    	try{
    		Connection conn = Jsoup.connect(url);
    		for (String[] head : HEADERS) {
    			conn.header(head[0], head[1]);
            }
    		//模拟浏览器
    		conn.header("User-Agent", USER_AGENT[new Random().nextInt(USER_AGENT.length)]);
    		//设置超时时间
    		conn.timeout(10000).followRedirects(true);
    		//执行
            String html = conn.execute().parse().html();
            return html;
    	}catch(Exception e){
    		logger.error("crawlProxyFromXici exception:{}",e);
    	}
    	return null;
    }
    
    /**
     * 抓取网页
     * 
     * @param url URL链接
     * @param isProxy 是否使用代理
     * @param encoding 网页字符集
     * @return String 采集到网页HTML或JSON字符串
     * 
     * */
    public static String sendPost(String url,boolean isProxy,String encoding,ProxyServerDao proxyServerDao) {
        logger.info("CrawlerUtils.sendPost with url:{} beg......",url);
        String pageHtml = "";
        ProxyServer proxy = null;
        URLConnection connection = null;
        Scanner cin = null;
        //发送次数
        int sendTimes = 0;
        do{
            try{
                sendTimes++;
                logger.info("CrawlerUtils.sendPost url:{},sendTime:{} beg......",url,sendTimes);
                // 判断是否使用代理服务器
                if(isProxy){
                    //获取代理服务器
                    proxy = ProxyServerUtil.getProxyFromCache();
                    //连接远程服务器
                    if(sendTimes < 8){
                        if(null != proxy){
                            Proxy URLproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
                            connection = new URL(url).openConnection(URLproxy);
                        }else{
                            connection = new URL(url).openConnection();
                        }
                    }else{
                        // 最后三次不使用代理
                        connection = new URL(url).openConnection();
                    }
                }else{
                	connection = new URL(url).openConnection();
                }

                //连接非空校验
                if(null == connection){
                    logger.info("CrawlerUtils.sendPost url:{},sendTime:{} failed with connection is null!",url,sendTimes);
                    if(sendTimes >= DataConstants.REPEAT_CRAWLER_TIMES){
                        logger.info("CrawlerUtils.sendPost url:{},sendTime:{} failed with connection is null,and craw times reached 10!",url,sendTimes);
                        return pageHtml;
                    }
                    continue;
                }
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
                //连接超时时间(单位:毫秒)
                connection.setConnectTimeout(60000);
                //读取数据超时时间(单位:毫秒)
                connection.setReadTimeout(60000);
                cin = new Scanner(connection.getInputStream(),encoding);
                StringBuilder builder = new StringBuilder();
                while (cin.hasNext()) {
                    builder.append(cin.nextLine());
                }
                cin.close();
                pageHtml = builder.toString();
                if(!StringUtils.isBlank(pageHtml)){
                    //有响应数据
                    logger.info("CrawlerUtils.sendPost url:{},sendTime:{} success!",url,sendTimes);
                    logger.info("CrawlerUtils.sendPost with url:{} end......",url);
                    logger.info("抓取成功,代理服务器:" + proxy.toString());
                    if(null != proxy){
    					//更新该代理服务器的成功次数
    					proxy.setAttribution("suc");
    					proxyServerDao.updateProxyTimes(proxy);
                    }
                    return pageHtml;
                }else{
                    //无响应数据[非正常]
                    logger.info("CrawlerUtils.sendPost url:{},sendTime:{} success,but no response data!",url,sendTimes);
                    logger.info("抓取失败,代理服务器:" + proxy.toString());
                    if(null != proxy){
    					//更新该代理服务器的失败次数
    					proxy.setAttribution("fail");
    					proxyServerDao.updateProxyTimes(proxy);
                    }
                    if(sendTimes >= DataConstants.REPEAT_CRAWLER_TIMES){
                        logger.info("CrawlerUtils.sendPost url:{},sendTime:{} success,but no response data,and craw times reached 10!",url,sendTimes);
                        return pageHtml;
                    }
                }
            }catch(Exception e){
                if(null != proxy){
                    logger.error("CrawlerUtils.sendPost url:{},sendTime:{} exception with proxy ip:{},port:{},exception:{}!",url,sendTimes,proxy.getHost(),proxy.getPort(),e);
					//更新该代理服务器的失败次数
					proxy.setAttribution("fail");
					proxyServerDao.updateProxyTimes(proxy);
                }else{
                    logger.error("CrawlerUtils.sendPost url:{},sendTime:{} exception:{}!",url,sendTimes,e);
                }
                //关闭流和连接
                if(null != cin){
                    try {
                        cin.close();
                    } catch (Exception e1) {
                        logger.error("CrawlerUtils.sendPost url:{},sendTime:{} close cin exception:{}!",url,sendTimes,e1);
                    }
                }
                if(sendTimes >= DataConstants.REPEAT_CRAWLER_TIMES){
                    logger.error("CrawlerUtils.sendPost url:{},sendTime:{} exception,and craw times reached 10!",url,sendTimes);
                    return pageHtml;
                }
            }
            //如果进行下次爬取，休眠5秒
            try {
                Thread.currentThread().sleep(5000L);
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            }
        }while(StringUtils.isBlank(pageHtml) && sendTimes < 10);
        logger.info("CrawlerUtils.sendPost end with url:{} end......",url);
        return pageHtml;
    }
    
    
    /**
     * 抓取网页2,使用JVM方式
     * 
     * @param url URL链接
     * @param encoding 网页字符集
     * @param proxyServerDao 代理服务器Dao
     * @return String 采集到网页HTML或JSON字符串
     * 
     * */
	public static String sendPostWithJVM(String url,String encoding,ProxyServerDao proxyServerDao){
        String pageHtml = "";
        ProxyServer proxy = null;
        Scanner cin = null;
        //发送次数
        int sendTimes = 0;
        do{
    		try{
    			sendTimes++;
    			logger.info("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} beg......",url,sendTimes);
                //获取代理服务器
                proxy = ProxyServerUtil.getProxyFromCache();
                if(null != proxy){
        			//设置代理服务器
        	        System.setProperty("http.proxySet", "true");
        	        System.setProperty("http.proxyHost", proxy.getHost());
        	        System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
                }
    	        //连接远程服务器
    	        URLConnection connection = new URL(url).openConnection();
    	        //连接超时时间(单位:毫秒)
    	        connection.setConnectTimeout(30000);
    	        //读取数据超时时间(单位:毫秒)
    	        connection.setReadTimeout(30000);
    	        cin = new Scanner(connection.getInputStream(),encoding);
    	        StringBuilder builder = new StringBuilder();
    	        while (cin.hasNext()) {
    	            builder.append(cin.nextLine());
    	        }
    	        cin.close();
    	        pageHtml = builder.toString();
    	        if(!StringUtils.isBlank(pageHtml) && pageHtml.indexOf("head")>0 
    	        		&& pageHtml.indexOf("data")>0 && pageHtml.indexOf("records")>0){
                    logger.info("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} success!",url,sendTimes);
                    logger.info("CrawlerUtils.sendPostWithJVM with url:{} end......",url);
                    logger.info("抓取成功,代理服务器:" + proxy.toString());
                    if(null != proxy){
    					//更新该代理服务器的成功次数
    					proxy.setAttribution("suc");
    					proxyServerDao.updateProxyTimes(proxy);
                    }
                    return pageHtml;
    	        }else{
                    //无响应数据或数据不完整
                    logger.info("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} success,but no response data!",url,sendTimes);
                    logger.info("抓取失败,代理服务器:" + proxy.toString());
                    if(null != proxy){
    					//更新该代理服务器的失败次数
    					proxy.setAttribution("fail");
    					proxyServerDao.updateProxyTimes(proxy);
                    }
                    if(sendTimes >= DataConstants.REPEAT_CRAWLER_TIMES){
                        logger.info("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} success,but no response data,and craw times reached 10!",url,sendTimes);
                        return pageHtml;
                    }
    	        }
    		}catch(Exception e){
                if(null != proxy){
                    logger.error("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} exception with proxy ip:{},port:{},exception:{}!",url,sendTimes,proxy.getHost(),proxy.getPort(),e);
					//更新该代理服务器的失败次数
					proxy.setAttribution("fail");
					proxyServerDao.updateProxyTimes(proxy);
                }else{
                    logger.error("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} exception:{}!",url,sendTimes,e);
                }
                //关闭流和连接
                if(null != cin){
                    try {
                        cin.close();
                    } catch (Exception e1) {
                        logger.error("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} close cin exception:{}!",url,sendTimes,e1);
                    }
                }
                if(sendTimes >= DataConstants.REPEAT_CRAWLER_TIMES){
                    logger.error("CrawlerUtils.sendPostWithJVM url:{},sendTime:{} exception,and craw times reached 10!",url,sendTimes);
                    return pageHtml;
                }
    		}
    		
            //如果进行下次爬取，休眠3秒
            try {
                Thread.currentThread().sleep(3000L);
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            }
        }while(StringUtils.isBlank(pageHtml) && sendTimes < 10);
        logger.info("CrawlerUtils.sendPostWithJVM end with url:{} end......",url);
        return pageHtml;
    }

}

package com.nstars.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nstars.constant.DataConstants;
import com.nstars.dao.ProxyServerDao;
import com.nstars.model.ProxyServer;

public class ProxyServerUtil{
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyServerUtil.class);
	
	@Autowired
	private ProxyServerDao proxyServerDao;
	//代理服务器
	public static List<ProxyServer> proxys = new ArrayList<ProxyServer>();
	
    //中国外汇交易中心网
//    private final static String URL = "http://www.chinamoney.com.cn/fe/jsp/CN/chinamoney/notice/beNewDraftByTremList.jsp?pagingPage_il_=1&";    
    private final static String URL_CHINA_MONEY = "http://www.chinamoney.com.cn/index.html";
	
	/**
	 * 定时器调用
	 * 从数据库加载所有可用的代理服务器
	 * 
	 * @param 
	 * @return 
	 * @throws Exception
	 * */
	public void loadProxyServer() throws Exception{
		logger.info("加载代理服务器 开始......");
		List<ProxyServer> proxy = proxyServerDao.getAllEffectiveProxyServers();
		if(null != proxy && proxy.size() > 0){
			proxys = proxy;
			logger.info("加载代理服务器成功,代理服务器{}台!",proxy.size());
		}else{
			logger.info("加载代理服务器失败,没有可用的代理服务器@<_>@!");
		}
		logger.info("加载代理服务器 结束......");
	}
	
    /**
     * 定时器调用
     * 从西刺代理网采集代理服务器
     * 
     * @param
     * @return
     * */
    public void crawlProxyServer(){
    	logger.info("crawlProxyServer beg......");
        String pageHtml = "";
        boolean crawlFlag = true;
        int pageIndex = 1;
        do{
        	if(!isContinueCrawl()){
        		logger.info("crawlProxyServer proxy server >= 150!!!");
        		break;
        	}
        	//采集数据
        	pageHtml = CrawlerUtils.crawlProxyFromXici(DataConstants.XICI_URL + pageIndex);
        	if(!StringUtils.isBlank(pageHtml)){
        		//解析数据
        		List<ProxyServer> proxyList = CrawlerUtils.extractDomainListFromPageHtml(pageHtml);
        		if(null != proxyList && proxyList.size() >0){
        			for(int i=0;i<proxyList.size();i++){
        				//判断库中是否存在该服务器
        				List<ProxyServer> dbLst = proxyServerDao.queryProxyBySective(proxyList.get(i));
        				if(null != dbLst && dbLst.size()>0){
        					continue;
        				}
        				proxyServerDao.insertProxyServer(proxyList.get(i));
        			}
        		}
        	}else{
        		logger.error("crawlProxyServer fail,pageIndex:{}",pageIndex);
        	}
        	//是否爬去下一页
        	if(pageIndex >= 3){
        		//只爬前3页
        		logger.info("crawlProxyServer pageIndex >=3");
        		crawlFlag = false;
        		break;
        	}
        	if(!isContinueCrawl()){
        		logger.info("crawlProxyServer proxy server >= 150");
        		crawlFlag = false;
        		break;
        	}
        	pageIndex++;
        }while(crawlFlag);
        logger.info("crawlProxyServer end......");
    }
	
	/**
	 * 获取内存中的代理服务器
	 * 规则：随机获取
	 * 
	 * @return ProxyServer 代理服务器
	 * @throws Exception
	 */
	public static ProxyServer getProxyFromCache() throws Exception{
		//随机获取代理服务器
		Random rand = new Random();
		if(null != proxys && proxys.size() > 0){
			int indexProxy = rand.nextInt(proxys.size());
			return proxys.get(indexProxy);
		}else{
			logger.info("从缓存获取代理服务器异常!");
			throw new Exception("从缓存获取代理服务器异常,请三分钟之后再来尝试!!");
		}
	}
	
	/**
	 * 定时器调用
	 * 验证数据库中所有可用状态代理服务器的可用性
	 * 
	 * @param 
	 * @return 
	 * @throws Exception
	 * */
	public void validateProxyServer() throws Exception{
		logger.info("验证代理服务器 开始......");
		//即将入缓存的代理服务器
		List<ProxyServer> validProxyList = new ArrayList<ProxyServer>();
		
		//查询可用状态的代理服务器
		List<ProxyServer> dbProxyList = proxyServerDao.getAllEffectiveProxyServers();
		if(null != dbProxyList &&dbProxyList.size() > 0){
			//统计有效代理服务器数量
			int suc = 0;
			//更新库专用Domain
			ProxyServer dom = new ProxyServer();
			for(int i=0;i<dbProxyList.size();i++){
				ProxyServer dbDom = dbProxyList.get(i);
				if(!verifyValidityWithJVM(dbDom.getHost(),dbDom.getPort())){
					logger.error("验证代理服务器:  {}:{},不可用!!!",dbDom.getHost(),dbDom.getPort());
					//更新状态为不可用
					dom.setId(dbDom.getId());
					dom.setIsEffective(0);
					proxyServerDao.updateBySective(dom);
				}else{
					//可用
					suc++;
					logger.info("验证代理服务器:  {}:{},可用。",dbDom.getHost(),dbDom.getPort());
					//更新状态为不可用
					dom.setId(dbDom.getId());
					dom.setIsEffective(1);
					proxyServerDao.updateBySective(dom);
					validProxyList.add(dbDom);
				}
			}
			proxys = validProxyList;
			logger.info("验证代理服务器成功,可用代理服务器{}台!",suc);
		}else{
			logger.info("验证代理服务器，数据库无可用状态的代理服务器@<_>@!");
		}
		logger.info("验证代理服务器 结束......");
	}
	
    /**
     * 验证代理服务器可用性1
     * 测试网址为中国外汇交易中心
     * JVM代理方法
     * 
     * @param ip 代理服务器IP
     * @param port 代理服务器PORT
     * @return 是否可用 false-不可用，true-可用
     * */
	public static boolean verifyValidityWithJVM(String ip,Integer port){
		try{
			//设置代理服务器
	        System.setProperty("http.proxySet", "true");
	        System.setProperty("http.proxyHost", ip);
	        System.setProperty("http.proxyPort", String.valueOf(port));
	        //连接远程服务器
	        URLConnection connection = new URL(DataConstants.VALID_URL).openConnection();
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
	        if(builder.toString().indexOf("head")>0 && builder.toString().indexOf("data")>0 && builder.toString().indexOf("records")>0){
	        	logger.info("校验代理服务器:  {}:{},可用......",ip,port);
	        	return true;
	        }else{
	        	logger.info("校验代理服务器:  {}:{},不可用!!!",ip,port);
	        	return false;
	        }
		}catch(Exception e){
			logger.error("校验代理服务器异常: {}:{},异常如下:{}",ip,port,e);
			return false;
		}
    }
    
    
    /**
     * 验证代理服务器可用性2
     * 测试网址为中国外汇交易中心
     * Proxy类代理方法
     * 
     * @param ip 代理服务器IP
     * @param port 代理服务器PORT
     * @return 是否可用 false-不可用，true-可用
     * */
	public static boolean verifyValidityWithProxy(String ip,Integer port){
    	logger.info("verifyValidityWithProxy ip:{} port:{} beg......",ip,port);
        //http连接和流读取
        URLConnection con = null;
        Scanner cin = null;
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            URL url = new URL(DataConstants.VALID_URL);
            con = url.openConnection(proxy);
            //设置连接参数
            if (null == con) {
                logger.info("verifyValidityWithProxy ip:{} port:{} con is null!!!",ip,port);
                return false;
            }
            con.setConnectTimeout(45000);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);
            con.connect();
            cin = new Scanner(con.getInputStream(), StandardCharsets.UTF_8.name());
            StringBuilder builder = new StringBuilder();
            while (cin.hasNext()) {
            	builder.append(cin.nextLine());
            }
            if(builder.toString().indexOf("中国货币网") >0 && builder.toString().indexOf("table")>0){
            	return true;
            }
            return false;
//            //设置参数
//            httpUrlConnection.setDoOutput(true); // 需要输出
//            httpUrlConnection.setRequestMethod("GET"); // 设置GET方式连接
//            httpUrlConnection.setUseCaches(false); // 不允许缓存
//            httpUrlConnection.setConnectTimeout(45000); // 连接超时时间
//            httpUrlConnection.setReadTimeout(30000); // 读取数据超时时间
//            //设置请求头
////            httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            httpUrlConnection.setRequestProperty("Content-Type", "text/html");
//            httpUrlConnection.setRequestProperty("Charset", StandardCharsets.UTF_8.name());

        } catch (Exception e) {
        	logger.error("verifyValidityWithProxy ip:{} port:{} exception:{}!",ip,port,e);
            return false;
        }finally{
            //关闭流和连接
        	try{
                if(null != cin){
                    cin.close();
                }
        	}catch(Exception e){
        		logger.error("verifyValidityWithProxy ip:{} port:{} close exception:{}!",ip,port,e);
        	}
        }
    }
    
    /**
     * 验证代理服务器可用性3
     * 测试网址为中国外汇交易中心
     * HttpClient 方法
     * 
     * @param ip 代理服务器IP
     * @param port 代理服务器PORT
     * @return 是否可用 false-不可用，true-可用
     * */
    public static boolean verifyValidityWithHttpClient(String ip,Integer port){
    	//代理服务器实例
        HttpHost proxy = new HttpHost(ip, port);
        //设置超时时间和代理服务器
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(45000).setConnectionRequestTimeout(45000).setProxy(proxy).build();
        //创建httpget实例  
        HttpGet httpGet = new HttpGet(DataConstants.VALID_URL);     
        httpGet.setConfig(requestConfig);
//        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        //创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建httpResponse实例
        CloseableHttpResponse response = null;
    	try{
    		response = httpClient.execute(httpGet);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if(200 == statusCode){
    			HttpEntity entity = response.getEntity();
    			if(null != entity){
    				InputStream content = entity.getContent();
    				BufferedReader bufReader = new BufferedReader(new InputStreamReader(content,"utf-8"));
    				String line = "";
    				StringBuilder sb = new StringBuilder();
    				while((line = bufReader.readLine()) != null){
    					sb.append(line);
    				}
    				bufReader.close();
        			if(sb.toString().indexOf("中国货币网") >0 && sb.toString().indexOf("table")>0){
        				return true;
        			}else{
        				return false;
        			}
    			}
    			return false;
    		}else{
    			return false;
    		}
    	}catch(Exception e){
    		logger.error("-----exception:{}",e);
    		return false;
    	}finally{
    		try {
    			if(null != response){
    				response.close();
    			}
			} catch (Exception e) {
				logger.error("-----exception:{}",e);
			}
    	}
    }
    

    
    public static boolean verifyProxyNew3(String ip,Integer port){
    	try{
    		Document doc = Jsoup.connect(DataConstants.VALID_URL)
                    .userAgent("Mozilla").ignoreContentType(true)  
                    .timeout(30000).proxy(ip, port)  
                    .get();  

    		String html = doc.body().text().trim().toString(); 
	        if(!StringUtils.isBlank(html)){
	        	return true;
	        }else{
	        	return false;
	        }
    	}catch(Exception e){
    		logger.error("crawlProxyFromXici exception:{}",e);
    		return false;
    	}
    }
    
	/**
	 * 验证代理服务器好用否
	 * 
	 * @param ip 代理服务器IP
	 * @param port 代理服务器端口号
	 * @return boolean 是否可用，true-可用，false-不可用
	 * @throws 
	 * 
	 */
    public static boolean isProxy(String ip,Integer port) {
		if (StringUtils.isBlank(ip) || port == null) {
			return false;
		}
		//发送请求 验证
		try {
			Jsoup.connect(URL_CHINA_MONEY)
			        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
			        .timeout(30 * 1000)
			        .proxy(ip, port)
			        .get();
		} catch (Exception e) {
			logger.error("isProxy exception:{}",e);
			return false;
		}
		return true;
	}
    
    /**
     * 验证代理是否可用
     * @param ip
     * @param port
     * @return
     */
    public static boolean isProxy2(String ip, Integer port ){
		if (StringUtils.isBlank(ip) || port == null) {
			return false;
		}
        boolean useful;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(DataConstants.VALID_URL);
            InetSocketAddress addr = new InetSocketAddress(ip, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(30 * 1000);
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(30 * 1000);
            int rCode = connection.getResponseCode();
            useful =  rCode == 200;
        }catch (Exception e1){
            logger.warn(String.format("verify proxy %s:%d exception: "+e1.getMessage(),ip, port));
            useful = false;
        }finally {
            if(connection != null)connection.disconnect();
        }
        logger.info(String.format("verify proxy %s:%d useful: "+useful, ip, port));
        return useful;
    }
    
    /**
     * 判断是否继续采集代理服务器
     * 规则：如果数据库中有效状态的服务器大于或等于150条，则停止爬去
     * 
     * @return 是否继续采集代理服务器，true-继续采集，false-停止采集
     */
    public boolean isContinueCrawl(){
    	Integer num = proxyServerDao.countProxyServerEffective();
    	if(null == num || num < 150){
    		return true;
    	}
    	return false;
    }

}

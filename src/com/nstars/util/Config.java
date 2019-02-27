package com.nstars.util;

import java.io.IOException;
import java.util.Properties;

/**
 * 加载配置文件
 */
public class Config {

    /**
     * 是否使用代理抓取
     */
    public static boolean isProxy;
    /**
     * 日志路径
     * */
    public static String logPath;
    /**
     * 爬取统计结果路径
     * */
    public static String crawlerPath;
    static {
        Properties p = new Properties();
        try {
            p.load(Config.class.getResourceAsStream("/config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        isProxy = Boolean.valueOf(p.getProperty("isProxy"));
        logPath = p.getProperty("log_path");
        crawlerPath = p.getProperty("crawler_log_path");
    }

}

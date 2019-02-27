package com.nstars.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
	static DateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	static DateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	public static String getCurrentTime(){
		return sdfTime.format(new Date());
	}
	
	/**
	 * 获取服务器当前日期字符串
	 * @param
	 * @return yyyy-MM-dd
	 * @throws 
	 * */
	public static String getCurrentDate(){
		return sdfDate.format(new Date());
	}
	
	/**
	 * 获取服务器当前日期字符串
	 * @param
	 * @return (yyyy-MM)
	 * @throws
	 * */
	public static String getCurrentYearMonthStr(){
		String dateStr = sdfDate.format(new Date());
		return dateStr.substring(0, dateStr.length()-3);
	}
	
	/**
	 * 遍历两个日期字符串之间的所有日期
	 * 
	 * @param start 起始日期
	 * @param end 终止日期
	 * @return dayMap [start,end]所有的日期串(yyyy-MM-dd)
	 * @throws Exception
	 * */
	public static Map<String,Integer> traverTwoDays(String start,String end) throws Exception{
		Date startDate = sdfDate.parse(start);
		Date endDate = sdfDate.parse(end);
		Map<String,Integer> dayMap = new HashMap<String,Integer>();
		//把开始时间加入集合
		dayMap.put(start, 0);
	    Calendar cal = Calendar.getInstance();
	    //使用给定的 Date 设置此 Calendar 的时间
	    cal.setTime(startDate);
	    boolean bContinue = true;
	    while (bContinue) {
	        //根据日历的规则，为给定的日历字段添加或减去指定的时间量
	        cal.add(Calendar.DAY_OF_MONTH, 1);
	        // 测试此日期是否在指定日期之后
	        if (endDate.after(cal.getTime())) {
	            dayMap.put(sdfDate.format(cal.getTime()), 0);
	        } else {
	            break;
	        }
	    }
	    //把结束时间加入集合
	    dayMap.put(end, 0);
	    return dayMap;
	}
	
	
}

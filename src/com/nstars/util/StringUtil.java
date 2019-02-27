package com.nstars.util;

public class StringUtil {
	
	public static boolean isEmpty(String str){
		if(null == str || "".equals(str)){
			return true;
		}
		return false;
	}

}

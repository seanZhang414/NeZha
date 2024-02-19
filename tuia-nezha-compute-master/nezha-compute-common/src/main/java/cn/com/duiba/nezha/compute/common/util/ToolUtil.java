package cn.com.duiba.nezha.compute.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtil {
	
	public static String trimComma(String str) {
		if(str.startsWith(",")) {
			str = str.substring(1);
		}
		if(str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	
	public static Boolean isNumType(String str) {
		
		Pattern pattern = Pattern.compile("[0-9]*");
		if(null == str)
			return false;
		Matcher isNum = pattern.matcher(str);
		if(!isNum.matches()){
			return false;
		}
		return true;
	}
}

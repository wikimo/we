package org.we.util;

/**
 * 字符串扩展工具类
 * @author wikimo
 *
 */
public class StringUtil {
	public static String formatFirstLetter(String str, String flag){
		StringBuilder sb =  new StringBuilder();
		
		if(flag == "upper"){
			sb.append(Character.toUpperCase(str.charAt(0))); 
		}else{
			sb.append(Character.toLowerCase(str.charAt(0))); 
		}
		sb.append(str.substring(1));
		return sb.toString();
	}
	
	public static void main(String[] args){
		System.out.println(StringUtil.formatFirstLetter("user", ""));
	}
}
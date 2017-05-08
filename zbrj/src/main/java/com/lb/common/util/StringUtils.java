package com.lb.common.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.text.TextUtils;

import com.lb.common.util.Log;
import com.lz.oncon.app.im.util.CharacterTool;
import com.lz.oncon.data.AccountData;

public class StringUtils {
	// 秒转成分钟
	public static String sTOm(int second){
		if(second<60){
			return second+"\"";
		}else if(second==60){
			return "1\'";
		}else{
			int m = second/60;
			int s = second%60;
			return m+"\'"+s+"\"";
		}
	}
	
	//判断合法的手机号码
	public static boolean isMobileNumber(String mobiles){ 
//		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"); 
		Pattern p = Pattern.compile("^1[3|4|5|8][0-9]\\d{8}$"); 
		Matcher m = p.matcher(mobiles); 
		System.out.println(m.matches()+"---"); 
		return m.matches(); 
		} 
	// 判断字符串是否为空
	public static boolean isNull(String s){
		if(s == null || s.equals("") || s.length() == 0){
			return true;
		} else {
			return false;
		}
	}
	
	public static int length(String str){
		int length = 0;
		try {
			if(!TextUtils.isEmpty(str)){
				length = str.getBytes("UTF-8").length;
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return length;
	}

	/**
     * 替换Null
     * @param str
     * @return
     */
    public static String repNull(String str) {
        if(str==null)str="";
        return str;
    }
    
    @SuppressWarnings("rawtypes")
	public static int getStringIdx(Context context, String key){
		int i = 0;
		try {
			Class localClass = Class.forName(context.getPackageName() + ".R$string");
			Field localField = localClass.getField(key);
		    i = Integer.parseInt(localField.get(localField.getName()).toString());
		} catch (ClassNotFoundException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} catch (SecurityException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} catch (NoSuchFieldException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} catch (NumberFormatException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		
	    return i;
	}

    /**
     * 去除字符串中的空格回车
     * @param sub
     * @return
     */
	public static String subString(String sub) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(sub);
		return m.replaceAll("");
	}
	
	public static String timePhoneNum(String mobile){
		if(TextUtils.isEmpty(mobile)){
			mobile = StringUtils.repNull(mobile);
		}else{
			mobile = mobile.trim();
			mobile = mobile.replaceAll("-", "");
			mobile = mobile.replaceAll(" ", "");//半角空格
			mobile = mobile.replaceAll("　", "");//全角空格
		}
		return mobile;
	}
	
	public static String timePhoneNumWithNN(String mobile){
		try{
			if("800".equals(mobile) || "901".equals(mobile) || mobile.startsWith("gz")){
				return mobile;
			}
			String nationNumber = AccountData.getInstance().getNationalNumber();
			if(Constants.DEFAULT_NATIONAL_CODE.equals(nationNumber)){
				if(mobile.startsWith("+")){
					mobile = mobile.replaceFirst("\\+", "00");
				}
				if(mobile.startsWith("0086")){
					mobile = mobile.replaceFirst("0086", "");
				}
			}else{
				String s3 = "";//254
				if(nationNumber.startsWith("00")){
					s3 = nationNumber.substring("00".length(),nationNumber.length());
				}
				if(mobile.startsWith("+")){
					mobile = mobile.replaceFirst("\\+", "00");
				}
				if(mobile.startsWith(s3)){
					mobile = mobile.replaceFirst(s3, nationNumber);
				}
				if(mobile.startsWith("07")){//肯尼亚
					mobile = mobile.replaceFirst("0", nationNumber);
				}
				if (mobile.startsWith("86")) {
					mobile = mobile.replaceFirst("86", "");
				}
				if(!mobile.startsWith("00")){
					mobile = nationNumber + mobile;
				}
				if(mobile.startsWith("0086")){
					mobile = mobile.replaceFirst("0086", "");
				}
			}
			mobile = timePhoneNum(mobile);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return mobile;
	}
	
	/**
	 * 去掉国际码，判断是否在通讯录
	 * @param mobile
	 * @return
	 */
	public static String timePhoneNumWithNoNationNumber(String mobile){
		try{
			if("800".equals(mobile) || "901".equals(mobile) || mobile.startsWith("gz")){
				return mobile;
			}
			String nationNumber = AccountData.getInstance().getNationalNumber();
			if(Constants.DEFAULT_NATIONAL_CODE.equals(nationNumber)){
				if(mobile.startsWith("+")){
					mobile = mobile.replaceFirst("\\+", "00");
				}
				if(mobile.startsWith("0086")){
					mobile = mobile.replaceFirst("0086", "");
				}
			}else{
				String s3 = "";//254
				if(nationNumber.startsWith("00")){
					s3 = nationNumber.substring("00".length(),nationNumber.length());
				}
				if(mobile.startsWith("+")){
					mobile = mobile.replaceFirst("\\+", "00");
				}
				if(mobile.startsWith(s3)){
					mobile = mobile.replaceFirst(s3, nationNumber);
				}
				if(mobile.startsWith("07")){//肯尼亚
					mobile = mobile.replaceFirst("0", nationNumber);
				}
				if(!mobile.startsWith("00")){
					mobile = nationNumber + mobile;
				}
				
				if(mobile.startsWith(nationNumber)){
					mobile = mobile.replaceFirst(nationNumber, "");
				}
				
				if(mobile.startsWith("0086")){
					mobile = mobile.replaceFirst("0086", "");
				}
			}
			mobile = timePhoneNum(mobile);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return mobile;
	}
	
	// 计算字符串字节长度  
	public static int charlen(String str) {
		if (str == null || str.length() <= 0) {
			return 0;
		}

		int len = 0;
		char c;
		for (int i = str.length() - 1; i >= 0; i--) {
			c = str.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')) { // 字母, 数字
				len++;
			} else {
				if (Character.isLetter(c)) { // 中文
					len += 2;
				} else { // 符号或控制字符
					len++;
				}
			}
		}
		return len;
	}
	
	// 计算字符串长度  
	public static int strlen(String str){
		int len = charlen(str);
		if(len%2 == 1){
			len = len/2+1;
		}else{
			len = len/2;
		}
		return len;
	}
	
	public static boolean isMobile(String mobile){
		return isNull(mobile) ? false : mobile.length() == 11 || mobile.length() == 15 ? true : false;
	}
	
	public static boolean isPublicAccount(String mobile){
		return isNull(mobile) ? false : mobile.startsWith("gz") ? true : false;
	}
	
	public static String parseFromPSTN(String pstnnum){
		String num = "";
		try{
			if(pstnnum.startsWith("9")){
				num = pstnnum.substring(1);
			}else{
				num = pstnnum;
			}
		}catch(Exception e){
			
		}
		return num;
	}
	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	
	/**
	 * 根据数字月份转换为英文缩写
	 * @param monthNum
	 * @return
	 */
	public static String MonthNumToMonthStr(String monthNum) {
		if ("01".equals(monthNum)) {
			return "Jan";
		}else if ("02".equals(monthNum)) {
			return "Feb";
		}else if ("03".equals(monthNum)) {
			return "Mar";
		}else if ("04".equals(monthNum)) {
			return "Apr";
		}else if ("05".equals(monthNum)) {
			return "May";
		}else if ("06".equals(monthNum)) {
			return "Jun";
		}else if ("07".equals(monthNum)) {
			return "Jul";
		}else if ("08".equals(monthNum)) {
			return "Aug";
		}else if ("09".equals(monthNum)) {
			return "Sep";
		}else if ("10".equals(monthNum)) {
			return "Oct";
		}else if ("11".equals(monthNum)) {
			return "Nov";
		}else if ("12".equals(monthNum)) {
			return "Dec";
		}
		return "";
	}
	
	/**
	 * 验证密码是否正确
	 * 
	 * @param mNewPasswordStr
	 *            新密码
	 * @return 匹配正确返回true，否则返回false
	 */
	public static boolean isRigthPW(String newPasswordStr) {
		return newPasswordStr.matches("^[a-zA-Z0-9_]{6,20}$");
	}
	
	public static String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		String characterIndex = CharacterTool.getPinYin(String.valueOf(c));
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(characterIndex).matches()) {
			return characterIndex.toUpperCase();
		} else {
			return "#";
		}
	}
	
	public static String checkXmlChar(String data) {
    	if(TextUtils.isEmpty(data)){
    		return data;
    	}
    	StringBuffer appender = new StringBuffer("");
        appender = new StringBuffer(data.length());
        for (int i = 0; i < data.length(); i++) {
        	char ch = data.charAt(i);
        	if ((ch == 0x9)//Tab
        			|| (ch == 0xA)//换行
        			|| (ch == 0xD)//回车
        			|| ((ch >= 0x20) && (ch <= 0xD7FF))
        			|| ((ch >= 0xE000) && (ch <= 0xFFFD))
        			|| ((ch >= 0x10000) && (ch <= 0x10FFFF)))
        		appender.append(ch);
        }
        String result = appender.toString();
        return result.replaceAll("]]>", "");
    }
}

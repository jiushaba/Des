package com.lb.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

public class DateUtil {
	
	public static final String CHINESE_PATTERN = "MM月dd日  HH:mm";

	public static String getDateString(Date date){
		return new SimpleDateFormat("yyyyMMdd")
		.format(date);
	}
	
	public static String getDateStringWithWeek() {
		return  new SimpleDateFormat("yyyy.MM.dd EEEE")
				.format(Calendar.getInstance().getTime());
	}
	
	public static String getDateString() {
		return  new SimpleDateFormat("yyyyMMddHHmmss")
				.format(Calendar.getInstance().getTime());
	}
	
	public static String getDateTimeString() {
		return  new SimpleDateFormat("yyyyMMddHHmmssSSS")
				.format(Calendar.getInstance().getTime());
	}
	
	public static String getDateString(String deliver){
		return  new SimpleDateFormat("yyyy"+deliver+"MM"+deliver+"dd")
		.format(Calendar.getInstance().getTime());
	}
	
	public static String getDateTimeString(String ddeliver, String tdeliver){
		return  new SimpleDateFormat("yyyy"+ddeliver+"MM"+ddeliver+"dd HH" + tdeliver + "mm")
		.format(Calendar.getInstance().getTime());
	}
	
	public static String getFullDateTimeString(String ddeliver, String tdeliver){
		return  new SimpleDateFormat("yyyy"+ddeliver+"MM"+ddeliver+"dd HH" + tdeliver + "mm" + tdeliver + "ss")
		.format(Calendar.getInstance().getTime());
	}
	
	public static String getDateString(Date date, String pattern){
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 获取当前Date对象
	 * 目前取系统时间
	 * @return
	 */
	public static Date getCurrentDate(){
		return Calendar.getInstance().getTime();
	}
	/**
	 * 根据字符串获得日期
	 * @param date yyyy-MM-dd
	 * @return
	 */
	public static Date getDate(String date){
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getDateTime(String date, String ddeliver, String tdeliver){
		try {
			return new SimpleDateFormat("yyyy"+ddeliver+"MM"+ddeliver+"dd HH" + tdeliver + "mm" + tdeliver + "ss").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 获取3个月前的时间 (毫秒值)
	 * @return threeMonthAgo
	 */
	public static String get3MonthAgoMillis() {
		long currentTimeMillis = System.currentTimeMillis();
		long three = 90*24*60*60*1000L;
		long threeMonthAgo = currentTimeMillis - three;
		return String.valueOf(threeMonthAgo);
	}
	
	/**
	 * 获取7天前的时间 (毫秒值)
	 * @return sevenDayAgo
	 */
	public static String get7DayAgoMillis() {
		long currentTimeMillis = System.currentTimeMillis();
		long seven = 7*24*60*60*1000L;
		long sevenDayAgo = currentTimeMillis - seven;
		return String.valueOf(sevenDayAgo);
	}
	
	/**
	 * 根据用户生日计算年龄
	 */
	public static int getAgeByBirthday(String birthday) {
		Date b = DateUtil.getDate(birthday);
		if(b == null){
			return 0;
		}
		Calendar cal = Calendar.getInstance();

		if (cal.before(b)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(b);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth 
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth 
				age--;
			}
		}
		return age;
	}
	
	public static boolean isSameYM(String time1, String time2){
		if(TextUtils.isEmpty(time1) || TextUtils.isEmpty(time2)){
			return false;
		}
		if(time1.substring(0,  7).equals(time2.substring(0,7))){
			return true;
		}
		return false;
	}
	
	public static String getAstro(int month, int day) {
          String[] astro = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
                          "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
          int[] arr = new int[] { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };// 两个星座分割日
          int index = month;
          // 所查询日期在分割日之前，索引-1，否则不变
          if (day < arr[month - 1]) {
                  index = index - 1;
          }
          // 返回索引指向的星座string
          return astro[index];
	}
}

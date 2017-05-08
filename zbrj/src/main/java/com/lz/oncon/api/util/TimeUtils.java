package com.lz.oncon.api.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.lb.common.util.Log;
import com.lz.oncon.api.core.im.data.Constants;


public class TimeUtils {
	
	/**
	 * @author chenyya 
	 * chenyya 自制 将传入的一个时间转成当前手机设置的时区的时间, 有好方式可优化
	 * @param time 时间(单位:毫秒)
	 * @return 当地的时间(单位:毫秒)
	 */
	public static long getStringByTime(long time){
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			TimeZone defaulTimeZ = TimeZone.getTimeZone("GMT+08:00");  //取中国标准时区时间
			TimeZone timeZone = TimeZone.getDefault(); //取当前手机设置的时区的时间
			int defaultOff = defaulTimeZ.getOffset(time); //获取中国标准时区和世界标准时区(GMT+00:00)之间的偏移量
			int offset = timeZone.getOffset(time); //获取当前手机设置的时区和世界标准时区(GMT+00:00)之间的偏移量
			int subMill = defaultOff - offset; // 通过两个偏移量即可得到当前手机设置的时区和中国时区相差的时间 (单位:毫秒)
			time -= subMill; //将传入的时间减去 相差的时间 即可得到当地的时间...
			return time;
		} catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return -1;
	}
}

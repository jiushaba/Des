package com.lz.oncon.activity.friendcircle;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.text.TextUtils;

import com.xuanbo.xuan.R;

/**
 * 时间处理
 * 
 * @author yao
 * 
 */
public class FC_TimeUtils {

	public static String getTime(Context mc, long time) {
		long currentTime = System.currentTimeMillis();
		long finalTime = currentTime - time;
		long tt;
		if (finalTime > 0 && finalTime < 1 * 60 * 1000) { // 1分钟内
			return "1" + mc.getString(R.string.fc_time_minute);
		} else if (finalTime > (1 * 60 * 1000) && finalTime < (1 * 60 * 60 * 1000)) { // 1小时内
			tt = finalTime / (60 * 1000);
			if (tt == 1) {
				return String.valueOf(tt) + mc.getString(R.string.fc_time_minute); // minute
																					// ago
			} else {
				return String.valueOf(tt) + mc.getString(R.string.fc_time_minutes); // minutes
																					// ago
			}
		} else if (finalTime > (1 * 60 * 60 * 1000) && finalTime < (24 * 60 * 60 * 1000)) { // 1小时~24小时(1天内)
			tt = finalTime / (1 * 60 * 60 * 1000);
			if (tt == 1) {
				return String.valueOf(tt) + mc.getString(R.string.fc_time_hour); // hour
																					// ago
			} else {
				return String.valueOf(tt) + mc.getString(R.string.fc_time_hours); // hours
																					// ago
			}
		} else if (finalTime > (1 * 24 * 60 * 60 * 1000)) { // 大于1天
			tt = finalTime / (1 * 24 * 60 * 60 * 1000);
			if (tt == 1) {
				return String.valueOf(tt) + mc.getString(R.string.fc_time_day); // day
																				// ago
			} else {
				return String.valueOf(tt) + mc.getString(R.string.fc_time_days); // days
																					// ago
			}
		}

		return "";
	}

	public static String getEDate(Context mc, long longtime) {
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String now_time = sdf.format(currentTime);
		String old_time = sdf.format(longtime);
		String now_daily = "";
		String old_daily = "";
		String[] now_elements = now_time.split("-");
		if (now_elements != null && now_elements.length > 1) {
			if (now_elements.length > 2) {
				now_daily = now_elements[2];
			}
		}
		String[] old_elements = old_time.split("-");
		if (old_elements != null && old_elements.length > 1) {
			if (old_elements.length > 2) {
				old_daily = old_elements[2];
			}
		}

		//今天和昨天的判断
		if (!TextUtils.isEmpty(now_daily) && !TextUtils.isEmpty(old_daily)) {
			if (now_daily.equals(old_daily)) {
				return mc.getString(R.string.fc_today);
			} else if (Integer.parseInt(now_daily)-Integer.parseInt(old_daily) == 1) {
				return mc.getString(R.string.fc_yesterday);
			}else{
				return old_time;
			}
		}else{
			return old_time;
		}
	}

	public static String getNoReadMessagDate(Context mc, long longtime) {
//		long currentTime = System.currentTimeMillis();
//		long finalTime = currentTime - longtime;
//		if (finalTime >= 0 && finalTime < (24 * 60 * 60 * 1000)) { // 0小时~24小时
//			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//			return sdf.format(longtime);
//		} else {
//			SimpleDateFormat sdf = new SimpleDateFormat("MM" + mc.getString(R.string.fc_message_month) + "dd" + mc.getString(R.string.fc_message_day) + " HH:mm");
//			return sdf.format(longtime);
//		}
		

		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String now_time = sdf.format(currentTime);
		String old_time = sdf.format(longtime);
		String now_daily = "";
		String old_daily = "";
		String[] now_elements = now_time.split("-");
		if (now_elements != null && now_elements.length > 1) {
			if (now_elements.length > 2) {
				now_daily = now_elements[2];
			}
		}
		String[] old_elements = old_time.split("-");
		if (old_elements != null && old_elements.length > 1) {
			if (old_elements.length > 2) {
				old_daily = old_elements[2];
			}
		}

		if (!TextUtils.isEmpty(now_daily) && !TextUtils.isEmpty(old_daily)) {
			if (now_daily.equals(old_daily)) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
				return sdf1.format(longtime);
			} else{
				SimpleDateFormat sdf2 = new SimpleDateFormat("MM" + mc.getString(R.string.fc_message_month) + "dd" + mc.getString(R.string.fc_message_day) + " HH:mm");
				return sdf2.format(longtime);
			}
		}else{
			SimpleDateFormat sdf3 = new SimpleDateFormat("MM" + mc.getString(R.string.fc_message_month) + "dd" + mc.getString(R.string.fc_message_day) + " HH:mm");
			return sdf3.format(longtime);
		}
	
	}

	/**
	 * 7月20日
	 * 
	 * @param mc
	 * @param longtime
	 * @return
	 */
	public static String getMessagDetailDate(Context mc, long longtime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM" + mc.getString(R.string.fc_message_month) + "dd" + mc.getString(R.string.fc_message_day) + " HH:mm");
		return sdf.format(longtime);
	}

}

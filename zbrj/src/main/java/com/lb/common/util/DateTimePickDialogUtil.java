package com.lb.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.xuanbo.xuan.R;
import com.lz.oncon.application.MyApplication;

public class DateTimePickDialogUtil implements OnDateChangedListener,
		OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		if(!TextUtils.isEmpty(initDateTime)){
			String hourStr = "";
			String minuteStr = "";
			if(initDateTime.indexOf(":")!=-1){
				hourStr = initDateTime.substring(0, initDateTime.indexOf(":")); // 
				minuteStr = initDateTime.substring(initDateTime.indexOf(":") + 1, initDateTime.length()); // 
			}
			if(!TextUtils.isEmpty(hourStr)&&!TextUtils.isEmpty(hourStr)){
				calendar = this.getCalendarByInintData(hourStr,minuteStr);
			}else{
				initDateTime = calendar.get(Calendar.YEAR) + activity.getResources().getString(R.string.fc_message_year)
						+ calendar.get(Calendar.MONTH) +  activity.getResources().getString(R.string.fc_message_month)
						+ calendar.get(Calendar.DAY_OF_MONTH) +  activity.getResources().getString(R.string.fc_message_day)
						+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
						+ calendar.get(Calendar.MINUTE);
			}
		}else{
			initDateTime = calendar.get(Calendar.YEAR) + activity.getResources().getString(R.string.fc_message_year)
					+ calendar.get(Calendar.MONTH) +  activity.getResources().getString(R.string.fc_message_month)
					+ calendar.get(Calendar.DAY_OF_MONTH) +  activity.getResources().getString(R.string.fc_message_day)
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}
		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	@SuppressLint("NewApi")
	public AlertDialog dateTimePicKDialog(final OnTimeCallInterface onTimeCall) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(false);
		timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT)
				.setTitle(activity.getResources().getString(R.string.setting_time))
				.setView(dateTimeLayout)
				.setPositiveButton(activity.getResources().getString(R.string.btn_setting), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(onTimeCall!=null){
							onTimeCall.onTimeCall(dateTime);
						}
					}
				})
				.setNegativeButton(activity.getResources().getString(R.string.cancal_chooser_contact), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();

		onDateChanged(null, 0, 0, 0);
		return ad;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(activity.getResources().getString(R.string.setting_time));
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String hourStr,String minuteStr) {
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentDay =  calendar.get(Calendar.DAY_OF_MONTH);
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();
		calendar.set(currentYear, currentMonth, currentDay, currentHour,
				currentMinute);
		return calendar;
	}
	
	/**
	 * 判断当前时间是否在设置时间内
	 * @return
	 */
	public static boolean isLimitSound(){
		boolean isSound = false;
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String hour = "00" + calendar.get(Calendar.HOUR_OF_DAY) + "";
		String minute = "00" + calendar.get(Calendar.MINUTE) + "";
		String nowtime = hour.substring(hour.length() - 2, hour.length()) + minute.substring(minute.length() - 2, minute.length());
		String starttime = MyApplication.getInstance().mPreferencesMan.getDisturbStartTime();
		String endtime =  MyApplication.getInstance().mPreferencesMan.getDisturbEndTime();
		if (starttime == null || starttime.length() == 0 || endtime == null || endtime.length() == 0) {
			return isSound;
		}
		String hourStr = "";
		String minuteStr = "";
		if(starttime.indexOf(":")!=-1){
			hourStr = starttime.substring(0, starttime.indexOf(":")); // 
			minuteStr = starttime.substring(starttime.indexOf(":") + 1, starttime.length()); // 
		}
		starttime = hourStr.concat(minuteStr);
		if(endtime.indexOf(":")!=-1){
			hourStr = endtime.substring(0, endtime.indexOf(":")); // 
			minuteStr = endtime.substring(endtime.indexOf(":") + 1, endtime.length()); // 
		}
		endtime = hourStr.concat(minuteStr);
		
		if(TextUtils.isEmpty(starttime)||TextUtils.isEmpty(endtime)){
			return isSound;
		}
		if (Integer.parseInt(starttime) <= Integer.parseInt(nowtime) && Integer.parseInt(nowtime) < Integer.parseInt(endtime)) {
			isSound = true;
		}
		return isSound;
	}
	
	
	public interface OnTimeCallInterface{
		public void onTimeCall(String time);
	}

}

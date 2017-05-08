package com.lb.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class AppInfoUtil {
	
	
	/**
	 * 获取当前应用的所有Activity
	 * @param context
	 * @return
	 */
	private static HashMap<String, Object> getAllActivityInPackageName(Context context) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activities = packageInfo.activities;
			for (int i = 0; i < activities.length; i++) {
				if(activities[i]!=null&&!TextUtils.isEmpty(activities[i].name)){
					map.put(activities[i].name,activities[i].name);
				}
			}
			return map;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取手机里所有应用的所有Activity
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getAllAppActivities(Context context) {
		ArrayList<String> activitiesList = new ArrayList<String>();		
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		for (int i = 0; i < packageInfoList.size(); i++) {
			ActivityInfo[] activities = packageInfoList.get(i).activities;
			if (activities != null) {
				for (int j = 0; j < activities.length; j++) {
					activitiesList.add(activities[i].name);
				}
			}
		}
		return activitiesList;
	}
	
	/**
	 * 判断activity 是否存在当前应用
	 * @param mContext
	 * @param activityname
	 * @return
	 */
	public static  boolean isExitActivityInPackage(Context mContext,String activityname){
		boolean isExit =false;
		HashMap<String, Object> map = getAllActivityInPackageName(mContext);
		if(map.containsKey(StringUtils.repNull(activityname))){
			isExit = true;
		}
		return isExit;
	}

}

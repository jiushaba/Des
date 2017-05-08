package com.lb.common.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

public class TaskUtil {
	//判断程序是否在前台运行
	public static boolean isTopActivity(Context ctx, String packageName){
		ActivityManager am = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos = am.getRunningTasks(1);
		if(taskInfos != null && taskInfos.size() > 0){
			if(packageName.equals(taskInfos.get(0).topActivity.getPackageName())){
				return true;
			}
		}
		return false;
	}
}

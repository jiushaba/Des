package com.lz.oncon.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.lb.common.util.Constants;

import android.app.Activity;
import com.lb.common.util.Log;

public class ActivityManager {
	private static Stack<Activity> activityStack;
	public static Stack<Activity> getActivityStack() {
		return activityStack;
	}

	public static void setActivityStack(Stack<Activity> activityStack) {
		ActivityManager.activityStack = activityStack;
	}

	private static ActivityManager instance;

	private ActivityManager() {
	}

	public static ActivityManager getScreenManager() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	// 退出栈顶Activity
	public void popActivity(Activity activity) {
		if (activity != null) {
			// 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void popActivity(Class cls) {
		if(activityStack == null){
			return;
		}
		Iterator<Activity> activityIterator = activityStack.iterator();
		if(activityIterator == null){
			return;
		}
		List<Activity> ll = new ArrayList<Activity>();
		while (activityIterator.hasNext()) {
			ll.add(activityIterator.next());
		}
		if(ll != null && ll.size() > 0){
			for(int i=0;i<ll.size();i++){
				Activity activity = ll.get(i);
				if (activity == null) {
					continue;
				}
				if (activity.getClass().equals(cls)) {
					popActivity(activity);
				}
			}
			ll = null;
			activityIterator = null;
		}
	}

	// 获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = null;
		if (!activityStack.empty())
			activity = activityStack.lastElement();
		return activity;
	}

	// 将当前Activity推入栈中
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	// 退出栈中所有Activity
	@SuppressWarnings("rawtypes")
	public void popAllActivityExceptOne(Class cls) {
		if(activityStack == null){
			return;
		}
		Iterator<Activity> activityIterator = activityStack.iterator();
		if(activityIterator == null){
			return;
		}
		List<Activity> ll = new ArrayList<Activity>();
		while (activityIterator.hasNext()) {
			ll.add(activityIterator.next());
		}
		if(ll != null && ll.size() > 0){
			for(int i=0;i<ll.size();i++){
				Activity activity = ll.get(i);
				if (activity == null) {
					continue;
				}
				if (activity.getClass().equals(cls)) {
					continue;
				}
				popActivity(activity);
			}
			ll = null;
			activityIterator = null;
		}
	}
	
	public void popAllActivity() {
		try{
			if(activityStack == null){
				return;
			}
			Iterator<Activity> activityIterator = activityStack.iterator();
			if(activityIterator == null){
				return;
			}
			List<Activity> ll = new ArrayList<Activity>();
			while (activityIterator.hasNext()) {
				ll.add(activityIterator.next());
			}
			if(ll != null && ll.size() > 0){
				for(int i=0;i<ll.size();i++){
					Activity activity = ll.get(i);
					if (activity == null) {
						continue;
					}
					popActivity(activity);
				}
				ll = null;
				activityIterator = null;
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public Activity getTop(){
		if(activityStack == null || activityStack.size() == 0){
			return null;
		}else{
			return activityStack.get(activityStack.size() - 1);
		}
	}
}
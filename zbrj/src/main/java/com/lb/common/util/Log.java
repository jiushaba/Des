package com.lb.common.util;

public class Log {
	
	private final static boolean isShowLog = true;
	private final static boolean isShowELog = true;
	
	public static void e(String msg, Throwable e){
		if(isShowELog){
			android.util.Log.e(Constants.LOG_TAG, msg,e);
		}
	}
	public static void e(String tag, String msg, Throwable e){
		if(isShowELog){
			android.util.Log.e(tag, msg,e);
		}
	}
	public static void e(String tag,String msg){
		if(isShowELog){
			android.util.Log.e(tag, msg);
		}
	}
	public static void w(String tag,String msg){
		if(isShowLog){
			android.util.Log.w(tag, msg);
		}
	}
	public static void d(String tag,String msg){
		if(isShowLog){
			android.util.Log.d(tag, msg);
		}
	}
	public static void i(String tag,String msg){
		if(isShowLog){
			android.util.Log.i(tag, msg);
		}
	}
	public static void v(String tag,String msg){
		if(isShowLog){
			android.util.Log.v(tag, msg);
		}
	}
	
	public static String getStackTraceString(Throwable tr){
		return android.util.Log.getStackTraceString(tr);
	}
}

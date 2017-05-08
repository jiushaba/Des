package com.danmu.comm;

public class Log {
	
	private final static boolean isShowLog = true;
	private static final String LOG_TAG = "danmu";
	
	public static void e(String msg,Exception e){
		if(isShowLog){
			android.util.Log.e(LOG_TAG, msg,e);
		}
	}
	public static void e(String tag,String msg,Exception e){
		if(isShowLog){
			android.util.Log.e(tag, msg,e);
		}
	}
	public static void e(String tag,String msg){
		if(isShowLog){
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
}

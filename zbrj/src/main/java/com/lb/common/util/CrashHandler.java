package com.lb.common.util;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;

public class CrashHandler implements UncaughtExceptionHandler {
	
	private static CrashHandler INSTANCE;
	
	@SuppressWarnings("unused")
	private Context mContext;
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	
	public static CrashHandler getInstance() {  
        if (INSTANCE == null)  
            INSTANCE = new CrashHandler();  
        return INSTANCE;  
    }
	
	public void init(Context ctx) {  
        mContext = ctx;  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }

	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {  
			// 如果用户没有处理则让系统默认的异常处理器来处理  
			mDefaultHandler.uncaughtException(thread, ex);  
		} else {  
			// Sleep一会后结束程序  
			// 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序  
			try {  
				Thread.sleep(3000);  
			} catch (InterruptedException e) {  
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			AppUtil.stopAllServices();
			AppUtil.exitIM();
			MyApplication.getInstance().mActivityManager.popAllActivity();
			AppUtil.closeDB();
			MyApplication.getInstance().mActivityManager.popAllActivity();
			AppUtil.cancelNotis();
			AppUtil.killApp();
		}
	}

	private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return true;  
        } 
        //FIXME 异常
//        MobclickAgent.reportError(mContext, ex.getMessage());
        Log.e(Constants.LOG_TAG, "CrashHandler:"+ ex.getMessage(), ex);
        return true;  
    }
}

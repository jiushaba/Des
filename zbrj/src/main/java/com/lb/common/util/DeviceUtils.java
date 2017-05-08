package com.lb.common.util;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

import org.OpenUDID.OpenUDID_manager;

import com.lz.oncon.application.MyApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

public class DeviceUtils {
	/**
	 * UDID,替代无法获取IMEI的情况
	 * @return
	 */
	public static String getUDID(){
		String UDID = "";
		if(OpenUDID_manager.isInitialized()){
			UDID = OpenUDID_manager.getOpenUDID();
		}else{
			OpenUDID_manager.sync(MyApplication.getInstance());
			if(OpenUDID_manager.isInitialized()){
				UDID = OpenUDID_manager.getOpenUDID();
			}
		}
		return UDID;
	}
	
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	public static void call(Context context, String number) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));
		context.startActivity(intent);
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
	
	public static String getAppVersion(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "";
	}
	
	public static boolean isExternalStorageWriteable(){
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
	        mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        mExternalStorageWriteable = false;
	    } else {
	        mExternalStorageWriteable = false;
	    }
		return mExternalStorageWriteable;
	}
	
	public static String getLocalIpAddress() {  
        try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface  
                    .getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf  
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
                        return inetAddress.getHostAddress().toString();  
                    }  
                }  
            }  
        } catch (SocketException e) {  
            Log.e(Constants.LOG_TAG, e.getMessage(), e);  
        }  
        return null;  
    }  
	//获取手机屏幕宽度
	public static int getScreenWidth(Activity ctx){
		DisplayMetrics dm = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm.widthPixels;
	}
	//获取手机屏幕高度
	public static int getScreenHeight(Activity ctx){
		DisplayMetrics dm = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm.heightPixels;
	}
	//判断屏幕是否点亮
	public static boolean isScreenOn(Context ctx){
		PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}
	
	public static void mkNoMediaFile(Context ctx){
		try{
			String path = "";
			if (DeviceUtils.isExternalStorageWriteable()) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ ctx.getPackageName() + File.separator
						+ ".nomedia";
			} else {
				path = MyApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator
						+ ctx.getPackageName() + File.separator
						+ ".nomedia";
			}
			File f = new File(path);
			File pf = f.getParentFile();
			if(!pf.exists()){
				pf.mkdirs();
			}
			if(f.exists()){
				f.delete();
			}
			if (DeviceUtils.isExternalStorageWriteable()) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ ctx.getPackageName() + File.separator + "oncon" + File.separator
						+ ".nomedia";
			} else {
				path = MyApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator
						+ ctx.getPackageName() + File.separator + "oncon" + File.separator
						+ ".nomedia";
			}
			f = new File(path);
			pf = f.getParentFile();
			if(!pf.exists()){
				pf.mkdirs();
			}
			if(!f.exists()){
				f.createNewFile();
			}
			if (DeviceUtils.isExternalStorageWriteable()) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ ctx.getPackageName() + File.separator + "pic" + File.separator
						+ ".nomedia";
			} else {
				path = MyApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator
						+ ctx.getPackageName() + File.separator + "pic" + File.separator
						+ ".nomedia";
			}
			f = new File(path);
			pf = f.getParentFile();
			if(!pf.exists()){
				pf.mkdirs();
			}
			if(!f.exists()){
				f.createNewFile();
			}
		}catch(Exception e){
		}
	}
	
	public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
	
	public static boolean isLand(Context context){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = manager.getDefaultDisplay().getRotation();
        switch (rotation) {  
        	case Surface.ROTATION_0:
        	case Surface.ROTATION_180:
        		return false;
        	default:
        		return true;
        }
	}
}

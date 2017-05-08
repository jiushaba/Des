package com.lb.common.util;

import java.io.InputStream;
import java.lang.reflect.Field;


import android.content.Context;
import com.lb.common.util.Log;
import com.lz.oncon.application.MyApplication;

public class ResourceUtil {

	@SuppressWarnings("rawtypes")
	private Class localClass;
	public ResourceUtil(Context ctx, String resType) throws Exception{
		try {
			this.localClass = Class.forName(ctx.getPackageName() + ".R$" + resType);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			throw e;
		}
	}
	
	public int getIdx(String key) throws Exception{
		int i = -1;
		try {
			Field localField = localClass.getField(key);
		    i = Integer.parseInt(localField.get(localField.getName()).toString());
		} catch (SecurityException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			throw e;
		} catch (NoSuchFieldException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			throw e;
		} catch (NumberFormatException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			throw e;
		} catch (IllegalArgumentException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			throw e;
		} catch (IllegalAccessException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			throw e;
		}	
	    return i;
	}
	
	public static int getRawIdx(String key){
		int i = -1;
		try {
			ResourceUtil ru = new ResourceUtil(MyApplication.getInstance(), "raw");
			i = ru.getIdx(key);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	    return i;
	}
	
	public static String readRaw(int resId){
		String str = "";
		InputStream is = null;
		try{
			is = MyApplication.getInstance().getResources().openRawResource(resId);
			byte buffer[]=new byte[is.available()];
            is.read(buffer);
            str=new String(buffer);
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}finally{
			try{
				if(is != null)is.close();
			}catch(Exception e){
				Log.e(e.getMessage(), e);
			}
		}
		return str;
	}
}

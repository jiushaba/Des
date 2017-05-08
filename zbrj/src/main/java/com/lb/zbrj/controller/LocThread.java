package com.lb.zbrj.controller;

import com.lb.common.util.BaiduLocation;
import com.lb.common.util.BaiduLocation.BaiduLocationLister;
import com.lb.common.util.Constants;
import com.lb.common.util.DeviceUtils;
import com.lb.common.util.TaskUtil;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;

import android.text.TextUtils;
import com.lb.common.util.Log;
import com.lz.oncon.application.MyApplication;

public class LocThread extends Thread{
	
	public void run(){
		try {
			boolean isScreenOn = DeviceUtils.isScreenOn(MyApplication.getInstance());
			boolean isTopActivity = TaskUtil.isTopActivity(MyApplication.getInstance(), MyApplication.getInstance().getPackageName());
			if(isScreenOn && isTopActivity){
				BaiduLocation.getInstance().startLocationListener(null, mBaiduLocationLister);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	private BaiduLocationLister mBaiduLocationLister = new BaiduLocationLister(){
		@Override
		public void baiduLocFinish(final String latitude, final String longitude, String address, String coorTypr) {
			try{
				if (!TextUtils.isEmpty(latitude)) {
					BaiduLocation.getInstance().stopLocationListener();
					String oldLat = MyApplication.getInstance().mPreferencesMan.getLatitude();
					String oldLong = MyApplication.getInstance().mPreferencesMan.getLongitude();
					if(TextUtils.isEmpty(oldLat) || TextUtils.isEmpty(oldLong)
							|| !oldLat.equals(latitude) || !oldLong.equals(longitude)){
						new Thread(){
							public void run(){
								NetInterfaceStatusDataStruct result = new NetIF_ZBRJ(MyApplication.getInstance())
									.m1_upload_location(Double.parseDouble(latitude), Double.parseDouble(longitude));
								if(Constants.RES_SUCCESS.equals(result.getStatus())){
									MyApplication.getInstance().mPreferencesMan.setLatitude(latitude);
									MyApplication.getInstance().mPreferencesMan.setLongitude(longitude);
								}
							}
						}.start();
					}
				}
			}catch(Exception e){
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
	};
}
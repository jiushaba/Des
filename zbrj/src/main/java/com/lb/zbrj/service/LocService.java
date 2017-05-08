package com.lb.zbrj.service;

import com.lb.common.util.BaiduLocation;
import com.lb.common.util.BaiduLocation.BaiduLocationLister;
import com.lb.common.util.Constants;
import com.lb.common.util.DeviceUtils;
import com.lb.common.util.TaskUtil;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import com.lb.common.util.Log;
import com.lz.oncon.application.MyApplication;

public class LocService extends Service {
	private Thread scanThread;
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
								NetInterfaceStatusDataStruct result = new NetIF_ZBRJ(LocService.this).m1_upload_location(Double.parseDouble(latitude), Double.parseDouble(longitude));
								if(Constants.RES_SUCCESS.equals(result.getStatus())){
									MyApplication.getInstance().mPreferencesMan.setLatitude(latitude);
									MyApplication.getInstance().mPreferencesMan.setLongitude(longitude);
								}
								LocService.this.stopSelf();
							}
						}.start();
					}else{
						LocService.this.stopSelf();
					}
				}else{
					LocService.this.stopSelf();
				}
			}catch(Exception e){
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
				LocService.this.stopSelf();
			}
		}
	};

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	public void onStart(Intent intent, int startId) {
		try {
			boolean isScreenOn = DeviceUtils.isScreenOn(MyApplication.getInstance());
			boolean isTopActivity = TaskUtil.isTopActivity(MyApplication.getInstance(), MyApplication.getInstance().getPackageName());
			if(isScreenOn && isTopActivity){
				scanThread = new Thread(new Runnable() {
					public void run() {
						try {
							BaiduLocation.getInstance().startLocationListener(null, mBaiduLocationLister);
						} catch (Exception e) {
							LocService.this.stopSelf();
						}
					}
				});
				scanThread.start();
			}else{
				LocService.this.stopSelf();
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally {
			if (scanThread != null) {
				try {
					scanThread.interrupt();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (scanThread != null) {
			try {
				scanThread.interrupt();
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
	}
}
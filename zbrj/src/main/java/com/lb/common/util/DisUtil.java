package com.lb.common.util;

import java.text.DecimalFormat;

import com.xuanbo.xuan.R;

import android.text.TextUtils;
import com.lb.common.util.Log;
import com.lz.oncon.application.MyApplication;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class DisUtil {

	public static String distance(String lat, String lng){
		try{
			DecimalFormat df = new DecimalFormat("0");
			String myLat = MyApplication.getInstance().mPreferencesMan.getLatitude();
			String myLng = MyApplication.getInstance().mPreferencesMan.getLongitude();
			if(!TextUtils.isEmpty(lat) && !"0.0".equals(lat.trim())
					&& !TextUtils.isEmpty(lng) && !"0.0".equals(lng.trim())
					&& !TextUtils.isEmpty(myLat) && !"0.0".equals(myLat.trim())
					&& !TextUtils.isEmpty(myLng) && !"0.0".equals(myLng.trim())
					){
				double dis = DistanceUtil.getDistance(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))
				, new LatLng(Double.parseDouble(MyApplication.getInstance().mPreferencesMan.getLatitude())
						, Double.parseDouble(MyApplication.getInstance().mPreferencesMan.getLongitude())));
				if(dis <= 1000){
					return MyApplication.getInstance().getString(R.string.disance_meter, df.format(dis));
				}else{
					return MyApplication.getInstance().getString(R.string.disance_mile, df.format(dis/1000));
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "未知";
	}
	public static int distanceInt(String lat,String lng){
		try{
			DecimalFormat df = new DecimalFormat("0");
			String myLat = MyApplication.getInstance().mPreferencesMan.getLatitude();
			String myLng = MyApplication.getInstance().mPreferencesMan.getLongitude();
			if(!TextUtils.isEmpty(lat) && !"0.0".equals(lat.trim())
					&& !TextUtils.isEmpty(lng) && !"0.0".equals(lng.trim())
					&& !TextUtils.isEmpty(myLat) && !"0.0".equals(myLat.trim())
					&& !TextUtils.isEmpty(myLng) && !"0.0".equals(myLng.trim())
					){
				double dis = DistanceUtil.getDistance(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))
				, new LatLng(Double.parseDouble(MyApplication.getInstance().mPreferencesMan.getLatitude())
						, Double.parseDouble(MyApplication.getInstance().mPreferencesMan.getLongitude())));
				return (Double.valueOf(dis)).intValue();
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return 0;
	}
	public static String number2String(double dis){
		DecimalFormat df = new DecimalFormat("0");
		if(dis <=0 )
			return "未知";
		if(dis <= 1000){
			return MyApplication.getInstance().getString(R.string.disance_meter, df.format(dis));
		}else{
			return MyApplication.getInstance().getString(R.string.disance_mile, df.format(dis/1000));
		}
	}
}

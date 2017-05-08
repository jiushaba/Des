package com.lb.common.util;

import com.lb.common.util.Log;
import com.lz.oncon.application.MyApplication;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class BaiduLocation implements BDLocationListener{

	private LocationClient mLocBaiduClient;
	private static BaiduLocation baiduLocation;
	private String latitude = "";
	private String longitude = "";
	private String address = "";
	private String coorTypr = "";
	public String getCoorTypr() {
		return coorTypr;
	}
	public void setCoorTypr(String coorTypr) {
		this.coorTypr = coorTypr;
	}
	public static BaiduLocation getInstance(){
		if(baiduLocation==null){
			baiduLocation = new BaiduLocation();
		}
		return baiduLocation;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BaiduLocation() {
		mLocBaiduClient = MyApplication.getInstance().mLocationClient;
	}
	
	/**
	 * 启动定位 
	 * @param coorTypr 坐标系
	 * 定位SDK可以返回bd09、bd09ll、gcj02三种类型坐标
	 */
	public void startLocationListener(String coorTypr,BaiduLocationLister mBaiduLocationListener) {
		this.mBaiduLocationListener = mBaiduLocationListener;
		if(coorTypr==null){
			coorTypr = "bd09ll";
		}
		this.coorTypr = coorTypr;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		option.setOpenGps(true);//设置打开GPS
		mLocBaiduClient.setLocOption(option);
		mLocBaiduClient.registerLocationListener(this);
		mLocBaiduClient.start();
	}
	
	public void stopLocationListener() {
		if (mLocBaiduClient != null) {
			mLocBaiduClient.unRegisterLocationListener(this);
			if (mLocBaiduClient.isStarted()) {
				mLocBaiduClient.stop();
			}
		}
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		latitude = "";
		longitude = "";
		address = "";
		if (location == null) {
			Log.d(Constants.LOG_TAG, "定位失败原因location = null");
		} else if (location.getLocType() == BDLocation.TypeGpsLocation
				|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
			latitude = location.getLatitude() + "";
			longitude = location.getLongitude() + "";
			address =location.getAddrStr();
		} else {
			Log.d(Constants.LOG_TAG, "定位失败原因=" + location.getLocType());
		}
		if(mBaiduLocationListener!=null){
			mBaiduLocationListener.baiduLocFinish(latitude, longitude, address,coorTypr);
		}
		stopLocationListener();

	}
	
	private BaiduLocationLister mBaiduLocationListener;
	public interface BaiduLocationLister{
		public void baiduLocFinish(String latitude,String longitude,String address,String coorTypr);
	}
	
}

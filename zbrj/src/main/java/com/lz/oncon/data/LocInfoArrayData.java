package com.lz.oncon.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lb.common.util.Constants;

import android.content.Context;
import com.lb.common.util.Log;

public class LocInfoArrayData {
	private static LocInfoArrayData instance;
	
	private Context mContext;
	private String locInfo;
	
	public synchronized static LocInfoArrayData getArrayData(Context mContext) {
		if (instance==null) {
			instance=new LocInfoArrayData(mContext);
		}
		return instance;
	}
	
	private LocInfoArrayData(Context mContext) {
		this.mContext=mContext;
		initData();
		System.gc();
	}
	
	public ArrayList<LocInfoData> getProvince(){
		ArrayList<LocInfoData> provinces=new ArrayList<LocInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("province")){
				JSONArray provinceArray = object.getJSONArray("province");
				int count=provinceArray.length();
				for (int i = 0; i <count ; i++) {
					JSONObject item=provinceArray.getJSONObject(i);
					LocInfoData province=new LocInfoData(item.getString("NAME"),item.getString("ID"),LocInfoData.TYPE_PROVINCE);
					provinces.add(province);
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return provinces;
	}
	
	public ArrayList<LocInfoData> getCity(String provinceID){
		ArrayList<LocInfoData> cities=new ArrayList<LocInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("city")){
				JSONArray cityArray=object.getJSONArray("city");
				for (int i = 0; i < cityArray.length(); i++) {
					JSONObject item=cityArray.getJSONObject(i);
					if (item.getString("ID").startsWith(provinceID)) {
						LocInfoData city=new LocInfoData(item.getString("NAME"),item.getString("ID"),LocInfoData.TYPE_CITY);
						cities.add(city);
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return cities;
	}
	
	public ArrayList<LocInfoData> getDistrict(String cityID){
		ArrayList<LocInfoData> districts=new ArrayList<LocInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("district")){
				JSONArray districtArray=object.getJSONArray("district");
				for (int i = 0; i < districtArray.length(); i++) {
					JSONObject item=districtArray.getJSONObject(i);
					if (item.getString("ID").startsWith(cityID)) {
						LocInfoData district=new LocInfoData(item.getString("NAME"),item.getString("ID"),LocInfoData.TYPE_DISTRICT);
						districts.add(district);
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return districts;
	}
	
	private void initData() {
				try {
					InputStream is=mContext.getAssets().open("addressInfoList.txt");
					byte[] data=new byte[is.available()];
					is.read(data);
					locInfo=new String(data,Charset.forName("utf8"));
					is.close();
					data=null;
				} catch (IOException e) {
					e.printStackTrace();
				} 
	}
}

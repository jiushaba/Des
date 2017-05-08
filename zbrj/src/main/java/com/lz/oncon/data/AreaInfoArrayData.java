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

public class AreaInfoArrayData {
	private static AreaInfoArrayData instance;
	
	private Context mContext;
	private String locInfo;
	
	public synchronized static AreaInfoArrayData getArrayData(Context mContext) {
		if (instance==null) {
			instance=new AreaInfoArrayData(mContext);
		}
		return instance;
	}
	
	private AreaInfoArrayData(Context mContext) {
		this.mContext=mContext;
		initData();
		System.gc();
	}
	/**
	 * 获取国家列表
	 * @return
	 */
	public ArrayList<AreaInfoData> getCountry(){
		ArrayList<AreaInfoData> countrys=new ArrayList<AreaInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("country")){
				JSONArray countryArray = object.getJSONArray("country");
				int count=countryArray.length();
				for (int i = 0; i <count ; i++) {
					JSONObject item=countryArray.getJSONObject(i);
					AreaInfoData areaInfoData = new AreaInfoData(item.getString("name_zh_cn"), item.getString("name_en"), item.getString("areacode"), item.getString("level"), item.getString("parent_code"), item.getString("zipcode"));
					countrys.add(areaInfoData);
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return countrys;
	}
	/**
	 * 根据国家ID获取省份列表
	 * @param countryID 国家ID
	 * @return
	 */
	public ArrayList<AreaInfoData> getProvince(String countryID){
		ArrayList<AreaInfoData> provinces=new ArrayList<AreaInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("province")){
				JSONArray provinceArray = object.getJSONArray("province");
				int count=provinceArray.length();
				for (int i = 0; i <count ; i++) {
					JSONObject item=provinceArray.getJSONObject(i);
					if (countryID.equals(item.getString("parent_code"))) {
						AreaInfoData province = new AreaInfoData(item.getString("name_zh_cn"), item.getString("name_en"), item.getString("areacode"), item.getString("level"), item.getString("parent_code"), item.getString("zipcode"));
						provinces.add(province);
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return provinces;
	}
	
	/**
	 * 根据省份ID获取城市列表
	 * @param provinceID 省份ID
	 * @return
	 */
	public ArrayList<AreaInfoData> getCity(String provinceID){
		ArrayList<AreaInfoData> cities=new ArrayList<AreaInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("city")){
				JSONArray cityArray=object.getJSONArray("city");
				for (int i = 0; i < cityArray.length(); i++) {
					JSONObject item=cityArray.getJSONObject(i);
					if (provinceID.equals(item.getString("parent_code"))) {
						AreaInfoData city = new AreaInfoData(item.getString("name_zh_cn"), item.getString("name_en"), item.getString("areacode"), item.getString("level"), item.getString("parent_code"), item.getString("zipcode"));
						cities.add(city);
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return cities;
	}
	
	/**
	 * 根据城市ID获取辖区
	 * @param cityID 城市ID
	 * @return
	 */
	public ArrayList<AreaInfoData> getArea(String cityID){
		ArrayList<AreaInfoData> areas=new ArrayList<AreaInfoData>();
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("area")){
				JSONArray areaArray=object.getJSONArray("area");
				for (int i = 0; i < areaArray.length(); i++) {
					JSONObject item = areaArray.getJSONObject(i);
					if (cityID.equals(item.getString("parent_code"))) {
						AreaInfoData area = new AreaInfoData(item.getString("name_zh_cn"), item.getString("name_en"), item.getString("areacode"), item.getString("level"), item.getString("parent_code"), item.getString("zipcode"));
						areas.add(area);
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return areas;
	}
	
	/**
	 * 根据省份ID反向找到对应的国家,此方法待后续数据完善后需要修改..  暂时使用
	 * @param provinceID 省份ID
	 * @return
	 */
	public String getCountryName(String provinceID) {
		String countryName = "";
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("province")){
				JSONArray provinceArray = object.getJSONArray("province");
				int count=provinceArray.length();
				for (int i = 0; i <count ; i++) {
					JSONObject item=provinceArray.getJSONObject(i);
					if (provinceID.equals(item.getString("areacode"))) {
						String tempCode = item.getString("parent_code");
						if(object.has("country")){
							JSONArray countryArray = object.getJSONArray("country");
							int countryCount=countryArray.length();
							for (int j = 0; j < countryCount ; j++) {
								JSONObject countryItem = countryArray.getJSONObject(j);
								if (tempCode.equals(countryItem.getString("areacode"))) {
									countryName = countryItem.getString("name_en");
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return countryName;
	}
	
	/**
	 * 根据省份ID获取省份名称
	 * @param provinceID 省份ID
	 * @return 
	 */
	public String getProvinceName(String provinceID) {
		String provinceName = "";
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("province")){
				JSONArray provinceArray = object.getJSONArray("province");
				int count=provinceArray.length();
				for (int i = 0; i <count ; i++) {
					JSONObject item=provinceArray.getJSONObject(i);
					if (provinceID.equals(item.getString("areacode"))) {
						provinceName = item.getString("name_en");
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return provinceName;
	}
	
	
	/**
	 * 根据城市ID获取城市名称
	 * @param cityID 城市ID
	 * @return
	 */
	public String getCityName(String cityID) {
		String cityName = "";
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("city")){
				JSONArray cityArray=object.getJSONArray("city");
				for (int i = 0; i < cityArray.length(); i++) {
					JSONObject item=cityArray.getJSONObject(i);
					if (cityID.equals(item.getString("areacode"))) {
						cityName = item.getString("name_en");
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return cityName;
	}
	
	/**
	 * 根据辖区ID获取辖区名称
	 * @param areaID 辖区ID
	 * @return
	 */
	public String getAreaName(String areaID) {
		String areaName = "";
		try{
			JSONObject object=new JSONObject(locInfo);
			if(object.has("area")){
				JSONArray areaArray=object.getJSONArray("area");
				for (int i = 0; i < areaArray.length(); i++) {
					JSONObject item = areaArray.getJSONObject(i);
					if (areaID.equals(item.getString("areacode"))) {
						areaName = item.getString("name_en");
					}
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return areaName;
	}
	
	private void initData() {
				try {
					InputStream is=mContext.getAssets().open("locaddress.txt");
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

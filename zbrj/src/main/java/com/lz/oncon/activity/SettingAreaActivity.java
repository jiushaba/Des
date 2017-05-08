package com.lz.oncon.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xuanbo.xuan.R;
import com.lz.oncon.adapter.SettingAreaAdapter;
import com.lz.oncon.data.AreaInfoArrayData;
import com.lz.oncon.data.AreaInfoData;

public class SettingAreaActivity extends BaseActivity {

	private ListView areaList;
	private ArrayList<AreaInfoData> country;
	private SettingAreaAdapter areaAdapter;
	private String areaInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initView();
		setValue();
		setListener();
	}

	public void initContentView() {
		this.setContentView(R.layout.activity_setting_area);
	}

	private void initView() {
		areaList = (ListView) findViewById(R.id.list);
	}

	private void setValue() {
		country = AreaInfoArrayData.getArrayData(this).getCountry();
		areaAdapter = new SettingAreaAdapter(this, country);
		areaList.setAdapter(areaAdapter);
	}

	private void setListener() {
		areaList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					ArrayList<AreaInfoData> subAreaInfoDatas = null;
					AreaInfoData areaInfoData = (AreaInfoData) areaAdapter.getItem(position);
					String levelType = areaInfoData.level;
					String areacode = areaInfoData.areacode;
					if (AreaInfoData.TYPE_COUNTRY.equals(levelType)) {
						setAreaInfo(areaInfoData.name_zh_cn);
						subAreaInfoDatas = AreaInfoArrayData.getArrayData(SettingAreaActivity.this).getProvince(areacode);
						if (subAreaInfoDatas != null && subAreaInfoDatas.size() > 0) {
							areaAdapter.setAreaList(subAreaInfoDatas);
						}else {
							onSelect(areacode);
						}
					} else if (AreaInfoData.TYPE_PROVINCE.equals(levelType)) {
						setAreaInfo(areaInfoData.name_zh_cn);
						subAreaInfoDatas = AreaInfoArrayData.getArrayData(SettingAreaActivity.this).getCity(areacode);
						if (subAreaInfoDatas != null && subAreaInfoDatas.size() > 0) {
							areaAdapter.setAreaList(subAreaInfoDatas);
						}else {
							onSelect(areacode);
						}
					} else if (AreaInfoData.TYPE_CITY.equals(levelType)) {
						setAreaInfo(areaInfoData.name_zh_cn);
						subAreaInfoDatas = AreaInfoArrayData.getArrayData(SettingAreaActivity.this).getArea(areacode);
						if (subAreaInfoDatas != null && subAreaInfoDatas.size() > 0) {
							areaAdapter.setAreaList(subAreaInfoDatas);
						}else {
							onSelect(areacode);
						}
					} else if (AreaInfoData.TYPE_AREA.equals(levelType)) {
						setAreaInfo(areaInfoData.name_zh_cn);
						subAreaInfoDatas = AreaInfoArrayData.getArrayData(SettingAreaActivity.this).getArea(areacode);
						if (subAreaInfoDatas != null) {
							areaAdapter.setAreaList(subAreaInfoDatas);
						}
						onSelect(areacode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setAreaInfo(String areaInfo) {
		if (TextUtils.isEmpty(this.areaInfo)) {
			this.areaInfo = areaInfo;
		} else {
			this.areaInfo += " " + areaInfo;
		}

	}
	
	private void onSelect(String areaCode) {
		Intent intent = new Intent();
		intent.putExtra("areaInfo", areaInfo);
		intent.putExtra("areaCode", areaCode);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}

}

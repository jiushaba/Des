package com.lz.oncon.activity;

import java.util.ArrayList;

import com.xuanbo.xuan.R;
import com.lz.oncon.adapter.LocInfoAdapter;
import com.lz.oncon.data.LocInfoArrayData;
import com.lz.oncon.data.LocInfoData;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LocOfflineActivity extends BaseActivity implements OnItemClickListener{
	private ArrayList<LocInfoData> provinces;
	private ListView mListView;
	private LocInfoAdapter mAdapter;
	
	private String locInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loc_offline);
		initView();		
		initData();
	}
	
	private void initView() {
		mListView=(ListView) findViewById(R.id.loc_offline_list);
	}

	private void initData() {
		provinces=LocInfoArrayData.getArrayData(this).getProvince();
		mAdapter=new LocInfoAdapter(this, provinces);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	public void setLocInfo(String locInfo) {
		if (TextUtils.isEmpty(this.locInfo)) {
			this.locInfo = locInfo;
		}else {
			this.locInfo+="-"+locInfo;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			ArrayList<LocInfoData> subLocInfo=null;
			LocInfoData locCurrent=(LocInfoData) mAdapter.getItem(position);
			String locType=locCurrent.getType();
			if (LocInfoData.TYPE_PROVINCE.equals(locType)) {
				subLocInfo=LocInfoArrayData.getArrayData(this).getCity(locCurrent.getID());
				if (subLocInfo!=null) {
					mAdapter.update(subLocInfo);
				}
				setLocInfo(locCurrent.getLocName());
			}else if (LocInfoData.TYPE_CITY.equals(locType)) {
//				subLocInfo=LocInfoArrayData.getArrayData(this).getDistrict(locCurrent.getID());
//				if (subLocInfo!=null) {
//					mAdapter.update(subLocInfo);
//				}
//				setLocInfo(locCurrent.getLocName());
//			}else if (LocInfoData.TYPE_DISTRICT.equals(locType)) {
				setLocInfo(locCurrent.getLocName());
				onSelect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onSelect() {
		Intent intent=new Intent();
		intent.putExtra("locInfo", locInfo);
		setResult(10087, intent);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}
}

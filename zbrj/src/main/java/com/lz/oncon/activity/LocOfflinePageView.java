package com.lz.oncon.activity;

import java.util.ArrayList;

import com.xuanbo.xuan.R;
import com.lz.oncon.adapter.LocInfoAdapter;
import com.lz.oncon.data.LocInfoArrayData;
import com.lz.oncon.data.LocInfoData;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class LocOfflinePageView extends LinearLayout implements OnItemClickListener{
	private LocOfflineActivity mContext;
	private ArrayList<LocInfoData> locInfo;
	private ListView mListView;
	private LocInfoAdapter mAdapter;
	
	public LocOfflinePageView(Context context) {
		super(context);
		this.mContext=(LocOfflineActivity) context;
		init();
	}
	
	public LocOfflinePageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=(LocOfflineActivity) context;
		init();
	}
	
	private void init() {
		LayoutInflater.from(mContext).inflate(R.layout.loc_offline_page, this);
		mListView=(ListView) findViewById(R.id.locInfo_addrs_list);
	}
	
	public void setLocInfo(ArrayList<LocInfoData> locInfo) {
		this.locInfo=locInfo;
		initAdapter();
	}
	
	private void initAdapter() {
		mAdapter=new LocInfoAdapter(mContext, locInfo);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			ArrayList<LocInfoData> subLocInfo=null;
			LocInfoData locCurrent=(LocInfoData) mAdapter.getItem(position);
			String locType=locCurrent.getType();
			if (LocInfoData.TYPE_PROVINCE.equals(locType)) {
				subLocInfo=LocInfoArrayData.getArrayData(mContext).getCity(locCurrent.getID());
				if (subLocInfo!=null) {
					mAdapter.update(subLocInfo);
				}
				mContext.setLocInfo(locCurrent.getLocName());
			}else if (LocInfoData.TYPE_CITY.equals(locType)) {
				subLocInfo=LocInfoArrayData.getArrayData(mContext).getDistrict(locCurrent.getID());
				if (subLocInfo!=null) {
					mAdapter.update(subLocInfo);
				}
				mContext.setLocInfo(locCurrent.getLocName());
			}else if (LocInfoData.TYPE_DISTRICT.equals(locType)) {
				mContext.setLocInfo(locCurrent.getLocName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

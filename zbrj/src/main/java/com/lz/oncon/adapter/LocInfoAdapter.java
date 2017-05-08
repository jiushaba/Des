package com.lz.oncon.adapter;

import java.util.ArrayList;

import com.xuanbo.xuan.R;

import android.content.Context;
import com.lb.common.util.Log;
import com.lz.oncon.data.LocInfoData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocInfoAdapter extends BaseAdapter{
	private ArrayList<LocInfoData> data;
	private Context mContext;
	
	public LocInfoAdapter(Context mContext,ArrayList<LocInfoData> data) {
		this.data=data;
		this.mContext=mContext;
	}
	
	public void update(ArrayList<LocInfoData> data) {
		this.data=data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		Log.d(LocInfoAdapter.class.getName(), "count "+data.size());
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView mView = null;
		LocInfoData locInfo=data.get(position);
		convertView=LayoutInflater.from(mContext).inflate(R.layout.loc_offline_page_listitem, null);
		mView=(TextView) convertView.findViewById(R.id.loc_offline_page_item_loc);
		mView.setText(locInfo.getLocName());
		return convertView;
	}

}

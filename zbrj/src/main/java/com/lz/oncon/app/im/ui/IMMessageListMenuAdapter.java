package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.data.MenuData;

public class IMMessageListMenuAdapter extends BaseAdapter {

	private ArrayList<MenuData> mDatas;
	private LayoutInflater mInflater;

	public IMMessageListMenuAdapter(Context context, ArrayList<MenuData> mDatas) {
		this.mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas == null ? null : mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.app_im_message_menu_item, null);
			holder.moreIV = (ImageView) convertView.findViewById(R.id.menu_more);
			holder.nameTV = (TextView) convertView.findViewById(R.id.menu_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MenuData data = mDatas.get(position);
		holder.nameTV.setText(data.name);
		if(data.sonMenus != null && data.sonMenus.size() > 0){
			holder.moreIV.setVisibility(View.VISIBLE);
		}else{
			holder.moreIV.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView moreIV;
		TextView nameTV;
	}
}
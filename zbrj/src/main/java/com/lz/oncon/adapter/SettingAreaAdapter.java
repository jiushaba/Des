package com.lz.oncon.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.data.AreaInfoData;

public class SettingAreaAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<AreaInfoData> areaList;
	
	public SettingAreaAdapter(Context context, ArrayList<AreaInfoData> list) {
		mContext = context;
		areaList = list;
	}
	
	public void setAreaList(ArrayList<AreaInfoData> areaList) {
		this.areaList = areaList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return areaList.size();
	}

	@Override
	public Object getItem(int position) {
		return areaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView==null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView=inflater.inflate(R.layout.area_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.address_tv);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(areaList.get(position).name_zh_cn);
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView name;
	}

}

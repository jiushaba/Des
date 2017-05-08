package com.lz.oncon.widget;

import java.util.List;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareGridAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> name;
	private List<Integer> iconarray;

	public ShareGridAdapter(Context context, List<String> name, List<Integer> iconarray) {
		this.inflater = LayoutInflater.from(context);
		this.name = name;
		this.iconarray = iconarray;
	}

	@Override
	public int getCount() {
		return name.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
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
			convertView = this.inflater.inflate(R.layout.umeng_socialize_shareboard_item, null);
			holder.iv = (ImageView) convertView
					.findViewById(R.id.umeng_socialize_shareboard_image);
			holder.tv = (TextView) convertView
					.findViewById(R.id.umeng_socialize_shareboard_pltform_name);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv.setImageResource(iconarray.get(position));
		holder.tv.setText(name.get(position));
		//holder.name = name.get(position);
		return convertView;
	}

	private class ViewHolder {
		ImageView iv;
		TextView tv;
		//String name;
	}

}

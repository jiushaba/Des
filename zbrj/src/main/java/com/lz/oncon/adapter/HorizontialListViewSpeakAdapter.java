package com.lz.oncon.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.data.GifFaceData;
import com.lz.oncon.data.db.FaceHelper;

public class HorizontialListViewSpeakAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<GifFaceData> mDatas;
	private int tabHost;
	private String class_name = "";

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	FaceHelper cHelper = null;

	public int getTabHost() {
		return tabHost;
	}

	public void setTabHost(int tabHost) {
		this.tabHost = tabHost;
	}


	public HorizontialListViewSpeakAdapter(Context c, ArrayList<GifFaceData> datas) {
		mContext = c;
		mDatas = datas;
	}

	@Override
	public int getCount() {
		if (mDatas == null) {
			return 0;
		} else {
			return mDatas.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mDatas != null && position >= 0 && position < mDatas.size()) {
			return mDatas.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.message_horizontial_speak_listview_item, null);
			holder.face_head = (ImageView) convertView
					.findViewById(R.id.message_class_speak);
			holder.tv = (TextView) convertView.findViewById(R.id.image_speak_des);
			convertView.setTag(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		GifFaceData facedata = mDatas.get(position);
			holder.face_head.setImageResource(facedata.getImage_ResourceID());
			holder.tv.setText(facedata.getText_ResourceID());
		return convertView;
	}

	class ViewHolder {
		private ImageView face_head;
		private TextView tv;
	}
}

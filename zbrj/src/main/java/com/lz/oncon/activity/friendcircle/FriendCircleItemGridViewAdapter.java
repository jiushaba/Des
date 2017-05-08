package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;

public class FriendCircleItemGridViewAdapter extends BaseAdapter{
	public Context mContext;
	public ArrayList<String> mList;
	
	public FriendCircleItemGridViewAdapter(Context c, ArrayList<String> list){
		this.mContext = c;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList==null?null:mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if(convertView==null){
			LayoutInflater in = LayoutInflater.from(mContext);
			convertView = in.inflate(R.layout.friendcircle_item_gridview_item, null);
			vh = new ViewHolder();
			vh.iv = (ImageView) convertView.findViewById(R.id.friendcircle_item_gridview_item_iv);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		String photos = mList.get(position);
		ImageLoader.displayPicImage(photos, vh.iv);
		return convertView;
	}
	
	static class ViewHolder{
		ImageView iv;
	}
	
}

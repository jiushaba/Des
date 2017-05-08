package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xuanbo.xuan.R;
import com.lb.zbrj.data.LikeData;
public class FcFeedLikeItemGridViewAdapter extends BaseAdapter {
	public Context mContext;
	public ArrayList<LikeData> mList;

	// private AsyncImageLoader ail;

	public FcFeedLikeItemGridViewAdapter(Context c, ArrayList<LikeData> list) {
		this.mContext = c;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			LayoutInflater in = LayoutInflater.from(mContext);
			convertView = in.inflate(R.layout.fc_feedlink_item_gridview_item, null);
			vh = new ViewHolder();
			vh.iv = (com.lz.oncon.widget.HeadImageView) convertView.findViewById(R.id.fc_feedlink_item_gridview_item_iv);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		LikeData flub = mList.get(position);
		if (mList != null && flub != null){
			vh.iv.setMobile(flub.likeAccount);
		}
		return convertView;
	}

	static class ViewHolder {
		com.lz.oncon.widget.HeadImageView iv;
	}

}

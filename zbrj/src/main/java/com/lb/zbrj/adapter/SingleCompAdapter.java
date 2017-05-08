package com.lb.zbrj.adapter;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.data.CompData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.view.CompVideoView2;

public class SingleCompAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mVideoList;
	private LayoutParams lllp, vlp;
	private int padding;
	private CompData comp;

	public SingleCompAdapter(Context context, List<Object> videoList) {
		this.mContext = context;
		mVideoList = videoList;
		lllp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		vlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		vlp.weight = 1;
		vlp.gravity = Gravity.CENTER_HORIZONTAL;
		padding = mContext.getResources().getDimensionPixelSize(R.dimen.comp_video_item_padding);
	}
	
	public void setComp(CompData comp){
		this.comp = comp;
	}

	public List<Object> getVideoList() {
		return mVideoList;
	}

	public void setVideoList(List<Object> videoList) {
		this.mVideoList = videoList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_single_comp_item, null);
			holder = new ViewHolder();
			holder.compName = (TextView) convertView.findViewById(R.id.compName);
			holder.video = (LinearLayout) convertView.findViewById(R.id.videoLL);
			holder.compNameLL = (LinearLayout) convertView.findViewById(R.id.compNameLL);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position == 0){
			holder.compNameLL.setVisibility(View.VISIBLE);
			holder.compName.setText(mContext.getString(R.string.comp_name_fmt, comp.compName));
		}else{
			holder.compNameLL.setVisibility(View.GONE);
		}
		
		holder.video.removeAllViews();
		LinearLayout ll = new LinearLayout(mContext);
		ll.setPadding(padding, padding, padding, padding);
		holder.video.addView(ll, lllp);
		CompVideoView2 v = new CompVideoView2(mContext);
		v.setData((VideoData)mVideoList.get(position * 2));
		ll.addView(v, vlp);
		if(position * 2 + 1 < mVideoList.size()){
			v = new CompVideoView2(mContext);
			v.setData((VideoData)mVideoList.get(position * 2 + 1));
			ll.addView(v, vlp);
		}else{
			ll.addView(new View(mContext), vlp);
		}
		
		return convertView;
	}

	public void addMoreData(List<VideoData> videoList) {
		if (null == videoList || videoList.size() == 0) {
			return;
		}
		videoList.remove(0);
		this.mVideoList.addAll(videoList);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null == mVideoList) {
			return 0;
		}
		return mVideoList.size() / 2 + (mVideoList.size() % 2 == 0 ? 0 : 1);
	}

	@Override
	public Object getItem(int position) {
		return mVideoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	static class ViewHolder {
		TextView compName;
		LinearLayout video, compNameLL;
	}
}

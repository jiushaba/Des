package com.lb.zbrj.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.activity.SingleCompActivity;
import com.lb.zbrj.data.CompData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.view.CompVideoView;

public class CompListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mVideoList;
	private LayoutParams lllp, vlp;
	private int padding;

	public CompListAdapter(Context context, List<Object> videoList) {
		this.mContext = context;
		mVideoList = videoList;
		lllp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		vlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		vlp.weight = 1;
		vlp.gravity = Gravity.CENTER_HORIZONTAL;
		padding = mContext.getResources().getDimensionPixelSize(R.dimen.comp_video_item_padding);
	}

	public List<Object> getVideoList() {
		return mVideoList;
	}

	public void setVideoList(List<Object> videoList) {
		this.mVideoList = videoList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CompData compData = (CompData)mVideoList.get(position);

		ViewHolder holder;
		View line = LayoutInflater.from(mContext).inflate(R.layout.line, null);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_watch_list_comp_item, null);
			holder = new ViewHolder();
			holder.compName = (TextView) convertView.findViewById(R.id.compName);
			holder.compType = (TextView) convertView.findViewById(R.id.compType);
			holder.compNum = (TextView) convertView.findViewById(R.id.compNum);
			holder.compOp = (TextView) convertView.findViewById(R.id.compOP);
			holder.video = (LinearLayout) convertView.findViewById(R.id.videoLL);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.compName.setText(mContext.getString(R.string.comp_name_fmt, compData.compName));
		holder.compName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SingleCompActivity.class);
				intent.putExtra("comp", compData);
				mContext.startActivity(intent);
			}
		});
		holder.compType.setText(compData.compType);
		holder.compNum.setText(mContext.getString(R.string.comp_num_fmt, compData.compNum));
		holder.compOp.setText(mContext.getString(R.string.release));
		holder.compOp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// FIXME 解除/置顶操作
				
			}
		});
		holder.video.removeAllViews();
		int videolines = compData.videos.size() / 2 + (compData.videos.size() % 2 == 0 ? 0 : 1);
		for(int i=0;i<videolines;i++){
			if(i != 0){
				holder.video.addView(line, lllp);
			}
			LinearLayout ll = new LinearLayout(mContext);
			ll.setPadding(padding, padding, padding, padding);
			holder.video.addView(ll, lllp);
			CompVideoView v = new CompVideoView(mContext);
			v.setData(compData.videos.get(i * 2));
			ll.addView(v, vlp);
			if(i * 2 + 1 < compData.videos.size()){
				v = new CompVideoView(mContext);
				v.setData(compData.videos.get(i * 2 + 1));
				ll.addView(v, vlp);
			}else{
				ll.addView(new View(mContext), vlp);
			}
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
		return mVideoList.size();
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
		TextView compName, compType, compNum, compOp;
		LinearLayout video;
	}
}

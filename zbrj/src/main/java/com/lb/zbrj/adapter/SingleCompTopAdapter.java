package com.lb.zbrj.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.data.CompData;
import com.lb.zbrj.data.VideoData;

public class SingleCompTopAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mVideoList;
	private CompData comp;

	public SingleCompTopAdapter(Context context, List<Object> videoList) {
		this.mContext = context;
		mVideoList = videoList;
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
		VideoData data = (VideoData)mVideoList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_single_comp_top_item, null);
			holder = new ViewHolder();
			holder.compName = (TextView) convertView.findViewById(R.id.compName);
			holder.titleV = (TextView) convertView.findViewById(R.id.title);
			holder.watchersNumV = (TextView) convertView.findViewById(R.id.watchersNum);
			holder.upNumV = (TextView) convertView.findViewById(R.id.upNum);
			holder.bulletsNumV = (TextView) convertView.findViewById(R.id.bulletsNum);
			holder.seqV = (TextView) convertView.findViewById(R.id.seq);
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
		holder.titleV.setText(data.title);
		holder.watchersNumV.setText(mContext.getResources().getString(R.string.watchersnum_fmt, data.watchersNum));
		holder.upNumV.setText(mContext.getResources().getString(R.string.upnum_fmt, data.upNum));
		holder.bulletsNumV.setText(mContext.getResources().getString(R.string.bulletsnum_fmt, data.bulletsNum));
		holder.seqV.setText((position+1) + "");
		
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
		TextView compName, titleV, watchersNumV, upNumV, bulletsNumV, seqV;
		ImageView videoImageV;
		LinearLayout compNameLL;
	}
}

package com.lb.zbrj.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.ImageLoader;
import com.lb.video.activity.VideoPlayerActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.widget.HeadImageView;

public class SearchListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mList;
	private LayoutInflater layoutInflater;
	private PersonController mPersonController;

	public SearchListAdapter(Context context, List<Object> list) {
		this.mContext = context;
		mList = list;
		layoutInflater = LayoutInflater.from(context);
		mPersonController = new PersonController();
	}

	public List<Object> getList() {
		return mList;
	}

	public void setList(List<Object> list) {
		this.mList = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(mList.get(position) instanceof VideoData){
			convertView = layoutInflater.inflate(R.layout.activity_watch_search_list_video_item, null);
			holder = new ViewHolder();
			holder.indexLayout = (RelativeLayout) convertView.findViewById(R.id.indexLayout);
			holder.view = (ImageView) convertView.findViewById(R.id.view);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
			setVideo((VideoData)mList.get(position), holder);
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					VideoPlayerActivity.start(mContext, (VideoData)mList.get(position));
				}
			});
			if(position == 0){
				holder.indexLayout.setVisibility(View.VISIBLE);
			}else if(mList.get(position-1) instanceof VideoData){
				holder.indexLayout.setVisibility(View.GONE);
			}else{
				holder.indexLayout.setVisibility(View.VISIBLE);
			}
		}else if(mList.get(position) instanceof PersonData){
			convertView = layoutInflater.inflate(R.layout.activity_watch_search_list_person_item, null);
			holder = new ViewHolder();
			holder.indexLayout = (RelativeLayout) convertView.findViewById(R.id.indexLayout);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.name);
			holder.iconView = (HeadImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
			setPerson((PersonData)mList.get(position), holder);
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					PersonController.go2Detail(mContext, ((PersonData)mList.get(position)).account);
				}
			});
			if(position == 0){
				holder.indexLayout.setVisibility(View.VISIBLE);
			}else if(mList.get(position-1) instanceof PersonData){
				holder.indexLayout.setVisibility(View.GONE);
			}else{
				holder.indexLayout.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	public void addMoreData(List<Object> videoList) {
		if (null == videoList || videoList.size() == 0) {
			return;
		}
		videoList.remove(0);
		this.mList.addAll(videoList);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null == mList) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	private void setVideo(final VideoData videoData, ViewHolder holder){
		ImageLoader.displayPicImage(videoData.videoImage, holder.view);
		if (!TextUtils.isEmpty(videoData.nick)) {
			holder.name.setText(videoData.nick);
		}
		holder.title.setText(videoData.title);
	}
	
	private void setPerson(final PersonData data, final ViewHolder holder){
		PersonData person = mPersonController.findPerson(data.account);
		if(TextUtils.isEmpty(person.nickname)){
			if(TextUtils.isEmpty(data.nickname)){
				holder.nameTextView.setText(data.account);
			}else{
				holder.nameTextView.setText(data.nickname);
			}
		}else{
			holder.nameTextView.setText(person.nickname);
		}
		if(TextUtils.isEmpty(person.image)){
			ImageLoader.displayHeadImage(data.image, holder.iconView);
		}else{
			ImageLoader.displayHeadImage(person.image, holder.iconView);
		}
	}

	static class ViewHolder {
		RelativeLayout indexLayout;
		
		ImageView view;
		TextView name, title;
		
		TextView nameTextView;
		HeadImageView iconView;
	}
}

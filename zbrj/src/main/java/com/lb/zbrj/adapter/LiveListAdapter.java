package com.lb.zbrj.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.common.util.DateUtil;
import com.lb.common.util.DisUtil;
import com.lb.common.util.ImageLoader;
import com.lb.video.activity.VideoPlayerActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.widget.HeadImageView;

public class LiveListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mVideoList;
	private LayoutInflater layoutInflater;

	public LiveListAdapter(Context context, List<Object> videoList) {
		this.mContext = context;
		mVideoList = videoList;
		layoutInflater = LayoutInflater.from(context);
	}

	public List<Object> getVideoList() {
		return mVideoList;
	}

	public void setVideoList(List<Object> videoList) {
		this.mVideoList = videoList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final VideoData videoData = (VideoData)mVideoList.get(position);
		/*//FIX1ME 测试热键赛
		int i = position%3;
		if(i== 0)
			videoData.isComp=1;
		else if(i== 1)
			videoData.recommandType=1;
		else 
			videoData.recommandType = 2;*/
		
		ViewHolder holder;
//		if (convertView == null) {
			if(1 == videoData.isBigVideo){
				convertView = layoutInflater.inflate(R.layout.activity_watch_list_bigitem, null);
			}else{
				convertView = layoutInflater.inflate(R.layout.activity_watch_list_item, null);
			}
			holder = new ViewHolder();
			holder.avatar = (HeadImageView) convertView.findViewById(R.id.avatar);
			holder.view = (ImageView) convertView.findViewById(R.id.view);
			holder.recommand = (ImageView) convertView.findViewById(R.id.recommand);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.watch = (TextView) convertView.findViewById(R.id.watch);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			/*holder.comment = (TextView) convertView.findViewById(R.id.comment);
			holder.like = (TextView) convertView.findViewById(R.id.like);*/
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.live = (TextView) convertView.findViewById(R.id.live);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.itemLL = (LinearLayout) convertView.findViewById(R.id.itemLL);
			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
		holder.avatar.setPerson(videoData.account, videoData.imageUrl);
		ImageLoader.displayPicImage(videoData.videoImage, holder.view);
		if(1 == videoData.isBigVideo){
			holder.recommand.setVisibility(View.GONE);
		/*	if(1 == videoData.isLive){
				holder.like.setText(R.string.live_broadcast);
			}else{
				holder.like.setText(R.string.recorded_broadcast);
			}*/
		}else if(1 == videoData.isComp){
			holder.recommand.setVisibility(View.VISIBLE);
			holder.recommand.setImageResource(R.drawable.ic_recommand_sai);
		}else if(0 == videoData.recommandType){
			holder.recommand.setVisibility(View.GONE);
		}else{
			holder.recommand.setVisibility(View.VISIBLE);
			if(1 == videoData.recommandType){
				holder.recommand.setImageResource(R.drawable.ic_recommand_jian);
			}else if(2 == videoData.recommandType){
				holder.recommand.setImageResource(R.drawable.ic_recommand_re);
			}
		}
		if (!TextUtils.isEmpty(videoData.nick)) {
			holder.name.setText(videoData.nick);
		}else{
			holder.name.setText("");
		}
		if(position % 2 == 0){
			holder.name.setBackgroundColor(mContext.getResources().getColor(R.color.watch_list_user_bg_1));
			holder.itemLL.setBackgroundColor(mContext.getResources().getColor(R.color.watch_list_video_bg_1));
		}else{
			holder.name.setBackgroundColor(mContext.getResources().getColor(R.color.watch_list_user_bg_2));
			holder.itemLL.setBackgroundColor(mContext.getResources().getColor(R.color.watch_list_video_bg_2));
		}
		if(TextUtils.isEmpty(videoData.dateTime)){
			holder.time.setText("");
		}else{
			Date date = DateUtil.getDateTime(videoData.dateTime, "-", ":");
			if(date == null){
				holder.time.setText("");
			}else{
				holder.time.setText(IMUtil.getVideoTime(date.getTime()));
			}
		}
		//FIXME 原型中观后面有个括号，存放的是观众中的好友个数
		holder.watch.setText(mContext.getString(R.string.watch_num, videoData.watchersNum));
		String distance = DisUtil.distance(videoData.locationX + "", videoData.locationY + "");
		if(!TextUtils.isEmpty(distance)){
			holder.distance.setText(distance);
		}else{
			holder.distance.setText("");
		}
		/*holder.comment.setText(mContext.getString(R.string.bullet_num, videoData.bulletsNum));
		holder.like.setText(mContext.getString(R.string.up_num, videoData.upNum));*/
		holder.title.setText(videoData.title);
		convertView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				VideoPlayerActivity.start(mContext, videoData);
			}
		});
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
		HeadImageView avatar;
		ImageView view, recommand;
		TextView name, watch, distance, title, live;
		TextView time;
		LinearLayout itemLL;
//		TextView comment,like;
	}
}

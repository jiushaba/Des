package com.lb.zbrj.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lb.common.util.Constants;
import com.lb.video.activity.VideoPlayerActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.data.db.WatchHistoryHelper;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HeadImageView;

public class WatchHistoryListAdapter extends BaseAdapter {
	@SuppressWarnings("unused")
	private Context mContext;
	private List<Object> mVideoList;
	private LayoutInflater layoutInflater;
	private WatchHistoryHelper mWatchHistoryHelper;
	private NetIF_ZBRJ netIf_ZBRJ = null;
	public AfterClearAllListener mAfterClearAllListener;
	public WatchHistoryListAdapter(Context context, List<Object> videoList) {
		this.mContext = context;
		mVideoList = videoList;
		layoutInflater = LayoutInflater.from(context);
		mWatchHistoryHelper = new WatchHistoryHelper(AccountData.getInstance().getBindphonenumber());
		netIf_ZBRJ = new NetIF_ZBRJ(context);
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
			convertView = layoutInflater.inflate(R.layout.activity_watchhistory_list_item, null);
			holder = new ViewHolder();
			holder.avatar = (HeadImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.watchTime = (TextView) convertView.findViewById(R.id.watch_time);
			holder.del = (TextView) convertView.findViewById(R.id.del);
			holder.delAll = (TextView) convertView.findViewById(R.id.delAll);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(mVideoList.get(position) instanceof VideoData){
			holder.avatar.setVisibility(View.VISIBLE);
			holder.name.setVisibility(View.VISIBLE);
			holder.title.setVisibility(View.VISIBLE);
			holder.watchTime.setVisibility(View.VISIBLE);
			holder.del.setVisibility(View.VISIBLE);
			holder.delAll.setVisibility(View.GONE);
			final VideoData videoData = (VideoData)mVideoList.get(position);
			holder.avatar.setPerson(videoData.account, videoData.imageUrl);
			if (!TextUtils.isEmpty(videoData.nick)) {
				holder.name.setText(videoData.nick);
			}
			holder.title.setText(videoData.title);
			holder.watchTime.setText(videoData.watchTime);
			holder.del.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					mWatchHistoryHelper.del(videoData.videoID);
					mHandler.obtainMessage(REFRESH, videoData.videoID).sendToTarget();
				}
				
			});
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view ) {
					NetAsyncTask task = new NetAsyncTask(mContext, videoData.videoID);
					AppUtil.execAsyncTask(task);
				}
			});
		}else{
			holder.avatar.setVisibility(View.GONE);
			holder.name.setVisibility(View.GONE);
			holder.title.setVisibility(View.GONE);
			holder.watchTime.setVisibility(View.GONE);
			holder.del.setVisibility(View.GONE);
			holder.delAll.setVisibility(View.VISIBLE);
			holder.delAll.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					mWatchHistoryHelper.delAll();
					mHandler.obtainMessage(REFRESH_ALL).sendToTarget();
					if(mAfterClearAllListener != null)mAfterClearAllListener.afterClearAll();
				}
				
			});
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
	
	public interface AfterClearAllListener{
		public void afterClearAll();
	}

	static class ViewHolder {
		HeadImageView avatar;
		TextView name, watchTime, title, del, delAll;
	}
	
	private static final int REFRESH = 0;
	private static final int REFRESH_ALL = 1;
	private UIHandler mHandler = new UIHandler();

	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 刷新列表
			case REFRESH:
				String id = (String)msg.obj;
				VideoData video = null;
				for(int i=0;i<mVideoList.size();i++){
					if(mVideoList.get(i) instanceof VideoData){
						VideoData data = (VideoData)mVideoList.get(i);
						if(id.equals(data.videoID)){
							video = data;
							break;
						}
					}
				}
				mVideoList.remove(video);
				if(mVideoList.size() == 1)mVideoList.remove(0);
				WatchHistoryListAdapter.this.notifyDataSetChanged();
				break;
			case REFRESH_ALL:
				mVideoList.clear();
				WatchHistoryListAdapter.this.notifyDataSetChanged();
				break;
			}
		}
	}
	
	class NetAsyncTask extends BaseNetAsyncTask{
		private Context context;
		private String videoID;
		public NetAsyncTask(Context context ,String videoID) {
			super(context);
			this.context = context;
			this.videoID = videoID;
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return netIf_ZBRJ.m1_get_videoInfo(videoID);
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			VideoData videoData = null;
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				videoData = (VideoData)result.getObj();
			}else{
			}
			if(videoData != null){
				VideoPlayerActivity.start(mContext, videoData);
			}else{
				Toast.makeText(context, R.string.no_video, 3000);
			}
			
		}
		
	}
}

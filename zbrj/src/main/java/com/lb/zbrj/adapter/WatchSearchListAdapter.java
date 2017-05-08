package com.lb.zbrj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.data.VideoTagData;
import com.lb.zbrj.data.db.VideoTagHelper;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HeadImageView;

public class WatchSearchListAdapter extends BaseAdapter implements Filterable{
	private Context mContext;
	private List<Object> mVideoList;
	private LayoutInflater layoutInflater;
	protected VideoTagHelper mVideoTagHelper;
	public AfterClearAllListener mAfterClearAllListener;
	DBFilter filter;
	public WatchSearchListAdapter(Context context, List<Object> videoList) {
		this.mContext = context;
		mVideoList = videoList;
		layoutInflater = LayoutInflater.from(context);
		mVideoTagHelper = new VideoTagHelper(AccountData.getInstance().getBindphonenumber());
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
			convertView = layoutInflater.inflate(R.layout.activity_watchsearch_list_item, null);
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

		if(mVideoList.get(position) instanceof VideoTagData){
			holder.avatar.setVisibility(View.GONE);
			holder.name.setVisibility(View.GONE);
			holder.title.setVisibility(View.VISIBLE);
			holder.watchTime.setVisibility(View.GONE);
			holder.del.setVisibility(View.VISIBLE);
			holder.delAll.setVisibility(View.GONE);
			final VideoTagData videoData = (VideoTagData)mVideoList.get(position);
			holder.title.setText(videoData.tag);
			holder.del.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					mVideoTagHelper.delRecent(videoData);
					mHandler.obtainMessage(REFRESH, videoData.tag).sendToTarget();
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
					mVideoTagHelper.delAll();
					mHandler.obtainMessage(REFRESH_ALL).sendToTarget();
					if(mAfterClearAllListener != null)mAfterClearAllListener.afterClearAll();
				}
				
			});
		}
		
		return convertView;
	}

	public void addMoreData(List<VideoTagData> videoList) {
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
				String tag = (String)msg.obj;
				VideoTagData video = null;
				for(int i=0;i<mVideoList.size();i++){
					if(mVideoList.get(i) instanceof VideoTagData){
						VideoTagData data = (VideoTagData)mVideoList.get(i);
						if(tag.equals(data.tag)){
							video = data;
							break;
						}
					}
				}
				mVideoList.remove(video);
				if(mVideoList.size() == 1)mVideoList.remove(0);
				WatchSearchListAdapter.this.notifyDataSetChanged();
				break;
			case REFRESH_ALL:
				mVideoList.clear();
				WatchSearchListAdapter.this.notifyDataSetChanged();
				break;
			}
		}
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new DBFilter();
		}
		return filter;
	}
	
	private class DBFilter extends Filter {
		/**
		 * 查询数据库
		 */
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			//查询结果保存到FilterResults对象里
			FilterResults results = new FilterResults();
			List<VideoTagData> queryUsers = mVideoTagHelper.findRecentTags(prefix.toString());
			results.values = queryUsers;
			results.count = queryUsers.size();
			return results;
		}
 
		/**
		 * 更新UI
		 */
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			List<VideoTagData> queryUsers = (List<VideoTagData>) results.values;
			//把结果读取出复制到users里
			if (mVideoList == null) {
				mVideoList = new ArrayList<Object>();
			}
			if (mVideoList.size() > 0) {
				mVideoList.clear();
			}
 
			if (queryUsers != null && queryUsers.size() > 0){
				for (VideoTagData user : queryUsers) {
					mVideoList.add(user);
					notifyDataSetChanged();
				}
				mVideoList.add("清除全部");
			}	
		}
 
	}
}

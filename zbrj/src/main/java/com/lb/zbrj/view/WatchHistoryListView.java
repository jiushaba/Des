package com.lb.zbrj.view;

import java.util.ArrayList;
import java.util.List;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.adapter.WatchHistoryListAdapter;
import com.lb.zbrj.adapter.WatchHistoryListAdapter.AfterClearAllListener;
import com.lb.zbrj.data.db.WatchHistoryHelper;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase;
import com.lz.oncon.data.AccountData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

@SuppressLint("HandlerLeak")
public class WatchHistoryListView extends VideoListView{

	protected WatchHistoryHelper mWatchHistoryHelper;
	
	public WatchHistoryListView(Context context) {
		super(context);
		init();
	}
	
	public WatchHistoryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public WatchHistoryListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		mWatchHistoryHelper = new WatchHistoryHelper(AccountData.getInstance().getUsername());
		// 测试数据
		//mWatchHistoryHelper.initTestData();
	}
	
	protected void setListAdapter(List<Object> videoList){
		if(null == videoList || videoList.size() == 0){
			if(null == mVideoList || mVideoList.size() == 0){
				showEmptyTextView(emptyText);
			}else{
				setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			}
			return;
		}
		if(videoList.size() < Constants.PAGE_SIZE_INT){
			setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		}
		if (null == mVideoList) {
			mVideoList = new ArrayList<Object>();
		}
		if (mRefreshType == REFRESH_MODE_PULL_DOWN) {
			mVideoList.clear();
			mPageNum = 2;
			mVideoList.addAll(videoList);
			if(videoList.size() >= 1){
				mVideoList.add(getContext().getString(R.string.all_clear));
			}
		} else if (mRefreshType == REFRESH_MODE_PULL_UP) {
			mPageNum++;
			mVideoList.addAll(mVideoList.size() - 2, videoList);
		}
		if (null == mWatchListAdapter) {
			mWatchListAdapter = new WatchHistoryListAdapter(getContext(), mVideoList);
			((WatchHistoryListAdapter)mWatchListAdapter).mAfterClearAllListener = new AfterClearAllListener() {
				@Override
				public void afterClearAll() {
					showEmptyTextView(emptyText);
				}
			};
			mPullToRefreshListView.setAdapter(mWatchListAdapter);
		} else {
			((WatchHistoryListAdapter)mWatchListAdapter).setVideoList(mVideoList);
			mWatchListAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 处理返回数据
	 * 
	 * @param niStatusData
	 */
	protected void handleResultData(NetInterfaceStatusDataStruct niStatusData) {
		@SuppressWarnings("unchecked")
		ArrayList<Object> videoList = (ArrayList<Object>) niStatusData.getObj();
		setListAdapter(videoList);
	}
	
	/**
	 * 搜索视频
	 */
	protected void getVideoList() {
		NetInterfaceStatusDataStruct niStatusData = new NetInterfaceStatusDataStruct();
		niStatusData.setStatus(Constants.RES_SUCCESS);
		niStatusData.setObj(mWatchHistoryHelper.find(mPageNum == 1 ? 0 : getLastVideoID()
				, Constants.PAGE_SIZE_INT));
		mLoadingRefreshState = LiveVideoListView.LoadingRefresh.IDLE;
		mLoadingMoreState = LiveVideoListView.LoadingMore.IDLE;
		mPageNum++;
		searchSuccess(niStatusData);
	}
}
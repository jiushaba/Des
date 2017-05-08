package com.lb.zbrj.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.zbrj.adapter.CompListAdapter;
import com.lb.zbrj.controller.CompareComp;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.Mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

@SuppressLint("HandlerLeak")
public class CompVideoListView extends VideoListView{

	public CompVideoListView(Context context) {
		super(context);
		init();
	}
	
	public CompVideoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public CompVideoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setMode(Mode.PULL_FROM_START);
	}
	
	protected void setListAdapter(List<Object> videoList){
		if(null == videoList || videoList.size() == 0){
			if(null == mVideoList || mVideoList.size() == 0){
				showEmptyTextView(emptyText);
			}
			return;
		}
		if (null == mVideoList) {
			mVideoList = new ArrayList<Object>();
		}
		if (mRefreshType == REFRESH_MODE_PULL_DOWN) {
			mVideoList.clear();
		}
		try {
			Collections.sort(videoList, new CompareComp());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		mVideoList.addAll(videoList);
		if (null == mWatchListAdapter) {
			mWatchListAdapter = new CompListAdapter(getContext(), videoList);
			mPullToRefreshListView.setAdapter(mWatchListAdapter);
		} else {
			((CompListAdapter)mWatchListAdapter).setVideoList(mVideoList);
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
		NetInterfaceStatusDataStruct niStatusData = new NetIF_ZBRJ(getContext()).m1_get_compVideoList();
		mLoadingRefreshState = LiveVideoListView.LoadingRefresh.IDLE;
		mLoadingMoreState = LiveVideoListView.LoadingMore.IDLE;
		if ("0".equals(niStatusData.getStatus())) {//比赛视频不分页
			searchSuccess(niStatusData);
		} else {
			searchFail(niStatusData);
		}
	}
}
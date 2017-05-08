package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;
import java.util.List;

import com.lb.common.util.Constants;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.view.VideoListView;
import com.lb.zbrj.view.LiveVideoListView;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MsgHistoryListView extends VideoListView{
	
	public TextView msgNumV;

	public MsgHistoryListView(Context context) {
		super(context);
		setMode(PullToRefreshBase.Mode.PULL_FROM_START);
	}
	
	public MsgHistoryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setMode(PullToRefreshBase.Mode.PULL_FROM_START);
	}
	
	@SuppressLint("NewApi")
	public MsgHistoryListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setMode(PullToRefreshBase.Mode.PULL_FROM_START);
	}
	
	protected void setListAdapter(List<Object> videoList){
		msgNumV.setText(getContext().getString(R.string.history_msg_fmt, videoList == null ? "0" : videoList.size() + ""));
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
		} else if (mRefreshType == REFRESH_MODE_PULL_UP) {
			mPageNum++;
		}
		mVideoList.addAll(videoList);
		if (null == mWatchListAdapter) {
			mWatchListAdapter = new MsgHistoryListAdapter(getContext(), videoList);
			mPullToRefreshListView.setAdapter(mWatchListAdapter);
		} else {
			((MsgHistoryListAdapter)mWatchListAdapter).setList(mVideoList);
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
	 * 获取粉丝列表
	 */
	protected void getVideoList() {
		NetInterfaceStatusDataStruct niStatusData = new NetIF_ZBRJ(getContext()).m1_get_allreply();
		mLoadingRefreshState = LiveVideoListView.LoadingRefresh.IDLE;
		mLoadingMoreState = LiveVideoListView.LoadingMore.IDLE;
		if ("0".equals(niStatusData.getStatus())) {
			searchSuccess(niStatusData);
		} else {
			searchFail(niStatusData);
		}
	}
}
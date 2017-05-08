package com.lb.zbrj.view;

import java.util.ArrayList;
import java.util.List;

import com.lb.common.util.Constants;
import com.lb.zbrj.adapter.SingleCompTopAdapter;
import com.lb.zbrj.data.CompData;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

public class SingleCompTopView extends VideoListView{
	
	private CompData data;

	public SingleCompTopView(Context context) {
		super(context);
	}
	
	public SingleCompTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@SuppressLint("NewApi")
	public SingleCompTopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setData(CompData data){
		this.data = data;
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
		} else if (mRefreshType == REFRESH_MODE_PULL_UP) {
			mPageNum++;
		}
		mVideoList.addAll(videoList);
		if (null == mWatchListAdapter) {
			mWatchListAdapter = new SingleCompTopAdapter(getContext(), videoList);
			((SingleCompTopAdapter)mWatchListAdapter).setComp(data);
			mPullToRefreshListView.setAdapter(mWatchListAdapter);
		} else {
			((SingleCompTopAdapter)mWatchListAdapter).setVideoList(mVideoList);
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
		NetInterfaceStatusDataStruct niStatusData = new NetIF_ZBRJ(getContext()).m1_get_CompTopLine(data.compid
				, mPageNum == 1 ? 0 : getLastVideoID()
				, Constants.PAGE_SIZE_INT);
		mLoadingRefreshState = LiveVideoListView.LoadingRefresh.IDLE;
		mLoadingMoreState = LiveVideoListView.LoadingMore.IDLE;
		if ("0".equals(niStatusData.getStatus())) {//比赛视频不分页
			searchSuccess(niStatusData);
		} else {
			searchFail(niStatusData);
		}
	}
}
package com.lb.zbrj.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import com.danmu.comm.Log;
import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.lb.zbrj.adapter.LiveListAdapter;
import com.lb.zbrj.controller.CompareVideoData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase;

@SuppressLint("HandlerLeak")
public class LiveVideoListView extends VideoListView{
	@SuppressLint("SimpleDateFormat")
	public LiveVideoListView(Context context) {
		super(context);
		init();
	}
	
	public LiveVideoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public LiveVideoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		mPullToRefreshListView.mRefreshableView.setDivider(new ColorDrawable(Color.argb(0, 0, 0, 0)));
	}
	
	protected void setListAdapter(List<Object> videoList){
		if(null == videoList || videoList.size() == 0){
			if(null == mVideoList || mVideoList.size() == 0){
				showEmptyTextView(emptyText);
				return;
			}else{
				if(mRefreshType !=REFRESH_MODE_PULL_DOWN ){
					setMode(PullToRefreshBase.Mode.PULL_FROM_START);
				}
			}
			
		}
		if(videoList.size() < Constants.PAGE_SIZE_INT && mRefreshType ==REFRESH_MODE_PULL_UP){
			setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		}
		if (null == mVideoList) {
			mVideoList = new ArrayList<Object>();
		}
		//下拉更新，如果返回行数等于一页，则清除显示；直播直接清除页面
		if (mRefreshType == REFRESH_MODE_PULL_DOWN && (videoList.size()==Constants.PAGE_SIZE_INT|| actTypeisLive())){
			mVideoList.clear();
		}
		/*if (mRefreshType == REFRESH_MODE_PULL_DOWN) {
			mVideoList.clear();
			mPageNum = 2;
		} else if (mRefreshType == REFRESH_MODE_PULL_UP) {
			mPageNum++;
		}*/
		if(videoList != null && videoList.size()>0){
			mVideoList.addAll(videoList);
		}
		//按照时间排序
		Collections.sort(mVideoList, new CompareVideoData());
		if (null == mWatchListAdapter) {
			mWatchListAdapter = new LiveListAdapter(getContext(), videoList);
			mPullToRefreshListView.setAdapter(mWatchListAdapter);
		} else {
			((LiveListAdapter)mWatchListAdapter).setVideoList(mVideoList);
			mWatchListAdapter.notifyDataSetChanged();
		}
	}
	@Override
	public void clearData(){
		if(mVideoList != null){
			mVideoList.clear();
			((LiveListAdapter)mWatchListAdapter).setVideoList(mVideoList);
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
		String starttime = computeStartTime(mRefreshType);
		int isUpdate = 0;
		//直播数据，下来直接获取最新的
		if(actTypeisLive() && mRefreshType == REFRESH_MODE_PULL_DOWN){
			starttime = "0";
			isUpdate = 0;
			
		} else if(!"0".equals(starttime)){
			isUpdate = mRefreshType ==REFRESH_MODE_PULL_DOWN?1:0;
		}
		NetInterfaceStatusDataStruct niStatusData = new NetIF_ZBRJ(getContext()).m1_get_videoList(videoType
				, isUpdate
				, starttime , queryType, actType 
				, Constants.PAGE_SIZE_INT);
		mLoadingRefreshState = LiveVideoListView.LoadingRefresh.IDLE;
		mLoadingMoreState = LiveVideoListView.LoadingMore.IDLE;
		if ("0".equals(niStatusData.getStatus())) {
			searchSuccess(niStatusData);
		} else {
			searchFail(niStatusData);
		}
	}
	private String computeStartTime(int refreshType){
		if(mVideoList == null || mVideoList.size()==0)
			return "0";
		int index = 0;
		if(refreshType == REFRESH_MODE_PULL_DOWN){
			index = 0;
		}else{
			index = mVideoList.size()-1;
			
		}
		try {
			VideoData videoData = (VideoData)mVideoList.get(index);
			if(StringUtils.isNull(videoData.dateTime)){
				return "0";
			}
				
			return videoData.dateTime;
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			return "0";
		}
	}
	
	private boolean actTypeisLive(){
		if(actType == 0)
			return true;
		return false;
	}
}
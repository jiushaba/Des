package com.lb.zbrj.view;

import java.util.ArrayList;
import java.util.List;

import com.lb.common.util.Constants;
import com.lb.zbrj.adapter.FocusListAdapter;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.db.FocusHelper;
import com.lb.zbrj.listener.FocusListener;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

public class FocusListView extends VideoListView{
	
	private PersonController mPersonController;

	public FocusListView(Context context) {
		super(context);
		init();
	}
	
	public FocusListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public FocusListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setMode(Mode.PULL_FROM_START);
		mPersonController = new PersonController();
	}
	
	private String mobile;
	public void setMobile(String mobile){
		this.mobile = mobile;
	}
	
	protected void setListAdapter(List<Object> list){
		if(null == list || list.size() == 0){
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
		mVideoList.addAll(list);
		if (null == mWatchListAdapter) {
			mWatchListAdapter = new FocusListAdapter(getContext(), list);
			mPullToRefreshListView.setAdapter(mWatchListAdapter);
		} else {
			((FocusListAdapter)mWatchListAdapter).setList(mVideoList);
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
		ArrayList<Object> list = (ArrayList<Object>) niStatusData.getObj();
		setListAdapter(list);
	}
	
	/**
	 * 获取关注列表,不分页
	 */
	protected void getVideoList() {
		NetInterfaceStatusDataStruct niStatusData = new NetIF_ZBRJ(getContext()).m1_get_focus(mobile, -1, 0);
		mLoadingRefreshState = LiveVideoListView.LoadingRefresh.IDLE;
		mLoadingMoreState = LiveVideoListView.LoadingMore.IDLE;
		if ("0".equals(niStatusData.getStatus())) {
			searchSuccess(niStatusData);
			mPersonController.delSynFocus((ArrayList<FansData>)niStatusData.getObj());
		} else {
			searchFail(niStatusData);
		}
	}
}
package com.lb.zbrj.view;

import java.util.Calendar;
import java.util.List;

import com.lb.common.util.DateUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.net.NetworkStatusCheck;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshListView;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public abstract class VideoListView extends LinearLayout implements OnRefreshListener2<ListView> {
	
	public static final int ACTTYPE_LIVE = 0;
	public static final int ACTTYPE_PLAYBACK = 1;
	public static final int ACTTYPE_COMPETITION = 2;
	
	public static final int QUERYTYPE_ALL = -1;
	public static final int QUERYTYPE_RECOMMEND = 0;
	public static final int QUERYTYPE_HOT = 1;
	public static final int QUERYTYPE_FRIEND = 2;
	public static final int QUERYTYPE_NEARBY = 3;
	
	public static final int REFRESH_MODE_PULL_DOWN = 1;
	public static final int REFRESH_MODE_PULL_UP = 2;
	
	// 点击加载更多枚举所有状态
	public static enum LoadingRefresh {
		LOADING, // 加载状态
		IDLE; // 结束状态
	}

	public static enum LoadingMore {
		LOADING, // 加载状态
		IDLE; // 结束状态
	}
	
	public LoadingRefresh mLoadingRefreshState = LoadingRefresh.IDLE;
	public LoadingMore mLoadingMoreState = LoadingMore.IDLE;
	
	protected boolean disablePullDownRefreshWhenEnd;
	protected PullToRefreshListView mPullToRefreshListView;
	
	protected ImageView emptyImageView;
	public String emptyText = "";
	protected TextView emptyTextView;
	protected View emptyView;
	protected boolean end = false;
	protected int errorCode = 0;
	protected String initText = "";
	protected volatile boolean isLoading = false;
	protected String netErrorText = "";
	protected int nextPage = 0;
	protected ProgressBar progressbarImageView;
	protected String refreshNetErrorText = "";
	protected String retryOnTouchErrorText = "";
	protected String serverErrorText = "";
	public int mRefreshType = 0;
	public int mPageNum = 1;
	public int videoType, actType, opType, queryType = -1;
	public String filterValue;
	
	public List<Object> mVideoList;
	public BaseAdapter mWatchListAdapter;
	
	protected View sectionLayout;

	public VideoListView(Context context) {
		super(context);
		init();
	}
	
	public VideoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public VideoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.activity_watch_list_live, this);
		initText = getResources().getString(R.string.list_init_text);
		emptyText = getResources().getString(R.string.list_empty_text);
		netErrorText = getResources().getString(R.string.network_error_empty_list);
		refreshNetErrorText = getResources().getString(R.string.network_error_refresh);
		//FIXME 网络错误直接屏蔽掉
		//retryOnTouchErrorText = getResources().getString(R.string.network_error_retry_on_touch);
		retryOnTouchErrorText = emptyText;
		sectionLayout = findViewById(R.id.pull_to_refresh_section);
		if (sectionLayout != null) {
			sectionLayout.setVisibility(View.GONE);
		}
		mPullToRefreshListView = ((PullToRefreshListView) findViewById(R.id.pull_to_refresh_list_view));
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		emptyView = findViewById(R.id.pull_to_refresh_list_empty);
		mPullToRefreshListView.setEmptyView(emptyView);
		emptyTextView = ((TextView) findViewById(R.id.emptyText));
		emptyImageView = ((ImageView) findViewById(R.id.emptyIcon));
		progressbarImageView = ((ProgressBar) findViewById(R.id.progressbar));
		showEmptyTextView(initText);
		((Activity)getContext()).registerForContextMenu(mPullToRefreshListView.getRefreshableView());
		
		setMode(Mode.BOTH);
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//		setLastUpdatedLabel(0);
		mRefreshType = REFRESH_MODE_PULL_DOWN;
		mPageNum = 1;
		setMode(PullToRefreshBase.Mode.BOTH);
//		end = false;
		getOnlineData(mRefreshType);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//		if (end){
//			mPullToRefreshListView.onRefreshComplete();
//			mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//			return;
//		}
		mRefreshType = REFRESH_MODE_PULL_UP;
		getOnlineData(mRefreshType);
	}
	
	protected void showEmptyTextView(String emptyStr) {
		if (emptyTextView != null) {
			if (TextUtils.isEmpty(emptyStr)) {
				emptyStr = emptyText;
			}
			emptyTextView.setText(emptyStr);
			if ((!emptyStr.equals(initText))
					|| (progressbarImageView == null)) {
				if (progressbarImageView != null) {
					progressbarImageView.setVisibility(View.GONE);
				}
			}else{
				progressbarImageView.setVisibility(View.VISIBLE);
			}
			emptyTextView.setVisibility(View.VISIBLE);
			emptyImageView.setVisibility(View.GONE);
//			if (emptyImageView != null) {
//				emptyImageView.setVisibility(View.VISIBLE);
//			}			
		}					
	}
	
	/**
	 * 设置下拉刷新时显示的上次刷新时间
	 */
	protected void setLastUpdatedLabel(int pullType) {
		int refreshLabelId;
		if (pullType == 0) {
			refreshLabelId = R.string.pull_to_refresh_update_time;
		} else {
			refreshLabelId = R.string.pull_to_refresh_load_time;
		}
		mPullToRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(
				String.format(getContext().getString(refreshLabelId, DateUtil.getDateString(
						Calendar.getInstance().getTime(),
						DateUtil.CHINESE_PATTERN))));

	}
	
	public void initOnlineData(){
		mPageNum = 1;
		mRefreshType = VideoListView.REFRESH_MODE_PULL_DOWN;
		getOnlineData(mRefreshType);
	}
	
	private void getOnlineData(int refreshType){
		if (NetworkStatusCheck.isNetworkConnected(getContext())) {
			if (refreshType == REFRESH_MODE_PULL_UP) {
				if (mLoadingMoreState == LoadingMore.LOADING) {
					mPullToRefreshListView.onRefreshComplete();
					return;
				}
				mLoadingMoreState = LoadingMore.LOADING;
			} else {
				if (mLoadingRefreshState == LoadingRefresh.LOADING) {
					mPullToRefreshListView.onRefreshComplete();
					return;
				}
				mLoadingRefreshState = LoadingRefresh.LOADING;
				mPageNum = 1;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					getVideoList();
				}
			}).start();
		} else {
			mPullToRefreshListView.onRefreshComplete();
			showEmptyTextView(retryOnTouchErrorText);
	        emptyView.setOnClickListener(new View.OnClickListener(){
	            public void onClick(View view){
	            	scrollTopAndPullDownToRefresh();
	            }
	        });
		}
	}
	
	public void pullUpToRefresh(){
	    if (mPullToRefreshListView != null){
	    	mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
	    	mPullToRefreshListView.setRefreshing(false);
	    }
	    onPullUpToRefresh(null);
	}
	
	public void scrollTopAndPullDownToRefresh(){
	    if (mPullToRefreshListView != null) {
	    	mPullToRefreshListView.setRefreshing();
	    }
	    onPullDownToRefresh(null);
	}
	
	protected void setMode(PullToRefreshBase.Mode mode){
	    if (mPullToRefreshListView != null) {
	    	mPullToRefreshListView.setMode(mode);
	    }
	}
	
	protected int getLastVideoID(){
		if(null == mVideoList || mVideoList.size() == 0){
			return 0;
		}else{
			return mVideoList.size();
		}
	}
	/*
	 * 清空当前数据,默认返回，具体实现在子类中
	 */
	public void clearData(){
		return;
	}
	/**
	 * 处理返回数据
	 */
	protected abstract void handleResultData(NetInterfaceStatusDataStruct niStatusData);
	
	protected void searchSuccess(NetInterfaceStatusDataStruct niStatusData){
		Message msg = Message.obtain();
		msg.obj = niStatusData;
		msg.what = GET_SEARCH_ONE_SUSSESS;
		mUIHandler.sendMessage(msg);
	}
	
	protected void searchFail(NetInterfaceStatusDataStruct niStatusData){
		Message msg = Message.obtain();
		msg.obj = niStatusData;
		msg.what = GET_SEARCH_ONE_FAILED;
		mUIHandler.sendMessage(msg);
	}
	
	/**
	 * 搜索视频
	 */
	protected abstract void getVideoList();
	
	private static final int GET_SEARCH_ONE_SUSSESS = 1;
	private static final int GET_SEARCH_ONE_FAILED = 2;

	private UIHandler mUIHandler = new UIHandler();
	
	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mPullToRefreshListView.onRefreshComplete();
			NetInterfaceStatusDataStruct niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
			switch (msg.what) {
			case GET_SEARCH_ONE_SUSSESS:
				handleResultData(niStatusData);
				break;
			case GET_SEARCH_ONE_FAILED:
				handleResultData(niStatusData);
				break;
			}
		}
	}
}
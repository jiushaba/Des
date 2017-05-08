package com.lz.oncon.app.im.ui.common;

import java.util.ArrayList;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import com.lb.common.util.Log;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.data.ImData.OnMsgDelListener;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.app.im.util.IMUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class IMMessageListView extends ListView implements OnScrollListener, OnMsgDelListener {

	private View headView;
	private LinearLayout refreshPB;
	private TextView tipCount, tipWhen;
	
	private IMMessageCommonListViewAdapter mAdapter;
	
	private String mOnconId;
	private String tipCountStr, tipWhenStr;
	private ArrayList<SIXmppMessage> mMsgs;
	public int totalCount, loadTimes, loadLeft; // 聊天记录的总条数
	private int frontRowId; // 第一个可视项索引
	public boolean delete_action = false;
	public boolean refreshing = false;
	
	public IMMessageListView(Context context) {
		super(context);
		init();
	}
	
	public IMMessageListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public IMMessageListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		headView = LayoutInflater.from(getContext()).inflate(R.layout.msgcenter_loadmore, null);
		refreshPB = (LinearLayout) headView.findViewById(R.id.refreshPB);
		tipCount = (TextView) headView.findViewById(R.id.tipCount);
		tipWhen = (TextView) headView.findViewById(R.id.tipWhen);
		addHeaderView(headView);
		tipCountStr = getResources().getString(R.string.page_load_tipcount);
		tipWhenStr = getResources().getString(R.string.page_load_tipwhen);
		setOnScrollListener(this);
		ImData.getInstance().addOnMsgDelListener(this);
	}
	
	public void setInfo(String onconId, ArrayList<SIXmppMessage> msgs){
		mOnconId = onconId;
		mMsgs = msgs;
		totalCount = ImData.getInstance().getMsgCount(mOnconId);
		loadTimes = totalCount / IMConstants.PAGE_SIZE;
		loadLeft = totalCount % IMConstants.PAGE_SIZE;
		if (loadTimes >= 1) {
			mMsgs.addAll(ImData.getInstance().getMsgByLimit(mOnconId, (loadTimes - 1) * IMConstants.PAGE_SIZE + loadLeft, IMConstants.PAGE_SIZE, "asc"));
		} else {
			mMsgs.addAll(ImData.getInstance().getMsgByLimit(mOnconId, 0, loadLeft, "asc"));
		}
	}
	
	@Override
	public void setAdapter(ListAdapter adapter){
		super.setAdapter(adapter);
		mAdapter = (IMMessageCommonListViewAdapter)adapter;
		scroll2Bottom();
	}
	
	private void loadMoreData(){
		if (delete_action) {
			totalCount = ImData.getInstance().getMsgCount(mOnconId);
			loadTimes = totalCount / IMConstants.PAGE_SIZE;
			loadLeft = totalCount % IMConstants.PAGE_SIZE;
			delete_action = false;
		}
		int beginIndex = (loadTimes - 2) * IMConstants.PAGE_SIZE + loadLeft + 1;
		int endIndex = (loadTimes - 1) * IMConstants.PAGE_SIZE + loadLeft;
		if (beginIndex == 0 || endIndex == 0) {
			return;
		}
		if (loadTimes > 0 && !refreshing && totalCount > IMConstants.PAGE_SIZE) {
			refreshPB.setVisibility(View.VISIBLE);
			refreshing = true;
			if ((totalCount - mMsgs.size()) / IMConstants.PAGE_SIZE == 0) {
				if (loadLeft == 1) {
					tipCount.setText(String.format(getResources().getString(R.string.page_load_tipcountOne), totalCount, 1));
				} else {
					tipCount.setText(String.format(tipCountStr, totalCount, 1, loadLeft));
				}
			} else {
				tipCount.setText(String.format(tipCountStr, totalCount, beginIndex, endIndex));
			}
			tipWhen.setText(String.format(tipWhenStr, IMUtil.getStringByTime(mMsgs.get(mMsgs.size() - 1).getTime())));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					--loadTimes;
					// 每多取n条记录就把加载次数自减
					if (loadTimes >= 1) {
						mMsgs.addAll(ImData.getInstance().getMsgByLimit(mOnconId, (loadTimes - 1) * IMConstants.PAGE_SIZE + loadLeft, IMConstants.PAGE_SIZE, "asc"));
						mHandler.sendEmptyMessage(IMConstants.CLOSE_PROGRESSBAR);
					} else if (loadTimes == 0) {
						mMsgs.addAll(ImData.getInstance().getMsgByLimit(mOnconId, 0, loadLeft, "asc"));
						mHandler.sendEmptyMessage(IMConstants.CLOSE_PROGRESSBAR);
					}
				}
			}, 2000);
		}
	}
	
	private static final int RECEIVE_MESSAGE_OR_REFRESH_LIST = 300;
	public UIHandler mHandler = new UIHandler();

	public class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case IMConstants.CLOSE_PROGRESSBAR:
				refreshing = false;
				refreshPB.setVisibility(View.GONE);
				IMUtil.sortMsgs(mMsgs);
				notifyAdapter();
				if (loadTimes >= 1) {
					setSelection(IMConstants.PAGE_SIZE);
				} else if (loadTimes == 0) {
					setSelection(loadLeft);
				} else {
					setSelection(0);
				}
				break;
			case RECEIVE_MESSAGE_OR_REFRESH_LIST:
				refresh();
				break;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		frontRowId = firstVisibleItem;
		if (totalCount + 1 == totalItemCount) {
			removeHeaderView(headView);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && 0 == frontRowId) {
			// 这句是判断如果当前的集合大小还大于每页的大小并且之前没有线程在刷新还可以分页刷新
			loadMoreData();
		}
	}
	
	public void scroll2Bottom(){
		if (mMsgs != null && mMsgs.size() > 0) {
			int p = 0;
			if (getHeaderViewsCount() > 0) {
				p = mMsgs.size() - 1 + getHeaderViewsCount();
			} else {
				p = mMsgs.size() - 1;
			}
			setSelection(p);
		}
	}
	
	public void scrollToMsg(final String msgId){
		((BaseActivity)getContext()).showProgressDialog(R.string.wait, false);
		new Thread(){
			public void run(){
				int position = -1;
				ArrayList<SIXmppMessage> msgs = new ArrayList<SIXmppMessage>();
				final ArrayList<SIXmppMessage> allmsgs = new ArrayList<SIXmppMessage>();
				msgs.addAll(mMsgs);
				while((position = getPosition(msgId, msgs)) == -1){
					--loadTimes;
					if (loadTimes >= 1) {
						msgs = ImData.getInstance().getMsgByLimit(mOnconId, (loadTimes - 1) * IMConstants.PAGE_SIZE + loadLeft, IMConstants.PAGE_SIZE, "asc");
					} else if (loadTimes == 0) {
						msgs = ImData.getInstance().getMsgByLimit(mOnconId, 0, loadLeft, "asc");
					}
					allmsgs.addAll(msgs);
				}
				final int point = position;
				((BaseActivity)getContext()).runOnUiThread(new Runnable(){
					public void run(){
						mMsgs.addAll(allmsgs);
						IMUtil.sortMsgs(mMsgs);
						notifyAdapter();
						IMMessageListView.this.postDelayed(new Runnable() {  
							@Override  
							public void run() {
								IMMessageListView.this.requestFocusFromTouch();  
								IMMessageListView.this.setSelection(point); 
								((BaseActivity)getContext()).hideProgressDialog();
							}  
						},500);
					}
				});
			}
		}.start();
	}
	
	private int getPosition(String msgId, ArrayList<SIXmppMessage> msgs){
		int position = -1;
		int len = msgs.size();
		for(int i=0;i<len;i++){
			if(msgId.equals(msgs.get(i).getId())){
				position = i;
				break;
			}
		}
		return position;
	}
	
	public void refresh(){
		if (mMsgs != null) {
			IMUtil.sortMsgs(mMsgs);
			notifyAdapter();
			scroll2Bottom();
		}
	}
	
	private void notifyAdapter(){
		mAdapter.notifyDataSetChanged();
	}
	
	public void releaseResources(){
		ImData.getInstance().removeOnMsgDelListener(this);
	}

	@Override
	public void delMsgs(String onconId) {
		if (onconId.equals(mOnconId)) {
			mMsgs.clear();
			mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
		}
	}

	@Override
	public void delMsg(String onconId, String packedId) {
		try {
			if (onconId.equals(mOnconId)) {
				SIXmppMessage msg = null;
				for (SIXmppMessage data : mMsgs) {
					if (packedId.equals(data.getId())) {
						msg = data;
					}
				}
				if (msg != null)
					mMsgs.remove(msg);
				mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
				delete_action = true;
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
}
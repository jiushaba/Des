package com.lb.zbrj.activity;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.adapter.AddChannelMngAdapter;
import com.lb.zbrj.adapter.UnAddChannelMngAdapter;
import com.lb.zbrj.data.ChannelData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.view.VideoListView;
import com.lz.oncon.api.CustomProtocolListener;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class WatchListActivity extends CommonRefreshListActivity
		implements View.OnClickListener, OnItemClickListener, SynPersonInfoListener, CustomProtocolListener {
	
	private PersonData person;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		MyApplication.getInstance().addListener(Constants.LISTENER_SYN_PERSONINFO, this);
		ImCore.getInstance().getConnection().addCustomProtocolListener(this);
		initViews();
		initData();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		MyApplication.getInstance().removeListener(Constants.LISTENER_SYN_PERSONINFO, this);
		ImCore.getInstance().getConnection().removeCustomProtocolListener(this);
	}

	private void initViews() {
		//设置GRIDVIEW的ITEM的点击监听
	    otherGridView.setOnItemClickListener(this);
	    userGridView.setOnItemClickListener(this);
	    
	}

	private void initData() {
		person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
		setPersonInfo();
//		((VideoListView)v3).initOnlineData();FIXME 屏蔽活动页面
		((VideoListView)v2).initOnlineData();
		((VideoListView)v1).initOnlineData();
		
		getChannelData();
	}
	
	private void setPersonInfo(){
		if(person == null){
			leftTitleV.setText("0");
		}else{
			leftTitleV.setText(person.fansNum+"");
		}
		//修改为直接写入数字，zlj
		/*if(person == null){
			leftTitleV.setText(getString(R.string.fans_fmt, 0));
			rightTitleV.setText(getString(R.string.score_fmt, 0));
		}else{
			int fans = person.fansNum - person.oldFansNum;
			if(fans > 0){
				leftTitleV.setText(getString(R.string.fans_up_fmt, person.fansNum, fans));
			}else if(fans == 0){
				leftTitleV.setText(getString(R.string.fans_fmt, person.fansNum));
			}else{
				leftTitleV.setText(getString(R.string.fans_down_fmt, person.fansNum, fans));
			}
			rightTitleV.setText(getString(R.string.score_fmt, person.score));
		}*/
	}

	@Override
	public void requestOnlineData() {
	}
	
	private void getChannelData(){
		channelDatas = mChannelHelper.findAll();
		for(int i=0;i<channelDatas.size();i++){
			if(channelDatas.get(i).isAdd == 0){
				otherChannelList.add(channelDatas.get(i));
			}else{
				userChannelList.add(channelDatas.get(i));
				addChannels.add(channelDatas.get(i).name);
			}
		}
		channelsV.setDatas(addChannels);
		channelmngmenus.add(getString(R.string.channel_manage));
		channelmngmenus.add(getString(R.string.filter));
		channelmngmenus.add(getString(R.string.watch_history));
		
		if(userAdapter == null){
			 userAdapter = new AddChannelMngAdapter(this, userChannelList);
			 userGridView.setAdapter(userAdapter);
		}else{
			userAdapter.notifyDataSetChanged();
		}
		if(otherAdapter == null){
			otherAdapter = new UnAddChannelMngAdapter(this, otherChannelList);
		    otherGridView.setAdapter(this.otherAdapter);
		}else{
			otherAdapter.notifyDataSetChanged();
		}
	}
	
	private void getChannelData2(){
		channelDatas = mChannelHelper.findAll();
		addChannels.clear();
		otherChannelList.clear();
		userChannelList.clear();
		for(int i=0;i<channelDatas.size();i++){
			if(channelDatas.get(i).isAdd == 0){
				otherChannelList.add(channelDatas.get(i));
			}else{
				userChannelList.add(channelDatas.get(i));
				addChannels.add(channelDatas.get(i).name);
			}
		}
		channelsV.setDatas(addChannels);
		userAdapter.notifyDataSetChanged();
		otherAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = null;
		switch(arg0.getId()){
		case R.id.title_left_ll:
			//粉丝列表
			intent = new Intent(this, FansListActivity.class);
			startActivity(intent);
			break;
		case R.id.title_search:
//			if(menuV.getVisibility() == View.VISIBLE){
//				menuV.setVisibility(View.GONE);
//				hideMenu();
//				mngLayout.setVisibility(View.VISIBLE);
//				vPager.setVisibility(View.GONE);
//				actTypeLayout.setVisibility(View.GONE);
//				channelMngLayout.setVisibility(View.GONE);
//				mWatchHistoryV.setVisibility(View.GONE);
//				mCollectVideoV.setVisibility(View.GONE);
//				searchLayout.setVisibility(View.VISIBLE);
//				setMenuType(R.string.search);
//				searchVideoListView.opType = 1;
//				searchVideoListView.filterValue = "";
//				searchTipV.setVisibility(View.VISIBLE);
//				recentVideoTagGridView.setVisibility(View.VISIBLE);
//				searchVideoListView.setVisibility(View.GONE);
//			}else{
//				menuV.setVisibility(View.VISIBLE);
//				goBack2Channel();
//			}
			intent = new Intent(this, WatchListSearchActivity.class);
			startActivity(intent);
			break;
		case R.id.history:
			hideMenu();
			mngLayout.setVisibility(View.VISIBLE);
			vPager.setVisibility(View.GONE);
			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.VISIBLE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(R.string.history);
			mWatchHistoryV.initOnlineData();
			break;
		case R.id.collect:
			hideMenu();
			mngLayout.setVisibility(View.VISIBLE);
			vPager.setVisibility(View.GONE);
			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.VISIBLE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(R.string.collect);
			mCollectVideoV.initOnlineData();
			break;
		case R.id.channel:
			hideMenu();
			mngLayout.setVisibility(View.VISIBLE);
			vPager.setVisibility(View.GONE);
			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.VISIBLE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(R.string.channel);
			break;
		case R.id.watch_only:
			if(subMenuLL.getVisibility() == View.VISIBLE){
				subMenuLL.setVisibility(View.GONE);
			}else{
				subMenuLL.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.hot:
			hideMenu();
			menuV.setImageResource(R.drawable.icon_arrow_down);
			mngLayout.setVisibility(View.GONE);
			vPager.setVisibility(View.VISIBLE);
//			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(getString(R.string.hot));
			initOnlineDataByQueryType(VideoListView.QUERYTYPE_HOT);
			break;
		case R.id.recommend:
			hideMenu();
			menuV.setImageResource(R.drawable.icon_arrow_down);
			mngLayout.setVisibility(View.GONE);
			vPager.setVisibility(View.VISIBLE);
//			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(getString(R.string.recommend));
			initOnlineDataByQueryType(VideoListView.QUERYTYPE_RECOMMEND);
			break;
		case R.id.friend:
			hideMenu();
			menuV.setImageResource(R.drawable.icon_arrow_down);
			mngLayout.setVisibility(View.GONE);
			vPager.setVisibility(View.VISIBLE);
//			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(getString(R.string.friend));
			initOnlineDataByQueryType(VideoListView.QUERYTYPE_FRIEND);
			break;
		case R.id.nearby:
			hideMenu();
			menuV.setImageResource(R.drawable.icon_arrow_down);
			mngLayout.setVisibility(View.GONE);
			vPager.setVisibility(View.VISIBLE);
//			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType(getString(R.string.nearby));
			initOnlineDataByQueryType(VideoListView.QUERYTYPE_NEARBY);
			break;
		case R.id.all:
			hideMenu();
			menuV.setImageResource(R.drawable.icon_arrow_down);
			mngLayout.setVisibility(View.GONE);
			vPager.setVisibility(View.VISIBLE);
//			actTypeLayout.setVisibility(View.GONE);
			channelMngLayout.setVisibility(View.GONE);
			mWatchHistoryV.setVisibility(View.GONE);
			mCollectVideoV.setVisibility(View.GONE);
//			searchLayout.setVisibility(View.GONE);
			setMenuType("");
			initOnlineDataByQueryType(VideoListView.QUERYTYPE_ALL);
			break;
		case R.id.menu:
			if(channelsV.getVisibility() == View.VISIBLE){
				if(mainMenuLL.getVisibility() == View.GONE){
					menuV.setImageResource(R.drawable.icon_arrow_up);
					mainMenuLL.setVisibility(View.VISIBLE);
				}else{
					menuV.setImageResource(R.drawable.icon_arrow_down);
					hideMenu();
				}
			}else{
				goBack2Channel();
			}
			
			
//			if(viewPagerLayout.getVisibility() == View.VISIBLE){
//				menuV.setImageResource(R.drawable.icon_arrow_up);
//				actTypeLayout.setVisibility(View.GONE);
//				viewPagerLayout.setVisibility(View.GONE);
//				mngLayout.setVisibility(View.VISIBLE);
//				channelMngLayout.setVisibility(View.VISIBLE);
//				mWatchHistoryV.setVisibility(View.GONE);
//				channelsV.setDatas(channelmngmenus);
//				channelsV.mOnIndicatorChangeListener = new com.lz.oncon.widget.HorizontalIndicatorView.OnIndicatorChangeListener() {
//					@Override
//					public void onChange(int idx) {
//						if(idx == 0){//频道管理
//							channelMngLayout.setVisibility(View.VISIBLE);
//							searchByTagLayout.setVisibility(View.GONE);
//							mWatchHistoryV.setVisibility(View.GONE);
//						}else if(idx == 1){//筛选
//							channelMngLayout.setVisibility(View.GONE);
//							searchByTagLayout.setVisibility(View.VISIBLE);
//							mWatchHistoryV.setVisibility(View.GONE);
//						}else if(idx == 2){//观看历史
//							channelMngLayout.setVisibility(View.GONE);
//							searchByTagLayout.setVisibility(View.GONE);
//							mWatchHistoryV.setVisibility(View.VISIBLE);
//						}
//					}
//				};
//			}else{
//				menuV.setImageResource(R.drawable.icon_arrow_down);
//				actTypeLayout.setVisibility(View.VISIBLE);
//				viewPagerLayout.setVisibility(View.VISIBLE);
//				mngLayout.setVisibility(View.GONE);
//				saveChannel();
//				channelsV.mOnIndicatorChangeListener = new com.lz.oncon.widget.HorizontalIndicatorView.OnIndicatorChangeListener() {
//					@Override
//					public void onChange(int idx) {
//						ChannelData channel = userChannelList.get(idx);
//						currentVideoListView.videoType = channel.id;
//						currentVideoListView.queryValue = "";
//						currentVideoListView.initOnlineData();
//					}
//				};
//			}
			break;
		}
	}

	private void goBack2Channel() {
		menuV.setImageResource(R.drawable.icon_arrow_down);
		channelsV.setVisibility(View.VISIBLE);
		menuTypeV.setVisibility(View.GONE);
		mngLayout.setVisibility(View.GONE);
		vPager.setVisibility(View.VISIBLE);
		setMenuType("");
		initOnlineDataByQueryType(VideoListView.QUERYTYPE_ALL);
		actTypeLayout.setVisibility(View.VISIBLE);
		if(channelMngLayout.getVisibility() == View.VISIBLE){
			saveChannel();
			channelMngLayout.setVisibility(View.GONE);
		}
	}
	
	/** 退出时候保存选择后数据库的设置  */
	private void saveChannel() {
		mChannelHelper.update(userAdapter.getChannnelLst());
		mChannelHelper.update(otherAdapter.getChannnelLst());
		getChannelData2();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AppUtil.toBackground(this);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			return true;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view, final int position,long id) {
		//如果点击的时候，之前动画还没结束，那么就让点击事件无效
				if(isMove){
					return;
				}
				switch (parent.getId()) {
				case R.id.userGridView:
					//position为 0，1 的不可以进行任何操作
					if (position != 0 && position != 1) {
						final ImageView moveImageView = getView(view);
						if (moveImageView != null) {
							TextView newTextView = (TextView) view.findViewById(R.id.text_item);
							final int[] startLocation = new int[2];
							newTextView.getLocationInWindow(startLocation);
							final ChannelData channel = ((AddChannelMngAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
							otherAdapter.setVisible(false);
							//添加到最后一个
							otherAdapter.addItem(channel);
							new Handler().postDelayed(new Runnable() {
								public void run() {
									try {
										int[] endLocation = new int[2];
										//获取终点的坐标
										otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
										MoveAnim(moveImageView, startLocation , endLocation, channel,userGridView);
										userAdapter.setRemove(position);
									} catch (Exception localException) {
									}
								}
							}, 50L);
						}
					}
					break;
				case R.id.otherGridView:
					final ImageView moveImageView = getView(view);
					if (moveImageView != null){
						TextView newTextView = (TextView) view.findViewById(R.id.text_item);
						final int[] startLocation = new int[2];
						newTextView.getLocationInWindow(startLocation);
						final ChannelData channel = ((UnAddChannelMngAdapter) parent.getAdapter()).getItem(position);
						userAdapter.setVisible(false);
						//添加到最后一个
						userAdapter.addItem(channel);
						new Handler().postDelayed(new Runnable() {
							public void run() {
								try {
									int[] endLocation = new int[2];
									//获取终点的坐标
									userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
									MoveAnim(moveImageView, startLocation , endLocation, channel,otherGridView);
									otherAdapter.setRemove(position);
								} catch (Exception localException) {
								}
							}
						}, 50L);
					}
					break;
				default:
					break;
				}
	}
	
	private void hideMenu(){
		mainMenuLL.setVisibility(View.GONE);
		subMenuLL.setVisibility(View.GONE);
	}
	
	private void setMenuType(int resId){
		channelsV.setVisibility(View.GONE);
		menuTypeV.setVisibility(View.VISIBLE);
		menuTypeV.setText(resId);
	}
	
	private void setMenuType(String str){
		menuType = str;
		if(currentVideoListView.actType == VideoListView.ACTTYPE_LIVE){
			menu = getString(R.string.live);
		}else if(currentVideoListView.actType == VideoListView.ACTTYPE_PLAYBACK){
			menu = getString(R.string.playback);
		}else if(currentVideoListView.actType == VideoListView.ACTTYPE_COMPETITION){
			menu = getString(R.string.competition);
		}
		if(!TextUtils.isEmpty(menuType)){
			actTypeV.setText(menu + "-" + menuType);
		}else{
			actTypeV.setText(menu);
		}
	}

	@Override
	public void syn(PersonData person) {
		if(person != null && person.account.equals(AccountData.getInstance().getBindphonenumber())){
			this.person = person;
			mUIHandler.sendEmptyMessage(SET_PERSON_INFO);
		}
	}
	
	private static final int SET_PERSON_INFO = 1;
	UIHandler mUIHandler = new UIHandler(this);

	private static class UIHandler extends Handler {
		WeakReference<WatchListActivity> mActivity;

		UIHandler(WatchListActivity activity) {
			mActivity = new WeakReference<WatchListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final WatchListActivity theActivity = mActivity.get();
			switch (msg.what) {
			case SET_PERSON_INFO:// 1
				theActivity.setPersonInfo();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void initOnlineDataByQueryType(int queryType){
		//FIXME 屏蔽活动页面
//		((VideoListView)v3).queryType = queryType;
//		((VideoListView)v3).clearData();
//		((VideoListView)v3).initOnlineData();
		((VideoListView)v2).queryType = queryType;
		((VideoListView)v2).clearData();
		((VideoListView)v2).initOnlineData();
		((VideoListView)v1).queryType = queryType;
		((VideoListView)v1).clearData();
		((VideoListView)v1).initOnlineData();
	}

	@Override
	public void request_join_live(String account, String nick, String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void response_join_live(String account, String nick, String videoID,
			String videoTitle, String accept) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void private_bullet(String account, String msg, String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kick_off_video(String account, String nick, String videoID,
			String videoTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mute_video(String account, String nick, String videoID,
			String videoTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forbid_bullet(String videoID,String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void friend_status(String account, String type, String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invite_video(String account, String nick, String videoID,
			String videoTitle, String playurl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entrust_invite_video(String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comment_notify(String commenVideoID, String commentid,
			String account, String nick, String imageurl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focus_notify(int optType, int isSpecial, String account,
			String nick, String imageurl) {
		person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
		setPersonInfo();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
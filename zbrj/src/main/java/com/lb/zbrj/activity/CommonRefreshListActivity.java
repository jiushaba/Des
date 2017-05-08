package com.lb.zbrj.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lb.common.util.DateUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.adapter.AddChannelMngAdapter;
import com.lb.zbrj.adapter.UnAddChannelMngAdapter;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.ChannelData;
import com.lb.zbrj.data.db.ChannelHelper;
import com.lb.zbrj.view.AddChannelMngGridView;
import com.lb.zbrj.view.CollectVideoListView;
import com.lb.zbrj.view.UnAddChannelMngGridView;
import com.lb.zbrj.view.LiveVideoListView;
import com.lb.zbrj.view.VideoListView;
import com.lb.zbrj.view.ViewPagerPointer;
import com.lb.zbrj.view.WatchHistoryListView;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.adapter.MyPagerAdapter;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HorizontalIndicatorView;
import com.lz.oncon.widget.HorizontalIndicatorView.OnIndicatorChangeListener;

public abstract class CommonRefreshListActivity extends BaseActivity implements OnPageChangeListener {
	
	protected HorizontalIndicatorView channelsV;
	protected ImageView menuV;
	protected ImageView titleSearchV;
	protected LinearLayout mngLayout, channelMngLayout, actTypeLayout, mainMenuLL, subMenuLL;
	protected GridView addedV, unAddedV;
	protected ListView watchHistoryV;
	protected TextView actTypeV, leftTitleV, menuTypeV, todayV;
	protected ViewPager vPager;
	protected ViewPagerPointer pointer;
	protected ArrayList<View> list = new ArrayList<View>();
	protected View v1, v2;//, v3;FIXME 屏蔽活动页面
	protected VideoListView currentVideoListView;
	protected int currentVideoListViewIdx;

	protected ProgressDialog progressDialog;
	protected ArrayList<ChannelData> channelDatas = new ArrayList<ChannelData>();
	protected ArrayList<String> addChannels = new ArrayList<String>();
	protected ArrayList<String> unAddChannels = new ArrayList<String>();
	protected ArrayList<String> channelmngmenus = new ArrayList<String>();
	protected ArrayList<Object> watchHistorys = new ArrayList<Object>();
	
	/** 用户栏目/系统标签/最近使用标签的GRIDVIEW */
	protected AddChannelMngGridView userGridView;
	/** 其它栏目的GRIDVIEW */
	protected UnAddChannelMngGridView otherGridView;
	/** 用户栏目对应/系统标签/最近使用标签的适配器，可以拖动 */
	protected AddChannelMngAdapter userAdapter;
	/** 其它栏目对应的适配器 */
	protected UnAddChannelMngAdapter otherAdapter;
	/** 其它栏目列表 */
	protected ArrayList<ChannelData> otherChannelList = new ArrayList<ChannelData>();
	/** 用户栏目列表 */
	protected ArrayList<ChannelData> userChannelList = new ArrayList<ChannelData>();
	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */	
	boolean isMove = false;
	//观看历史
	protected WatchHistoryListView mWatchHistoryV;
	//收藏
	protected CollectVideoListView mCollectVideoV;
	
	protected ChannelHelper mChannelHelper;
	protected PersonController mPersonController;
	protected String menuType, menu;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initContentView();
		initViews();
		initController();
	}

	private void initContentView() {
		setContentView(R.layout.activity_watch_list);
	}
	
	private void initViews() {
		leftTitleV = (TextView) findViewById(R.id.title_left);
		titleSearchV = (ImageView) findViewById(R.id.title_search);
		channelsV = (HorizontalIndicatorView) findViewById(R.id.channels);
		channelsV.setColor(getResources().getColor(R.color.c9), getResources().getColor(R.color.c10));
		channelsV.mOnIndicatorChangeListener = new OnIndicatorChangeListener() {
			@Override
			public void onChange(int idx) {
				initOnlineDataByVideoType(userChannelList.get(idx).id);
			}
		};
		menuV = (ImageView)findViewById(R.id.menu);
		mngLayout = (LinearLayout)findViewById(R.id.mng_layout);
		channelMngLayout = (LinearLayout)findViewById(R.id.channel_mng_layout);
		actTypeLayout = (LinearLayout)findViewById(R.id.actType_layout);
		mainMenuLL = (LinearLayout)findViewById(R.id.main_menu_ll);
		subMenuLL = (LinearLayout)findViewById(R.id.sub_menu_ll);
		
		userGridView = (AddChannelMngGridView) findViewById(R.id.userGridView);
		otherGridView = (UnAddChannelMngGridView) findViewById(R.id.otherGridView);
		
		actTypeV = (TextView) findViewById(R.id.actType);
		vPager = (ViewPager) findViewById(R.id.vPager);
		pointer = (ViewPagerPointer) findViewById(R.id.vPager_pointer);
		menuTypeV = (TextView) findViewById(R.id.menuType);
		todayV = (TextView) findViewById(R.id.today);
		todayV.setText(DateUtil.getDateStringWithWeek());
		
		v1 = new LiveVideoListView(this);
		((VideoListView)v1).actType = VideoListView.ACTTYPE_LIVE;
		((VideoListView)v1).emptyText = getString(R.string.no_zb_memo);
		currentVideoListView = (VideoListView)v1;
		list.add(v1);
		v2 = new LiveVideoListView(this);
		((VideoListView)v2).actType = VideoListView.ACTTYPE_PLAYBACK;
		((VideoListView)v2).emptyText = getString(R.string.no_hf_memo);
		list.add(v2);
		//FIXME 屏蔽活动页面
//		v3 = new CompVideoListView(this);
//		((VideoListView)v3).actType = VideoListView.ACTTYPE_COMPETITION;
//		((VideoListView)v3).emptyText = getString(R.string.no_bs_memo);
//		list.add(v3);
		vPager.setAdapter(new MyPagerAdapter(list));
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(this);
		pointer.setPageCount(2);
		
		mWatchHistoryV = (WatchHistoryListView) findViewById(R.id.watch_history);
		mCollectVideoV = (CollectVideoListView) findViewById(R.id.collect_video);
	}

	public void initController() {
		mChannelHelper = new ChannelHelper(AccountData.getInstance().getUsername());
		mPersonController = new PersonController();
	}
	
	public abstract void requestOnlineData();

	public void showInputMethod(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService("input_method");
		if (inputMethodManager != null) {
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	public void closeInputMethod(View view) {
		if ((view != null) && (view.getContext() != null)) {
			InputMethodManager inputMethodManager = (InputMethodManager) view
					.getContext().getSystemService("input_method");
			if (inputMethodManager != null) {
				inputMethodManager.hideSoftInputFromWindow(
						view.getWindowToken(), 0);
			}
		}
	}
	
	/**
	 * 获取点击的Item的对应View，
	 * @param view
	 * @return
	 */
	protected ImageView getView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}
	
	/**
	 * 点击ITEM移动动画
	 * @param moveView
	 * @param startLocation
	 * @param endLocation
	 * @param moveChannel
	 * @param clickGridView
	 */
	protected void MoveAnim(View moveView, int[] startLocation,int[] endLocation, final ChannelData moveChannel,
			final GridView clickGridView) {
		int[] initLocation = new int[2];
		//获取传递过来的VIEW的坐标
		moveView.getLocationInWindow(initLocation);
		//得到要移动的VIEW,并放入对应的容器中
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
		//创建移动动画
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);//动画时间
		//动画配置
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				isMove = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				// instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
				if (clickGridView instanceof AddChannelMngGridView) {
					otherAdapter.setVisible(true);
					otherAdapter.notifyDataSetChanged();
					userAdapter.remove();
				}else{
					userAdapter.setVisible(true);
					userAdapter.notifyDataSetChanged();
					otherAdapter.remove();
				}
				isMove = false;
			}
		});
	}
	
	/**
	 * 获取移动的VIEW，放入对应ViewGroup布局容器
	 * @param viewGroup
	 * @param view
	 * @param initLocation
	 * @return
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}
	
	/**
	 * 创建移动的ITEM对应的ViewGroup布局容器
	 */
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			currentVideoListView = (VideoListView)v1;
			menu = getString(R.string.live);
		} else if (arg0 == 1) {
			currentVideoListView = (VideoListView)v2;
			menu = getString(R.string.playback);
		} else if (arg0 == 2) {
			//FIXME 屏蔽活动页面
//			currentVideoListView = (VideoListView)v3;
//			menu = getString(R.string.competition);
		}
		pointer.setCurrentPage(arg0);
		if(!TextUtils.isEmpty(menuType)){
			actTypeV.setText(menu + "-" + menuType);
		}else{
			actTypeV.setText(menu);
		}
		currentVideoListViewIdx = arg0;
	}
	
	private void initOnlineDataByVideoType(int videoType){
		//FIXME 屏蔽活动页面
//		((VideoListView)v3).videoType = videoType;
//		((VideoListView)v3).clearData();
//		((VideoListView)v3).initOnlineData();
		((VideoListView)v2).videoType = videoType;
		((VideoListView)v2).clearData();
		((VideoListView)v2).initOnlineData();
		((VideoListView)v1).videoType = videoType;
		((VideoListView)v1).clearData();
		((VideoListView)v1).initOnlineData();
	}
}
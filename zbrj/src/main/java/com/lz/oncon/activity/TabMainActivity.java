package com.lz.oncon.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TextView;

import com.lb.common.util.AnimationUtil;
import com.lb.common.util.ImageUtil;
import com.lb.common.util.Constants.ActivityState;
import com.lb.video.activity.RecordActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.activity.WatchListActivity;
import com.lz.oncon.app.im.data.IMNotification;
import com.lz.oncon.app.im.ui.IMListActivity;
import com.lz.oncon.receiver.OnNotiReceiver;
import com.lz.oncon.receiver.OnNotiReceiver.NotiListener;

public class TabMainActivity extends TabBaseActivity implements NotiListener {
	private TabHost mTabHost;
	private TabHost.TabSpec spec;
	private OnNotiReceiver _OnNotiReceiver_NewMsgRecved = null;
	LayoutParams wwlp, lp;
	String menu_tag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					//FIXME
//					UmengUpdateAgent.update(TabMainActivity.this);
				}
			}, 3 * 60 * 1000);
		} catch (Exception e) {
		}

		setContentView(R.layout.tab_main);
		mTabHost = getTabHost();
		Intent intent1 = new Intent(this, WatchListActivity.class);
		spec = mTabHost
				.newTabSpec("0")
				.setIndicator(createTabView(R.string.watch, R.drawable.home_radio_button_tab1))
				.setContent(intent1);
		mTabHost.addTab(spec);
		//FIXME 设置成第一个，防止嵌套
		//Intent intent2 = new Intent(this, RecordActivity.class);
		spec = mTabHost
				.newTabSpec("1")
				.setIndicator(createTabView(R.string.broadcasting, R.drawable.home_radio_button_tab2))
				.setContent(intent1);
		mTabHost.addTab(spec);

		Intent intent3 = new Intent(this, IMListActivity.class);
		spec = mTabHost
				.newTabSpec("2")
				.setIndicator(createTabView(R.string.message, R.drawable.home_radio_button_tab4))
				.setContent(intent3);
		mTabHost.addTab(spec);

		int h = ImageUtil.convertDipToPx(this, 20);
		int ww = ImageUtil.convertDipToPx(this, 30);
		wwlp = new LayoutParams(ww, h);
		lp = new LayoutParams(h, h);
		wwlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		wwlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		wwlp.topMargin = ImageUtil.convertDipToPx(this, 2);
		wwlp.rightMargin = ImageUtil.convertDipToPx(this, 10);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.topMargin = ImageUtil.convertDipToPx(this, 2);
		lp.rightMargin = ImageUtil.convertDipToPx(this, 15);

		initMainReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (_OnNotiReceiver_NewMsgRecved != null) {
			unregisterReceiver(_OnNotiReceiver_NewMsgRecved);
		}
	}

	@Override
	public void finishNoti(String action) {
		if (OnNotiReceiver.ONCON_IM_RECVNEWMSG.equals(action)) {
			initNewMsgRecvedNoti();
		}
	}

	public void initMainReceiver() {
		// 收到新消息
		initNewMsgRecvedNoti();
		_OnNotiReceiver_NewMsgRecved = new OnNotiReceiver();
		_OnNotiReceiver_NewMsgRecved.addNotiListener(OnNotiReceiver.ONCON_IM_RECVNEWMSG, this);
		IntentFilter intentFilter4 = new IntentFilter(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
		registerReceiver(_OnNotiReceiver_NewMsgRecved, intentFilter4);
	}

	private View createTabView(int textResId, int iconResId) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_main_indicator, null);
		TextView tv = (TextView) view.findViewById(R.id.tab_tv);
		tv.setText(textResId);
		ImageView iv = (ImageView) view.findViewById(R.id.tab_iv);
		//FIXME zlj开播图标扩大1.5倍
		/*if(textResId == R.string.broadcasting){
			android.view.ViewGroup.LayoutParams params = iv.getLayoutParams();
			params.height = (int)(params.height*1.5);
			params.width = (int)(params.width*1.5);
			iv.setLayoutParams(params);
		}*/
		iv.setImageResource(iconResId);
		if(textResId == R.string.broadcasting){
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(TabMainActivity.this, RecordActivity.class));
					mTabHost.setCurrentTab(0);
				}
			});
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(TabMainActivity.this, RecordActivity.class));
					mTabHost.setCurrentTab(0);
				}
			});
		}
		return view;
	}

	private void initNewMsgRecvedNoti() {
		IMNotification notification = IMNotification.getInstance();
		int noticationCount = notification.getAllNewMessageNoticationCount();
		TextView noti = (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_msg_noti);
		if (noticationCount > 0) {
			noti.setVisibility(View.VISIBLE);
//			if (noticationCount <= 99) {
//				noti.setText(noticationCount + "");
//				noti.setBackgroundResource(R.drawable.ic_numbg_0);
//				noti.setLayoutParams(lp);
//			} else {
//				noti.setText("99+");
//				noti.setBackgroundResource(R.drawable.ic_numbg_0);
//				noti.setLayoutParams(wwlp);
//			}
		} else {
			noti.setVisibility(View.GONE);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		try {
			if (getIntent().getExtras().get("ActivityWillSwitch") != null) {
				int currIdx = 0;
				if (ActivityState.EnterAddressBook == getIntent().getExtras()
						.get("ActivityWillSwitch")) {
					currIdx = 0;
				} else if (ActivityState.MessageCenter == getIntent()
						.getExtras().get("ActivityWillSwitch")) {
					currIdx = 1;
				} else if (ActivityState.AppCentre == getIntent().getExtras()
						.get("ActivityWillSwitch")) {
					currIdx = 2;
				} else if (ActivityState.More == getIntent().getExtras().get(
						"ActivityWillSwitch")) {
					currIdx = 3;
					mTabHost.setBackgroundResource(getResources().getColor(
							R.color.gray));
				}
				mTabHost.setCurrentTab(currIdx);
				getIntent().getExtras().remove("ActivityWillSwitch");
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onPause() {
		AnimationUtil.setLayout(R.anim.slide_in_right, R.anim.slide_out_left);
		if (AnimationUtil.ANIM_IN != 0 && AnimationUtil.ANIM_OUT != 0) {
			super.overridePendingTransition(AnimationUtil.ANIM_IN, AnimationUtil.ANIM_OUT);
			AnimationUtil.clear();
		}
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		
	}
}
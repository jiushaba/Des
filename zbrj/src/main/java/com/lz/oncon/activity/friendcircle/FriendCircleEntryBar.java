package com.lz.oncon.activity.friendcircle;

/**
 * 朋友圈入口
 * 好友新动态提示
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.widget.HeadImageView;

public class FriendCircleEntryBar extends LinearLayout {
	public RelativeLayout friendcircle_bar_root;
	public TextView friendcircle_bar_text_noti_fl;// 通知
	public FrameLayout friendcircle_bar_noti_fl;// 通知
	public com.lz.oncon.widget.HeadImageView friendcircle_bar_noti_avatar;// 头像通知
	private String mobile = "";
	private boolean isShow=false;
	public FriendCircleEntryBar(Context context) {
		super(context);
		init();
	}

	public FriendCircleEntryBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressLint("NewApi")
	public FriendCircleEntryBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.friendcircle_entry_bar, this);
		friendcircle_bar_root = (RelativeLayout) findViewById(R.id.friendcircle_bar_root);
		friendcircle_bar_text_noti_fl = (TextView) findViewById(R.id.friendcircle_bar_text_noti_fl);
		friendcircle_bar_noti_fl = (FrameLayout) findViewById(R.id.friendcircle_bar_noti_fl);
		friendcircle_bar_noti_avatar = (HeadImageView) findViewById(R.id.friendcircle_bar_noti_avatar);
	}

	
	public void setValue(Context context,boolean isShow,String mobile) {
		this.mobile = mobile;
		this.isShow = isShow;
		cwjHandler.post(mUpdateResults);
	}

	final Handler cwjHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateUI();
		}
	};

	private void updateUI() {
		if(!TextUtils.isEmpty(mobile)){
			friendcircle_bar_text_noti_fl.setVisibility(View.VISIBLE);
			friendcircle_bar_noti_fl.setVisibility(View.VISIBLE);
			friendcircle_bar_noti_avatar.setVisibility(View.VISIBLE);
			friendcircle_bar_noti_avatar.setMobile(mobile);
		}else if(isShow){
			friendcircle_bar_text_noti_fl.setVisibility(View.VISIBLE);
			friendcircle_bar_noti_fl.setVisibility(View.GONE);
			friendcircle_bar_noti_avatar.setVisibility(View.GONE);
		}else{
			friendcircle_bar_text_noti_fl.setVisibility(View.GONE);
			friendcircle_bar_noti_fl.setVisibility(View.GONE);
			friendcircle_bar_noti_avatar.setVisibility(View.GONE);
		}
	}
}
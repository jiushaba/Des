package com.lz.oncon.activity.friendcircle;

/**
 * 朋友圈页面
 * 好友新动态提示
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.widget.HeadImageView;

public class FriendCircleNotificationNum extends LinearLayout {
	public LinearLayout meaasge_notification_layout;
	private com.lz.oncon.widget.HeadImageView headerview_image;
	private TextView headerview_notification_num;// 通知
	private String msgStr = "";
	private String mobile = "";

	public FriendCircleNotificationNum(Context context) {
		super(context);
		init();
	}

	public FriendCircleNotificationNum(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressLint("NewApi")
	public FriendCircleNotificationNum(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.friendcircle_unreadview, this);
		meaasge_notification_layout = (LinearLayout) findViewById(R.id.meaasge_notification);
		headerview_image = (HeadImageView) findViewById(R.id.headerview_image);
		headerview_notification_num = (TextView) findViewById(R.id.headerview_notification_num);
	}

	public void setValue(Context context, String noti_num, String mobile) {
		if (!TextUtils.isEmpty(mobile)) {
			this.mobile = mobile;
			msgStr = context.getResources().getString(R.string.fc_notification_num, noti_num);
			// headerview_notification_num.setText(msgStr);
			cwjHandler.post(mUpdateResults); // 高速UI线程可以更新结果了
		}
	}

	final Handler cwjHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateUI();
		}
	};

	private void updateUI() {
		headerview_image.setMobile(mobile);
		headerview_notification_num.setText(msgStr);
	}

}
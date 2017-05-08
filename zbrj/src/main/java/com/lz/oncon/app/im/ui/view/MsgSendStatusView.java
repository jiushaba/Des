package com.lz.oncon.app.im.ui.view;

import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.SendStatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgSendStatusView extends LinearLayout{
	public boolean hasSendStatus = false;
	private SIXmppMessage d;
	private TextView memoV;
	private ImageView iconV;

	public MsgSendStatusView(Context context) {
		super(context);
		init();
	}
	
	public MsgSendStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public MsgSendStatusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.message_sendstatus, this);
		memoV = (TextView)findViewById(R.id.memo);
		iconV = (ImageView)findViewById(R.id.icon);
	}
	
	public void setMessage(final SIXmppMessage d){
		this.d = d;
		setMessage();
	}

	private void setMessage(){
		iconV.setVisibility(View.GONE);
		switch (d.getStatus()) {
		case STATUS_SENT:
			memoV.setText(R.string.send);
			memoV.setTextColor(getContext().getResources().getColor(R.color.im_send));
			hasSendStatus = true;
			break;
		case STATUS_ARRIVED:
			memoV.setText(R.string.msg_sendstatus_toserver);
			memoV.setTextColor(getContext().getResources().getColor(R.color.im_send));
			hasSendStatus = true;
			break;
		case STATUS_ERROR:
			memoV.setText(R.string.im_unarrived);
			memoV.setTextColor(Color.RED);
			hasSendStatus = true;
			break;
		case STATUS_READED:
			memoV.setText(R.string.msg_sendstatus_readed);
			memoV.setTextColor(getContext().getResources().getColor(R.color.im_send));
			hasSendStatus = true;
			break;
		default:
			break;
		}
	}
	
	public void refreshMessage(SendStatus sendStatus, String noread_count){
		d.setStatus(sendStatus);
		d.noread_count = noread_count;
		mUIHandler.sendEmptyMessage(REFRESH);
	}
	
	private UIHandler mUIHandler = new UIHandler();
	private static final int REFRESH = 0;

	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH:
				setMessage();
				break;
			default:
				break;
			}
		}
	}
}
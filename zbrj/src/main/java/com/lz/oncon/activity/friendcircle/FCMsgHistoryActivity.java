package com.lz.oncon.activity.friendcircle;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;

public class FCMsgHistoryActivity extends BaseActivity{
	
	protected MsgHistoryListView mListView;
	private TextView msgNumV;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initContentView();
		initViews();
		initData();
		initController();
	}

	private void initContentView() {
		setContentView(R.layout.fc_msg_history);
	}
	
	private void initViews() {
		mListView = (MsgHistoryListView)findViewById(R.id.msgList);
		msgNumV = (TextView)findViewById(R.id.msg_num);
		mListView.msgNumV = msgNumV; 
	}

	public void initController() {
	}
	
	public void initData(){
		mListView.initOnlineData();
	}
	
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch(arg0.getId()){
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}
}
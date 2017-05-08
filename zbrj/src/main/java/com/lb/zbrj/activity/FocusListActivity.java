package com.lb.zbrj.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.FocusListener;
import com.lb.zbrj.view.FocusListView;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class FocusListActivity extends BaseActivity implements FocusListener{
	
	protected FocusListView mFocusListView;
	private TextView focusNumV;
	private PersonController mPersonController;
	private String mobile = "";
	private PersonData person;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initContentView();
		initViews();
		initController();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().removeListener(Constants.LISTENER_FOCUS, this);
	}

	private void initContentView() {
		setContentView(R.layout.activity_focus_list);
	}
	
	private void initViews() {
		mFocusListView = (FocusListView)findViewById(R.id.focusList);
		focusNumV = (TextView) findViewById(R.id.focus_num);
	}

	public void initController() {
		mPersonController = new PersonController();
		MyApplication.getInstance().addListener(Constants.LISTENER_FOCUS, this);
	}
	
	public void initData(){
		mobile = getIntent().hasExtra("mobile") ? getIntent().getStringExtra("mobile") : AccountData.getInstance().getBindphonenumber();
		person = mPersonController.findPerson(mobile);
		mFocusListView.setMobile(mobile);
		mFocusListView.initOnlineData();
		focusNumV.setText(getString(R.string.friend_fmt1, person.focusNum));
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

	@Override
	protected void onResume() {
		super.onResume();
		if(needRefresh && mFocusListView != null){
			mFocusListView.initOnlineData();
		}
	}

	private boolean needRefresh = false;
	@Override
	public void syn(ArrayList<FansData> focus) {
		needRefresh = true;
	}
	
}
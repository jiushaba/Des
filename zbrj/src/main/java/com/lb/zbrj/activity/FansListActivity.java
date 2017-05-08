package com.lb.zbrj.activity;

import java.util.ArrayList;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.FocusListener;
import com.lb.zbrj.view.FansListView;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class FansListActivity extends BaseActivity implements FocusListener{
	
	protected FansListView mFansListView;
	private TextView fansNumV;
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
		setContentView(R.layout.activity_fans_list);
	}
	
	private void initViews() {
		mFansListView = (FansListView)findViewById(R.id.fansList);
		fansNumV = (TextView) findViewById(R.id.fans_num);
	}

	public void initController() {
		mPersonController = new PersonController();
		MyApplication.getInstance().addListener(Constants.LISTENER_FOCUS, this);
	}
	
	public void initData(){
		mobile = getIntent().hasExtra("mobile") ? getIntent().getStringExtra("mobile") : AccountData.getInstance().getBindphonenumber();
		person = mPersonController.findPerson(mobile);
		mFansListView.setMobile(mobile);
		mFansListView.initOnlineData();
		fansNumV.setText(getString(R.string.fans_fmt1, person.fansNum));
		needRefresh = true;
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
		if(needRefresh && mFansListView != null){
			mFansListView.initOnlineData();
		}
	}

	private boolean needRefresh = false;
	@Override
	public void syn(ArrayList<FansData> focus) {
		needRefresh = true;
	}
}
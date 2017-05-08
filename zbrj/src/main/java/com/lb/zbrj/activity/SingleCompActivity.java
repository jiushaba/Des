package com.lb.zbrj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lb.video.activity.RecordActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.CompData;
import com.lb.zbrj.view.SingleCompTopView;
import com.lb.zbrj.view.SingleCompView;
import com.lz.oncon.activity.BaseActivity;

public class SingleCompActivity extends BaseActivity{
	
	private TextView titleLeftV, titleCenterV, titleRightV;
	protected SingleCompView mSingleCompView;
	private SingleCompTopView mCompBroardView;
	private CompData data;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initContentView();
		initViews();
		initData();
		initController();
	}

	private void initContentView() {
		setContentView(R.layout.activity_single_comp);
	}
	
	private void initViews() {
		mSingleCompView = (SingleCompView)findViewById(R.id.videoList);
		mCompBroardView = (SingleCompTopView)findViewById(R.id.videoBroardList);
		titleLeftV = (TextView)findViewById(R.id.title_left);
		titleCenterV = (TextView)findViewById(R.id.title_center);
		titleRightV = (TextView)findViewById(R.id.title_right);
	}

	public void initController() {
		titleRightV.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(mSingleCompView.getVisibility() == View.VISIBLE){
					mSingleCompView.setVisibility(View.GONE);
					mCompBroardView.setVisibility(View.VISIBLE);
				}else{
					mSingleCompView.setVisibility(View.VISIBLE);
					mCompBroardView.setVisibility(View.GONE);
				}
			}
		});
	}
	
	public void initData(){
		data = (CompData)getIntent().getSerializableExtra("comp");
		mSingleCompView.setData(data);
		mCompBroardView.setData(data);
		titleLeftV.setText(getString(R.string.comp_num_fmt, data.compNum));
		titleCenterV.setText(getString(R.string.comp_name_fmt, data.compName) + data.compType);
		titleRightV.setText(R.string.billildboard);
		
		mSingleCompView.initOnlineData();
		mCompBroardView.initOnlineData();
	}
	
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch(arg0.getId()){
		case R.id.back:
			finish();
			break;
		case R.id.take_part_in:
			//FIXME 参加比赛
			Intent intent = new Intent(this, RecordActivity.class);
			intent.putExtra("compid", data.compid);
			startActivity(intent);
			break;
		}
	}
}
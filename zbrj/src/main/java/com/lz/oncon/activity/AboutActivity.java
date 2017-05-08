package com.lz.oncon.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuanbo.xuan.R;

public class AboutActivity extends BaseActivity {
	public static final String ACTION_APPUPDATE="com.lz.rhtx.action.APPUPDATE";
	TextView softname, companyname;
	ImageView logo;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView(R.layout.about);
		initController();
		initViews();
		setValues();
		setListeners();
	}

	public void initController() {
	}

	public void initViews() {
		softname = (TextView) findViewById(R.id.softname);
		companyname = (TextView) findViewById(R.id.companyname);
		logo = (ImageView) findViewById(R.id.logo);
	}

	public void setListeners() {
	}

	@SuppressLint("NewApi")
	public void setValues() {
	}
	
	@Override
	public void onClick(View v){
		super.onClick(v);
		Intent intent;
		if(v.getId() == R.id.common_title_TV_left){
			finish();
		}else if(v.getId() == R.id.help){
			/*intent = new Intent(this, HelpActivity.class);
			startActivity(intent);*/
		}else if(v.getId() == R.id.intro){
			/*intent = new Intent(this, UserGuideActivity.class);
			startActivity(intent);*/
		}
	}
}
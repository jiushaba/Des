package com.lz.oncon.activity;

import com.xuanbo.xuan.R;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {
	TextView tv1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
	}
	
	public void initContentView() {
		this.setContentView(R.layout.help);
	}

	public void initController() {
	}

	public void initViews() {
		tv1 = (TextView)this.findViewById(R.id.help_TV_1);
		tv1.setText(R.string.help_1);
		Linkify.addLinks(tv1, Linkify.ALL);
	}

	public void setListeners() {
	}

	public void setValues() {
	}
	
	public void onClick(View v){
		super.onClick(v);
		switch(v.getId()){
			case R.id.common_title_TV_left:
				finish();
				break;
			default:
				break;
		}
	}
}
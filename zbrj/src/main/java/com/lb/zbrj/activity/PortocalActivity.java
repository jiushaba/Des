package com.lb.zbrj.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lb.common.util.Log;
import com.lb.common.util.ResourceUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;

public class PortocalActivity extends BaseActivity {
	TextView displayTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();		
		loadTxtDisplay();
	}

	private void loadTxtDisplay() {
		try {
			displayTextView.setText(Html.fromHtml(ResourceUtil.readRaw(R.raw.protocol_cn)));
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		
	}

	private void initView() {
		setContentView(R.layout.activity_portocal);
		findViewById(R.id.common_title_TV_left_iv).setOnClickListener(this);
		displayTextView = (TextView) findViewById(R.id.display_txt);
	}
	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.common_title_TV_left_iv:
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}

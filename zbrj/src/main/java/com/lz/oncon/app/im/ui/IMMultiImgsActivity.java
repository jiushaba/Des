package com.lz.oncon.app.im.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.widget.ScrollLayout;

public class IMMultiImgsActivity extends BaseActivity implements ScrollLayout.PageListener{
	HorizontalScrollView hsv;
	LinearLayout ll;
	ScrollLayout curPage; // 自定义的左右滑动
	
	List<Bitmap> bitmaps = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initViews();
		setValues();
		setListeners();
	}

	public void initContentView() {
		setContentView(R.layout.app_im_multiimgs);
	}

	public void initViews() {
		hsv = (HorizontalScrollView) findViewById(R.id.app_im_multiimgs_HSV);
		ll = (LinearLayout)findViewById(R.id.app_im_multiimgs_LL);
		curPage = (ScrollLayout) findViewById(R.id.scrolayout);
	}

	public void setValues() {
	}

	public void setListeners() {
		curPage.setPageListener(this);
	}

	private static final int MESSAGE_GET_APP_CLASS_SUCCESS = 1;
	private static final int MESSAGE_INIT_APP_SUCCESS = 3;
	private static final int MESSAGE_GET_APP_FAIL = 4;
	private static final int MESSAGE_GET_APP_SUCCESS = 5;
	private static final int UPDATE_APP_STATUS = 6;
	
	private UIHandler mUIHandler = new UIHandler();

	private class UIHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_INIT_APP_SUCCESS:
				break;
			case MESSAGE_GET_APP_FAIL:
				break;
			case MESSAGE_GET_APP_SUCCESS:
				break;
			case MESSAGE_GET_APP_CLASS_SUCCESS:
				break;
			case UPDATE_APP_STATUS:
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.common_title_TV_left:
				finish();
				break;
			case R.id.app_im_multiimgs_btn:
				break;
			default:
				break;
		}
	}

	@Override
	public void page(View v, int page) {
//		hsv.smoothScrollTo(tabW * page, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
    }
}

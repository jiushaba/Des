package com.lz.oncon.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsoluteLayout;

import com.lb.common.util.ImageLoader;
import com.lb.zbrj.data.PersonData;
import com.lz.oncon.app.im.ui.TouchView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.xuanbo.xuan.R;

@SuppressWarnings("deprecation")
public class HeadBigActivity extends Activity implements OnClickListener{
	TouchView icon;
	private DisplayMetrics dm = new DisplayMetrics();
	private int screen_width, screen_height;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		dm = getResources().getDisplayMetrics();
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.headicon_big); 
		
		icon = (TouchView) findViewById(R.id.head_icon);
		icon.setCanDisappear(true);
		
		Intent i = getIntent();
		PersonData person = (PersonData)i.getExtras().getSerializable("data");
		ImageLoader.displayHeadImage(person.image, icon, new com.nostra13.universalimageloader.core.listener.ImageLoadingListener(){
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				icon.setImageResource(R.drawable.avatar_img_loading);
				setLayoutParams(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_img_loading));
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if(loadedImage != null){
					icon.setImageBitmap(loadedImage);
					setLayoutParams(loadedImage);
				}else{
					icon.setImageResource(R.drawable.avatar_img_loading);
					setLayoutParams(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_img_loading));
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				icon.setImageResource(R.drawable.avatar_img_loading);
				setLayoutParams(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_img_loading));
			}
		});
	}
	
	private void setLayoutParams(Bitmap b){
		int	bmw = b.getWidth();
		int bmh = b.getHeight();
		int l, t, w = AbsoluteLayout.LayoutParams.MATCH_PARENT, h = AbsoluteLayout.LayoutParams.MATCH_PARENT;
		float sw = (float)bmw / (float)screen_width;
		float sh = (float)bmh / (float)screen_height;
		float s = sw >= sh ? sw : sh;
		s = s <= 0 ? 1 : s;
		w = (int)((float)bmw / s);
		h = (int)((float)bmh / s);
		l = (screen_width - w)/2;
		t = (screen_height - h)/2;
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(w, h, l, t);
		icon.setLayoutParams(params);
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		//FIXME 错误上报
//		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//FIXME 错误上报
//		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.head_icon:
				finish();
				break;
			case R.id.head_big_RL:
				finish();
				break;
		}
	}
	
	@Override
	public void finish(){
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left_top);
	}
}

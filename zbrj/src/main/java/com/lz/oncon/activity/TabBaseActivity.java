package com.lz.oncon.activity;

import com.xuanbo.xuan.R;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
/**
 * Activity继承该BaseActivity可实现底部按钮事件
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class TabBaseActivity extends TabActivity implements OnClickListener{

	public static final String MAIN_ACTIVITY_EXIT_ACTION = "com.exit.app";
//	private IMConnErrReceiver _IMConnErrReceiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initReceiver();
		
		MyApplication.getInstance().mActivityManager.pushActivity(this);
	}
	
	private static boolean b = false;
	
	/**
	 * 关闭应用的广播接收器
	 */
	private BroadcastReceiver broadCastReceive = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (MAIN_ACTIVITY_EXIT_ACTION.equals(arg1.getAction())) {
				b = true;
				finish();
			}
		}
	};
	
	/**
	 * 注册广播接收器
	 */
	public void initReceiver() {
		IntentFilter intentFilter = new IntentFilter(MAIN_ACTIVITY_EXIT_ACTION);
		registerReceiver(broadCastReceive, intentFilter); // 将BroadCastReceiver注册到系统当中
		
//		_IMConnErrReceiver = new IMConnErrReceiver(this);
//		IntentFilter intentFilter3 = new IntentFilter(Constants.BROAD_ONCON_IM_CONN_ERR);
//		registerReceiver(_IMConnErrReceiver, intentFilter3);
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
	protected void onDestroy() {
		super.onDestroy();
		if(broadCastReceive != null){
			unregisterReceiver(broadCastReceive);
		}
		MyApplication.getInstance().mActivityManager.popActivity(this);
		if(b){
			new Thread(){
				public void run(){
					MyApplication.getInstance().mActivityManager.popAllActivity();
					AppUtil.killApp();
				}
			}.start();
		}
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
//		Toast.makeText(this, R.string.low_memory, Toast.LENGTH_SHORT).show();
		System.gc();
	}
	
	public void toastToMessage(int resId){
		Toast.makeText(TabBaseActivity.this, resId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode){
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	@Override
	public void finish(){
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}

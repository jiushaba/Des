package com.lz.oncon.activity;

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lb.common.util.DeviceUtils;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.receiver.OnNotiReceiver.NotiListener;
import com.lz.oncon.receiver.ScreenOrHomeReceiver;
import com.lz.oncon.widget.InfoProgressDialog;
import com.lz.oncon.widget.InfoToast;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.OauthHelper;
import com.xuanbo.xuan.R;
/**
 * Activity继承该BaseActivity可实现底部按钮事件
 * @author Administrator
 *
 */
public class BaseActivity extends Activity implements OnClickListener, NotiListener{

	public static final String MAIN_ACTIVITY_EXIT_ACTION = "com.exit.app";
	public InfoProgressDialog progressDialog;
	private BroadcastReceiver screenReceiver = new ScreenOrHomeReceiver();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog = new InfoProgressDialog(this);
		initReceiver();
		MyApplication.getInstance().mActivityManager.pushActivity(this);
		
		if (screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
		}
	}


	/**
	 * 点击底部按钮事件
	 * @param arg0
	 */
	@Override
	public void onClick(View arg0) {
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
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		screenReceiver = new ScreenOrHomeReceiver();
		registerReceiver(screenReceiver, filter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//FIXME 错误上报
//		MobclickAgent.onResume(this);
        MyApplication.getInstance().beTopTime = Calendar.getInstance().getTime();
        MyApplication.getInstance().isTopActivity = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//FIXME 错误上报
//		MobclickAgent.onPause(this);
		// when the screen is about to turn off
        if (ScreenOrHomeReceiver.wasScreenOn) {
            // this is the case when onPause() is called by the system due to a screen state change
            MyApplication.getInstance().isTopActivity = false;
        } else {
            // this is when onPause() is called when the screen state has not changed
        }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
		if(broadCastReceive != null){
			unregisterReceiver(broadCastReceive);
		}
		if(screenReceiver != null)unregisterReceiver(screenReceiver);
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
		InfoToast.makeText(this
				, getString(resId)
				, Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
	}
	
	public void toastToMessage(String s){
		InfoToast.makeText(this
				, s
				, Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void finishNoti(String action) {
	}
	
	
	public void removeWeiboAuthors(){
		if(OauthHelper.isAuthenticatedAndTokenNotExpired(BaseActivity.this, SHARE_MEDIA.SINA)){
			OauthHelper.remove(BaseActivity.this, SHARE_MEDIA.SINA);
		}
		if(OauthHelper.isAuthenticatedAndTokenNotExpired(BaseActivity.this, SHARE_MEDIA.TENCENT)){
			OauthHelper.remove(BaseActivity.this, SHARE_MEDIA.TENCENT);
		}
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
	public void oldfinish(){
		super.finish();
	}
	public void initContentView(int id){
		setContentView(id);
	}
	
	public void hideKeyboard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && editText != null) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}
	
	public void showProgressDialog(int msgResId, boolean cancelable){
		if(progressDialog != null && !progressDialog.isShowing()){
			progressDialog.setCancelable(cancelable);
			progressDialog.setMessage(msgResId);
			progressDialog.show();
		}
	}
	
	public void hideProgressDialog(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * show or hide popupwindow
	 */
	private static PopupWindow popupWindow;
	public static boolean flag = false;
	public static int screenWidth = 0;
	public static int screenHeight = 0;
	private void openPopupwin(int parentId) {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.popupwindow, null);
		popupWindow = new PopupWindow(menuView, screenWidth, screenHeight);
		try {
			popupWindow.showAtLocation(findViewById(parentId), Gravity.CENTER,
					0, 0);
			popupWindow.update();
		} catch (Exception e) {
			closePopupwins();
		}
	}

	private final void closePopupwins() {
		if (popupWindow != null && popupWindow.isShowing()) {
			try {
				popupWindow.dismiss();
			} catch (NullPointerException e) {
			} catch (IllegalArgumentException e) {
			}
		}

		if (mThread != null) {
			mThread.interrupt();
			bRun = false;
		}
	}
	
	private static String latOutId;
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				latOutId = String.valueOf(msg.obj);
				mHandler.post(mThread);
				break;
			case 1:
				closePopupwins();
				break;
			default:
				break;
			}
		}
	};
	static boolean bRun = false;
	private final Thread mThread = new Thread() {

		public void run() {
			mHandler.removeCallbacks(mThread);
			if (!bRun) {
				bRun = true;
				openPopupwin(Integer.parseInt(latOutId));
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	public void startPro(int layOutId) {
		flag = true;
		mHandler.obtainMessage(0, layOutId).sendToTarget();
	}

	public void stopPro(long seconedMills) {
		flag = false;
		Message msg = mHandler.obtainMessage(1);
		mHandler.sendMessageDelayed(msg, seconedMills);
	}
	
	public static String getLang() {
		boolean l = DeviceUtils.isZh(MyApplication.getInstance());
		if (l) {
			return "zh";
		}else {
			return "en";
		}
	}
	
	protected void go2MainActivity() {
		showProgressDialog(R.string.wait, false);
		new Thread(){
			public void run(){
				AppUtil.loginIM();
				BaseActivity.this.runOnUiThread(new Runnable(){
					public void run(){
						Intent intent = AppUtil.getMainActIntent(BaseActivity.this);
				 		startActivity(intent);
				 		hideProgressDialog();
						finish();
					}
				});
			}
		}.start();
	}
}
package com.lz.oncon.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.lb.common.util.Log;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.lb.common.util.Constants;
import com.umeng.socialize.common.SocializeConstants;
import com.xuanbo.xuan.R;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.controller.AccountController.SyncListener;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.AccountDataStruct;

public class LoadingActivity extends BaseActivity implements SyncListener {
	public BaseController mController;
	private AccountData acc;
	private ImageView mLoadingImage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SocializeConstants.SHOW_ERROR_CODE = true;
		SocializeConstants.DEBUG_MODE = true;
		MyApplication.getInstance().mActivityManager.popAllActivityExceptOne(LoadingActivity.class);
		try {
			//FIXME 错误上报
//			MobclickAgent.setDebugMode(false);
//			MobclickAgent.setAutoLocation(false);
//			MobclickAgent.onError(this);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
		if (acc != null) {
			new Handler().post(new LoadRunable());
		}
	}

	public void initContentView() {
		this.setContentView(R.layout.loading);
	}

	public void initController() {
		this.mController = new AccountController(this);
	}

	public void initViews() {
		mLoadingImage = (ImageView) findViewById(R.id.loading_image);
//		try {
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_login);
//			BitmapDrawable drawable = new BitmapDrawable(MyApplication.getInstance().getResources(), bitmap);
//			drawable.setTileModeX(TileMode.REPEAT);
//			drawable.setDither(true);
//			mLoadingImage.setBackgroundDrawable(drawable);
////			rootL.setBackgroundDrawable(getResources().getDrawable(
////					R.drawable.bg_login));
//		} catch (Exception e) {
//			Log.e(Constants.LOG_TAG, e.getMessage(), e);
//		}
	}

	public void setListeners() {
		((AccountController) this.mController).setSyncListener(this);
	}

	public void setValues() {
//		String bg = MyApplication.getInstance().mPreferencesMan.getBgFileName();
//		if(!TextUtils.isEmpty(bg)){
//			try {
//				Bitmap b = ImageThumbUtil.getInstance().loadImageFromFile(((AccountController)this.mController).getLocalBgDir().getAbsolutePath() + File.separator + bg);
//				if(b == null){
//					mLoadingImage.setImageDrawable(getResources().getDrawable(R.drawable.bg_load));
//				}else{
//					mLoadingImage.setImageBitmap(b);
//				}
//			} catch (Exception e) {
//				Log.e(Constants.LOG_TAG, e.getMessage(), e);
//			}
//		}else{
//			try{
//				mLoadingImage.setImageDrawable(getResources().getDrawable(R.drawable.bg_load));
//			}catch(Exception e){}
//		}

		//如果第一次启动app，显示新手引导
//		if(SettingInfoData.getInstance().isFirstLoad()){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				Log.e(Constants.LOG_TAG, e.getMessage(), e);
//			}
//			Intent intent = new Intent(LoadingActivity.this, UserGuideActivity.class);
//			intent.putExtra("nextActivity", AppUtil.getLoginActivityName());
//			deliverExtras(intent);
//			startActivity(intent);
//			finish();
//			return;
//		}
		if(TextUtils.isEmpty(AccountData.getInstance().getLastUsername())){
			//无用户信息则跳转至登录界面
			new Thread(){
				public void run(){
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						Log.e(Constants.LOG_TAG, e.getMessage(), e);
					}
					go2LoginActivity();
				}
			}.start();
		}else{
			List<AccountDataStruct> accList = ((AccountController)LoadingActivity.this.mController).findRecentLogin();
			if(accList == null || accList.size() == 0){
				//无用户信息则跳转至登录界面
				new Thread(){
					public void run(){
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							Log.e(Constants.LOG_TAG, e.getMessage(), e);
						}
						go2LoginActivity();
					}
				}.start();
			}else{
				acc = AccountData.getInstance();
				acc.copy(accList.get(0));
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (mController != null)
			mController.onDestroy();
	}

	private void go2LoginActivity() {
		Intent intent = AppUtil.getLoginActIntent(this);
		this.startActivity(intent);
		this.finish();
	}

	private static final int MESSAGE_LOGIN_FAIL = 1;
	private static final int MESSAGE_LOGIN_SUCCESS = 3;
	private static final int MESSAGE_LOGIN_NETERROR = 5;

	
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_LOGIN_FAIL:
				String message = (String)msg.obj;
				toastToMessage(TextUtils.isEmpty(message)?
						getString(R.string.login) +" "+ getString(R.string.fail)
						: message);
				go2LoginActivity();
				break;
			case MESSAGE_LOGIN_SUCCESS:
				go2MainActivity();
				break;
			case MESSAGE_LOGIN_NETERROR:
				go2MainActivity();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	class LoadRunable implements Runnable {
		public void run() {
			// 登录
			((AccountController) LoadingActivity.this.mController).login(acc, false, 1 * 1000);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return false;
	}

	@Override
	public void onLogined(String status, String message, AccountData acc) {
		Message msg = new Message();
		if (Constants.RES_SUCCESS.equals(status)) {
			msg.what = MESSAGE_LOGIN_SUCCESS;
		} else if(Constants.RES_NET_ERROR.equals(status)){
			msg.what = MESSAGE_LOGIN_NETERROR;
		} else {
			msg.what = MESSAGE_LOGIN_FAIL;
			msg.obj = message;
		}
		mUIHandler.sendMessage(msg);
	}

	@Override
	public void onCheckContactsed(String status, String sendnum, String filesize, String filedir) {
	}
}
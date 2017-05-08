package com.lz.oncon.activity;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.lb.common.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.controller.AccountController.SyncListener;
import com.lz.oncon.data.AccountData;

public class RegisterActivity2 extends BaseActivity implements SyncListener {

	private BaseController mController;
	private TextView mobileTV;
	private EditText checkCodeET;
	private TextView resendTV;
	private TextView resendMemoTV;
	private AlertDialog failDialog, unRegDialog, regDialog, cancelDialog;
	private String coutryCode, mobile, ccode, finalMobile, password;
	private AccountData acc;
	private boolean willDirectLogin = false, isFirstReg = false, isfind = false;
	private SmsObserver mSMSObserver;
	private boolean isRequestReg = false;//防止重复注册

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent() != null) {
			isfind = getIntent().getBooleanExtra("isfind", false);
		}
		isRequestReg = false;
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
		registerSMSObserver();		
	}

	public void initContentView() {
		this.setContentView(R.layout.register2);
	}

	public void initController() {
		mController = new AccountController(this);
	}

	private void registerSMSObserver() {
		mSMSObserver = new SmsObserver (new Handler());
		// 注册短信变化监听
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, mSMSObserver);
	}

	class SmsObserver  extends ContentObserver {
		private Cursor cursor = null;

		public SmsObserver (Handler handler) {
			super(handler);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);		
				cursor = managedQuery(Uri.parse("content://sms/inbox"),
				new String[] { "_id", "address", "body" },
				null,
				null, "date desc");

				if (cursor != null && cursor.getCount() > 0 ) {
					cursor.moveToFirst();
					String body = cursor.getString(cursor.getColumnIndex("body"));	
					String verCode = getDynamicPassword(body);
					if(!TextUtils.isEmpty(verCode) && !isRequestReg){
						isRequestReg = true;
						getContentResolver().unregisterContentObserver(mSMSObserver);
						checkCodeET.setText(verCode);
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								nextStep();
							}
						}, 500);
					}
					
				}
				if (Build.VERSION.SDK_INT < 14 && cursor != null) {
			        cursor.close();
			      }			
		}
	}

	/**
	   * 从字符串中截取连续4位数字组合 ([0-9]{" + 4 + "})截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
	   * @param str
	   *            短信内容
	   * @return 截取得到的4位动态密码
	   */
	public static String getDynamicPassword(String str) {
		Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{" + 4 + "})(?![0-9])");
		Matcher m = continuousNumberPattern.matcher(str);
		String dynamicPassword = "";
	    while (m.find()) {
	    	dynamicPassword = m.group();
	    }
	    return dynamicPassword;
	}
	
	public void initViews() {
		mobileTV = (TextView) this.findViewById(R.id.mobile_TV);
		checkCodeET = (EditText) this.findViewById(R.id.check_code_ET);
		resendTV = (TextView) this.findViewById(R.id.resend);
		resendMemoTV = (TextView) this.findViewById(R.id.resend_memo);

		failDialog = new AlertDialog.Builder(this)
				.setMessage(R.string.verifycode_expire)
				.setPositiveButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						}).create();

		unRegDialog = new AlertDialog.Builder(this)
				.setPositiveButton(R.string.good,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								willDirectLogin = false;
								isFirstReg = true;
								login();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {

								dialog.dismiss();
							}
						}).create();

		regDialog = new AlertDialog.Builder(this)
				.setPositiveButton(R.string.continue_reg,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								willDirectLogin = false;
								isFirstReg = true; //为了重复注册以后回来可以方便的设置密码
								login();
							}
						})
				.setNeutralButton(R.string.direct_login,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								willDirectLogin = true;
								isFirstReg = true; //为了重复注册以后回来可以方便的设置密码
								login();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						}).create();

		cancelDialog = new AlertDialog.Builder(this)
				.setMessage(R.string.cancel_and_return)
				.setPositiveButton(R.string.waitfor,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.bak,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								RegisterActivity2.this.finish();
							}
						}).create();
	}

	public void setListeners() {
		((AccountController) mController).setSyncListener(this);
	}

	public void setValues() {
		coutryCode = getIntent().getStringExtra("coutryCode");
		mobile = getIntent().getStringExtra("mobile");
		mobileTV.setText(coutryCode + " " + mobile);
		ccode = coutryCode.replace("+", "00");
		finalMobile = "0086".equals(ccode) ? mobile : ccode + mobile;
		new Thread(new ResendThread()).start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			cancel();
			break;
		case R.id.common_title_TV_right:
			nextStep();
			break;
		case R.id.resend:
			resendCheckCode();
			break;
		default:
			break;
		}
	}
	
	private void nextStep(){
		String checkCode = checkCodeET.getText().toString().trim();
		password = ((TextView)findViewById(R.id.password_ET)).getText().toString().trim();
		if (TextUtils.isEmpty(checkCode)) {
			toastToMessage(R.string.please_enter_verifycode);
		} else if(TextUtils.isEmpty(password)){
			toastToMessage(R.string.login_input_pwdhint);
		} else {
			if (isfind) {
				((AccountController) mController).findpwd(ccode + mobile, checkCode);
			} else {
				new NetIFUI_ZBRJ(RegisterActivity2.this, new NetInterfaceListener() {
					@Override
					public void finish(NetInterfaceStatusDataStruct niStatusData) {
						mUIHandler.obtainMessage(MSG_REGISTER_MOBILE, niStatusData).sendToTarget();
					}
				}).m1_reg(finalMobile, password, checkCode);
			}
		}
	}
	

	private void login() {
		acc = AccountData.getInstance();
		acc.clearCurrAcc();
		acc.setUsername(finalMobile);
		acc.setPassword(password);
		acc.setBindphonenumber(acc.getUsername());
		((AccountController) mController).login(acc, willDirectLogin, 30 * 1000);
	}

	private void go2Register3() {
		Intent intent = new Intent(RegisterActivity2.this,
				RegisterActivity3.class);
		startActivity(intent);
	}

	private static final int MESSAGE_LOGIN_FAIL = 1;
	private static final int MESSAGE_LOGIN_SUCCESS = 3;
	private static final int MESSAGE_LOGIN_NETERROR = 5;
	private static final int MSG_UPD_RESEND_SECOND = 8;
	private static final int MSG_REGISTER_MOBILE = 9;
	private static final int MSG_SEND_VERIFY_CODE = 10;

	private UIHandler mUIHandler = new UIHandler(this);

	private static class UIHandler extends Handler {
		WeakReference<RegisterActivity2> mActivity;

		UIHandler(RegisterActivity2 activity) {
			mActivity = new WeakReference<RegisterActivity2>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			try {
				RegisterActivity2 theActivity = mActivity.get();
				NetInterfaceStatusDataStruct niStatusData;
				switch (msg.what) {
				case MESSAGE_LOGIN_FAIL:// 1
					String message = (String) msg.obj;
					theActivity
							.toastToMessage(TextUtils.isEmpty(message) ? theActivity
									.getString(R.string.login)
									+ " "
									+ theActivity.getString(R.string.fail)
									: message);
					break;
				case MESSAGE_LOGIN_SUCCESS:// 3
					if (theActivity.isFirstReg) {
						MyApplication.getInstance().mPreferencesMan
								.setFirstReg(true);
					}
					if (theActivity.willDirectLogin) {
						theActivity.go2MainActivity();
					} else {
						theActivity.go2Register3();
					}
					break;
				case MESSAGE_LOGIN_NETERROR:// 5
					if (theActivity.willDirectLogin) {
						theActivity.go2MainActivity();
					} else {
						theActivity.go2Register3();
					}
					break;
				case MSG_UPD_RESEND_SECOND:
					if (msg.arg1 > 0) {
						theActivity.resendMemoTV.setText(theActivity.getString(R.string.receive_sms_about_time, msg.arg1));
						theActivity.resendMemoTV.setVisibility(View.VISIBLE);
						theActivity.resendTV.setVisibility(View.GONE);
					} else {
						theActivity.getContentResolver().unregisterContentObserver(theActivity.mSMSObserver);
						theActivity.resendMemoTV.setVisibility(View.GONE);
						theActivity.resendTV.setVisibility(View.VISIBLE);
					}
					break;
				case MSG_REGISTER_MOBILE:
					niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
					if ("0".equals(niStatusData.getStatus())) {
						if (!theActivity.unRegDialog.isShowing()) {
							theActivity.unRegDialog.setMessage(theActivity
									.getString(R.string.not_reg_memo));
							theActivity.unRegDialog.show();
						}
					} else if ("5".equals(niStatusData.getStatus())) {// 已注册
						if (!theActivity.regDialog.isShowing()) {
							theActivity.regDialog.setMessage(theActivity
									.getString(R.string.reg_memo));
							theActivity.regDialog.show();
						}
					} else {// 失败
						if (!theActivity.failDialog.isShowing()) {
							if(!TextUtils.isEmpty(niStatusData.getMessage())){
								theActivity.failDialog.setMessage(niStatusData.getMessage());
							}
							theActivity.failDialog.show();
						}
					}
					break;
				case MSG_SEND_VERIFY_CODE:
					niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
					if ("0".equals(niStatusData.getStatus())) {
						new Thread(theActivity.new ResendThread()).start();
					} else {
						theActivity.toastToMessage(niStatusData.getMessage());
					}
					break;
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
	}

	class ResendThread implements Runnable {
		public void run() {
			for (int i = 60; i >= 0; i--) {
				mUIHandler.obtainMessage(MSG_UPD_RESEND_SECOND, i, 0)
						.sendToTarget();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	public void onLogined(String status,  String message, AccountData acc) {
		Message msg = Message.obtain();
		if (Constants.RES_SUCCESS.equals(status)) {
			msg.what = MESSAGE_LOGIN_SUCCESS;
		} else if (Constants.RES_NET_ERROR.equals(status)) {
			msg.what = MESSAGE_LOGIN_NETERROR;
		} else {
			msg.what = MESSAGE_LOGIN_FAIL;
			msg.obj = message;			
		}
		mUIHandler.sendMessage(msg);
	}

	@Override
	public void onCheckContactsed(String status, String sendnum,
			String filesize, String filedir) {
	}

	private void resendCheckCode() {
		registerSMSObserver();
		new NetIFUI_ZBRJ(RegisterActivity2.this, new NetInterfaceListener() {
			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				mUIHandler.obtainMessage(MSG_SEND_VERIFY_CODE, niStatusData).sendToTarget();
			}
		}).m1_get_verify(ccode.equals("0086") ? mobile : ccode + mobile);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void cancel() {
		if (!cancelDialog.isShowing()) {
			cancelDialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.getContentResolver().unregisterContentObserver(mSMSObserver);		
	}			
}
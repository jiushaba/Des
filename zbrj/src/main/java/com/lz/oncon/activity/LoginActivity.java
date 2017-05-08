package com.lz.oncon.activity;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.lb.common.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.activity.PortocalActivity;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.controller.AccountController.SyncListener;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.AccountDataStruct;
import com.lz.oncon.widget.EditableSpinner;

public class LoginActivity extends BaseActivity implements SyncListener{
	private BaseController mController;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter adapter;
	private TextView tvLogin;

	private EditableSpinner esUsername;
	private EditText etPassword;
	private List<AccountDataStruct> accList;
	private AccountData acc;
	private RelativeLayout rootL;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().mActivityManager
				.popAllActivityExceptOne(LoginActivity.class);

		initContentView(R.layout.login);
		initController();
		initViews();
		setValues();
		setListeners();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		String username = getIntent().getStringExtra(Constants.KW_USERNAME);
		String password = getIntent().getStringExtra(Constants.KW_PASSWORD);
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			esUsername.setValue(username);
			etPassword.setText(password);
			tvLogin.performClick();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@SuppressWarnings("rawtypes")
	public void initController() {
		mController = new AccountController(this);
		adapter = new ArrayAdapter(this, R.layout.editable_spinner_listitem);
	}

	@SuppressWarnings("deprecation")
	public void initViews() {
		rootL = (RelativeLayout) findViewById(R.id.root);
//		try {
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_login);
//			BitmapDrawable drawable = new BitmapDrawable(bitmap);
//			drawable.setTileModeX(TileMode.REPEAT);
//			drawable.setDither(true);
//			rootL.setBackgroundDrawable(drawable);
//		} catch (Exception e) {
//			Log.e(Constants.LOG_TAG, e.getMessage(), e);
//		}
		tvLogin = (TextView) this.findViewById(R.id.login_TV_Btn_login);
		esUsername = (EditableSpinner) this
				.findViewById(R.id.login_ES_username);
		etPassword = (EditText) this.findViewById(R.id.login_ET_password);
	}

	public void weiboLogin(String nnumber, String pwd) {
		acc = AccountData.getInstance();
		acc.setUsername(nnumber.trim());
		acc.setPassword(pwd.trim());
		((AccountController) mController).login(acc, true, 30 * 1000);
	}

	public void setListeners() {
		((AccountController) mController).setSyncListener(this);
	}

	@SuppressWarnings("unchecked")
	public void setValues() {
		acc = AccountData.getInstance();
		accList = ((AccountController) mController).findRecentLogin();
		adapter.clear();
		if (accList != null && accList.size() > 0) {
			for (AccountDataStruct acc : accList) {
				adapter.add(acc.getUsername());
			}
		}
		adapter.notifyDataSetChanged();
		esUsername.setAdapter(adapter);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mController != null)
			mController.onDestroy();		
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.exit)
					.setMessage(R.string.dialog_message)
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									Message message = Message.obtain();
									message.what = EXIT_APP;
									mUIHandler.sendMessage(message);
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
								}
							}).show();
		}
		return false;
	}

	private static final int MESSAGE_LOGIN_FAIL = 1;
	private static final int MESSAGE_LOGIN_SUCCESS = 3;
	private static final int MESSAGE_LOGIN_NETERROR = 5;
	private static final int EXIT_APP = 7;
	UIHandler mUIHandler = new UIHandler(this);

	private static class UIHandler extends Handler {
		WeakReference<LoginActivity> mActivity;

		UIHandler(LoginActivity activity) {
			mActivity = new WeakReference<LoginActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final LoginActivity theActivity = mActivity.get();
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
				theActivity.go2MainActivity();
				break;
			case MESSAGE_LOGIN_NETERROR:// 5
				theActivity.go2MainActivity();
				break;
			case EXIT_APP:// 7
				try {
					theActivity.showProgressDialog(R.string.exiting, false);
					new Thread(new Runnable() {
						public void run() {
							try {
								AppUtil.stopAllServices();
								if (theActivity.acc != null)
									theActivity.acc.clearCurrAcc();
								AppUtil.exitIM();
								AppUtil.cancelNotis();
								AppUtil.closeDB();
							} catch (Exception e) {
								Log.e(Constants.LOG_TAG, e.getMessage(), e);
							} finally {
								theActivity.hideProgressDialog();
								Intent intent = new Intent();
								intent.setAction(BaseActivity.MAIN_ACTIVITY_EXIT_ACTION);
								theActivity.sendBroadcast(intent);
							}
						}
					}).start();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login_TV_Btn_login:
			// 登录校验
			if (TextUtils.isEmpty(esUsername.getValue().trim())) {
				toastToMessage(getString(R.string.please_enter)
						+ getString(R.string.username_or_mobile));
				return;
			}
			if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
				toastToMessage(getString(R.string.please_enter)
						+ getString(R.string.password));
				return;
			}
			acc = AccountData.getInstance();
			acc.clearCurrAcc();
			acc.setUsername(esUsername.getValue());
			acc.setPassword(etPassword.getText().toString());
			acc.setBindphonenumber(acc.getUsername());
			((AccountController) mController).login(acc, true, 30 * 1000);
			break;
		case R.id.login_Btn_register:
			startActivity(new Intent(this, RegisterAllActivity.class));
			break;
		case R.id.findpwd:
			Intent intent = new Intent(this, RegisterAllActivity.class);
			intent.putExtra("isRegister", false);
			startActivity(intent);
			break;
		case R.id.btn_protocal:
			startActivity(new Intent(this, PortocalActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onLogined(String status, String message, AccountData acc) {
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
}
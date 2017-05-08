package com.lz.oncon.activity;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.activity.PortocalActivity;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.controller.AccountController.SyncListener;
import com.lz.oncon.data.AccountData;

public class RegisterAllActivity extends BaseActivity implements SyncListener {
	private static final int MSG_SEND_VERIFY_CODE = 1;
	private static final int MESSAGE_LOGIN_SUCCESS = 3;
	private static final int MESSAGE_LOGIN_FAIL = 4;
	private static final int MESSAGE_LOGIN_NETERROR = 5;
	private static final int MSG_REGISTER_MOBILE = 9;
	private static final int MSG_MODIFY_PWD = 10;
	private BaseController mController;
	private EditText mobileET,checkCode,password;
	private AlertDialog dialog;
	private boolean isRegister = true;//false 为找回密码
	private boolean isRequestReg = false;//防止重复注册
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkIsRegister();
		initController();
		initContentView();
		initViews();
		/*
		setListeners();*/
		
		
	}
	

	private void checkIsRegister() {
		if(getIntent().getExtras() != null && getIntent().getExtras().get("isRegister") != null){
			isRegister = getIntent().getExtras().getBoolean("isRegister", true);
		}else{
			isRegister = true;
		}
		
	}


	private void initViews() {
		mobileET = (EditText) findViewById(R.id.mobile_ET);
		checkCode = (EditText) findViewById(R.id.check_code_ET);
		password = (EditText) findViewById(R.id.password_ET);
		if(isRegister == false){
			findViewById(R.id.btn_register).setVisibility(View.INVISIBLE);
			findViewById(R.id.btn_getpassword).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_protocal).setVisibility(View.INVISIBLE);
			findViewById(R.id.title).setVisibility(View.VISIBLE);
		}
		dialog = new AlertDialog.Builder(RegisterAllActivity.this)
        .setPositiveButton(R.string.good, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		}).create();
	}


	private void initController() {
		mController = new AccountController(this);
	}
	@Override
	public void onClick(View v){
		super.onClick(v);
		switch(v.getId()){
			case R.id.common_title_TV_left:
				finish();
				break;
			case R.id.send_verificode:
				{
					String number = mobileET.getText().toString().trim();
					if(checkAndToastPhone() == false){
						new NetIFUI_ZBRJ(RegisterAllActivity.this, new com.lb.zbrj.net.NetIFUI.NetInterfaceListener(){
							@Override
							public void finish(NetInterfaceStatusDataStruct niStatusData) {
								mUIHandler.obtainMessage(MSG_SEND_VERIFY_CODE, niStatusData).sendToTarget();
							}
						}).m1_get_verify(number);
					}
				}
				break;
			case R.id.btn_register:
				{
					String number = mobileET.getText().toString().trim();
					String verifyCode = checkCode.getText().toString().trim();
					String passwd = password.getText().toString().trim();
					if(checkAndToastPhone() || checkAndToastCode() || checkAndToastPassword()){
						return;
					}
					new NetIFUI_ZBRJ(RegisterAllActivity.this, new NetInterfaceListener() {
						@Override
						public void finish(NetInterfaceStatusDataStruct niStatusData) {
							mUIHandler.obtainMessage(MSG_REGISTER_MOBILE, niStatusData).sendToTarget();
						}
					}).m1_reg(number, passwd, verifyCode);
				}
				
				break;
			case R.id.btn_getpassword:
				if(checkAndToastPhone() || checkAndToastCode() || checkAndToastPassword()){
					return;
				}
				String number = mobileET.getText().toString().trim();
				String verifyCode = checkCode.getText().toString().trim();
				String passwd = password.getText().toString().trim();
				new NetIFUI_ZBRJ(RegisterAllActivity.this, new NetInterfaceListener() {
					@Override
					public void finish(NetInterfaceStatusDataStruct niStatusData) {
						mUIHandler.obtainMessage(MSG_MODIFY_PWD, niStatusData).sendToTarget();
					}
				}).m1_modify_pwd(number, passwd, verifyCode);
				break;
			case R.id.btn_protocal:
				startActivity(new Intent(this, PortocalActivity.class));
				break;
			default:
				break;
		}
	}

	private boolean checkAndToastPhone(){
		String number = mobileET.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			toastToMessage(R.string.find_pwd_numberempty);
			return true;
		}
		return false;
	}
	private boolean checkAndToastCode(){
		String number = checkCode.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			toastToMessage(R.string.please_enter_verifycode);
			return true;
		}
		return false;
	}
	private boolean checkAndToastPassword(){
		String number = password.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			toastToMessage(R.string.login_input_pwdhint);
			return true;
		}
		return false;
	}
	private void initContentView() {
		this.setContentView(R.layout.registerall);
		
	}


	private UIHandler mUIHandler = new UIHandler(this);
	
	@SuppressLint("HandlerLeak")
	private  class UIHandler extends Handler {
		WeakReference<RegisterAllActivity> mActivity;
		UIHandler(RegisterAllActivity activity) {
			mActivity = new WeakReference<RegisterAllActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			final RegisterAllActivity theActivity = mActivity.get();
			switch(msg.what){
			case MSG_SEND_VERIFY_CODE:
				NetInterfaceStatusDataStruct niStatusData = (NetInterfaceStatusDataStruct)msg.obj;
				if("0".equals(niStatusData.getStatus())){
					String message = getResources().getString(R.string.bind2_mobile_memo)+mobileET.getText().toString().trim();
					if(!dialog.isShowing()){
						 dialog.setTitle(R.string.memo);
				     	 dialog.setMessage(message);
						 dialog.show();
					}
					
				}else if("4".equals(niStatusData.getStatus())){
				}else{
					theActivity.toastToMessage(niStatusData.getMessage());
				}
				break;
			case MESSAGE_LOGIN_FAIL:// 1
				String message = (String) msg.obj;
				theActivity
						.toastToMessage(TextUtils.isEmpty(message) ? theActivity
								.getString(R.string.login)
								+ " "
								+ theActivity.getString(R.string.fail)
								: message);
				break;
			case MSG_REGISTER_MOBILE:
				niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
				if ("0".equals(niStatusData.getStatus())) {
					theActivity.toastToMessage(R.string.register_success);
					theActivity.finish();
				} else if ("5".equals(niStatusData.getStatus())) {// 已注册
					if(!dialog.isShowing()){
						 dialog.setTitle(R.string.memo);
				     	 dialog.setMessage(theActivity
									.getString(R.string.reg_memo));
						 dialog.show();
					}
				} else {// 失败
					if(!dialog.isShowing()){
						 dialog.setTitle(R.string.memo);
				     	 dialog.setMessage(niStatusData.getMessage());
						 dialog.show();
					}
					
				}
				break;
			case MSG_MODIFY_PWD:
				niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
				if ("0".equals(niStatusData.getStatus())) {
					theActivity.toastToMessage(R.string.modify_pwd_success);
					theActivity.finish();
				} else if ("5".equals(niStatusData.getStatus())) {// 已注册
					if(!dialog.isShowing()){
						 dialog.setTitle(R.string.memo);
				     	 dialog.setMessage(theActivity
									.getString(R.string.reg_memo));
						 dialog.show();
					}
				} else {// 失败
					if(!dialog.isShowing()){
						 dialog.setTitle(R.string.memo);
				     	 dialog.setMessage(niStatusData.getMessage());
						 dialog.show();
					}
					
				}
				break;
			}
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
		// TODO Auto-generated method stub
		
	}

}

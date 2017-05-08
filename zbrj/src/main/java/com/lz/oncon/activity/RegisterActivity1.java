package com.lz.oncon.activity;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.controller.AccountController.SyncListener;
import com.lz.oncon.data.AccountData;

public class RegisterActivity1 extends BaseActivity implements SyncListener{

	private BaseController mController;
	private EditText mobileET;
	private ImageView clearIV;
	private AlertDialog dialog;
	private boolean willDirectLogin = false, isFirstReg = true;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
		
		
	}
	
	public void initContentView() {
		this.setContentView(R.layout.register1);
	}

	public void initController() {
		mController = new AccountController(this);
	}

	public void initViews() {		
		mobileET = (EditText)this.findViewById(R.id.mobile_ET);
		clearIV = (ImageView) findViewById(R.id.clear_IV);
		dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.confirm_mobile)
			.setPositiveButton(R.string.good, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					String mobile = mobileET.getText().toString().trim();
					new NetIFUI_ZBRJ(RegisterActivity1.this, new com.lb.zbrj.net.NetIFUI.NetInterfaceListener(){
						@Override
						public void finish(NetInterfaceStatusDataStruct niStatusData) {
							mUIHandler.obtainMessage(MSG_SEND_VERIFY_CODE, niStatusData).sendToTarget();
						}
					}).m1_get_verify(mobile);
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			}).create();
	}

	public void setListeners() {
		((AccountController) mController).setSyncListener(this);
		mobileET.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				String mobile = mobileET.getText().toString().trim();
				if(mobile.length() > 0){
					clearIV.setVisibility(View.VISIBLE);
				}else{
					clearIV.setVisibility(View.GONE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
		});
	}

	public void setValues() {
	}
	
	@Override
	public void onClick(View v){
		super.onClick(v);
		switch(v.getId()){
			case R.id.common_title_TV_left:
				finish();
				break;
			case R.id.common_title_TV_right:
				String number = mobileET.getText().toString().trim();
				if(TextUtils.isEmpty(number)){
					toastToMessage(R.string.find_pwd_numberempty);
				}else{
					if(dialog != null && !dialog.isShowing()){
						dialog.setMessage(getString(R.string.we_will_send_verifycode_to, number));
						dialog.show();
					}
				}
				break;
			case R.id.clear_IV:
				mobileET.setText("");
				clearIV.setVisibility(View.GONE);
				break;
			default:
				break;
		}
	}
	
	private static final int MSG_SEND_VERIFY_CODE = 1;
	private static final int MESSAGE_LOGIN_SUCCESS = 3;
	private static final int MESSAGE_LOGIN_FAIL = 4;
	private static final int MESSAGE_LOGIN_NETERROR = 5;
	
	private UIHandler mUIHandler = new UIHandler(this);
	
	@SuppressLint("HandlerLeak")
	private  class UIHandler extends Handler {
		WeakReference<RegisterActivity1> mActivity;
		UIHandler(RegisterActivity1 activity) {
			mActivity = new WeakReference<RegisterActivity1>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			final RegisterActivity1 theActivity = mActivity.get();
			switch(msg.what){
			case MSG_SEND_VERIFY_CODE:
				NetInterfaceStatusDataStruct niStatusData = (NetInterfaceStatusDataStruct)msg.obj;
				if("0".equals(niStatusData.getStatus())){
					String mobile = theActivity.mobileET.getText().toString().trim();
					Intent intent = new Intent(theActivity, RegisterActivity2.class);
					intent.putExtra("coutryCode", "+86");
					intent.putExtra("mobile", mobile);
					theActivity.startActivity(intent);
				}else if("4".equals(niStatusData.getStatus())){
				}else{
					theActivity.toastToMessage(niStatusData.getMessage());
				}
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
			case MESSAGE_LOGIN_FAIL:// 1
				String message = (String) msg.obj;
				theActivity
						.toastToMessage(TextUtils.isEmpty(message) ? theActivity
								.getString(R.string.login)
								+ " "
								+ theActivity.getString(R.string.fail)
								: message);
				break;
			case MESSAGE_LOGIN_NETERROR:// 5
				if (theActivity.willDirectLogin) {
					theActivity.go2MainActivity();
				} else {
					theActivity.go2Register3();
				}
				break;
			}
		}
	}

	private void go2Register3() {
		Intent intent = new Intent(RegisterActivity1.this, RegisterActivity3.class);
		startActivity(intent);
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

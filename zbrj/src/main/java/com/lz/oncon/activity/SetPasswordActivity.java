package com.lz.oncon.activity;

import com.xuanbo.xuan.R;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.data.AccountData;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetPasswordActivity extends BaseActivity {
	public BaseController mController;
	EditText etNewpwd;
	EditText etConfirmpwd;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
	}
	
	public void initContentView() {
		this.setContentView(R.layout.setpwd);
	}

	public void initController() {
		mController = new AccountController(this);
	}

	public void initViews() {
		etNewpwd = (EditText)this.findViewById(R.id.newpwd_ET);
		etConfirmpwd = (EditText)this.findViewById(R.id.confirmpwd_ET);
	}

	public void setListeners() {
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
				((AccountController)mController).updPassword(AccountData.getInstance().getPassword(), etNewpwd.getText().toString(), etConfirmpwd.getText().toString());
				break;
			default:
				break;
		}
	}
	
}

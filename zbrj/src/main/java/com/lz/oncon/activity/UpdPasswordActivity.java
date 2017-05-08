package com.lz.oncon.activity;

import com.lb.common.util.ImageUtil;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.data.AccountData;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UpdPasswordActivity extends BaseActivity {
	public BaseController mController;
	TextView tvUsername;
	EditText etOldpwd;
	EditText etNewpwd;
	EditText etConfirmpwd;
	RelativeLayout oldpwdRL, newpwdRL;
	String password;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
	}
	
	public void initContentView() {
		this.setContentView(R.layout.updpwd);
	}

	public void initController() {
		mController = new AccountController(this);
	}

	public void initViews() {
		tvUsername = (TextView)findViewById(R.id.updpwd_TV_username_value);
		etOldpwd = (EditText)this.findViewById(R.id.updpwd_ET_oldpwd);
		etNewpwd = (EditText)this.findViewById(R.id.updpwd_ET_newpwd);
		etConfirmpwd = (EditText)this.findViewById(R.id.updpwd_ET_confirmpwd);
		oldpwdRL = (RelativeLayout)findViewById(R.id.updpwd_RL_oldpwd);
		newpwdRL = (RelativeLayout)findViewById(R.id.updpwd_RL_newpwd);
	}

	public void setListeners() {

	}

	public void setValues() {
		tvUsername.setText(StringUtils.repNull(AccountData.getInstance().getUsername()));
		if(MyApplication.getInstance().mPreferencesMan.isFirstReg()){
			oldpwdRL.setVisibility(View.GONE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.topMargin = ImageUtil.convertDipToPx(MyApplication.getInstance(), 20);
			lp.leftMargin = ImageUtil.convertDipToPx(MyApplication.getInstance(), 10);
			lp.rightMargin = lp.leftMargin;
			newpwdRL.setBackgroundResource(R.drawable.group_settings_rename_selector_top);
			newpwdRL.setLayoutParams(lp);
		}
	}
	
	@Override
	public void onClick(View v){
		super.onClick(v);
		switch(v.getId()){
			case R.id.common_title_TV_left:
				finish();
				break;
			case R.id.common_title_TV_right:
				if(MyApplication.getInstance().mPreferencesMan.isFirstReg()){
					password = AccountData.getInstance().getPassword();
				}else{
					password = etOldpwd.getText().toString();
				}
				((AccountController)mController).updPassword(password, etNewpwd.getText().toString(), etConfirmpwd.getText().toString());
				break;
			default:
				break;
		}
	}
	
}

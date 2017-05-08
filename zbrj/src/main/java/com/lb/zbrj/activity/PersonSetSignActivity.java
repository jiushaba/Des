package com.lb.zbrj.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.application.AppUtil;

public class PersonSetSignActivity extends BaseActivity {

	private TextView signV;
	private PersonController personController;
	private PersonData person;
	private String oldsign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_setsign);
		initView();
		initController();
		setValue();
		setListener();
	}

	private void setListener() {
	}

	private void initController() {
		personController = new PersonController();
	}

	public void setValue() {
		person = (PersonData)getIntent().getSerializableExtra("person");
		signV.setText(person.sign);
	}

	private void initView() {
		signV = (TextView) this.findViewById(R.id.sign);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		case R.id.common_title_TV_right:
			AppUtil.execAsyncTask(new SetSignAsyncTask(this));
			break;
		}
	}
	
	class SetSignAsyncTask extends BaseNetAsyncTask{

		public SetSignAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			oldsign = person.sign;
			person.sign = signV.getText().toString().trim();
			return super.ni.m1_update_personalInfo(person);
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				person.sign = signV.getText().toString().trim();
				personController.insert(person);
				personController.updPerson(person.account, person);
				Intent intent = new Intent();
				intent.putExtra("person", person);
				setResult(RESULT_OK, intent);
				finish();
			}else{
				toastToMessage(getString(R.string.set_note_name) + getString(R.string.fail));
			}
		}
	}
}
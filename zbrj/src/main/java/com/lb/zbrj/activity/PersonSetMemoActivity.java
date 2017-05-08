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

public class PersonSetMemoActivity extends BaseActivity {

	private TextView note_nameV;
	private PersonController personController;
	private PersonData person;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_setmemo);
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
		note_nameV.setText(personController.findNameByMobile(person.account));
	}

	private void initView() {
		note_nameV = (TextView) this.findViewById(R.id.note_name);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		case R.id.common_title_TV_right:
			AppUtil.execAsyncTask(new SetMemoAsyncTask(this));
			break;
		}
	}
	
	class SetMemoAsyncTask extends BaseNetAsyncTask{

		public SetMemoAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return super.ni.m1_add_nick(person.account, note_nameV.getText().toString().trim());
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				person.memoName = note_nameV.getText().toString().trim();
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
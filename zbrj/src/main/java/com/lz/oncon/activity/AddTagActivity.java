package com.lz.oncon.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.data.AccountData;

public class AddTagActivity extends BaseActivity {
	private PersonController mController;
	private EditText etTag;
	private PersonData person;
	private String oldTag, newTag;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initController();
		initContentView(R.layout.addtag);
		initViews();
		setValues();
		setListeners();
	}

	public void initController() {
		mController = new PersonController();
	}

	public void initViews() {
		etTag = (EditText) this.findViewById(R.id.addtag_ET_tag);
	}

	public void setListeners() {
	}

	public void setValues() {
		person = mController.findPerson(AccountData.getInstance().getBindphonenumber());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		case R.id.common_title_TV_left:
			finish();
			break;
		case R.id.common_title_TV_right:
			String label_content = etTag.getText().toString();
			if (StringUtils.length(label_content) <= 0) {
				toastToMessage(getString(R.string.please_enter) + getString(R.string.tag));
				return;
			}
			String newCurrTag = "";
			if (TextUtils.isEmpty(person.label)) {
				newCurrTag = label_content;
			} else {
				newCurrTag = person.label + Constants.LABEL_SPLIT + label_content;
			}
			if (StringUtils.length(newCurrTag) > 256) {
				toastToMessage(getString(R.string.tag) + getString(R.string.str_length_not_more_than, "256"));
				return;
			}
			oldTag = person.label;
			newTag = newCurrTag;
			person.label = newTag;
			AppUtil.execAsyncTask(new AddLabelAsyncTask(this));
		}
	}
	
	class AddLabelAsyncTask extends BaseNetAsyncTask{

		public AddLabelAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return super.ni.m1_update_personalInfoLabel(person.label);
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				mController.insert(person);
				mController.updPerson(person.account, person);
				toastToMessage(getString(R.string.addtag) + getString(R.string.success));
				finish();
			}else{
				toastToMessage(getString(R.string.addtag) + getString(R.string.fail));
			}
		}
	}
}
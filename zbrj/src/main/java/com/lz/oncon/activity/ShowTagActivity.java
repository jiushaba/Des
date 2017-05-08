package com.lz.oncon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.AddTagTextView;
import com.lz.oncon.widget.AutoWrapViewGroup;
import com.lz.oncon.widget.CancleableTextView;
import com.lz.oncon.widget.CancleableTextView.AfterTVClickListener;
import com.lz.oncon.widget.CancleableTextView.OnDispearListener;

public class ShowTagActivity extends BaseActivity {

	private AutoWrapViewGroup llTag;
	private AddTagTextView tvBtnTag;
	private PersonController personController;
	private PersonData person;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initView();
		initController();
		setValue();
		setListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setValue();
	}

	private void setListener() {
		tvBtnTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转至添加标签界面
				Intent intent = new Intent(ShowTagActivity.this, AddTagActivity.class);
				ShowTagActivity.this.startActivity(intent);
			}

		});
	}

	private void initController() {
		personController = new PersonController();
	}

	public void setValue() {
		person = personController.findPerson(AccountData.getInstance().getBindphonenumber());
		llTag.removeAllViews();
		if (!TextUtils.isEmpty(person.label)) {
			String[] tags = person.label.split(Constants.LABEL_SPLIT);
			if (tags != null && tags.length > 0) {
				for (String tagTemp : tags) {
					final CancleableTextView tv = new CancleableTextView(this);
					tv.setValue(tagTemp);
					tv.setOnDispearListener(new OnDispearListener() {

						@Override
						public void onDispear() {
							delLabel(tv.getCancleable_textview_TV().getText().toString());
						}

					});
					tv.setAfterTVClickListener(new AfterTVClickListener() {

						@Override
						public void afterTVClick() {
							for (int i = 0; i < llTag.getChildCount(); i++) {
								if (llTag.getChildAt(i) instanceof CancleableTextView) {
									CancleableTextView ctv = (CancleableTextView) llTag.getChildAt(i);
									if (ctv != tv) {
										ctv.getCancleable_textview_IV().setVisibility(View.INVISIBLE);
									}
								}

							}
						}

					});
					llTag.addView(tv);
				}
			}
		}
		llTag.addView(tvBtnTag);
	}

	private void initView() {
		llTag = (AutoWrapViewGroup) this.findViewById(R.id.AWVG_tag_value);
		tvBtnTag = new AddTagTextView(this);
	}

	private void initContentView() {
		setContentView(R.layout.activity_showtag);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}
	
	private void delLabel(String label_content){
		String newCurrAccTag = "";
		String[] tags = person.label.split(Constants.LABEL_SPLIT);
		for (int i = 0; i < tags.length; i++) {
			if (TextUtils.isEmpty(tags[i])) {
				continue;
			}
			if (!tags[i].equalsIgnoreCase(label_content)) {
				newCurrAccTag += tags[i];
				if (i != tags.length - 1) {
					newCurrAccTag += Constants.LABEL_SPLIT;
				}
			}
		}
		if (newCurrAccTag.endsWith(Constants.LABEL_SPLIT)) {
			newCurrAccTag = newCurrAccTag.substring(0, newCurrAccTag.lastIndexOf(Constants.LABEL_SPLIT));
		}
		person.label = newCurrAccTag;
		AppUtil.execAsyncTask(new DelLabelAsyncTask(this));
	}
	
	class DelLabelAsyncTask extends BaseNetAsyncTask{

		public DelLabelAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return super.ni.m1_update_personalInfoLabel(person.label);
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				personController.insert(person);
				personController.updPerson(person.account, person);
			}else{
				toastToMessage(getString(R.string.deltag) + getString(R.string.fail));
			}
			setValue();
		}
	}
}
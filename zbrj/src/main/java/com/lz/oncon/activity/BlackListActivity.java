package com.lz.oncon.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.adapter.BlackListAdapter;
import com.lz.oncon.application.AppUtil;

public class BlackListActivity extends BaseActivity {
	
	private ListView black_list;
	private ArrayList<String> blackListDatas;
	private BlackListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initView();
		getServerData();
		setListener();
	}
	
	private void initContentView() {
		initContentView(R.layout.activity_blacklist);
	}
	
	private void initView() {
		black_list = (ListView) findViewById(R.id.black_list);
	}
	
	private void getServerData() {
		AppUtil.execAsyncTask(new QryBlackListAsyncTask(this));
	}
	
	private void setListener() {
		black_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PersonController.go2Detail(BlackListActivity.this, blackListDatas.get(position));
			}
		});
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}
	
	class QryBlackListAsyncTask extends BaseNetAsyncTask{

		public QryBlackListAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return super.ni.m1_get_blacklist();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				blackListDatas = (ArrayList<String>)result.getObj();
				if (blackListDatas != null && blackListDatas.size() > 0) {
					adapter = new BlackListAdapter(BlackListActivity.this, blackListDatas);
					black_list.setAdapter(adapter);
				}else {
					toastToMessage(getResources().getString(R.string.no_data));
				}
			}else{
				if (TextUtils.isEmpty(result.getMessage())) {
					toastToMessage(getResources().getString(R.string.servernoresponse));
				}else {
					toastToMessage(result.getMessage());
				}
			}
		}
		
	}
}
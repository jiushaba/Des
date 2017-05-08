package com.lz.oncon.app.im.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.widget.TitleView;

public class IMRecordThreadsDetailActivity extends BaseActivity implements
		OnItemClickListener, SynPersonInfoListener {

	private ListView mListView = null;
	private IMRecordThreadMsgAdapter mAdapter;
	private TitleView title;

	private IMThreadData data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.app_im_recordthreads_msg);
		
		title = (TitleView) findViewById(R.id.title);
		// listview
		mListView = (ListView) findViewById(R.id.im_thread_list_DLL);
		mListView.setOnItemClickListener(this);
		
		data = (IMThreadData)getIntent().getSerializableExtra("data");
		title.setTitle(new PersonController().findNameByMobile(data.getId()));
		
		mAdapter = new IMRecordThreadMsgAdapter(
					IMRecordThreadsDetailActivity.this, data);
		mListView.setVisibility(View.VISIBLE);
		mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, IMMessageListActivity.class);
		intent.putExtra("data", data.getId());
		intent.putExtra("msgId2Scroll", data.getMsgs().get(position).getId());
		startActivity(intent);
	}

	@Override
	public void syn(PersonData person) {
		// TODO Auto-generated method stub
		
	}
}
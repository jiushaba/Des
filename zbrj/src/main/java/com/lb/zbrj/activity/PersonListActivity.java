package com.lb.zbrj.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import com.lb.common.util.Log;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.adapter.PersonListAdapter;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.listener.FocusListener;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.receiver.OnNotiReceiver;
import com.lz.oncon.widget.MyLetterListView;
import com.lz.oncon.widget.SearchBar;
import com.lz.oncon.widget.MyLetterListView.OnTouchingLetterChangedListener;

public class PersonListActivity extends BaseActivity implements OnItemClickListener, OnTouchingLetterChangedListener
		, FocusListener{
	PersonController mPersonController;
	Handler handler;
	OverlayThread overlayThread;
	TextView overlay;
	OnNotiReceiver mOnNotiReceiver;
	PersonListAdapter adapter;
	private ArrayList<FansData> list = new ArrayList<FansData>();
	ListView lvFriend;
	MyLetterListView letterListView;
	private SearchBar search_bar;
	boolean FirstFlag = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_person_list);
		initController();
		initViews();
		setListeners();
		setValues();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mOnNotiReceiver);

		WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		try {
			MyApplication.getInstance().removeListener(Constants.LISTENER_FOCUS, this);
			windowManager.removeView(overlay);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public void initController() {
		mPersonController = new PersonController();
		handler = new Handler();
		overlayThread = new OverlayThread();
		MyApplication.getInstance().addListener(Constants.LISTENER_FOCUS, this);
	}

	public void initViews() {
		lvFriend = (ListView) this.findViewById(R.id.friend_LV);
		adapter = new PersonListAdapter(this, list);
		lvFriend.setAdapter(adapter);
		letterListView = (MyLetterListView) findViewById(R.id.friend_MLLV);
		search_bar = (SearchBar) findViewById(R.id.search_bar);

		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		try {
			windowManager.addView(overlay, lp);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public void setListeners() {
		lvFriend.setOnItemClickListener(this);

		letterListView.setOnTouchingLetterChangedListener(this);

		search_bar.mSearchListener = new SearchBar.SearchListener() {

			@Override
			public void search() {
				doSearch();
			}

			@Override
			public void clear() {
				clearSearch();
			}

		};

		// 注册广播接收
		IntentFilter logTaskFilter = new IntentFilter();
		logTaskFilter.addAction(OnNotiReceiver.ONCON_FRIEND_CHANGED);
		mOnNotiReceiver = new OnNotiReceiver();
		mOnNotiReceiver.addNotiListener(OnNotiReceiver.ONCON_FRIEND_CHANGED, this);
		registerReceiver(mOnNotiReceiver, logTaskFilter);
	}

	public void setValues() {
		dealSearch();
	}

	private void doSearch() {
		dealSearch();
	}

	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	private void clearSearch() {
		dealSearch();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !TextUtils.isEmpty(search_bar.search_word.getText().toString())) {
			search_bar.search_word.setText("");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		try {
			switch (v.getId()) {
			case R.id.common_title_TV_left:
				finish();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.friend_LV:
			PersonController.go2Detail(this, list.get(position).account);
		}
	}

	@Override
	public void finishNoti(String action) {
		super.finishNoti(action);
		if (OnNotiReceiver.ONCON_FRIEND_CHANGED.equals(action)) {
			setValues();
		}
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		if (adapter.getIndexer().get(s) != null) {
			int position = adapter.getIndexer().get(s);
			lvFriend.setSelection(position);
		}
		overlay.setText(s);
		overlay.setVisibility(View.VISIBLE);
		handler.removeCallbacks(overlayThread);
		handler.postDelayed(overlayThread, 1500);
	}

	private void dealSearch() {
		this.showProgressDialog(R.string.wait, false);
		new Thread(new Runnable() {
			public void run() {
				final ArrayList<FansData> temp = mPersonController.getFriends(search_bar.search_word.getText().toString());
				PersonListActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						list.clear();
						if(temp != null)list.addAll(temp);
						if (adapter != null) {
							adapter.setList(list);
							adapter.notifyDataSetChanged();
						}
						PersonListActivity.this.hideProgressDialog();
					}
				});
			}
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(needRefresh && adapter != null){
			dealSearch();
		}
	}

	private boolean needRefresh = false;
	@Override
	public void syn(ArrayList<FansData> focus) {
		needRefresh = true;
	}
}
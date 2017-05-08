package com.lz.oncon.app.im.ui;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.core.im.data.IMDataDB;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.widget.SearchBar;

public class IMRecordThreadsSearchActivity extends BaseActivity implements
		OnItemClickListener, SynPersonInfoListener {

	private ListView mListView = null;
	private IMRecordThreadsAdapter mAdapter;
	AlertDialog clearMsgDialog;

	private SearchBar search_bar;
	private boolean isSearch = false;
	private ArrayList<IMThreadData> list;
	
	private ExecutorService pool;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pool = Executors.newSingleThreadExecutor();
		initView();
	}

	private void initView() {
		setContentView(R.layout.app_im_recordthreads_search);
		
		// listview
		mListView = (ListView) findViewById(R.id.im_thread_list_DLL);
		mListView.setOnItemClickListener(this);

		initSearchListView();
	}

	private void initSearchListView() {
		search_bar = (SearchBar) findViewById(R.id.search_bar);
		search_bar.mSearchListener = new SearchBar.SearchListener() {

			@Override
			public void search() {
				doSearch();
			}

			@Override
			public void clear() {
				if (isSearch)
					clearSearch();
			}			

		};
		search_bar.mTextChangeListener = new SearchBar.TextChangeListener() {
			
			@Override
			public void textChanged() {
				doSearch();
			}
		};
		search_bar.requestSearchBarFocus();
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
		case R.id.top_search_right_txt:
		case R.id.im_empty_view:
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(search_bar.search_word.getWindowToken(), 0);
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, IMRecordThreadsDetailActivity.class);
		intent.putExtra("data", list.get(position));
		startActivity(intent);
	}

	
	private void doSearch() {
		String word = search_bar.search_word.getText().toString();
		final String real_word = StringUtils.subString(word);

		if (!StringUtils.isNull(real_word)) {
			mListView.setVisibility(View.VISIBLE);
			if (list == null) {
				list = new ArrayList<IMThreadData>();
			} else {
				list.clear();
			}
			if (mAdapter == null) {
				mAdapter = new IMRecordThreadsAdapter(
						IMRecordThreadsSearchActivity.this, list);
			}
			mListView.setAdapter(mAdapter);
			
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						ArrayList<String> tempIdx = new ArrayList<String>();
						tempIdx.addAll(ImData.getInstance().getIndexs());
						if(tempIdx.size() > 0){
							for(String idx:tempIdx){
								ArrayList<SIXmppMessage> msgs = IMDataDB.getInstance().queryMsgByWord(idx, real_word);
								if(msgs != null && msgs.size() > 0){
									IMThreadData data = new IMThreadData(idx, idx, msgs, IMThreadData.Type.P2P);
									list.add(data);
								}
							}
						}
						IMRecordThreadsSearchActivity.this
								.runOnUiThread(new Runnable() {
									public void run() {
										try {
											mAdapter
													.notifyDataSetChanged();
											isSearch = true;
											if (list.size() <= 0) {
												toastToMessage(R.string.no_search_data);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
					} catch (Exception e) {
						Log.e(Constants.LOG_TAG, e.getMessage(), e);
					} finally {
						IMRecordThreadsSearchActivity.this
								.runOnUiThread(new Runnable() {
									public void run() {
										try {
											hideProgressDialog();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
					}
				
				}
			});
		} else {
			toastToMessage(R.string.no_search_word);
		}
	}

	private void clearSearch() {
		isSearch = false;
		search_bar.search_word.setText("");
		mListView.setAdapter(mAdapter);
		mListView.setVisibility(View.GONE);
	}

	@Override
	public void syn(PersonData person) {
		// TODO Auto-generated method stub
		
	}
}
package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.core.im.data.IMDataDB;
import com.lz.oncon.widget.SearchBar;

public class IMMessageHistorySearchActivity extends BaseActivity implements
		OnItemClickListener, SynPersonInfoListener {

	private ListView mListView = null;
	private IMMessageHistorySearchAdapter mAdapter;
	private ArrayList<SIXmppMessage> list = new ArrayList<SIXmppMessage>();

	private SearchBar search_bar;
	private boolean isSearch = false;
	private String mobile = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		mobile = getIntent().getStringExtra("mobile");
	}

	private void initView() {
		setContentView(R.layout.app_im_message_history_search);
		
		// listview
		mListView = (ListView) findViewById(R.id.im_thread_list_DLL);
		mListView.setOnItemClickListener(this);
		mAdapter = new IMMessageHistorySearchAdapter(IMMessageHistorySearchActivity.this, list);
		mListView.setAdapter(mAdapter);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, IMMessageListActivity.class);
		intent.putExtra("data", mobile);
		intent.putExtra("msgId2Scroll", list.get(position).getId());
		startActivity(intent);
	}

	private void doSearch() {
		String word = search_bar.search_word.getText().toString();
		final String real_word = StringUtils.subString(word);

		if (!StringUtils.isNull(real_word)) {
			list.clear();
			mAdapter.notifyDataSetChanged();
			mListView.setVisibility(View.GONE);
			this.showProgressDialog(R.string.search, true);
			new Thread() {
				public void run() {
					try {
						//查找聊天记录
						list.addAll(IMDataDB.getInstance().queryMsgByWord(mobile, real_word));
						IMMessageHistorySearchActivity.this .runOnUiThread(new Runnable() {
							public void run() {
								try {
									mAdapter.notifyDataSetChanged();
									isSearch = true;
									if (list.size() <= 0) {
										toastToMessage(R.string.no_search_data);
									}else{
										mListView.setVisibility(View.VISIBLE);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} catch (Exception e) {
						Log.e(e.getMessage(), e);
					} finally {
						IMMessageHistorySearchActivity.this.runOnUiThread(new Runnable() {
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
			}.start();
		} else {
			toastToMessage(R.string.no_search_word);
		}
	}

	private void clearSearch() {
		isSearch = false;
		search_bar.search_word.setText("");
		list.clear();
		mAdapter.notifyDataSetChanged();
		mListView.setVisibility(View.GONE);
	}

	@Override
	public void syn(PersonData person) {
		// TODO Auto-generated method stub
	}
}
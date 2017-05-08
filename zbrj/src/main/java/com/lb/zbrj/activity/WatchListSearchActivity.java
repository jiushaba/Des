package com.lb.zbrj.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.xuanbo.xuan.R;
import com.lb.zbrj.adapter.WatchSearchListAdapter;
import com.lb.zbrj.data.VideoTagData;
import com.lb.zbrj.data.db.VideoTagHelper;
import com.lb.zbrj.view.SearchVideoListView;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.data.AccountData;

public class WatchListSearchActivity extends BaseActivity implements OnItemClickListener{

	protected SearchVideoListView searchVideoListView;
	
	private AutoCompleteTextView auto;
    private Button cancelV;
    private WatchSearchListAdapter arr_adapter;
    private ImageView searchV;
	
	protected VideoTagHelper mVideoTagHelper;
	
	protected ArrayList<Object> recentVideoTags = new ArrayList<Object>();
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initContentView();
		initController();
		initViews();
		initData();
	}

	private void initContentView() {
		setContentView(R.layout.activity_watchsearch_list);
	}
	
	private void initViews() {
		searchVideoListView = (SearchVideoListView) findViewById(R.id.search_result);
		searchV = (ImageView) findViewById(R.id.search_iv);
		
		auto = (AutoCompleteTextView) findViewById(R.id.auto);
		auto.setDropDownBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
		auto.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, final int position,long id) {
				if(recentVideoTags.get(position) instanceof VideoTagData){
					VideoTagData tag = (VideoTagData)recentVideoTags.get(position);
					auto.setText(tag.tag);
					search();
				}
			}
			
		});
		auto.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		auto.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {  
					search();
		            return true;    
		        }
				return false;
			}
		});
		cancelV = (Button) findViewById(R.id.cancel);
        getSearchHis();
        // 设置监听事件，点击搜索写入搜索词
        searchV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        cancelV.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		auto.setText("");
        		searchVideoListView.filterValue = "";
				searchVideoListView.setVisibility(View.GONE);
        	}
        });
	}
	
	private void getSearchHis(){
		recentVideoTags.clear();
		recentVideoTags.addAll(mVideoTagHelper.findRecentTags(""));
		if(recentVideoTags.size() > 0){
			recentVideoTags.add("清除全部");
		}
        //新建适配器，适配器数据为搜索历史文件内容
        arr_adapter = new WatchSearchListAdapter(this, recentVideoTags);

        // 设置适配器
        auto.setAdapter(arr_adapter);
	}

	public void initController() {
		mVideoTagHelper = new VideoTagHelper(AccountData.getInstance().getUsername());
	}
	
	public void initData(){
	}
	
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch(arg0.getId()){
		case R.id.common_title_TV_left:
			finish();
			break;
		}
	}
	
	private void initVideoSearch(){
		searchVideoListView.setVisibility(View.VISIBLE);
		if(searchVideoListView.mVideoList != null){
			searchVideoListView.mVideoList.clear();
		}
		searchVideoListView.initOnlineData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view, final int position,long id) {
		switch (parent.getId()) {
		default:
			break;
		}
	}
	
	private void search() {
		super.hideKeyboard(this, auto);
        // 获取搜索框信息
        String text = auto.getText().toString();
        if(!TextUtils.isEmpty(text)){
        	VideoTagData videoTag = new VideoTagData();
    		videoTag.seq = Calendar.getInstance().getTimeInMillis();
    		videoTag.tag = text;
    		videoTag.type = 0;
    		mVideoTagHelper.insertRecent(videoTag);
        }
        getSearchHis();
        searchVideoListView.filterValue = text;
		initVideoSearch();
    }
}
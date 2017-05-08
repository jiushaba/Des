package com.lz.oncon.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.xuanbo.xuan.R;

public class SearchBar extends LinearLayout {

	public SearchListener mSearchListener;
	public TextChangeListener mTextChangeListener;
	public ImageView search_button, search_bar_cancel;
	public EditText search_word;
	public TextView search_word_tv;
	
	public SearchBar(Context context) {
		super(context);
		init();
	}
	
	public SearchBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public SearchBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.search_bar, this);
		search_button = (ImageView)findViewById(R.id.search_button);
		search_bar_cancel = (ImageView)findViewById(R.id.search_bar_cancel);
		search_word = (EditText)findViewById(R.id.search_word);
		search_word_tv = (TextView)findViewById(R.id.search_word_tv);
		
		search_word.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		search_word.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s.toString())){
					search_bar_cancel.setVisibility(View.GONE);
					if(mSearchListener != null)mSearchListener.clear();
				}else{
					search_bar_cancel.setVisibility(View.VISIBLE);
					if(mTextChangeListener != null)mTextChangeListener.textChanged();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			
		});
		search_word.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if(mSearchListener != null)mSearchListener.search();
				return true;
			}
			
		});
		
		search_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mSearchListener != null)mSearchListener.search();
			}
			
		});
		
		search_bar_cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				search_word.setText("");
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(search_word.getWindowToken(), 0);
				if(mSearchListener != null)mSearchListener.clear();
			}
			
		});
	}
	
	public interface SearchListener{
		public void search();
		public void clear();
	}
	
	public interface TextChangeListener{
		public void textChanged();		
	}
	
	public void requestSearchBarFocus(){
		search_word.requestFocus();
	}
	
	private static final int MESSAGE_VOICE = 1000;
	private UIHandler mHandler = new UIHandler();
	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_VOICE:
				if(search_word!=null){
					String object = (String) msg.obj;
					search_word.setText("");
					search_word.setText(object);
				}
				break;
			}
		};
	};
}
package com.lz.oncon.widget;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class TitleSearchBar extends LinearLayout {

	public SearchListener mSearchListener;
	public ImageView search_button, search_bar_cancel;
	public EditText search_word;
	
	public TitleSearchBar(Context context) {
		super(context);
		init();
	}
	
	public TitleSearchBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public TitleSearchBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.widget_title_search_bar, this);
		search_button = (ImageView)findViewById(R.id.search_button);
		search_bar_cancel = (ImageView)findViewById(R.id.search_bar_cancel);
		search_word = (EditText)findViewById(R.id.search_word);
		
		search_word.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		search_word.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s.toString())){
					search_bar_cancel.setVisibility(View.GONE);
					if(mSearchListener != null)mSearchListener.clear();
				}else{
					search_bar_cancel.setVisibility(View.VISIBLE);
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
				if(mSearchListener != null)mSearchListener.cancel();
			}
			
		});
	}
	
	public interface SearchListener{
		public void search();
		public void clear();
		public void cancel();
	}
}
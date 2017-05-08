package com.lz.oncon.widget;

import com.xuanbo.xuan.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class DialogInfo extends Dialog {

	private TextView mTextView;
	public DialogInfo(Context context) {
		super(context, R.style.InfoDialog);
		init();
	}

	private void init(){
		setContentView(R.layout.dialog_info);
		mTextView = (TextView)findViewById(R.id.content);
	}
	
	public void setText(int resId){
		mTextView.setText(resId);
	}
	
	public void setText(String text){
		mTextView.setText(text);
	}
}

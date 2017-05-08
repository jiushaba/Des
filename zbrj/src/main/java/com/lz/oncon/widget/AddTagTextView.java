package com.lz.oncon.widget;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddTagTextView extends RelativeLayout {
	
	private TextView addtag_textview_TV;
		
	public AddTagTextView(Context context) {
		super(context);
		init();
	}
	
	public AddTagTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public AddTagTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.addtag_textview, this);
		addtag_textview_TV =  (TextView)findViewById(R.id.addtag_textview_TV);
	}
}

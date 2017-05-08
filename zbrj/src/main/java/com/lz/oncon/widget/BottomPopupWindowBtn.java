package com.lz.oncon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;

public class BottomPopupWindowBtn extends LinearLayout {

	public TextView btn;
	public BottomPopupWindowBtn(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.widget_bottom_popwindow_btn, this);
		btn = (TextView)findViewById(R.id.btn);
	}
	public BottomPopupWindowBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(getContext()).inflate(R.layout.widget_bottom_popwindow_btn, this);
		btn = (TextView)findViewById(R.id.btn);
	}
}

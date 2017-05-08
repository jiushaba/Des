package com.lz.oncon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;

public class BottomPopupWindowBtnNone extends LinearLayout {

	public TextView btn;
	public BottomPopupWindowBtnNone(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.widget_bottom_popwindow_btn_none, this);
		btn = (TextView)findViewById(R.id.btn);
	}
	public BottomPopupWindowBtnNone(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(getContext()).inflate(R.layout.widget_bottom_popwindow_btn_none, this);
		btn = (TextView)findViewById(R.id.btn);
	}
}

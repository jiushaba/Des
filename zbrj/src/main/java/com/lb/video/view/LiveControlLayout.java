package com.lb.video.view;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class LiveControlLayout extends FrameLayout {
	public LiveControlLayout(Context context) {
		super(context);
		init(context);
	}
	public LiveControlLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public LiveControlLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.video_recorder_controler_layout,this);
		
	}
	

	

}

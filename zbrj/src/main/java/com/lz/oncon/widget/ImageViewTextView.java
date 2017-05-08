package com.lz.oncon.widget;

import com.lb.common.util.ImageUtil;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class ImageViewTextView extends TextView {

	Context mContext;

	public ImageViewTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public ImageViewTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ImageViewTextView(Context context) {
		super(context);
		mContext = context;
	}

	public void setBackgroundResource(int resid) {
		setBackgroundDrawable(createSLD(mContext, getResources().getDrawable(resid)));
	};

	private StateListDrawable createSLD(Context context, Drawable drawable) {
		StateListDrawable bg = new StateListDrawable();
		Paint p = new Paint();
		p.setColor(0x40222222);
		Drawable normal = drawable;
		Drawable pressed = ImageUtil.createDrawable(drawable, p);
		bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
		bg.addState(View.ENABLED_STATE_SET, normal);
		bg.addState(View.EMPTY_STATE_SET, normal);
		return bg;
	}
}
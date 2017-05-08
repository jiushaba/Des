package com.lb.video.view;

import org.apache.qpid.management.common.sasl.UserPasswordCallbackHandler;
import org.bytedeco.javacpp.opencv_nonfree.SIFT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.ViewDebug.ExportedProperty;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class VideoplaySurfaceView extends SurfaceView {
	
	 private int mOrientation;
	public VideoplaySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VideoplaySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public VideoplaySurfaceView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
/*	//FIXME 直接翻转看看效果
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
       //super.onMeasure(heightSpec, widthSpec);
    	super.onMeasure(widthSpec ,heightSpec);
    }

	@Override
	public void dispatchDisplayHint(int hint) {
		// TODO Auto-generated method stub
		super.dispatchDisplayHint(hint);
	}

	 // Rotate the view counter-clockwise
    public void setOrientation(int orientation) {
        orientation = orientation % 360;
        if (mOrientation == orientation) return;
        mOrientation = orientation;
        requestLayout();
    }*/
    
}

package com.lb.video.comm;

import android.app.Activity;
import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 继承ImageView 实现了多点触碰的拖动和缩放
 * 
 * @author Administrator
 * 
 */
public class TouchEvent implements OnTouchListener {
	static final int NONE = 0;
	static final int DRAG = 1; // 拖动中
	static final int ZOOM = 2; // 缩放中
	static final int BIGGER = 3; // 放大ing
	static final int SMALLER = 4; // 缩小ing
	private int mode = NONE; // 当前的事件

	private float beforeLength; // 两触点距离
	private float afterLength; // 两触点距离
	private float scale = 0.04f; // 缩放的比例 X Y方向都是这个值 越大缩放的越快

	/* 处理拖动 变量 */
	private int start_x;
	private int start_y;
	private int stop_x;
	private int stop_y;
	
	private int down_x, down_y;
	private boolean canDisappear = false;
	private Context mContext;
	
	private OnEventListener onClickListener;
	private OnEventListener onBiggerListener;
	private OnEventListener onSmallListener;
	private int viewId;
	public boolean canZoom = true;
	public TouchEvent(int viewId ,OnEventListener onClickListener,OnEventListener onBiggerListener,OnEventListener onSmallListener){
		this.viewId = viewId;
		this.onClickListener = onClickListener;
		this.onBiggerListener = onBiggerListener;
		this.onSmallListener = onSmallListener;
	}
	

	/**
	 * 就算两点间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 处理触碰..
	 */
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(view.getId() != viewId)
			return false;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			stop_x = (int) event.getRawX();
			stop_y = (int) event.getRawY();
			start_x = (int) event.getX();
			start_y = stop_y - view.getTop();
			down_x = (int) event.getRawX();
			down_y = (int) event.getRawY();
			if (event.getPointerCount() == 2)
				beforeLength = spacing(event);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (spacing(event) > 10f) {
				mode = ZOOM;
				beforeLength = spacing(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			if(mode == DRAG){
				mode = NONE;
				int up_x = (int) event.getRawX();
				int up_y = (int) (int) event.getRawY();
				//
				if(Math.abs(up_x-down_x)<100 && Math.abs(up_y-down_y)<100){
					if(canDisappear){
						((Activity)mContext).finish();
					}else{
						if(onClickListener != null)onClickListener.onEvent();
					}
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			/* 处理拖动 */
			if (mode == DRAG) {
			}
			/* 处理缩放 */
			else if (mode == ZOOM) {
				if(canZoom){
					if (spacing(event) > 10f) {
						afterLength = spacing(event);
						float gapLength = afterLength - beforeLength;
						if (gapLength == 0) {
							break;
						} else if (Math.abs(gapLength) > 5f) {
							if (gapLength > 0) {
								if(this.onBiggerListener != null) onBiggerListener.onEvent();
							} else {
								if(this.onSmallListener != null) onSmallListener.onEvent();
							}
							beforeLength = afterLength;
						}
					}
				}
			}
			break;
		}
		return true;
	}
	
	

	public interface OnEventListener{
		public void onEvent();
	}

}
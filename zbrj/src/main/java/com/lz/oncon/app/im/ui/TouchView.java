package com.lz.oncon.app.im.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 继承ImageView 实现了多点触碰的拖动和缩放
 * 
 * @author Administrator
 * 
 */
public class TouchView extends ImageView {
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
	
	private OnClickListener onClickListener;

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void setCanDisappear(boolean canDisappear) {
		this.canDisappear = canDisappear;
	}

	private TranslateAnimation trans; // 处理超出边界的动画

	public TouchView(Context context, int w, int h) {
		super(context);
		mContext = context;
		this.setPadding(0, 0, 0, 0);
	}

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.setPadding(0, 0, 0, 0);
	}

	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		this.setPadding(0, 0, 0, 0);
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
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			stop_x = (int) event.getRawX();
			stop_y = (int) event.getRawY();
			start_x = (int) event.getX();
			start_y = stop_y - this.getTop();
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
			mode = NONE;
			if(down_x == (int) event.getRawX() && down_y == (int) event.getRawY()){
				if(canDisappear){
					((Activity)mContext).finish();
				}else{
					if(onClickListener != null)onClickListener.onClick();
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			/* 处理拖动 */
			if (mode == DRAG) {
				if (Math.abs(stop_x - start_x - getLeft()) < 88
						&& Math.abs(stop_y - start_y - getTop()) < 85) {
					this.setPosition(stop_x - start_x, stop_y - start_y, stop_x
							+ this.getWidth() - start_x, stop_y - start_y
							+ this.getHeight());
					stop_x = (int) event.getRawX();
					stop_y = (int) event.getRawY();
				}
			}
			/* 处理缩放 */
			else if (mode == ZOOM) {
				if (spacing(event) > 10f) {
					afterLength = spacing(event);
					float gapLength = afterLength - beforeLength;
					if (gapLength == 0) {
						break;
					} else if (Math.abs(gapLength) > 5f) {
						if (gapLength > 0) {
							this.setScale(scale, BIGGER);
						} else {
							this.setScale(scale, SMALLER);
						}
						beforeLength = afterLength;
					}
				}
			}
			break;
		}
		return true;
	}

	/**
	 * 实现处理缩放
	 */
	private void setScale(float temp, int flag) {
		if (flag == BIGGER) {
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
					this.getTop() - (int) (temp * this.getHeight()),
					this.getRight() + (int) (temp * this.getWidth()),
					this.getBottom() + (int) (temp * this.getHeight()));
		} else if (flag == SMALLER) {
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
					this.getTop() + (int) (temp * this.getHeight()),
					this.getRight() - (int) (temp * this.getWidth()),
					this.getBottom() - (int) (temp * this.getHeight()));
		}
	}

	/**
	 * 实现处理拖动
	 */
	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}
	
	public interface OnClickListener{
		public void onClick();
	}
}
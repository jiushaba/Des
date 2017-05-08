package com.lb.common.util;

import com.nineoldandroids.animation.ObjectAnimator;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.RotateAnimation;

@SuppressLint("NewApi")
public class AnimationUtil {

	  /**
	   * anim中的布局ID
	   */
	  public static int ANIM_IN = 0;

	  /**
	   * anim中的布局ID
	   */
	  public static int ANIM_OUT = 0;

	  /**
	   * 通过动画xml文件的id设置需要使用的动画布局文件
	   *
	   * @param layoutIn
	   * @param layoutOut
	   */
	  public static void setLayout(int layoutIn, int layoutOut) {
	      ANIM_IN = layoutIn;
	      ANIM_OUT = layoutOut;
	  }

	  /**
	   * 设置id为0
	   */
	  public static void clear() {
	      ANIM_IN = 0;
	      ANIM_OUT = 0;
	  }
	  
	  /**
	   * 顺时针旋转
	   */
	  public static void clockwiseRotation90ByCenter(View v){
		  float w = v.getWidth();
		  float h = v.getHeight();
		  RotateAnimation anim = new RotateAnimation(0,90,w/2,h/2);
		  anim.setDuration(10 * 1);
		  anim.setFillAfter(true);
		  v.startAnimation(anim);
	  }
	  
	  /**
	   * 逆时针旋转
	   */
	  public static void counterClockwiseRotation90ByCenter(View v){
		  float w = v.getWidth();
		  float h = v.getHeight();
		  RotateAnimation anim = new RotateAnimation(0,90,w/2,h/2);
		  anim.setDuration(10 * 1);
		  anim.setFillAfter(true);
		  v.startAnimation(anim);
	  }
	  
	  /**
	   * 顺时针旋转
	   */
	public static void clockwiseRotation90ByTop(View v){
//		  RotateAnimation anim = new RotateAnimation(-90,0);
//		  anim.setDuration(1000 * 1);
//		  anim.setFillAfter(true);
//		  v.startAnimation(anim);
		v.setPivotX(0f);
		v.setPivotY(0f);
		  ObjectAnimator//
			 .ofFloat(v, "rotation", 0.0F, 90.0F)//
			 .setDuration(1)//
			 .start();
	  }
	  
	  /**
	   * 逆时针旋转
	   */
	public static void counterClockwiseRotation90ByTop(View v){
//		  RotateAnimation anim = new RotateAnimation(0,-90);
//		  anim.setDuration(1000 * 1);
//		  anim.setFillAfter(true);
//		  v.startAnimation(anim);
		v.setPivotX(0f);
		v.setPivotY(0f);
		  ObjectAnimator//
			 .ofFloat(v, "rotation", 0.0F, -90.0F)//
			 .setDuration(1)//
			 .start();
	  }
	
	  /**
	   * 顺时针旋转
	   */
	public static void clockwiseRotation90ByLB(View v){
//		  RotateAnimation anim = new RotateAnimation(-90,0);
//		  anim.setDuration(1000 * 1);
//		  anim.setFillAfter(true);
//		  v.startAnimation(anim);
		v.setPivotX(0f);
		v.setPivotY(v.getHeight());
		  ObjectAnimator//
			 .ofFloat(v, "rotation", -90.0F, 0.0F)//
			 .setDuration(1)//
			 .start();
	  }
	  
	  /**
	   * 逆时针旋转
	   */
	public static void counterClockwiseRotation90ByLB(View v){
//		  RotateAnimation anim = new RotateAnimation(0,-90);
//		  anim.setDuration(1000 * 1);
//		  anim.setFillAfter(true);
//		  v.startAnimation(anim);
		v.setPivotX(0f);
		v.setPivotY(v.getHeight());
		  ObjectAnimator//
			 .ofFloat(v, "rotation", 0.0F, -90.0F)//
			 .setDuration(1)//
			 .start();
	  }
}

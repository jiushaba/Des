package com.lb.common.util;

import android.content.Context;

public class DensityUtil {
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int Dp2Px(Context context, float dp) { 
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (dp * scale); 
	} 
	 
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int Px2Dp(Context context, float px) { 
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1)); 
	} 

}

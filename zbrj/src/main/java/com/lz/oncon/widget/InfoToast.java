package com.lz.oncon.widget;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class InfoToast {
	public static Toast makeText(Context context,View view,int duration) {
		Toast mToast=new Toast(context);
		mToast.setDuration(duration);
		mToast.setView(view);
		return mToast;
	}
	
	public static Toast makeText(Context context,String content,int duration) {
		Toast mToast=new Toast(context);
		View view=LayoutInflater.from(context).inflate(R.layout.group_op_result, null);
		TextView mTextView=(TextView) view.findViewById(R.id.group_op_result_text);
		mTextView.setText(content);
		mToast.setDuration(duration);
		mToast.setView(view);
		return mToast;
	}
	
	public static Toast makeText(Context context,String content,int gravity,int xOffset,int yOffset ,int duration) {
		Toast mToast=new Toast(context);
		View view=LayoutInflater.from(context).inflate(R.layout.group_op_result, null);
		TextView mTextView=(TextView) view.findViewById(R.id.group_op_result_text);
		mTextView.setText(content);
		mToast.setDuration(duration);
		mToast.setGravity(gravity, xOffset, yOffset);
//		mToast.setMargin(horizontalMargin, verticalMargin);
		mToast.setView(view);
		return mToast;
	}
}

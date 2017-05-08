package com.lz.oncon.app.im.ui.common;

import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.widget.BottomPopupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.view.View;

public class ContactBubbleView extends LinearLayout implements View.OnLongClickListener{
	
	private Context mContext;
	private TextView textV;
	private BottomPopupWindow bottomPopupWindow;
	private int padding;

	public ContactBubbleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public ContactBubbleView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	private void init(){
		padding = ImageUtil.convertDipToPx(mContext, 5);
	}
	
	public void setData(String number, String name){
		textV = new TextView(mContext);
		textV.setText(name);
		textV.setTextColor(Color.BLACK);
		textV.setTextSize(20.0f);
		textV.setTag(number);
		textV.setOnLongClickListener(this);
		textV.setGravity(Gravity.CENTER);
		LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = padding;
		lp.topMargin = padding;
		textV.setLayoutParams(lp);
		textV.setBackgroundResource(R.drawable.bg_contact_bubble_oncon);
		textV.setPadding(padding, padding, padding, padding);
		this.addView(textV);
	}
		
	@Override
	public boolean onLongClick(View v) {
		final String item = (String)v.getTag();
		bottomPopupWindow = new BottomPopupWindow((Activity)mContext);
		bottomPopupWindow.setTitle(R.string.delete_receiver);
		bottomPopupWindow.addButton(R.string.delete, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnDeleteItemListener!=null){
					mOnDeleteItemListener.onDeleteItem(item);
					bottomPopupWindow.dismiss();
				}
			}
		}, false);
		bottomPopupWindow.showAtLocation(((Activity)getContext()).findViewById(R.id.root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		return true;
	}	
	
	public interface OnDeleteItemListener{
		public void onDeleteItem(String item);
		public void onDeleteAllItem();
	}
	private OnDeleteItemListener mOnDeleteItemListener;
	public void setOnDeleteItemListener(OnDeleteItemListener listener){
		this.mOnDeleteItemListener = listener;
	}
}
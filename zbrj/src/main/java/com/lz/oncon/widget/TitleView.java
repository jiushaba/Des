package com.lz.oncon.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;

public class TitleView extends RelativeLayout {
	protected LinearLayout llLeft;
	protected TextView tvLeft;
	protected ImageView ivLeft;
	protected TextView tvRight;
	protected TextView tvCenter;
	protected LinearLayout tvCenterLinear;
	protected RelativeLayout rl;
	
	protected Integer rightImageSrc = 0;
	
	protected Integer leftImageSrc = 0;
	protected String centerValue = "";
	protected String rightValue = "";
	protected String rightValue2 = "";
	protected String leftValue = "";
	protected String leftValue2 = "";
	protected String tabLayoutValue = "";
	protected String tabLayoutValue2 = "";
	
	public void setRightImageOnClickListener(OnClickListener mOnClickListener){
		tvRight.setOnClickListener(mOnClickListener);
	}
	
	public void setLeftImageOnClickListener(OnClickListener mOnClickListener){
		tvLeft.setOnClickListener(mOnClickListener);
	}
	
	public TitleView(Context context) {
		super(context);	
		init();
	}
	
	@SuppressLint("Recycle")
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.TitleViewAttrs);
		rightImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_rightImageSrc, 0);
		leftImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_leftImageSrc,0);
		centerValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		rightValue = attrsArray.getString(R.styleable.TitleViewAttrs_rightValue);
		leftValue = attrsArray.getString(R.styleable.TitleViewAttrs_leftValue);
		tabLayoutValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		tabLayoutValue2 = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		init();
	}
	
	@SuppressLint("Recycle")
	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.TitleViewAttrs);
		rightImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_rightImageSrc, 0);
		leftImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_leftImageSrc,0);
		centerValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		rightValue = attrsArray.getString(R.styleable.TitleViewAttrs_rightValue);
		leftValue = attrsArray.getString(R.styleable.TitleViewAttrs_leftValue);
		tabLayoutValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		tabLayoutValue2 = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		init();
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.common_title, this);
		llLeft = (LinearLayout)findViewById(R.id.common_title_TV_left);
		tvLeft =  (TextView)findViewById(R.id.common_title_TV_left_tv);
		ivLeft = (ImageView)findViewById(R.id.common_title_TV_left_iv);
		tvRight =  (TextView)findViewById(R.id.common_title_TV_right);
		tvCenter = (TextView) findViewById(R.id.common_title_TV_center);
		tvCenterLinear = (LinearLayout) findViewById(R.id.common_title_TV_center_linear);
		rl = (RelativeLayout) findViewById(R.id.common_title_RL);
		if(!TextUtils.isEmpty(centerValue)){
			tvCenter.setText(centerValue);
			tvCenter.setVisibility(View.VISIBLE);
		}else{
			tvCenter.setVisibility(View.INVISIBLE);
		}	
		if(!TextUtils.isEmpty(rightValue) || rightImageSrc != 0){
			if(!TextUtils.isEmpty(rightValue)){
				tvRight.setText(rightValue);
			}
			if(rightImageSrc != 0){
				tvRight.setBackgroundResource(rightImageSrc);
			}
			tvRight.setVisibility(View.VISIBLE);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
		
		
		if(!TextUtils.isEmpty(leftValue) || leftImageSrc != 0){
			if(!TextUtils.isEmpty(leftValue)){
				tvLeft.setText(leftValue);
				tvLeft.setVisibility(View.VISIBLE);
			}
			if(leftImageSrc != 0){
				ivLeft.setImageResource(leftImageSrc);
				ivLeft.setVisibility(View.VISIBLE);
			}
		}else{
			tvLeft.setVisibility(View.INVISIBLE);
			ivLeft.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setTitle(String title){
		centerValue = title;
		if(tvCenter != null){
			mUIHandler.obtainMessage(MESSAGE_REFRESH_TITEL, 0, 0, title).sendToTarget();
			tvCenter.setVisibility(View.VISIBLE);
		}else{
			tvCenter.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setRightValue(String rightValue){
		this.rightValue = rightValue;
		if(tvRight != null){
			mUIHandler.obtainMessage(MESSAGE_REFRESH_RIGHT_TITLE, 0, 0, rightValue).sendToTarget();
			tvRight.setVisibility(View.VISIBLE);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setLeftValue(String leftValue){
		this.leftValue = leftValue;
		if(tvLeft != null){
			mUIHandler.obtainMessage(MESSAGE_REFRESH_lEFT_TITLE, 0, 0, leftValue).sendToTarget();
			tvLeft.setVisibility(View.VISIBLE);
			ivLeft.setVisibility(View.GONE);
		}else{
			tvLeft.setVisibility(View.INVISIBLE);
			ivLeft.setVisibility(View.GONE);
		}
	}
	
	public void setRightImg(int imgResId){
		rightImageSrc = imgResId;
		if(tvRight != null){
			tvRight.setBackgroundResource(rightImageSrc);
			tvRight.setVisibility(View.VISIBLE);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setLeftImg(int imgResId){
		this.leftImageSrc = imgResId;
		if(ivLeft != null){
			ivLeft.setImageResource(leftImageSrc);
			ivLeft.setVisibility(View.VISIBLE);
			tvLeft.setVisibility(View.GONE);
		}else{
			ivLeft.setVisibility(View.INVISIBLE);
			tvLeft.setVisibility(View.GONE);
		}
	}
	
	public void setTabLayout(String tabLayoutValue, String tabLayoutValue_2) {
		this.tabLayoutValue = tabLayoutValue;
		this.tabLayoutValue2 = tabLayoutValue_2;
		tvCenterLinear.setVisibility(View.GONE);
	}
	
	public void setRightImgVisible(boolean isVisible){
		if(isVisible){
			tvRight.setVisibility(View.VISIBLE);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setRightImgVisible(boolean isVisible,String rightValue){
		this.rightValue = rightValue;
		if(isVisible){
			tvRight.setVisibility(View.VISIBLE);
			tvRight.setText(rightValue);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setLeftImgVisible(boolean isVisible){
		if(isVisible){
			tvLeft.setVisibility(View.VISIBLE);
		}else{
			tvLeft.setVisibility(View.INVISIBLE);
		}
	}

	public void setBG(int resid){
		rl.setBackgroundResource(resid);
	}
	
	public TextView getRightView(){
		return tvRight;
	}
	public String getCenterValue() {
		return centerValue;
	}

	public void setCenterValue(String centerValue) {
		this.centerValue = centerValue;
	}
	
	
	private static final int MESSAGE_REFRESH_TITEL = 1;
	private static final int MESSAGE_REFRESH_lEFT_TITLE = 2;
	private static final int MESSAGE_REFRESH_RIGHT_TITLE = 3;

	private UIHandler mUIHandler = new UIHandler();

	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_REFRESH_TITEL:
				tvCenter.setText(StringUtils.repNull((String)msg.obj));
				break;
			case MESSAGE_REFRESH_lEFT_TITLE:
				tvLeft.setText(StringUtils.repNull((String)msg.obj));
				break;
			case MESSAGE_REFRESH_RIGHT_TITLE:
				tvRight.setText(StringUtils.repNull((String)msg.obj));
				break;
			default:
				break;
			}
		}
	}

}
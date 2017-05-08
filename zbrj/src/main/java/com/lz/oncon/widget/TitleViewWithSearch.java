package com.lz.oncon.widget;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleViewWithSearch extends RelativeLayout {
	protected TextView tvLeft;
	protected TextView tvRight;
	protected RelativeLayout rl;
	public TitleSearchBar searchBar;
	
	protected Integer rightImageSrc = 0;
	protected Integer leftImageSrc = 0;
	protected String centerValue = "";
	protected String rightValue = "";
	protected String leftValue = "";
	
	public void setRightImageOnClickListener(OnClickListener mOnClickListener){
		tvRight.setOnClickListener(mOnClickListener);
	}
	
	public void setLeftImageOnClickListener(OnClickListener mOnClickListener){
		tvLeft.setOnClickListener(mOnClickListener);
	}
	
	public TitleViewWithSearch(Context context) {
		super(context);	
		init();
	}
	
	public TitleViewWithSearch(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.TitleViewAttrs);
		rightImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_rightImageSrc, 0);
		leftImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_leftImageSrc,0);
		centerValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		rightValue = attrsArray.getString(R.styleable.TitleViewAttrs_rightValue);
		leftValue = attrsArray.getString(R.styleable.TitleViewAttrs_leftValue);
		init();
	}
	
	public TitleViewWithSearch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.TitleViewAttrs);
		rightImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_rightImageSrc, 0);
		leftImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_leftImageSrc,0);
		centerValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		rightValue = attrsArray.getString(R.styleable.TitleViewAttrs_rightValue);
		leftValue = attrsArray.getString(R.styleable.TitleViewAttrs_leftValue);
		init();
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.widget_title_search, this);
		tvLeft =  (TextView)findViewById(R.id.common_title_TV_left); 
		tvRight =  (TextView)findViewById(R.id.common_title_TV_right);
		rl = (RelativeLayout) findViewById(R.id.common_title_RL);
		searchBar = (TitleSearchBar) findViewById(R.id.common_title_TV_search);	
		if(!TextUtils.isEmpty(rightValue) || rightImageSrc != 0){
			if(!TextUtils.isEmpty(rightValue)){
				tvRight.setText(rightValue);
//				tvRight.setBackgroundResource(R.drawable.ic_but_send_bg);
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
//				tvLeft.setBackgroundResource(R.drawable.ic_but_send_bg);
			}
			if(leftImageSrc != 0){
				tvLeft.setBackgroundResource(leftImageSrc);
			}
			tvLeft.setVisibility(View.VISIBLE);
		}else{
			tvLeft.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setRightValue(String rightValue){
		this.rightValue = rightValue;
		if(tvRight != null){
			tvRight.setText(rightValue);
//			tvRight.setBackgroundResource(R.drawable.ic_but_send_bg);
			tvRight.setVisibility(View.VISIBLE);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
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
	
	public void setLeftValue(String leftValue){
		this.leftValue = leftValue;
		if(tvLeft != null){
			tvLeft.setText(leftValue);
//			tvLeft.setBackgroundResource(R.drawable.ic_but_send_bg);
			tvLeft.setVisibility(View.VISIBLE);
		}else{
			tvLeft.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setLeftImg(int imgResId){
		this.leftImageSrc = imgResId;
		if(tvLeft != null){
			tvLeft.setBackgroundResource(leftImageSrc);
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
}
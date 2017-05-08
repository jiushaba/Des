package com.lz.oncon.widget;

import java.util.ArrayList;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;

import android.content.Context;
import android.util.AttributeSet;

public class HeadImageView extends RoundAngleImageView implements SynPersonInfoListener{
	
	private String mMobile;

	public HeadImageView(Context context) {
		super(context);
	}
	
	public HeadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setPerson(String mobile, String image){
		mMobile = mobile;
		ImageLoader.displayHeadImage(image, HeadImageView.this);
	}
	
	public void setMobile(final String mobile){
		mMobile = mobile;
		ImageLoader.displayHeadImage(new PersonController().findPerson(mobile).image, HeadImageView.this);
	}
	
	public void setGroupId(final String id, final ArrayList<String> members){
		setImageResource(R.drawable.groupren);//FIXME 群组
	}
	
	public String getMobile() {
		return mMobile;
	}

	@Override
	public void syn(PersonData person) {
		if(person != null && mMobile.equals(person.account)){
			ImageLoader.displayHeadImage(person.image, HeadImageView.this);
		}
	}
}
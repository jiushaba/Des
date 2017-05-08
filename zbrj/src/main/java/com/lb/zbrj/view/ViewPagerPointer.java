package com.lb.zbrj.view;

import com.xuanbo.xuan.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ViewPagerPointer extends LinearLayout {

	private int pageCount = 0;
	private int currentPage = 0;
	private LayoutParams p1, p2;
	
	public ViewPagerPointer(Context context) {
		super(context);
		init();
	}
	
	public ViewPagerPointer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressLint("NewApi")
	public ViewPagerPointer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		int w = getContext().getResources().getDimensionPixelSize(R.dimen.view_pager_pointer_width);
		p1 = new LayoutParams(w, w);
		p2 = new LayoutParams(w, w);
		p2.leftMargin = getContext().getResources().getDimensionPixelSize(R.dimen.view_pager_pointer_margin_left);
	}

	public void setPageCount(int pageCount){
		this.pageCount = pageCount;
		this.removeAllViews();
		for(int i=0;i<pageCount;i++){
			View v = new View(getContext());
			if(i == 0){
				v.setBackgroundResource(R.drawable.zbrj_solid_red_round);
				this.addView(v, p1);
			}else{
				v.setBackgroundResource(R.drawable.zbrj_solid_dark_gray_round);
				this.addView(v, p2);
			}
		}
	}
	
	public void setCurrentPage(int currentPage){
		this.currentPage = currentPage;
		for(int i=0;i<pageCount;i++){
			if(i == currentPage){
				this.getChildAt(i).setBackgroundResource(R.drawable.zbrj_solid_red_round);
			}else{
				this.getChildAt(i).setBackgroundResource(R.drawable.zbrj_solid_dark_gray_round);
			}
		}
	}
}

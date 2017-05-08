package com.lz.oncon.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPageAdapter extends PagerAdapter{

	List<View> mViewList;
	
	public ViewPageAdapter(List<View> viewList)
	{
		mViewList = viewList;
	}

	public int getCount() {
		if (mViewList != null)
		{
			return mViewList.size();
		}
		
		return 0;
	}
	
	public Object instantiateItem(View view, int index) {
	
		((ViewPager) view).addView(mViewList.get(index), 0);
	
		return mViewList.get(index);
	}
	
	public void destroyItem(View view, int position, Object arg2) {
		((ViewPager) view).removeView(mViewList.get(position));
	}

	public void finishUpdate(View arg0) {
	}

	public boolean isViewFromObject(View view, Object obj) {
		return (view == obj);
	}

	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	public Parcelable saveState() {
		return null;
	}

	public void startUpdate(View arg0) {
	}

}

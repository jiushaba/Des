package com.lz.oncon.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import com.lb.common.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lz.oncon.adapter.ViewPageAdapter;
import com.lz.oncon.data.SettingInfoData;

/*
 * 初始化安装后介绍信息
 */
public class UserGuideActivity extends BaseActivity implements OnPageChangeListener {

	private ViewPager mViewPager;
	private ViewPageAdapter mPageAdapter;
	private final static int viewBackground[] = { R.drawable.loading,
			R.drawable.loading, R.drawable.loading, R.drawable.loading };
	private List<View> mListViews;
	private int mViewCount;
	private int mCurSel;
	private String nextActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initViews();
		setValues();
	}
	
	public void initContentView() {
		setContentView(R.layout.user_guide);
	}

	public void initViews() {
		mViewPager = (ViewPager) findViewById(R.id.user_guide_ViewPager_vp);
	}

	public void setValues() {
		nextActivity = getIntent().hasExtra("nextActivity") ? getIntent().getStringExtra("nextActivity") : "";
		initViewPage();
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initViewPage() {
		mListViews = new ArrayList<View>();
		LayoutInflater mLayoutInflater = getLayoutInflater();

		mViewCount = viewBackground.length;
		for (int i = 0; i < mViewCount; i++) {
			View view = mLayoutInflater.inflate(R.layout.user_guide_layout, null);
			try{
//				if(getResources().getConfiguration().locale.getCountry().toUpperCase().equals("CN")){
					view.setBackgroundDrawable(getResources().getDrawable(viewBackground[i]));
//				}else{
//					view.setBackgroundDrawable(getResources().getDrawable(viewBackground_en[i]));
//				}
			}catch(Exception e){
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			view.setContentDescription("user_guide_" + (i+1) + "/" + mViewCount);
			if (i == mViewCount - 1) {
				view.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if(!TextUtils.isEmpty(nextActivity)){
							try {
								Intent intent = new Intent(UserGuideActivity.this, Class.forName(nextActivity));
								deliverExtras(intent);
								UserGuideActivity.this.startActivity(intent);
							} catch (Exception e) {
								Log.e(Constants.LOG_TAG, e.getMessage(), e);
							}
						}
						UserGuideActivity.this.finish();
						SettingInfoData.getInstance().setIsFirstLoad(false);
					}

				});
			}
			mListViews.add(view);
		}

		mPageAdapter = new ViewPageAdapter(mListViews);
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	private void initView() {
		mCurSel = 0;
	}

	public void setListeners() {

	}

	public void setKeyCodeBackAnim() {

	}

	public void onClick(View v) {
		int pos = (Integer) v.getTag();
		setCurView(pos);
		setCurPoint(pos);
	}

	public void onPageScrollStateChanged(int arg0) {

	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	public void onPageSelected(int arg0) {
		setCurPoint(arg0);
	}

	private void setCurView(int pos) {
		if (pos < 0 || pos >= mViewCount) {
			return;
		}

		mViewPager.setCurrentItem(pos);
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mCurSel = index;
	}
	
	private void deliverExtras(Intent targetIntent){
		if(getIntent().hasExtra("menu_tag")){
			targetIntent.putExtra("menu_tag", getIntent().getStringExtra("menu_tag"));
		}
		if(getIntent().hasExtra("encryptInfo")){
			targetIntent.putExtra("encryptInfo", getIntent().getStringExtra("encryptInfo"));
		}
	}
}

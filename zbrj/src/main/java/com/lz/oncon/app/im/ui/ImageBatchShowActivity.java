package com.lz.oncon.app.im.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.adapter.ViewPageAdapter;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.widget.NetImageView;
import com.lz.oncon.widget.TitleView;

public class ImageBatchShowActivity extends BaseActivity implements OnPageChangeListener{
	
	private String mOnconId, mCurrentMsgId;
	private ArrayList<SIXmppMessage> msgs;
	private List<View> mListViews;
	private ViewPageAdapter adapter;
	private int mCurSel, mViewCount;
	
	private TitleView titleV;
	private ViewPager contentV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_batch_show);
		
		titleV = (TitleView) findViewById(R.id.title);
		contentV = (ViewPager) findViewById(R.id.content);
		
		initValue();
		
		contentV.setOnPageChangeListener(this);
		
	}
	
	private void initValue(){
		Bundle bundle = getIntent().getExtras();
		mOnconId = bundle.getString("onconId");
		mCurrentMsgId = bundle.getString("msgId");
		try {
			msgs = new ArrayList<SIXmppMessage>();
			msgs.addAll(ImData.getInstance().queryAllImageMsgOfThread(mOnconId));
			mViewCount = msgs.size();
			
			mListViews = new ArrayList<View>();
			for (int i = 0; i < mViewCount; i++) {
				SIXmppMessage msg = msgs.get(i);
				if(mCurrentMsgId.equals(msg.getId())){
					mCurSel = i;
				}
				View view = new NetImageView(this);
				if (!TextUtils.isEmpty(msg.getImagePath())) {
					((NetImageView)view).setImage(msg.getImagePath(), msg.getImageURL());
				}else {
					((NetImageView)view).setImage(SIXmppMessage.FILE_TEMP_DIC + msg.getImageFileId(), msg.getImageFileId());
				}
				((NetImageView)view).imageV.setOnClickListener(new TouchView.OnClickListener() {
					@Override
					public void onClick() {
						if(titleV.getVisibility() == View.GONE){
							titleV.setVisibility(View.VISIBLE);
						}else{
							titleV.setVisibility(View.GONE);
						}
					}
				});
				mListViews.add(view);
			}
			
			titleV.setTitle((mCurSel + 1) + "/" + mViewCount);
			adapter = new ViewPageAdapter(mListViews);
			contentV.setAdapter(adapter);
			contentV.setCurrentItem(mCurSel);
			((NetImageView)mListViews.get(mCurSel)).loadBM();
		} catch (Throwable e) {
			toastToMessage(R.string.pic_load_failed);
		}

	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			//点击返回按钮，退出
			case R.id.common_title_TV_left:
				finish();
				break;
			case R.id.common_title_TV_right:
				String mImageFilePath = ((NetImageView)mListViews.get(mCurSel)).localPath;
				if(!TextUtils.isEmpty(mImageFilePath)){
					ImageUtil.saveImage2Local(this, mImageFilePath, ImageUtil.FAVORITE_SAVE_DIC);
				}
				break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		((NetImageView)mListViews.get(index)).loadBM();
		mCurSel = index;
		titleV.setTitle((mCurSel + 1) + "/" + mViewCount);
	}
}
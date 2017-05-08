package com.lz.oncon.app.im.ui.common;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lb.common.util.BaiduLocation;
import com.lb.common.util.BaiduLocation.BaiduLocationLister;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.activity.fc.selectimage.FriendCicleSelectImageActivity;
import com.lz.oncon.adapter.GridViewAddMegAdapter;
import com.lz.oncon.adapter.MyPagerAdapter;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.app.im.data.IMLargeClass;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.ui.IMMessageListActivity;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog;
import com.lz.oncon.widget.InfoProgressDialog;

public class IMMessageMoreBtnBar extends FrameLayout {
	
	EditText mEditText;
	ViewPager vPager;
	LinearLayout pointer;
	InfoProgressDialog mBaiduDialog;
	CameraGalleryWithClearChoiceDialog mCameraDialog;
	IMMessageInputBar inputBar;
	
	int mPageSize;
	ArrayList<View> mGridViews = new ArrayList<View>();
	IMLargeClass imLargeClass;
	String mOnconId, mName;
	IMThreadData.Type mType;

	public IMMessageMoreBtnBar(Context context) {
		super(context);
		init();
	}

	public IMMessageMoreBtnBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public IMMessageMoreBtnBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.app_im_message_morebtnbar, this);
		vPager = (ViewPager)findViewById(R.id.vPager);
		pointer = (LinearLayout)findViewById(R.id.vPager_pointer);
		mBaiduDialog = new InfoProgressDialog(getContext());
		mCameraDialog = new CameraGalleryWithClearChoiceDialog((BaseActivity)getContext());
	}
	
	public void setInputView(EditText editText){
		mEditText = editText;
	}
	
	public void setInputBar(IMMessageInputBar inputBar){
		this.inputBar = inputBar;
	}
	
	public void setThread(IMThreadData.Type type, String onconId, String name,boolean isSpeP2P){
		mType = type;
		mOnconId = onconId;
		mName = name;
		Integer[] imageResourceId = { R.drawable.btn_im_image, R.drawable.btn_im_photo, R.drawable.btn_im_location};
		Integer[] tvResourceId = { R.string.im_photo, R.string.im_camera, R.string.location};
		mPageSize = imageResourceId.length / 8 + (imageResourceId.length % 8 > 0 ? 1 : 0);
		
		LinearLayout.LayoutParams mViewPagerPointerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mViewPagerPointerParams.leftMargin = 13;
		for (int i = 0; i < mPageSize; i++) {
			int itemSize = (i + 1) * 8 > imageResourceId.length ? imageResourceId.length - i * 8 : 8;
			ArrayList<IMLargeClass> mImageIds = new ArrayList<IMLargeClass>();
			for (int j = 0; j < itemSize; j++) {
				imLargeClass = new IMLargeClass();
				imLargeClass.setImageResourceId(imageResourceId[i * 8 + j]);
				imLargeClass.setTextViewResourceId(tvResourceId[i * 8 + j]);
				mImageIds.add(imLargeClass);
			}
			GridView gv = new GridView(getContext());
			gv.setAdapter(new GridViewAddMegAdapter(getContext(), mImageIds));
			gv.setOnItemClickListener(mMoreFuncOnItemClickListener);
			gv.setCacheColorHint(Color.TRANSPARENT);
			gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gv.setNumColumns(4);
			gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gv.setVerticalSpacing(getContext().getResources().getDimensionPixelSize(R.dimen.im_msg_more_btn_bar_margin));
			gv.setVerticalFadingEdgeEnabled(false);
			mGridViews.add(gv);
			ImageView iv = new ImageView(getContext());
			iv.setLayoutParams(new LinearLayout.LayoutParams(4, 4));
			if (i == 0) {
				iv.setLayoutParams(mViewPagerPointerParams);
				iv.setImageResource(R.drawable.ic_view_page_pointer_s);
			} else {
				iv.setLayoutParams(mViewPagerPointerParams);
				iv.setImageResource(R.drawable.ic_view_page_pointer_n);
			}
			pointer.addView(iv);
		}
		vPager.setAdapter(new MyPagerAdapter(mGridViews));
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(mOnPageChangeListener);
	}
	
	public void setName(String name){
		mName = name;
	}
	
	private BaiduLocationLister mBaiduLocationLister = new BaiduLocationLister(){
		@Override
		public void baiduLocFinish(String latitude, String longitude, String address, String coorTypr) {
			if (latitude != null && latitude.length() > 0) {
				BaiduLocation.getInstance().stopLocationListener();
				SIXmppMessage xmppMessage = ImCore.getInstance().getChatManager().createChat(mOnconId).sendLocMessage(coorTypr, longitude, latitude, address,SIXmppThreadInfo.Type.P2P);
				((IMMessageListActivity)getContext()).sendMsg(xmppMessage);
			} else {
				((BaseActivity)getContext()).toastToMessage(R.string.im_loc_error);
			}
			mHandler.sendEmptyMessage(DIALOG_DISMISS);
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener(){
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mPageSize; i++) {
				if (arg0 == i) {
					((ImageView) pointer.getChildAt(i)).setImageResource(R.drawable.ic_view_page_pointer_s);
				} else {
					((ImageView) pointer.getChildAt(i)).setImageResource(R.drawable.ic_view_page_pointer_n);
				}
			}
		}
	};
	
	private OnItemClickListener mMoreFuncOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch ((int) arg3) {
			case R.drawable.btn_im_image:// 照片
				Fc_PicConstants.fc_selected_Pic_List.clear();
				Fc_PicConstants.selectlist.clear();
				Fc_PicConstants.et_content = "";
				Intent in1 = new Intent(getContext(), FriendCicleSelectImageActivity.class);
				in1.putExtra("channel", "btn_im_image");
				((Activity)getContext()).startActivityForResult(in1, com.lb.common.util.Constants.ActivitytoMorePicActivity);
				break;
			case R.drawable.btn_im_photo:// 拍摄
				SystemCamera.takePicture((Activity)getContext(), SystemCamera.TAKE_PICTURE);
				break;
			case R.drawable.btn_im_location:// 位置
				mBaiduDialog.setMessage(getContext().getString(R.string.locing));
				mBaiduDialog.show();
				new Thread(new Runnable() {
					public void run() {
						BaiduLocation.getInstance().startLocationListener(null, mBaiduLocationLister);
					}
				}).start();
				break;
			case R.drawable.btn_im_face:// 动态表情
				inputBar.faceBar.setVisibility(View.VISIBLE);
				setVisibility(View.GONE);
				inputBar.faceBar.refresh();
				break;
			default:
				break;
			}
		}
	};
	
	private static final int DIALOG_DISMISS = 301;// 取消DIALOG
	
	public UIHandler mHandler = new UIHandler();
	
	public class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DIALOG_DISMISS:
				if (mBaiduDialog != null && mBaiduDialog.isShowing()) {
					mBaiduDialog.dismiss();
				}
				break;
			}
		}
	}
	
}
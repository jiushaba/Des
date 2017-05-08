package com.lz.oncon.widget;

import java.io.File;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.app.im.ui.TouchView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public class NetImageView extends AbsoluteLayout {
	
	public String localPath;
	private String remoteUrl;
	
	public TouchView imageV;
	public LinearLayout progressV;
	
	public NetImageView(Context context) {
		super(context);
		init();
	}
	
	public NetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.widget_net_image, this);
		imageV = (TouchView) findViewById(R.id.image);
		progressV = (LinearLayout) findViewById(R.id.progress);
	}

	public void setImage(String localPath, String remoteUrl){
		this.localPath = localPath;
		this.remoteUrl = remoteUrl;
	}
	
	public void loadBM(){
		File imageFile = new File(localPath);
		if(imageFile.exists() && imageFile.length()>0){
			String path_temp = "";
			if(!TextUtils.isEmpty(localPath)){
				if(localPath.indexOf("file:///")<0){
					path_temp = "file:///".concat(localPath);
				}else{
					path_temp = localPath;
				}
			}
			ImageLoader.displayPicImage(path_temp, imageV, mImageLoadingListener);
			return;
		}
		if(remoteUrl != null){						
			File parentFile = imageFile.getParentFile();
			if(!parentFile.exists()){
				parentFile.mkdirs();
			}
			ImageLoader.displayPicImage(remoteUrl, imageV, mImageLoadingListener);
		}
	}
	
	ImageLoadingListener mImageLoadingListener = new ImageLoadingListener(){

		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			mUIHandler.sendEmptyMessage(MSG_RECEIVE_FILE_ERROR);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			mUIHandler.sendEmptyMessage(MSG_RESET_IMAGE_URI);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			
		}
		
	};
	
	private UIHandler mUIHandler = new UIHandler();
	private static final int MSG_RESET_IMAGE_URI = 1;
	private static final int MSG_RECEIVE_FILE_ERROR = 2; 
	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESET_IMAGE_URI:
				progressV.setVisibility(View.GONE);
				break;
			case MSG_RECEIVE_FILE_ERROR:
				((BaseActivity)getContext()).toastToMessage(R.string.im_imageshow_error);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
}
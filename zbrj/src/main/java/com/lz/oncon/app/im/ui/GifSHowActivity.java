package com.lz.oncon.app.im.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.lb.common.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.lb.common.util.Constants;
import com.lb.common.util.FileCore;
import com.lb.common.util.ImageThumbUtil;
import com.lb.common.util.ResourceUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.app.im.ui.news.AsyncImageLoader;
import com.lz.oncon.app.im.ui.news.AsyncImageLoader.ImageCallback;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.GifFaceData;
import com.lz.oncon.data.db.FaceHelper;

@SuppressWarnings("deprecation")
public class GifSHowActivity extends BaseActivity implements View.OnClickListener {

	private GifView mImageView;
	private TouchView imageshow_imageview;
	private LinearLayout mProgressbarLayout;

	private String gifFileName = "";
	final Handler cwjHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Bundle bundle = getIntent().getExtras();
		gifFileName = bundle.getString("mResID");
		setContentView(R.layout.gifshow);

		mProgressbarLayout = (LinearLayout) findViewById(R.id.imageshow__progressbar_layout);
		mImageView = (GifView) findViewById(R.id.imageshow_image);
		imageshow_imageview = (TouchView) findViewById(R.id.imageshow_imageview);
		initViewEvent();

		if (gifFileName != null && gifFileName.length() > 0) {
			GifFaceData gif = FaceHelper.getInstance(AccountData.getInstance().getUsername()).findImageByImageName(gifFileName);
			if (gif != null && gif.getIsdefault() != null && gif.getIsdefault().equals("0")) {
				try {
					int resId = ResourceUtil.getRawIdx(gifFileName);
					if (gif.getExtension_name().trim().equals("gif")) {
						mImageView.setGifImage(resId); // 从文件流中加载GIF动画
						mImageView.setGifImageType(GifImageType.COVER); // 只显示第一帧再显示
						showImage();
					} else {
						imageshow_imageview.setImageResource(resId);
						showImageView();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (gif != null && gif.getIsdefault() != null && gif.getIsdefault().equals("1")) {
				String faceName = gifFileName.concat(".").concat(gif.getExtension_name());
//				String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(faceName);
				String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(gifFileName).concat(gif.getExtension_name());
				String picRemoteUrl = gif.getSuburl().concat(faceName);
				// FaceHelper.loadGifFace(new AsyncImageLoader(), picRemoteUrl,
				// picLocalPath, faceName, faceView.getGifImageView(), gif,
				// true);
				File imageFile = new File(picLocalPath);
				InputStream is = null;
				if (imageFile.exists()) {

					if (gif.getExtension_name().trim().equals("gif")) {
						try {
							is = FileCore.readFile(picLocalPath);
							if (is != null) {
								mImageView.setGifImage(is);
								showImage();
							} else {
								System.out.println("is==" + is);
							}

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Bitmap b = ImageThumbUtil.getInstance().loadImageFromFile(imageFile.getAbsolutePath());
							if (b != null) {
								imageshow_imageview.setImageBitmap(b);
								showImageView();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else if (picRemoteUrl != null) {
					if (gif.getExtension_name().trim().equals("gif")) {
						is = AsyncImageLoader.loadImageInputStreamFromUrl(picRemoteUrl);
						if (is != null) {
							mImageView.setGifImage(is);
							showImage();
						}
					} else {
						AsyncImageLoader.getInstance().loadDrawable(picRemoteUrl, new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable, String imageUrl) {
								imageshow_imageview.setImageDrawable(imageDrawable);
								showImageView();
							}
						}, IMConstants.LOAD_FROM_SERVER);
					}
				}
			} else {// 数据库不存在，从网络下载
				final String mobile = AccountData.getInstance().getBindphonenumber();
				// faceView.getGifImageView().setBackgroundResource(R.drawable.default_image);
				if (!TextUtils.isEmpty(mobile)) {
					cwjHandler.post(new Runnable() {
						@Override
						public void run() {
							//FIXME 下载动态表情
						}
					});
				}
			}
		}

	}

	private void initViewEvent() {
	}

	private UIHandler mUIHandler = new UIHandler();
	private static final int MSG_RESET_IMAGE_URI = 1;
	private static final int MSG_RECEIVE_FILE_ERROR = 2;

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESET_IMAGE_URI:
				if (mImageView != null) {
					// mImageView.setGifImage(mResID); //从文件流中加载GIF动画
					// mImageView.setGifImageType(GifImageType.COVER);
					// //只显示第一帧再显示
					showImage();
					// mImageView.invalidate();
				}
				break;
			case MSG_RECEIVE_FILE_ERROR:
				toastToMessage(R.string.im_imageshow_error);
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	private void showImage() {
		mImageView.setVisibility(View.VISIBLE);
		mProgressbarLayout.setVisibility(View.GONE);
	}

	private Bitmap bm = null;
	private int screen_width, screen_height;
	
	private void showImageView() {
		imageshow_imageview.setVisibility(View.VISIBLE);
		mProgressbarLayout.setVisibility(View.GONE);

		try{
			if(bm!=null){
			int bmw = bm.getWidth();
			int bmh = bm.getHeight();
			int l, t, w = AbsoluteLayout.LayoutParams.MATCH_PARENT, h = AbsoluteLayout.LayoutParams.MATCH_PARENT;
			if(bmw <= screen_width && bmh <= screen_height){
				w = bmw;
				h = bmh;
				l = (screen_width - w)/2;
				t = (screen_height - h)/2;
			}else{
				float sw = (float)bmw / (float)screen_width;
				float sh = (float)bmh / (float)screen_height;
				float s = sw >= sh ? sw : sh;
				s = s <= 0 ? 1 : s;
				w = (int)((float)bmw / s);
				h = (int)((float)bmh / s);
				l = (screen_width - w)/2;
				t = (screen_height - h)/2;
			}
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(w, h, l, t);
			mImageView.setLayoutParams(params);
			mProgressbarLayout.setVisibility(View.GONE);
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击返回按钮，退出
		case R.id.common_title_TV_left: {
			finish();
			break;
		}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageView.free();
	}
}

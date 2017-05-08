package com.lz.oncon.activity.friendcircle.image;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.lb.common.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.activity.fc.selectimage.ImageItem;
import com.lz.oncon.activity.friendcircle.image.Fc_PicPreviewOrSelectActivity.TextCallback;
import com.lz.oncon.app.im.ui.TouchView;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class Fc_LocalImageView extends AbsoluteLayout {

	private Bitmap bm = null;
	private int screen_width, screen_height;
	private String localPath;
	private String remoteUrl;
	private String channel;
	private ImageView isselectedImage;
	private RelativeLayout select_image_rl;
	private boolean isselect = false;
	public TouchView imageV;
	public LinearLayout progressV;
	public ImageItem imageItem;
	private ImageLimitNumListener imageLimitListener = null;
	private TextCallback textcallback = null;
	private List<ImageItem> dataList = null;

	public TextCallback getTextcallback() {
		return textcallback;
	}

	public void setTextcallback(TextCallback textcallback) {
		this.textcallback = textcallback;
	}

	public ImageLimitNumListener getImageLimitListener() {
		return imageLimitListener;
	}

	public void setImageLimitListener(ImageLimitNumListener imageLimitListener) {
		this.imageLimitListener = imageLimitListener;
	}

	public Fc_LocalImageView(Context context) {
		super(context);
		init();
	}
	
	public Fc_LocalImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public Fc_LocalImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.fc_widget_net_image, this);
		imageV = (TouchView) findViewById(R.id.image);
		progressV = (LinearLayout) findViewById(R.id.progress);
		isselectedImage = (ImageView) findViewById(R.id.isselected);
		select_image_rl = (RelativeLayout)findViewById(R.id.select_image_rl);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;
	}

	public void setImage(List<ImageItem> dataList, int postion ,final int need_select,String channel) {
		this.dataList = dataList;
		this.imageItem = dataList.get(postion);
		this.localPath = imageItem.imagePath;
		this.remoteUrl = null;
		this.channel = channel;
		if(!TextUtils.isEmpty(channel)&&channel.equals("preview")){
			select_image_rl.setVisibility(View.GONE);
		}else {
			select_image_rl.setVisibility(View.VISIBLE);
			if(Fc_PicConstants.fc_selected_Pic_List.containsKey(StringUtils.repNull(imageItem.imagePath))){
				imageItem.isSelected = true;
			} else {
				imageItem.isSelected = false;
			}
		}
		this.isselect = imageItem.isSelected;
		if (isselect) {
			mUIHandler.obtainMessage(MESSAGE_SELECT).sendToTarget();
		} else {
			mUIHandler.obtainMessage(MESSAGE_NO_SELECT).sendToTarget();
		}
		isselectedImage.setOnClickListener(new OnClickListener() {//只在选图页面生效
			@Override
			public void onClick(View v) {
				if (imageItem != null) {
					String path = imageItem.imagePath;
					if ((Fc_PicConstants.fc_selected_Pic_List.size()) < (need_select)) {
						imageItem.isSelected = !imageItem.isSelected;
						if (imageItem.isSelected) {//选中
								mUIHandler.obtainMessage(MESSAGE_SELECT).sendToTarget();
								Fc_PicConstants.fc_selected_Pic_List.put(path, imageItem);
						} else if (!imageItem.isSelected) {//取消选中
								mUIHandler.obtainMessage(MESSAGE_NO_SELECT).sendToTarget();
								Fc_PicConstants.fc_selected_Pic_List.remove(path);
						}
						if (textcallback != null)
							textcallback.onListen(Fc_PicConstants.fc_selected_Pic_List.size());
					} else if ((Fc_PicConstants.fc_selected_Pic_List.size()) >= (need_select)) {
						if (imageItem.isSelected) {
							if (Fc_PicConstants.fc_selected_Pic_List.containsKey(path)) {
								imageItem.isSelected = !imageItem.isSelected;
								mUIHandler.obtainMessage(MESSAGE_NO_SELECT).sendToTarget();
								Fc_PicConstants.fc_selected_Pic_List.remove(path);
							}
							if (textcallback != null)
								textcallback.onListen(Fc_PicConstants.fc_selected_Pic_List.size());
						} else {
							if(imageLimitListener!=null){
								imageLimitListener.imageLimit();
							}
						}
					
					}
				}

			}
		});
	}

	public void loadBM() {
		if (bm != null && !bm.isRecycled()) {
			return;
		}
		File imageFile = new File(localPath);
		if (imageFile.exists() && imageFile.length() > 0) {
			mUIHandler.sendEmptyMessage(MSG_RESET_IMAGE_URI);
			return;
		}
		if (remoteUrl != null) {
			File parentFile = imageFile.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
		}
	}

	public void recyleBM() {
		try {
			if (bm != null && !bm.isRecycled()) {
				bm.recycle(); // 回收图片所占的内存
				System.gc(); // 提醒系统及时回收
			}
		} catch (Exception e) {
		}
	}
	
	@SuppressWarnings("unused")
	private void showImage() {
		try {
			int bmw = bm.getWidth();
			int bmh = bm.getHeight();
			int l, t, w = LayoutParams.MATCH_PARENT, h = LayoutParams.MATCH_PARENT;
			if (bmw <= screen_width && bmh <= screen_height) {
				w = bmw;
				h = bmh;
				l = (screen_width - w) / 2;
				t = (screen_height - h) / 2;
			} else {
				float sw = (float) bmw / (float) screen_width;
				float sh = (float) bmh / (float) screen_height;
				float s = sw >= sh ? sw : sh;
				s = s <= 0 ? 1 : s;
				w = (int) ((float) bmw / s);
				h = (int) ((float) bmh / s);
				l = (screen_width - w) / 2;
				t = (screen_height - h) / 2;
			}
//			LayoutParams params = new LayoutParams(w, h, l, t);
			// imageV.setLayoutParams(params);
			progressV.setVisibility(View.GONE);
			if(!TextUtils.isEmpty(channel)&&channel.equals("preview")){
				select_image_rl.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public class Preview_cancel_Listener implements OnClickListener {
		private ImageItem iItem = null;
		public Preview_cancel_Listener(ImageItem ii) {
			this.iItem = ii;
		}
		@Override
		public void onClick(View v) {
			if(dataList!=null&&iItem!=null&&dataList.contains(iItem)){
				mUIHandler.obtainMessage(MESSAGE_NO_SELECT).sendToTarget();
				dataList.remove(iItem);
			}
		}
	}
	private UIHandler mUIHandler = new UIHandler();
	private static final int MSG_RESET_IMAGE_URI = 1;
	private static final int MSG_RECEIVE_FILE_ERROR = 2;
	private static final int MESSAGE_SELECT = 3;
	private static final int MESSAGE_NO_SELECT = 4;

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESET_IMAGE_URI:
				if (imageV != null) {
					if (bm != null && !bm.isRecycled()) {
						bm.recycle(); // 回收图片所占的内存
						System.gc(); // 提醒系统及时回收
					}
					/**
					 * 图太大会报内存溢出
					 */
					// bm = BitmapFactory.decodeFile(localPath);
					// imageV.setImageBitmap(bm);
					try {
						bm = Fc_ThumbnailUtils.createImageThumbnail(localPath, Fc_ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL, false);
						imageV.setImageBitmap(bm);
						showImage();
						imageV.invalidate();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case MSG_RECEIVE_FILE_ERROR:
				((BaseActivity) getContext()).toastToMessage(R.string.im_imageshow_error);
				break;
			case MESSAGE_SELECT:
				isselectedImage.setImageResource(R.drawable.fc_select_image);
				break;
			case MESSAGE_NO_SELECT:
				isselectedImage.setImageResource(R.drawable.fc_no_select_image);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	
	public interface ImageLimitNumListener {
		public void imageLimit();
	}

}
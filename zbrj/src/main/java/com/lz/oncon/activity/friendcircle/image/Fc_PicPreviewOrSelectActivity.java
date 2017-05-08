package com.lz.oncon.activity.friendcircle.image;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.activity.fc.selectimage.FriendCicleSelectImageActivity;
import com.lz.oncon.activity.fc.selectimage.ImageItem;
import com.lz.oncon.activity.friendcircle.image.Fc_LocalImageView.ImageLimitNumListener;
import com.lz.oncon.app.im.ui.TouchView;

public class Fc_PicPreviewOrSelectActivity extends BaseActivity  {
	private int mCurSel;
	private int need_select;// 还需要选几张
	private String channel = "";// 来源渠道 preview仅为浏览,不为空发图页面入口;btn_im_image从IM进入的

	private ViewPager contentV;
	private RelativeLayout title_layout;
	private TextView more_image_choose_preview_button;
	private TextView preview_image_num_tv;

	private List<ImageItem> dataList;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fc_image_preview_or_select);
		
		title_layout = (RelativeLayout) findViewById(R.id.title_layout);
		more_image_choose_preview_button = (TextView) findViewById(R.id.more_image_choose_preview_button);
		preview_image_num_tv = (TextView) findViewById(R.id.preview_image_num_tv);
		contentV = (ViewPager) findViewById(R.id.content);
		initValue();
	}

	private void initValue() {
		dataList = (List<ImageItem>) getIntent().getSerializableExtra("imagelist");
		mCurSel = getIntent().getExtras().getInt("position");
		need_select = getIntent().getExtras().getInt("need_select");
		channel = getIntent().getExtras().getString("channel");
		if (!"preview".equals(channel)) {
			more_image_choose_preview_button.setBackgroundResource(R.drawable.fc_but_finish_bg);
			more_image_choose_preview_button.setText(getResources().getString(R.string.fc_message_finish));
		} else if ("preview".equals(channel)) {
			more_image_choose_preview_button.setBackgroundResource(R.drawable.fc_select_image_remove);
		}

		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.logo).showImageOnFail(R.drawable.logo).resetViewBeforeLoading(true).cacheOnDisc(false).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(300)).build();

		mHandler.obtainMessage(MESSAGE_RIGHT_TV, String.valueOf(Fc_PicConstants.fc_selected_Pic_List.size())).sendToTarget();
		mHandler.post(r);
	}

	public Runnable r = new Runnable() {
		@Override
		public void run() {
			if (dataList != null && dataList.size() > 0) {
				if ((dataList.get(0) != null && dataList.get(0).isCamera)) {
					mCurSel = (mCurSel - 1);
					dataList.remove(0);
				}else{
				}
				contentV.setAdapter(new ImagePagerAdapter(dataList));
				contentV.setCurrentItem(mCurSel);
			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击返回按钮，退出
		case R.id.friendcircle_back:
			finish();
			break;
		case R.id.more_image_choose_preview_button:
			if (!"preview".equals(channel) && !"btn_im_image".equals(channel)) {// 发图页面
//				Intent in1 = new Intent(Fc_PicPreviewOrSelectActivity.this, FriendCircleNewSendImgTxtActivity.class);
//				Fc_PicPreviewOrSelectActivity.this.startActivity(in1);
				setResult(RESULT_OK);
				finish();
			} else if ("preview".equals(channel)) {// 预览
				new AlertDialog.Builder(Fc_PicPreviewOrSelectActivity.this).setTitle(Fc_PicPreviewOrSelectActivity.this.getString(R.string.memo)).setPositiveButton(Fc_PicPreviewOrSelectActivity.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setNegativeButton(Fc_PicPreviewOrSelectActivity.this.getString(R.string.confirm), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (dataList.size() == 0) {
							finish();
							return;
						} else {
							ImageItem imageItem = ((ImageItem) dataList.get(mCurSel));
							if (imageItem != null) {
								for (int i = 0; i < Fc_PicConstants.selectlist.size(); i++) {
									if (Fc_PicConstants.selectlist.get(i) != null) {
										if (Fc_PicConstants.selectlist.get(i).imagePath.equals(imageItem.imagePath)) {
											Fc_PicConstants.selectlist.remove(i);
											break;
										}
									}
								}
								dataList.remove(imageItem);
								contentV.removeAllViews();
							}
							mCurSel = ((mCurSel == dataList.size()) ? (mCurSel - 1) : (mCurSel));
							if (mCurSel == dataList.size()) {
								mCurSel--;
							}
							if (dataList != null && dataList.size() <= 0) {
								finish();
								return;
							} else {
								mHandler.post(r);
							}
						}
					}
				}).setMessage(Fc_PicPreviewOrSelectActivity.this.getString(R.string.fc_detele_select_image)).show();

			} else {// im 发送消息来源
				Fc_PicPreviewOrSelectActivity.this.setResult(RESULT_OK);
				finish();
			}
			break;
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
	}

	public interface TextCallback {
		public void onListen(int count);

		public void onLimitInfo();
	}

	private static final int MESSAGE_LIMIT_INFO = 0;
	private static final int MESSAGE_RIGHT_TV = 1;
	private static final int MESSAGE_REFLASH = 2;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_LIMIT_INFO:
				String info = String.format(getResources().getString(R.string.fc_message_limit_num), FriendCicleSelectImageActivity.limit_max);
				toastToMessage(info);
				break;
			case MESSAGE_RIGHT_TV:
				String countString = (String) msg.obj;
				int totalCount = 0;
				int count = 0;
				if (dataList != null) {
					totalCount = dataList.size();
				}
				if (!TextUtils.isEmpty(countString)) {
					count = Integer.parseInt(countString);
				}
				if (!"preview".equals(channel)) {
					String pre_num = String.format(getResources().getString(R.string.fc_message_select_imagenum), count, need_select);
					more_image_choose_preview_button.setText(pre_num);
				}
				break;
			case MESSAGE_REFLASH:
				preview_image_num_tv.setText((mCurSel + 1) + "/" + dataList.size());
				break;

			default:
				break;
			}
		}
	};

	private class ImagePagerAdapter extends PagerAdapter {

		private List<ImageItem> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<ImageItem> images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.size();
		}
		/**
	     * 跳转到每个页面都要执行的方法
	     */
	    @Override
	    public void setPrimaryItem(View container, int position, Object object) {
	        //把这个position赋值到一个全局变量，通过这个就会知道滑动到哪个页面了
	    	mCurSel = position;
	    	preview_image_num_tv.setText((position+1) + "/" + images.size());
	    }


		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			ImageItem imageItem = images.get(position);
			if (imageItem != null && !TextUtils.isEmpty(imageItem.imagePath)) {
				final View imageLayout = new Fc_LocalImageView(Fc_PicPreviewOrSelectActivity.this);
				((Fc_LocalImageView) imageLayout).setImage(dataList, position, need_select, channel);
				((Fc_LocalImageView) imageLayout).imageV.setOnClickListener(new TouchView.OnClickListener() {
					@Override
					public void onClick() {
						if (title_layout.getVisibility() == View.GONE) {
							title_layout.setVisibility(View.VISIBLE);
						} else {
							title_layout.setVisibility(View.GONE);
						}
					}
				});
				((Fc_LocalImageView) imageLayout).setImageLimitListener(new ImageLimitNumListener() {

					@Override
					public void imageLimit() {
						mHandler.obtainMessage(MESSAGE_LIMIT_INFO).sendToTarget();
					}
				});
				((Fc_LocalImageView) imageLayout).setTextcallback(new TextCallback() {

					public void onListen(int count) {
						mHandler.obtainMessage(MESSAGE_RIGHT_TV, String.valueOf(count)).sendToTarget();
					}

					@Override
					public void onLimitInfo() {
						mHandler.obtainMessage(MESSAGE_LIMIT_INFO).sendToTarget();
					}

				});
				String path_temp = "";
				if (!TextUtils.isEmpty(imageItem.imagePath)) {
					if (imageItem.imagePath.indexOf("file:///") < 0) {
						path_temp = "file:///".concat(imageItem.imagePath);
					} else {
						path_temp = imageItem.imagePath;
					}
				}
				imageLoader.displayImage(path_temp, ((Fc_LocalImageView) imageLayout).imageV, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						((Fc_LocalImageView) imageLayout).progressV.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(Fc_PicPreviewOrSelectActivity.this, message, Toast.LENGTH_SHORT).show();

						((Fc_LocalImageView) imageLayout).progressV.setVisibility(View.GONE);

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						((Fc_LocalImageView) imageLayout).progressV.setVisibility(View.GONE);
					}
				});

				((ViewPager) view).addView(imageLayout, 0);
				return imageLayout;
			} else {
				return null;
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
}

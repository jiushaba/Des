package com.lz.oncon.activity.fc.selectimage;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.friendcircle.image.Fc_PicPreviewOrSelectActivity;
import com.lz.oncon.activity.friendcircle.image.Fc_PicPreviewOrSelectActivity.TextCallback;
import com.lz.oncon.app.im.util.SystemCamera;

public class FriendCicleSelectImageActivity extends BaseActivity {
	private TextView friendcircle_back;
	private TextView friendcircle_message;
	private Button more_image_choose_preview_button;
	private RelativeLayout gridviewll;
	private RelativeLayout button_left;
	private RelativeLayout button_right;
	private TextView tv_image_num;
	private TextView tv_left_dir;
	private GridView gridView;
	private ProgressBar pb;
	private static List<ImageItem> dataList;
	private static List<ImageBucket> imageBucketList;
	private FriendCicleImageGridAdapter adapter;
	private AlbumHelper helper;
	public static int limit_max = 9;
	private String channel = "";//来源
	private String show_limit_num = "";//显示限制数量,传入整形
	public static final String MESSAGE_NO_SHOW_CAMERA = "no_show_camera";//不显示照相机
	public static final String MESSAGE_SHOW_LIMIT_NUM = "show_limit_num";//显示限制数量
	private int need_select = FriendCicleSelectImageActivity.limit_max;//默认最大张
	private Fc_BottomPopupWindow bottomPopupWindow = null;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initViews();
		initController();
		setValues();
		setListeners();

	}

	public void initContentView() {
		setContentView(R.layout.fc_activity_image_grid);
	}
	public void initViews() {
		friendcircle_back = (TextView) findViewById(R.id.friendcircle_back);
		friendcircle_message = (TextView) findViewById(R.id.friendcircle_message);
		more_image_choose_preview_button = (Button) findViewById(R.id.more_image_choose_preview_button);
		gridviewll = (RelativeLayout)findViewById(R.id.gridviewll);
		pb = (ProgressBar) findViewById(R.id.more_image_album_pb);
		gridView = (GridView) findViewById(R.id.gridview);
		button_left = (RelativeLayout) findViewById(R.id.button_left);
		button_right = (RelativeLayout) findViewById(R.id.button_right);
		tv_image_num = (TextView)findViewById(R.id.tv_image_num);
		tv_left_dir =(TextView)findViewById(R.id.tv_left_dir);
	}
	public void initController() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_ALBUM, 200);
	}


	public void setValues() {
		channel = getIntent().hasExtra("channel") ? getIntent().getStringExtra("channel") : "";
		show_limit_num = getIntent().hasExtra("show_limit_num") ? getIntent().getStringExtra("show_limit_num") : "";
		need_select = getIntent().getIntExtra("need_select",need_select);
		need_select = (need_select>=FriendCicleSelectImageActivity.limit_max?FriendCicleSelectImageActivity.limit_max:need_select);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.fc_select_defalt)
		.showImageForEmptyUri(R.drawable.fc_select_defalt)
		.showImageOnFail(R.drawable.fc_select_defalt)
		.cacheInMemory(true)
		.cacheOnDisc(false)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public void setListeners() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ImageItem imageItem = (ImageItem) dataList.get(position);
				if (imageItem != null) {
					if (imageItem.isCamera) {
						if (Fc_PicConstants.fc_selected_Pic_List.size() < need_select) {
							SystemCamera.takePicture(FriendCicleSelectImageActivity.this, SystemCamera.TAKE_PICTURE);
						}else{
							mHandler.obtainMessage(MESSAGE_LIMIT_INFO).sendToTarget();
						}
					} else {
						if (dataList != null) {
							Intent intent1 = new Intent(getApplicationContext(), Fc_PicPreviewOrSelectActivity.class);
							intent1.putExtra("position", position);
							intent1.putExtra("imagelist",(Serializable) dataList);
							intent1.putExtra("need_select", need_select);
							if(!TextUtils.isEmpty(channel)){
								intent1.putExtra("channel",channel);
							}
							startActivityForResult(intent1, SELECT_IMAGE_IM_ONRESULT);
						}
					}
				}
			}

		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.obtainMessage(MESSAGE_NOTIFICATION_COUNT, String.valueOf(Fc_PicConstants.fc_selected_Pic_List.size())).sendToTarget();
		mHandler.sendEmptyMessage(MESSAGE_REFLASH);
	}

	private static final int MESSAGE_LIMIT_INFO = 0;
	private static final int MESSAGE_NOTIFICATION_COUNT = 1;
	private static final int MESSAGE_ADD_CAMERA = 3;
	private static final int MESSAGE_REFLASH = 4;
	private static final int MESSAGE_REFRESH_ADAPTER = 5;
	private static final int MESSAGE_SHOW_ALBUM = 6;
	private static final int MESSAGE_LEFT_DIR_STRING = 7;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_LIMIT_INFO:
				String info = "";
				if(!TextUtils.isEmpty(show_limit_num)){
					info = String.format(getResources().getString(R.string.fc_message_limit_num), Integer.parseInt(show_limit_num));
				}else{
					info = String.format(getResources().getString(R.string.fc_message_limit_num), need_select);
				}
				toastToMessage(info);
				break;
			case MESSAGE_NOTIFICATION_COUNT:
				String countString = (String) msg.obj;
				int totalCount = 0;
				int count = 0;
				if (dataList != null) {
					totalCount = dataList.size();
				}
				if (!TextUtils.isEmpty(countString)) {
					count = Integer.parseInt(countString);
				}
				String pre_num = String.format(getResources().getString(R.string.fc_message_select_imagenum), count, need_select);
				more_image_choose_preview_button.setText(pre_num);
				String pre_select_num="";
				if(count==0){
					pre_select_num = getResources().getString(R.string.fc_preview);
				}else{
					pre_select_num = String.format(getResources().getString(R.string.fc_preview_select), count);
				}
				tv_image_num.setText(pre_select_num);
				break;

			case MESSAGE_ADD_CAMERA:
				FriendCicleSelectImageActivity.this.setResult(RESULT_OK);
				finish();
				break;
			case MESSAGE_REFLASH:
				if(adapter!=null)
				adapter.notifyDataSetChanged();
				break;
			case MESSAGE_REFRESH_ADAPTER:
				break;
			case MESSAGE_SHOW_ALBUM:
				AlbumHelper.bucketList.clear();
				imageBucketList = helper.getImagesBucketList(true);
				dataList = helper.getImageItemList(imageBucketList);
				if(dataList!=null){
					if(!channel.equals(MESSAGE_NO_SHOW_CAMERA)){
						ImageItem itCamera = new ImageItem();
						itCamera.isCamera = true;
						dataList.add(0,itCamera);
					}
					List<ImageItem> dataAllList = new ArrayList<ImageItem>();
					dataAllList.addAll(dataList);
					// 加入所有图片对象
					ImageBucket ib = new ImageBucket();
					ib.bucketName = getResources().getString(R.string.fc_all_image);
					ib.imageList = dataAllList;
					imageBucketList.add(0, ib);
					
					adapter = new FriendCicleImageGridAdapter(FriendCicleSelectImageActivity.this, dataList,need_select,imageLoader,options);
					gridView.setAdapter(adapter);
					adapter.setTextCallback(new TextCallback() {
						public void onListen(int count) {
							mHandler.obtainMessage(MESSAGE_NOTIFICATION_COUNT, String.valueOf(count)).sendToTarget();
						}

						@Override
						public void onLimitInfo() {
							mHandler.obtainMessage(MESSAGE_LIMIT_INFO).sendToTarget();
						}
					});
					pb.setVisibility(View.GONE);
					gridviewll.setVisibility(View.VISIBLE);
				}else{
					pb.setVisibility(View.GONE);
					gridviewll.setVisibility(View.VISIBLE);
					toastToMessage(getResources().getString(R.string.no_select_pic));
				}
				
				break;
			case MESSAGE_LEFT_DIR_STRING:
				String string = (String) msg.obj;
				if(!TextUtils.isEmpty(string))
				tv_left_dir.setText(string);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击返回按钮，退出
		case R.id.friendcircle_back:
			Fc_PicConstants.fc_selected_Pic_List.clear();
			FriendCicleSelectImageActivity.this.setResult(-1004);
			FriendCicleSelectImageActivity.this.finish();
			break;
		case R.id.more_image_choose_preview_button:
			FriendCicleSelectImageActivity.this.setResult(RESULT_OK);
			finish();
			break;
		case R.id.button_right:
			if (Fc_PicConstants.getSelectImageItemList() != null) {
				Intent intent1 = new Intent(getApplicationContext(), Fc_PicPreviewOrSelectActivity.class);
				intent1.putExtra("position", 0);
				intent1.putExtra("imagelist", (Serializable) Fc_PicConstants.getSelectImageItemList());
				intent1.putExtra("need_select", need_select);
				if(!TextUtils.isEmpty(channel)){
					intent1.putExtra("channel", channel);
				}
				startActivityForResult(intent1, SELECT_IMAGE_IM_ONRESULT);
			}
			
			break;
		case R.id.button_left:
			if (imageBucketList != null) {
				if (bottomPopupWindow == null) {
					bottomPopupWindow = new Fc_BottomPopupWindow(FriendCicleSelectImageActivity.this);
				}
				bottomPopupWindow.addList(FriendCicleSelectImageActivity.this, imageBucketList, itemsOnClick);
				bottomPopupWindow.showAsPullUp(findViewById(R.id.gridview_button), 0,0);
			}
			break;
		}
	}
	
	// 为弹出窗口实现监听类
	private OnItemClickListener itemsOnClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			bottomPopupWindow.dismiss();
			List<ImageItem> dataListTemp = ((ImageBucket) imageBucketList.get(position)).imageList;
			if (dataListTemp != null && dataListTemp.size() > 0) {
				dataList.clear();
				if (!getResources().getString(R.string.fc_all_image).equals(((ImageBucket) imageBucketList.get(position)).bucketName)) {
					// 按照时间由大到小排序
					Collections.sort(dataListTemp, new Comparator<ImageItem>() {
						@Override
						public int compare(ImageItem lhs, ImageItem rhs) {
							return StringUtils.repNull(rhs.date_added).compareTo(StringUtils.repNull(lhs.date_added));
						}

					});
				}
				dataList.addAll(dataListTemp);
				mHandler.obtainMessage(MESSAGE_LEFT_DIR_STRING,((ImageBucket) imageBucketList.get(position)).bucketName).sendToTarget();
				mHandler.sendEmptyMessage(MESSAGE_REFLASH);
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Fc_PicConstants.fc_selected_Pic_List.clear();
			FriendCicleSelectImageActivity.this.setResult(-1004);
			FriendCicleSelectImageActivity.this.finish();
		}
		return false;
	}

	public static final int SELECT_IMAGE_IM_ONRESULT = 1000001;//IM回
	
	public static final int SELECT_IMAGE_FC_ONRESULT = 1000002;//FC回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String filePath = null;
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SystemCamera.TAKE_PICTURE) {
				filePath = SystemCamera.getCaptureFilePath();
				File file = new File(filePath);
				if (file != null && file.exists()) {
					com.lz.oncon.activity.friendcircle.image.Fc_ThumbnailUtils.createImageThumbnail(filePath, filePath, Images.Thumbnails.MINI_KIND, false);
				}
				if (file != null && !file.exists()) {
					toastToMessage(getString(R.string.camera) + getString(R.string.fail));
					return;
				}
				if (!TextUtils.isEmpty(filePath)&&!Fc_PicConstants.fc_selected_Pic_List.containsKey(filePath)) {
					ImageItem ii = new ImageItem();
					ii.fromCamera=true;
					ii.imagePath = filePath;
					Fc_PicConstants.fc_selected_Pic_List.put(filePath, ii);
				}
				Message m = new Message();
				m.what = MESSAGE_ADD_CAMERA;
				m.obj = filePath;
				mHandler.sendMessage(m);
			}else if(requestCode == SELECT_IMAGE_IM_ONRESULT){
				//im 发送消息来源
				FriendCicleSelectImageActivity.this.setResult(RESULT_OK);
				finish();
			}else if(requestCode == SELECT_IMAGE_IM_ONRESULT){//朋友圈选图回调
				setResult(RESULT_OK);
				finish();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		imageLoader.clearMemoryCache(); 
//		imageLoader.clearDiscCache();  
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("channel", channel);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        channel =savedInstanceState.getString("channel");
    }
}

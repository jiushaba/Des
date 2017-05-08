package com.lz.oncon.activity.friendcircle;

/**
 * 朋友圈activity
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.lb.common.util.corpimage.CropImage;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.CommentData;
import com.lb.zbrj.data.LikeData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.net.NetworkStatusCheck;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.activity.friendcircle.FriendCircleListView.DListViewState;
import com.lz.oncon.activity.friendcircle.FriendCircleListView.OnRefreshListener;
import com.lz.oncon.api.CustomProtocolListener;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.data.ImData.OnDataChangeListener;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.db.FCHelper;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog;
import com.lz.oncon.widget.HeadImageView;

public class FriendCircleActivity extends BaseActivity implements OnScrollListener, OnDataChangeListener, SynPersonInfoListener
	, CustomProtocolListener{
	private FriendCircleListView listview;
	private ImageView iv_loading;
	private String phone;

	private NetIF_ZBRJ ni;
	private NetworkStatusCheck nsc;

	private int pageNo = 1;
	private int pageSize = 10;
	private int typeFromCache = 1;//从缓存中加载
	
	private ArrayList<VideoData> list = new ArrayList<VideoData>();
	private ArrayList<VideoData> list_s = new ArrayList<VideoData>();
	private ArrayList<VideoData> list_sl = new ArrayList<VideoData>();
	private FriendCircleAdapter adapter;
	private boolean footerviewIsShow = false;

	private Bitmap tempb;

	public RelativeLayout rl_comment;
	public EditText et_content;
	public Button btn_send;
	private VideoData dynamic_temp = null;
	private PersonController mPersonController;
	private HeadImageView headerView_avatar;
	private TextView headerView_nickname, headerView_sex, headerView_video;
	private TextView newMsgNotiV;
	
	@SuppressWarnings("rawtypes")
	class SortDynamicComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Source_Dynamic s1 = (Source_Dynamic) o1;
			Source_Dynamic s2 = (Source_Dynamic) o2;
			return s2.getCreateTime().compareTo(s1.getCreateTime());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView();
		initController();
		initViews();
		setValues();
		setListeners();
	}


	public void initContentView() {
		setContentView(R.layout.fc_activity_friendcircle);
	}

	
	public void initController() {
		nsc = new NetworkStatusCheck(FriendCircleActivity.this);
		ni = new NetIF_ZBRJ(FriendCircleActivity.this);
		dynamic_temp = (VideoData) getIntent().getSerializableExtra("source_Dynamic");
		mPersonController = new PersonController();
	}
	
	public void initViews() {
		listview = (FriendCircleListView) findViewById(R.id.friendcircle_listview);
		iv_loading = (ImageView) findViewById(R.id.friendcircle_loading);
		initFooterView();
		rl_comment = (RelativeLayout) findViewById(R.id.friendcircle_comment_rl);
		et_content = (EditText) findViewById(R.id.friendcircle_comment_rl_et);
		btn_send = (Button) findViewById(R.id.friendcircle_comment_rl_btn);
		
		headerView_avatar = (HeadImageView) findViewById(R.id.head);
		headerView_nickname = (TextView) findViewById(R.id.nickname);
		headerView_sex = (TextView) findViewById(R.id.sex_v);
		headerView_video = (TextView) findViewById(R.id.video_num);
		newMsgNotiV = (TextView) findViewById(R.id.msg_noti);
	}

	public void setValues() {
		phone = getIntent().hasExtra("mobile") ? getIntent().getStringExtra("mobile") : AccountData.getInstance().getBindphonenumber();
		String videoCount = getIntent().hasExtra("videoCount") ? getIntent().getStringExtra("videoCount"):"0";
		headerView_video.setText(videoCount);
		setMobile(phone);
		Fc_NoReadMessageBean fnM = SortDynamicByDate.getNoReadMessage();
		if(fnM!=null)
		setMessageNum(FriendCircleActivity.this,fnM.num, fnM.mobile, fnM.list);
		adapter = new FriendCircleAdapter(this, list, phone, yHandler, null);
		listview.setAdapter(adapter);
		/**
		 * 先从缓存中加载
		 */
		String key = phone + "_" + pageNo;
		Source_DynamicList source_dynamicList = (Source_DynamicList) FriendCircleCacheUtil.readObject(key, MyApplication.getInstance());
		if (source_dynamicList != null) {
			Message m = new Message();
			m.what = SUCCESS;
			m.arg1 = typeFromCache;
			m.obj = (ArrayList<VideoData>) source_dynamicList.getSourceDynamicList();
			yHandler.sendMessage(m);
		}else{
			Message m = new Message();
			m.what = SUCCESS;
			m.obj = new ArrayList<VideoData>();
			yHandler.sendMessage(m);
		}

		createThreadForGetDynamic();
	}

	public void setListeners() {
		ImData.getInstance().addOnDataChangeListener(this);
		ImCore.getInstance().getConnection().addCustomProtocolListener(this);
		listview.setOnScrollListener(this);

		// 下拉刷新
		listview.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pageNo = 1;
				createThreadForGetDynamic();
			}
		});
		
		findViewById(R.id.common_title_TV_left).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
			
		});
		if(phone.equals(AccountData.getInstance().getBindphonenumber())){
			findViewById(R.id.common_title_TV_right).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.common_title_TV_right).setVisibility(View.GONE);
		}
		findViewById(R.id.common_title_TV_right).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(findViewById(R.id.menu_ll).getVisibility() == View.VISIBLE){
					findViewById(R.id.menu_ll).setVisibility(View.GONE);
				}else{
					findViewById(R.id.menu_ll).setVisibility(View.VISIBLE);
				}
			}
			
		});
		findViewById(R.id.history_msg).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				findViewById(R.id.menu_ll).setVisibility(View.GONE);
				Intent intent = new Intent(FriendCircleActivity.this, FCMsgHistoryActivity.class);
				startActivity(intent);
			}
			
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Fc_NoReadMessageBean fnM = SortDynamicByDate.getNoReadMessage();
		if(fnM!=null)
		setMessageNum(FriendCircleActivity.this,fnM.num, fnM.mobile, fnM.list);
		
		yHandler.sendEmptyMessage(FC_REFRESH_ADAPTER);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == REFRESH) {
				yHandler.sendEmptyMessage(FC_REFRESH_ADAPTER);
			}
			if (data != null) {
				switch (requestCode) {
				case Constants.CAMERA_RESULT_CODE:
					if (tempb != null && !tempb.isRecycled())
						tempb.recycle();
					String filePath = SystemCamera.getCaptureFilePath();
					tempb = BitmapFactory.decodeFile(filePath);
					SystemCamera.getCropImageIntentForFriendcircle(this, tempb, 5, 3, 500, 300);
					SystemCamera.captureFilePath = null;
					break;
				case Constants.GALLERY_RESULT_CODE:
					if (tempb != null && !tempb.isRecycled())
						tempb.recycle();
					Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
					cursor.moveToFirst();
					String photopathString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					if (cursor != null) {
						cursor.close();
					}
					tempb = BitmapFactory.decodeFile(photopathString);
					SystemCamera.getCropImageIntentForFriendcircle(this, tempb, 5, 3, 500, 300);
					break;
//				case FC_REFRESH_ADAPTER:// 刷新列表 FIXME 赞时没有手工增加
//					Source_Dynamic source_Dynamic = (Source_Dynamic) data.getSerializableExtra("source_Dynamic");
//					if (source_Dynamic != null && adapter.mList != null) {
//						adapter.mList.add(0, source_Dynamic);
//						yHandler.sendEmptyMessage(FC_REFRESH_ADAPTER);
//					}
//					break;
				default:
					break;
				}
			} else {
				if(Fc_PicConstants.source_Dynamic!=null){
//					if (adapter.mList != null) { FIXME 赞时没有手工增加
//						adapter.mList.add(0, Fc_PicConstants.source_Dynamic);
//					}
//					yHandler.sendEmptyMessage(FC_REFRESH_ADAPTER);
					Fc_PicConstants.source_Dynamic = null;
				}else if (!CropImage.flag) {
					if (tempb != null && !tempb.isRecycled())
						tempb.recycle();
					String filePath = CameraGalleryWithClearChoiceDialog.getFilePath();
					if(filePath!=null)
					tempb = BitmapFactory.decodeFile(filePath);
					if(tempb!=null)
					SystemCamera.getCropImageIntentForFriendcircle(this, tempb, 5, 3, 500, 300);
				} else {
					CropImage.flag = false;
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 获取修剪后的图片资源
	 * 
	 * @param data
	 */
	public void readPhotoInfo(Intent data) {
		try {
			if (tempb != null && !tempb.isRecycled())
				tempb.recycle();
			Uri uri = data.getData();
			if (uri == null) {
				Bundle bundle = data.getExtras();
				tempb = (Bitmap) bundle.get("data");
			} else {
				ContentResolver cr = getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				tempb = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			toastToMessage(R.string.read_photo_fail);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (rl_comment.getVisibility() == View.VISIBLE) {
				et_content.setText("");
				et_content.clearFocus();
				hideInputMethod(et_content);
				rl_comment.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		if (ImData.isInstanciated()) {
			ImData.getInstance().removeOnDataChangeListener(this);
		}
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (tempb != null && !tempb.isRecycled())
			tempb.recycle();
		ImCore.getInstance().getConnection().removeCustomProtocolListener(this);
	}

	public interface AfterUploadImgInterface {
		public void handleAfterUpload(String url);
	}

	/**
	 * 获取朋友圈动态公用方法
	 * 
	 * @param phone
	 * @param pageNo
	 * @param readTime
	 * @return
	 */
	public NetInterfaceStatusDataStruct getFriendCircleInfoCommon(int pageNo, boolean isRefresh) {
		String key = phone + "_" + pageNo;
		Log.d(Constants.LOG_TAG, "加载第"+key+"页");
		Source_DynamicList source_dynamicList = null;
		NetInterfaceStatusDataStruct nsdf = new NetInterfaceStatusDataStruct();
		if (nsc.checkNetWorkAvliable() && (!FriendCircleCacheUtil.isReadDataCache(key, MyApplication.getInstance()) || isRefresh)) {// 网络获取
			try {
				nsdf = ni.m1_get_myvideoList(phone
						, isRefresh ? (list_s.size() == 0 ? "0" : list_s.get(0).dateTime) : (list_s.size() == 0 ? "0" : list_s.get(list_s.size() - 1).dateTime)
						, com.lb.common.util.Constants.PAGE_SIZE_INT
						, isRefresh ? (list_s.size() == 0 ? 0 : 1) : 0);
				source_dynamicList = (Source_DynamicList) nsdf.getObj();
				if (source_dynamicList != null) {
					source_dynamicList.setCacheKey(key);
					FriendCircleCacheUtil.saveObject(source_dynamicList, key, MyApplication.getInstance());
				} else {
					source_dynamicList = (Source_DynamicList) FriendCircleCacheUtil.readObject(key, MyApplication.getInstance());
					nsdf.setObj(source_dynamicList);
					if (source_dynamicList != null) {
						nsdf.setStatus(Constants.RES_SUCCESS);
					} else {
						nsdf.setStatus(Constants.RES_FAIL);
					}
				}
			} catch (Exception e) {
				source_dynamicList = (Source_DynamicList) FriendCircleCacheUtil.readObject(key, MyApplication.getInstance());
				nsdf.setObj(source_dynamicList);
				if (source_dynamicList != null) {
					nsdf.setStatus(Constants.RES_SUCCESS);
				} else {
					nsdf.setStatus(Constants.RES_FAIL);
				}
			}
		} else {
			source_dynamicList = (Source_DynamicList) FriendCircleCacheUtil.readObject(key, MyApplication.getInstance());
			nsdf.setObj(source_dynamicList);
			if (source_dynamicList != null) {
				nsdf.setStatus(Constants.RES_SUCCESS);
			} else {
				nsdf.setStatus(Constants.RES_FAIL);
			}
		}
		return nsdf;
	}

	// 初始获取动态、下拉刷新获取动态
	private void createThreadForGetDynamic() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				yHandler.sendEmptyMessage(STARTLOADING);
				NetInterfaceStatusDataStruct nsdf = getFriendCircleInfoCommon(pageNo, true);
				if (nsdf != null && nsdf.getStatus() != null) {
					if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) { // 获取数据成功
						Source_DynamicList source_dynamicList = (Source_DynamicList) nsdf.getObj();
						list = (ArrayList<VideoData>) source_dynamicList.getSourceDynamicList();
						if (list != null)
							Log.d(Constants.LOG_TAG, "list.size()=" + list.size());
						Message m = new Message();
						m.what = SUCCESS_LOADING;
						m.obj = list;
						yHandler.sendMessage(m);
					} else {
						Message m = new Message();
						m.what = FAILS_LOADING;
						m.obj = nsdf.getStatus();
						yHandler.sendMessage(m);
					}
					Message m = new Message();
					m.what = END_LOADING;
					m.obj = nsdf.getStatus();
					yHandler.sendMessage(m);
				} else {
					yHandler.sendEmptyMessage(NOWHY);
				}
			}
		});
		thread.start();
	}

	// 初始获取动态、下拉刷新获取动态
	private void createThreadForGetDynamic_loading() {
		if (list_s.size() >= pageSize&&!isLoading_suc) {// 首页小于1页，不重复加入
			isLoading_suc = true;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					NetInterfaceStatusDataStruct nsdf = getFriendCircleInfoCommon(pageNo, false);
					if (nsdf != null && nsdf.getStatus() != null) {
						if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) { // 获取数据成功
							Source_DynamicList source_dynamicList = (Source_DynamicList) nsdf.getObj();
							list = (ArrayList<VideoData>) source_dynamicList.getSourceDynamicList();
							Message m = new Message();
							m.what = SUCCESS_LOADING;
							m.obj = list;
							yHandler.sendMessage(m);
						} else {
							Message m = new Message();
							m.what = FAILS_LOADING;
							m.obj = nsdf.getStatus();
							yHandler.sendMessage(m);
						}
					} else {
						yHandler.sendEmptyMessage(NOWHY);
					}
				}
			});
			thread.start();
		}
	}

	// 提交评论线程
	public void threadForComment(final int index, final String pid) {
		try {
			final CommentData comment = new CommentData();
			if(TextUtils.isEmpty(pid)){
				comment.type = 0;
			}else{
				comment.type = 1;
			}
			comment.commentAccount = AccountData.getInstance().getBindphonenumber();
			PersonData person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
			comment.nick = mPersonController.findNameByMobile(person.account);
			comment.comentVideoID = list_s.get(index).videoID;
			comment.commentToCommentID = StringUtils.repNull(pid);
			comment.content = et_content.getText().toString();
			comment.commentToAccount = list_s.get(index).account;
			comment.commentToNick = list_s.get(index).nick;
			comment.commentID = Calendar.getInstance().getTimeInMillis() + "";//FIXME 需要服务器返回
			comment.imageurl = person.image;
			new NetIFUI_ZBRJ(FriendCircleActivity.this, new NetInterfaceListener() {
				@Override
				public void finish(NetInterfaceStatusDataStruct niStatusData) {
					if (niStatusData != null && niStatusData.getStatus() != null) {
						if (niStatusData.getStatus().equals(Constants.RES_SUCCESS)) { // success
							Message m = new Message();
							m.what = SUCCESS_COMMENT;
							m.arg1 = index;
							m.obj = comment;
							yHandler.sendMessage(m);
						} else { // fails
							yHandler.sendEmptyMessage(COMMENTIS_FAIL);
						}
					} else { // nowhy, debug or ask myyule interface developer
						yHandler.sendEmptyMessage(COMMENTIS_FAIL);
					}
				}
			}).m1_comment(comment);
		} catch (Exception e) {
			e.getStackTrace();
			yHandler.sendEmptyMessage(COMMENTIS_FAIL);
		}
	}

	/**
	 * 删除评论
	 * 
	 * @param index
	 * @param pid
	 */
	public void deteleForComment(final int index, final String pid) {
		try {
			new NetIFUI_ZBRJ(FriendCircleActivity.this, new NetInterfaceListener() {
				@Override
				public void finish(NetInterfaceStatusDataStruct nsdf) {
					if (nsdf != null && nsdf.getStatus() != null) {
						if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) { // success
							Message m = new Message();
							m.what = DETELE_COMMENT_SUC;
							m.arg1 = index;
							m.obj = pid;
							yHandler.sendMessage(m);

						} else { // fails errorCode
							yHandler.sendEmptyMessage(FriendCircleActivity.DETELE_COMMENT_FAIL);
						}
					} else { // other error
						yHandler.sendEmptyMessage(FriendCircleActivity.DETELE_COMMENT_FAIL);
					}
				}
			}).m1_del_comment(list_s.get(index).videoID, StringUtils.repNull(pid));

		} catch (Exception e) {
			e.getStackTrace();
			yHandler.sendEmptyMessage(FriendCircleActivity.DETELE_COMMENT_FAIL);
		}
	}

	public static final int STARTLOADING = 0; // 启动动画、更改ListView状态
	public static final int SUCCESS = 1; // 初始加载动态、下拉刷新加载动态 成功
	public static final int FAILS = 2; // 初始加载动态、下拉刷新加载动态 失败
	public static final int NONET = 3;
	public static final int SUCCESS_LOADING = 4; // 加载下10条动态 成功
	public static final int FAILS_LOADING = 5; // 加载下10条动态 失败
	public static final int NOWHY = 6; // 返回json无状态（result）字段

	public static final int UPD_COVER_SUCCESS = 7; // 封面上传成功
	public static final int UPD_COVER_FAILS = 8; // 封面上传失败
	public static final int UPD_COVER_NOWHY = 9; // 封面上传失败， 返回json无状态（result）字段

	public static final int GET_COVER_SUCCESS = 10; // 获取封面信息成功
	public static final int SET_BACKGROUND = 11; // 下载陈功后更新封面

	public static final int SUCCESS_COMMENT = 12;
	public static final int SHOW_KEYBOARD = 13;
	public static final int COMMENTISNULL = 14;

	public static final int REFRESH_AFTER_UP = 15;
	public static final int REFRESH_AFTER_CANCEL_UP = 16;
	public static final int FC_REFRESH_ADAPTER = 17;// 增加评论刷新

	public static final int DEL_DYNAMIC_SUC = 18;// 删除动态成功
	public static final int DEL_DYNAMIC_FAIL = 19;// 删除动态失败

	public static final int REFRESH = 20;// 刷新
	public static final int DETELE_COMMENT_SUC = 21;// 删除评论
	public static final int DETELE_COMMENT_FAIL = 22;// 删除评论失败
	public static final int COMMENTIS_FAIL = 23;// 评论失败
	
	public static final int END_LOADING = 24;// 家在结束
	
	public boolean isLoading_suc  = false;//滑动刷新标志位 

	public Handler yHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_AFTER_UP:{
				int index = (Integer) msg.obj;
				ArrayList<LikeData> ul = list_s.get(index).likes;
				LikeData up = new LikeData();
				PersonData person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
				up.likeAccount = person.account;
				up.nick = mPersonController.findNameByMobile(person.account);
				up.imageurl = person.image;
				ul.add(0,up);
				adapter.notifyDataSetChanged();
			}
				break;
			case REFRESH_AFTER_CANCEL_UP:
				int index1 = (Integer) msg.obj;
				ArrayList<LikeData> ul1 = list_s.get(index1).likes;
				if (ul1 != null && ul1.size() > 0) {
					for (int i = 0; i < ul1.size(); i++) {
						if (ul1.get(i).likeAccount.equals(AccountData.getInstance().getBindphonenumber())) {
							ul1.remove(i);
						}
					}
				}
				adapter.notifyDataSetChanged();
				break;
			case FC_REFRESH_ADAPTER:// 刷新
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				break;
			case COMMENTISNULL:
				toastToMessage(R.string.fc_comment_content_isnonull);
				break;
			case SUCCESS_COMMENT:{
//				toastToMessage(R.string.fc_comment_success);
				rl_comment.setVisibility(View.GONE);
				int index2 = (Integer) msg.arg1;
				CommentData comment = (CommentData) msg.obj;
				ArrayList<CommentData> comments = list_s.get(index2).comments;
				comments.add(0, comment);
				ImCore.getInstance().getCustomProtocolDealerManager().createDealer(comment.commentToAccount).comment_notify(comment);
				adapter.notifyDataSetChanged();
				et_content.setText("");
				hideInputMethod(et_content);
			}
				break;
			case DETELE_COMMENT_SUC:// 删除评论成功
				int index3 = (Integer) msg.arg1;
				String commentId = (String) msg.obj;
				ArrayList<CommentData> comments2 = list_s.get(index3).comments;
				if (comments2 != null) {
					for (int i = 0; i < comments2.size(); i++) {
						CommentData sc = comments2.get(i);
						if (sc != null && commentId.equals(sc.commentID)) {
							comments2.remove(i);
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
				break;
			case DETELE_COMMENT_FAIL:// 删除评论
				toastToMessage(R.string.fc_detele_comment_fail);
				break;
			case SHOW_KEYBOARD:
				showInputMethod(et_content);
				break;
			case SUCCESS:
				list_s = (ArrayList<VideoData>) msg.obj;
				if (list_s != null) {
					if(dynamic_temp!=null){
						list_s.add(0,dynamic_temp);
					}
					adapter = new FriendCircleAdapter(FriendCircleActivity.this, list_s, phone, yHandler, new ShowCommentLayoutInterface() {
						@Override
						public void showCommentLayout() {
							rl_comment.setVisibility(View.VISIBLE);
							listview.clearFocus();
							et_content.requestFocus();
							et_content.setFocusable(true);
							et_content.setFocusableInTouchMode(true);

							yHandler.sendEmptyMessageDelayed(SHOW_KEYBOARD, 100);
						}

						@Override
						public void sendComment(int index) {// 评论，回复评论
							btn_send.setOnClickListener(new MyOnClickListener(index, ""));
						}

						@Override
						public void refreshAfterUpOperator(int index, int which) {
							Message m = new Message();
							if (which == 0) {
								m.what = REFRESH_AFTER_UP;
							} else if (which == 1) {
								m.what = REFRESH_AFTER_CANCEL_UP;
							}
							m.obj = index;
							yHandler.sendMessage(m);
						}

						@Override
						public void sendComment(int index, String pid) {
							if (index != -1)
								btn_send.setOnClickListener(new MyOnClickListener(index, pid));
						}

						@Override
						public void deteltComment(int index, String pid) {
							deteleForComment(index, pid);
						}
					});
					listview.setAdapter(adapter);
					if (list_s.size() == pageSize) {
						addFooter(listview);
						if(msg.arg1!=1){
							pageNo++;
						}
					}
				} else {
					toastToMessage(R.string.fc_getdata_nothing);
				}
				endLoadingAnim();
				break;
			case FAILS:
				endLoadingAnim();
				break;
			case END_LOADING:
				endLoadingAnim();
				break;
			case SUCCESS_LOADING:
				list_sl = (ArrayList<VideoData>) msg.obj;
				if (list_sl != null) {
					removeFooter(listview);
					if (list_s.size() > 0) {
						for (VideoData sd1 : list_sl) {
							boolean b = false;
							for (VideoData sd2 : list_s) {
								if (sd1!=null&&sd2!=null&&sd1.videoID.equals(sd2.videoID)) {
									b = true;
									break;
								}
							}
							if (!b){
								list_s.add(sd1);
							}
						}
					} else {
						list_s.addAll(list_sl);
					}
					Collections.sort(list_s, new SortDynamicByDate());
					Log.d(Constants.LOG_TAG, " pageNo前 ="+pageNo);
					if (list_sl.size() == pageSize) {
						addFooter(listview);
						pageNo++;
					}
					Log.d(Constants.LOG_TAG, " pageNo后 ="+pageNo);
					isLoading_suc = false;
					adapter.notifyDataSetChanged();
				}else{
					toastToMessage(R.string.fc_getdata_nomore);
				}
				break;
			case FAILS_LOADING:
				String errorCode_loading = (String) msg.obj;
				isLoading_suc = false;
				if (!TextUtils.isEmpty(errorCode_loading)) {
					String fails = getString(R.string.fc_getdata_fails_ec);
				} else {
					toastToMessage(R.string.fc_getdata_fails);
				}

				break;
			case NONET:
				listview.switchViewState(DListViewState.LV_NORMAL); // 注意ListView状态更改
				break;
			case NOWHY:
				endLoadingAnim();
				break;
			case STARTLOADING:
				startLoadingAnim();
				break;
			case DEL_DYNAMIC_SUC:
				adapter.notifyDataSetChanged();
				break;
			case DEL_DYNAMIC_FAIL:
				toastToMessage(R.string.fc_del_dynamic_fail);
				break;
			case COMMENTIS_FAIL:
				toastToMessage(R.string.fc_comment_fail);
				break;

			default:
				break;
			}
		};
	};

	// title显示正在加载的loading
	public void startLoadingAnim() {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.loading_sync_msg);
		LinearInterpolator interpolator = new LinearInterpolator();
		anim.setInterpolator(interpolator);
		iv_loading.setVisibility(View.VISIBLE);
		iv_loading.startAnimation(anim);
		listview.switchViewState(DListViewState.LV_LOADING); // 注意ListView状态更改
	}

	// title隐藏、停止正在加载的loading
	public void endLoadingAnim() {
		iv_loading.setVisibility(View.GONE);
		iv_loading.clearAnimation();
		listview.switchViewState(DListViewState.LV_NORMAL); // 注意ListView状态更改
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (footerviewIsShow && view.getLastVisiblePosition() == view.getCount() - 1 && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			createThreadForGetDynamic_loading();
		}

		// listview非idle时隐藏评论的edittext和键盘
		if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
			if (rl_comment.getVisibility() == View.VISIBLE) {
				et_content.clearFocus();
				hideInputMethod(et_content);
				rl_comment.setVisibility(View.GONE);
				et_content.setText("");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		listview.setFirstItemIndex(firstVisibleItem);
	}

	public LayoutInflater in;
	public View footerView;

	public void initFooterView() {
		in = LayoutInflater.from(this);
		footerView = in.inflate(R.layout.friendcircle_footerview, null);
	}

	public void addFooter(ListView lv) {
		if (lv != null) {
			lv.addFooterView(footerView);
			footerviewIsShow = true;
		}
	}

	public void removeFooter(ListView lv) {
		if (lv != null) {
			lv.removeFooterView(footerView);
			footerviewIsShow = false;
		}
	}

	/**
	 * 刷新列表
	 * 
	 * @author Administrator
	 * 
	 */
	public interface ReFreshLayoutInterface {
		public void reFreshAdapter(Source_Dynamic list_item);
	}

	public interface ShowCommentLayoutInterface {
		/**
		 * 显示评论的layout
		 */
		public void showCommentLayout();

		/**
		 * 发送评论
		 * 
		 * @param index
		 */
		public void sendComment(int index);

		public void sendComment(int index, String pid);

		/**
		 * 点赞操作成功后刷新界面
		 * 
		 * @param index
		 * @param which
		 *            0-赞，1-取消赞
		 */
		public void refreshAfterUpOperator(int index, int which);

		/**
		 * 删除评论
		 * 
		 * @param index
		 * @param pid
		 */
		public void deteltComment(int index, String pid);
	}

	public class MyOnClickListener implements OnClickListener {
		public int mIndex;
		private String pid;

		public MyOnClickListener(int index, String pid) {
			this.mIndex = index;
			this.pid = pid;
		}

		@Override
		public void onClick(View v) {
			threadForComment(mIndex, pid);
		}
	}

	/**
	 * 显示键盘
	 * 
	 * @param v
	 */
	private void showInputMethod(View v) {
		InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(FriendCircleActivity.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(v, 0);
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param v
	 */
	private void hideInputMethod(View v) {
		InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(FriendCircleActivity.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * 刷新提醒消息
	 */
	@Override
	public void onDataChanged(String onconId) {
		Fc_NoReadMessageBean fnM = SortDynamicByDate.getNoReadMessage();
		if(fnM!=null)
		setMessageNum(FriendCircleActivity.this,fnM.num, fnM.mobile, fnM.list);
		yHandler.sendEmptyMessage(FC_REFRESH_ADAPTER);
	}


	@Override
	public void syn(PersonData person) {
		if(person != null && person.account.equals(phone)){
			setMobile(phone);
		}
	}


	@Override
	public void request_join_live(String account, String nick, String videoID) {
	}


	@Override
	public void response_join_live(String account, String nick, String videoID,
			String videoTitle, String accept) {
	}


	@Override
	public void private_bullet(String account, String msg, String videoID) {
	}


	@Override
	public void kick_off_video(String account, String nick, String videoID,
			String videoTitle) {
	}


	@Override
	public void mute_video(String account, String nick, String videoID,
			String videoTitle) {
	}


	@Override
	public void forbid_bullet(String videoID,String type) {
	}


	@Override
	public void friend_status(String account, String type, String videoID) {
	}


	@Override
	public void invite_video(String account, String nick, String videoID,
			String videoTitle, String playurl) {
	}


	@Override
	public void entrust_invite_video(String videoID) {
	}


	@Override
	public void comment_notify(String commenVideoID, String commentid,
			String account, String nick, String imageurl) {
		Fc_NoReadMessageBean fnM = SortDynamicByDate.getNoReadMessage();
		if(fnM!=null)
		setMessageNum(FriendCircleActivity.this,fnM.num, fnM.mobile, fnM.list);
	}
	
	private void setMobile(String mobile){
		PersonData person = mPersonController.findPerson(mobile);
		headerView_nickname.setText(mPersonController.findNameByMobile(person.account));
		headerView_sex.setText(person == null ? "" : person.sex == 1 ? getString(R.string.female_sign) : getString(R.string.male_sign));
		headerView_avatar.setPerson(mobile, person.image);
	}
	
	private void setMessageNum(final Context context,String num,String mobile,final ArrayList<VideoData> list ){
		if(!phone.equals(AccountData.getInstance().getBindphonenumber())){
			return;
		}
		if(num == null){
			newMsgNotiV.setVisibility(View.GONE);
			return;
		}
		if(num!=null&&Integer.parseInt(num) <= 0){
			newMsgNotiV.setVisibility(View.GONE);
			return;
		}
		if(list == null){
			newMsgNotiV.setVisibility(View.GONE);
			return;
		}
		newMsgNotiV.setText(getString(R.string.new_msg_noti_fmt, num));
		if(newMsgNotiV.getVisibility() == View.GONE){
			newMsgNotiV.setVisibility(View.VISIBLE);
		}
		newMsgNotiV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, FriendCircleMessageActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ArrayList<VideoData> listtemp = new ArrayList<VideoData>();
				listtemp.addAll(list);
				i.putExtra("list", listtemp);
				MyApplication.getInstance().startActivity(i);
				if (list != null && list.size() > 0) {
					for (VideoData sDynamic:list) {
						FriendCircleCacheUtil.removeDataCache(sDynamic.post_id, MyApplication.getInstance());
					}
					list.clear();
					new FCHelper(AccountData.getInstance().getUsername()).clearAllFcNoti();
				}
			}
		});
	}


	@Override
	public void focus_notify(int optType, int isSpecial, String account,
			String nick, String imageurl) {
		// TODO Auto-generated method stub
		
	}
}

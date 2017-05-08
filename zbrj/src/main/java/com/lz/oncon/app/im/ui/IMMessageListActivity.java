package com.lz.oncon.app.im.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import com.lb.common.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.util.VoiceRecorder;
import com.lb.common.util.AlertDialogListener;
import com.lb.common.util.Constants;
import com.lb.common.util.DeviceUtils;
import com.lb.common.util.DialogUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.ImageFilterActivity;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.activity.fc.selectimage.ImageItem;
import com.lz.oncon.adapter.GridViewFaceAdapter.FaceGroupLister;
import com.lz.oncon.api.SIXmppChat;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppReceiveMessageListener;
import com.lz.oncon.api.SIXmppSendMessageListener;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.SIXmppMessage.LinkMsgType;
import com.lz.oncon.api.core.im.data.ThumbnailUtils;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.data.IMContactChooserData;
import com.lz.oncon.app.im.data.IMMessageWriteData;
import com.lz.oncon.app.im.data.IMNotification;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.data.IMThreadData.Type;
import com.lz.oncon.app.im.data.ImData.OnDataChangeListener;
import com.lz.oncon.app.im.ui.common.IMMessageCommonListViewAdapter;
import com.lz.oncon.app.im.ui.common.IMMessageInputBar;
import com.lz.oncon.app.im.ui.common.IMMessageListView;
import com.lz.oncon.app.im.ui.common.IMMessageListViewOnTouchListener;
import com.lz.oncon.app.im.ui.common.VoicePlayClickListener;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.widget.TitleView;

public class IMMessageListActivity extends BaseActivity implements
		OnGlobalLayoutListener, SIXmppSendMessageListener,
		SIXmppReceiveMessageListener,
		OnDataChangeListener, SynPersonInfoListener{

	private IMMessageListView mListView;
	private TitleView title;
	private RelativeLayout rootLayout;
	private IMMessageInputBar inputBar;
	private View recordingContainer;
	private VoiceRecorder voiceRecorder;
	private ImageView micImage;
	private TextView recordingHint;

	private IMThreadData mData;
	private String mOnconId = "";
	private String msgId2Scroll = "";
	public String getmOnconId() {
		return mOnconId;
	}

	private String parseMsg = "";// 转发过来的消息
	public String playMsgId;
	private int mLaunchMode = 0;
	private ArrayList<SIXmppMessage> msgs = new ArrayList<SIXmppMessage>();
	private String mNickNameString = "";
	private String entrance = "";
	private int lastHeightDiff = 0;
	private Type threadType;

	private SIXmppChat mChat;// chat
	public IMMessageCommonListViewAdapter mAdapter;

	private PersonController mPersonController;
	public static final String TIMEZONE_CHANGED = Intent.ACTION_TIMEZONE_CHANGED;
	private PowerManager.WakeLock wakeLock;
	private Drawable[] micImages;
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		threadType = IMThreadData.Type.P2P;
		mPersonController = new PersonController();
		MyApplication.getInstance().addListener(
				com.lb.common.util.Constants.LISTENER_PUBLIC_ACCOUNT_SYN,
				this);
		mOnconId = getIntent().getStringExtra("data");
		msgId2Scroll = getIntent().hasExtra("msgId2Scroll") ? getIntent().getStringExtra("msgId2Scroll") : "";
		entrance = getIntent().getStringExtra("entrance");
		parseMsg = getIntent().getStringExtra("parseMsg");
		mLaunchMode = getIntent().getIntExtra("mLaunchMode", 0);
		mOnconId = IMUtil.removeCCode(mOnconId);
		if(mPersonController.isFriend(mOnconId)){
			mData = ImData.getInstance().getDatas().get(mOnconId);
			if (mData == null) {
				mData = new IMThreadData(mOnconId, mNickNameString,
						new ArrayList<SIXmppMessage>(), threadType);
				ImData.getInstance().addThreadData(mOnconId, mData);
			}
		}else{
			mData = ImData.getInstance().getSDatas().get(mOnconId);
			if (mData == null) {
				mData = new IMThreadData(mOnconId, mNickNameString,
						new ArrayList<SIXmppMessage>(), threadType);
				ImData.getInstance().addSThreadData(mOnconId, mData);
			}
		}	
		if (!TextUtils.isEmpty(entrance)) {
			mNickNameString = entrance;
		} else {
			mNickNameString = mData.getNickName();
			if (TextUtils.isEmpty(mNickNameString)
					|| mOnconId.equals(mNickNameString)) {
				mNickNameString = mPersonController.findNameByMobile(mOnconId);
			}
		}
		setContentView(R.layout.app_im_message);
		inputBar = (IMMessageInputBar) findViewById(R.id.im_message__input_layout);
		if (ContactMsgCenterActivity.LAUNCH_MODE_COMEFROM_MULTIP2P == mLaunchMode) {
			inputBar.setInfo(threadType, mOnconId, mNickNameString, msgs, true);
		} else {
			inputBar.setInfo(threadType, mOnconId, mNickNameString, msgs, false);
		}

		inputBar.setFaceGroupLister(new FaceGroupLister() {
			@Override
			public void faceSendResult() {
				mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
			}
		});
		mListView = (IMMessageListView) findViewById(R.id.im_message__list);
		initListView();
		setListeners();
		title = (TitleView) findViewById(R.id.title);
		title.setTitle(mNickNameString);
		mChat = ImCore.getInstance().getChatManager().createChat(mOnconId);
		ImCore.getInstance().getConnection().addReceivedMessageListener(this);
		ImCore.getInstance().getConnection().addSendMessageListener(this);
		MyApplication.getInstance().addListener(Constants.LISTENER_SYN_PERSONINFO, this);

		rootLayout = (RelativeLayout) findViewById(R.id.root);
		rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
		if (ContactMsgCenterActivity.LAUNCH_MODE_TRANSMIT == mLaunchMode) {// 判断是否转发过来的
			if (!TextUtils.isEmpty(parseMsg)) {
				SIXmppMessage xmppMessage = forwardMsg(parseMsg, mOnconId,
						SIXmppThreadInfo.Type.P2P);
				sendMsg(xmppMessage);
			} else {
				toastToMessage(R.string.message_transmit_fail);
			}
			//mLaunchMode = 0;
			IMContactChooserData.getInstance().clear();
		} else if (ContactMsgCenterActivity.LAUNCH_MODE_IMAGETEXTMSG == mLaunchMode) {// 判断是否转发过来的
			sendImageTextMsg();
		} else if (ContactMsgCenterActivity.LAUNCH_MODE_PUBLICACCOUNTNAMECARD == mLaunchMode) {
			sendPublicAccountCardMsg();
		}
		initTimeZoneReceiver();
		initVoice();
	}
	
	private void initVoice(){
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		recordingContainer = findViewById(R.id.recording_container);
		voiceRecorder = new VoiceRecorder(micImageHandler);
		micImage = (ImageView) findViewById(R.id.mic_image);
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
						getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
						getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
						getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
						getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09),
						getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
						getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13),
						getResources().getDrawable(R.drawable.record_animate_14), };
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		inputBar.mRecordButton.setOnTouchListener(new PressToSpeakListen());
	}

	public void initListView() {
		if (mOnconId != null) {
			mListView.setInfo(mOnconId, msgs);
			mAdapter = new IMMessageCommonListViewAdapter(this,
					mOnconId, mNickNameString, msgs, threadType);
			mListView.setAdapter(mAdapter);
			mListView.setOnTouchListener(new IMMessageListViewOnTouchListener(
					inputBar));
			if(!TextUtils.isEmpty(msgId2Scroll)){
				mListView.scrollToMsg(msgId2Scroll);
			}
		}
	}

	public void initTimeZoneReceiver() {
		IntentFilter iFilter = new IntentFilter(TIMEZONE_CHANGED);
		registerReceiver(timeZoneReceiver, iFilter);
	}

	public void setListeners() {
		MyApplication.getInstance().addListener(
				Constants.LISTENER_UPDATE_LISTVIEW_AFTER_SYNC_MSG, this);
	}

	@Override
	protected void onResume() {
		IMNotification.getInstance().addOnconidFilter(mOnconId);
		IMNotification.getInstance().removeNewMessageNotication(mOnconId);
		IMNotification.getInstance().cancelNotification();
		inputBar.setText(IMMessageWriteData.getInstance().getMessage(mOnconId));
		super.onResume();
	}

	/**
	 * 时区切换的receiver
	 */
	private BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TIMEZONE_CHANGED.equals(intent.getAction())) {
				mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
			}
		}
	};
	private static final int RECEIVE_MESSAGE_OR_REFRESH_LIST = 300;
	private static final int RECEIVE_MESSAGE_OR_REFRESH_LIST2 = 301;

	public UIHandler mHandler = new UIHandler();

	public class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVE_MESSAGE_OR_REFRESH_LIST:
				if (mLaunchMode == ContactMsgCenterActivity.LAUNCH_MODE_TRANSMIT
				|| mLaunchMode == ContactMsgCenterActivity.LAUNCH_MODE_IMAGETEXTMSG
				|| mLaunchMode == ContactMsgCenterActivity.LAUNCH_MODE_PUBLICACCOUNTNAMECARD){
					mLaunchMode = 0;
					showShareResultDialog();
				}
				mListView.refresh();
				break;
			case RECEIVE_MESSAGE_OR_REFRESH_LIST2:
				msgs.add((SIXmppMessage) msg.obj);
				mListView.refresh();				
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 分享成功后选择返回或留下
	 */
	private void showShareResultDialog() {
		DialogUtil.showTipDialog(this, R.string.success,
				R.string.bak, R.string.stay, true,
				new AlertDialogListener() {

					@Override
					public void positive() {
						finish();
					}

					@Override
					public void negative() {
					}
				});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			backToMessageCenter();
			break;
		case R.id.common_title_TV_right:
			Intent intent = new Intent(this, IMP2PSettingActivity.class);
			intent.putExtra("onconid", mOnconId);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	public void backToMessageCenter() {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String filePath = null;
		if (resultCode == Activity.RESULT_OK) {
			if ((requestCode == SystemCamera.SHOW_PICTURE || requestCode == Constants.GALLERY_RESULT_CODE)
					&& data != null) {
				Uri uri = data.getData();
				if (uri.toString().startsWith("file:///")) {
					filePath = uri.toString().substring(7);
				} else if (uri.toString().startsWith(
						"content://com.alensw.PicFolder")) {
					filePath = uri.toString().substring(43);
				} else {
					filePath = IMUtil.getFilePathFromResourceUri(uri);
				}
				if (filePath == null) {
					toastToMessage(R.string.read_photo_fail);
					return;
				} else {
					File file = new File(filePath);
					if (!file.exists()) {
						toastToMessage(R.string.read_photo_fail);
						return;
					}
					String destFilePath = SystemCamera.getPicturePath(filePath);
					if (file != null && file.exists()) {
						if (destFilePath.endsWith(".png")) {
							destFilePath = destFilePath.substring(0,
									destFilePath.length() - 4) + ".jpg";
						}
						ThumbnailUtils.createImageThumbnail(destFilePath,
								filePath, Images.Thumbnails.MINI_KIND, false);
						filePath = destFilePath;
					}
				}
			} else if (requestCode == SystemCamera.TAKE_PICTURE
					|| requestCode == Constants.CAMERA_RESULT_CODE) {
				filePath = SystemCamera.getCaptureFilePath();
				File file = new File(filePath);
				if (file != null && file.exists()) {
					ThumbnailUtils.createImageThumbnail(filePath, filePath,
							Images.Thumbnails.MINI_KIND, false);
				}
				if (file != null && !file.exists()) {
					toastToMessage(getString(R.string.camera)
							+ getString(R.string.fail));
					return;
				}
			} else if (requestCode == SystemCamera.FILTER_PICTURE) {
				if (resultCode == RESULT_OK) {
					SIXmppMessage xmppMessage = mChat.sendImageMessage(
							data.getStringExtra("imagepath"),
							SIXmppThreadInfo.Type.P2P);
					sendMsg(xmppMessage);
				} else {
				}
			}
			if (Constants.ActivitytoMorePicActivity == requestCode) {
				List<ImageItem> sendImage = Fc_PicConstants
						.getSelectImageItemList();
				if (sendImage != null && sendImage.size() > 0) {
					for (int i = 0; i < sendImage.size(); i++) {
						ImageItem imageItem = sendImage.get(i);
						if (imageItem != null
								&& !TextUtils.isEmpty(imageItem.imagePath)) {
							File f = new File(imageItem.imagePath);
							if (f.exists()) {
								SIXmppMessage xmppMessage = mChat
										.sendImageMessage(imageItem.imagePath,
												SIXmppThreadInfo.Type.P2P);
								sendMsg(xmppMessage);
							}
						}
					}
					// 发送完成后清空临时图片路径列表
					Fc_PicConstants.selectlist.clear();
				}

			}
			if (requestCode == SystemCamera.TAKE_PICTURE
					|| requestCode == SystemCamera.SHOW_PICTURE) {
				if (filePath != null && mOnconId != null
						&& !mOnconId.equals("")) {
					Intent i = new Intent(this, ImageFilterActivity.class);
					i.putExtra("image_path", filePath);
					i.putExtra("data", mOnconId);
					startActivityForResult(i, SystemCamera.FILTER_PICTURE);
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!inputBar.isMoreShow()) {
				backToMessageCenter();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		parseMsg = "";
		IMNotification.getInstance().removeOnconidFilter(mOnconId);
		IMMessageWriteData.getInstance().putMessage(mOnconId, inputBar.getText());
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}
		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		try {
			IMNotification.getInstance().removeNewMessageNotication(mOnconId);
			ImCore.getInstance().getConnection().removeReceivedMessageListener(this);
			ImCore.getInstance().getConnection().removeSendMessageListener(this);
			if (mListView != null)
				mListView.releaseResources();
			MyApplication.getInstance().removeListener(Constants.LISTENER_PUBLIC_ACCOUNT_SYN, this);
			MyApplication.getInstance().removeListener(Constants.LISTENER_SYN_PERSONINFO, this);
			super.onDestroy();
			if (timeZoneReceiver != null) {
				IMMessageListActivity.this.unregisterReceiver(timeZoneReceiver);
			}
			msgs.clear();

			MyApplication.getInstance().removeListener(Constants.LISTENER_UPDATE_LISTVIEW_AFTER_SYNC_MSG, this);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	@Override
	public void receiveMessage(String onconidOrGroupid, SIXmppMessage message) {
		if (message != null && onconidOrGroupid != null
				&& onconidOrGroupid.equals(mOnconId)) {
			IMNotification.getInstance().removeNewMessageNotication(mOnconId);
			if (isAddMessage(message)) {
				mHandler.obtainMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST2,
						message).sendToTarget();
			}
		}
	}

	/**
	 * 实时刷新发送消息的发送状态
	 */
	@Override
	public void statusChanged(SIXmppMessage message) {
		try {
			for (SIXmppMessage msg : msgs) {
				if (message.getId().equalsIgnoreCase(msg.getId())) {
					msg.setTime(message.getTime());
					msg.setStatus(message.getStatus());
					mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
					break;
				}
			}
		} catch (Exception e) {
		}
	}
	
	@Override
	public void onGlobalLayout() {
		int heightDiff = rootLayout.getRootView().getHeight()
				- rootLayout.getHeight();
		if (heightDiff > 100 && lastHeightDiff != heightDiff) {// Soft KeyBoard
																// Shown
			mListView.scroll2Bottom();
			inputBar.isMoreShow();
		} else {// Soft KeyBoard Hidden
		}
		lastHeightDiff = heightDiff;
	}

	public void sendMsg(SIXmppMessage xmppMessage) {
		ImData.getInstance().addMessageData(mOnconId, xmppMessage);
		mHandler.obtainMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST2, xmppMessage)
				.sendToTarget();
	}

	private SIXmppMessage forwardMsg(String textContent, String toOnconid,
			SIXmppThreadInfo.Type mtype) {
		int idx = getIntent().getIntExtra("contentType", 0);
		ContentType contentType = ContentType.values()[idx];
		return mChat.forwardMessage(textContent, toOnconid, mtype, contentType);
	}

	@Override
	public void onDataChanged(String onconid) {
		try {
			if (mOnconId.equals(onconid)) {
				mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 是否加入消息队列，应用提醒不加入消息队列
	 * 
	 * @param msg
	 * @return
	 */
	private boolean isAddMessage(SIXmppMessage msg) {
		boolean isAdd = true;
		if (msg != null) {
			switch (msg.getContentType()) {
			case TYPE_APP_NOTI: {
				isAdd = false;
			}
			default:
				break;
			}
		}
		return isAdd;
	}

	private void sendImageTextMsg() {
		showProgressDialog(R.string.sending, false);
		new Thread() {
			public void run() {
				SIXmppMessage xmppMessage = mChat.sendImageTextMessage(
						getIntent().getStringExtra("title"), getIntent()
								.getStringExtra("brief"), getIntent()
								.getStringExtra("image_url"), getIntent()
								.getStringExtra("detail_url"), getIntent()
								.getStringExtra("pub_account"), getIntent()
								.getStringExtra("author"),
						SIXmppThreadInfo.Type.P2P);
				sendMsg(xmppMessage);
				String share_text = getIntent().getStringExtra("share_text");
				if (!TextUtils.isEmpty(share_text)) {
					xmppMessage = mChat.sendTextMessage(getIntent()
							.getStringExtra("share_text"),
							SIXmppThreadInfo.Type.P2P);
					sendMsg(xmppMessage);
				}
//				mLaunchMode = 0;
				IMContactChooserData.getInstance().clear();
				IMMessageListActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						hideProgressDialog();
					}
				});
			}
		}.start();
	}

	private void sendLinkMsg() {
		showProgressDialog(R.string.sending, false);
		new Thread() {
			public void run() {
				SIXmppMessage xmppMessage = mChat.sendLinkMessage(
						LinkMsgType.FRIEND,
						getIntent().getStringExtra("title"), getIntent()
								.getStringExtra("brief"), getIntent()
								.getStringExtra("detail_url"), getIntent()
								.getStringExtra("image_url"), "", "",
						getIntent().getStringExtra("pub_account"),
						SIXmppThreadInfo.Type.P2P);
				sendMsg(xmppMessage);
				String share_text = getIntent().getStringExtra("share_text");
				if (!TextUtils.isEmpty(share_text)) {
					xmppMessage = mChat.sendTextMessage(getIntent()
							.getStringExtra("share_text"),
							SIXmppThreadInfo.Type.P2P);
					sendMsg(xmppMessage);
				}
//				mLaunchMode = 0;
				IMContactChooserData.getInstance().clear();
				IMMessageListActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						hideProgressDialog();
					}
				});
			}
		}.start();
	}

	private void sendPublicAccountCardMsg() {
		showProgressDialog(R.string.sending, false);
		new Thread() {
			public void run() {
				SIXmppMessage xmppMessage = mChat
						.sendPublicAccountNameCardMessage(getIntent()
								.getStringExtra("id"), getIntent()
								.getStringExtra("name"),
								SIXmppThreadInfo.Type.P2P);
				sendMsg(xmppMessage);
//				mLaunchMode = 0;
				IMContactChooserData.getInstance().clear();
				IMMessageListActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						hideProgressDialog();
					}
				});
			}
		}.start();
	}

	@Override
	public void viewMessage(String onconid, ArrayList<SIXmppMessage> messages) {
		// 无消除图标需求
	}

	@Override
	public void statusChanged(ArrayList<SIXmppMessage> messages) {
		// 发送状态变化-目前只有已阅读
		try {
			for (SIXmppMessage msg : msgs) {
				for (SIXmppMessage message : messages) {
					if (message.getId().equalsIgnoreCase(msg.getId())) {
						msg.setTime(message.getTime());
						msg.setStatus(message.getStatus());
					}
				}
			}
			mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!DeviceUtils.isExternalStorageWriteable()) {
					IMMessageListActivity.this.toastToMessage(R.string.Send_voice_need_sdcard_support);
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, mOnconId, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					IMMessageListActivity.this.toastToMessage(R.string.recoding_fail);
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(mOnconId),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						IMMessageListActivity.this.toastToMessage(st3);
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}
	
	/**
	 * 发送语音
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			SIXmppMessage message = mChat.sendAudioMessage(filePath, Integer.parseInt(length), SIXmppThreadInfo.Type.P2P);
			sendMsg(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void syn(PersonData person) {
		if(person != null && mOnconId.equals(person.account)){
			mHandler.sendEmptyMessage(RECEIVE_MESSAGE_OR_REFRESH_LIST);
		}
	}
}
package com.lz.oncon.app.im.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.FileCore;
import com.lb.common.util.Log;
import com.xuanbo.xuan.R;
import com.lb.zbrj.activity.PersonActivity;
import com.lb.zbrj.activity.PersonListActivity;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.FocusListener;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.LoginActivity;
import com.lz.oncon.activity.RetainMsgActivity;
import com.lz.oncon.api.CustomProtocolListener;
import com.lz.oncon.api.SIXmppAccout;
import com.lz.oncon.api.SIXmppConnectionListener;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppSendMessageListener;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.app.im.data.IMMessageWriteData;
import com.lz.oncon.app.im.data.IMNotification;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.data.ImData.OnDataChangeListener;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.receiver.OnNotiReceiver;
import com.lz.oncon.widget.CircleHeadImageView;

public class IMListActivity extends BaseActivity implements View.OnCreateContextMenuListener, SIXmppConnectionListener,
		OnDataChangeListener, OnItemClickListener, SynPersonInfoListener,
		SIXmppSendMessageListener, CustomProtocolListener, FocusListener {

	private CircleHeadImageView headV;
	private IMListView mListView = null;
	private TextView  nicknameV, friendV;
	//用户要求删除,title 修改为图片提醒方式
//		private TextView friendV,mTitleTextView;
		private ImageView loadingImageView;
	private RelativeLayout mWarningLayout;
	private IMListViewAdapter mAdapter;
	private ArrayList<String> mIndexs = new ArrayList<String>();
	private HashMap<String, IMThreadData> mDatas = new HashMap<String, IMThreadData>();
	AlertDialog clearMsgDialog;

	private ImageView search_bar;
	private PersonController mPersonController;
	private PersonData person;
	private RelativeLayout mTitleBarLL;
	
	public static final String TIMEZONE_CHANGED = Intent.ACTION_TIMEZONE_CHANGED;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPersonController = new PersonController();
		person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
		if (TextUtils.isEmpty(AccountData.getInstance().getUsername())) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		ImCore.getInstance().getConnection().addConnectionListener(this);
		ImData.getInstance().addOnDataChangeListener(this);
		ImCore.getInstance().getConnection().addSendMessageListener(this);
		ImCore.getInstance().getConnection().addCustomProtocolListener(this);
		MyApplication.getInstance().addListener(Constants.LISTENER_CONTACT_PHOTO_UPLOAD, this);
		MyApplication.getInstance().addListener(Constants.LISTENER_SYN_PERSONINFO, this);
		MyApplication.getInstance().addListener(Constants.LISTENER_FOCUS, this);
		MyApplication.getInstance().addListener(Constants.LISTENER_PUBLIC_ACCOUNT_SYN, this);
		MyApplication.getInstance().addListener(Constants.LISTENER_FC_NOTI, this);// 朋友圈
		initView();
		initTimeZoneReceiver();
		// 检查垃圾
		checkTrash();
	}
	
	private void setPersonInfo(){
		nicknameV.setText(mPersonController.findNameByMobile(person.account));
		headV.setPerson(person.account, person.image);
	}
	
	private void setFriendsInfo(){
		friendV.setText(mPersonController.getFriends("").size() + "");
	}

	private void initTimeZoneReceiver() {
		IntentFilter iFilter = new IntentFilter(TIMEZONE_CHANGED);
		registerReceiver(timeZoneReceiver, iFilter);
	}

	private void initView() {
		setContentView(R.layout.app_im_thread_list);
		// title
		mTitleBarLL = (RelativeLayout) findViewById(R.id.im_thread_list__layout_title_fl);
		//mTitleTextView = (TextView) findViewById(R.id.common_title_TV_center);
		loadingImageView = (ImageView) findViewById(R.id.loading);
		nicknameV = (TextView) findViewById(R.id.nickname);
		headV = (CircleHeadImageView) findViewById(R.id.headpic);
		setPersonInfo();
		friendV = (TextView) findViewById(R.id.title_right);
		setFriendsInfo();
//		friendV = (TextView) findViewById(R.id.friend);
//		friendV.setText(getString(R.string.friend_fmt, mPersonController.getFriends("").size()));
		// listview
		mListView = (IMListView) findViewById(R.id.im_thread_list_DLL);
		mListView.setOnCreateContextMenuListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnRefreshListener(new IMListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mListView.onRefreshComplete();
			}
		});
		mWarningLayout = (RelativeLayout) findViewById(R.id.im_thread_list__Layout_warning);
		initSearchListView();
		mHandler.sendEmptyMessage(MESSAGE_INIT_UI);

		clearMsgDialog = new AlertDialog.Builder(IMListActivity.this).setTitle(R.string.retain_msg).setMessage(R.string.garbage_much)
				.setPositiveButton(R.string.clean, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						showProgressDialog(R.string.wait, false);
						new ClearMsgRecordAsyncTask().execute("3");
					}
				}).setNeutralButton(R.string.setting, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(IMListActivity.this, RetainMsgActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearMsgDialog.dismiss();
					}
				}).create();
	}

	private void initSearchListView() {
		search_bar = mListView.search_view;

		search_bar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openSearchPage();
					}
				});
	}
	
	private void openSearchPage() {
		mTitleBarLL.setVisibility(View.GONE);
		// mTitleBarLL.startLayoutAnimation();

		Intent intent = new Intent(IMListActivity.this, IMRecordThreadsSearchActivity.class);
		startActivityForResult(intent, 999);
		overridePendingTransition(0, 0);

	}

	private void checkTrash() {
		String checkTime = AccountData.getInstance().getCheckTime();
		String chioceItem = AccountData.getInstance().getChioceItem();
		long currentTime = System.currentTimeMillis();
		Long lastTime = TextUtils.isEmpty(checkTime) ? 0L : Long.valueOf(checkTime);
		long sevenDay = 7 * 24 * 60 * 60 * 1000L;
		int count = 0;
		if (currentTime - lastTime > sevenDay) {
			AccountData.getInstance().setCheckTime(String.valueOf(currentTime));
			count = ImData.getInstance().queryAllThreadsMessageCount();
			if (10000 < count) {
				clearMsgDialog.show();
			} else {
				if (!TextUtils.isEmpty(chioceItem)) {
					if ("1".equals(chioceItem)) {
						new ClearMsgRecordAsyncTask().execute("1");
					} else if ("3".equals(chioceItem)) {
						new ClearMsgRecordAsyncTask().execute("3");
					}
				} else {
					new ClearMsgRecordAsyncTask().execute("1");
				}
			}
		}
	}

	/**
	 * 时区切换的receiver
	 */
	private BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TIMEZONE_CHANGED.equals(intent.getAction())) {
				mHandler.sendEmptyMessage(MESSAGE_UPDATE_UI);
			}
		}
	};

	private final int MESSAGE_UPDATE_UI = 0;
	private final int MESSAGE_UPDATE_WARNING_UI = 1;
	private final int MESSAGE_INIT_UI = 2;
	private final int MESSAGE_WAKEUP = 4;
	private static final int SET_PERSON_INFO = 5;
	private static final int SET_FRIENDS_NUM = 6;

	private UIHandler mHandler = new UIHandler();

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			NetInterfaceStatusDataStruct niStatusData = null;
			switch (msg.what) {
			case MESSAGE_INIT_UI:
				changeWarningStatusUI(ImCore.getInstance().getConnection().getConnectionStatus(), false);
				putData();
				mAdapter = new IMListViewAdapter(IMListActivity.this, mIndexs, mDatas);
				mListView.setAdapter(mAdapter);
				break;
			case MESSAGE_UPDATE_UI:
				putData();
				mAdapter.notifyDataSetChanged();
				break;
			case MESSAGE_UPDATE_WARNING_UI:
				changeWarningStatusUI(ImCore.getInstance().getConnection().getConnectionStatus(), true);
				break;
			case MESSAGE_WAKEUP:
				// 唤醒心跳
				if (!TextUtils.isEmpty(AccountData.getInstance().getBindphonenumber())) {
					SIXmppAccout account = ImCore.getInstance().getAccout();
					if (TextUtils.isEmpty(account.getUsername())) {
						ImCore.getInstance().setAccout();
					}
					IMNotification.getInstance();
					ImData.getInstance();
					IMMessageWriteData.getInstance();
				}
				break;
			case SET_PERSON_INFO:// 1
				setPersonInfo();
				break;
			case SET_FRIENDS_NUM:
				setFriendsInfo();
				break;
			default:
				break;
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AppUtil.toBackground(this);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		if (ImCore.isInstanciated()) {
			ImCore.getInstance().getConnection().removeSendMessageListener(this);
			ImCore.getInstance().getConnection().removeCustomProtocolListener(this);
		}
		if (ImData.isInstanciated()) {
			ImData.getInstance().removeOnDataChangeListener(this);
		}
		MyApplication.getInstance().removeListener(Constants.LISTENER_CONTACT_PHOTO_UPLOAD, this);
		MyApplication.getInstance().removeListener(Constants.LISTENER_SYN_PERSONINFO, this);
		MyApplication.getInstance().removeListener(Constants.LISTENER_FOCUS, this);
		MyApplication.getInstance().removeListener(Constants.LISTENER_PUBLIC_ACCOUNT_SYN, this);
		MyApplication.getInstance().removeListener(Constants.LISTENER_FC_NOTI, this);// 朋友圈
		super.onDestroy();
		if (timeZoneReceiver != null) {
			IMListActivity.this.unregisterReceiver(timeZoneReceiver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent i = new Intent(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
		MyApplication.getInstance().sendBroadcast(i);
		mHandler.sendEmptyMessage(MESSAGE_WAKEUP);
		mHandler.sendEmptyMessage(MESSAGE_UPDATE_UI);
	}

	private static final int CONTEXTMENU_ITEM_DELETE = 0;
	private int contextmenu_groupId = 0;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(contextmenu_groupId++, CONTEXTMENU_ITEM_DELETE, 0, getString(R.string.delete));
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXTMENU_ITEM_DELETE:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
							String roster = ImData.getInstance().getIndexs().get(menuInfo.position - 1);
							ImData.getInstance().deleteThreadData(roster);
							IMNotification.getInstance().removeNewMessageNotication(roster);
							putData();
							mAdapter.notifyDataSetChanged();
						}
					});
				}
			}).start();
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 999:
			mTitleBarLL.setVisibility(View.VISIBLE);

			break;
		}
	}

	@Override
	public void loginStatusChanged(int status) {
		mHandler.sendEmptyMessage(MESSAGE_UPDATE_WARNING_UI);
	}

	private void changeWarningStatusUI(int status, boolean isRelogin) {
		String connecting = "(" + getString(R.string.im_warning_network_reconnect) + ")";
		String title = getResources().getString(R.string.message);
		switch (status) {
		case SUCCESS:
			mWarningLayout.setVisibility(View.GONE);
//			mTitleTextView.setText(title);
			endLoadingAnim();
			break;
		case FAILED:
			mWarningLayout.setVisibility(View.VISIBLE);
//			mTitleTextView.setText(title);
			endLoadingAnim();
			break;
		case CONNECTTING:
			mWarningLayout.setVisibility(View.GONE);
//			mTitleTextView.setText(title + connecting);
			startLoadingAnim();
			break;
		default:
			break;
		}
	}
	
	// title左侧显示正在加载的loading
	public void startLoadingAnim() {
		if(loadingImageView.getVisibility() == View.VISIBLE)
			return;
		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.loading_sync_msg);
		loadingImageView.setVisibility(View.VISIBLE);
		loadingImageView.startAnimation(anim);
	}

	// title左侧隐藏、停止正在加载的loading
	public void endLoadingAnim() {
		loadingImageView.setVisibility(View.GONE);
		loadingImageView.clearAnimation();
	}
	
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.friend:
			Intent intent1 = new Intent(IMListActivity.this, PersonListActivity.class);
			startActivity(intent1);
			break;
		case R.id.common_title_TV_center_linear:
			break;
		case R.id.im_thread_list_Button_sethead:
			intent = new Intent(IMListActivity.this, PersonActivity.class);
			startActivity(intent);
			break;
		case R.id.im_thread_list__Layout_warning:
			// 唤醒心跳
			if (!TextUtils.isEmpty(AccountData.getInstance().getBindphonenumber())) {
				SIXmppAccout account = ImCore.getInstance().getAccout();
				if (TextUtils.isEmpty(account.getUsername())) {
					ImCore.getInstance().setAccout();
				}
				IMNotification.getInstance();
				ImData.getInstance();
				IMMessageWriteData.getInstance();
			}
			mHandler.sendEmptyMessage(MESSAGE_UPDATE_WARNING_UI);
			break;
		}
	}

	@Override
	public void onDataChanged(String onconid) {
//		Log.e(Constants.LOG_TAG, "onDataChanged:" + onconid);
		mHandler.sendEmptyMessage(MESSAGE_UPDATE_UI);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int idx = position - 1;
		IMThreadData data = ImData.getInstance().getDatas().get(ImData.getInstance().getIndexs().get(idx));
		switch (data.getType()) {
		case P2P: {
			if(SIXmppThreadInfo.ID_STRANGER.equals(data.getId())){
				Intent intent = new Intent(this, IMSListActivity.class);
				startActivity(intent);
			}else{
				Intent intent = new Intent(this, IMMessageListActivity.class);
				intent.putExtra("data", data.getId());
				startActivity(intent);
			}
		}
		break;
		case GROUP:
			break;
		default:
			break;
		}
	}
	
	/**
	 * 这个是出现 map遍历的同时删除了元素 的异常的解决方案
	 */
	private synchronized void putData() {
		mIndexs.clear();
		mIndexs.addAll(ImData.getInstance().getIndexs());
		mDatas.clear();
		mDatas.putAll(ImData.getInstance().getDatas());
	}

	@Override
	public void statusChanged(SIXmppMessage msg) {
		if(msg.getStatus() != null && msg.getStatus().ordinal() != msg.getOldStatus().ordinal()
				&& msg.getSourceType() != null && msg.getSourceType().ordinal() == SourceType.SEND_MESSAGE.ordinal()
				&& (msg.getOldStatus().ordinal() == SendStatus.STATUS_DRAFT.ordinal()
				|| msg.getOldStatus().ordinal() == SendStatus.STATUS_ERROR.ordinal())){
			Log.e(Constants.LOG_TAG, "statusChanged:" + msg.getId());
			mHandler.sendEmptyMessage(MESSAGE_UPDATE_UI);
		}
	}

	@Override
	public void statusChanged(ArrayList<SIXmppMessage> messages) {
		//目前更新状态主要是已阅读,会话界面不需要更新该状态
	}

	class ClearMsgRecordAsyncTask extends AsyncTask<String, Integer, String> {
		String type = "";

		@Override
		protected String doInBackground(String... parameter) {
			type = parameter[0];
			try {
				if ("1".equals(type)) {
					ImData.getInstance().deleteAllThreadsMessageByTime(DateUtil.get3MonthAgoMillis());
				} else if ("3".equals(type)) {
					ImData.getInstance().deleteAllThreadsMessageByTime(DateUtil.get7DayAgoMillis());
				} else {
					ImData.getInstance().deleteThreadDataAll();
				}
				IMNotification.getInstance().clear();
				MyApplication.getInstance().mActivityManager.popActivity(IMMessageListActivity.class);

				String onconpath = "";
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					onconpath = getFilesDir().getAbsolutePath() + File.separator + "oncon" + File.separator;
				} else {
					onconpath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator
							+ "oncon" + File.separator;
				}
				if ("1".equals(type)) {
					File f = new File(Constants.THUMB_BIG);// 多图发送大图
					if (f.exists())
						FileCore.RecursionDeleteFileByTime(f, Long.valueOf(DateUtil.get3MonthAgoMillis()));
					f = new File(Constants.THUMB_SMALL);// 多图发送小图
					if (f.exists())
						FileCore.RecursionDeleteFileByTime(f, Long.valueOf(DateUtil.get3MonthAgoMillis()));
					f = new File(onconpath);// 聊天相关语音和图片
					if (f.exists())
						FileCore.RecursionDeleteFileByTime(f, Long.valueOf(DateUtil.get3MonthAgoMillis()));
				} else if ("3".equals(type)) {
					File f = new File(Constants.THUMB_BIG);// 多图发送大图
					if (f.exists())
						FileCore.RecursionDeleteFileByTime(f, Long.valueOf(DateUtil.get7DayAgoMillis()));
					f = new File(Constants.THUMB_SMALL);// 多图发送小图
					if (f.exists())
						FileCore.RecursionDeleteFileByTime(f, Long.valueOf(DateUtil.get7DayAgoMillis()));
					f = new File(onconpath);// 聊天相关语音和图片
					if (f.exists())
						FileCore.RecursionDeleteFileByTime(f, Long.valueOf(DateUtil.get7DayAgoMillis()));
				} else {
					File f = new File(Constants.THUMB_BIG);// 多图发送大图
					if (f.exists())
						FileCore.RecursionDeleteFile(f);
					f = new File(Constants.THUMB_SMALL);// 多图发送小图
					if (f.exists())
						FileCore.RecursionDeleteFile(f);
					f = new File(onconpath);// 聊天相关语音和图片
					if (f.exists())
						FileCore.RecursionDeleteFile(f);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					hideProgressDialog();
					IMListActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							try {
								hideProgressDialog();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "";
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

	@Override
	public void request_join_live(String account, String nick, String videoID) {//请求加入直播间
	}

	@Override
	public void response_join_live(String account, String nick, String videoID,
			String videoTitle, String accept) {//请求加入直播间
	}

	@Override
	public void private_bullet(String account, String msg, String videoID) {//直播间内发送私聊信息
	}

	@Override
	public void kick_off_video(String account, String nick, String videoID,
			String videoTitle) {//直播间踢出
	}

	@Override
	public void mute_video(String account, String nick, String videoID,
			String videoTitle) {//直播间禁言
	}

	@Override
	public void forbid_bullet(String videoID,String type) {//服务器禁止弹幕推送
	}

	@Override
	public void friend_status(String account, String type, String videoID) {//推送好友当前操作状态
//		Log.e(Constants.LOG_TAG, "friend_status:account:" + account + ",type:" + type + ",videoID:" + videoID);
		mHandler.sendEmptyMessage(MESSAGE_UPDATE_UI);
	}

	@Override
	public void invite_video(String account, String nick, String videoID,
			String videoTitle, String playurl) {//邀请观看视频信息
	}

	@Override
	public void entrust_invite_video(String videoID) {//委托邀请	
	}

	@Override
	public void comment_notify(String commenVideoID, String commentid,
			String account, String nick, String imageurl) {//评论通知
	}

	@Override
	public void syn(PersonData person) {
		if(person != null && person.account.equals(AccountData.getInstance().getBindphonenumber())){
			this.person = person;
			mHandler.sendEmptyMessage(SET_PERSON_INFO);
		}
		if(person != null && mDatas.containsKey(person.account)){
			mHandler.sendEmptyMessage(MESSAGE_UPDATE_UI);
		}
	}

	@Override
	public void syn(ArrayList<FansData> focus) {
		mHandler.sendEmptyMessage(SET_FRIENDS_NUM);
	}

	@Override
	public void focus_notify(int optType, int isSpecial, String account,
			String nick, String imageurl) {
	}
}
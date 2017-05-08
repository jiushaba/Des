package com.lz.oncon.app.im.contact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.lb.common.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lb.common.util.Clipboard;
import com.lb.common.util.Constants;
import com.lb.common.util.SmsUtil;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.adapter.ChooserAdapter;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.core.im.network.NetworkStatusCheck;
import com.lz.oncon.app.im.data.IMContactChooserData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.ui.IMMessageListActivity;
import com.lz.oncon.app.im.ui.IMNewMessageActivity;
import com.lz.oncon.app.im.ui.IMPerContactListView;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.application.ActivityManager;
import com.lz.oncon.widget.BottomPopupWindow;
import com.lz.oncon.widget.MyLetterListView;
import com.lz.oncon.widget.SearchBar;
import com.lz.oncon.widget.TitleView;
import com.lz.oncon.widget.MyLetterListView.OnTouchingLetterChangedListener;

public class ContactMsgCenterActivity extends BaseActivity implements OnTouchingLetterChangedListener {
	
	private SearchBar search_bar;
	private TitleView title;
	private IMPerContactListView mPerContactListView;
	private MyLetterListView friend_MLLV;
	private ChooserSelectedListView mSelectedListView;
	
	private PersonController mPersonController;
	private TextView overlay;
	private OverlayThread overlayThread;
	private String real_word = "";

	private ChooserAdapter contactAdapter;

	private List<FansData> mDatas;
	// 这里负责判断是邀请还是创建类型
	public static final String LAUNCH_MODE = "launch";
	public static final int LAUNCH_MODE_NEW = 0;
	public static final int LAUNCH_MODE_INVITE = 1;// 圈聊增加模式
	public static final int LAUNCH_MODE_SELECT = 2;// 选择联系人模式
	public static final int LAUNCH_MODE_GROUPSMS = 6;// 群发短信
	public static final int LAUNCH_MODE_TRANSMIT = 10;// 消息转发
	public static final int LAUNCH_MODE_COMEFROM_MULTIP2P = 11;// 一对一群发
	public static final int LAUNCH_MODE_P2PTOGROUP = 12;// 单聊转群聊选人
	public static final int LAUNCH_MODE_IMAGETEXTMSG = 13;// 图文消息转发
	public static final int LAUNCH_MODE_PUBLICACCOUNTNAMECARD = 16;// 公众号名片转发
	
	public static final int LAUNCH_MODE_SEND_RECOMMAND = 17;//推荐给好友
	public static final int LAUNCH_MODE_LINKMSG = 18;// 网页消息转发
	private int mLaunchMode = LAUNCH_MODE_NEW;
	public static final String NEXT_ACTIVITY = "nextactivity";
	
	public enum DisplayViewType {
		GLOBAL, SEARCH;
	}

	private DisplayViewType sType = DisplayViewType.GLOBAL;
	private DisplayViewType ssType = DisplayViewType.GLOBAL;
	private String mobile = "";

	private BottomPopupWindow bottomPopupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBundleData();
		setContentView(R.layout.contact_tab_switcher);
		mPersonController = new PersonController();
		initSearchListView();
		initViews();
		setValues();
		setListeners();
		initCharactIndexes();
	}

	public void getBundleData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(LAUNCH_MODE)) {
				mLaunchMode = bundle.getInt(LAUNCH_MODE);
				switch (mLaunchMode) {
				case LAUNCH_MODE_NEW:
					break;
				case LAUNCH_MODE_INVITE:
					break;
				case LAUNCH_MODE_SELECT:
					break;
				case LAUNCH_MODE_GROUPSMS:
					break;
				}
			}
		}
	}

	/**
	 * Description: 初始化搜索组件 void
	 */
	private void initSearchListView() {
		search_bar = (SearchBar) findViewById(R.id.search_bar);
		search_bar.mSearchListener = new SearchBar.SearchListener() {

			@Override
			public void search() {
				if (DisplayViewType.SEARCH == sType) {
					sType = ssType;
				}
				doSearch();
			}

			@Override
			public void clear() {
				if (sType != DisplayViewType.GLOBAL) {
					goBack();
				}
			}

		};
	}

	/*
	 * 初始化UI组件
	 */
	public void initViews() {
		// 标题
		title = (TitleView) findViewById(R.id.title);
		title.setRightImgVisible(false);
		// 手机通讯录列表
		mPerContactListView = (IMPerContactListView) findViewById(R.id.friend_LV);
		friend_MLLV = (MyLetterListView) findViewById(R.id.friend_MLLV);
		friend_MLLV.setOnTouchingLetterChangedListener(this);

		// 选中人的列表
		mSelectedListView = (ChooserSelectedListView) findViewById(R.id.im_contactchooser_selectedlist);
		mSelectedListView.getmOKButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mLaunchMode == LAUNCH_MODE_INVITE) {
					inviteMembers();
				} else if (mLaunchMode == LAUNCH_MODE_SELECT) {// 群发消息
					bottomPopupWindow = new BottomPopupWindow(ContactMsgCenterActivity.this);
					int count = IMContactChooserData.getInstance().getMemberCount();
					if (count == 1) {
						bottomPopupWindow.addButton(R.string.dialog_to_oncon, new View.OnClickListener() {
							@Override
							public void onClick(View v) {// 发消息
								showSendDialog();
								bottomPopupWindow.dismiss();
							}
						}, false);
					}else {
						bottomPopupWindow.addButton(R.string.group_send, new View.OnClickListener() {
							@Override
							public void onClick(View v) {// 群发消息
								showSendDialog();
								bottomPopupWindow.dismiss();
							}
						}, false);
					}
					

					if (bottomPopupWindow != null && !bottomPopupWindow.isShowing())
						bottomPopupWindow.showAtLocation(findViewById(R.id.topLayout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else if (mLaunchMode == LAUNCH_MODE_GROUPSMS) {
					Intent intent = new Intent(ContactMsgCenterActivity.this, IMNewMessageActivity.class);
					intent.putExtra(ContactMsgCenterActivity.LAUNCH_MODE, ContactMsgCenterActivity.LAUNCH_MODE_GROUPSMS);
					startActivity(intent);
					finish();
				} else if (mLaunchMode == LAUNCH_MODE_SEND_RECOMMAND) {//推荐给好友
					if (!NetworkStatusCheck.isNetworkConnected(ContactMsgCenterActivity.this)) {
						ContactMsgCenterActivity.this.toastToMessage(R.string.im_warning_network_check2);
						return;
					}
					sendRecommand();
				} else if (mLaunchMode == LAUNCH_MODE_TRANSMIT) {// 转发
					ArrayList<String> members = IMContactChooserData.getInstance().getMemberNumber();
					if (members != null && members.size() == 1) {
						String onconmobile = members.get(0);
						if (TextUtils.isEmpty(onconmobile)) {
							return;
						}
						Intent intent = new Intent(ContactMsgCenterActivity.this, IMMessageListActivity.class);
						intent.putExtra("mLaunchMode", LAUNCH_MODE_TRANSMIT);
						intent.putExtra("data", onconmobile);
						intent.putExtra("contentType", getIntent().getIntExtra("contentType", 0));
						intent.putExtra("parseMsg", StringUtils.repNull(Clipboard.getText(ContactMsgCenterActivity.this)));
						startActivity(intent);
						finish();
					} else if (members != null && members.size() > 1) {
						toastToMessage(R.string.message_transmit);
					}
				} else if (mLaunchMode == LAUNCH_MODE_P2PTOGROUP) {// 单聊转群聊
				} else if (mLaunchMode == LAUNCH_MODE_IMAGETEXTMSG) {// 图文消息转发
					ArrayList<String> members = IMContactChooserData.getInstance().getMemberNumber();
					if (members != null && members.size() == 1) {
						String onconmobile = members.get(0);
						if (TextUtils.isEmpty(onconmobile)) {
							return;
						}
						Intent intent = new Intent(ContactMsgCenterActivity.this, IMMessageListActivity.class);
						intent.putExtra("mLaunchMode", mLaunchMode);
						intent.putExtra("data", onconmobile);
						intent.putExtra("title", getIntent().getStringExtra("title"));
						intent.putExtra("brief", getIntent().getStringExtra("brief"));
						intent.putExtra("image_url", getIntent().getStringExtra("image_url"));
						intent.putExtra("detail_url", getIntent().getStringExtra("detail_url"));
						intent.putExtra("pub_account", getIntent().getStringExtra("pub_account"));
						intent.putExtra("author", getIntent().getStringExtra("author"));
						intent.putExtra("share_text", getIntent().getStringExtra("share_text"));
						startActivity(intent);
						finish();
					}else if (members != null && members.size() > 1) {
						toastToMessage(R.string.message_transmit);
					}
				} else if (mLaunchMode == LAUNCH_MODE_PUBLICACCOUNTNAMECARD) {// 公共账号名片转发
					ArrayList<String> members = IMContactChooserData.getInstance().getMemberNumber();
					if (members != null && members.size() == 1) {
						String onconmobile = members.get(0);
						if (TextUtils.isEmpty(onconmobile)) {
							return;
						}
						Intent intent = new Intent(ContactMsgCenterActivity.this, IMMessageListActivity.class);
						intent.putExtra("mLaunchMode", LAUNCH_MODE_PUBLICACCOUNTNAMECARD);
						intent.putExtra("data", onconmobile);
						intent.putExtra("id", getIntent().getStringExtra("id"));
						intent.putExtra("name", getIntent().getStringExtra("name"));
						startActivity(intent);
						finish();
					} else if (members != null && members.size() > 1) {
						toastToMessage(R.string.message_imagetext_nomulti);
					}
				}
			}
		});

	}
	
	public void setValues() {
		mobile = getIntent().hasExtra("mobile") ? getIntent().getStringExtra("mobile") : "";
		//查询好友
		title.setTitle(getString(R.string.friend));
		sType = DisplayViewType.GLOBAL;
		if (progressDialog != null && !progressDialog.isShowing()) {
			progressDialog.setMessage(getString(R.string.loading));
			progressDialog.show();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				mDatas = findFriend(real_word);
				contactAdapter = new ChooserAdapter(ContactMsgCenterActivity.this, mDatas, handler);
				handler.sendEmptyMessage(IMConstants.ADAPTER_FRESH);
			}
		}).start();
	}

	public void setListeners() {
	}

	/**
	 * Description: 初始化字母索引 void
	 */
	public void initCharactIndexes() {
		overlayThread = new OverlayThread();
		overlay = (TextView) LayoutInflater.from(this).inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);

	}

	// 邀请朋友
	private void inviteMembers() {
		
	}

	public void showDefaultLayout() {
		sType = DisplayViewType.GLOBAL;
		title.setTitle(getString(R.string.friend));
	}

	public void goBack() {
		title.setRightImgVisible(false);
		if (sType == DisplayViewType.SEARCH) {
			if (!TextUtils.isEmpty(search_bar.search_word.getText().toString())) {
				search_bar.search_word.setText("");
			}
			showDefaultLayout();
		} 
		else if (sType == DisplayViewType.GLOBAL) {
			if (mLaunchMode == LAUNCH_MODE_INVITE) {
				IMContactChooserData.getInstance().removeAllMembers();
				finish();
			} else if (mLaunchMode == LAUNCH_MODE_SEND_RECOMMAND) {//推荐给好友
				IMContactChooserData.getInstance().removeAllMembers();
				finish();
			} else {
				IMContactChooserData.getInstance().removeAllMembers();
				finish();
			}
		}
	}

	/**
	 * Description: 点击确定按钮弹出创建圈子对话框 void
	 */
	private void showSendDialog() {
		int count = IMContactChooserData.getInstance().getMemberCount();
		if (count == 0) {
			toastToMessage(R.string.message_minselect_people);
			return;
		} else {
			if (!NetworkStatusCheck.isNetworkConnected(ContactMsgCenterActivity.this)) {
				ContactMsgCenterActivity.this.toastToMessage(R.string.im_warning_network_check2);
				return;
			}
			createMultiSend();
		}
	}

	/**
	 * Description: 创建群发消息 void
	 */
	public void createMultiSend() {
		int count = IMContactChooserData.getInstance().getMemberCount();
		if (count == 0) {
			// 提示未选中
			toastToMessage(R.string.no_receiver_selected);
			return;
		} else if (count > 20) {
			// 提示不得超过20人
			toastToMessage(R.string.no_more_than_20_receivers_selected);
			return;
		} else {
			if (progressDialog != null && !progressDialog.isShowing()) {
				progressDialog.setMessage(getString(R.string.loading));
				progressDialog.show();
			}
			if (count == 1) {// 只有一人则进入点对点
				Intent intent = new Intent(ContactMsgCenterActivity.this, IMMessageListActivity.class);
				intent.putExtra("mLaunchMode", LAUNCH_MODE_COMEFROM_MULTIP2P);
				intent.putExtra("data", mobile);
				startActivity(intent);
				IMContactChooserData.getInstance().clear();
				finish();
			} else {
			}
		}
	}

	/**
	 * 获取选中的手机号码集合
	 * 
	 * @return
	 */
	private String mOnconString(String divide) {
		StringBuffer mOnconId = new StringBuffer();
		Iterator<Entry<String, String>> it = IMContactChooserData.getInstance().getMemberNumberAndNames().entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			if (i > 0) {
				if (TextUtils.isEmpty(divide)) {
					mOnconId.append(",");
				} else {
					mOnconId.append(divide);
				}
			}
			mOnconId.append(entry.getKey());
			i++;
		}
		IMContactChooserData.getInstance().clear();
		return mOnconId.toString();
	}

	private static final int DIALOG_ID_SENDSMS = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ID_SENDSMS) {
			return new AlertDialog.Builder(ContactMsgCenterActivity.this).setTitle(R.string.app_name).setMessage(R.string.not_yixin_user_info)
					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							SmsUtil.sendSMS(ContactMsgCenterActivity.this, mOnconString(";"), "");
							IMContactChooserData.getInstance().clear();
							ContactMsgCenterActivity.this.finish();
						}
					}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					}).create();

		}
		return super.onCreateDialog(id);
	}

	/**
	 * Description: 搜索通讯录 void
	 */
	private void doSearch() {
		String word = search_bar.search_word.getText().toString();
		final String real_word = StringUtils.subString(word);
		switch (sType) {
		case GLOBAL: {
			this.showProgressDialog(R.string.wait, false);
			new Thread(new Runnable(){
				public void run(){
					try{
						contactAdapter.setList(findFriend(real_word));
					}catch(Exception e){
						Log.e(Constants.LOG_TAG, e.getMessage(), e);
					}finally{
						ContactMsgCenterActivity.this.runOnUiThread(new Runnable(){
							public void run(){
								contactAdapter.notifyDataSetChanged();
								ContactMsgCenterActivity.this.hideProgressDialog();
								hideProgressDialog();
							}
						});
					}
				}
			}).start();
		}
			break;
		default:
			break;
		}
		ssType = sType;
		sType = DisplayViewType.SEARCH;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(overlay);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case IMConstants.CHOOSER_ADDMEMBER:
				Object object = (Object) msg.obj;
				String addid = "";
				if (object instanceof FansData) {
					addid = ((FansData) object).account;
				}
				mSelectedListView.addMember(addid, object, contactAdapter);
				break;
			case IMConstants.CHOOSER_SUBMEMBER:
				String subid = (String) msg.obj;
				mSelectedListView.removeMember(subid, contactAdapter);
				break;
			case IMConstants.ADAPTER_FRESH:
				mPerContactListView.setAdapter(contactAdapter);
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			case IMConstants.DISMISSDIALOG:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			case IMConstants.COLOSE_DIALOG:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				mSelectedListView.removeAllMembers();
				if (contactAdapter != null) {
					contactAdapter.notifyDataSetChanged();
				}
				break;
			case IMConstants.COLOSE_DIALOG_BYFAILED:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Toast.makeText(ContactMsgCenterActivity.this, "创建圈聊失败", Toast.LENGTH_SHORT).show();
				mSelectedListView.removeAllMembers();
				if (contactAdapter != null) {
					contactAdapter.notifyDataSetChanged();
				}
				break;
			case IMConstants.SEND_INVITATION:
				NetInterfaceStatusDataStruct nisds = (NetInterfaceStatusDataStruct) msg.obj;
				if (Constants.RES_SUCCESS.equals(nisds.getStatus())) {
					ContactMsgCenterActivity.this.toastToMessage(R.string.recommend_success);
					IMContactChooserData.getInstance().removeAllMembers();
					finish();
				} else {
					ContactMsgCenterActivity.this.toastToMessage(R.string.recommend_fail);
				}
				break;
			case IMConstants.SHOW_DIALOG:
				showDialog(DIALOG_ID_SENDSMS);
				break;
			case IMConstants.SHOW_ALL_BUTTON:
				title.setRightImgVisible(true, getResources().getString(R.string.all_chooser_contact));
				break;
			case IMConstants.SHOW_CANCAL_BUTTON:
				title.setRightImgVisible(true, getResources().getString(R.string.cancal_chooser_contact));
				break;
			}
		}
	};

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			goBack();
			break;
		case R.id.common_title_TV_right:
			List<FansData> phoneList = null;
			if (contactAdapter != null)
				phoneList = contactAdapter.getList();
			if (phoneList != null && phoneList.size() > 0) {
				for (int i = 0; i < phoneList.size(); i++) {
					Message msg = Message.obtain();
					String mobile = phoneList.get(i).account;
					if (getResources().getString(R.string.all_chooser_contact).equals(title.getRightView().getText().toString())) {
						msg.what = IMConstants.SEARCH_CHOOSER_ADDMEMBER;
						msg.obj = phoneList.get(i);
					} else {
						msg.what = IMConstants.SEARCH_CHOOSER_SUBMEMBER;
						msg.obj = mobile;
					}
					handler.sendMessage(msg);
				}
				if (getResources().getString(R.string.all_chooser_contact).equals(title.getRightView().getText().toString())) {
					handler.sendEmptyMessage(IMConstants.SHOW_CANCAL_BUTTON);
				} else {
					handler.sendEmptyMessage(IMConstants.SHOW_ALL_BUTTON);
				}
				contactAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.search_button:
			if (DisplayViewType.SEARCH == sType) {
				sType = ssType;
			}
			doSearch();
			break;
		default:
			break;
		}
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		if (contactAdapter.getIndexer().get(s) != null) {
			int position = contactAdapter.getIndexer().get(s);
			mPerContactListView.setSelection(position);
		}
		overlay.setText(s);
		overlay.setVisibility(View.VISIBLE);
		handler.removeCallbacks(overlayThread);
		handler.postDelayed(overlayThread, 1500);
	}

	private ArrayList<FansData> findFriend(String searchword) {
		return mPersonController.getFriends(searchword);
	}
	
	private void sendRecommand(){
		if (IMContactChooserData.getInstance().getMembers() != null && IMContactChooserData.getInstance().getMembers().size() > 0) {
			this.showProgressDialog(R.string.wait, true);
			new Thread(){
				public void run(){
					try{
						ArrayList<String> memberTemps = new ArrayList<String>();
						memberTemps.addAll(IMContactChooserData.getInstance().getMembers().keySet());
						ArrayList<Activity> activitys = new ArrayList<Activity>();
						activitys.addAll(ActivityManager.getActivityStack());
						for(String member:memberTemps){
							SIXmppMessage msg = ImCore.getInstance().getCustomProtocolDealerManager().createDealer(member).recommand_friend(mobile);
							boolean isAdd = false;
							try{
								for(Activity activity:activitys){
									if(activity != null && activity instanceof IMMessageListActivity
											&& member.equals(((IMMessageListActivity)activity).getmOnconId())){
										((IMMessageListActivity)activity).sendMsg(msg);
										isAdd = true;
										break;
									}
								}
							}catch(Exception e){
								Log.e(e.getMessage(), e);
							}
							if(!isAdd){
								ImData.getInstance().addMessageData(member, msg);
							}
						}
					}catch(Exception e){
						Log.e(e.getMessage(), e);
					}finally{
						ContactMsgCenterActivity.this.runOnUiThread(new Runnable(){
							public void run(){
								ContactMsgCenterActivity.this.hideProgressDialog();
								IMContactChooserData.getInstance().removeAllMembers();
								finish();
							}
						});
					}
				}
			}.start();
		} else {
			ContactMsgCenterActivity.this.toastToMessage(R.string.please_choose_contacts);
		}
	}
}
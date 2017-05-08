package com.lb.zbrj.activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lb.zbrj.data.PrivateConfigInfo;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.connections.adapter.CheckItem;
import com.lz.oncon.activity.connections.adapter.CheckItemListAdapter;
import com.lz.oncon.activity.connections.widget.MenuBarViewHolder;

/**
 * 隐私设置
 */
public class PrivateConfigActivity extends BaseActivity {
	private MenuBarViewHolder menuBarViewHolder;
	private PrivateConfigInfo mPrivateConfigInfo, tempPrivateConfigInfo;
	private LinkedList<CheckItem> lookMyProfileLinkedList, addMeLinkedList,
			sendMessageLinkedList, feedLinkedList;
	private CheckItemListAdapter lookMyProfileCheckItemListAdapter,
			addMeCheckItemListAdapter, sendMessageCheckItemListAdapter,
			feedCheckItemListAdapter;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_private_config);
		initViews();
		initData();
	}

	private void initViews() {
		menuBarViewHolder = MenuBarViewHolder.create(this);
		menuBarViewHolder.fillTitle(getString(R.string.private_config));
		menuBarViewHolder.fillLeft("", R.drawable.ic_back,
				new OnClickListener() {

					public void onClick(View v) {
						onBackPressed();
					}
				});
		((TextView) findViewById(R.id.private_config_profile).findViewById(
				R.id.section_flag_txt))
				.setText(R.string.who_can_see_my_profile);
		lookMyProfileLinkedList = new LinkedList<CheckItem>();
		lookMyProfileLinkedList.add(new CheckItem(getString(R.string.only_me),
				false));
		lookMyProfileLinkedList.add(new CheckItem(
				getString(R.string.friend_friend), false));
		lookMyProfileLinkedList.add(new CheckItem(
				getString(R.string.all_people), false));
		
		ListView lookMyProfileListView = (ListView) findViewById(R.id.private_config_profile_list);
		lookMyProfileCheckItemListAdapter = new CheckItemListAdapter(this,
				R.layout.check_list_item_view, lookMyProfileLinkedList,null);
		
		lookMyProfileListView.setAdapter(lookMyProfileCheckItemListAdapter);
		lookMyProfileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tempPrivateConfigInfo.pinfo = String.valueOf(position + 1);
				setPrivateConfigInfo(tempPrivateConfigInfo);
			}
		});

		((TextView) findViewById(R.id.private_config_add_friend).findViewById(
				R.id.section_flag_txt)).setText(R.string.who_can_add_me);
		addMeLinkedList = new LinkedList<CheckItem>();
		addMeLinkedList.add(new CheckItem(getString(R.string.only_from_friend),
				false));
		addMeLinkedList.add(new CheckItem(getString(R.string.friend_friend),
				false));
		addMeLinkedList
				.add(new CheckItem(getString(R.string.all_people), false));
		ListView addMeListView = (ListView) findViewById(R.id.private_config_add_friend_list);
		addMeCheckItemListAdapter = new CheckItemListAdapter(this,
				R.layout.check_list_item_view, addMeLinkedList,null);
		addMeListView.setAdapter(addMeCheckItemListAdapter);
		addMeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tempPrivateConfigInfo.friend = String.valueOf(position + 1);
				setPrivateConfigInfo(tempPrivateConfigInfo);
			}
		});

		((TextView) findViewById(R.id.private_config_send_msg).findViewById(
				R.id.section_flag_txt))
				.setText(R.string.who_can_send_me_message);
		sendMessageLinkedList = new LinkedList<CheckItem>();
		sendMessageLinkedList.add(new CheckItem(
				getString(R.string.only_friends), false));
		sendMessageLinkedList.add(new CheckItem(
				getString(R.string.friend_friend), false));
		sendMessageLinkedList.add(new CheckItem(getString(R.string.all_people),
				false));
		ListView sendMessageListView = (ListView) findViewById(R.id.private_config_send_msg_list);
		sendMessageCheckItemListAdapter = new CheckItemListAdapter(this,
				R.layout.check_list_item_view, sendMessageLinkedList,null);
		sendMessageListView.setAdapter(sendMessageCheckItemListAdapter);
		sendMessageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tempPrivateConfigInfo.message = String.valueOf(position + 1);
				setPrivateConfigInfo(tempPrivateConfigInfo);
			}
		});

		((TextView) findViewById(R.id.private_config_feed_scope).findViewById(
				R.id.section_flag_txt)).setText(R.string.filter_dynamic);
		feedLinkedList = new LinkedList<CheckItem>();
		feedLinkedList.add(new CheckItem(getString(R.string.only_friends),
				false));
		feedLinkedList.add(new CheckItem(getString(R.string.friend_friend),
				false));

		feedLinkedList.add(new CheckItem(getString(R.string.comm_friends_all_industry), false));
		ListView feedListView = (ListView) findViewById(R.id.private_config_feed_scope_list);
		feedCheckItemListAdapter = new CheckItemListAdapter(this,
				R.layout.check_list_item_view, feedLinkedList,null);
		feedListView.setAdapter(feedCheckItemListAdapter);
		feedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tempPrivateConfigInfo.dp = String.valueOf(position + 1);
				setPrivateConfigInfo(tempPrivateConfigInfo);
			}
		});
	}

	private void initData() {
		mPrivateConfigInfo = new PrivateConfigInfo();
		tempPrivateConfigInfo = new PrivateConfigInfo();
		getPrivateConfigInfo();
	}

	/**
	 * 获取隐私设置信息
	 * 
	 * @param type
	 */
	private void getPrivateConfigInfo() {
		new NetIFUI_ZBRJ(this, new NetInterfaceListener() {
			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				if ("0".equals(niStatusData.getStatus())) {
					Message msg = Message.obtain();
					msg.obj = niStatusData;
					msg.what = GET_PRIVATE_INFO_SUSSESS;
					mUIHandler.sendMessage(msg);
				} else {
					Message msg = Message.obtain();
					msg.obj = niStatusData;
					msg.what = GET_PRIVATE_INFO_FAILED;
					mUIHandler.sendMessage(msg);
				}
			}
		})
//				.getAccountSecret()  FIXME 获取隐私设置
		;
	}

	/**
	 * 隐私设置
	 * 
	 * @param type
	 */
	private void setPrivateConfigInfo(final PrivateConfigInfo privateConfigInfo) {
		new NetIFUI_ZBRJ(this, new NetInterfaceListener() {
			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				if ("0".equals(niStatusData.getStatus())) {
					Message msg = Message.obtain();
					msg.obj = niStatusData;
					msg.what = SET_PRIVATE_INFO_SUSSESS;
					mUIHandler.sendMessage(msg);
				} else {
					Message msg = Message.obtain();
					msg.obj = niStatusData;
					msg.what = SET_PRIVATE_INFO_FAILED;
					mUIHandler.sendMessage(msg);
				}
			}
		})
//			.setAccountSecret(privateConfigInfo)   FIXME 获取隐私设置
		;
	}

	private void setValues(PrivateConfigInfo privateConfigInfo) {
		if (null == privateConfigInfo)
			return;
		if ("1".equals(privateConfigInfo.pinfo)) {
			lookMyProfileLinkedList.get(0).checked = true;
			lookMyProfileLinkedList.get(1).checked = false;
			lookMyProfileLinkedList.get(2).checked = false;
		} else if ("2".equals(privateConfigInfo.pinfo)) {
			lookMyProfileLinkedList.get(0).checked = false;
			lookMyProfileLinkedList.get(1).checked = true;
			lookMyProfileLinkedList.get(2).checked = false;
		} else if ("3".equals(privateConfigInfo.pinfo)) {
			lookMyProfileLinkedList.get(0).checked = false;
			lookMyProfileLinkedList.get(1).checked = false;
			lookMyProfileLinkedList.get(2).checked = true;
		}
		lookMyProfileCheckItemListAdapter.notifyDataSetChanged();

		if ("1".equals(privateConfigInfo.friend)) {
			addMeLinkedList.get(0).checked = true;
			addMeLinkedList.get(1).checked = false;
			addMeLinkedList.get(2).checked = false;
		} else if ("2".equals(privateConfigInfo.friend)) {
			addMeLinkedList.get(0).checked = false;
			addMeLinkedList.get(1).checked = true;
			addMeLinkedList.get(2).checked = false;
		} else if ("3".equals(privateConfigInfo.friend)) {
			addMeLinkedList.get(0).checked = false;
			addMeLinkedList.get(1).checked = false;
			addMeLinkedList.get(2).checked = true;
		}
		addMeCheckItemListAdapter.notifyDataSetChanged();

		if ("1".equals(privateConfigInfo.message)) {
			sendMessageLinkedList.get(0).checked = true;
			sendMessageLinkedList.get(1).checked = false;
			sendMessageLinkedList.get(2).checked = false;
		} else if ("2".equals(privateConfigInfo.message)) {
			sendMessageLinkedList.get(0).checked = false;
			sendMessageLinkedList.get(1).checked = true;
			sendMessageLinkedList.get(2).checked = false;
		} else if ("3".equals(privateConfigInfo.message)) {
			sendMessageLinkedList.get(0).checked = false;
			sendMessageLinkedList.get(1).checked = false;
			sendMessageLinkedList.get(2).checked = true;
		}
		sendMessageCheckItemListAdapter.notifyDataSetChanged();

		if ("1".equals(privateConfigInfo.dp)) {
			feedLinkedList.get(0).checked = true;
			feedLinkedList.get(1).checked = false;
			feedLinkedList.get(2).checked = false;
		} else if ("2".equals(privateConfigInfo.dp)) {
			feedLinkedList.get(0).checked = false;
			feedLinkedList.get(1).checked = true;
			feedLinkedList.get(2).checked = false;
		} else if ("3".equals(privateConfigInfo.dp)) {
			feedLinkedList.get(0).checked = false;
			feedLinkedList.get(1).checked = false;
			feedLinkedList.get(2).checked = true;
		}
		feedCheckItemListAdapter.notifyDataSetChanged();
	}

	private UIHandler mUIHandler = new UIHandler(PrivateConfigActivity.this);

	private static final int GET_PRIVATE_INFO_SUSSESS = 1;
	private static final int GET_PRIVATE_INFO_FAILED = 2;

	private static final int SET_PRIVATE_INFO_SUSSESS = 3;
	private static final int SET_PRIVATE_INFO_FAILED = 4;

	static class UIHandler extends Handler {
		WeakReference<PrivateConfigActivity> mActivity;

		UIHandler(PrivateConfigActivity activity) {
			mActivity = new WeakReference<PrivateConfigActivity>(activity);

		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final PrivateConfigActivity theActivity = mActivity.get();
			NetInterfaceStatusDataStruct niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
			switch (msg.what) {
			case GET_PRIVATE_INFO_SUSSESS:
				theActivity.mPrivateConfigInfo = (PrivateConfigInfo) niStatusData
						.getObj();
				theActivity.tempPrivateConfigInfo = theActivity
						.copy(theActivity.mPrivateConfigInfo);
				if (null != theActivity.mPrivateConfigInfo) {
					theActivity.setValues(theActivity.mPrivateConfigInfo);
				}
				break;
			case GET_PRIVATE_INFO_FAILED:
				break;
			case SET_PRIVATE_INFO_SUSSESS:
				theActivity.mPrivateConfigInfo = theActivity
						.copy(theActivity.tempPrivateConfigInfo);
				theActivity.setValues(theActivity.mPrivateConfigInfo);
				break;
			case SET_PRIVATE_INFO_FAILED:
				theActivity.tempPrivateConfigInfo = theActivity
						.copy(theActivity.mPrivateConfigInfo);
				theActivity.setValues(theActivity.mPrivateConfigInfo);
				break;
			}
		}
	}

	private PrivateConfigInfo copy(PrivateConfigInfo privateConfigInfo) {
		PrivateConfigInfo pCInfo = new PrivateConfigInfo();
		pCInfo.pinfo = privateConfigInfo.pinfo;
		pCInfo.friend = privateConfigInfo.friend;
		pCInfo.message = privateConfigInfo.message;
		pCInfo.dp = privateConfigInfo.dp;
		return pCInfo;
	}
}
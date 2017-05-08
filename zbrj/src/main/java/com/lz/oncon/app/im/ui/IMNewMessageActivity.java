package com.lz.oncon.app.im.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.lb.common.util.ImageUtil;
import com.lb.common.util.Constants.ActivityState;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.data.IMContactChooserData;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.ui.common.IMMessageInputBar;
import com.lz.oncon.app.im.ui.common.ContactBubbleView.OnDeleteItemListener;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.widget.AutoWrapViewGroup;

public class IMNewMessageActivity extends BaseActivity implements View.OnClickListener {

	public static final String MESSAGE = "msg";
	
	private AutoWrapViewGroup membersV;
	private IMMessageInputBar inputBar;

	private int padding;
	private StringBuffer mOnconId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		padding = ImageUtil.convertDipToPx(this, 5);
		initView();
	}

	protected void onDestroy(){
		super.onDestroy();
		IMContactChooserData.getInstance().clear();
	}
	
	private void initView() {
		setContentView(R.layout.app_im_new_message);
		inputBar = (IMMessageInputBar) findViewById(R.id.im_message__input_layout);
		inputBar.mChangeButton.setVisibility(View.GONE);
		inputBar.mMoreButton.setVisibility(View.GONE);
		
		membersV = (AutoWrapViewGroup) findViewById(R.id.membersV);
		addMembers();
	}
	
	private void addMembers(){
		membersV.removeAllViews();
		addLabel();
		mOnconId = new StringBuffer();
		Iterator<Entry<String, String>> it = IMContactChooserData.getInstance().getMemberNumberAndNames().entrySet().iterator();
		int i=0;
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			addMember(entry.getKey(),entry.getValue());
			if(i > 0){
				mOnconId.append(",");
			}
			mOnconId.append(entry.getKey());
			i++;
		}
		inputBar.setInfo(IMThreadData.Type.BATCH, mOnconId.toString(), "", new ArrayList<SIXmppMessage>(),false);
	}
	
	private void addLabel(){
		LinearLayout ll = new LinearLayout(this);
		TextView textV = new TextView(this);
		textV.setPadding(padding, padding, padding, padding);
		textV.setTextColor(Color.BLACK);
		textV.setText(getString(R.string.receiver)+":");
		textV.setTextSize(20.0f);
		textV.setGravity(Gravity.CENTER);
		LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.topMargin = padding;
		textV.setLayoutParams(lp);
		ll.addView(textV);
		membersV.addView(ll);
	}
	
	private void addMember(String number, String name){
		final com.lz.oncon.app.im.ui.common.ContactBubbleView contactView = new com.lz.oncon.app.im.ui.common.ContactBubbleView(this);
		contactView.setData(number, name);
		contactView.setOnDeleteItemListener(new OnDeleteItemListener(){
			@Override
			public void onDeleteItem(String item) {
				membersV.removeView(contactView);
				IMContactChooserData.getInstance().getMembers().remove(item);
				
				mOnconId = new StringBuffer();
				Iterator<Entry<String, String>> it = IMContactChooserData.getInstance().getMemberNumberAndNames().entrySet().iterator();
				int i=0;
				while(it.hasNext()){
					Entry<String, String> entry = it.next();
					if(i > 0){
						mOnconId.append(",");
					}
					mOnconId.append(entry.getKey());
					i++;
				}
				inputBar.setmOnconId(mOnconId.toString());
			}
			@Override
			public void onDeleteAllItem() {
			}
		});
		membersV.addView(contactView);
	}

	private void sendMessage(final String message, boolean isSend) {
		// send
		String firstRosterId = null;
		
		HashMap<String, Object> members= IMContactChooserData.getInstance().getMembers();
		ArrayList<String> onconIds = new ArrayList<String>();
		if (members != null) {
			Iterator<Entry<String, Object>> it = members.entrySet().iterator();
	        while(it.hasNext()){
	        	Entry<String, Object> d = it.next();
	        	final String rosterid = d.getKey();
				onconIds.add(rosterid);
				if (firstRosterId == null) {
					firstRosterId = rosterid;
				}
	        }
	        //群发消息
	        if(onconIds.size() > 0){
//	        	String to = mOnconId.toString();
//	        	SIXmppMessage xmppMessage = ImCore.getInstance().getConnection().sendBatchMsg(to, message,SIXmppMessage.ContentType.TYPE_TEXT);
//	        	IMThreadData mData = new IMThreadData(to, to, new ArrayList<SIXmppMessage>(), IMThreadData.Type.BATCH);
//	        	ImData.getInstance().addThreadData(to, mData);
//				ImData.getInstance().addMessageData(to, xmppMessage);
	        }
		}
		if (firstRosterId != null) {
			Intent intent1 = AppUtil.getMainActIntent(this);
			intent1.putExtra("ActivityWillSwitch", ActivityState.MessageCenter);
			startActivity(intent1);
		} else if (members.size() == 0) {
			toastToMessage("没有联系人");
		}
		finish();
		IMContactChooserData.getInstance().clear();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_title_TV_left:
			backToMessageCenter();
			break;
		// 点击消息发送按钮
		case R.id.im_message__send:
			if (IMContactChooserData.getInstance().getMemberCount() == 0) {
				toastToMessage("没有联系人");
				break;
			}
			String message = inputBar.getText();
			if (message == null || message.equals("")) {
				toastToMessage("消息不能为空");
				break;
			}
			sendMessage(message, false);
			inputBar.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToMessageCenter();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void backToMessageCenter(){
		Intent intent = AppUtil.getMainActIntent(this);
		intent.putExtra("ActivityWillSwitch", ActivityState.MessageCenter);

		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();
		IMContactChooserData.getInstance().clear();
	}
}
package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import com.lb.common.util.SmsUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.core.im.network.NetworkStatusCheck;
import com.lz.oncon.app.im.data.ImCore;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public class ReSendOnClickListener implements OnClickListener {
	
	private ArrayList<SIXmppMessage> mDatas;
	NetworkStatusCheck mNetworkStatusCheck;
	Context mContext;
	String mOnconId;
	SIXmppThreadInfo.Type threadType;
	
	public ReSendOnClickListener(Context context, ArrayList<SIXmppMessage> datas
			, String onconId, SIXmppThreadInfo.Type type){
		mContext = context;
		this.mDatas = datas;
		this.mOnconId = onconId;
		this.threadType = type;
		mNetworkStatusCheck = new NetworkStatusCheck(mContext);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag(R.id.tag_position);
		final SIXmppMessage d = mDatas.get(position);
		Builder mStatusBuilder = new Builder(mContext);
		mStatusBuilder.setTitle(R.string.app_name);
		boolean isNetOn = mNetworkStatusCheck.checkMobileNetStatus() || mNetworkStatusCheck.checkWifiNetStatus();
		if (isNetOn) {
			mStatusBuilder.setMessage(R.string.msg_resend_content);
			mStatusBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
			if (d.getContentType() == ContentType.TYPE_TEXT && SIXmppThreadInfo.Type.P2P == threadType) {
				mStatusBuilder.setNeutralButton(R.string.dialog_to_sendmessage, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SmsUtil.sendSMS(mContext, d.getTo(), d.getTextContent());
					}
				});
			}
			mStatusBuilder.setPositiveButton(R.string.msg_sendstatus_resend, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					new Thread(new Runnable() {
						@Override
						public void run() {// 发送，weixk
							if(SIXmppThreadInfo.Type.GROUP == threadType){
							}else if(SIXmppThreadInfo.Type.BATCH == threadType){
//								ImCore.getInstance().getConnection().sendBatchMsg(d);
							}else{
								ImCore.getInstance().getChatManager().createChat(mOnconId).sendMessage(d,SIXmppThreadInfo.Type.P2P);
							}
						}
					}).start();
				}
			});
		} else {
			mStatusBuilder.setMessage(R.string.im_warning_network_check2);
			mStatusBuilder.setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
		}
		mStatusBuilder.show();
	}
}
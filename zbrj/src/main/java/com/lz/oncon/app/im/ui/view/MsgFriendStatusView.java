package com.lz.oncon.app.im.ui.view;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.api.core.im.core.OnconIMMessage;
import com.lz.oncon.app.im.ui.IMMessageFormat;

public class MsgFriendStatusView extends TextView {
	
	PersonController mController;
	public MsgFriendStatusView(Context context) {
		super(context);
		setGravity(Gravity.CENTER_VERTICAL);
		setTextColor(Color.BLACK);
		setTextSize(15);
		setMaxWidth(BaseActivity.screenWidth - IMMessageFormat.msg_otherwidth - IMMessageFormat.msgbg_paddingshort - IMMessageFormat.msgbg_paddinglong);
		mController = new PersonController();
	}

	public void setMessage(SIXmppMessage d){
		String msgStr = d.getTextContent() == null ? "" : d.getTextContent();
		if(msgStr.startsWith("@custom_protocol@:friend_status")){
			HashMap<String, String> params = OnconIMMessage.parseCustomProtocol(msgStr);
			if(d.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
				String from = new PersonController().findNameByMobile(d.getFrom());
				if("1".equals(params.get("type"))){
					msgStr = getContext().getString(R.string.friend_status_msg_fmt, from);
					setText(msgStr);
				}
				/*else if("2".equals(params.get("type")) || "3".equals(params.get("type")))
					msgStr = getContext().getString(R.string.friend_status_msg_fmt2, from);*/
			}
		}
		//setText(msgStr);
	}
}

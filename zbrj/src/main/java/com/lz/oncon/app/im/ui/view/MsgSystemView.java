package com.lz.oncon.app.im.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.util.IMUtil;

public class MsgSystemView extends LinearLayout {

	public TextView message_system;
	public MsgSystemView(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.message_system, this);
		message_system = (TextView)findViewById(R.id.message_system);
	}

	public void setMessage(SIXmppMessage d){
		String msgStr = d.getTextContent() == null ? "" : d.getTextContent();
		if(msgStr.startsWith("m1_chatroom_msg@@@lz-oncon@@@v1.0")){
//			msgStr = IMUtil.parseChatroomMngMsg(d, mContactController);
		}else if(msgStr.startsWith("m1_extend_msg@@@lz-oncon@@@v1.0")){
			msgStr = IMUtil.parseExtendMsg(d);
		}
		message_system.setText(msgStr);
	}
}

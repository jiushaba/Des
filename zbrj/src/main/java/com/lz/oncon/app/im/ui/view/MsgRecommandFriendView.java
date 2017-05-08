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

public class MsgRecommandFriendView extends TextView {
	
	PersonController mController;
	public MsgRecommandFriendView(Context context) {
		super(context);
		setGravity(Gravity.CENTER_VERTICAL);
		setTextColor(Color.BLACK);
		setTextSize(15);
		setMaxWidth(BaseActivity.screenWidth - IMMessageFormat.msg_otherwidth - IMMessageFormat.msgbg_paddingshort - IMMessageFormat.msgbg_paddinglong);
		mController = new PersonController();
	}

	public void setMessage(SIXmppMessage d){
		String msgStr = d.getTextContent() == null ? "" : d.getTextContent();
		if(msgStr.startsWith("@custom_protocol@:recommand_friend")){
			HashMap<String, String> params = OnconIMMessage.parseCustomProtocol(msgStr);
			String mobile = params.get("recommandAccount");
			if(d.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
				msgStr = getContext().getString(R.string.recommand_friend_msg_fmt, mController.findNameByMobile(d.getFrom()), mController.findNameByMobile(mobile));
			}else{
				msgStr = getContext().getString(R.string.recommand_friend_msg_fmt2, mController.findNameByMobile(d.getTo()), mController.findNameByMobile(mobile));
			}
		}
		setText(msgStr);
	}
}

package com.lz.oncon.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.ui.IMMessageFormat;

public class LocationView extends LinearLayout{

	private String message_location_attr;
	public TextView message_textview_attr ;
	
	public String getMessage_location_attr() {
		return message_location_attr;
	}

	public void setMessage_location_attr(String message_location_attr) {
		this.message_location_attr = message_location_attr;
	}

	public LocationView(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.message_location, this);
		message_textview_attr = (TextView)findViewById(R.id.message_location_attr);
	}
	
	public void setMessage(SIXmppMessage msg){
		setGravity(Gravity.CENTER_VERTICAL);
		message_textview_attr.setTextColor(Color.WHITE);
		String[] result = IMMessageFormat.getLocString(msg.getTextContent());
		if (result != null && result[0] != null) {
			if (msg.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE) {
				message_textview_attr.setText(result[0]);
			} else {
				message_textview_attr.setText(result[0]);
			}
		} else{
			message_textview_attr.setText(R.string.im_loc_error);
		}
	}
}

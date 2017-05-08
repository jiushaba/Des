package com.lz.oncon.app.im.ui.view;

import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.SourceType;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class MsgDeviceView extends TextView {
	
	public boolean hasDeviceType = false;
	private Context mContext;
	private String fromName;

	public MsgDeviceView(Context context) {
		super(context);
		mContext = context;
	}
	
	public MsgDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public MsgDeviceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	public void setFromName(String fromName){
		this.fromName = fromName;
	}

	public void setMessage(SIXmppMessage d){
		if(d.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
			setGravity(Gravity.LEFT);
			setPadding(getResources().getDimensionPixelSize(R.dimen.height_10dp), 0, 0, 0);
		}else{
			setGravity(Gravity.RIGHT);
			setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.height_10dp), 0);
		}
		setTextColor(mContext.getResources().getColor(R.color.im_send));
		setTextSize(10);
		if (SIXmppMessage.Device.DEVICE_ANDROID.ordinal() == d.getDevice().ordinal()) {
			if(TextUtils.isEmpty(fromName)){
				setText(R.string.im_message_comefrom_android);
			}else{
				setText(fromName + ":Android");
			}
			hasDeviceType = true;
		} else if (SIXmppMessage.Device.DEVICE_IPHONE.ordinal() == d.getDevice().ordinal()) {
			if(TextUtils.isEmpty(fromName)){
				setText(R.string.im_message_comefrom_iphone);
			}else{
				setText(fromName + ":iPhone");
			}
			hasDeviceType = true;
		} else if (SIXmppMessage.Device.DEVICE_WINDOWS.ordinal() == d.getDevice().ordinal()) {
			if(TextUtils.isEmpty(fromName)){
				setText(R.string.im_message_comefrom_windows);
			}else{
				setText(fromName + ":Windows");
			}
			hasDeviceType = true;
		} else if (SIXmppMessage.Device.DEVICE_IPOD_TOUCH.ordinal() == d.getDevice().ordinal()) {
			if(TextUtils.isEmpty(fromName)){
				setText(R.string.im_message_comefrom_ipodtouch);
			}else{
				setText(fromName + ":iPod");
			}
			hasDeviceType = true;
		} else if (SIXmppMessage.Device.DEVICE_IPAD.ordinal() == d.getDevice().ordinal()) {
			if(TextUtils.isEmpty(fromName)){
				setText(R.string.im_message_comefrom_ipad);
			}else{
				setText(fromName + ":iPad");
			}
			hasDeviceType = true;
		} else if (SIXmppMessage.Device.DEVICE_MAC.ordinal() == d.getDevice().ordinal()) {
			if(TextUtils.isEmpty(fromName)){
				setText(R.string.im_message_comefrom_mac);
			}else{
				setText(fromName + ":Mac");
			}
			hasDeviceType = true;
		}else{
			if(!TextUtils.isEmpty(fromName)){
				setText(fromName);
			}
		}
	}
}

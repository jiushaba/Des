package com.lz.oncon.controller;

import java.util.ArrayList;

import com.xuanbo.xuan.R;
import com.lz.oncon.widget.InfoProgressDialog;
import com.lz.oncon.widget.InfoToast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.widget.Toast;

public class InviteController extends BaseController {
	
	public InviteListener inviteListener;
	
	public InfoProgressDialog progressDialog;
	
	public InviteController(Context mContext){
		super(mContext);
		progressDialog = new InfoProgressDialog(mContext);
	}

	@Override
	public void initDatabase() {
	}

	@Override
	public void onDestroy() {
	}
	
	public void invite(ArrayList<String> mobiles){
		sendSMS(mobiles, mContext.getString(R.string.group_invite_sms_content));
	}
	
	public void sendSMS(final ArrayList<String> mobiles, final String msg){
		if(progressDialog != null && !progressDialog.isShowing()){
			progressDialog.setMessage(mContext.getString(R.string.send));
			progressDialog.show();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				SmsManager smsManager = SmsManager.getDefault();
				try{
					if (mobiles != null && mobiles.size() > 0) {
						for(String mobile:mobiles){
							smsManager.sendTextMessage(mobile, null, msg, null, null);
						}
					}
					handler.sendEmptyMessage(SEND_SUCCESS);
				}catch(SecurityException e){
					if(e.getMessage().indexOf("android.permission.SEND_SMS") >= 0){
						handler.sendEmptyMessage(NO_SEND_SMS_RIGHT);
					}
				}catch(Exception e){
					handler.sendEmptyMessage(SEND_FAIL);
				}
			}
		}).start();
	}
	
	private static final int SEND_SUCCESS = 1;
	private static final int SEND_FAIL = 3;
	private static final int NO_SEND_SMS_RIGHT = 4;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SEND_SUCCESS:
				InfoToast.makeText(mContext , mContext.getString(R.string.send_sms_success), Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if(inviteListener != null)inviteListener.afterInvite(true);
				break;
			case SEND_FAIL:
				InfoToast.makeText(mContext , mContext.getString(R.string.send_sms_fail), Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if(inviteListener != null)inviteListener.afterInvite(false);
				break;
			case NO_SEND_SMS_RIGHT:
				InfoToast.makeText(mContext , mContext.getString(R.string.no_right_sendsms), Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if(inviteListener != null)inviteListener.afterInvite(false);
				break;
			}
		}
	};
	
	public interface InviteListener{
		public void afterInvite(boolean result);
	}
}
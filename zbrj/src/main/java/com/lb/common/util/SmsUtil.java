package com.lb.common.util;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsUtil {

	public static boolean sendSMS(Context context, String mobile, String content){
		try{
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("smsto:" + mobile));
			intent.putExtra("sms_body", StringUtils.repNull(content));
			context.startActivity(intent);
			return true;
		}catch(Exception e){
			try{
				Toast.makeText(context, R.string.no_right_sendsms, Toast.LENGTH_SHORT).show();
			}catch(Exception e1){}
			return false;
		}
	}
	
	public static boolean sendSMSBG(Context context, String mobile, String content){
		try{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(mobile, null, content, null, null);
			return true;
		}catch(Exception e){
			try{
				Toast.makeText(context, R.string.no_right_sendsms, Toast.LENGTH_SHORT).show();
			}catch(Exception e1){}
			return false;
		}
	}
}

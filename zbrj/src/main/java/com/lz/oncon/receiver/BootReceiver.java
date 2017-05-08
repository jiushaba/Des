package com.lz.oncon.receiver;

import com.lz.oncon.application.AppUtil;
import com.lz.oncon.data.AccountData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class BootReceiver extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
		if(!TextUtils.isEmpty(AccountData.getInstance().getLastBindphonenumber())){
			AccountData.getInstance().setBindphonenumber(AccountData.getInstance().getLastBindphonenumber());
			AccountData.getInstance().setPassword(AccountData.getInstance().getLastPassword());
			AccountData.getInstance().setUsername(AccountData.getInstance().getLastUsername());
			AppUtil.loginIM();
		}
    }
}
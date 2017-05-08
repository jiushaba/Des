package com.lz.oncon.widget;

import com.xuanbo.xuan.R;
import com.lz.oncon.app.im.util.WeiXinShareUtil;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class Share2WeixinDialog extends Builder {

	String[] share_wx;
	Context mContext;
	
	public Share2WeixinDialog(Context context) {
		super(context);
		mContext = context;
		share_wx = context.getResources().getStringArray(R.array.wx_share_menu);
	}
	
	public void setShareContent(final String text, final String picPath, int type){
		this.setItems(share_wx,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				WeiXinShareUtil.wx_share_text(mContext, which, text + picPath);
			}
		});
	}
}

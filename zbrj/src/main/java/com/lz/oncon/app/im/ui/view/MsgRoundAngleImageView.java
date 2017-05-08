package com.lz.oncon.app.im.ui.view;

import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.widget.HeadImageView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class MsgRoundAngleImageView extends HeadImageView implements OnClickListener {

	private Context mContext;
	
	public MsgRoundAngleImageView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle);
        this.mContext = context;
        this.setOnClickListener(this);
    } 
 
    public MsgRoundAngleImageView(Context context, AttributeSet attrs) { 
        super(context, attrs);
        this.mContext = context;
        this.setOnClickListener(this);
    } 
 
    public MsgRoundAngleImageView(Context context) { 
        super(context);
        this.mContext = context;
        this.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if(TextUtils.isEmpty(getMobile())){
			return;
		}
		PersonController.go2Detail(mContext, IMUtil.getInfosJid(getMobile()));
	} 
}
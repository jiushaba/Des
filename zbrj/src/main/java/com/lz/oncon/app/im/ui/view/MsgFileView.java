package com.lz.oncon.app.im.ui.view;

import java.text.DecimalFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.common.util.FileCore;
import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.util.IMUtil;

public class MsgFileView extends LinearLayout{

	public ImageView message_file_image;
	public TextView message_file_name;
	public TextView message_file_size;
	public ImageView message_file_image2;
	public MsgFileView(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.message_file, this);
		message_file_image = (ImageView)findViewById(R.id.message_file_image);
		message_file_name = (TextView)findViewById(R.id.message_file_name);
		message_file_size = (TextView)findViewById(R.id.message_file_size);
		message_file_image2 = (ImageView)findViewById(R.id.message_file_image2);
	}

	public void setMessage(SIXmppMessage d){
		try{
			float filesize = d.getImageFileSize();
			DecimalFormat df = new DecimalFormat("0.##");
			String sFilesize = filesize > 1024*1024 ? df.format(filesize/1024/1024) + "MB" :
				filesize > 1024 ? df.format(filesize/1024) + "KB" : filesize + "B";
			String sf = FileCore.getSuffix(d.getImageName()).toLowerCase();
			int resId = IMUtil.getImageId(sf);
			if(d.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE){
				message_file_image2.setVisibility(View.VISIBLE);
				message_file_image2.setImageResource(resId);
				message_file_image.setVisibility(View.GONE);
			}else if(d.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE){
				message_file_image2.setVisibility(View.GONE);
				message_file_image.setVisibility(View.VISIBLE);
				message_file_image.setImageResource(resId);
			}else{
				message_file_image.setVisibility(View.GONE);
				message_file_image2.setVisibility(View.GONE);
			}
			message_file_name.setText(d.getImageName());
			message_file_size.setText(sFilesize);
		}catch(Exception e){
		}
	}
}

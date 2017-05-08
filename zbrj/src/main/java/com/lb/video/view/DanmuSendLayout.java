package com.lb.video.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lb.common.util.Log;
import com.lb.video.data.CommResData;
import com.lb.video.data.DanmuSendData;
import com.lb.video.data.ShareData;
import com.xuanbo.xuan.R;
import com.lb.zbrj.net.NetIF_ZBRJ;

public class DanmuSendLayout extends RelativeLayout implements OnClickListener{
	public ShareData shareData;
	private Button sendButton;
	private NetIF_ZBRJ netIF_ZBRJ;
	private EditText editText;
	public DanmuSendLayout(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.video_danmu_control, this);
		init(context);
	}
	public DanmuSendLayout(Context context, AttributeSet attr) {
		super(context, attr);
		LayoutInflater.from(context).inflate(R.layout.video_danmu_control, this);
		init(context);
	}
	public DanmuSendLayout(Context context, AttributeSet attr ,int defstyle) {
		super(context, attr ,defstyle);
		LayoutInflater.from(context).inflate(R.layout.video_danmu_control, this);
		init(context);
	}
	private void init(Context context){
		sendButton = (Button)findViewById(R.id.send_danmu);
		editText = (EditText)findViewById(R.id.msg_input);
		sendButton.setOnClickListener(this);
		netIF_ZBRJ = new NetIF_ZBRJ(context);
	}
	@Override
	public void onClick(View view) {
		String msg = editText.getText().toString();
		SendDanmukuData(msg);
		editText.setText("");
	}

	private void SendDanmukuData(String msg) {
		
		DanmuSendData data = new DanmuSendData();
		data.videoID = shareData.videoId;
		data.content.id = netIF_ZBRJ.getRandomNumber()+"";
		data.content.account = shareData.account;
		data.content.nick = shareData.nick;
		data.content.fontsize = "1";
		
		data.content.time = convertTimeToString();
		data.content.msg = msg;
		data.id = netIF_ZBRJ.getRandomNumber()+"";
		CommResData result  = netIF_ZBRJ.danmu_send(data);
		Log.i("danmu_send", result.status+"------"+result.desc);
	}
	private String convertTimeToString(){
		if(shareData.recordingTime<=0)
			return "00:00";
		else{
			long min = (shareData.recordingTime/60000);
			long sec = (shareData.recordingTime%60000)/1000;
			StringBuilder sb = new StringBuilder();
			if(min<10){
				sb.append(0);
			}
			sb.append(min).append(":");
			if(sec<10){
				sb.append(0);
			}
			sb.append(sec);
			return sb.toString();
		}
		
	}
}

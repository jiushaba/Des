package com.lb.video.job;

import java.util.Map;

import org.videolan.libvlc.EventHandler;

import android.R.integer;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
/**
 * 查询视频弹幕和显示的信息
 * @author zhanglijun
 *
 */
public class VideoNumberQueryRunnable implements IRunnable {
	private NetIF_ZBRJ netIF_ZBRJ ;
	private boolean runing = true;
	public long sleepTime = 10000;
	private boolean runonce = false;
	private String videoID;
	private Handler eventHandler;
	private int msgWhat = 0;
	private Bundle oldData;
	public VideoNumberQueryRunnable(Context context,boolean runonce,String videoID ,Handler eventHandler ,int msgWhat) {
		this.netIF_ZBRJ = new NetIF_ZBRJ(context);
		this.runonce = runonce;
		this.videoID = videoID;
		this.eventHandler = eventHandler;
		this.msgWhat = msgWhat;
	}

	@Override
	public void run() {
		while(runing){
			try{
				NetInterfaceStatusDataStruct result = netIF_ZBRJ.m1_query_viewer(videoID);
				if(eventHandler != null){
					Message msg = new Message();
					Bundle data = (Bundle) result.getObj();
					msg.setData(data);
					msg.what = msgWhat;
					eventHandler.sendMessage(msg);
				}
				Thread.sleep(sleepTime);
			}catch(Exception e){
				Log.e(Constants.LOG_TAG, e);
			}
			if(runonce)
				stop();
		}
	}
	
	@Override
	public void stop() {
		runing = false;

	}
	
}

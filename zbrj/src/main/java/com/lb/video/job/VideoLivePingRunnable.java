package com.lb.video.job;

import android.os.Handler;

import com.danmu.comm.Log;
import com.lb.common.util.Constants;
import com.lb.video.activity.RecordActivity;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.net.NetworkStatusCheck;

public class VideoLivePingRunnable implements IRunnable {
	private NetIF_ZBRJ netif;
	private boolean running=true;
	public long sleepTime = 5000;
	private String videoID;
	private RecordActivity activity;
	private NetworkStatusCheck nsc;
	private Handler handler;
	private int netErrors = 0;
	private int pingErrors = 0;
	public VideoLivePingRunnable(RecordActivity activity ,NetIF_ZBRJ netif ,String videoID ,Handler handler) {
		super();
		this.netif = netif;
		this.videoID = videoID;
		this.activity = activity;
		this.handler = handler;
		nsc = new NetworkStatusCheck(activity);
	}

	@Override
	public void run() {
		while(running){
			try {
				NetInterfaceStatusDataStruct result = netif.m1_send_ping(videoID);
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					pingErrors=0;
					netErrors = 0;
				}else{
					if(nsc.checkNetWorkAvliable() == false){
						netErrors++;
					}
					pingErrors++;
				}
				if(netErrors>1){
					handler.sendEmptyMessage(activity.EventLiveNetError);
					stop();
				}else if(pingErrors>3){
					handler.sendEmptyMessage(activity.EventLivePingError);
					stop();
				}
				Thread.sleep(sleepTime);
				//Log.i(tag, msg)("ping", "ping job start"+pingErrors+" netErrors:"+netErrors);
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
	}

	@Override
	public void stop() {
		running = false;
	}

}

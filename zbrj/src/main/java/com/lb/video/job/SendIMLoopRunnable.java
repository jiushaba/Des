package com.lb.video.job;

import java.util.ArrayList;
import java.util.List;

import com.lb.common.util.DateUtil;
import com.lb.common.util.Log;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.api.CustomProtocolDealerManager;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.data.AccountData;
/*
 * 向多人发送im 消息通知
 */
public class SendIMLoopRunnable implements Runnable {
	public static final int invite_video = 1;
	public static final int start_recorder = 2;
	public static final int start_watch_live = 3;
	public static final int start_watch_playback = 4;
	public static final int donothing = 5;
	private int doing = 0;
	public List<String> accountList = new ArrayList<String>();
	public VideoData videoData = null;
	public SendIMLoopRunnable(int doing ,VideoData videoData , List<String> accountList){
		this.doing = doing;
		this.videoData = videoData;
		this.accountList = accountList;
	}
	@Override
	public void run() {
		try{
			if(accountList == null || accountList.size() == 0)
				return;
			CustomProtocolDealerManager cdm = ImCore.getInstance().getCustomProtocolDealerManager();
			String myaccount = AccountData.getInstance().getBindphonenumber();
			for(String account :accountList){
				//屏蔽自己，防止自己给自己发消息
				if(account != null && account.equals(myaccount)){
					continue;
				}
				if(invite_video== doing){
					cdm.createDealer(account).invite_video(videoData.nick ,videoData.videoID, videoData.title, videoData.playUrl);
				}else if(donothing == doing){
					cdm.createDealer(account).friend_status(0, videoData);
				}else if(start_recorder == doing){
					cdm.createDealer(account).friend_status(1,videoData);
				}else if(start_watch_live == doing){
					cdm.createDealer(account).friend_status(2, videoData);
				}else if(start_watch_playback == doing){
					cdm.createDealer(account).friend_status(3, videoData);
				}
			}
		}catch(Exception e){
			Log.e("SendIMUserStateRunnable", e);
		}finally{
			accountList = null;
			videoData = null;
		}
	}
}

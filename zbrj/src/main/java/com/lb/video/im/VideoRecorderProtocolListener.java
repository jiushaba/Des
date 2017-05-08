package com.lb.video.im;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.danmu.comm.Log;
import com.lb.video.activity.RecordActivity;
import com.lb.video.widget.RecorderSurfaceView;
import com.lz.oncon.api.CustomProtocolListener;

public class VideoRecorderProtocolListener implements CustomProtocolListener {
	private RecordActivity recorder;
	private Handler handler;
	public VideoRecorderProtocolListener(RecordActivity recorder,Handler VideoHandler) {
		this.recorder = recorder;
		this.handler = VideoHandler;
	}

	@Override
	public void request_join_live(String account, String nick, String videoID) {
		try {
			if(recorder.isFinishing())
				return;
			Bundle bundle = new Bundle();
			bundle.putString("account", account);
			bundle.putString("nick", nick);
			bundle.putString("videoID", videoID);
			Message msg = new Message();
			msg.what = recorder.EventRequestJoinLive;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	@Override
	public void response_join_live(String account, String nick, String videoID,
			String videoTitle, String accept) {
		
	}
	/**
	 * 服务器直接私聊
	 * FIXME 这个功能需要实现
	 */
	@Override
	public void private_bullet(String account, String msg, String videoID) {
		
	}

	@Override
	public void kick_off_video(String account, String nick, String videoID,
			String videoTitle) {
		
	}

	@Override
	public void mute_video(String account, String nick, String videoID,
			String videoTitle) {
		
	}

	@Override
	public void forbid_bullet(String videoID ,String type) {
		
	}

	@Override
	public void friend_status(String account, String type, String videoID) {
	
	}

	@Override
	public void invite_video(String account, String nick, String videoID,
			String videoTitle, String playurl) {
		
	}

	@Override
	public void entrust_invite_video(String videoID) {
		
	}

	@Override
	public void comment_notify(String commenVideoID, String commentid,
			String account, String nick, String imageurl) {
		
	}

	@Override
	public void focus_notify(int optType, int isSpecial, String account,
			String nick, String imageurl) {
		// TODO Auto-generated method stub
		
	}



}

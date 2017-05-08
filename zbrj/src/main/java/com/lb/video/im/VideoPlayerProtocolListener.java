package com.lb.video.im;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lz.oncon.api.CustomProtocolListener;

public class VideoPlayerProtocolListener implements ProtocalMessageWhat,CustomProtocolListener {
	public static final int Event_Response_join_live = 10001;
	public static final int Event_kick_off_video = 10002;
	private Handler handler;
	private String nowVideoId;
	public VideoPlayerProtocolListener(Handler videoHandler,String nowVideoId) {
		this.handler = videoHandler;
		this.nowVideoId = nowVideoId;
	}

	@Override
	public void request_join_live(String account, String nick, String videoID) {
		
	}
	/**
	 * 请求加入直播回应
	 */
	@Override
	public void response_join_live(String account, String nick, String videoID,
			String videoTitle, String accept) {
		if("1".equals(accept)){
			handler.sendEmptyMessage(acceptPlay);
		}else{
			Message message = new Message();
			Bundle data = new Bundle();
			data.putString("message", nick+"拒绝了 您加入"+videoTitle+"的请求");
			message.setData(data);
			message.what = refusePlay;
			handler.sendMessage(message);
		}
	}
	/**
	 * 服务器直接私聊
	 */
	@Override
	public void private_bullet(String account, String msg, String videoID) {
		if(notEqualVideoId(videoID)){
			return;
		}
		Message message = new Message();
		Bundle data = createBundle(account, "", videoID);
		data.putString("msg", msg);
		message.setData(data);
		message.what = private_bullet;
		handler.sendMessage(message);
	}
	/**
	 * 踢出禁言
	 * 
	 */
	@Override
	public void kick_off_video(String account, String nick, String videoID,
			String videoTitle) {
		if(notEqualVideoId(videoID)){
			return;
		}
		Message message = new Message();
		Bundle data = new Bundle();
		data.putString("message", nick+"将您踢出了"+videoTitle);
		message.setData(data);
		message.what = kick_off_video;
		handler.sendMessage(message);
	}
	/**
	 * 禁言
	 */
	@Override
	public void mute_video(String account, String nick, String videoID,
			String videoTitle) {
		if(notEqualVideoId(videoID)){
			return;
		}
		Message message = new Message();
		Bundle data = createBundle(account, nick, videoID,videoTitle);
		message.setData(data);
		message.what = mute_video;
		handler.sendMessage(message);
	}
	/**
	 * 禁止弹幕
	 */
	@Override
	public void forbid_bullet(String videoID, String type) {
		if(notEqualVideoId(videoID)){
			return;
		}
		Bundle data = new Bundle();
		data.putString("type", type);
		Message message = new Message();
		message.what = forbid_bullet;
		message.setData(data);
		handler.sendMessage(message);
	}

	@Override
	public void friend_status(String account, String type, String videoID) {
	
	}
	/**
	 * 邀请观看视频
	 */
	@Override
	public void invite_video(String account, String nick, String videoID,
			String videoTitle, String playurl) {
		/*if(notEqualVideoId(videoID)){
			return;
		}
		Message message = new Message();
		Bundle data = createBundle(account, nick, videoID,videoTitle);
		data.putString("playurl", playurl);
		message.setData(data);
		message.what = invite_video;
		handler.sendMessage(message);*/
	}
	/**
	 * 委托邀请
	 */
	@Override
	public void entrust_invite_video(String videoID) {
		if(notEqualVideoId(videoID)){
			return;
		}
		handler.sendEmptyMessage(entrust_invite_video);
	}

	@Override
	public void comment_notify(String commenVideoID, String commentid,
			String account, String nick, String imageurl) {
		
	}
	
	
	private Bundle createBundle(String account ,String nick,String videoID){
		Bundle data = new Bundle();
		data.putString("account", account);
		data.putString("nick", account);
		data.putString("videoID", account);
		return data;
	}
	private Bundle createBundle(String account ,String nick,String videoID ,String videoTitle){
		Bundle data = createBundle(account, nick, videoID);
		data.putString("videoTitle", videoTitle);
		return data;
	}
	private boolean notEqualVideoId(String videoID){
		if(nowVideoId != null && nowVideoId.equals(videoID)){
			return false;
		}
		return true;
	}

	@Override
	public void focus_notify(int optType, int isSpecial, String account,
			String nick, String imageurl) {
		// TODO Auto-generated method stub
		
	}
}

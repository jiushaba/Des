package com.lz.oncon.api;

public interface CustomProtocolListener {

	public void request_join_live(String account, String nick, String videoID);
	public void response_join_live(String account, String nick, String videoID, String videoTitle, String accept);
	public void private_bullet(String account, String msg, String videoID);
	public void kick_off_video(String account, String nick, String videoID, String videoTitle);
	public void mute_video(String account, String nick, String videoID, String videoTitle);
	public void forbid_bullet(String videoID,String type);
	public void friend_status(String account, String type, String videoID);
	public void invite_video(String account, String nick, String videoID, String videoTitle, String playurl);
	public void entrust_invite_video(String videoID);
	public void comment_notify(String commenVideoID, String commentid, String account, String nick, String imageurl);
	public void focus_notify(int optType, int isSpecial, String account, String nick, String imageurl);
}
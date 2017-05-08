package com.lz.oncon.api;

import com.lb.common.util.StringUtils;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.CommentData;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.api.core.im.core.OnconIMCore;
import com.lz.oncon.api.core.im.data.Constants;
import com.lz.oncon.data.AccountData;

public class CustomProtocolDealer {
	private PersonController personController = new PersonController();
	private String username = null;
	private String countryCode = null;
	
	protected CustomProtocolDealer(String username) {
		if(username.startsWith(Constants.COUNTRY_CODE_CHINA)|| username.startsWith("0086")){
			countryCode = Constants.COUNTRY_CODE_CHINA;
			this.username = username.substring(Constants.COUNTRY_CODE_CHINA.length(),username.length());
		}else{
			this.username = username;
		}
		
	}
	
	
	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	/**
	 * 获取对话对方的账号名
	 * @return
	 */
	public String getUsername(){
		return username;
	}
	/**
	 * 1.16	 推荐给好友接口
	 * @return
	 */
	public SIXmppMessage recommand_friend(String recommandAccount){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:recommand_friend?recommandAccount=");
		sb.append(recommandAccount);
		return OnconIMCore.getInstance().sendCustomProtocolMsg(username, sb.toString());
	}
	//FIXME 目前自定义协议大部分使用的是    OnconIMCore的sendCustomProtocolMsg进行发送的,根据实际情况建议后期都改为sendCustomProtocolMsgNoDB方法发送，区别在于是否入库,如果确实需要入库,需要修改对应的消息中心的展示信息
	/**
	 * 1.33 （非服务器）请求加入直播间（仅当直播间为授权模式可用）
	 * @return
	 */
	public SIXmppMessage request_join_live(String videoID){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:request_join_live?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		String nick = getNickName(AccountData.getInstance().getBindphonenumber());
		sb.append(nick);
		sb.append("&videoID=");
		sb.append(videoID);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.33 （非服务器）响应加入直播间（仅当直播间为授权模式可用）
	 * @return
	 */
	public SIXmppMessage response_join_live(String videoID ,String videoTitle,int accept){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:response_join_live?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		String nick = getNickName(AccountData.getInstance().getBindphonenumber());
		sb.append(nick);
		sb.append("&videoID=");
		sb.append(videoID);
		sb.append("&videoTitle=");
		sb.append(videoTitle);
		sb.append("&accept=");
		sb.append(accept);
		
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.34 （非服务器）直播间内发送私聊信息(直播间@消息）
	 * @return
	 */
	public SIXmppMessage private_bullet(String msg, String videoID){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:private_bullet?account=");
		sb.append(username);
		sb.append("&msg=");
		sb.append(msg);
		sb.append("&videoID=");
		sb.append(videoID);
		return OnconIMCore.getInstance().sendCustomProtocolMsg(username, sb.toString());
	}
	
	/**
	 * 1.35 （非服务器）直播间踢出
	 * @return
	 */
	public SIXmppMessage kick_off_video(String videoID, String videoTitle){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:kick_off_video?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		String nick = getNickName(AccountData.getInstance().getBindphonenumber());
		sb.append(nick);
		sb.append("&videoID=");
		sb.append(videoID);
		sb.append("&videoTitle=");
		sb.append(videoTitle);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.36  (非服务器)直播间禁言
	 * @return
	 */
	public SIXmppMessage mute_video(String videoID, String videoTitle){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:mute_video?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		String nick = getNickName(AccountData.getInstance().getBindphonenumber());
		sb.append(nick);
		sb.append("&videoID=");
		sb.append(videoID);
		sb.append("&videoTitle=");
		sb.append(videoTitle);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.38 服务器禁止弹幕推送（im消息）
	 * @return
	 */
	public SIXmppMessage forbid_bullet(String videoID){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:forbid_bullet?");
		sb.append("&videoID=");
		sb.append(videoID);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.39 推送好友当前操作状态
	 * Type 操作类型 0表示结束操作，1表示正在直播，2表示看直播，3表示看点播
	 * @return
	 */
	public SIXmppMessage friend_status(int type,VideoData videoData){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:friend_status?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		String nick ="";
		if(StringUtils.isNull(nick)){
			nick = getNickName(AccountData.getInstance().getBindphonenumber());
		}
		sb.append(nick);
		sb.append("&type=");
		sb.append(type);
		if(type == 0){
			sb.append("&videoID=0");
		}else{
			sb.append("&videoID=");
			sb.append(videoData.videoID);
			sb.append("&playUrl=");
			sb.append(videoData.playUrl);
			sb.append("&title=");
			sb.append(videoData.title);
			if(videoData.bulletFile != null){
				sb.append("&bulletFile=");
				sb.append(videoData.bulletFile);
			}
			sb.append("&dateTime=");
			sb.append(videoData.dateTime);	
			sb.append("&locationX=");
			sb.append(videoData.locationX);
			sb.append("&locationY=");
			sb.append(videoData.locationY);
		}
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.41 （非服务器）邀请观看视频信息
	 * @return
	 */
	public SIXmppMessage invite_video(String videoID, String videoTitle, String playurl){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:invite_video?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		String nick = getNickName(AccountData.getInstance().getBindphonenumber());
		sb.append(nick);
		sb.append("&videoID=");
		sb.append(videoID);
		sb.append("&videoTitle=");
		sb.append(videoTitle);
		sb.append("&playurl=");
		sb.append(playurl);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	public SIXmppMessage invite_video(String nick,String videoID, String videoTitle, String playurl){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:invite_video?account=");
		sb.append(AccountData.getInstance().getBindphonenumber());
		sb.append("&nick=");
		sb.append(nick);
		sb.append("&videoID=");
		sb.append(videoID);
		sb.append("&videoTitle=");
		sb.append(videoTitle);
		sb.append("&playurl=");
		sb.append(playurl);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	/**
	 * 1.42 （非服务器）委托邀请
	 * @return
	 */
	public SIXmppMessage entrust_invite_video(String videoID){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:entrust_invite_video?");
		sb.append("videoID=");
		sb.append(videoID);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	
	/**
	 * 1.52评论通知消息接口
	 */
	public SIXmppMessage comment_notify(CommentData comment){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:comment_notify?");
		sb.append("commenVideoID=");
		sb.append(comment.comentVideoID);
		sb.append("&commentid=");
		sb.append(comment.commentID);
		sb.append("&account=");
		sb.append(comment.commentAccount);
		sb.append("&nick=");
		sb.append(comment.nick);
		sb.append("&imageurl=");
		sb.append(comment.imageurl);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
	public String getNickName(String account){
		String nick =  personController.findNameByMobile(account);
		if(StringUtils.isNull(nick))
			return account;
		else {
			return nick;
		}
	}
	
	/**
	 * 1.54 添加/删除关注通知接口
	 */
	public SIXmppMessage focus_notify(FansData fans, int optType){
		StringBuffer sb = new StringBuffer("@custom_protocol");
		sb.append("@:focus_notify?");
		sb.append("optType=");
		sb.append(optType);
		sb.append("&isSpecial=");
		sb.append(fans.isSpecial);
		sb.append("&account=");
		sb.append(fans.account);
		sb.append("&nick=");
		sb.append(fans.nick);
		sb.append("&imageurl=");
		sb.append(fans.imageurl);
		return OnconIMCore.getInstance().sendCustomProtocolMsgNoDB(username, sb.toString());
	}
}
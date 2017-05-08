package com.lz.oncon.api;

import com.lb.zbrj.data.CommentData;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.SIXmppThreadInfo.Type;
import com.lz.oncon.api.core.im.core.OnconIMCore;
import com.lz.oncon.api.core.im.data.Constants;

public class SIXmppChat {

	private String username = null;
	private String countryCode = null;
	
	protected SIXmppChat(String username) {
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
	 * 发送文本消息
	 * mtype mtype.P2P/BATCH
	 * @param content
	 */
	public SIXmppMessage sendTextMessage(String content,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendTextMessage(username, content, mtype, true);
	}
	
	/**
	 * 发送文本消息
	 * mtype mtype.P2P/BATCH
	 * @param content
	 */
	public SIXmppMessage sendTextMessageNoDB(String content,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendTextMessage(username, content, mtype, false);
	}
	
	/**
	 * 发送图片消息
	 * mtype mtype.P2P/BATCH
	 * @param imagePath
	 */
	public SIXmppMessage sendImageMessage(String imagePath,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendImageMessage(username, imagePath, mtype);
	}
	
	/**
	 * 发送录音消息
	 * mtype mtype.P2P/BATCH
	 * @param audioPath
	 */
	public SIXmppMessage sendAudioMessage(String audioPath, int audioTimeLength,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendAudioMessage(username, audioPath, audioTimeLength, mtype);
	}
	
	/**
	 * 通用的消息发送方法
	 * mtype mtype.P2P/BATCH
	 * @param msg
	 */
	public SIXmppMessage sendMessage(SIXmppMessage msg, SIXmppThreadInfo.Type mtype){		
		return OnconIMCore.getInstance().sendMessage(msg, mtype, true, false);
	}
	

	/**
	 * 发送位置消息
	 * mtype mtype.P2P/BATCH
	 * @param content
	 */
	public SIXmppMessage sendLocMessage(String coor, String longtitude, String latitude, String loc,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendLocMessage(username, coor, longtitude, latitude, loc, mtype);
	}
	
	/**
	 * 发送动态表情消息
	 * mtype mtype.P2P/BATCH
	 * @param content
	 */
	public SIXmppMessage sendDynExpMessage(String name,String desc,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendDynExpMessage(username, name, mtype,desc);
	}
	
	/**
	 * 发送会说话的图片消息
	 * mtype mtype.P2P/BATCH
	 */
	public SIXmppMessage sendTalkPicMessage(String imagePath, String audioPath,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendTalkPicMessage(username, imagePath, audioPath, mtype);
	}
	
	/**
	 * 发送闪图消息
	 * mtype mtype.P2P/BATCH
	 */
	public SIXmppMessage sendSnapPicMessage(String imagePath, int snapTime,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendSnapPicMessage(username, imagePath, snapTime, mtype);
	}
	
	/**
	 * 发送文件消息
	 * @param filePath
	 * mtype mtype.P2P/BATCH
	 * @return
	 */
	public SIXmppMessage sendFileMessage(String filePath,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendFileMessage(username, filePath, mtype);
	}
	
	/**
	 * 发送音乐消息
	 * mtype mtype.P2P/BATCH
	 */
	public SIXmppMessage sendMusicMessage(String songId, String songName, String singer, String songPath, String bigImgPath, String smallImgPath,SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendMusicMessage(username, songId, songName, singer, songPath, bigImgPath, smallImgPath, mtype);
	}
	
	/**
	 * 发送服务号名片消息
	 * mtype mtype.P2P/BATCH
	 */
	public SIXmppMessage sendPublicAccountNameCardMessage(String pubaccount_id, String pubaccount_name, SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendPublicAccountNameCardMessage(username, pubaccount_id, pubaccount_name, mtype);
	}
	
	/**
	 * 发送图文分享消息
	 * mtype mtype.P2P/BATCH
	 */
	public SIXmppMessage sendImageTextMessage(String title, String brief, String image_url, String detail_url, String pub_account, String author, SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendImageTextMessage(username, title, brief, image_url, detail_url, pub_account, author, mtype);
	}
	
	/**
	 * 链接消息
	 * mtype mtype.P2P/BATCH
	 */
	public SIXmppMessage sendLinkMessage(SIXmppMessage.LinkMsgType subtype, String title, String desc, String link, String img_url,
			String img_width, String img_height, String source, SIXmppThreadInfo.Type mtype){
		return OnconIMCore.getInstance().sendLinkMessage(username, subtype, title, desc, link, img_url, img_width, img_height, source, mtype);
	}
	
	/**
	 * 消息转发
	 */
	public SIXmppMessage forwardMessage(String textContent, String toOnconid, Type mtype, ContentType contentType){		
		return OnconIMCore.getInstance().forwardMessage(textContent, toOnconid, mtype, contentType);
	}
	
	public void sendReadMessage(String packetid){
		OnconIMCore.getInstance().sendReadMessage(username, packetid);
	}
}
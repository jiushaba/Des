package com.lz.oncon.api;

import java.util.ArrayList;

import android.content.Context;

import com.lz.oncon.api.SIXmppThreadInfo.Type;
import com.lz.oncon.api.core.im.data.IMDataDB;

public class SIXmppHistoryManager {	
	
	/**
	 * 通过用户名和密码，获得访问权限
	 * @param username
	 * @param password
	 */
	public SIXmppHistoryManager(Context context,String username,String password) {
		IMDataDB.getInstance().init(context, username);
	}
	
//	public void close(){
//		IMDataDB.getInstance().close();
//	}
	
	/**
	 * 按照时间逆序，获得存储消息的联系人
	 * @param start
	 * @param end
	 * @return
	 */
	public ArrayList<SIXmppThreadInfo> getUsernames(int start ,int end){
		//暂时不关心参数，返回全部的
		return IMDataDB.getInstance().queryAllThreads();
	}
	
	/**
	 * 获得账号个数
	 * @return
	 */
	public int getUserCount(){
		ArrayList<SIXmppThreadInfo> usernames = IMDataDB.getInstance().queryAllThreads();
		if(usernames!=null){
			return usernames.size();
		}else {
			return 0;
		}
	}
	
	/**
	 * 按时间逆序取聊天记录
	 * @param username
	 * @param startMessageId
	 * @param count
	 * @return
	 */
	public ArrayList<SIXmppMessage> getHistoryByUsername(String username,String startMessageId,int count){
		//暂时只关心count参数，不关心startmessage
		IMDataDB db = IMDataDB.getInstance();
		return db.queryAllMessageOfThread(username,count);
	}
	
	public ArrayList<SIXmppMessage> queryAllImageMsgOfThread(String username){
		//暂时只关心count参数，不关心startmessage
		IMDataDB db = IMDataDB.getInstance();
		return db.queryAllImageMsgOfThread(username);
	}
	
	//gaotaiwen begin
	public SIXmppMessage getLatestMsgById(String id){
		IMDataDB db = IMDataDB.getInstance();
		return db.getLatestMsgById(id);
	}
	
	public int getMsgCount(String onconid){
		IMDataDB db = IMDataDB.getInstance();
		return db.queryAllMegCount(onconid);
	}
	
	public ArrayList<SIXmppMessage> getMsgByCount(String id,int count){
		
		IMDataDB db = IMDataDB.getInstance();
		return db.queryMsgByCount(id, count);
	}
	
	public ArrayList<SIXmppMessage> getMsgByLimit(String id,int begin,int count, String order){
		
		IMDataDB db = IMDataDB.getInstance();
		return db.queryMsgByLimit(id,begin,count, order);
	}
	//end gaotiawen
	
	/**
	 * 获得某个账号历史消息的个数
	 * @param username
	 * @return
	 */
	public int getHistoryCountByUsername(String username){
		//暂时没有实现
		return -1;
	}
	
	/**
	 * 获取所有消息的总数量
	 * @return 总数量
	 */
	public int queryAllThreadsMessageCount() {
		IMDataDB db = IMDataDB.getInstance();
		return db.queryAllThreadsMessageCount();
	}
	
	/**
	 * 通过消息id，获得消息内容，待定，以后删掉吧
	 * @param id
	 * @return
	 */
	public SIXmppMessage getMessageById(String onconid,String packetid){
		IMDataDB db = IMDataDB.getInstance();
		return db.queryMessageOfThreadById(onconid,packetid);
	}
	
	public void deleteMessageById(String onconid,String packetid){
		IMDataDB.getInstance().deleteMessage(onconid, packetid);
	}
	
	public void deleteAllMessageByUsername(String onconid){
		IMDataDB.getInstance().deleteThread(onconid);
	}
	public void deleteAllMessage(){
		IMDataDB.getInstance().deleteAllThreads();
	}
	
	public void deleteAllThreadsExceptMsgs(){
		IMDataDB.getInstance().deleteAllThreadsExceptMsgs();
	}
	
	/**
	 * 根据传入时间,删除全部会话里所有早于这个时间的消息
	 * @param time
	 */
	public void deleteAllThreadsMessageByTime(String time) {
		IMDataDB.getInstance().deleteAllThreadsMessageByTime(time);
	}
	
	public void setNickname(String onconid,String nickname, Type threadType){
		SIXmppThreadInfo thread = IMDataDB.getInstance().queryThread(onconid);
		if(thread != null){
			thread.setNickname(nickname);
			IMDataDB.getInstance().updateThread(thread);
		}
	}
	
	public void addMessage(String onconid,String nickname,SIXmppMessage message, Type threadType){
		IMDataDB.getInstance().insertMessage(onconid, nickname, message, threadType);
	}
	
	public void updateMessageThumbnailPath(String onconid,String packetid, String thumbnailPath){
		IMDataDB.getInstance().updateMessageThumbnailPath(onconid, packetid, thumbnailPath);
	}
	
	public SIXmppP2PInfo p2p_query(String onconid){
		return IMDataDB.getInstance().p2p_query(onconid);
	}
	
	public boolean p2p_setAttributes(String onconid,String key,String value){
		return IMDataDB.getInstance().p2p_setAttributes(onconid, key, value);
	}
	public void updateAllThreadVideoStatus2Zero(){
		IMDataDB.getInstance().updateAllThreadVideoStatus2Zero();
	}
	
	public int qryNewMsgCount(String onconid){
		IMDataDB db = IMDataDB.getInstance();
		return db.qryNewMsgCount(onconid);
	}
}
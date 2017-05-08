package com.lz.oncon.api.core.im.data;

import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import com.lb.common.util.Log;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppP2PInfo;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.SIXmppThreadInfo.Type;


public class IMDataDB {
	// 文件数据保存地址
	public static String FILE_TEMP_DIC = Environment
			.getExternalStorageDirectory() + "/" + Constants.PACKAGENAME + "/oncon/temp/";

	private String mUsername;

	private IMDataDBHelper db;

	private IMDataDB() {
	}

	private static IMDataDB instance = null;

	public static IMDataDB getInstance() {
		if (instance == null) {
			instance = new IMDataDB();
		}
		return instance;
	}

	public void init(Context context, String username) {
		if(context == null || username == null){
			return;
		}
		if (db == null) {
			try {
				db = new IMDataDBHelper(context, username);
			} catch (Exception e) {}
		} else if (mUsername != null && !username.equals(mUsername)) {
			try {
				db.close();
				db = new IMDataDBHelper(context, username);
			} catch (Exception e) {}
		}
		this.mUsername = username;
	}

	public boolean isInit() {
		if(db == null){
			return false;
		}else{
			return true;
		}
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}		
	}

	synchronized public void insertMessage(String onconid, String nickname,
			SIXmppMessage message, Type threadType) {
		if (db != null) {
			db.insertMessage(onconid, nickname, message, threadType);
		}
	}

	synchronized public void updateThread(SIXmppThreadInfo thread) {
		if (db != null) {
			db.updateThread(thread);
		}
	}
	
	synchronized public void updateThreadIsStranger(String onconid, String isstranger) {
		if (db != null) {
			db.updateThreadIsStranger(onconid, isstranger);
		}
	}
	
	synchronized public void insertThread(SIXmppThreadInfo thread) {
		if (db != null) {
			db.insertThread(thread);
		}
	}

	synchronized public void updateMessage(String onconid, SIXmppMessage message) {
		if (db != null) {
			db.updateMessage(onconid, message);
		}
	}
	
	synchronized public void updateMsgId(String onconid, String oldMsgId, String newMsgId) {
		if (db != null) {
			db.updateMsgId( onconid,  oldMsgId,  newMsgId);
		}
	}

	synchronized public void updateMessageStatus(String onconid,
			String packetid, SendStatus status) {
		if (db != null) {
			db.updateMessageStatus(onconid, packetid, status);
		}
	}
	
	synchronized public void updateMessageStatusSended(String onconid,
			String packetid) {
		if (db != null) {
			db.updateMessageStatusSended(onconid, packetid);
		}
	}
	
	synchronized public void updateMessageStatusArrived(String onconid,
			String packetid) {
		if (db != null) {
			db.updateMessageStatusArrived(onconid, packetid);
		}
	}
	
	synchronized public void updateMessageStatusReaded(String onconid,
			String packetid) {
		if (db != null) {
			db.updateMessageStatusReaded(onconid, packetid);
		}
	}
	
	synchronized public void updateMessageSendTime(String onconid,
			String packetid, long time) {
		if (db != null) {
			db.updateMessageSendTime(onconid, packetid, time);
		}
	}
	
	synchronized public void updateMessageThumbnailPath(String onconid,
			String packetid, String thumbnailPath) {
		if (db != null) {
			db.updateMessageThumbnailPath(onconid, packetid, thumbnailPath);
		}
	}
	
	synchronized public void updateMessageImagePath(String onconid,
			String packetid, String imagePath) {
		if (db != null) {
			db.updateMessageImagePath(onconid, packetid, imagePath);
		}
	}
	
	synchronized public void updateMessageAudioPath(String onconid,
			String packetid, String audioPath) {
		if (db != null) {
			db.updateMessageAudioPath(onconid, packetid, audioPath);
		}
	}
	
	/**
	 * 更新闪图时间
	 * @param onconid
	 * @param packetid
	 * @param snaptime
	 */
	synchronized public void updateMessageSnapTime(String onconid,
			String packetid, String snaptime) {
		if (db != null) {
			db.updateMessageSnaptime(onconid, packetid, snaptime);
		}
	}

	synchronized public SIXmppMessage queryMessageOfThreadById(String onconid,
			String packetid) {
		try{
			if (db != null) {
				return db.queryMessageOfThreadById(onconid, packetid, mUsername);
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return null;
	}

	synchronized public ArrayList<SIXmppMessage> queryAllMessageOfThread(
			String onconid, int count) {
		if (db != null) {
			return db.queryAllMessageOfThread(onconid, count, mUsername);
		}
		return null;
	}
	
	synchronized public ArrayList<SIXmppMessage> queryAllImageMsgOfThread(String onconid) {
		if (db != null) {
			return db.queryAllImageMsgOfThread(onconid, mUsername);
		}
		return null;
	}
	
	synchronized public ArrayList<SIXmppMessage> queryMsgByWord(String onconid, String word) {
		if (db != null) {
			return db.queryMsgByWord(onconid, mUsername, word);
		}
		return new ArrayList<SIXmppMessage>();
	}
	
	// gaotaiwen
	//分页查询，可以任意页查询
	synchronized public ArrayList<SIXmppMessage> queryMsgByLimit(
			String onconid,int begin, int count, String order) {
		if (db != null) {
			return db.queryMsgByLimit(onconid, mUsername,begin, count, order);
		}
		return new ArrayList<SIXmppMessage>();
	}
	
	//查询前count条记录
	synchronized public ArrayList<SIXmppMessage> queryMsgByCount(
			String onconid, int count) {
		if (db != null) {
			return db.queryMsgByPage(onconid, mUsername, count);
		}
		return null;
	}
	
	
	synchronized public int queryAllMegCount(
			String onconid) {
		if (db != null) {
			return db.queryMsgCount(onconid);
		}
		return 0;
	}
//end gaotaiwen
	
	/**
	 * 查询所有会话的消息总条数
	 * @return
	 */
	synchronized public int queryAllThreadsMessageCount() {
		if (db != null) {
			return db.queryAllThreadsMessageCount();
		}
		return 0;
	}
	
	synchronized public SIXmppMessage getLatestMsgById(String onconid) {
		if (db != null) {
			return db.getLatestMsgById(onconid, mUsername);
		}
		return null;
	}

	synchronized public ArrayList<SIXmppThreadInfo> queryAllThreads() {
		if (db != null) {
			return db.queryAllThreads();
		}
		return null;
	}
	
	synchronized public SIXmppThreadInfo queryThread(String onconid) {
		if (db != null) {
			return db.queryThread(onconid);
		}
		return null;
	}

	synchronized public void deleteMessage(String onconid, String packetid) {
		if (db != null) {
			db.deleteMessage(onconid, packetid);
		}
	}

	synchronized public void deleteThread(String onconid) {
		if (db != null) {
			db.deleteThread(onconid);
		}
	}

	synchronized public void deleteAllThreads() {
		if (db != null) {
			db.deleteAllThreads();
		}
	}
	
	synchronized public void deleteAllThreadsExceptMsgs() {
		if (db != null) {
			db.deleteAllThreadsExceptMsgs();
		}
	}
	/**
	 * 根据传入时间,删除全部会话里所有早于这个时间的消息
	 * @param time
	 */
	synchronized public void deleteAllThreadsMessageByTime(String time) {
		if (db != null) {
			db.deleteAllThreadsMessageByTime(time);
		}
	}
	
	synchronized public SIXmppP2PInfo p2p_query(String onconid) {
		if (db != null) {
			return db.p2p_query(onconid);
		}
		return null;
	}

	synchronized public boolean p2p_setAttributes(String onconid, String key,String value) {
		if (db != null) {
			int r = db.p2p_setAttributes(onconid, key, value);
			if (r >= 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	synchronized public void clearSendingMsg(){
		if (db != null) {
			db.clearSendingMsg();
		}
	}
	
	synchronized public int getGroupProp(String groupId){
		int count = 0;
		if (db != null) {
			count = db.getGroupProp(groupId);
		}
		return count;
	}
	
	synchronized public long insertGroupProp(String groupId){
		long rowid = -1;
		if (db != null) {
			rowid = db.insertGroupProp(groupId);
		}
		return rowid;
	}
	
	synchronized public ArrayList<SIXmppMessage> updateSendMsgReaded(String onconid){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		if (db != null) {
			messages = db.updateSendMsgReaded(onconid);
		}
		return messages;
	}
	
	synchronized public ArrayList<SIXmppMessage> updateSendMsgReaded(String onconid, String time){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		if (db != null) {
			messages = db.updateSendMsgReaded(onconid, time);
		}
		return messages;
	}
	
	synchronized public ArrayList<SIXmppMessage> updateRecvMsgView(String onconid){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		if (db != null) {
			messages = db.updateRecvMsgView(onconid);
		}
		return messages;
	}
	
	synchronized public ArrayList<SIXmppMessage> updateRecvMsgView(String onconid, String time){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		if (db != null) {
			messages = db.updateRecvMsgView(onconid, time);
		}
		return messages;
	}
	
	synchronized public int qryNewMsgCount(String onconid){
		if (db != null) {
			return db.qryNewMsgCount(onconid);
		}
		return 0;
	}
	
	synchronized public void updateMessageReadids(String onconid, String packetid, String readids) {
		if (db != null) {
			db.updateMessageReadids(onconid, packetid, readids);
		}
	}
	synchronized public void updateAllThreadVideoStatus2Zero(){
		if(db != null)
			db.updateAllThreadVideoStatus2Zero();
	}
}
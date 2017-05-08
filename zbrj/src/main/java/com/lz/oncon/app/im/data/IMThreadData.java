package com.lz.oncon.app.im.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.lz.oncon.api.SIXmppMessage;

public class IMThreadData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5933087493734942583L;
	/**
	 * 用户id
	 */
	private String mId = "";
	/**
	 * 昵称
	 */
	private String mNickName = "";
	/**
	 * 视频状态
	 */
	public VideoStatus videoStatus = VideoStatus.NONE;
	public String videoID;
	/**
	 * 消息列表
	 */
	private ArrayList<SIXmppMessage> mMsgs = new ArrayList<SIXmppMessage>();
	
	public enum VideoStatus{
		NONE, LIVING, WATCHING_LIVE, WATCHING_PLAYBACK
	}
	
	public enum Type{
		P2P,GROUP,BATCH
	}
	private Type mType = Type.P2P;
	
	private int intercomCount;
	private ArrayList<String> intercomMembers = new ArrayList<String>();
	public int getIntercomCount() {
		return intercomCount;
	}
	public void setIntercomCount(int intercomCount) {
		this.intercomCount = intercomCount;
	}
	public ArrayList<String> getIntercomMembers() {
		return intercomMembers;
	}
	public void setIntercomMembers(ArrayList<String> intercomMembers) {
		this.intercomMembers = intercomMembers;
	}
	/**
	 * 
	 * @param id
	 * @param nickName
	 * @param messages
	 */	
	public IMThreadData(String id,String nickName,ArrayList<SIXmppMessage> messages, Type type) {
		this.mId = id;
		this.mNickName = nickName;
		this.mMsgs = messages;
		this.mType = type;
	}
	public String getId() {
		return mId;
	}
	public String getNickName() {
		return mNickName;
	}
	public ArrayList<SIXmppMessage> getMsgs() {
		return mMsgs;
	}
	public Type getType() {
		return mType;
	}
	public void setId(String mId) {
		this.mId = mId;
	}
	public void setNickName(String mNickName) {
		this.mNickName = mNickName;
	}
	public void setMsgs(ArrayList<SIXmppMessage> mMsgs) {
		this.mMsgs = mMsgs;
	}
	public void setType(Type type) {
		this.mType = type;
	}
	
	public long getLastMessageTime(){
		try{
			if(mMsgs!=null && mMsgs.size()>0){
				return mMsgs.get(mMsgs.size()-1).getTime();
			}
		}catch(Exception e){}
		return 0;
	}
	
	public SIXmppMessage getImMessageDataByPacketid(String packetid){
		Iterator<SIXmppMessage> it = mMsgs.iterator();
		while (it.hasNext()) {
			SIXmppMessage data = it.next();
			if(data.getId().equals(packetid)){
				return data;
			}
		}
		return null;
	}
}
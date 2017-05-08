package com.lz.oncon.api;

import java.util.ArrayList;

public class SIXmppThreadInfo {
	
	public static final String NOT_STRANGER = "0";
	public static final String STRANGER = "1";
	public static final String ID_STRANGER = "systemid_stranger";
	
	private String username = null;
	private String nickname = null;
	public enum Type{
		P2P,GROUP,BATCH,
	}
	private Type type = Type.P2P;
	private int intercomCount;
	private ArrayList<String> intercomMembers = new ArrayList<String>();
	private String firstnewmsgid;
	private String firstnewmsgtime;
	private String newmsgcount;
	public String videostatus = "0";
	public String videoid;
	public String isstranger = "0";//0-不是,1-是
	public String getFirstnewmsgid() {
		return firstnewmsgid;
	}
	public void setFirstnewmsgid(String firstnewmsgid) {
		this.firstnewmsgid = firstnewmsgid;
	}
	public String getFirstnewmsgtime() {
		return firstnewmsgtime;
	}
	public void setFirstnewmsgtime(String firstnewmsgtime) {
		this.firstnewmsgtime = firstnewmsgtime;
	}
	public String getNewmsgcount() {
		return newmsgcount;
	}
	public void setNewmsgcount(String newmsgcount) {
		this.newmsgcount = newmsgcount;
	}
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
	public SIXmppThreadInfo() {
	}
	public SIXmppThreadInfo(String username,String nickname,Type type) {
		this.username = username;
		this.nickname = nickname;
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "SIXmppThreadInfo [username=" + username + ", nickname="
				+ nickname + ", type=" + type + "]";
	}
	
	
}

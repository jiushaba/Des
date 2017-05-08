package com.lb.zbrj.data;

import java.io.Serializable;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class CommentData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String comentVideoID;//评论的条目id
	public String commentID     ;//评论id       
	public String commentAccount;//评论人账号   
	public String nick          ;//评论人昵称   
	public String imageurl      ;//评论人头像url
	public String content       ;//评论内容   
	public String commentToCommentID;//回复ID
	public String commentToAccount;//被评论人账号
	public String commentToNick;//被评论人昵称
	public int type;//评论类型0为评论1为回复
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json.has("commentID"))commentID = json.getString("commentID");
			if(json.has("commentAccount"))commentAccount = json.getString("commentAccount");
			if(json.has("nick"))nick = json.getString("nick");
			if(json.has("imageUrl"))imageurl = json.getString("imageUrl");
			if(json.has("content"))content = json.getString("content");
			if(json.has("commentToAccount"))commentToAccount = json.getString("commentToAccount");
			if(json.has("commentToNick"))commentToNick = json.getString("commentToNick");
			if(json.has("type"))type = json.getInt("type");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}

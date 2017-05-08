package com.lz.oncon.activity.friendcircle;

import java.io.Serializable;

/**
 * 点赞资源
 * @author yao
 *
 */
public class Source_Up implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5161378721826462788L;
	public String id; // Mongo主键id
	public String userId; // 用户id
	public String feedId; // 动态id
	public String nickname; // 昵称
	public String createTime; // 创建时间
	public String phone; // 手机号码
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "Source_Up [id=" + id + ", userId=" + userId + ", feedId=" + feedId + ", nickname=" + nickname + ", createTime=" + createTime + ", phone=" + phone + "]";
	}
	
}

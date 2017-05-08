package com.lz.oncon.data;

/**
 * 联系人的个人信息
 * @author Administrator
 *
 */
public class PersonInfoData {
	private String mobile; // 移动电话
	private String tags; // 标签
	private String timestamp; // 头像更新时间（由服务器返回）
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}

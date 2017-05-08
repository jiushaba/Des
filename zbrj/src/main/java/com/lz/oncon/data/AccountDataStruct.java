package com.lz.oncon.data;

import android.text.TextUtils;

public class AccountDataStruct {

	String username; // 帐号名称
	String password; // 密码

	String lasttime; // 上次登录时间
	String timestamp; // 格式为：yyyy-mm-dd hh:nn:ss
						// 当前客户端上一次上传头像的时间，如果是第一次则该值为null
	String isautologin; // 是否自动登录 0-不自动登录， 1-自动登录
	String bindphonenumber; // 帐号绑定的手机号码
	String nationalNumber;
	String nickname;//

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	private String lastLoginTime;

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getNationalNumber() {
		return TextUtils.isEmpty(nationalNumber) ? "0086" : nationalNumber;
	}

	public void setNationalNumber(String nationalNumber) {
		this.nationalNumber = nationalNumber;
	}

	public String getIsautologin() {
		return isautologin;
	}

	public String getBindphonenumber() {
		return bindphonenumber;
	}

	public void setBindphonenumber(String bindphonenumber) {
		this.bindphonenumber = bindphonenumber;
	}

	public void setIsautologin(String isautologin) {
		this.isautologin = isautologin;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLasttime() {
		if (TextUtils.isEmpty(lasttime)) {
			lasttime = "0";
		}
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		if (TextUtils.isEmpty(lasttime)) {
			lasttime = "0";
		}
		this.lasttime = lasttime;
	}

	@Override
	public String toString() {
		return "AccountDataStruct [username=" + username + ", password=" + password + ", lasttime=" + lasttime + ", timestamp=" + timestamp
				+ ", isautologin=" + isautologin + ", bindphonenumber=" + bindphonenumber + ", nationalNumber=" + nationalNumber 
				+ ", lastLoginTime=" + lastLoginTime + "]";
	}

}

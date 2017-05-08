package com.lz.oncon.data;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lb.common.util.Encoding;
import com.lz.oncon.application.MyApplication;

public class AccountData extends AccountDataStruct {

	private static AccountData instance;
	private SharedPreferences sp = null;
	private SharedPreferences.Editor editor = null;
	// sessionId
	private String sessionId = "0";
	private String lastUsername;
	private String lastBindphonenumber;
	private String lastPassword;

	// private String lastNationNumber;
	public String getLastUsername() {
		return lastUsername;
	}

	public String getLastBindphonenumber() {
		return lastBindphonenumber;
	}

	public String getLastPassword() {
		return lastPassword;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
		editor.putString("sessionId", Encoding.encodingCanDecode(sessionId));
		editor.commit();
	}

	
	public String getCheckTime() {
		return sp.getString("checkTime", "");
	}

	public String getChioceItem() {
		return sp.getString("chioceItem", "");
	}
	
	public String getCurrVersion() {
		return sp.getString("currVersion", "");
	}

	public static AccountData getInstance() {
		if (instance == null) {
			instance = new AccountData();
		}
		return instance;
	}

	private AccountData() {
		sp = MyApplication.getInstance().getSharedPreferences("currAcc", 0);
		editor = sp.edit();
		String username = sp.getString("username", "");
		String usernameD = Encoding.decoding(username);
		username = TextUtils.isEmpty(usernameD) ? username : usernameD;
		super.setUsername(username);
		String password = sp.getString("password", "");
		String passwordD = Encoding.decoding(password);
		password = TextUtils.isEmpty(passwordD) ? password : passwordD;
		super.setPassword(password);

		String lasttime = sp.getString("lasttime", "0");
		String lasttimeD = Encoding.decoding(lasttime);
		lasttime = TextUtils.isEmpty(lasttimeD) ? lasttime : lasttimeD;
		super.setLasttime(lasttime);
		String timestamp = sp.getString("timestamp", "");
		String timestampD = Encoding.decoding(timestamp);
		timestamp = TextUtils.isEmpty(timestampD) ? timestamp : timestampD;
		super.setTimestamp(timestamp);
		String isautologin = sp.getString("isautologin", "");
		String isautologinD = Encoding.decoding(isautologin);
		isautologin = TextUtils.isEmpty(isautologin) ? isautologin : isautologinD;
		super.setIsautologin(isautologin);
		String bindphonenumber = sp.getString("bindphonenumber", "");
		String bindphonenumberD = Encoding.decoding(bindphonenumber);
		bindphonenumber = TextUtils.isEmpty(bindphonenumberD) ? bindphonenumber : bindphonenumberD;
		super.setBindphonenumber(bindphonenumber);
		String nationalNumber = sp.getString("nationalNumber", "0086");
		String nationalNumberD = Encoding.decoding(nationalNumber);
		nationalNumber = TextUtils.isEmpty(nationalNumberD) ? nationalNumber : nationalNumberD;
		super.setNationalNumber(nationalNumber);
		String lastLoginTime = sp.getString("lastLoginTime", "");
		String lastLoginTimeD = Encoding.decoding(lastLoginTime);
		lastLoginTime = TextUtils.isEmpty(lastLoginTimeD) ? lastLoginTime : lastLoginTimeD;
		super.setLastLoginTime(lastLoginTime);
		String lastUsername = sp.getString("lastUsername", "");
		String lastUsernameD = Encoding.decoding(lastUsername);
		this.lastUsername = TextUtils.isEmpty(lastUsernameD) ? lastUsername : lastUsernameD;
		String lastBindphonenumber = sp.getString("lastBindphonenumber", "");
		String lastBindphonenumberD = Encoding.decoding(lastBindphonenumber);
		this.lastBindphonenumber = TextUtils.isEmpty(lastBindphonenumberD) ? lastBindphonenumber : lastBindphonenumberD;
		String lastPassword = sp.getString("lastPassword", "");
		String lastPasswordD = Encoding.decoding(lastPassword);
		this.lastPassword = TextUtils.isEmpty(lastPasswordD) ? lastPassword : lastPasswordD;

		String sessionId = sp.getString("sessionId", "");
		String sessionIdD = Encoding.decoding(sessionId);
		this.sessionId = TextUtils.isEmpty(sessionIdD) ? sessionId : sessionIdD;
		
		String nickname = sp.getString("nickname", "");
		String nicknameD = Encoding.decoding(nickname);
		nickname = TextUtils.isEmpty(nicknameD) ? nickname : nicknameD;
		super.setNickname(nickname);
	}

	public void clearCurrAcc() {
		MyApplication.getInstance().mPreferencesMan.setSinaAted("");
		MyApplication.getInstance().mPreferencesMan.setTencentAted("");
		MyApplication.getInstance().mPreferencesMan.setTencentRealAted("");

		super.setUsername("");
		editor.putString("username", "");
		super.setPassword("");
		editor.putString("password", "");
		setLasttime("0", false);
		setTimestamp("", false);
		setIsautologin("", false);
		super.setBindphonenumber("");
		editor.putString("bindphonenumber", "");
		// setNationalNumber("");
		setLastLoginTime("", false);
		editor.commit();
	}

	public void clearLastAcc() {
		this.lastUsername = "";
		editor.putString("lastUsername", "");
		this.lastPassword = "";
		editor.putString("lastPassword", "");

		this.lastBindphonenumber = "";
		editor.putString("lastBindphonenumber", "");
		editor.commit();
	}

	public void copy(AccountDataStruct accountDataStruct) {
		setUsername(accountDataStruct.getUsername(), false);
		setPassword(accountDataStruct.getPassword(), false);
		setLasttime(accountDataStruct.getLasttime(), false);
		setTimestamp(accountDataStruct.getTimestamp(), false);
		setIsautologin(accountDataStruct.getIsautologin(), false);
		setBindphonenumber(accountDataStruct.getBindphonenumber(), false);
		setNationalNumber(accountDataStruct.getNationalNumber(), false);
		setLastLoginTime(accountDataStruct.getLastLoginTime(), false);
		editor.commit();
	}

	public void setLastLoginTime(String lastLoginTime) {
		setLastLoginTime(lastLoginTime, true);
	}
	
	private void setLastLoginTime(String lastLoginTime, boolean isCommit) {
		super.setLastLoginTime(lastLoginTime);
		editor.putString("lastLoginTime", Encoding.encodingCanDecode(lastLoginTime));
		if(isCommit)editor.commit();
	}
	
	public void setNationalNumber(String nationalNumber) {
		setNationalNumber(nationalNumber, true);
	}
	
	private void setNationalNumber(String nationalNumber, boolean isCommit) {
		super.setNationalNumber(nationalNumber);
		editor.putString("nationalNumber", Encoding.encodingCanDecode(nationalNumber));
		if(isCommit)editor.commit();
	}

	public void setBindphonenumber(String bindphonenumber) {
		setBindphonenumber(bindphonenumber, true);
	}
	
	private void setBindphonenumber(String bindphonenumber, boolean isCommit) {
		super.setBindphonenumber(bindphonenumber);
		editor.putString("bindphonenumber", Encoding.encodingCanDecode(bindphonenumber));

		this.lastBindphonenumber = bindphonenumber;
		editor.putString("lastBindphonenumber", Encoding.encodingCanDecode(bindphonenumber));
		if(isCommit)editor.commit();
	}

	public void setIsautologin(String isautologin) {
		setIsautologin(isautologin, true);
	}
	
	private void setIsautologin(String isautologin, boolean isCommit) {
		super.setIsautologin(isautologin);
		editor.putString("isautologin", Encoding.encodingCanDecode(isautologin));
		if(isCommit)editor.commit();
	}

	public void setTimestamp(String timestamp) {
		setTimestamp(timestamp, true);
	}
	
	private void setTimestamp(String timestamp, boolean isCommit) {
		super.setTimestamp(timestamp);
		editor.putString("timestamp", Encoding.encodingCanDecode(timestamp));
		if(isCommit)editor.commit();
	}

	public void setUsername(String username) {
		setUsername(username, true);
	}
	
	private void setUsername(String username, boolean isCommit){
		super.setUsername(username);
		editor.putString("username", Encoding.encodingCanDecode(username));

		this.lastUsername = username;
		editor.putString("lastUsername", Encoding.encodingCanDecode(username));
		if(isCommit)editor.commit();
	}

	public void setPassword(String password) {
		setPassword(password, true);
	}
	
	private void setPassword(String password, boolean isCommit) {
		super.setPassword(password);
		editor.putString("password", Encoding.encodingCanDecode(password));

		this.lastPassword = password;
		editor.putString("lastPassword", Encoding.encodingCanDecode(password));
		if(isCommit)editor.commit();
	}

	public void setLasttime(String lasttime) {
		setLasttime(lasttime, true);
	}
	
	private void setLasttime(String lasttime, boolean isCommit) {
		super.setLasttime(lasttime);
		editor.putString("lasttime", Encoding.encodingCanDecode(lasttime));
		editor.commit();
	}
	
	public void setCheckTime(String checkTime) {
		editor.putString("checkTime", checkTime);
		editor.commit();
	}

	public void setChioceItem(String chioceItem) {
		editor.putString("chioceItem", chioceItem);
		editor.commit();
	}
	
	public void setCurrVersion(String currVersion) {
		editor.putString("currVersion", currVersion);
		editor.commit();
	}
	
	public void setNickname(String nickname) {
		setNickname(nickname, true);
	}
	
	private void setNickname(String nickname, boolean isCommit){
		super.setNickname(nickname);
		editor.putString("nickname", Encoding.encodingCanDecode(nickname));
	}

	@Override
	public String toString() {
		return "AccountData [sessionId=" + sessionId + ", lastUsername=" + lastUsername + ", lastBindphonenumber=" + lastBindphonenumber
				+ ", lastPassword=" + lastPassword
				+ ", nationalNumber=" + nationalNumber + "]";
	}

}

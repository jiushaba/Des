package com.lz.oncon.data.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.AccountDataStruct;

/**
 * 对数据库accounts.db中的表account 增删改查
 * 
 * @author Administrator
 * 
 */
public class AccountHelper {
	private SQLiteDatabase db;
	public static int number = 0; // 登录账户的个数

	public AccountHelper() {
		db = DatabaseMan.getInstance().getAccountDB();
	}

	/**
	 * 修改密码
	 * @param password
	 * @param username
	 */
	public void updatePassword(String password, String username, String mobile) {
		String sql = "update account set password = ? where username = ?";
		db.execSQL(sql, new Object[] { password, username });
		
		sql = "update login set password = ? where username = ? or username = ?";
		db.execSQL(sql, new Object[] { password, username, mobile});
	}

	/**
	 * 修改帐号的绑定手机号
	 * 
	 * @param bindPhoneNumber
	 * @param username
	 */
	public void modifyBindPhoneNumber(String bindPhoneNumber, String username) {
		String sql = "update account set bindphonenumber = ? where username = ?";
		db.execSQL(sql, new Object[] { bindPhoneNumber, username });
	}

	/**
	 * 修改帐号最后更新头像时间
	 * 
	 * @param isautologin
	 * @param username
	 */
	public void modifyTimeStamp(String timestamp, String username) {
		String sql = "update account set timestamp = ? where username = ?";
		db.execSQL(sql, new Object[] { timestamp, username });
	}

	/**
	 * 修改帐号的最后同步企业通讯录时间
	 * 
	 * @param lasttime
	 * @param username
	 */
	public void modifyLastTime(String lasttime, String username) {
		String sql = "update account set lasttime = ? where username = ?";
		db.execSQL(sql, new Object[] { lasttime, username });
	}
	
	/**
	 * 修改帐号的最后登录时间
	 * 
	 * @param lastLoginTime
	 * @param username
	 */
	public void modifyLastLoginAcc(String lastLoginTime, String password, String username) {
		String sql = "update account set lastlogintime = ?, password = ? where username = ?";
		db.execSQL(sql, new Object[] { lastLoginTime, password, username});
	}
	
	public void modifyLastLogin(String lastLoginTime, String password, String nationalNumber, String username) {
		String sql = "update login set lastlogintime = ?, password = ?, nationalNumber = ? where username = ?";
		db.execSQL(sql, new Object[] { lastLoginTime, password, nationalNumber, username});
	}

	/**
	 * 查找全部账号信息
	 * 
	 * @return
	 */
	public List<AccountDataStruct> findAll() {
		String sql = "select * from account";
		Cursor c = db.rawQuery(sql, null);
		List<AccountDataStruct> list = null;
		if(c == null){
			return null;
		}
		if (c != null && c.moveToFirst()) {
			list = new ArrayList<AccountDataStruct>();
			do {
				AccountDataStruct account = new AccountDataStruct();
				account.setUsername(c.getString(c.getColumnIndex("username")));
				account.setPassword(c.getString(c.getColumnIndex("password")));
				account.setLasttime(c.getString(c.getColumnIndex("lasttime")));
				account.setTimestamp(c.getString(c.getColumnIndex("timestamp")));
				account.setIsautologin(c.getString(c.getColumnIndex("isautologin")));
				account.setBindphonenumber(c.getString(c.getColumnIndex("bindphonenumber")));
				account.setNationalNumber(c.getString(c.getColumnIndex("nationalNumber")));
				account.setLastLoginTime(c.getString(c.getColumnIndex("lastlogintime")));
				list.add(account);
			} while (c.moveToNext());
		}
		if (c != null) {
			c.close();
		}
		return list;
	}
	
	/**
	 * 查找最近登陆
	 * 
	 * @return
	 */
	public List<AccountDataStruct> findRecentLogin() {
		String sql = "select * from login order by lastlogintime desc limit 7 offset 0";
		Cursor c = null;
		List<AccountDataStruct> list = null;
		try{
			c = db.rawQuery(sql, null);
			if (c != null && c.moveToFirst()) {
				list = new ArrayList<AccountDataStruct>();
				do {
					AccountDataStruct account = new AccountDataStruct();
					account.setUsername(c.getString(c.getColumnIndex("username")));
					account.setPassword(c.getString(c.getColumnIndex("password")));
					account.setLastLoginTime(c.getString(c.getColumnIndex("lastlogintime")));
					account.setNationalNumber(c.getString(c.getColumnIndex("nationalNumber")));
					list.add(account);
				} while (c.moveToNext());
			}
		}catch(Exception e){
		}finally{
			try{
				if(c != null)c.close();
			}catch(Exception e){}
		}
		return list;
	}

	/**
	 * 使用账户名获得该账户上一次更换头像的时间
	 * 
	 * @param username
	 * @return
	 */
	public String findTimestamp(String username) {
		String time = "";
		Cursor c = null;
		try{
			String sql = "select * from account where username = ? or bindphonenumber = ?";
			if(db == null)return time;
			c = db.rawQuery(sql, new String[] { username, username });
			if(c == null) return time;
			if (c.moveToFirst()) {
				time = c.getString(c.getColumnIndex("timestamp"));
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if (c != null) {
					c.close();
				}
			}catch(Exception e){}
		}
		return time;
	}

	/**
	 * 插入一条登录帐号的信息
	 * 
	 * @param accountData
	 */
	public void addAccount(AccountData accountData) {
		String sql = "insert into account (username, password, lasttime, timestamp, isautologin,"
				+ " bindphonenumber, nationalNumber, lastlogintime) values (?,?,?,?,?,?,?,?)";
		db.execSQL(sql, new String[] { accountData.getUsername(),
				accountData.getPassword(), accountData.getLasttime(),
				accountData.getTimestamp(), accountData.getIsautologin(),
				accountData.getBindphonenumber(),
				accountData.getNationalNumber(),
				accountData.getLastLoginTime()});
	}
	
	public void addLogin(AccountData accountData) {
		String sql = "insert into login (username, password, nationalNumber,lastlogintime) values (?,?,?,?)";
		db.execSQL(sql, new String[] { accountData.getUsername(),
				accountData.getPassword(), accountData.getNationalNumber(),accountData.getLastLoginTime()});
	}
}

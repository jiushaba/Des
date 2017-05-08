package com.lz.oncon.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建管理已登录帐号的数据库accounts.db
 * 
 * @author Administrator
 * 
 */
public class DBOpenHelperAccount extends SQLiteOpenHelper {

	public static final int DBVERSION = 1;
	public static final String DB_NAME = "accounts.db";

	public DBOpenHelperAccount(Context context) {
		super(context, DB_NAME, null, DBVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS account (_id INTEGER primary key autoincrement, username TEXT, password TEXT,"
				+ " lasttime TEXT, timestamp TEXT, isautologin TEXT, bindphonenumber TEXT, nationalNumber TEXT,"
				+ " bindemail TEXT, tag TEXT, lastlogintime TEXT)";
		db.execSQL(sql);
		
		sql = "CREATE TABLE IF NOT EXISTS login (_id INTEGER primary key autoincrement, username TEXT, password TEXT,nationalNumber TEXT DEFAULT '0086',"
				+ " lastlogintime TEXT)";
		db.execSQL(sql);
		
		//直播日记好友——本地通讯录
		String sql_localContacts = "CREATE TABLE IF NOT EXISTS local_contacts (_id INTEGER primary key autoincrement, contactid TEXT, contactname TEXT, mobiles TEXT, nameidx TEXT )";
		db.execSQL(sql_localContacts);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
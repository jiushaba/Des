package com.lz.oncon.data.db;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lz.oncon.application.MyApplication;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseMan {

	private static DatabaseMan instance;
	
	private SQLiteDatabase accountdb;
	private Map<String, SQLiteDatabase> dbs = new HashMap<String, SQLiteDatabase>();
	
	private DatabaseMan(){
	}
	
	public static DatabaseMan getInstance(){
		if(instance == null){
			instance = new DatabaseMan();
		}
		return instance;
	}
	
	public void close(){
		if(accountdb != null && accountdb.isOpen()){
			accountdb.close();
		}
		if(dbs != null && dbs.size() > 0){
			Iterator<String> keys = dbs.keySet().iterator();
			while(keys.hasNext()){
				SQLiteDatabase db = dbs.get(keys.next());
				if(db != null && db.isOpen()){
					db.close();
				}
			}
		}
	}
	
	public SQLiteDatabase getAccountDB(){
		if(accountdb != null && accountdb.isOpen()){
			return accountdb;
		}
		try {
			File f = MyApplication.getInstance().getDatabasePath(DBOpenHelperAccount.DB_NAME);
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			accountdb = new DBOpenHelperAccount(MyApplication.getInstance()).getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(accountdb == null){
			Log.e(Constants.LOG_TAG, "accountdb is null");
		}
		return accountdb ;
	}
	
	public SQLiteDatabase getDB(String dbName){
		SQLiteDatabase db = null;
		if(dbs != null && dbs.size() > 0){
			db = dbs.get(dbName);
		}
		if(db != null && db.isOpen()){
			return db;
		}
		try {
			File f = MyApplication.getInstance().getDatabasePath(dbName);
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			db = new DBOpenHelper(MyApplication.getInstance(), dbName).getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(db == null){
			Log.e(Constants.LOG_TAG, "db is null, dbName:" + dbName);
		}
		dbs.put(dbName, db);
		return db ;
	}
}

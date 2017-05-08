package com.lb.zbrj.data.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lz.oncon.data.db.DatabaseMan;

public class BlackHelper {
	private SQLiteDatabase db;

	public BlackHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}
	
	public ArrayList<String> findAll() {
		Cursor c = null;
		ArrayList<String> datas = new ArrayList<String>();
		try{
			String sql = "select * from blacklist order by account";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						datas.add(c.getString(c.getColumnIndex("account")));
					} while (c.moveToNext());
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(c != null)c.close();
			}catch(Exception e){}
		}
		return datas;
	}
	
	public boolean isBlack(String mobile) {
		Cursor c = null;
		boolean isBlack = false;
		try{
			String sql = "select * from blacklist where account='"+mobile+"'";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return isBlack;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					isBlack = true;
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(c != null)c.close();
			}catch(Exception e){}
		}
		return isBlack;
	}
	
	public void delAll(){
		db.delete("blacklist", null, null);
	}
	
	public void del(String mobile){
		db.delete("blacklist", "account=?", new String[]{mobile});
	}
	
	public void insert(ArrayList<String> datas) {
		try{
			db.beginTransaction();
			ContentValues cv = null;
			for(String data:datas){
				cv = new ContentValues();
				cv.put("account", data);
				db.insert("blacklist", null, cv);
			}
			db.setTransactionSuccessful();
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			db.endTransaction();
		}
	}
	
	public void insert(String data) {
		try{
			if(isBlack(data)){
				return;
			}
			ContentValues cv = new ContentValues();
			cv.put("account", data);
			db.insert("blacklist", null, cv);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
}
package com.lb.zbrj.data.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.lb.zbrj.data.FansData;
import com.lz.oncon.data.db.DatabaseMan;

public class FansHelper {
	private SQLiteDatabase db;

	public FansHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}
	
	public ArrayList<FansData> findAllFocused() {
		Cursor c = null;
		ArrayList<FansData> datas = new ArrayList<FansData>();
		try{
			String sql = "select * from fanslist where isfocused = 1 order by account";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return datas;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						FansData data = new FansData();
						data.account = c.getString(c.getColumnIndex("account"));
						data.nick = c.getString(c.getColumnIndex("nick"));
						data.imageurl = c.getString(c.getColumnIndex("imageurl"));
						data.isFocused = c.getInt(c.getColumnIndex("isfocused"));
						data.index = c.getString(c.getColumnIndex("idx"));
						datas.add(data);
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
	
	public ArrayList<FansData> findAllFocused(String search_word) {
		Cursor c = null;
		ArrayList<FansData> datas = new ArrayList<FansData>();
		try{
			String sql = "select * from fanslist where isfocused = 1 and (account like '%"+search_word+"%' or nick like '%"+search_word+"%') order by account";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return datas;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						FansData data = new FansData();
						data.account = c.getString(c.getColumnIndex("account"));
						data.nick = c.getString(c.getColumnIndex("nick"));
						data.imageurl = c.getString(c.getColumnIndex("imageurl"));
						data.isFocused = c.getInt(c.getColumnIndex("isfocused"));
						data.index = c.getString(c.getColumnIndex("idx"));
						datas.add(data);
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
	
	public ArrayList<FansData> findAll() {
		Cursor c = null;
		ArrayList<FansData> datas = new ArrayList<FansData>();
		try{
			String sql = "select * from fanslist order by account";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return datas;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						FansData data = new FansData();
						data.account = c.getString(c.getColumnIndex("account"));
						data.nick = c.getString(c.getColumnIndex("nick"));
						data.imageurl = c.getString(c.getColumnIndex("imageurl"));
						data.isFocused = c.getInt(c.getColumnIndex("isfocused"));
						data.index = c.getString(c.getColumnIndex("idx"));
						datas.add(data);
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
	
	public void delAll(){
		db.delete("fanslist", null, null);
	}
	
	public void insert(ArrayList<FansData> datas) {
		try{
			db.beginTransaction();
			ContentValues cv = null;
			for(FansData data:datas){
				cv = new ContentValues();
				cv.put("account", data.account);
				cv.put("nick", data.nick);
				cv.put("imageurl", data.imageurl);
				cv.put("isFocused", data.isFocused);
				data.index = StringUtils.getAlpha(data.nick);
				cv.put("idx", data.index);
				db.insert("fanslist", null, cv);
			}
			db.setTransactionSuccessful();
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			db.endTransaction();
		}
	}
}
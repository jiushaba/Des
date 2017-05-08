package com.lb.zbrj.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.zbrj.data.PersonData;
import com.lz.oncon.data.db.DatabaseMan;

public class PersonHelper {
	private SQLiteDatabase db;

	public PersonHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}
	
	public PersonData find(String mobile) {
		Cursor c = null;
		PersonData data = null;
		try{
			String sql = "select * from person where account='"+mobile+"'";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					data = new PersonData();
					data.account = c.getString(c.getColumnIndex("account"));
					data.nickname = c.getString(c.getColumnIndex("nickname"));
					data.memoName = c.getString(c.getColumnIndex("memoname"));
					data.image = c.getString(c.getColumnIndex("image"));
					data.sign = c.getString(c.getColumnIndex("sign"));
					data.label = c.getString(c.getColumnIndex("label"));
					data.fansNum = c.getInt(c.getColumnIndex("fansNum"));
					data.oldFansNum = c.getInt(c.getColumnIndex("oldFansNum"));
					data.score = c.getInt(c.getColumnIndex("score"));
					data.focusNum = c.getInt(c.getColumnIndex("focusNum"));
					data.location = c.getString(c.getColumnIndex("location"));
					data.videoNums = c.getInt(c.getColumnIndex("videoNums"));
					data.sex = c.getInt(c.getColumnIndex("sex"));
					data.birthday = c.getString(c.getColumnIndex("birthday"));
					data.timestamp = c.getLong(c.getColumnIndex("timestamp"));
					data.fanstimestamp = c.getLong(c.getColumnIndex("fanstimestamp"));
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(c != null)c.close();
			}catch(Exception e){}
		}
		return data;
	}
	
	public void insert(PersonData data) {
		try{
			PersonData d = find(data.account);
			ContentValues cv = new ContentValues();
			cv.put("nickname", data.nickname);
			cv.put("memoname", data.memoName);
			cv.put("image", data.image);
			cv.put("sign", data.sign);
			cv.put("label", data.label);
			cv.put("fansNum", data.fansNum);
			cv.put("oldFocusNum", data.oldFansNum);
			cv.put("score", data.score);
			cv.put("focusNum", data.focusNum);
			cv.put("location", data.location);
			cv.put("videoNums", data.videoNums);
			cv.put("sex", data.sex);
			cv.put("birthday", data.birthday);
			cv.put("timestamp", data.timestamp);
			cv.put("fanstimestamp", data.fanstimestamp);
			if (d == null) {
				cv.put("account", data.account);
				db.insert("person", null, cv);
			}else{
				db.update("person", cv, "account = ?", new String[]{data.account});
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
}
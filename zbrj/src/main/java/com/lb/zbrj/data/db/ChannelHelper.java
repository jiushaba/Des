package com.lb.zbrj.data.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.zbrj.data.ChannelData;
import com.lz.oncon.data.db.DatabaseMan;

public class ChannelHelper {
	private SQLiteDatabase db;

	public ChannelHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}

	public ArrayList<ChannelData> findAdded() {
		Cursor c = null;
		ArrayList<ChannelData> datas = new ArrayList<ChannelData>();
		try{
			String sql = "select * from channel where isadd = '1' order by seq";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						ChannelData data = new ChannelData();
						data.id = c.getInt(c.getColumnIndex("id"));
						data.name = c.getString(c.getColumnIndex("name"));
						data.seq = c.getInt(c.getColumnIndex("seq"));
						data.isAdd = c.getInt(c.getColumnIndex("isadd"));
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
	
	public ArrayList<ChannelData> findAll() {
		Cursor c = null;
		ArrayList<ChannelData> datas = new ArrayList<ChannelData>();
		try{
			String sql = "select * from channel order by seq";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						ChannelData data = new ChannelData();
						data.id = c.getInt(c.getColumnIndex("id"));
						data.name = c.getString(c.getColumnIndex("name"));
						data.seq = c.getInt(c.getColumnIndex("seq"));
						data.isAdd = c.getInt(c.getColumnIndex("isadd"));
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

	public synchronized void update(ChannelData data) {
		try{
			ContentValues cv = new ContentValues();
			cv.put("seq", data.seq);
			cv.put("isadd", data.isAdd);
			db.update("channel", cv, "id = ?", new String[]{data.id + ""});
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized void update(List<ChannelData> datas) {
		try{
			db.beginTransaction();
			ContentValues cv = null;
			for(ChannelData data:datas){
				cv = new ContentValues();
				cv.put("seq", data.seq);
				cv.put("isadd", data.isAdd);
				db.update("channel", cv, "id = ?", new String[]{data.id + ""});
			}
			db.setTransactionSuccessful();
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			db.endTransaction();
		}
	}
	
	public synchronized void insert(ChannelData data){
		try{
			ArrayList<ChannelData> channels = findAll();
			ContentValues cv = new ContentValues();
			cv.put("name", data.name);
			if (channels == null || channels.size() == 0) {
				cv.put("id", data.id);
				cv.put("isAdd", data.isAdd);
				cv.put("seq", 1);
				db.insert("channel", null, cv);
			}else{
				ChannelData c = null;
				for(ChannelData temp:channels){
					if(temp.id == data.id){
						c = temp;
						break;
					}
				}
				if(c == null){
					cv.put("id", data.id);
					cv.put("isAdd", data.isAdd);
					cv.put("seq", channels.get(channels.size() - 1).seq + 1);
					db.insert("channel", null, cv);
				}else{
					db.update("channel", cv, "id = ?", new String[]{data.id + ""});
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void del(ChannelData data){
		db.delete("channel", "id = ?", new String[]{data.id + ""});
	}
	
	public synchronized void delAll(){
		db.delete("channel", null, null);
	}
}
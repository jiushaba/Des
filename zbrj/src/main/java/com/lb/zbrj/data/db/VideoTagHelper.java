package com.lb.zbrj.data.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.zbrj.data.VideoTagData;
import com.lz.oncon.data.db.DatabaseMan;

public class VideoTagHelper {
	private SQLiteDatabase db;

	public VideoTagHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}

	public ArrayList<VideoTagData> findSystemTags() {
		Cursor c = null;
		ArrayList<VideoTagData> datas = new ArrayList<VideoTagData>();
		try{
			String sql = "select * from video_tag where type = '1' order by seq";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						VideoTagData data = new VideoTagData();
						data.tag = c.getString(c.getColumnIndex("tag"));
						data.seq = c.getInt(c.getColumnIndex("seq"));
						data.type = c.getInt(c.getColumnIndex("type"));
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
	
	public ArrayList<VideoTagData> findRecentTags(String word) {
		Cursor c = null;
		ArrayList<VideoTagData> datas = new ArrayList<VideoTagData>();
		try{
			String sql = "select * from video_tag where type = '0'";
			if(!TextUtils.isEmpty(word)){
				sql += " and tag like '"+word+"%'";
			}
			sql += " order by seq desc";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						VideoTagData data = new VideoTagData();
						data.tag = c.getString(c.getColumnIndex("tag"));
						data.seq = c.getInt(c.getColumnIndex("seq"));
						data.type = c.getInt(c.getColumnIndex("type"));
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
	
	public synchronized void insertRecent(VideoTagData data){
		try{
			ArrayList<VideoTagData> tags = findRecentTags("");
			ContentValues cv = new ContentValues();
			if (tags == null || tags.size() == 0) {
				cv.put("tag", data.tag);
				cv.put("type", "0");
				cv.put("seq", data.seq);
				db.insert("video_tag", null, cv);
			}else{
				VideoTagData c = null;
				for(VideoTagData temp:tags){
					if(temp.tag.equals(data.tag)){
						c = temp;
						break;
					}
				}
				if(c == null){
					cv.put("tag", data.tag);
					cv.put("type", "0");
					cv.put("seq", data.seq);
					db.insert("video_tag", null, cv);
				}else{
					cv.put("seq", data.seq);
					db.update("video_tag", cv, "tag = ?", new String[]{data.tag});
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void delRecent(VideoTagData data){
		db.delete("video_tag", "tag = ? and type = '0'", new String[]{data.tag});
	}
	
	public synchronized void delAll(){
		db.delete("video_tag", null, null);
	}
}
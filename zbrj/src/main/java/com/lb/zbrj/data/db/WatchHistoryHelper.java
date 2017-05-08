package com.lb.zbrj.data.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.data.db.DatabaseMan;

public class WatchHistoryHelper {
	private SQLiteDatabase db;

	public WatchHistoryHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}

	public ArrayList<VideoData> findAll() {
		Cursor c = null;
		ArrayList<VideoData> datas = new ArrayList<VideoData>();
		try{
			String sql = "select * from watch_history order by watchtime desc";
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						VideoData data = new VideoData();
						data.videoID = c.getString(c.getColumnIndex("videoid"));
						data.nick = c.getString(c.getColumnIndex("nick"));
						data.imageUrl = c.getString(c.getColumnIndex("imageurl"));
						data.title = c.getString(c.getColumnIndex("title"));
						data.watchTime = c.getString(c.getColumnIndex("watchtime"));
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
	
	public ArrayList<VideoData> find(int start, int count) {
		Cursor c = null;
		ArrayList<VideoData> datas = new ArrayList<VideoData>();
		try{
			String sql = "select * from watch_history order by watchtime desc limit " + start + "," + count;
			c = db.rawQuery(sql, null);
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						VideoData data = new VideoData();
						data.videoID = c.getString(c.getColumnIndex("videoid"));
						data.nick = c.getString(c.getColumnIndex("nick"));
						data.imageUrl = c.getString(c.getColumnIndex("imageurl"));
						data.title = c.getString(c.getColumnIndex("title"));
						data.watchTime = c.getString(c.getColumnIndex("watchtime"));
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

	public synchronized void del(String videoid) {
		try{
			db.delete("watch_history", "videoid=?", new String[]{videoid});
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void delAll() {
		try{
			db.delete("watch_history", null, null);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized void add(VideoData data) {
		try{
			ContentValues cv = new ContentValues();
			cv.put("videoid", data.videoID);
			cv.put("nick", data.nick);
			cv.put("imageurl", data.imageUrl);
			cv.put("title", data.title);
			cv.put("watchtime", data.watchTime);			
			db.insert("watch_history", null, cv);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void initTestData(){
		//FIXME 测试数据
		db.execSQL("delete from watch_history");
		db.execSQL("insert into watch_history(videoid, nick, imageurl, title, watchtime) values('01','白玫瑰'" +
						",'http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd5fff25ceb53da81cb39db3d0f.jpg'" +
						",'教你做好吃的手撕鸡', '2015-02-04 14:18:14')");
		db.execSQL("insert into watch_history(videoid, nick, imageurl, title, watchtime) values('02','美美丫头'" +
						",'http://c.hiphotos.baidu.com/image/pic/item/d6ca7bcb0a46f21f0491e3cef4246b600d33ae82.jpg'" +
						",'午后阳光', '2015-02-03 14:18:14')");
		db.execSQL("insert into watch_history(videoid, nick, imageurl, title, watchtime) values('03','捕风'" +
						",'http://c.hiphotos.baidu.com/image/pic/item/d1a20cf431adcbefac9fdc19aeaf2edda3cc9f02.jpg'" +
						",'挑战极限', '2015-02-04 15:18:14')");
	}
}
package com.lz.oncon.data.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lb.common.util.Log;
import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.lb.zbrj.data.VideoData;

public class FCHelper {
	private SQLiteDatabase db;

	public FCHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}

	/**
	 * 查询朋友圈全部未读提醒
	 * @return
	 */
	public HashMap<String, VideoData> getAll_NoReadFcNoti(){
		HashMap<String, VideoData> datas = new HashMap<String, VideoData>();
		Cursor c = null;
		try{
			String sql = "select * from fc_noti where states = ? order by optime desc ";
			c = db.rawQuery(sql, new String[]{"0"});
			VideoData data = null;
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						data = new VideoData();
						String dynamic_id = c.getString(c.getColumnIndex("dynamic_id"));
						data.videoID = dynamic_id;
						data.sub_type = c.getString(c.getColumnIndex("subtype"));
						data.post_id = c.getString(c.getColumnIndex("post_id"));
						data.operator = c.getString(c.getColumnIndex("operator"));
						data.optime = c.getString(c.getColumnIndex("optime"));
						data.states = c.getString(c.getColumnIndex("states"));
						if(!TextUtils.isEmpty(dynamic_id)){//动态参与
							datas.put(dynamic_id, data);
						}else{
							try{
								String sql1 = "delete from fc_noti where post_id = ?";
								db.execSQL(sql1, new Object[]{data.post_id});
							}catch(Exception e){
								Log.e(Constants.LOG_TAG, e.getMessage(), e);
							}
						}
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
	
	/**
	 * 查询朋友圈全部已读提醒
	 * @return
	 */
	public HashMap<String, VideoData> getAll_ReadFcNoti(){
		HashMap<String, VideoData> datas = new HashMap<String, VideoData>();
		Cursor c = null;
		try{
			String sql = "select * from fc_noti where states = ? order by optime desc ";
			c = db.rawQuery(sql, new String[]{"1"});
			VideoData data = null;
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						data = new VideoData();
						String dynamic_id = c.getString(c.getColumnIndex("dynamic_id"));
						data.videoID = dynamic_id;
						data.sub_type = c.getString(c.getColumnIndex("subtype"));
						data.post_id = c.getString(c.getColumnIndex("post_id"));
						data.operator = c.getString(c.getColumnIndex("operator"));
						data.optime = c.getString(c.getColumnIndex("optime"));
						data.states = c.getString(c.getColumnIndex("states"));
						if(!TextUtils.isEmpty(dynamic_id)){//动态参与
							datas.put(data.post_id, data);
						}
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
	
	/**
	 * 添加朋友圈提醒
	 * @param dynamic_id
	 * @param dynamic
	 * dynamic_id TEXT,subtype TEXT,post_id TEXT,post_content TEXT,operator TEXT,optime TEXT,states TEXT
	 */
	public void addOrUpdateFcNoti(String dynamic_id, VideoData dynamic){
		try{
			VideoData data = this.getFcNoti(StringUtils.repNull(dynamic.post_id));
			ContentValues cv = new ContentValues();
			cv.put("subtype", StringUtils.repNull(dynamic.sub_type));
			cv.put("post_id", StringUtils.repNull(dynamic.post_id));
			cv.put("operator", StringUtils.repNull(dynamic.operator));
			cv.put("opnick", StringUtils.repNull(dynamic.opnick));
			cv.put("opimageurl", StringUtils.repNull(dynamic.opimageurl));
			cv.put("optime", StringUtils.repNull(dynamic.optime));
			cv.put("states", StringUtils.repNull(dynamic.states));
			if(data == null){
				cv.put("dynamic_id", StringUtils.repNull(dynamic_id));
				db.insert("fc_noti", null, cv);
			}else{
				db.update("fc_noti", cv, "dynamic_id = ? ", new String[]{StringUtils.repNull(dynamic_id)});
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 查找单一提醒
	 * @return
	 */
	public VideoData getFcNoti(String post_id){
		VideoData data = null;
		Cursor c = null;
		try{
			String sql = "select * from fc_noti where post_id = ?";
			c = db.rawQuery(sql, new String[]{post_id});
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						data = new VideoData();
						data.videoID = c.getString(c.getColumnIndex("dynamic_id"));
						data.sub_type = c.getString(c.getColumnIndex("subtype"));
						data.post_id = c.getString(c.getColumnIndex("post_id"));
						data.operator = c.getString(c.getColumnIndex("operator"));
						data.opnick = c.getString(c.getColumnIndex("opnick"));
						data.opimageurl = c.getString(c.getColumnIndex("opimageurl"));
						data.optime = c.getString(c.getColumnIndex("optime"));
						data.states = c.getString(c.getColumnIndex("states"));
						break;
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
		return data;
	}
	
	/**
	 * 清空朋友圈所有的提醒
	 */
	public void clearAllFcNoti(){
		try {
			db.execSQL("delete from fc_noti");
		} catch (Exception e) {
		}
	}
}
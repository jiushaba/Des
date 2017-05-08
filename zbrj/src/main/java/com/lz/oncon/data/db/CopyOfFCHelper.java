package com.lz.oncon.data.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lb.common.util.Log;
import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.lz.oncon.activity.friendcircle.Source_Dynamic;

public class CopyOfFCHelper {
	private SQLiteDatabase db;

	public CopyOfFCHelper(String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}

	/**
	 * 查询朋友圈全部未读提醒
	 * dynamic_id TEXT,subtype TEXT,post_id TEXT,post_content TEXT,operator TEXT,optime TEXT,states TEXT
	 * @return
	 */
	public HashMap<String, Source_Dynamic> getAll_NoReadFcNoti(){
		HashMap<String, Source_Dynamic> datas = new HashMap<String, Source_Dynamic>();
		Cursor c = null;
		try{
			String sql = "select * from fc_noti where states = ? order by optime desc ";
			c = db.rawQuery(sql, new String[]{"0"});
			Source_Dynamic data = null;
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						data = new Source_Dynamic();
						String dynamic_id = c.getString(c.getColumnIndex("dynamic_id"));
						data.setId(dynamic_id);
						data.sub_type = c.getString(c.getColumnIndex("subtype"));
						data.post_id = c.getString(c.getColumnIndex("post_id"));
//						data.post_conrtent = c.getString(c.getColumnIndex("post_content"));
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
	public HashMap<String, Source_Dynamic> getAll_ReadFcNoti(){
		HashMap<String, Source_Dynamic> datas = new HashMap<String, Source_Dynamic>();
		Cursor c = null;
		try{
			String sql = "select * from fc_noti where states = ? order by optime desc ";
			c = db.rawQuery(sql, new String[]{"1"});
			Source_Dynamic data = null;
			if (c == null) {
				return null;
			}
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						data = new Source_Dynamic();
						data = new Source_Dynamic();
						String dynamic_id = c.getString(c.getColumnIndex("dynamic_id"));
						data.setId(dynamic_id);
						data.sub_type = c.getString(c.getColumnIndex("subtype"));
						data.post_id = c.getString(c.getColumnIndex("post_id"));
//						data.post_conrtent = c.getString(c.getColumnIndex("post_content"));
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
	public void addOrUpdateFcNoti(String dynamic_id, Source_Dynamic dynamic){
		try{
			Source_Dynamic data = this.getFcNoti(StringUtils.repNull(dynamic.post_id));
			if(data == null){
				ContentValues cv = new ContentValues();
				cv.put("dynamic_id", StringUtils.repNull(dynamic_id));
				cv.put("subtype", StringUtils.repNull(dynamic.sub_type));
				cv.put("post_id", StringUtils.repNull(dynamic.post_id));
//				cv.put("post_content", StringUtils.repNull(dynamic.post_conrtent));
				cv.put("operator", StringUtils.repNull(dynamic.operator));
				cv.put("optime", StringUtils.repNull(dynamic.optime));
				cv.put("states", StringUtils.repNull(dynamic.states));
				long a = db.insert("fc_noti", null, cv);
			}else{
				ContentValues cv = new ContentValues();
				cv.put("subtype", StringUtils.repNull(dynamic.sub_type));
				cv.put("post_id", StringUtils.repNull(dynamic.post_id));
//				cv.put("post_content", StringUtils.repNull(dynamic.post_conrtent));
				cv.put("operator", StringUtils.repNull(dynamic.operator));
				cv.put("optime", StringUtils.repNull(dynamic.optime));
				cv.put("states", StringUtils.repNull(dynamic.states));
				long b = db.update("fc_noti", cv, "dynamic_id = ? ", new String[]{StringUtils.repNull(dynamic_id)});
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 查找单一提醒
	 * @param app_id
	 * @return
	 */
	public Source_Dynamic getFcNoti(String post_id){
		Source_Dynamic data = null;
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
						data = new Source_Dynamic();
						data.setId(c.getString(c.getColumnIndex("dynamic_id")));
						data.sub_type = c.getString(c.getColumnIndex("subtype"));
						data.post_id = c.getString(c.getColumnIndex("post_id"));
//						data.post_conrtent = c.getString(c.getColumnIndex("post_content"));
						data.operator = c.getString(c.getColumnIndex("operator"));
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
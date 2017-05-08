package com.lz.oncon.data.db;

import com.lb.common.util.Constants;

import android.content.Context;
import android.database.Cursor;
import com.lb.common.util.Log;
import com.lz.oncon.data.PersonInfoData;

import android.database.sqlite.SQLiteDatabase;

/**
 * 操作个人信息
 * 
 * @author Administrator
 * 
 */
public class PerInfoHelper {
	
	private SQLiteDatabase db;

	public PerInfoHelper(Context context, String dbName) {
		db = DatabaseMan.getInstance().getDB(dbName);
	}

	/**
	 * 判断电话为mobile的记录是否存在
	 * 
	 * @param mobile
	 * @return
	 */
	public boolean findPerExist(String mobile) {
		String sql = "select * from per_info where mobile = ?";
		Cursor c = db.rawQuery(sql, new String[] { mobile });
		if (c != null && c.moveToFirst()) {
			c.close();
			return true;
		}
		if (c != null) {
			c.close();
		}
		return false;
	}

	/**
	 * 根据mobile更新tags
	 * 
	 * @param mobile
	 * @param tags
	 */
	public void updateTagsFromMobile(String mobile, String tags) {
		String sql = "update per_info set tags = ? where mobile = ?";
		db.execSQL(sql, new Object[] { tags, mobile });
	}

	/**
	 * 更新 更新头像时间（时间由服务器生成、传回）
	 * 
	 * @param timestamp
	 * @param mobile
	 */
	public void updateTime(String timestamp, String mobile) {
		String sql = "update per_info set timestamp = ? where mobile = ?";
		db.execSQL(sql, new Object[] { timestamp, mobile });
	}

	/**
	 * 根据电话查找是否存在记录
	 * 
	 * @param mobile
	 * @return
	 */
	public synchronized PersonInfoData mobileFindPerson(String mobile) {
		String sql = "select * from per_info where mobile = ?";
		PersonInfoData data = null;
		Cursor c = null;
		try{
			if(db == null)return null;
			c = db.rawQuery(sql, new String[] { mobile });
			if(c == null)return null;
			if (c.moveToFirst()) {
				data = new PersonInfoData();
				data.setMobile(c.getString(c.getColumnIndex("mobile")));
				data.setTags(c.getString(c.getColumnIndex("tags")));
				data.setTimestamp(c.getString(c.getColumnIndex("timestamp")));
				c.close();
				return data;
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if (c != null) c.close();
			}catch(Exception e){
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 添加数据到表
	 * 
	 * @param mobile
	 * @param tags
	 * @param timestamp
	 */
	public void add(String mobile, String tags, String timestamp) {
		String sql = "insert into per_info (mobile, tags, timestamp) values (?,?,?)";
		db.execSQL(sql, new Object[] { mobile, tags, timestamp });
	}
}

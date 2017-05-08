package com.lb.common.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lz.oncon.data.DownloadInfo;

public class DownloadService {

	private SQLiteOpenHelper mOpenHelper;

	public DownloadService(Context context) {
		mOpenHelper = new DownloadDBHelper(context);

	}

	/**
	 * 插入下载记录
	 * 
	 * @param info
	 */
	public void save(DownloadInfo info) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("threadid", info.getThreadid());
			values.put("path", info.getPath());
			values.put("downloadlength", info.getDownloadlength());
			db.insert("download", "_id", values);
		}
		db.close();
	}

	/**
	 * 更新指定的下载记录
	 * 
	 * @param info
	 */
	public void update(DownloadInfo info) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("downloadlength", info.getDownloadlength());
			String whereClause = "threadid = ? and path = ?";
			String[] whereArgs = new String[] { info.getThreadid() + "", info.getPath() };
			db.update("download", values, whereClause, whereArgs);
		}
		db.close();
	}

	/**
	 * 判断下载记录是否存在
	 * 
	 * @param path
	 * @return
	 */
	public boolean isExist(String path) {
		boolean isExist = false;
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			String selection = " path = ?";
			String[] selectionArgs = new String[] { path };
			Cursor c = db.query("download", new String[] { "*" }, selection, selectionArgs, null, null, null);
			if (c.moveToNext()) {
				isExist = true;
			}
			if (c != null) {
				c.close();
			}
		}
		db.close();
		return isExist;
	}

	/**
	 * 计算已经下载总数据量
	 * 
	 * @param path
	 * @return
	 */
	public int getDownloadSize(String path) {
		int size = 0;
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			String selection = " path = ?";
			String[] selectionArgs = new String[] { path };
			Cursor c = db.query("download", new String[] { "*" }, selection, selectionArgs, null, null, null);
			while (c.moveToNext()) {
				int length = c.getInt(c.getColumnIndex("downloadlength"));
				size = size + length;
			}
			if (c != null) {
				c.close();
			}
		}
		db.close();
		return size;
	}

	/**
	 * 得到指定线程下载的数据量
	 * 
	 * @param info
	 * @return
	 */
	public int getDownloadSize(DownloadInfo info) {
		int size = 0;
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			String selection = " threadid = ? and path = ?";
			String[] selectionArgs = new String[] { info.getThreadid() + "", info.getPath() };
			Cursor c = db.query("download", new String[] { "*" }, selection, selectionArgs, null, null, null);
			if (c.moveToNext()) {
				size = c.getInt(c.getColumnIndex("downloadlength"));
			}
			if (c != null) {
				c.close();
			}
		}
		db.close();
		return size;
	}

	/**
	 * 删除下载记录
	 * 
	 * @param path
	 */
	public void delete(String path) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			int delete = db.delete("download", " path = ?", new String[] { path });
			System.out.println("delete============" + delete);
		}
		db.close();
	}
}

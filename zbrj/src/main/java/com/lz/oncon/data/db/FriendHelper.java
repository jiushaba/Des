package com.lz.oncon.data.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lz.oncon.data.FriendData;

/**
 * 直播日记好友DB操作类
 * @author Administrator
 * 
 */
public class FriendHelper {
	private SQLiteDatabase db;

	public FriendHelper() {
		db = DatabaseMan.getInstance().getAccountDB();
	}
	
	/**
	 * 批量添加
	 * @param depts
	 */
	public void add(List<FriendData> datas) {
		try {
			db.beginTransaction();
			String sqlInsert = "insert into local_contacts (contactid, contactname, mobiles, nameidx) values (?,?,?,?)";
			for(FriendData data: datas){
				db.execSQL(sqlInsert, new Object[] { data.getContactid(), data.getContactName(), data.getMobile(), data.getIndex() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void del(List<FriendData> datas) {
		try {
			db.beginTransaction();
			String sqlDel = "delete from local_contacts where contactid = ? and mobiles = ?";
			for(FriendData data: datas){
				db.execSQL(sqlDel, new Object[] { data.getContactName(), data.getMobile() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void upd(List<FriendData> datas) {
		try {
			db.beginTransaction();
			String sqlUpd = "update local_contacts set contactname = ?, nameidx = ? where contactid = ? and mobiles = ?";
			for(FriendData data: datas){
				db.execSQL(sqlUpd, new Object[] { data.getContactName(), data.getIndex(), data.getContactid(), data.getMobile() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 查找所有
	 * 
	 * @return
	 */
	public ArrayList<FriendData> findAll() {
		ArrayList<FriendData> list = new ArrayList<FriendData>();
		String sql = "select contactid,contactname,mobiles,nameidx from local_contacts order by nameidx, contactname COLLATE LOCALIZED ASC";
		Cursor c = db.rawQuery(sql, null);
		if(c != null){
			if (c.moveToFirst()) {
				do {
					FriendData data = new FriendData();
					data.setContactid(c.getString(0));
					data.setContactName(c.getString(1));
					data.setMobile(c.getString(2));
					data.setIndex(c.getString(3));
					list.add(data);
				} while (c.moveToNext());
			}
			c.close();
		}
		return list;
	}
	
	public ArrayList<FriendData> find(String search_word) {
		ArrayList<FriendData> list = new ArrayList<FriendData>();
		String sql = "select contactid,contactname,mobiles,nameidx from local_contacts " +
				" where contactname like ? or mobiles like ? " +
				" order by nameidx, contactname COLLATE LOCALIZED ASC";
		Cursor c = db.rawQuery(sql, new String[]{"%"+search_word+"%", "%"+search_word+"%"});
		if(c != null){
			if (c.moveToFirst()) {
				do {
					FriendData data = new FriendData();
					data.setContactid(c.getString(0));
					data.setContactName(c.getString(1));
					data.setMobile(c.getString(2));
					data.setIndex(c.getString(3));
					list.add(data);
				} while (c.moveToNext());
			}
			c.close();
		}
		return list;
	}

	/**
	 * 删除所有
	 * 
	 * @return
	 */
	public void delAll() {
		db.delete("local_contacts", null, null);
	}

}
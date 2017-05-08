package com.lz.oncon.data.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASEVERSION = 1;
	private String databaseName; // 数据库名 形式为：accout_id.db

	// 个人通讯录备份表
	/**
	 * 创建库 根据登陆时帐号获得库名
	 * 
	 * @param context
	 * @param name
	 */
	public DBOpenHelper(Context context, String name) {
		super(context, name, null, DATABASEVERSION);
		this.databaseName = name;
	}

	/**
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		/* 创建个人信息表 */
		String sql_perInfo = "CREATE TABLE IF NOT EXISTS person (_id INTEGER primary key autoincrement" +
				", account TEXT, nickname TEXT, memoname TEXT, image TEXT, sign TEXT, label TEXT, fansNum TEXT, oldFansNum TEXT, score TEXT" +
				", focusNum TEXT, oldFocusNum TEXT, location TEXT, videoNums TEXT, sex TEXT, birthday TEXT, timestamp long, fanstimestamp long)";
		db.execSQL(sql_perInfo);
		
		createFCNotiTable(db);
		
		createZBRJ(db);
	}

	// 更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	private void execSQL(SQLiteDatabase db, String sql){
		try{
			db.execSQL(sql);
		}catch(Exception e){
		}
	}
	
	/* 创建朋友圈提醒表 */
	private void createFCNotiTable(SQLiteDatabase db){
		execSQL(db, "CREATE TABLE IF NOT EXISTS fc_noti(_id INTEGER primary key autoincrement" +
				", dynamic_id TEXT,subtype TEXT,post_id TEXT,post_content TEXT" +
				",operator TEXT,opnick TEXT, opimageurl TEXT" +
				",optime TEXT,states TEXT)");
	
	}
	
	public void createZBRJ(SQLiteDatabase db){
		//频道表
		StringBuffer sb = new StringBuffer("CREATE TABLE IF NOT EXISTS channel (_id INTEGER primary key autoincrement");
		sb.append(", id INTEGER");
		sb.append(", name TEXT");
		sb.append(", seq INTEGER");
		sb.append(", isadd INTEGER");
		sb.append(")");
		execSQL(db, sb.toString());
		//购物 美容 服装 娱乐 微商 众筹 聚会 教育 生活 二次元
		execSQL(db, "insert into channel(id, name, seq, isadd) values(0,'全部',1,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(1,'购物',2,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(2,'美容',3,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(3,'服装',4,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(4,'娱乐',5,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(5,'微商',6,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(6,'众筹',6,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(7,'聚会',6,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(8,'教育',6,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(9,'生活',6,1)");
		execSQL(db, "insert into channel(id, name, seq, isadd) values(10,'二次元',6,1)");
		//观看历史
		sb = new StringBuffer("CREATE TABLE IF NOT EXISTS watch_history (_id INTEGER primary key autoincrement");
		sb.append(", videoid TEXT");
		sb.append(", nick TEXT");
		sb.append(", imageurl TEXT");
		sb.append(", title TEXT");
		sb.append(", watchtime TEXT");
		sb.append(")");
		execSQL(db, sb.toString());
		//收藏
		sb = new StringBuffer("CREATE TABLE IF NOT EXISTS collect_video (_id INTEGER primary key autoincrement");
		sb.append(", videoid TEXT");
		sb.append(", nick TEXT");
		sb.append(", imageurl TEXT");
		sb.append(", title TEXT");
		sb.append(", watchtime TEXT");
		sb.append(")");
		execSQL(db, sb.toString());
		//黑名单表
		sb = new StringBuffer("CREATE TABLE IF NOT EXISTS blacklist (_id INTEGER primary key autoincrement");
		sb.append(", account TEXT");
		sb.append(")");
		execSQL(db, sb.toString());
		//粉丝表
		sb = new StringBuffer("CREATE TABLE IF NOT EXISTS fanslist (_id INTEGER primary key autoincrement");
		sb.append(", account TEXT");
		sb.append(", nick TEXT");
		sb.append(", imageurl TEXT");
		sb.append(", isfocused TEXT");
		sb.append(", idx TEXT");
		sb.append(")");
		execSQL(db, sb.toString());
		//关注表
		sb = new StringBuffer("CREATE TABLE IF NOT EXISTS focuslist (_id INTEGER primary key autoincrement");
		sb.append(", account TEXT");
		sb.append(", nick TEXT");
		sb.append(", imageurl TEXT");
		sb.append(", isfocused TEXT");
		sb.append(", idx TEXT");
		sb.append(")");
		execSQL(db, sb.toString());
		//标签表
		sb = new StringBuffer("CREATE TABLE IF NOT EXISTS video_tag (_id INTEGER primary key autoincrement");
		sb.append(", tag TEXT");		
		sb.append(", seq LONG");
		sb.append(", type TEXT");
		sb.append(")");
		execSQL(db, sb.toString());
		execSQL(db, "insert into video_tag(tag, seq, type) values('热门', 1, 1)");
		execSQL(db, "insert into video_tag(tag, seq, type) values('推荐', 2, 1)");
		execSQL(db, "insert into video_tag(tag, seq, type) values('好友', 3, 1)");
		execSQL(db, "insert into video_tag(tag, seq, type) values('附近', 4, 1)");
	}
}

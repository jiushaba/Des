package com.lz.oncon.api.core.im.data;

import java.util.ArrayList;

import org.json.JSONObject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.lb.common.util.Log;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppP2PInfo;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.SIXmppMessage.Device;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.api.SIXmppThreadInfo.Type;

class IMDataDBHelper extends SQLiteOpenHelper {

	// 数据库信息
	public static final String DATABASE_NAME_STRING = "im_";// 数据库名称"im_"+username
	public static final int DATABASE_VERSION_INT = 1;

	// 即时消息联系人信息表
	public static final String IM_THREAD_TABLE_NAME_STRING = "oncon_im_thread";
	public static final String IM_THREAD_KEY_ID = "_id";
	public static final String IM_THREAD_ID_STRING = "onconid";
	public static final String IM_THREAD_NICKNAME_STRING = "nickname";
	public static final String IM_THREAD_TYPE = "threadtype";
	public static final String IM_THREAD_VIDEO_STATUS = "videostatus";
	public static final String IM_THREAD_VIDEO_ID = "videoid";
	public static final String IM_THREAD_IS_STRANGER = "isstranger";

	// 具体一个联系人的消息表 ,消息表名是联系人名
	public static final String MESSAGE_TABLE_NAME_STRING = "m_";
	public static final String MESSAGE_KEY_ID = "_id";
	public static final String MESSAGE_PACKET_ID_STRING = "packetid";
	public static final String MESSAGE_PACKET_ID_T_STRING = "packetid_t";
	public static final String MESSAGE_CONTENT_TYPE_INT = "contenttype";
	public static final String MESSAGE_TIME_LONG = "time";
	public static final String MESSAGE_TEXT_CONTENT_STRING = "textcontent";
	
	public static final String MESSAGE_IMAGE_ID_STRING = "imageid";
	public static final String MESSAGE_IMAGE_NAME_STRING = "imagename";
	public static final String MESSAGE_IMAGE_WIDTH_INT = "imagewidth";
	public static final String MESSAGE_IMAGE_HEIGHT_INT = "imageheight";
	public static final String MESSAGE_IMAGE_PATH_STRING = "imagepath";
	public static final String MESSAGE_IMAGE_SIZE_LONG = "imagesize";
	public static final String MESSAGE_IMAGE_URL_STRING = "imageurl";
	
	public static final String MESSAGE_THUMBNAIL_ID_STRING = "thumbnailid";
	public static final String MESSAGE_THUMBNAIL_PATH_STRING = "thumbnailpath";
	public static final String MESSAGE_THUMBNAIL_URL_STRING = "thumbnailurl";
	
	public static final String MESSAGE_AUDIO_ID_STRING = "audioid";
	public static final String MESSAGE_AUDIO_NAME_STRING = "audioname";
	public static final String MESSAGE_AUDIO_PATH_STRING = "audiopath";
	public static final String MESSAGE_AUDIO_SIZE_LONG = "audiosize";
	public static final String MESSAGE_AUDIO_URL_STRING = "audiourl";
	public static final String MESSAGE_AUDIO_TIMELENGTH_INT = "audiotimelength";
	
	public static final String MESSAGE_SOURCE_TYPE_INT = "sourcetype";
	public static final String MESSAGE_SEND_STATUS_INT = "sendstatus";
	public static final String MESSAGE_DEVICE_INT = "device";
	public static final String MESSAGE_FROM_STRING = "fromid";
	public static final String MESSAGE_TO_STRING = "toid";
	public static final String MESSAGE_SNAP_TIME = "snaptime";
	public static final String MESSAGE_NEW_MSG_FLAG = "newmsgflag";
	public static final String MESSAGE_NOREAD_COUNT = "noread_count";
	public static final String MESSAGE_READ_IDS = "read_ids";

	// 圈子列表
	public static final String IM_GROUP_TABLE_NAME_STRING = "oncon_im_group";
	public static final String IM_GROUP_KEY_ID = "_id";
	public static final String IM_GROUP_ID = "id";
	public static final String IM_GROUP_NAME = "name";
	public static final String IM_GROUP_OWNER = "owner";
	public static final String IM_GROUP_MEMBERS = "members";
	public static final String IM_GROUP_SAVETOCONTACT = "save";
	public static final String IM_GROUP_PUSH = "push";
	public static final String IM_GROUP_TONE = "tone";
	public static final String IM_GROUP_TOP = "top";
	public static final String IM_GROUP_THDAPPID = "thdappid";
	public static final String IM_GROUP_THDROOMID = "thdroomid";
	public static final String IM_GROUP_DEPID = "dep_id";
	public static final String IM_GROUP_WSPACEURL = "wspace_url";
	public static final String IM_GROUP_MSPACEURL = "mspace_url";
	//圈子属性表
	public static final String IM_GROUP_PROP_TABLE = "oncon_im_group_prop";
	
	//P2P
	public static final String IM_P2P_TABLE_NAME_STRING = "oncon_im_p2p";
	public static final String IM_P2P_KEY_ID = "_id";
	public static final String IM_P2P_ID = "id";
	public static final String IM_P2P_PUSH = "push";
	public static final String IM_P2P_TONE = "tone";
	public static final String IM_P2P_TOP = "top";
	
	private String mUsername;
	private Context mContext;
	private static SQLiteDatabase db;
	private PersonController mPersonController;

	public IMDataDBHelper(Context context, String username) {
		super(context, DATABASE_NAME_STRING + username, null, DATABASE_VERSION_INT);
		mUsername = username;
		mContext = context;
		mPersonController = new PersonController();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		execSQL(db, "Create TABLE IF NOT EXISTS " + IM_THREAD_TABLE_NAME_STRING + " (" 
					+ IM_THREAD_KEY_ID + " INTEGER primary key autoincrement, " + IM_THREAD_ID_STRING + " text, " 
					+ IM_THREAD_NICKNAME_STRING + " text," + IM_THREAD_TYPE + " text,"
					+ IM_THREAD_VIDEO_STATUS + " text default '0', " + IM_THREAD_VIDEO_ID + " text,"
					+ IM_THREAD_IS_STRANGER + " text default '0'"
					+ ")");
		execSQL(db, "Create TABLE IF NOT EXISTS " + IM_GROUP_TABLE_NAME_STRING 
					+ " (" + IM_GROUP_KEY_ID + " INTEGER primary key autoincrement, " 
					+ IM_GROUP_ID + " text, " + IM_GROUP_NAME + " text, " + IM_GROUP_OWNER + " text, " 
					+ IM_GROUP_SAVETOCONTACT + " text default '1'," + IM_GROUP_PUSH + " text default '1'," 
					+ IM_GROUP_TONE + " text default '1'," + IM_GROUP_TOP + " text default '0'," 
					+ IM_GROUP_MEMBERS + " text," 
					+ IM_GROUP_THDAPPID + " text,"
					+ IM_GROUP_THDROOMID + " text,"
					+ IM_GROUP_DEPID + " text,"
					+ IM_GROUP_WSPACEURL + " text,"
					+ IM_GROUP_MSPACEURL + " text"
					+ ")");
		execSQL(db, "Create TABLE IF NOT EXISTS " + IM_P2P_TABLE_NAME_STRING + " (" + IM_P2P_KEY_ID + " INTEGER primary key autoincrement, " + IM_P2P_ID + " text, " + IM_P2P_PUSH + " text default '1'," + IM_P2P_TONE + " text default '1'," + IM_P2P_TOP + " text default '0')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	private void execSQL(SQLiteDatabase db, String sql){
		try{
			db.execSQL(sql);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized void createTableBeforInsert(SQLiteDatabase db, String onconidTemp) {
		try {
			String sqlString = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " (" + MESSAGE_KEY_ID + " INTEGER primary key autoincrement, " 
					+ MESSAGE_PACKET_ID_STRING + " text, " 
					+ MESSAGE_PACKET_ID_T_STRING + " text, "
					+ MESSAGE_FROM_STRING + " text, " + MESSAGE_TO_STRING + " text, " + MESSAGE_CONTENT_TYPE_INT + " int, " 
					+ MESSAGE_TIME_LONG + " int, " + MESSAGE_TEXT_CONTENT_STRING + " text, " 
					
					+ MESSAGE_IMAGE_ID_STRING + " text, " + MESSAGE_IMAGE_NAME_STRING + " text, " 
					+ MESSAGE_IMAGE_WIDTH_INT + " integer, " + MESSAGE_IMAGE_HEIGHT_INT + " integer, "
					+ MESSAGE_IMAGE_PATH_STRING + " text, " + MESSAGE_IMAGE_URL_STRING + " text, "
					+ MESSAGE_IMAGE_SIZE_LONG + " int, "
					
					+ MESSAGE_THUMBNAIL_ID_STRING + " text, " + MESSAGE_THUMBNAIL_PATH_STRING + " text, "
					+ MESSAGE_THUMBNAIL_URL_STRING + " text, " 
					
					+ MESSAGE_AUDIO_ID_STRING + " text, " + MESSAGE_AUDIO_NAME_STRING + " text, " 
					+ MESSAGE_AUDIO_PATH_STRING + " text, " + MESSAGE_AUDIO_URL_STRING + " text, "  
					+ MESSAGE_AUDIO_TIMELENGTH_INT + " int, " + MESSAGE_AUDIO_SIZE_LONG + " int, "
					
					+ MESSAGE_SOURCE_TYPE_INT + " int, " + MESSAGE_SEND_STATUS_INT + " int, " + MESSAGE_DEVICE_INT + " int," 
					+ MESSAGE_SNAP_TIME + " text default '2', " + MESSAGE_NEW_MSG_FLAG + " text default '0',"
					+ MESSAGE_READ_IDS + " text, " + MESSAGE_NOREAD_COUNT + " text"					
					+ ")";
			db.execSQL(sqlString);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}

	}

	/**
	 * 插入消息
	 * 
	 * @param onconid
	 * @param message
	 */
	public synchronized void insertMessage(String onconid, String nickname, SIXmppMessage message, Type threadType) {
		Cursor cursor = null;
		try {
			db = getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);

			// 更新thread表
			cursor = db.rawQuery("SELECT * FROM " + IM_THREAD_TABLE_NAME_STRING + " WHERE " + IM_THREAD_ID_STRING + "='" + onconid + "'", null);

			if (cursor.getCount() == 0) {
				ContentValues c = new ContentValues();
				c.put(IM_THREAD_ID_STRING, onconid);
				if (!TextUtils.isEmpty(nickname)) {
					c.put(IM_THREAD_NICKNAME_STRING, nickname);
				} else {
					c.put(IM_THREAD_NICKNAME_STRING, onconid);
				}
				if(threadType == null){
					c.put(IM_THREAD_TYPE, "");
				}else{
					c.put(IM_THREAD_TYPE, threadType.ordinal() + "");
				}
				if(!mPersonController.isFriend(onconid)){
					c.put(IM_THREAD_IS_STRANGER, SIXmppThreadInfo.STRANGER);
				}
				db.insert(IM_THREAD_TABLE_NAME_STRING, null, c);
			} else {
				cursor.moveToNext();
				ContentValues c = new ContentValues();
				if (!TextUtils.isEmpty(nickname)) {
					c.put(IM_THREAD_NICKNAME_STRING, nickname);
				} else {
					c.put(IM_THREAD_NICKNAME_STRING, onconid);
				}
				if(threadType == null){
					c.put(IM_THREAD_TYPE, "");
				}else{
					c.put(IM_THREAD_TYPE, threadType.ordinal() + "");
				}
				if(!mPersonController.isFriend(onconid)){
					c.put(IM_THREAD_IS_STRANGER, SIXmppThreadInfo.STRANGER);
				}
				db.update(IM_THREAD_TABLE_NAME_STRING, c, IM_THREAD_ID_STRING + " = ?", new String[] { onconid });
			}

			ContentValues cv = new ContentValues();
			if (message.getId() != null) {
				cv.put(MESSAGE_PACKET_ID_STRING, message.getId());
			}
			if (message.getFrom() != null) {
				cv.put(MESSAGE_FROM_STRING, message.getFrom());
			}
			if (message.getTo() != null) {
				cv.put(MESSAGE_TO_STRING, message.getTo());
			}
			cv.put(MESSAGE_CONTENT_TYPE_INT, message.getContentType().ordinal());
			cv.put(MESSAGE_TIME_LONG, message.getTime());
			if (message.getTextContent() != null) {
				cv.put(MESSAGE_TEXT_CONTENT_STRING, message.getTextContent());
			}
			
			if (message.getImageFileId() != null) {
				cv.put(MESSAGE_IMAGE_ID_STRING, message.getImageFileId() == null ? "" : message.getImageFileId());
			}
			if (message.getImageName() != null) {
				cv.put(MESSAGE_IMAGE_NAME_STRING, message.getImageName());
			}
			if (message.getImagePath() != null) {
				cv.put(MESSAGE_IMAGE_PATH_STRING, message.getImagePath());
			}
			if (message.getImageURL() != null) {
				cv.put(MESSAGE_IMAGE_URL_STRING, message.getImageURL());
			}
			cv.put(MESSAGE_IMAGE_WIDTH_INT, message.getImageWidth());
			cv.put(MESSAGE_IMAGE_HEIGHT_INT, message.getImageHeight());
			cv.put(MESSAGE_IMAGE_SIZE_LONG, message.getImageFileSize());
			
			if (message.getThumbnailFileId() != null) {
				cv.put(MESSAGE_THUMBNAIL_ID_STRING, message.getThumbnailFileId() == null ? "" : message.getThumbnailFileId());
			}
			if (message.getThumbnailPath() != null) {
				cv.put(MESSAGE_THUMBNAIL_PATH_STRING, message.getThumbnailPath());
			}
			if (message.getThumbnailURL() != null) {
				cv.put(MESSAGE_THUMBNAIL_URL_STRING, message.getThumbnailURL());
			}
			
			if (message.getAudioFileId() != null) {
				cv.put(MESSAGE_AUDIO_ID_STRING, message.getAudioFileId());
			}
			if (message.getAudioName() != null) {
				cv.put(MESSAGE_AUDIO_NAME_STRING, message.getAudioName());
			}
			if (message.getAudioPath() != null) {
				cv.put(MESSAGE_AUDIO_PATH_STRING, message.getAudioPath());
			}
			if (message.getAudioURL() != null) {
				cv.put(MESSAGE_AUDIO_URL_STRING, message.getAudioURL());
			}
			cv.put(MESSAGE_AUDIO_TIMELENGTH_INT, message.getAudioTimeLength());
			cv.put(MESSAGE_AUDIO_SIZE_LONG, message.getAudioFileSize());
			
			cv.put(MESSAGE_SOURCE_TYPE_INT, message.getSourceType().ordinal());
			cv.put(MESSAGE_SEND_STATUS_INT, message.getStatus().ordinal());
			cv.put(MESSAGE_DEVICE_INT, message.getDevice().ordinal());
			cv.put(MESSAGE_SNAP_TIME, String.valueOf(message.getSnapTime() == 0 ? 2:message.getSnapTime()));
			cv.put(MESSAGE_NEW_MSG_FLAG, message.getNewMsgFlag());
			db.insert(MESSAGE_TABLE_NAME_STRING + onconidTemp, null, cv);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
	}

	public synchronized void updateThread(SIXmppThreadInfo thread) {
		try {
			db = this.getWritableDatabase();
			String where = IM_THREAD_ID_STRING + " = ?";
			String[] whereValue = { thread.getUsername() };
			ContentValues cv = new ContentValues();
			cv.put(IM_THREAD_NICKNAME_STRING, thread.getNickname());
			if(thread.getType() == null){
				cv.put(IM_THREAD_TYPE, "");
			}else{
				cv.put(IM_THREAD_TYPE, thread.getType().ordinal() + "");
			}
			cv.put(IM_THREAD_VIDEO_STATUS, thread.videostatus);
			cv.put(IM_THREAD_VIDEO_ID, thread.videoid);
			cv.put(IM_THREAD_IS_STRANGER, thread.isstranger);
			db.update(IM_THREAD_TABLE_NAME_STRING, cv, where, whereValue);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	public synchronized void updateAllThreadVideoStatus2Zero() {
		try {
			db = this.getWritableDatabase();
			String where = IM_THREAD_VIDEO_STATUS + " = ? or " +IM_THREAD_VIDEO_STATUS +" = ?";
			String[] whereValue = { "1","2"};
			ContentValues cv = new ContentValues();
			cv.put(IM_THREAD_VIDEO_STATUS, "0");
			cv.put(IM_THREAD_VIDEO_ID, "");
			db.update(IM_THREAD_TABLE_NAME_STRING, cv, where, whereValue);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	public synchronized void updateThreadIsStranger(String onconid, String isstranger) {
		try {
			db = this.getWritableDatabase();
			String where = IM_THREAD_ID_STRING + " = ?";
			String[] whereValue = { onconid };
			ContentValues cv = new ContentValues();
			cv.put(IM_THREAD_IS_STRANGER, isstranger);
			db.update(IM_THREAD_TABLE_NAME_STRING, cv, where, whereValue);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void insertThread(SIXmppThreadInfo thread) {
		try {
			db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(IM_THREAD_ID_STRING, thread.getUsername());
			cv.put(IM_THREAD_NICKNAME_STRING, thread.getNickname());
			if(thread.getType() == null){
				cv.put(IM_THREAD_TYPE, "");
			}else{
				cv.put(IM_THREAD_TYPE, thread.getType().ordinal() + "");
			}
			cv.put(IM_THREAD_VIDEO_STATUS, thread.videostatus);
			cv.put(IM_THREAD_VIDEO_ID, thread.videoid);
			cv.put(IM_THREAD_IS_STRANGER, thread.isstranger);
			db.insert(IM_THREAD_TABLE_NAME_STRING, null, cv);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMsgId(String onconid, String id, String tid){
		try{
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_PACKET_ID_T_STRING, tid);
			String where = MESSAGE_PACKET_ID_STRING + " = ?";
			String[] whereValue = { id };
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv, where, whereValue);
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}

	public synchronized void updateMessage(String onconid, SIXmppMessage message) {
		try {
			db = this.getWritableDatabase();
			String where = MESSAGE_PACKET_ID_STRING + " = ?";
			String[] whereValue = { message.getId() };
			ContentValues cv = new ContentValues();
			if (message.getId() != null) {
				cv.put(MESSAGE_PACKET_ID_STRING, message.getId());
			}
			if (message.getFrom() != null) {
				cv.put(MESSAGE_FROM_STRING, message.getFrom());
			}
			if (message.getTo() != null) {
				cv.put(MESSAGE_TO_STRING, message.getTo());
			}
			cv.put(MESSAGE_CONTENT_TYPE_INT, message.getContentType().ordinal());
			cv.put(MESSAGE_TIME_LONG, message.getTime());
			if (message.getTextContent() != null) {
				cv.put(MESSAGE_TEXT_CONTENT_STRING, message.getTextContent());
			}
			if (message.getImageName() != null) {
				cv.put(MESSAGE_IMAGE_NAME_STRING, message.getImageName());
			}
			if (message.getAudioName() != null) {
				cv.put(MESSAGE_AUDIO_NAME_STRING, message.getAudioName());
			}
			if (message.getImagePath() != null) {
				cv.put(MESSAGE_IMAGE_PATH_STRING, message.getImagePath());
			}
			if (message.getThumbnailPath() != null) {
				cv.put(MESSAGE_THUMBNAIL_PATH_STRING, message.getThumbnailPath());
			}
			if (message.getAudioPath() != null) {
				cv.put(MESSAGE_AUDIO_PATH_STRING, message.getAudioPath());
			}
			if (message.getImageFileId() != null) {
				cv.put(MESSAGE_IMAGE_ID_STRING, message.getImageFileId() == null ? "" : message.getImageFileId());
			}
			if (message.getThumbnailFileId() != null) {
				cv.put(MESSAGE_THUMBNAIL_ID_STRING, message.getThumbnailFileId() == null ? "" : message.getThumbnailFileId());
			}
			if (message.getAudioFileId() != null) {
				cv.put(MESSAGE_AUDIO_ID_STRING, message.getAudioFileId());
			}
			cv.put(MESSAGE_IMAGE_SIZE_LONG, message.getImageFileSize());
			cv.put(MESSAGE_AUDIO_SIZE_LONG, message.getAudioFileSize());
			cv.put(MESSAGE_SOURCE_TYPE_INT, message.getSourceType().ordinal());
			cv.put(MESSAGE_SEND_STATUS_INT, message.getStatus().ordinal());
			cv.put(MESSAGE_DEVICE_INT, message.getDevice().ordinal());
			cv.put(MESSAGE_SNAP_TIME, String.valueOf(message.getSnapTime() == 0 ? 2:message.getSnapTime()));
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv, where, whereValue);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized void updateMessageStatus(String onconid, String packetid, SendStatus status) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " SET " + MESSAGE_SEND_STATUS_INT + "=" + status.ordinal() + " WHERE " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'";
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageStatusSended(String onconid, String packetid) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " SET " + MESSAGE_SEND_STATUS_INT + "=" + SendStatus.STATUS_SENT.ordinal() 
					+ " WHERE (" + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'"
					+ " or " + MESSAGE_PACKET_ID_T_STRING + "='" + packetid + "')"
					+ " AND " + MESSAGE_SEND_STATUS_INT + "<>" + SendStatus.STATUS_ARRIVED.ordinal()
					+ " AND " + MESSAGE_SEND_STATUS_INT + "<>" + SendStatus.STATUS_READED.ordinal();
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageStatusArrived(String onconid, String packetid) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " SET " + MESSAGE_SEND_STATUS_INT + "=" + SendStatus.STATUS_ARRIVED.ordinal() 
					+ " WHERE (" + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'"
					+ " or " + MESSAGE_PACKET_ID_T_STRING + "='" + packetid + "')"
					+ " AND " + MESSAGE_SEND_STATUS_INT + "<>" + SendStatus.STATUS_READED.ordinal();
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageStatusReaded(String onconid, String packetid) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " SET " + MESSAGE_SEND_STATUS_INT + "=" + SendStatus.STATUS_READED.ordinal() 
					+ " WHERE (" + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'"
					+ " or " + MESSAGE_PACKET_ID_T_STRING + "='" + packetid + "')"
					+ " AND " + MESSAGE_SEND_STATUS_INT + "<>" + SendStatus.STATUS_READED.ordinal();
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageSendTime(String onconid, String packetid, long time) {
		try {
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_TIME_LONG, time);
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv
					, MESSAGE_PACKET_ID_STRING + "=? or " + MESSAGE_PACKET_ID_T_STRING + "=?"
					, new String[]{packetid, packetid});
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageThumbnailPath(String onconid, String packetid, String thumbnailPath) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " SET " + MESSAGE_THUMBNAIL_PATH_STRING + "='" + thumbnailPath + "' WHERE " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'";
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageImagePath(String onconid, String packetid, String imagePath) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " SET " + MESSAGE_IMAGE_PATH_STRING + "='" + imagePath + "' WHERE " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'";
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public synchronized void updateMessageAudioPath(String onconid, String packetid, String audioPath) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " SET " + MESSAGE_AUDIO_NAME_STRING + "='" + audioPath + "' WHERE " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'";
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 更新闪图时间
	 * @param onconid
	 * @param packetid
	 * @param snaptime
	 */
	public synchronized void updateMessageSnaptime(String onconid, String packetid, String snaptime) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "UPDATE " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " SET " + MESSAGE_SNAP_TIME + "=" + snaptime + " WHERE " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'";
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 更新阅读情况
	 */
	public synchronized void updateMessageReadids(String onconid, String packetid, String readids) {
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_READ_IDS, readids);
			if(!TextUtils.isEmpty(readids)){
				JSONObject obj = new JSONObject(readids);
				if(obj.has("noreadlist") && !obj.isNull("noreadlist")){
					cv.put(MESSAGE_NOREAD_COUNT, obj.getJSONArray("noreadlist").length() + "");
					if(obj.getJSONArray("noreadlist").length() == 0){
						cv.put(MESSAGE_SEND_STATUS_INT, SendStatus.STATUS_READED.ordinal());
					}
				}
			}
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv, MESSAGE_PACKET_ID_STRING + "=?", new String[]{packetid});
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized SIXmppMessage queryMessageOfThreadById(String onconid, String packetid, String mUsername) {
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();

			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			// before query we must create table...
			createTableBeforInsert(db, onconidTemp);

			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'"
					+ " or " + MESSAGE_PACKET_ID_T_STRING + "='" + packetid + "'"
					, null);

			SIXmppMessage message = null;
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					message = cursor2Message(onconid, cursor, mUsername);
				}
			}
			return message;
		} catch (SQLiteException e) {
			return null;
		} catch (Exception e) {
			return null;
		} finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
	}
	
	public synchronized SIXmppMessage getLatestMsgById(String onconid, String mUsername) {
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			this.createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp +" order by time desc, _id desc limit 1", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					return cursor2Message(onconid, cursor, mUsername);
				}
			}
			
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}

	// gaotaiwen
	public synchronized int queryMsgCount(String onconid) {
		int count = 0;
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp, null);
			if (cursor != null) {
				count = cursor.getCount();
			}
			return count;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return count;
	}
	
	/**
	 * 按照内容查询
	 */
	public synchronized ArrayList<SIXmppMessage> queryMsgByWord(String onconid, String mUsername, String word) {
		Cursor cursor = null;
		try {
			ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where " + MESSAGE_TEXT_CONTENT_STRING + " like '%"+word+"%'", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						messages.add(cursor2Message(onconid, cursor, mUsername));
					} while (cursor.moveToNext());
				}
			}
			return messages;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}

	/**
	 * Description: 前n条查询
	 * 
	 * @param onconid
	 * @param mUsername
	 * @param count
	 *            要查询记录的前多少条
	 * @return ArrayList<SIXmppMessage>
	 */
	public synchronized ArrayList<SIXmppMessage> queryMsgByPage(String onconid, String mUsername, int count) {
		Cursor cursor = null;
		try {
			ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " LIMIT " + count, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						messages.add(cursor2Message(onconid, cursor, mUsername));
					} while (cursor.moveToNext());
				}
			}
			return messages;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}

	/**
	 * Description: 分页查询
	 * 
	 * @param onconid
	 * @param mUsername
	 * @param begin
	 *            查询记录的开始处
	 * @param count
	 *            要查询记录的条数
	 * @return ArrayList<SIXmppMessage>
	 */
	public synchronized ArrayList<SIXmppMessage> queryMsgByLimit(String onconid, String mUsername, int begin, int count, String order) {
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " ORDER BY "+MESSAGE_TIME_LONG + " " + order +",_id "+order+"  LIMIT " + begin + "," + count, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						messages.add(cursor2Message(onconid, cursor, mUsername));
					} while (cursor.moveToNext());
				}
			}
			return messages;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return messages;
	}

	// end gaotaiwen

	public synchronized ArrayList<SIXmppMessage> queryAllMessageOfThread(String onconid, int count, String mUsername) {
		Cursor cursor = null;
		try {
			int index = 0;
			ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " order by time", null);
			if (cursor != null) {
				if (cursor.moveToLast()) {
					do {
						if (index >= count)
							break;
						messages.add(0, cursor2Message(onconid, cursor, mUsername));
						index++;
					} while (cursor.moveToPrevious());
				}
			}
			return messages;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}
	
	public synchronized ArrayList<SIXmppMessage> queryAllImageMsgOfThread(String onconid, String mUsername) {
		Cursor cursor = null;
		try {
			ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where " + MESSAGE_CONTENT_TYPE_INT + " = " + SIXmppMessage.ContentType.TYPE_IMAGE.ordinal()
					+ " order by time", null);
			if (cursor != null) {
				if (cursor.moveToLast()) {
					do {
						messages.add(0, cursor2Message(onconid, cursor, mUsername));
					} while (cursor.moveToPrevious());
				}
			}
			return messages;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}

	public synchronized ArrayList<SIXmppThreadInfo> queryAllThreads() {
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + IM_THREAD_TABLE_NAME_STRING, null);
			ArrayList<SIXmppThreadInfo> threads = new ArrayList<SIXmppThreadInfo>();
			if (cursor.moveToFirst()) {
				do {
					SIXmppThreadInfo threadInfo = new SIXmppThreadInfo();
					threadInfo.setUsername(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_ID_STRING)));
					threadInfo.setNickname(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_NICKNAME_STRING)));
					String threadtype = null;
					try{
						threadtype = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_TYPE));
					}catch(Exception e){}
					if((Type.GROUP.ordinal() + "").equals(threadtype)){
						threadInfo.setType(Type.GROUP);
					}else if((Type.BATCH.ordinal() + "").equals(threadtype)){
						threadInfo.setType(Type.BATCH);
					}else if((Type.P2P.ordinal() + "").equals(threadtype)){
						threadInfo.setType(Type.P2P);
					}else{
						threadInfo.setType(Type.P2P);
					}
					threadInfo.videostatus = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_VIDEO_STATUS));
					threadInfo.videoid = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_VIDEO_ID));
					threadInfo.isstranger = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_IS_STRANGER));
					threads.add(threadInfo);
				} while (cursor.moveToNext());
			}
			return threads;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}
	
	public synchronized SIXmppThreadInfo queryThread(String onconid) {
		Cursor cursor = null;
		SIXmppThreadInfo threadInfo = null;
		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + IM_THREAD_TABLE_NAME_STRING + " where " + IM_THREAD_ID_STRING + " = '"+onconid+"'", null);
			if (cursor.moveToFirst()) {
				do {
					threadInfo = new SIXmppThreadInfo();
					threadInfo.setUsername(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_ID_STRING)));
					threadInfo.setNickname(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_NICKNAME_STRING)));
					String threadtype = null;
					try{
						threadtype = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_TYPE));
					}catch(Exception e){}
					if((Type.GROUP.ordinal() + "").equals(threadtype)){
						threadInfo.setType(Type.GROUP);
					}else if((Type.BATCH.ordinal() + "").equals(threadtype)){
						threadInfo.setType(Type.BATCH);
					}else if((Type.P2P.ordinal() + "").equals(threadtype)){
						threadInfo.setType(Type.P2P);
					}else{
						threadInfo.setType(Type.P2P);
					}
					threadInfo.videostatus = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_VIDEO_STATUS));
					threadInfo.videoid = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_VIDEO_ID));
					threadInfo.isstranger = cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_THREAD_IS_STRANGER));
				} while (cursor.moveToNext());
			}
			return threadInfo;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}
	
	/**
	 * 查询所有的会话的条数
	 */
	public synchronized int queryAllThreadsMessageCount() {
		Cursor cursor = null;
		int single_count = 0;
		int all_count = 0;
		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;", null);//查找所有的表

			if (cursor.moveToFirst()) {
				do {
					String tablename = cursor.getString(cursor.getColumnIndex("name"));
					if(tablename.toLowerCase().startsWith(MESSAGE_TABLE_NAME_STRING)){
						String sqlString = "SELECT count(*) from " + tablename.toLowerCase();
						Cursor single = db.rawQuery(sqlString, null);
						if (single != null) {
							single_count = cursor.getCount();
							all_count += single_count;
						}
					}
				} while (cursor.moveToNext());
			}
			return all_count;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return all_count;
	}

	
	public synchronized void deleteMessage(String onconid, String packetid) {
		try {
			db = this.getWritableDatabase();
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "DELETE FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp + " WHERE " + MESSAGE_PACKET_ID_STRING + "='" + packetid + "'";

			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized void deleteThread(String onconid) {
		try {
			db = this.getWritableDatabase();
			String where = IM_THREAD_ID_STRING + " = ?";
			String[] whereValue = { onconid };
			db.delete(IM_THREAD_TABLE_NAME_STRING, where, whereValue);
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			String sqlString = "DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME_STRING + onconidTemp;
			db.execSQL(sqlString);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public synchronized void deleteAllThreads() {
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;", null);//查找所有的表

			if (cursor.moveToFirst()) {
				do {
					String tablename = cursor.getString(cursor.getColumnIndex("name"));
					if(tablename.toLowerCase().startsWith(MESSAGE_TABLE_NAME_STRING)){
						String sqlString = "DROP TABLE IF EXISTS " + tablename;
						db.execSQL(sqlString);
					}
				} while (cursor.moveToNext());
			}
			db.delete(IM_THREAD_TABLE_NAME_STRING, null, null);

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
	}
	
	public synchronized void deleteAllThreadsExceptMsgs() {
		try {
			db = this.getWritableDatabase();
			db.delete(IM_THREAD_TABLE_NAME_STRING, null, null);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 根据传入时间,删除全部会话里所有早于这个时间的消息
	 * @param time
	 * @return
	 */
	public synchronized void deleteAllThreadsMessageByTime(String time) {
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;", null);//查找所有的表

			if (cursor.moveToFirst()) {
				do {
					String tablename = cursor.getString(cursor.getColumnIndex("name"));
					if(tablename.toLowerCase().startsWith(MESSAGE_TABLE_NAME_STRING)){
						String sqlString = "DELETE from " + tablename.toLowerCase() +" WHERE " + MESSAGE_TIME_LONG +" < " + time;
						db.execSQL(sqlString);
						String sqlString_count = "SELECT count(*) from " + tablename.toLowerCase();
						Cursor single = db.rawQuery(sqlString_count, null);
						if (single != null) {
							int single_count = cursor.getCount();
							if (single_count == 0) {
								String sqlString_drop = "DROP TABLE IF EXISTS " + tablename.toLowerCase();
								db.execSQL(sqlString_drop);
							}
						}
					}
				} while (cursor.moveToNext());
			}
			
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
	}

	private SIXmppMessage cursor2Message(String onconid, Cursor cursor, String mUsername) {
		SIXmppMessage message = new SIXmppMessage();
		message.setId(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_PACKET_ID_STRING)));
		message.setFrom(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_FROM_STRING)));
		message.setTo(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_TO_STRING)));
		int contentTypeIndex = cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_CONTENT_TYPE_INT));
		if (contentTypeIndex >= 0 && contentTypeIndex < ContentType.values().length) {
			message.setContentType(ContentType.values()[contentTypeIndex]);
		}
		message.setTime(cursor.getLong(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_TIME_LONG)));
		message.setTextContent(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_TEXT_CONTENT_STRING)));
		
		message.setImageFileId(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_ID_STRING)));
		message.setImageName(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_NAME_STRING)));
		message.setImagePath(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_PATH_STRING)));
		message.setImageWidth(cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_WIDTH_INT)));
		message.setImageHeight(cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_HEIGHT_INT)));
		message.setImageURL(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_URL_STRING)));
		message.setImageFileSize(cursor.getLong(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_IMAGE_SIZE_LONG)));
		
		message.setThumbnailFileId(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_THUMBNAIL_ID_STRING)));
		message.setThumbnailPath(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_THUMBNAIL_PATH_STRING)));
		message.setThumbnailURL(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_THUMBNAIL_URL_STRING)));
		
		message.setAudioFileId(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_AUDIO_ID_STRING)));
		message.setAudioName(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_AUDIO_NAME_STRING)));
		message.setAudioPath(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_AUDIO_PATH_STRING)));
		message.setAudioURL(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_AUDIO_URL_STRING)));
		message.setAudioFileSize(cursor.getLong(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_AUDIO_SIZE_LONG)));
		message.setAudioTimeLength(cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_AUDIO_TIMELENGTH_INT)));
		
		int sourceTypeIndex = cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_SOURCE_TYPE_INT));
		int snapTime = cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_SNAP_TIME));
		message.setSnapTime(snapTime);
		message.read_ids = cursor.getString(cursor.getColumnIndex(MESSAGE_READ_IDS));
		message.noread_count = cursor.getString(cursor.getColumnIndex(MESSAGE_NOREAD_COUNT));
		if (sourceTypeIndex >= 0 && sourceTypeIndex < SourceType.values().length) {
			message.setSourceType(SourceType.values()[sourceTypeIndex]);
		}
		int sendStatusIndex = cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_SEND_STATUS_INT));
		if (sendStatusIndex >= 0 && sendStatusIndex < SendStatus.values().length) {
			message.setStatus(SendStatus.values()[sendStatusIndex]);
			message.setOldStatus(SendStatus.values()[sendStatusIndex]);
		}
		int deviceIndex = cursor.getInt(cursor.getColumnIndex(IMDataDBHelper.MESSAGE_DEVICE_INT));
		if (deviceIndex >= 0 && deviceIndex < Device.values().length) {
			message.setDevice(Device.values()[deviceIndex]);
		}

		if (message.getSourceType() == SourceType.SEND_MESSAGE) {
			if (message.getFrom() == null || message.getFrom().equals("")) {
				message.setFrom(mUsername);
			}
			if (message.getTo() == null || message.getTo().equals("")) {
				message.setTo(onconid);
			}
		} else if (message.getSourceType() == SourceType.RECEIVE_MESSAGE) {
			if (message.getFrom() == null || message.getFrom().equals("")) {
				message.setFrom(onconid);
			}
			if (message.getTo() == null || message.getTo().equals("")) {
				message.setTo(mUsername);
			}
		}
		return message;
	}
	
	
	public synchronized SIXmppP2PInfo p2p_query(String onconid) {
		SIXmppP2PInfo p2pInfo = new SIXmppP2PInfo();
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + IM_P2P_TABLE_NAME_STRING + " where " + IMDataDBHelper.IM_P2P_ID + "='"+onconid+"'" , null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					p2pInfo.setOnconid(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_P2P_ID)));
					p2pInfo.setPush(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_P2P_PUSH)));
					p2pInfo.setTone(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_P2P_TONE)));
					p2pInfo.setTop(cursor.getString(cursor.getColumnIndex(IMDataDBHelper.IM_P2P_TOP)));
				} while (cursor.moveToNext());
			}
			return p2pInfo;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}
	
	public synchronized int p2p_setAttributes(String onconid, String key, String value) {
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			// 更新group表
			cursor = db.rawQuery("SELECT * FROM " + IM_P2P_TABLE_NAME_STRING + " WHERE " + IM_P2P_ID + "='" + onconid + "'", null);
			int count = cursor.getCount();
			if (count == 0) {
				db.execSQL("insert into " + IM_P2P_TABLE_NAME_STRING + " (" + IM_P2P_ID + "," + key + ")" + " values('" + onconid + "', '" + value + "')");
			} else {
				String where = IM_P2P_ID + " = ?";
				String[] whereValue = { onconid };
				ContentValues cv = new ContentValues();
				cv.put(key, value);
				db.update(IM_P2P_TABLE_NAME_STRING, cv, where, whereValue);
			}
			return 0;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return -1;
	}
	
	public synchronized void clearSendingMsg(){
		try{
			ArrayList<SIXmppThreadInfo> allThreads = queryAllThreads();
			if(allThreads != null && allThreads.size() > 0){
				for(SIXmppThreadInfo thread:allThreads){
					String onconidTemp = MD5.bytes2hex(MD5.md5(thread.getUsername().getBytes()));
					this.createTableBeforInsert(db, onconidTemp);
					ContentValues cv = new ContentValues();
					cv.put(MESSAGE_SEND_STATUS_INT, SIXmppMessage.SendStatus.STATUS_ERROR.ordinal());
					
					db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv, MESSAGE_SEND_STATUS_INT + " = ?", new String[]{SIXmppMessage.SendStatus.STATUS_DRAFT.ordinal()+""});
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	private void createGroupPropTable(){
		try{
			db.execSQL("create table IF NOT EXISTS " + IM_GROUP_PROP_TABLE + " (_id INTEGER primary key autoincrement, id text)");
		}catch(Exception e){}
	}
	
	public int getGroupProp(String groupId){
		int count = 0;
		Cursor cursor = null;
		try {
			createGroupPropTable();
			String keys = "id = ?";
			String[] values = new String[] {groupId};
			cursor = db.query(IM_GROUP_PROP_TABLE, null, keys, values, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					count ++;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return count;
	}
	
	public long insertGroupProp(String groupId){
		try {
			createGroupPropTable();
			ContentValues cv = new ContentValues();
			cv.put("id", groupId);
			return db.insert(IM_GROUP_PROP_TABLE, null, cv);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return -1;
	}
	
	public int deleteGroupProp(String groupId){
		int count = 0;
		try {
			createGroupPropTable();
			count = db.delete(IM_GROUP_PROP_TABLE, "id=?", new String[]{groupId});
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return count;
	}
	
	/**
	 * 更新指定会话消息为已读
	 * @param onconid
	 * @return
	 */
	public ArrayList<SIXmppMessage> updateSendMsgReaded(String onconid){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		Cursor cursor = null;
		try{
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where "+ MESSAGE_SEND_STATUS_INT + " <> '" + SIXmppMessage.SendStatus.STATUS_READED.ordinal() + "'"
					+ " and " + MESSAGE_SOURCE_TYPE_INT + " = '" + SIXmppMessage.SourceType.SEND_MESSAGE.ordinal() + "'", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						SIXmppMessage msg = cursor2Message(onconid, cursor, mUsername);
						msg.setStatus(SendStatus.STATUS_READED);
						messages.add(msg);
					} while (cursor.moveToNext());
				}
			}
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_SEND_STATUS_INT, SIXmppMessage.SendStatus.STATUS_READED.ordinal());
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv, MESSAGE_SEND_STATUS_INT + " <> ? and " + MESSAGE_SOURCE_TYPE_INT + " = ?"
					, new String[]{SIXmppMessage.SendStatus.STATUS_READED.ordinal() + "", SIXmppMessage.SourceType.SEND_MESSAGE.ordinal() + ""});
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return messages;
	}
	
	/**
	 * 根据时间更新指定会话消息为已读
	 * @param onconid
	 * @return
	 */
	public ArrayList<SIXmppMessage> updateSendMsgReaded(String onconid, String time){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		Cursor cursor = null;
		try{
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where "+ MESSAGE_SEND_STATUS_INT + " <> '" + SIXmppMessage.SendStatus.STATUS_READED.ordinal() + "'"
					+ " and " + MESSAGE_TIME_LONG + " <= " + time
					+ " and " + MESSAGE_SOURCE_TYPE_INT + " = '" + SIXmppMessage.SourceType.SEND_MESSAGE.ordinal() + "'", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						SIXmppMessage msg = cursor2Message(onconid, cursor, mUsername);
						msg.setStatus(SendStatus.STATUS_READED);
						messages.add(msg);
					} while (cursor.moveToNext());
				}
			}
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_SEND_STATUS_INT, SIXmppMessage.SendStatus.STATUS_READED.ordinal());
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv
					, MESSAGE_SEND_STATUS_INT + " <> ? and " + MESSAGE_TIME_LONG + " <= ? and " + MESSAGE_SOURCE_TYPE_INT + " = ?"
					, new String[]{SIXmppMessage.SendStatus.STATUS_READED.ordinal() + "", time, SIXmppMessage.SourceType.SEND_MESSAGE.ordinal() + ""});
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return messages;
	}
	
	/**
	 * 更新指定会话消息为已查看
	 * @param onconid
	 * @return
	 */
	public ArrayList<SIXmppMessage> updateRecvMsgView(String onconid){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		Cursor cursor = null;
		try{
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where "+ MESSAGE_NEW_MSG_FLAG + " = '1'"
					+ " and " + MESSAGE_SOURCE_TYPE_INT + " = '" + SIXmppMessage.SourceType.RECEIVE_MESSAGE.ordinal() + "'", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						messages.add(cursor2Message(onconid, cursor, mUsername));
					} while (cursor.moveToNext());
				}
			}
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_NEW_MSG_FLAG, "0");
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv
					, MESSAGE_NEW_MSG_FLAG + " = ? and " + MESSAGE_SOURCE_TYPE_INT + " = ?"
					, new String[]{"1", SIXmppMessage.SourceType.RECEIVE_MESSAGE.ordinal() + ""});
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return messages;
	}
	
	/**
	 * 根据时间更新指定会话消息为已查看
	 * @param onconid
	 * @return
	 */
	public ArrayList<SIXmppMessage> updateRecvMsgView(String onconid, String time){
		ArrayList<SIXmppMessage> messages = new ArrayList<SIXmppMessage>();
		Cursor cursor = null;
		try{
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT * FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where "+ MESSAGE_NEW_MSG_FLAG + " = '1' and " + MESSAGE_TIME_LONG +" <= " + time 
					+ " and " + MESSAGE_SOURCE_TYPE_INT + " = '" + SIXmppMessage.SourceType.RECEIVE_MESSAGE.ordinal() + "'", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						messages.add(cursor2Message(onconid, cursor, mUsername));
					} while (cursor.moveToNext());
				}
			}
			ContentValues cv = new ContentValues();
			cv.put(MESSAGE_NEW_MSG_FLAG, "0");
			db.update(MESSAGE_TABLE_NAME_STRING + onconidTemp, cv
					, MESSAGE_NEW_MSG_FLAG + " = ? and " + MESSAGE_TIME_LONG + " <= ? and " + MESSAGE_SOURCE_TYPE_INT + " = ?"
					, new String[]{"1", time, SIXmppMessage.SourceType.RECEIVE_MESSAGE.ordinal() + ""});
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return messages;
	}
	
	public int qryNewMsgCount(String onconid){
		int count = 0;
		Cursor cursor = null;
		try{
			String onconidTemp = MD5.bytes2hex(MD5.md5(onconid.getBytes()));
			createTableBeforInsert(db, onconidTemp);
			cursor = db.rawQuery("SELECT count(1) FROM " + MESSAGE_TABLE_NAME_STRING + onconidTemp 
					+ " where "+ MESSAGE_NEW_MSG_FLAG + " = '1'"
					+ " and " + MESSAGE_SOURCE_TYPE_INT + " = '" + SIXmppMessage.SourceType.RECEIVE_MESSAGE.ordinal() + "'", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					count = cursor.getInt(0);
				}
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return count;
	}
}
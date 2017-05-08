package com.lz.oncon.api.core.im.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.lz.oncon.api.CustomProtocolListener;
import com.lz.oncon.api.SIXmppConnectionListener;
import com.lz.oncon.api.SIXmppHistoryManager;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppReceiveMessageListener;
import com.lz.oncon.api.SIXmppSendMessageListener;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.SIXmppMessage.Device;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.api.SIXmppThreadInfo.Type;
import com.lz.oncon.api.core.im.data.Constants;
import com.lz.oncon.api.core.im.data.IMDataDB;
import com.lz.oncon.api.util.TimeUtils;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.easemob.util.NetUtils;

public class OnconIMCore{
	
	private static final int POOL_CAPACITY = 10;
	
	private ExecutorService threadPool;

	// singleinstance
	private static OnconIMCore instance = null;
	
	// 用户信息
	private String username = null;
	public String getUsername() {
		return username;
	}
	private String password = null;

	private Context mContext = null;
	private int mConnectionStatus = SIXmppConnectionListener.FAILED;
	private int mConnectionErrorCode, mLoginErrorCode;
	
	public int getmConnectionErrorCode() {
		return mConnectionErrorCode;
	}
	
	public int getLoginErrorCode() {
		return mLoginErrorCode;
	}

	public void setmConnectionStatus(int mConnectionStatus) {
		this.mConnectionStatus = mConnectionStatus;
	}

	/**
	 * 获得IMCore实例
	 * 
	 * @return
	 */
	public synchronized static OnconIMCore getInstance() {
		if (instance == null) {
			instance = new OnconIMCore();
		}
		return instance;
	}

	private void setContext(Context context) {
		this.mContext = context;
		Constants.LOG_TAG = context.getPackageName();
		Constants.PACKAGENAME = context.getPackageName();
		IMDataDB.FILE_TEMP_DIC = Environment.getExternalStorageDirectory() + "/" + Constants.PACKAGENAME + "/oncon/temp/";
	}

	private OnconIMCore() {
		mConnectionListeners = new ArrayList<SIXmppConnectionListener>();
		mReceiveMessageListeners = new ArrayList<SIXmppReceiveMessageListener>();
		mSendMessageListeners = new ArrayList<SIXmppSendMessageListener>();
		mCustomProtocolListeners = new ArrayList<CustomProtocolListener>();
		threadPool = Executors.newFixedThreadPool(POOL_CAPACITY);
		IMDataDB.getInstance().clearSendingMsg();
	}

//	private boolean isLogining = false;

	/**
	 * 登录
	 * 
	 * @param username
	 * @param password
	 */
	public void login(String username, String password, String resource, boolean auth) {
//		if (isLogining) {
//			return;
//		}
//		isLogining = true;
//		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(resource)) {
//			setLoginStatus(SIXmppConnectionListener.FAILED);
//			isLogining = false;
//			return;
//		}
//		if(!NetUtils.hasNetwork(mContext)){
//			setLoginStatus(SIXmppConnectionListener.FAILED);
//			isLogining = false;
//			return;
//		}
		this.EMLogin(username, password);
	}

	/**
	 * 登出
	 */
	public void logout() {
		try{
			try {
	            EMChatManager.getInstance().logout();
	            setLoginStatus(SIXmppConnectionListener.FAILED);
	        } catch (Exception e) {
	        	Log.e(Constants.LOG_TAG, e.getMessage(), e);
	        }
			IMDataDB.getInstance().close();
			Log.e(Constants.LOG_TAG, "im logout--------------------");
		}catch(Exception e){
		}
	}
	
	/**
	 * 清理数据
	 */
	public void clear() {
		username = null;
		password = null;

		mContext = null;
	}

	/*
	 * SIXmppConnectioLisener
	 */
	private ArrayList<SIXmppConnectionListener> mConnectionListeners = null;

	public ArrayList<SIXmppConnectionListener> getmConnectionListeners() {
		return mConnectionListeners;
	}

	public void addConnectionListener(SIXmppConnectionListener listener) {
		mConnectionListeners.add(listener);
	}

	public void removeConnectionListener(SIXmppConnectionListener listener) {
		mConnectionListeners.remove(listener);
	}
	/*
	 * SIXmppConnectioLisener end
	 */
	
	/*
	 * CustomProtocolListener
	 */
	private ArrayList<CustomProtocolListener> mCustomProtocolListeners = null;

	public ArrayList<CustomProtocolListener> getCustomProtocolListeners() {
		return mCustomProtocolListeners;
	}

	public void addCustomProtocolListener(CustomProtocolListener listener) {
		mCustomProtocolListeners.add(listener);
	}

	public void removeCustomProtocolListener(CustomProtocolListener listener) {
		mCustomProtocolListeners.remove(listener);
	}
	/*
	 * CustomProtocolListener end
	 */
	
	private void handleMessage(final EMMessage message, SIXmppMessage msg, final boolean isGroup){
		String from = message.getFrom().split("@")[0];
		String to = message.getTo().split("@")[0];
		final String onconid = isGroup ? from : from.equals(username) ? to: from;
		final Type threadType = isGroup ? Type.GROUP : Type.P2P;
		switch (msg.getContentType()) {
		case TYPE_TEXT:
		case TYPE_IMAGE:
		case TYPE_AUDIO:
		case TYPE_LOC:
			msg.setNewMsgFlag("0".equals(msg.getOnconArrived()) ? "1" : "0");
			handleReceivedMessage(onconid, onconid, msg, true, threadType);
			break;
		case TYPE_TALK_PIC:
		case TYPE_SNAP_PIC:
		case TYPE_PUBLICACCOUNT_NAMECARD://服务号名片
		case TYPE_IMAGE_TEXT://图文分享消息
		case TYPE_HTML_TEXT:
		case TYPE_HTML_TEXT_2:
		case TYPE_DYN_EXP:
		case TYPE_NEWS:
		case TYPE_FILE:
		case TYPE_MUSIC:
		case TYPE_FRIENDCIRCLE_NOTI:
		case TYPE_HTML_TEXT_GENERAL:
		case TYPE_LINK_MSG:
		case TYPE_APP_MSG:
		case TYPE_APP_NOTI:
			break;
		default:
			break;
		}
	}

	private void handleP2PMessage(EMMessage message) {
		// 获取联系人信息
		String onconid = message.getFrom();
		// 接收消息
		SIXmppMessage msg = OnconIMMessage.createSIXmppMessageByEMMessage(message, SourceType.RECEIVE_MESSAGE);
		if(msg.getFrom().equals(username)){
			msg.setSourceType(SourceType.SEND_MESSAGE);
			msg.setStatus(SendStatus.STATUS_SENT);
			onconid = message.getTo();
		}
		if (IMDataDB.getInstance().queryMessageOfThreadById(onconid, msg.getId()) != null) {// 过滤重复消息
			return;
		}
		if(ContentType.TYPE_CUSTOM_PROTOCOL.ordinal() == msg.getContentType().ordinal()){
			handleManageMessage(message, msg);
		}else{
			handleMessage(message, msg, false);
		}
	}

	private void handleManageMessage(EMMessage message, SIXmppMessage msg) {
		String body = msg.getTextContent();
		if(body.startsWith("@custom_protocol")){//自定义协议
			try{
				HashMap<String, String> params = OnconIMMessage.parseCustomProtocol(body);
				ArrayList<CustomProtocolListener> listeners = new ArrayList<CustomProtocolListener>();
				listeners.addAll(mCustomProtocolListeners);
				if(body.startsWith("@custom_protocol@:recommand_friend?")){//1.16 推荐给好友接口
					String from = message.getFrom().split("@")[0];
					String onconid = from;
					msg.setNewMsgFlag("0".equals(msg.getOnconArrived()) ? "1" : "0");
					handleReceivedMessage(onconid, onconid, msg, true, Type.P2P);
				}
				else if (body.startsWith("@custom_protocol@:request_join_live?")) {//1.33 （非服务器）请求加入直播间（仅当直播间为授权模式可用）
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.request_join_live(params.get("account"), params.get("nick"), params.get("videoID"));
						}
					}
				}else if(body.startsWith("@custom_protocol@:private_bullet?")){//1.34 （非服务器）直播间内发送私聊信息(直播间@消息）
					String from = message.getFrom().split("@")[0];
					String onconid = from;
					msg.setNewMsgFlag("0".equals(msg.getOnconArrived()) ? "1" : "0");
					handleReceivedMessage(onconid, onconid, msg, true, Type.P2P);
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.private_bullet(params.get("account"), params.get("msg"), params.get("videoID"));
						}
					}
				}else if(body.startsWith("@custom_protocol@:kick_off_video?")){//1.35 （非服务器）直播间踢出
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.kick_off_video(params.get("account"), params.get("nick"), params.get("videoID"), params.get("videoTitle"));
						}
					}
				}else if(body.startsWith("@custom_protocol@:mute_video?")){//1.36  (非服务器)直播间禁言
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.mute_video(params.get("account"), params.get("nick"), params.get("videoID")
									, params.get("videoTitle"));
						}
					}
				}else if(body.startsWith("@custom_protocol@:forbid_bullet?")){//1.38 服务器禁止弹幕推送（im消息）
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							String s = params.get("is_open");
							listener.forbid_bullet(params.get("videoID"),s== null?"0":s);
						}
					}
				}else if(body.startsWith("@custom_protocol@:friend_status?")){//1.39 推送好友当前操作状态
//					SIXmppThreadInfo thread = IMDataDB.getInstance().queryThread(params.get("account"));
//					if(params.get("type").equals("0") && thread != null){
//						thread.videoid = params.get("videoID");
//						thread.videostatus = params.get("type");
//						IMDataDB.getInstance().updateThread(thread);
//					}else if(params.get("type").equals("1") 
//							|| params.get("type").equals("2")
//							|| params.get("type").equals("3")){
//						if(thread == null){
//							thread = new SIXmppThreadInfo();
//							thread.setUsername(params.get("account"));
//							thread.videoid = params.get("videoID");
//							thread.videostatus = params.get("type");
//							IMDataDB.getInstance().insertThread(thread);
//						}else{
//							thread.videoid = params.get("videoID");
//							thread.videostatus = params.get("type");
//							IMDataDB.getInstance().updateThread(thread);
//						}
//					}
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.friend_status(message.getFrom().split("@")[0], params.get("type"), params.get("videoID"));
						}
					}
					if("1".equals(params.get("type"))){
						handleReceivedMessage(message.getFrom().split("@")[0], message.getFrom().split("@")[0], msg, true, Type.P2P);
					}
				}else if(body.startsWith("@custom_protocol@:invite_video?")){//1.41 （非服务器）邀请观看视频信息
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.invite_video(params.get("account"), params.get("nick"), params.get("videoID")
									, params.get("videoTitle"), params.get("playurl"));
						}
					}
					handleReceivedMessage(params.get("account"), params.get("nick"), msg, true, Type.P2P);
				}else if(body.startsWith("@custom_protocol@:entrust_invite_video?")){//1.42 （非服务器）委托邀请
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.entrust_invite_video(params.get("videoID"));
						}
					}
				}else if(body.startsWith("@custom_protocol@:comment_notify?")){//1.52评论通知消息接口
					String commenVideoID = params.get("commenVideoID");
					String commentid = params.get("commentid");
					String account = params.get("account");
					String nick = params.get("nick");
					String imageurl = params.get("imageurl");
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.comment_notify(commenVideoID, commentid, account, nick, imageurl);
						}
					}
				}else if(body.startsWith("@custom_protocol@:response_join_live?")){//1.33 请求加入直播间 回应消息
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.response_join_live(params.get("account"), params.get("nick"), params.get("videoID")
									, params.get("videoTitle"), params.get("accept"));
						}
					}
					
				}else if(body.startsWith("@custom_protocol@:focus_notify?")){//1.54 添加/删除关注通知接口
					int opType = Integer.parseInt(params.get("optType"));
					int isSpecial = Integer.parseInt(params.get("isSpecial"));
					String account = params.get("account");
					String nick = params.get("nick");
					String imageurl = params.get("imageurl");
					for(CustomProtocolListener listener:listeners){
						if(listener != null){
							listener.focus_notify(opType, isSpecial, account, nick, imageurl);
						}
					}
				}
			}catch(Exception e){
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
	}

	private synchronized void handleReceivedMessage(String onconid, String nickname
			, SIXmppMessage msg, boolean insertDB, Type threadType) {
		if (onconid == null || msg == null) {
			return;
		}
		if (IMDataDB.getInstance().queryMessageOfThreadById(onconid, msg.getId()) != null) {// 过滤重复消息
			return;
		}
		if(insertDB){
			IMDataDB.getInstance().insertMessage(onconid, nickname, msg, threadType);
		}
		Iterator<SIXmppReceiveMessageListener> receiveListenerIt = mReceiveMessageListeners.iterator();
		while (receiveListenerIt.hasNext()) {
			receiveListenerIt.next().receiveMessage(onconid, msg);
		}
	}

	/**
	 * 消息发送成功的反馈处理
	 * 
	 * @param onconid
	 * @param message
	 */
	private void sendSuccess(final String onconid, final EMMessage message) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try{
					SIXmppMessage msg = new SIXmppHistoryManager(mContext, username, password).getMessageById(onconid, message.getMsgId());
					long time = message.getMsgTime();
					if (msg != null) {
						if(msg.getStatus().ordinal() == SendStatus.STATUS_ARRIVED.ordinal()
								|| msg.getStatus().ordinal() == SendStatus.STATUS_READED.ordinal()){
							//已送达、已阅读状态不更新状态
						}else{
							msg.setStatus(SendStatus.STATUS_SENT);
						}
						if(time != 0){
							long timeDiff = TimeUtils.getStringByTime(time);
							msg.setTime(timeDiff);
							IMDataDB.getInstance().updateMessageSendTime(onconid, message.getMsgId(), msg.getTime());
						}
						// & save into database
						if(msg.getStatus().ordinal() == SendStatus.STATUS_ARRIVED.ordinal()
								|| msg.getStatus().ordinal() == SendStatus.STATUS_READED.ordinal()){
							//已送达、已阅读状态不更新状态
						}else{
							IMDataDB.getInstance().updateMessageStatusSended(onconid, message.getMsgId());
						}
						ArrayList<SIXmppSendMessageListener> listeners = new ArrayList<SIXmppSendMessageListener>();
						listeners.addAll(mSendMessageListeners);
						for(int i=0;i<listeners.size();i++){
							if(listeners.get(i) != null){
								listeners.get(i).statusChanged(msg);
							}
						}
					}
				}catch(Exception e){
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * 消息送达成功的反馈处理
	 * 
	 * @param onconid
	 * @param message
	 */
	private void arrivedSuccess(final String onconid, final EMMessage message) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try{
					Iterator<SIXmppSendMessageListener> listenerit = mSendMessageListeners.iterator();
					SIXmppMessage msg = new SIXmppHistoryManager(mContext, username, password).getMessageById(onconid, message.getMsgId());
					if (msg != null) {
						if(msg.getStatus().ordinal() != SendStatus.STATUS_READED.ordinal()){
							msg.setStatus(SendStatus.STATUS_ARRIVED);
							// & save into database
							IMDataDB.getInstance().updateMessageStatusArrived(onconid, message.getMsgId());
						}
						// notif
						while (listenerit.hasNext()) {
							listenerit.next().statusChanged(msg);
						}
					}
				}catch(Exception e){
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		});
	}
	
	private void readed(final String onconid, final EMMessage message) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try{
					Iterator<SIXmppSendMessageListener> listenerit = mSendMessageListeners.iterator();
					SIXmppMessage msg = new SIXmppHistoryManager(mContext, username, password).getMessageById(onconid, message.getMsgId());
					if (msg != null) {
						msg.setStatus(SendStatus.STATUS_READED);
						// & save into database
						IMDataDB.getInstance().updateMessageStatusReaded(onconid, message.getMsgId());
						// notif
						while (listenerit.hasNext()) {
							listenerit.next().statusChanged(msg);
						}
					}
				}catch(Exception e){
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		});
	}

	private ArrayList<SIXmppReceiveMessageListener> mReceiveMessageListeners;

	public ArrayList<SIXmppReceiveMessageListener> getmReceiveMessageListeners() {
		return mReceiveMessageListeners;
	}

	public void setmReceiveMessageListeners(ArrayList<SIXmppReceiveMessageListener> mReceiveMessageListeners) {
		this.mReceiveMessageListeners = mReceiveMessageListeners;
	}

	public void addReceiverMessageListener(SIXmppReceiveMessageListener listener) {
		mReceiveMessageListeners.add(listener);
	}

	public void removeReceiverMessageListener(SIXmppReceiveMessageListener listener) {
		mReceiveMessageListeners.remove(listener);
	}

	private ArrayList<SIXmppSendMessageListener> mSendMessageListeners;

	public void addSendMessageListener(SIXmppSendMessageListener listener) {
		mSendMessageListeners.add(listener);
	}

	public void removeSendMessageListener(SIXmppSendMessageListener listener) {
		mSendMessageListeners.remove(listener);
	}

	/*
	 * receive message end
	 */

	/*
	 * send message
	 */
	private SIXmppMessage createMessage(String toOnconid, ContentType contentType){
		SIXmppMessage message = new SIXmppMessage();
		message.setDevice(Device.DEVICE_ANDROID);
		message.setFrom(username);
		message.setId(UUID.randomUUID().toString());
		message.setSourceType(SourceType.SEND_MESSAGE);
		message.setStatus(SendStatus.STATUS_DRAFT);
		message.setTime(System.currentTimeMillis());
		message.setTo(toOnconid);
		message.setContentType(contentType);
		return message;
	}
	
	/**
	 * 发送音乐消息
	 */
	public SIXmppMessage sendMusicMessage(String toOnconid, String songId, String songName, String singer, String songPath, String bigImgPath, String smallImgPath, SIXmppThreadInfo.Type mtype){
		if(toOnconid == null || songId == null || songName == null || singer == null || songPath == null || bigImgPath == null || smallImgPath == null){
			// 并且通知失败
			return null;
		}
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_MUSIC);
		message.setTextContent("m1_extend_msg@@@lz-oncon@@@v1.0|||type=11|||songId="+songId+"|||songName="+songName+"|||singer="+singer+"|||songPath="+songPath+"|||bigImgPath="+bigImgPath+"|||smallImgPath="+smallImgPath);
		
		sendMessage(message, mtype, true, false);
		
		return message;
	}
	
	/**
	 * 服务号名片
	 */
	public SIXmppMessage sendPublicAccountNameCardMessage(String toOnconid, String pubaccount_id, String pubaccount_name, SIXmppThreadInfo.Type mtype){
		if(TextUtils.isEmpty(toOnconid) || TextUtils.isEmpty(pubaccount_id) 
				|| TextUtils.isEmpty(pubaccount_name)){
			// 并且通知失败
			return null;
		}
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_PUBLICACCOUNT_NAMECARD);
		message.setTextContent("m1_extend_msg@@@lz-oncon@@@v1.0|||type=19|||pubaccount_id="+pubaccount_id
				+"|||pubaccount_name="+pubaccount_name);
		
		sendMessage(message, mtype, true, false);
		
		return message;
	}
	
	/**
	 * 服务号名片
	 */
	public SIXmppMessage sendImageTextMessage(String toOnconid, String title, String brief, String image_url, String detail_url,
			String pub_account,	String author, SIXmppThreadInfo.Type mtype){
		if(TextUtils.isEmpty(toOnconid) || TextUtils.isEmpty(title) 
				|| TextUtils.isEmpty(brief) || TextUtils.isEmpty(image_url) || TextUtils.isEmpty(detail_url)){
			// 并且通知失败
			return null;
		}
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_IMAGE_TEXT);
		message.setTextContent("m1_extend_msg@@@lz-oncon@@@v1.0|||type=20|||title="+title+"|||brief="+brief
				+"|||image_url="+image_url+"|||detail_url="+detail_url + "|||pub_account=" + pub_account +"|||author="+author);
		
		sendMessage(message, mtype, true, false);
		
		return message;
	}
	
	/**
	 * 链接消息	适用于分享到直播日记好友、直播日记人脉圈等场景，但不限于此场景。
	 */
	public SIXmppMessage sendLinkMessage(String toOnconid, SIXmppMessage.LinkMsgType subtype, String title, String desc, String link, String img_url,
			String img_width, String img_height, String source, SIXmppThreadInfo.Type mtype){
		if(subtype == null || TextUtils.isEmpty(toOnconid) || TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(link)){
			return null;
		}
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_LINK_MSG);
		StringBuffer sb = new StringBuffer("m1_extend_msg@@@lz-oncon@@@v1.0|||type=27|||subtype=");
		sb.append(subtype.ordinal() + 1);
		sb.append("|||title=");
		sb.append(title);
		sb.append("|||desc=");
		sb.append(desc);
		sb.append("|||link=");
		sb.append(link);
		sb.append("|||img_url=");
		sb.append(StringUtils.repNull(img_url));
		sb.append("|||img_width=");
		sb.append(StringUtils.repNull(img_width));
		sb.append("|||img_height=");
		sb.append(StringUtils.repNull(img_height));
		sb.append("|||source=");
		sb.append(StringUtils.repNull(source));
		message.setTextContent(sb.toString());
		sendMessage(message, mtype, true, false);
		
		return message;
	}
	
	/**
	 * 发送文本消息
	 */
	public SIXmppMessage sendTextMessage(String toOnconid, String content, SIXmppThreadInfo.Type mtype, boolean needDB) {
		if (toOnconid == null || content == null) {
			// 并且通知失败
			return null;
		}
		content = StringUtils.checkXmlChar(content);
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_TEXT);
		message.setTextContent(content);

		sendMessage(message, mtype, needDB, false);

		return message;
	}
	
	public SIXmppMessage sendLocMessage(String toOnconid, String coor, String longtitude, String latitude, String loc, SIXmppThreadInfo.Type mtype) {
		if (toOnconid == null || coor == null || longtitude == null || latitude == null || loc == null) {
			// 并且通知失败
			return null;
		}
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_LOC);
		message.setTextContent("m1_extend_msg@@@lz-oncon@@@v1.0|||type=2|||coor="+coor+"|||long="+longtitude+"|||lat="+latitude+"|||loc="+loc);

		sendMessage(message, mtype, true, false);

		return message;
	}

	public SIXmppMessage sendDynExpMessage(String toOnconid, String name, SIXmppThreadInfo.Type mtype, String desc) {
		if (toOnconid == null || name == null) {
			// 并且通知失败
			return null;
		}
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_DYN_EXP);
		message.setTextContent("m1_extend_msg@@@lz-oncon@@@v1.0|||type=3|||name="+name+"|||desc="+desc);

		sendMessage(message, mtype, true, false);

		return message;
	}
	
	/**
	 * 发送会说话的图片消息
	 */
	public SIXmppMessage sendTalkPicMessage(String toOnconid, String imagePath, String audioPath, SIXmppThreadInfo.Type mtype) {
		if (toOnconid == null || audioPath == null || imagePath == null) {
			// 并且通知失败
			return null;
		}

		// 生成消息体，并且发送
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_TALK_PIC);
		message.setImagePath(imagePath);
		message.setAudioPath(audioPath);
		File audioFile = new File(audioPath);
		if (audioFile.exists()) {
			message.setAudioFileSize(audioFile.length());
		} else {
			// 并且通知失败
			return null;
		}
		File imageFile = new File(imagePath);
		if (audioFile.exists()) {
			message.setImageFileSize(imageFile.length());
		} else {
			// 并且通知失败
			return null;
		}

		sendMessage(message, mtype, true, false);

		return message;
	}
	
	/**
	 * 发送闪图消息
	 */
	public SIXmppMessage sendSnapPicMessage(String toOnconid, String imagePath, int snapTime, SIXmppThreadInfo.Type mtype) {
		if (toOnconid == null || imagePath == null) {
			// 并且通知失败
			return null;
		}
		// 生成消息体，并且发送
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_SNAP_PIC);
		message.setSnapTime(snapTime);
		message.setImagePath(imagePath);
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			message.setImageFileSize(imageFile.length());
		} else {
			// 并且通知失败
			return null;
		}

		sendMessage(message, mtype, true, false);

		return message;
	}

	/**
	 * 发送音频消息
	 */
	public SIXmppMessage sendAudioMessage(String toOnconid, String audioPath, int audioTimeLength, SIXmppThreadInfo.Type mtype) {
		if (toOnconid == null || audioPath == null) {
			// 并且通知失败
			return null;
		}
		// 生成消息体，并且发送
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_AUDIO);
		message.setAudioPath(audioPath);
		message.setAudioTimeLength(audioTimeLength);
		sendMessage(message, mtype, true, false);

		return message;
	}

	/**
	 * 发送图片消息
	 */
	public SIXmppMessage sendImageMessage(String toOnconid,
			String imagePath, SIXmppThreadInfo.Type mtype) {
		if (toOnconid == null || imagePath == null) {
			// 并且通知失败
			return null;
		}

		// 生成消息体，并且发送
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_IMAGE);
		message.setImagePath(imagePath);
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			message.setImageFileSize(imageFile.length());
		} else {
			// 并且通知失败
			return null;
		}

		sendMessage(message, mtype, true, false);

		return message;
	}
	
	/**
	 * 发送文件消息
	 */
	public SIXmppMessage sendFileMessage(String toOnconid, String filePath, SIXmppThreadInfo.Type mtype) {
		if (toOnconid == null || filePath == null) {
			// 并且通知失败
			return null;
		}

		// 生成消息体，并且发送
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_FILE);
		message.setImagePath(filePath);
		try{
			File f = new File(filePath);
			message.setImageFileSize(f.length());
			message.setImageName(f.getName());
		}catch(Exception e){}
		
		sendMessage(message, mtype, true, false);

		return message;
	}

	/**
	 * 发送消息，通用消息
	 * 
	 * @param message
	 */
	public SIXmppMessage sendMessage(SIXmppMessage message, final SIXmppThreadInfo.Type mtype, final boolean needDB, final boolean isForward) {
		// 2.send status to other module
		message.setStatus(SendStatus.STATUS_DRAFT);
		// 3.save message into database
		if(needDB){
			IMDataDB db = IMDataDB.getInstance();
			if (db.queryMessageOfThreadById(message.getTo(), message.getId()) == null) {
				if(message.getTo().indexOf(",") > -1){
					db.insertMessage(message.getTo(), message.getTo(), message, Type.BATCH);
				}else{
					db.insertMessage(message.getTo(), message.getTo(), message, mtype);
				}
			} else {
				db.updateMessage(message.getTo(), message);
			}
		}
		// 4.notif other modules
		Iterator<SIXmppSendMessageListener> listenerit = mSendMessageListeners.iterator();
		while (listenerit.hasNext()) {
			listenerit.next().statusChanged(message);
		}
		ContentType contentType = message.getContentType();
		if(isForward){
		} else if(ContentType.TYPE_TEXT == contentType){
			EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
			TextMessageBody txtBody = new TextMessageBody(message.getTextContent());
			// 设置消息body
			msg.addBody(txtBody);
			sendMessage(message.getTo(), message.getId(), mtype, msg);
		} else if(ContentType.TYPE_CUSTOM_PROTOCOL == contentType){
			if(needDB){
				EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
				TextMessageBody txtBody = new TextMessageBody(message.getTextContent());
				// 设置消息body
				msg.addBody(txtBody);
				sendMessage(message.getTo(), message.getId(), mtype, msg);
			}else{
				EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.CMD);
				CmdMessageBody cmdBody = new CmdMessageBody(message.getTextContent());
				// 设置消息body
				msg.addBody(cmdBody);
				sendMessage(message.getTo(), message.getId(), mtype, msg);
			}
		} else if (contentType == ContentType.TYPE_IMAGE) {
			EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
			ImageMessageBody body = new ImageMessageBody(new File(message.getImagePath()));
			// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
			// body.setSendOriginalImage(true);
			msg.addBody(body);
			sendMessage(message.getTo(), message.getId(), mtype, msg);
		} else if (contentType == ContentType.TYPE_AUDIO) {
			EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			VoiceMessageBody body = new VoiceMessageBody(new File(message.getAudioPath()), message.getAudioTimeLength());
			msg.addBody(body);
			sendMessage(message.getTo(), message.getId(), mtype, msg);
		} else if (contentType == ContentType.TYPE_TALK_PIC) {
		} else if (contentType == ContentType.TYPE_SNAP_PIC) {
		} else if (contentType == ContentType.TYPE_FILE){
		} else if (contentType == ContentType.TYPE_LOC) {
			EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
			HashMap<String, String> params = OnconIMMessage.parseExtMsg(message.getTextContent());
			LocationMessageBody locBody = new LocationMessageBody(params.get("loc")
					, Double.parseDouble(params.get("lat"))
					, Double.parseDouble(params.get("long")));
			msg.addBody(locBody);
			sendMessage(message.getTo(), message.getId(), mtype, msg);
		}
		return message;
	}
	
	private void sendMessage(final String to, final String packetid, Type mtype, final EMMessage message){
		if (SIXmppThreadInfo.Type.P2P.ordinal() == mtype.ordinal()) {
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(to);
			message.setMsgId(packetid);
			// send message
			try {
				EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
					@Override
					public void onError(int code, java.lang.String message) {
						sendMessageError(to, packetid);
					}
					@Override
					public void onProgress(int progress, java.lang.String status) {
					}
					@Override
					public void onSuccess() {
						IMDataDB.getInstance().updateMsgId(to, packetid, message.getMsgId());
						sendSuccess(to, message);
					}
				});
			}catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
				sendMessageError(to, packetid);
			}
			try {
				if(message.getBody() instanceof CmdMessageBody){
					System.out.println("CmdMessageBody");
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(17 * 1000);
						} catch (InterruptedException e) {
						}
						sendMessageError(to, packetid);
					}
				}).start();
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
				sendMessageError(to, packetid);
			}
		} else if(SIXmppThreadInfo.Type.GROUP.ordinal() == mtype.ordinal()){
		} else if(SIXmppThreadInfo.Type.BATCH.ordinal() == mtype.ordinal()){
		}
	}

	private void sendMessageError(String toOnconid, String packetID) {
		SIXmppMessage msg = new SIXmppHistoryManager(mContext, username, password).getMessageById(toOnconid, packetID);
		if (msg != null && msg.getStatus() != null && msg.getStatus() == SendStatus.STATUS_DRAFT) {
			msg.setStatus(SendStatus.STATUS_ERROR);
			IMDataDB.getInstance().updateMessageStatus(toOnconid, packetID, SendStatus.STATUS_ERROR);
			Iterator<SIXmppSendMessageListener> listenerit = mSendMessageListeners.iterator();
			while (listenerit.hasNext()) {
				listenerit.next().statusChanged(msg);
			}
			//FIXME 暂时不用
//			if(SIXmppConnectionListener.CONNECTTING != mConnectionStatus){
//				Iterator<SIXmppConnectionListener> connectionListenerIterator = mConnectionListeners.iterator();
//				while (connectionListenerIterator.hasNext()) {
//					connectionListenerIterator.next().loginStatusChanged(SIXmppConnectionListener.FAILED);
//				}
//			}
		}
	}
	
	public void sendReadMessage(String to, String packetid){
		try {
			EMChatManager.getInstance().ackMessageRead(to, packetid);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public SIXmppMessage forwardMessage(String textContent, String toOnconid, Type mtype, ContentType contentType){
		SIXmppMessage message = createMessage(toOnconid, contentType);
		message.setTextContent(textContent);
		sendMessage(message, mtype, true, true);
		return message;
	}

	/*
	 * send message end
	 */
	/*
	 * ----------------------------------------------- 点对点聊天 end
	 * -----------------------------------------------
	 */
	
	private void setLoginStatus(int loginstatus){
		if(mConnectionStatus == loginstatus)return;
		ArrayList<SIXmppConnectionListener> listeners = new ArrayList<SIXmppConnectionListener>();
		listeners.addAll(mConnectionListeners);
		for(SIXmppConnectionListener listener:listeners){
			if(listener != null)listener.loginStatusChanged(loginstatus);
		}
		if(mConnectionStatus == SIXmppConnectionListener.SUCCESS){
			EMAfterLogin();
		}
	}
	
	public SIXmppMessage sendCustomProtocolMsg(String toOnconid, String content) {
		if (toOnconid == null || content == null) {
			// 并且通知失败
			return null;
		}
		content = StringUtils.checkXmlChar(content);
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_CUSTOM_PROTOCOL);
		message.setTextContent(content);
		
		sendMessage(message, SIXmppThreadInfo.Type.P2P, true, false);

		return message;
	}
	
	public SIXmppMessage sendCustomProtocolMsgNoDB(String toOnconid, String content) {
		if (toOnconid == null || content == null) {
			// 并且通知失败
			return null;
		}
		content = StringUtils.checkXmlChar(content);
		SIXmppMessage message = createMessage(toOnconid, ContentType.TYPE_CUSTOM_PROTOCOL);
		message.setTextContent(content);

		sendMessage(message, SIXmppThreadInfo.Type.P2P, false, false);

		return message;
	}
	
	public void initEM(Context context){
		this.setContext(context);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null ||!processAppName.equalsIgnoreCase(mContext.getPackageName())) {
        	// 则此application::onCreate 是被service 调用的，直接返回
        	return;
        }
        // 初始化环信SDK,一定要先调用init()
        EMChat.getInstance().init(mContext);
	}
	
	@SuppressWarnings("rawtypes")
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = mContext.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}
	
	private void EMLogin(String username, String password){
		this.username = username;
		this.password = password;
		IMDataDB.getInstance().init(mContext, username);
		Looper.prepare();
		initEMOptions();
        // 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(username, password, new EMCallBack() {

			@Override
			public void onSuccess() {
				setLoginStatus(SIXmppConnectionListener.SUCCESS);
			}

			@Override
			public void onProgress(int progress, String status) {
				setLoginStatus(SIXmppConnectionListener.CONNECTTING);
			}

			@Override
			public void onError(final int code, final String message) {
				Log.e(Constants.LOG_TAG, "code:" + code + ",message:"+ message);
				setLoginStatus(SIXmppConnectionListener.FAILED);
			}
		});
		Looper.loop();
	}
	
	private void initEMOptions(){
        // 设置sandbox测试环境
        // 建议开发者开发时设置此模式
        EMChat.getInstance().setEnv(EMEnvMode.EMProductMode);
        // set debug mode in development process
        EMChat.getInstance().setDebugMode(true);  
		// 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(false);
        // 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
        options.setNotifyBySoundAndVibrate(false);
        // 设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(false);
        // 设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(false);
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(true);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(true);
	}
	
	private void EMAfterLogin(){
		// 注册一个接收消息的BroadcastReceiver
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		mContext.registerReceiver(EMNewMessageBroadcastReceiver, intentFilter);
		
		// 注册一个消息送达的BroadcastReceiver
		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
		deliveryAckMessageIntentFilter.setPriority(5);
		mContext.registerReceiver(EMDeliveryAckMessageReceiver, deliveryAckMessageIntentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		mContext.registerReceiver(EMAckMessageReceiver, ackMessageIntentFilter);
		
		//注册一个透传消息的BroadcastReceiver
		IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
//		cmdMessageIntentFilter.setPriority(3);
		mContext.registerReceiver(EMCmdMessageReceiver, cmdMessageIntentFilter);
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(EMMyConnectionListener);
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}
	
	private EMNewMessageBroadcastReceiver EMNewMessageBroadcastReceiver = new EMNewMessageBroadcastReceiver();
	/**
	 * 新消息广播接收者
	 */
	private class EMNewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
//			String from = intent.getStringExtra("from");
			//消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();
			handleP2PMessage(message);
		}
	}
	
	private BroadcastReceiver EMDeliveryAckMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isDelivered = true;
					arrivedSuccess(from, msg);
				}
			}
		}
	};

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver EMAckMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
					readed(from, msg);
				}
			}
		}
	};
	
	/**
	 * 透传消息BroadcastReceiver
	 */
	private BroadcastReceiver EMCmdMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			abortBroadcast();
			//获取cmd message对象
			//String msgId = intent.getStringExtra("msgid");
			try{
			EMMessage message = intent.getParcelableExtra("message");
			//获取消息body
//			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
//			String action = cmdMsgBody.action;//获取自定义action
			//FIXME 环信自带命令,是否启用?
			handleP2PMessage(message);
			}catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
	};
	
	private EMMyConnectionListener EMMyConnectionListener = new EMMyConnectionListener();
	/**
	 * 连接监听listener
	 */
	private class EMMyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			setLoginStatus(SIXmppConnectionListener.SUCCESS);
		}

		@Override
		public void onDisconnected(final int error) {
			setLoginStatus(SIXmppConnectionListener.FAILED);
			if(error == EMError.USER_REMOVED){
				// 显示帐号已经被移除
			}else if (error == EMError.CONNECTION_CONFLICT) {
				// 显示帐号在其他设备登陆dialog
			} else {
				if (NetUtils.hasNetwork(mContext)){//连接不到聊天服务器
				} else{//当前网络不可用，请检查网络设置
				}
			}
		}
	}
}
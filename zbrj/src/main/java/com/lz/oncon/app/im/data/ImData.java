package com.lz.oncon.app.im.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.text.TextUtils;

import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;

import com.lb.common.util.Constants;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.activity.friendcircle.FriendCircleCacheUtil;
import com.lz.oncon.api.CustomProtocolListener;
import com.lz.oncon.api.SIXmppHistoryManager;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppP2PInfo;
import com.lz.oncon.api.SIXmppReceiveMessageListener;
import com.lz.oncon.api.SIXmppSendMessageListener;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.core.im.data.IMDataDB;
import com.lz.oncon.app.im.data.IMThreadData.Type;
import com.lz.oncon.app.im.util.CompareIMSThread;
import com.lz.oncon.app.im.util.CompareIMThread;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.db.FCHelper;
import com.lz.oncon.receiver.OnNotiReceiver;

public class ImData {

	// roster arraylist
	private ArrayList<String> mIndexs = new ArrayList<String>();
	private Map<String, IMThreadData> mDatas = null;
	//stranger
	private ArrayList<String> mSIndexs = new ArrayList<String>();
	private Map<String, IMThreadData> mSDatas = null;
	
	private SIXmppHistoryManager mHistoryManager = null;
	private List<OnDataChangeListener> onDataChangeListeners;
	private List<OnMsgDelListener> onMsgDelListeners;

	private static ImData instance = null;
	private SIXmppReceiveMessageListener mSIXmppReceiveMessageListener = null;
	private SIXmppSendMessageListener mSIXmppSendMessageListener = null;
	private CustomProtocolListener mCustomProtocolListener = null;
	private FCHelper mFCHelper;
	private PersonController mPersonController;

	public static boolean isInstanciated() {
		return instance != null;
	}
	
	public synchronized static ImData getInstance() {
		if (instance == null) {
			instance = new ImData();
		}
		return instance;
	}

	private ImData() {
		mDatas = Collections.synchronizedMap(new HashMap<String, IMThreadData>());
		mSDatas = Collections.synchronizedMap(new HashMap<String, IMThreadData>());
		mHistoryManager = new SIXmppHistoryManager(MyApplication.getInstance()
				.getApplicationContext(), ImCore.getInstance().getAccout().getUsername()
				, ImCore.getInstance().getAccout().getPassword());
		mPersonController = new PersonController();
		mFCHelper = new FCHelper(AccountData.getInstance().getUsername());
		new Thread(new Runnable() {
			@Override
			public void run() {
				initFromHistory();
			}
		}).start();
		mSIXmppReceiveMessageListener = new SIXmppReceiveMessageListener() {
			@Override
			public void receiveMessage(String onconidOrGroupid, SIXmppMessage message) {
				if(SIXmppMessage.ContentType.TYPE_SYSTEM == message.getContentType()){//系统消息处理
					if(message.getTextContent().startsWith("m1_chatroom_msg@@@lz-oncon@@@v1.0")){//群组管理消息
					}else if(message.getTextContent().startsWith("m1_extend_msg@@@lz-oncon@@@v1.0|||type=15|||subtype=1")){
						//新建群通知为本人在其他平台或终端的操作
					}else if(message.getTextContent().startsWith("m1_extend_msg@@@lz-oncon@@@v1.0|||type=15|||subtype=2")){
						//删除群消息
					}else if(message.getTextContent().startsWith("m1_extend_msg@@@lz-oncon@@@v1.0|||type=15|||subtype=3")){
						//群成员角色设置通知
					}else {
						addMessageData(onconidOrGroupid, message);
						IMNotification.getInstance().addNewMessageNotifaction(onconidOrGroupid, message);
						Intent intent = new Intent(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
						MyApplication.getInstance().sendBroadcast(intent);
					}
				}else if(message.getFrom().equals(AccountData.getInstance().getBindphonenumber())){
					//自己发的消息显示不提醒
					addMessageData(onconidOrGroupid, message);
				}
				else{
					addMessageData(onconidOrGroupid, message);
					IMNotification.getInstance().addNewMessageNotifaction(onconidOrGroupid, message);
					Intent intent = new Intent(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
					MyApplication.getInstance().sendBroadcast(intent);
				}
			}

			@Override
			public void viewMessage(String onconid, ArrayList<SIXmppMessage> messages) {
				//计算提醒图标数值,发出广播刷新会话界面图标
				IMNotification.getInstance().updNewMsgCount(onconid, IMDataDB.getInstance().qryNewMsgCount(onconid));
				try{
					if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
						for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
							onDataChangeListener.onDataChanged("");
						}
					}
				}catch(Exception e){}
				Intent intent = new Intent(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
				MyApplication.getInstance().sendBroadcast(intent);
			}
		};
		ImCore.getInstance().getConnection().addReceivedMessageListener(mSIXmppReceiveMessageListener);
		mSIXmppSendMessageListener = new SIXmppSendMessageListener() {
			@Override
			public void statusChanged(SIXmppMessage message) {
				String onconid = message.getTo();
				if (mDatas.containsKey(onconid) && mDatas.get(onconid) != null) {
					ArrayList<SIXmppMessage> msgs = mDatas.get(onconid).getMsgs();
					for (int index = 0; index < msgs.size(); index++) {
						SIXmppMessage msg = msgs.get(index);
						if (msg!=null&&msg.getId()!=null && msg.getId().equals(message.getId())) {
							msgs.set(index, message);
							break;
						}
					}
				}
				if (mSDatas.containsKey(onconid) && mSDatas.get(onconid) != null) {
					ArrayList<SIXmppMessage> msgs = mSDatas.get(onconid).getMsgs();
					for (int index = 0; index < msgs.size(); index++) {
						SIXmppMessage msg = msgs.get(index);
						if (msg!=null&&msg.getId()!=null && msg.getId().equals(message.getId())) {
							msgs.set(index, message);
							break;
						}
					}
					IMThreadData thread = mDatas.get(SIXmppThreadInfo.ID_STRANGER);
					if(thread != null){
						msgs = thread.getMsgs();
						for (int index = 0; index < msgs.size(); index++) {
							SIXmppMessage msg = msgs.get(index);
							if (msg!=null&&msg.getId()!=null && msg.getId().equals(message.getId())) {
								msgs.set(index, message);
								break;
							}
						}
					}
				}
			}

			@Override
			public void statusChanged(ArrayList<SIXmppMessage> arg0) {
				//目前批量状态刷新为已阅读状态,不需要更改会话部分的显示
			}
		};
		ImCore.getInstance().getConnection().addSendMessageListener(mSIXmppSendMessageListener);
		
		mCustomProtocolListener = new CustomProtocolListener(){
			
			@Override
			public void request_join_live(String account, String nick,
					String videoID) {
			}

			@Override
			public void response_join_live(String account, String nick,
					String videoID, String videoTitle, String accept) {
			}

			@Override
			public void private_bullet(String account, String msg, String videoID) {
			}

			@Override
			public void kick_off_video(String account, String nick,
					String videoID, String videoTitle) {
			}

			@Override
			public void mute_video(String account, String nick, String videoID,
					String videoTitle) {
			}

			@Override
			public void forbid_bullet(String videoID ,String type) {
			}

			@Override
			public void friend_status(String account, String type, String videoID) {
				if (!mDatas.containsKey(account)) {
					IMThreadData threadData = new IMThreadData(account, "", new ArrayList<SIXmppMessage>(),Type.P2P);
					addThreadData(account, threadData);// insert into database
				}
				IMThreadData d = mDatas.get(account);
				d.videoStatus = IMThreadData.VideoStatus.values()[Integer.parseInt(type)];
				d.videoID = videoID;
			}

			@Override
			public void invite_video(String account, String nick,
					String videoID, String videoTitle, String playurl) {
			}

			@Override
			public void entrust_invite_video(String videoID) {
			}

			@Override
			public void comment_notify(String commenVideoID, String commentid,
					String account, String nick, String imageurl) {
				//FIXME 朋友圈通知
				String id = commenVideoID + "_" + commentid;
				VideoData vd = new VideoData();
				vd.sub_type = "2";
				vd.videoID = commenVideoID;
				vd.post_id = commentid;
				vd.operator = account;
				vd.opnick = nick;
				vd.opimageurl = imageurl;
				vd.optime = Calendar.getInstance().getTimeInMillis() + "";
				mFCHelper.addOrUpdateFcNoti(id, vd);
				String key = id ;
				if (vd != null) {
					vd.setCacheKey(key);
					FriendCircleCacheUtil.saveObject(vd, key, MyApplication.getInstance());
				}
			}

			@Override
			public void focus_notify(int optType, int isSpecial,
					String account, String nick, String imageurl) {
				PersonData person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
				if(optType == 0){//0添加
					person.fansNum++;
				}else if(optType == 1){//1取消
					person.fansNum--;
				}
				mPersonController.updPerson(person.account, person);
			}
			
		};
		ImCore.getInstance().getConnection().addCustomProtocolListener(mCustomProtocolListener);
	}
	
	/** 
	* Description: 分页查询数据库消息记录
	* @param id
	* @param begin 开始索引
	* @param pagesize 每页记录条数
	* @return     
	* ArrayList<SIXmppMessage> 
	*/
	public ArrayList<SIXmppMessage> getMsgByLimit(String id,int begin,int pagesize, String order){
		
		return mHistoryManager.getMsgByLimit(id, begin, pagesize, order);
	}
	
	/** 
	* Description: 获取最新的一条消息
	* @param id
	* @return     
	* SIXmppMessage 
	*/
	public SIXmppMessage getLatestMsgById(String id){
		return mHistoryManager.getLatestMsgById(id);
	}
	
	/** 
	* Description: 获取消息的总数
	* @param id
	* @return     
	* int 
	*/
	public int getMsgCount(String id){
		return mHistoryManager.getMsgCount(id);
	}
	
	public int queryAllThreadsMessageCount() {
		return mHistoryManager.queryAllThreadsMessageCount();
	}
	
	/**
	 * 根据传入时间,删除全部会话里所有早于这个时间的消息
	 */
	public void deleteAllThreadsMessageByTime(String time) {
		for (Iterator<Entry<String, IMThreadData>> iter = mDatas.entrySet().iterator(); iter.hasNext();) {
			Entry<String, IMThreadData> element = (Map.Entry<String, IMThreadData>) iter.next();
			Object imkey = element.getKey();
			Object IMObj = element.getValue();
			IMThreadData datas = (IMThreadData) IMObj;
			long lastMegTime = datas.getMsgs().get(0).getTime();
			if (datas!= null && lastMegTime < Long.valueOf(time)) {
				iter.remove();
				IMNotification.getInstance().removeNewMessageNotication((String)imkey);
			}
		}
		for (Iterator<Entry<String, IMThreadData>> iter = mSDatas.entrySet().iterator(); iter.hasNext();) {
			Entry<String, IMThreadData> element = (Map.Entry<String, IMThreadData>) iter.next();
			Object imkey = element.getKey();
			Object IMObj = element.getValue();
			IMThreadData datas = (IMThreadData) IMObj;
			long lastMegTime = datas.getMsgs().get(0).getTime();
			if (datas!= null && lastMegTime < Long.valueOf(time)) {
				iter.remove();
				IMNotification.getInstance().removeNewMessageNotication((String)imkey);
			}
		}
		mHistoryManager.deleteAllThreadsMessageByTime(time);

		initIndexs();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged("");
				}
			}
		}catch(Exception e){}
	}
	
	/** 
	* Description: 根据id和包id删除某一条消息
	* @param msgId
	* @param paketId     
	* void 
	*/
	public void deleteMessageById(String onconId,String paketId){
		mHistoryManager.deleteMessageById(onconId, paketId);
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged(onconId);
				}
			}
		}catch(Exception e){}
	}
	//获取所有对话的个数包括个人对个人和圈聊
	public ArrayList<SIXmppThreadInfo> getAllChats(){
		ArrayList<SIXmppThreadInfo> chats = mHistoryManager.getUsernames(0, -1);
		ArrayList<SIXmppThreadInfo> groupChats = new ArrayList<SIXmppThreadInfo>();
		if(chats != null && chats.size()>0){
			for(SIXmppThreadInfo gc:chats){
				if(gc.getType() == SIXmppThreadInfo.Type.GROUP)
				groupChats.add(gc);
			}
		}
		return groupChats;
	}
	
	synchronized private void initFromHistory() {
		//FIXME
		ArrayList<SIXmppThreadInfo> onconids = mHistoryManager.getUsernames(0, -1);
		if (onconids == null)
			return;
		mHistoryManager.updateAllThreadVideoStatus2Zero();
		for(SIXmppThreadInfo threadInfo:onconids){
			IMThreadData.Type type = threadInfo.getType()==SIXmppThreadInfo.Type.P2P ? IMThreadData.Type.P2P 
					: threadInfo.getType()==SIXmppThreadInfo.Type.BATCH ? IMThreadData.Type.BATCH : IMThreadData.Type.GROUP;
			IMThreadData d = null;
			if(SIXmppThreadInfo.NOT_STRANGER.equals(threadInfo.isstranger)){
				d = mDatas.get(threadInfo.getUsername());
			}else{
				d = mSDatas.get(threadInfo.getUsername());
			}
			if(d == null){
				d = new IMThreadData(threadInfo.getUsername(), threadInfo.getNickname(), new ArrayList<SIXmppMessage>(), type);//还缺少圈子组信息初始化
				if(SIXmppThreadInfo.NOT_STRANGER.equals(threadInfo.isstranger)){
					mDatas.put(threadInfo.getUsername(), d);
				}else{
					mSDatas.put(threadInfo.getUsername(), d);
				}
			}else{
				d.setNickName(threadInfo.getNickname());
			}
			d.videoStatus = IMThreadData.VideoStatus.values()[Integer.parseInt(threadInfo.videostatus)];
			d.videoID = threadInfo.videoid;
		}
		// 添加测试账号
		ArrayList<String> tests = new ArrayList<String>();
		tests.add(Constants.NO_900);
		tests.add(Constants.NO_901);
		/*tests.add("15910514083");
		tests.add("18677929252");
		tests.add("18600918026");*/
		for(String t:tests){
			if(!mDatas.containsKey(t)){
				IMThreadData d = new IMThreadData(t, t, new ArrayList<SIXmppMessage>(), IMThreadData.Type.P2P);
				mDatas.put(t, d);
			}
		}
		mIndexs.clear();
		mIndexs.addAll(mDatas.keySet());
		//获取消息数量
		new Thread(){
			public void run(){
				try{
					ArrayList<String> idxs = new ArrayList<String>();
					idxs.addAll(mIndexs);
					for(String idx:idxs){
						if(SIXmppThreadInfo.ID_STRANGER.equals(idx)){
							continue;
						}
						IMThreadData data = mDatas.get(idx);
						if(data != null){
							String id = data.getId();
							SIXmppMessage msg = mHistoryManager.getLatestMsgById(id);
							if(msg == null){
								continue;
							}
							if (msg.getStatus() == SendStatus.STATUS_DRAFT) {
								msg.setStatus(SendStatus.STATUS_ERROR);
							}
							data.getMsgs().clear();
							data.getMsgs().add(msg);
						}
					}
					idxs.clear();
					idxs.addAll(mSIndexs);
					for(String idx:idxs){
						IMThreadData data = mSDatas.get(idx);
						if(data != null){
							String id = data.getId();
							SIXmppMessage msg = mHistoryManager.getLatestMsgById(id);
							if(msg == null){
								continue;
							}
							if (msg.getStatus() == SendStatus.STATUS_DRAFT) {
								msg.setStatus(SendStatus.STATUS_ERROR);
							}
							data.getMsgs().clear();
							data.getMsgs().add(msg);
						}
					}
					if(mSIndexs.size() > 0){
						sortSIndexs();
						IMThreadData d = mDatas.get(SIXmppThreadInfo.ID_STRANGER);
						if(d == null){
							d = new IMThreadData(SIXmppThreadInfo.ID_STRANGER, SIXmppThreadInfo.ID_STRANGER, new ArrayList<SIXmppMessage>(), IMThreadData.Type.P2P);
							mDatas.put(SIXmppThreadInfo.ID_STRANGER, d);
							mIndexs.add(SIXmppThreadInfo.ID_STRANGER);
						}
						d.getMsgs().clear();
						for(String idx:idxs){
							IMThreadData data = mSDatas.get(idx);
							if(data.getMsgs().size() > 0){
								d.getMsgs().add(data.getMsgs().get(0));
								break;
							}
						}
					}else{
						mDatas.remove(SIXmppThreadInfo.ID_STRANGER);
						mIndexs.remove(SIXmppThreadInfo.ID_STRANGER);
					}
					sortIndexs();
				}catch(Exception e){
					Log.e(e.getMessage(), e);
				}finally{
					Log.e(Constants.LOG_TAG, "initFromHistory:" + mIndexs.size() + "---------------------");
					try{
						if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
							for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
								onDataChangeListener.onDataChanged("");
							}
						}
					}catch(Exception e){}
				}
			}
		}.start();
		//更新消息提醒数量
		new Thread(){
			public void run(){
				int count = 0;
				try{
					ArrayList<String> idxs = new ArrayList<String>();
					idxs.addAll(mIndexs);
					for(String idx:idxs){
						IMThreadData data = mDatas.get(idx);
						if(data != null){
							if(!SIXmppThreadInfo.ID_STRANGER.equals(idx)){
								String id = data.getId();
								int ic = mHistoryManager.qryNewMsgCount(id);
								count += ic;
								IMNotification.getInstance().updNewMsgCount(id, ic);
							}else{
								ArrayList<String> sidxs = new ArrayList<String>();
								sidxs.addAll(mSIndexs);
								int scount = 0;
								for(String sidx:sidxs){
									IMThreadData sdata = mSDatas.get(sidx);
									if(sdata != null){
										String id = data.getId();
										int ic = mHistoryManager.qryNewMsgCount(id);
										scount += ic;
										IMNotification.getInstance().updNewMsgCount(id, ic);
									}
								}
								IMNotification.getInstance().updNewMsgCount(SIXmppThreadInfo.ID_STRANGER, scount);
								count += scount;
							}
						}
					}
					
				}catch(Exception e){
					Log.e(e.getMessage(), e);
				}finally{
					Log.e(Constants.LOG_TAG, "initFromHistory2:" + mIndexs.size() + "---------------------");
					if(count > 0){
						try{
							if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
								for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
									onDataChangeListener.onDataChanged("");
								}
							}
						}catch(Exception e){}
					}
					//广播通知刷新tab的未读消息
					Intent intent = new Intent(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
					MyApplication.getInstance().sendBroadcast(intent);
				}
				
			}
		}.start();
	}

	public Map<String, IMThreadData> getDatas() {
		return mDatas;
	}
	
	public Map<String, IMThreadData> getSDatas() {
		return mSDatas;
	}

	synchronized public void addMessageData(String onconid, SIXmppMessage data) {
		if (onconid == null || data == null) {
			return;
		}
		if(mPersonController.isFriend(onconid)){
			if (!mDatas.containsKey(onconid)) {
				IMThreadData threadData = new IMThreadData(onconid, StringUtils.repNull(data.getNickname()), new ArrayList<SIXmppMessage>(),Type.P2P);
				addThreadData(onconid, threadData);// insert into database
			}
			if (mDatas.containsKey(onconid)) {
				mDatas.get(onconid).getMsgs().clear();
				SIXmppMessage dataTemp = this.getLatestMsgById(onconid);
				if(dataTemp == null){
					mDatas.get(onconid).getMsgs().add(data);
				}else if(dataTemp.getTime() <= data.getTime()){
					mDatas.get(onconid).getMsgs().add(data);
				}else{
					mDatas.get(onconid).getMsgs().add(dataTemp);
				}
			}
		}else{
			if (!mSDatas.containsKey(onconid)) {
				IMThreadData threadData = new IMThreadData(onconid, StringUtils.repNull(data.getNickname()), new ArrayList<SIXmppMessage>(),Type.P2P);
				addSThreadData(onconid, threadData);// insert into database
			}
			if(!mDatas.containsKey(SIXmppThreadInfo.ID_STRANGER)){
				IMThreadData stranger = new IMThreadData(SIXmppThreadInfo.ID_STRANGER, SIXmppThreadInfo.ID_STRANGER, new ArrayList<SIXmppMessage>(),Type.P2P);
				addThreadData(SIXmppThreadInfo.ID_STRANGER, stranger);
			}
			if (mSDatas.containsKey(onconid)) {
				mSDatas.get(onconid).getMsgs().clear();
				mDatas.get(SIXmppThreadInfo.ID_STRANGER).getMsgs().clear();
				SIXmppMessage dataTemp = this.getLatestMsgById(onconid);
				if(dataTemp == null){
					mSDatas.get(onconid).getMsgs().add(data);
					mDatas.get(SIXmppThreadInfo.ID_STRANGER).getMsgs().add(data);
				}else if(dataTemp.getTime() <= data.getTime()){
					mSDatas.get(onconid).getMsgs().add(data);
					mDatas.get(SIXmppThreadInfo.ID_STRANGER).getMsgs().add(data);
				}else{
					mSDatas.get(onconid).getMsgs().add(dataTemp);
					mDatas.get(SIXmppThreadInfo.ID_STRANGER).getMsgs().add(dataTemp);
				}
			}
		}
		sort();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged(onconid);
				}
			}
		}catch(Exception e){}
		return;
	}
	
	synchronized public void addThreadData(String id, IMThreadData data) {
		if (id == null || id.equals("") || data == null) {
			return;
		}
		if(mDatas.containsKey(id)){
			return;
		}
		mDatas.put(id, data);
		mIndexs.add(id);
		sort();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged(id);
				}
			}
		}catch(Exception e){}
	}
	
	synchronized public void addSThreadData(String id, IMThreadData data) {
		if (id == null || id.equals("") || data == null) {
			return;
		}
		if(mSDatas.containsKey(id)){
			return;
		}
		mSDatas.put(id, data);
		mSIndexs.add(id);
		sort();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged(id);
				}
			}
		}catch(Exception e){}
	}
	
	synchronized public void moveSThread2Thread(String id){
		if (TextUtils.isEmpty(id) || !mSDatas.containsKey(id)) {
			return;
		}
		IMThreadData thread = mSDatas.get(id);
		mSDatas.remove(id);
		mSIndexs.remove(id);
		mDatas.put(id, thread);
		if(!mIndexs.contains(id))
			mIndexs.add(id);
		IMDataDB.getInstance().updateThreadIsStranger(id, "0");
		setStrangerLastestMsg();
		sort();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged(id);
				}
			}
		}catch(Exception e){}
	}
	
	synchronized public void moveThread2SThread(String id){
		if (TextUtils.isEmpty(id) || !mDatas.containsKey(id)) {
			return;
		}
		IMThreadData thread = mDatas.get(id);
		mDatas.remove(id);
		mIndexs.remove(id);
		mSDatas.put(id, thread);
		if(!mSIndexs.contains(id))
			mSIndexs.add(id);
		IMDataDB.getInstance().updateThreadIsStranger(id, "1");
		if(!mDatas.containsKey(SIXmppThreadInfo.ID_STRANGER)){
			IMThreadData stranger = new IMThreadData(SIXmppThreadInfo.ID_STRANGER, SIXmppThreadInfo.ID_STRANGER, new ArrayList<SIXmppMessage>(),Type.P2P);
			addThreadData(SIXmppThreadInfo.ID_STRANGER, stranger);
		}
		setStrangerLastestMsg();
		sort();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged(id);
				}
			}
		}catch(Exception e){}
	}
	
	private void setStrangerLastestMsg(){
		SIXmppMessage msg = null;
		for(String id:mSIndexs){
			IMThreadData thread = mSDatas.get(id);
			if(thread != null && thread.getMsgs().size() > 0){
				SIXmppMessage temp = thread.getMsgs().get(0);
				if(msg == null){
					msg = temp;
				}else if(temp.getTime() > msg.getTime()){
					msg = temp;
				}
			}
		}
		if(msg == null){
			if(mDatas.containsKey(SIXmppThreadInfo.ID_STRANGER))
				mDatas.get(SIXmppThreadInfo.ID_STRANGER).getMsgs().clear();
			return;
		}
		if(!mDatas.containsKey(SIXmppThreadInfo.ID_STRANGER)){
			IMThreadData stranger = new IMThreadData(SIXmppThreadInfo.ID_STRANGER, SIXmppThreadInfo.ID_STRANGER, new ArrayList<SIXmppMessage>(),Type.P2P);
			addThreadData(SIXmppThreadInfo.ID_STRANGER, stranger);
		}
		mDatas.get(SIXmppThreadInfo.ID_STRANGER).getMsgs().add(msg);
	}

	synchronized public void updateMessageData(String onconid, SIXmppMessage data) {
		if (onconid == null || data == null) {
			return;
		}
		if(SIXmppThreadInfo.ID_STRANGER.equals(onconid)){
			if (!mSDatas.containsKey(onconid)) {
				addMessageData(onconid, data);
			} else {
				ArrayList<SIXmppMessage> msgs = mSDatas.get(onconid).getMsgs();
				for (int index = 0; index < msgs.size(); index++) {
					SIXmppMessage msg = msgs.get(index);
					if (msg.getId()!=null && msg.getId().equals(data.getId())) {
						msgs.set(index, data);
						return;
					}
				}
			}
		}else{
			if (!mDatas.containsKey(onconid)) {
				addMessageData(onconid, data);
			} else {
				ArrayList<SIXmppMessage> msgs = mDatas.get(onconid).getMsgs();
				for (int index = 0; index < msgs.size(); index++) {
					SIXmppMessage msg = msgs.get(index);
					if (msg.getId()!=null && msg.getId().equals(data.getId())) {
						msgs.set(index, data);
						return;
					}
				}
			}
		}
	}

	public ArrayList<String> getIndexs() {
		return mIndexs;
	}
	
	public ArrayList<String> getSIndexs() {
		return mSIndexs;
	}

	synchronized public void initIndexs() {
		mIndexs.clear();
		mIndexs.addAll(mDatas.keySet());
		mSIndexs.clear();
		mSIndexs.addAll(mSDatas.keySet());
		sort();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged("");
				}
			}
		}catch(Exception e){}
	}
	
	synchronized private void sort(){
		try {
			Collections.sort(mIndexs, new CompareIMThread());
			Collections.sort(mSIndexs, new CompareIMSThread());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	synchronized private void sortIndexs(){
		try {
			Collections.sort(mIndexs, new CompareIMThread());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	synchronized private void sortSIndexs(){
		try {
			Collections.sort(mSIndexs, new CompareIMSThread());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	synchronized public void deleteThreadData(String onconid) {
		if (onconid != null) {
			if(mDatas.containsKey(onconid)){
				mDatas.remove(onconid);
			}
			if(mSDatas.containsKey(onconid)){
				mSDatas.remove(onconid);
			}
			// delete from database
			mHistoryManager.deleteAllMessageByUsername(onconid);

			initIndexs();
		}
	}

	synchronized public void deleteThreadDataAll() {
		mDatas.clear();
		mSDatas.clear();
		// delete from database
		mHistoryManager.deleteAllMessage();
		initIndexs();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged("");
				}
			}
		}catch(Exception e){}
	}
	
	synchronized public void deleteAllThreadsExceptMsgs() {
		mDatas.clear();
		mSDatas.clear();
		// delete from database
		mHistoryManager.deleteAllThreadsExceptMsgs();
		initIndexs();
		try{
			if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
				for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
					onDataChangeListener.onDataChanged("");
				}
			}
		}catch(Exception e){}
	}

	synchronized public void deleteMessageData(String onconid, String packetId) {
		if (onconid != null) {
			mHistoryManager.deleteMessageById(onconid, packetId);
			if(mDatas.containsKey(onconid)){
				mDatas.get(onconid).getMsgs().clear();
				SIXmppMessage lastmsg = this.getLatestMsgById(onconid);
				if(lastmsg != null){
					mDatas.get(onconid).getMsgs().add(lastmsg);
				}
			}
			if(mSDatas.containsKey(onconid)){
				mSDatas.get(onconid).getMsgs().clear();
				SIXmppMessage lastmsg = this.getLatestMsgById(onconid);
				if(lastmsg != null){
					mSDatas.get(onconid).getMsgs().add(lastmsg);
				}
			}
			try{
				if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
					for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
						onDataChangeListener.onDataChanged(onconid);
					}
				}
			}catch(Exception e){}
			try{
				if(onMsgDelListeners != null && onMsgDelListeners.size() > 0){
					for(OnMsgDelListener onMsgDelListener: onMsgDelListeners){
						onMsgDelListener.delMsg(onconid, packetId);
					}
				}
			}catch(Exception e){}
			initIndexs();
		}
	}

	synchronized public void deleteMessageData(String onconid) {
		if (onconid != null) {
			// delete data from database
			mHistoryManager.deleteAllMessageByUsername(onconid);
			if(mDatas.containsKey(onconid)){
				mDatas.get(onconid).getMsgs().clear();
			}
			if(mSDatas.containsKey(onconid)){
				mSDatas.get(onconid).getMsgs().clear();
			}
			try{
				if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
					for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
						onDataChangeListener.onDataChanged(onconid);
					}
				}
			}catch(Exception e){}
			try{
				if(onMsgDelListeners != null && onMsgDelListeners.size() > 0){
					for(OnMsgDelListener onMsgDelListener: onMsgDelListeners){
						onMsgDelListener.delMsgs(onconid);
					}
				}
			}catch(Exception e){}
			initIndexs();
		}
	}

	synchronized public void clear() {
		mIndexs.clear();
		mDatas.clear();
		mSIndexs.clear();
		mSDatas.clear();
		if(ImCore.isInstanciated()){
			ImCore.getInstance().getConnection().removeReceivedMessageListener(mSIXmppReceiveMessageListener);
			ImCore.getInstance().getConnection().removeSendMessageListener(mSIXmppSendMessageListener);
			ImCore.getInstance().getConnection().removeCustomProtocolListener(mCustomProtocolListener);
		}
		instance = null;
	}
	
	public interface OnDataChangeListener{
		public void onDataChanged(String onconId);
	}
	
	public void addOnDataChangeListener(OnDataChangeListener OnDataChangeListener){
		if(onDataChangeListeners == null){
			onDataChangeListeners = new ArrayList<OnDataChangeListener>();
		}
		onDataChangeListeners.add(OnDataChangeListener);
	}
	
	public void removeOnDataChangeListener(OnDataChangeListener OnDataChangeListener){
		if(onDataChangeListeners == null){
			onDataChangeListeners = new ArrayList<OnDataChangeListener>();
		}
		onDataChangeListeners.remove(OnDataChangeListener);
	}
	
	public List<OnDataChangeListener> getOnDataChangeListener(){
		if(onDataChangeListeners == null){
			onDataChangeListeners = new ArrayList<OnDataChangeListener>();
		}
		return onDataChangeListeners;
	}
	
	public interface OnMsgDelListener{
		public void delMsgs(String onconId);
		public void delMsg(String onconId, String packedId);
	}
	
	public void addOnMsgDelListener(OnMsgDelListener OnMsgDelListener){
		if(onMsgDelListeners == null){
			onMsgDelListeners = new ArrayList<OnMsgDelListener>();
		}
		onMsgDelListeners.add(OnMsgDelListener);
	}
	
	public void removeOnMsgDelListener(OnMsgDelListener OnMsgDelListener){
		if(onMsgDelListeners == null){
			onMsgDelListeners = new ArrayList<OnMsgDelListener>();
		}
		onMsgDelListeners.remove(OnMsgDelListener);
	}
	
	synchronized public void updateMessageThumbnailPath(String onconid, String packetid, String thumbnailPath) {
		mHistoryManager.updateMessageThumbnailPath(onconid, packetid, thumbnailPath);
	}
	
	public boolean hasSetTopChat(String onconid){
		boolean hasSetTopChat = false;
		SIXmppP2PInfo p2p = this.p2p_query(onconid);
		if(p2p != null && "1".equalsIgnoreCase(p2p.getTop())){
			hasSetTopChat = true;
		}
		return hasSetTopChat;
	}
	
	synchronized public void setTopChat(String onconid, String top, boolean isGroup){
		SIXmppP2PInfo p2p = p2p_query(onconid);
		if(p2p != null){
			p2p.setTop(top);
			initIndexs();
			try{
				if(onDataChangeListeners != null && onDataChangeListeners.size() > 0){
					for(OnDataChangeListener onDataChangeListener: onDataChangeListeners){
						onDataChangeListener.onDataChanged(onconid);
					}
				}
			}catch(Exception e){}
		}
	}
	
	public SIXmppP2PInfo p2p_query(String onconid){
		return mHistoryManager.p2p_query(onconid);
	}
	
	public boolean setP2PAttributes(String onconid, String key,String value){
		return mHistoryManager.p2p_setAttributes(onconid, key, value);
	}
	
	public SIXmppMessage getMsgById(String onconid, String packetid){
		return mHistoryManager.getMessageById(onconid, packetid);
	}
	
	public ArrayList<SIXmppMessage> queryAllImageMsgOfThread(String onconId){
		return mHistoryManager.queryAllImageMsgOfThread(onconId);
	}
}
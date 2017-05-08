package com.lz.oncon.app.im.data;

import java.util.List;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.lz.oncon.api.CustomProtocolDealerManager;
import com.lz.oncon.api.SIXmppAccout;
import com.lz.oncon.api.SIXmppChatManager;
import com.lz.oncon.api.SIXmppConnection;
import com.lz.oncon.api.SIXmppConnectionListener;
import com.lz.oncon.api.SIXmppHistoryManager;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppReceiveMessageListener;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.SIXmppMessage.Device;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.api.SIXmppThreadInfo.Type;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class ImCore {

	private SIXmppAccout mAccout;// 当前用户信息
	private SIXmppConnection mConnection;// 连接管理器

	private int connectStatus;// 当前连接状态（分为失败，连接中，成功）
	private static ImCore instance;

	private AlarmManager doHeartAlarm; // 心跳定时器
	private PendingIntent doHeartSender;

	public static boolean isInstanciated() {
		return instance != null;
	}

	public synchronized static ImCore getInstance() {
		if (instance == null) {
			instance = new ImCore();
		}
		return instance;
	}
	
	public synchronized static ImCore clearImCore() {
		if (isInstanciated()) {
			instance = null;
			return instance;
		}
		return instance;
	}

	private ImCore() {
		mConnection = new SIXmppConnection(MyApplication.getInstance().getApplicationContext());

		// 添加一个连接监听器，在连接超时可以进行重连
		mConnection.addConnectionListener(new SIXmppConnectionListener() {

			@Override
			public void loginStatusChanged(int status) {
				connectStatus = status;
			}
		});

		mAccout = new SIXmppAccout();
		setAccout();

		doHeartAlarm = (AlarmManager) MyApplication.getInstance().getSystemService(Service.ALARM_SERVICE);
		doHeartSender = PendingIntent.getBroadcast(MyApplication.getInstance(), 0, new Intent(IMNotiReceiver.ONCON_IM_HEARTBEAT), 0);
	}

	public SIXmppConnection getConnection() {
		return mConnection;
	}

	public SIXmppAccout getAccout() {
		return mAccout;
	}

	public void setAccout() {
		mAccout.setAccoutInfo(AccountData.getInstance().getBindphonenumber(),
				AccountData.getInstance().getPassword(), IMUtil.genIMResource());
		mAccout.auth = true;
	}

	public SIXmppChatManager getChatManager() {
		return mConnection.getChatManager();
	}
	
	public CustomProtocolDealerManager getCustomProtocolDealerManager() {
		return mConnection.getCustomProtocolDealerManager();
	}

	public void login() {
		setAccout();
		mConnection.login(mAccout);
		doHeartAlarm.cancel(doHeartSender);
		doHeartAlarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				com.lz.oncon.api.core.im.data.Constants.HEARTBEAT_INTERVAL, doHeartSender);
	}

	public void logout() {
		IMNotification.getInstance().clear();
		ImData.getInstance().clear();
		IMMessageWriteData.getInstance().clearMessags();
		if (doHeartAlarm != null) {
			doHeartAlarm.cancel(doHeartSender);
		}
		mConnection.logout();
	}

	public void addSystemMessage(String onconid, String messageTextContent, SourceType sourceType, long time) {
		addSystemMessage(onconid, messageTextContent, sourceType, time, ContentType.TYPE_TEXT);
	}

	public void addSystemMessage(String onconid, String messageTextContent, SourceType sourceType, long time, ContentType contentType) {
		SIXmppMessage message = new SIXmppMessage();
		message.setDevice(Device.DEVICE_ANDROID);
		message.setTo(com.lz.oncon.data.AccountData.getInstance().getBindphonenumber());
		message.setId(UUID.randomUUID().toString());
		message.setSourceType(sourceType);
		message.setStatus(SendStatus.STATUS_ARRIVED);
		message.setTime(time);
		message.setFrom(onconid);
		message.setContentType(contentType);
		message.setTextContent(messageTextContent);

		SIXmppHistoryManager historyManager = new SIXmppHistoryManager(MyApplication.getInstance().getApplicationContext(), AccountData.getInstance()
				.getBindphonenumber(), AccountData.getInstance().getPassword());
		historyManager.addMessage(onconid, onconid, message, Type.P2P);

		List<SIXmppReceiveMessageListener> receiveMessageListeners = ImCore.getInstance().getConnection().getReceiveMessageListener();
		if (receiveMessageListeners != null && receiveMessageListeners.size() > 0) {
			for (SIXmppReceiveMessageListener receiveMessageListener : receiveMessageListeners) {
				receiveMessageListener.receiveMessage(onconid, message);
			}
		}
	}
}

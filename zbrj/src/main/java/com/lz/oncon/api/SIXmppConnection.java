package com.lz.oncon.api;

import java.util.List;

import android.content.Context;

import com.lz.oncon.api.SIXmppChatManager;
import com.lz.oncon.api.core.im.core.OnconIMCore;

public class SIXmppConnection {
		
	private SIXmppChatManager mChatManager;
	private CustomProtocolDealerManager mCustomProtocolDealerManager;
	public CustomProtocolDealerManager getCustomProtocolDealerManager() {
		return mCustomProtocolDealerManager;
	}

	public void setCustomProtocolDealerManager(
			CustomProtocolDealerManager mCustomProtocolDealerManager) {
		this.mCustomProtocolDealerManager = mCustomProtocolDealerManager;
	}

	private int mConnectionStatus;
	
	public SIXmppConnection(Context context) {
		OnconIMCore.getInstance().initEM(context);
		mChatManager = new SIXmppChatManager();
		mCustomProtocolDealerManager = new CustomProtocolDealerManager();
		mConnectionStatus = SIXmppConnectionListener.FAILED;
		addConnectionListener(new SIXmppConnectionListener() {
			@Override
			public void loginStatusChanged(int status) {
				mConnectionStatus = status;
				OnconIMCore.getInstance().setmConnectionStatus(status);
			}
		});
	}
	
	public int getmConnectionStatus() {
		return mConnectionStatus;
	}

	/**
	 * 添加连接的监听器，监听各种连接状态
	 * @param listener
	 */
	public void addConnectionListener(SIXmppConnectionListener listener){
		OnconIMCore.getInstance().addConnectionListener(listener);
	}
	public void removeConnectionListener(SIXmppConnectionListener listener){
		OnconIMCore.getInstance().removeConnectionListener(listener);
	}
	public List<SIXmppConnectionListener> getConnectionListeners(){
		return OnconIMCore.getInstance().getmConnectionListeners();
	}
	
	/**
	 * 获取即时消息连接状态
	 */
	public int getConnectionStatus(){
		return mConnectionStatus;
	}
	
	/**
	 * 登陆
	 * @param accout
	 */
	public void login(SIXmppAccout accout){
		if(accout.getUsername()!=null && accout.getPassword()!=null && accout.getResouce()!=null){
			OnconIMCore.getInstance().login(accout.getUsername(), accout.getPassword(), accout.getResouce(), accout.auth);
		}
	}
	
	/**
	 * 注销
	 */
	public void logout(){
		OnconIMCore.getInstance().logout();
	}
	
	/**
	 * 获取对话管理器（参考SIXmppChatManager）
	 * @return
	 */
	public SIXmppChatManager getChatManager(){
		return mChatManager;
	}
	
	/**
	 * 添加接收信息的监听器
	 * @param listener
	 */
	public void addReceivedMessageListener(SIXmppReceiveMessageListener listener){
		OnconIMCore.getInstance().addReceiverMessageListener(listener);
	}
	
	/**
	 * 删除接受信息的监听器
	 * @param listener
	 */
	public void removeReceivedMessageListener(SIXmppReceiveMessageListener listener){
		OnconIMCore.getInstance().removeReceiverMessageListener(listener);
	}
	
	public List<SIXmppReceiveMessageListener> getReceiveMessageListener(){
		return OnconIMCore.getInstance().getmReceiveMessageListeners();
	}
	
	/**
	 * 添加发送状态的监听器
	 * @param listener
	 */
	public void addSendMessageListener(SIXmppSendMessageListener listener){
		OnconIMCore.getInstance().addSendMessageListener(listener);
	}
	
	/**
	 * 删除发送状态的监听器
	 * @param listener
	 */
	public void removeSendMessageListener(SIXmppSendMessageListener listener){
		OnconIMCore.getInstance().removeSendMessageListener(listener);
	}
	
	public int getmConnectionErrorCode(){
		return OnconIMCore.getInstance().getmConnectionErrorCode();
	}
	
	public int getLoginErrorCode(){
		return OnconIMCore.getInstance().getLoginErrorCode();
	}
	
	public void sendReadMessage(String onconid, String packetid){
		OnconIMCore.getInstance().sendReadMessage(onconid, packetid);
	}
	
	/**
	 * 添加自定义协议监听器
	 */
	public void addCustomProtocolListener(CustomProtocolListener listener){
		OnconIMCore.getInstance().addCustomProtocolListener(listener);
	}
	
	/**
	 * 删除自定义协议监听器
	 */
	public void removeCustomProtocolListener(CustomProtocolListener listener){
		OnconIMCore.getInstance().removeCustomProtocolListener(listener);
	}
}
package com.lz.oncon.api;

import java.util.ArrayList;

/**
 * 发送状态变化时的回调
 * @author Administrator
 *
 */
public interface SIXmppSendMessageListener{
	/**
	 * 发送状态变更
	 * @param msg
	 */
	public void statusChanged(SIXmppMessage message);
	/**
	 * 发送状态变更
	 */
	public void statusChanged(ArrayList<SIXmppMessage> messages);
}
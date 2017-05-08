
package com.lz.oncon.api;

import java.util.ArrayList;

/**
 * 接收消息的回调
 * @author Administrator
 *
 */
public interface SIXmppReceiveMessageListener{
	/**
	 * 接收消息
	 * @param msg
	 */
	public void receiveMessage(String onconidOrGroupid, SIXmppMessage message);
	/**
	 * 已阅读消息
	 * @param onconid
	 * @param msgs
	 */
	public void viewMessage(String onconid, ArrayList<SIXmppMessage> messages);
}

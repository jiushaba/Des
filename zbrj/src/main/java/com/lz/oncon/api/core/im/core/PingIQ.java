package com.lz.oncon.api.core.im.core;

import org.jivesoftware.smack.packet.IQ;

public class PingIQ extends IQ{

	@Override
	public String getChildElementXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<ping xmlns='urn:xmpp:ping' />");
		return buffer.toString();
	}

}

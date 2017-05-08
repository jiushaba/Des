package com.lz.oncon.api.core.im.core;

import org.jivesoftware.smack.packet.IQ;

public class PongIQ extends IQ {

	public PongIQ(){
		this.setType(Type.RESULT);
	}
	@Override
	public String getChildElementXML() {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}

}

package com.lz.oncon.api.core.im.core;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class PingIQProvider implements IQProvider {

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {		
		return new PingIQ();
	}

}

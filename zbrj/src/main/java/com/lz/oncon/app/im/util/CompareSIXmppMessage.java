package com.lz.oncon.app.im.util;

import java.util.Comparator;

import com.lz.oncon.api.SIXmppMessage;

public class CompareSIXmppMessage implements Comparator<SIXmppMessage> {

	@Override
	public int compare(SIXmppMessage msg0, SIXmppMessage msg1) {
		try{
			if(msg0.getTime() == msg1.getTime()){
				return 0;
			}else if(msg0.getTime() > msg1.getTime()){
				return 1;
			}else{
				return -1;
			}
		}catch(Exception e){
			return -1;
		}
	}

}

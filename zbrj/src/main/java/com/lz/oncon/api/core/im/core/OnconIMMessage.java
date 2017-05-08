package com.lz.oncon.api.core.im.core;

import java.util.HashMap;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.ContentType;

public class OnconIMMessage {
	
	public static SIXmppMessage createSIXmppMessageByEMMessage(EMMessage message, SIXmppMessage.SourceType sourceType){
		SIXmppMessage msg = new SIXmppMessage();
		msg.setId(message.getMsgId());
		msg.setFrom(message.getFrom());
		msg.setTo(message.getTo());
		msg.setDevice(SIXmppMessage.Device.DEVICE_UNKNOWN);
		msg.setTime(message.getMsgTime());
		
		msg.setOnconActive("1");
		msg.setOnconArrived("0");
		
		parseMsgBody(msg, message);
		
		msg.setSourceType(sourceType);
		
		return msg;
	}
	
	public static void parseMsgBody(SIXmppMessage msg, EMMessage message){
		if(message.getType().ordinal() == EMMessage.Type.TXT.ordinal()){
			msg.setContentType(ContentType.TYPE_TEXT);
			TextMessageBody body = (TextMessageBody)message.getBody();
			msg.setTextContent(body.getMessage());
			if(msg.getTextContent().startsWith("@custom_protocol")){
				msg.setContentType(ContentType.TYPE_CUSTOM_PROTOCOL);
			}
		} else if(message.getType().ordinal() == EMMessage.Type.IMAGE.ordinal()){
			msg.setContentType(ContentType.TYPE_IMAGE);
			ImageMessageBody body = (ImageMessageBody)message.getBody();
			msg.setImageName(body.getFileName());
			msg.setImageWidth(body.getWidth());
			msg.setImageHeight(body.getHeight());
			msg.setImagePath(body.getLocalUrl());
			msg.setImageURL(body.getRemoteUrl());
			msg.setThumbnailURL(body.getThumbnailUrl());
		} else if(message.getType().ordinal() == EMMessage.Type.VOICE.ordinal()){
			msg.setContentType(ContentType.TYPE_AUDIO);
			VoiceMessageBody body = (VoiceMessageBody)message.getBody();
			msg.setAudioTimeLength(body.getLength());
			msg.setAudioName(body.getFileName());
			msg.setAudioPath(body.getLocalUrl());
			msg.setAudioURL(body.getRemoteUrl());
		} else if(message.getType().ordinal() == EMMessage.Type.VIDEO.ordinal()){
		} else if(message.getType().ordinal() == EMMessage.Type.LOCATION.ordinal()){
			msg.setContentType(ContentType.TYPE_LOC);
			LocationMessageBody body = (LocationMessageBody)message.getBody();
			msg.setTextContent("m1_extend_msg@@@lz-oncon@@@v1.0|||type=2|||coor=|||long="+body.getLongitude()
					+"|||lat="+body.getLatitude()+"|||loc="+body.getAddress());
		} else if(message.getType().ordinal() == EMMessage.Type.FILE.ordinal()){
			msg.setContentType(ContentType.TYPE_FILE);
		} else if(message.getType().ordinal() == EMMessage.Type.CMD.ordinal()){
			CmdMessageBody body = (CmdMessageBody)message.getBody();
			msg.setTextContent(body.action);
			if(msg.getTextContent().startsWith("@custom_protocol")){
				msg.setContentType(ContentType.TYPE_CUSTOM_PROTOCOL);
			}
		}
	}
	
	public static String genMsgBody(SIXmppMessage msg){
		String str = msg.getTextContent();
		switch(msg.getContentType()){
		case TYPE_IMAGE:
			str = getImageMsgBody(msg);
			break;
		case TYPE_AUDIO:
			str = getAudioMsgBody(msg);
			break;
		case TYPE_TALK_PIC:
			str = getTalkPicMsgBody(msg);
			break;
		case TYPE_SNAP_PIC:
			str = getSnapPicMsgBody(msg);
			break;
		case TYPE_FILE:
			str = getFileMsgBody(msg);
			break;
		default:
			break;
		}
		return str;
	}
	
	private static String getImageMsgBody(SIXmppMessage msg){
		StringBuffer sb = new StringBuffer("m1_file_msg@@@lz-oncon@@@v1.0");
		sb.append("|||fileid=" + msg.getImageFileId());
		sb.append("|||smallfileid=" + msg.getThumbnailFileId());
		sb.append("|||filename=" + msg.getImageName());
		sb.append("|||filesize=" + msg.getImageFileSize());
		sb.append("|||filetype=pic");
		return sb.toString();
	}
	
	private static String getAudioMsgBody(SIXmppMessage msg){
		StringBuffer sb = new StringBuffer("m1_file_msg@@@lz-oncon@@@v1.0");
		sb.append("|||fileid=" + msg.getAudioFileId());
		sb.append("|||filename=" + msg.getAudioName());
		sb.append("|||filesize=" + msg.getAudioFileSize());
		sb.append("|||filetype=call");
		return sb.toString();
	}
	
	private static String getTalkPicMsgBody(SIXmppMessage msg){
		StringBuffer sb = new StringBuffer("m1_extend_msg@@@lz-oncon@@@v1.0|||type=4|||");
		sb.append("|||b_url=" + msg.getImageFileId());
		sb.append("|||m_url=" + msg.getThumbnailFileId());
		sb.append("|||v_url=" + msg.getAudioFileId());
		return sb.toString();
	}
	
	private static String getSnapPicMsgBody(SIXmppMessage msg){
		StringBuffer sb = new StringBuffer("m1_extend_msg@@@lz-oncon@@@v1.0|||type=5");
		sb.append("|||i_url=" + msg.getImageFileId());
		sb.append("|||time=" + msg.getSnapTime());
		return sb.toString();
	}
	
	private static String getFileMsgBody(SIXmppMessage msg){
		StringBuffer sb = new StringBuffer();
		sb.append("m1_extend_msg@@@lz-oncon@@@v1.0|||type=8");
		sb.append("|||url=" + msg.getImageFileId());
		sb.append("|||name=" + msg.getImageName());
		sb.append("|||size=" + msg.getImageFileSize());
		return sb.toString();
	}
	
	public static HashMap<String, String> parseExtMsg(String msgTextContent){
		HashMap<String, String> extParams = new HashMap<String, String>();
		String[] elements = msgTextContent.split("\\|\\|\\|");
		if (elements != null && elements.length > 1) {
			for (int i = 1; i < elements.length; i++) {
				String element = elements[i];
				if(element.indexOf("=") > 0){
					String key = element.substring(0, element.indexOf("="));
					String value = "";
					if(element.indexOf("=") < element.length() - 1){
						value = element.substring(element.indexOf("=") + 1);
					}
					extParams.put(key, value);
				}
			}
		}
		return extParams;
	}
	
	public static HashMap<String, String> parseCustomProtocol(String msgTextContent){
		HashMap<String, String> extParams = new HashMap<String, String>();
		String[] elements = msgTextContent.substring(msgTextContent.indexOf("?") + 1).split("&");
		if (elements != null && elements.length >= 1) {
			for (int i = 0; i < elements.length; i++) {
				String element = elements[i];
				if(element.indexOf("=") > 0){
					String key = element.substring(0, element.indexOf("="));
					String value = "";
					if(element.indexOf("=") < element.length() - 1){
						value = element.substring(element.indexOf("=") + 1);
					}
					extParams.put(key, value);
				}
			}
		}
		return extParams;
	}
}
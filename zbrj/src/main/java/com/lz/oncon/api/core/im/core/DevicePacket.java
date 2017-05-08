package com.lz.oncon.api.core.im.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.lb.common.util.Log;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.Device;
import com.lz.oncon.api.core.im.data.Constants;


/**
 * 设备扩展
 * 
 * @author Administrator
 * 
 */
class DevicePacket implements PacketExtension {
	public static final int ANDROID = 1;
	public static final int IPHONE = 0;
	public static final int PC = 2;
	public static final int OTHER = 3;

	public int getDevicevalue() {

		return 0;
	}

	@Override
	public String toXML() {
		StringBuffer domBuffer = new StringBuffer();

		domBuffer.append('<').append(getElementName()).append(" xmlns=\"")
				.append("jabber:x:oncon_devicetype").append("\" value=\"")
				.append(String.valueOf(ANDROID)).append("\"></x>");

		return domBuffer.toString();
	}

	@Override
	public String getNamespace() {
		return "jabber:x:oncon_devicetype";
	}

	@Override
	public String getElementName() {
		return "x";
	}

	// get send Device type,by x
	public static String getDeviceByMessage(Message message) {
		PacketExtension xPe = message.getExtension("x",
				"jabber:x:oncon_devicetype");
		if (xPe != null) {
			try {
				Document dom = DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(new ByteArrayInputStream(xPe.toXML()
								.getBytes()));
				String deviceType = dom.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
				return deviceType;
			} catch (SAXException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			} catch (ParserConfigurationException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
		return null;
	}
	
	public static SIXmppMessage.Device getDeviceIdByMessage(Message message) {
		String device = getDeviceByMessage(message);
		if(device == null){
			return Device.DEVICE_UNKNOWN;
		}else if(device.equals("1")){
			return Device.DEVICE_ANDROID;
		}else if(device.equals("0")){
			return Device.DEVICE_IPHONE;
		}else if(device.equals("2")){
			return Device.DEVICE_WINDOWS;
		}else if(device.equals("3")){
			return Device.DEVICE_IPOD_TOUCH;
		}else if(device.equals("4")){
			return Device.DEVICE_IPAD;
		}else if(device.equals("5")){
			return Device.DEVICE_MAC;
		}else {
			return Device.DEVICE_UNKNOWN;
		}
	}
}

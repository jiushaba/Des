package com.lz.oncon.app.im.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.lb.common.util.DeviceUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.api.SIXmppThreadInfo.Type;
import com.lz.oncon.api.core.im.core.OnconIMMessage;
import com.lz.oncon.app.im.ui.IMMessageFormat;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;


public class IMUtil {
	
	private static Map<String, Integer> imagemap = new HashMap<String, Integer>();
	static final public String sFolder = ".";
	static final public String sEmpty = "";
	/*
	 * 判断手机是否打开了wifi
	 */
	public static boolean isOpenWIFI(Context context){
		boolean success = false; 
		 ConnectivityManager connManager = (ConnectivityManager) context  
                 .getSystemService(Context.CONNECTIVITY_SERVICE); 
		if(connManager != null){
			NetworkInfo ni = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if(ni != null){
				State state = ni.getState();  //获取WIFI状态
				if (State.CONNECTED == state) {  
					success = true;   
				} 
			}
		}
		return success;
	}
	/** 
	* Description: 判断手机是否有网络
	* @param context
	* @return     
	* boolean 
	*/
	public static boolean isNetworkConnected(Context context) {  
	     if (context != null) {  
	         ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                 .getSystemService(Context.CONNECTIVITY_SERVICE);  
	         NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	         if (mNetworkInfo != null) {  
	             return mNetworkInfo.isAvailable();  
	         }  
	     }  
	     return false;  
	 }
	
	public static String removeCCode(String str){
		if(IMConstants.COUNTRY_CODE_SCHINA.equals(AccountData.getInstance().getNationalNumber()) || IMConstants.COUNTRY_CODE_CHINA.equals(AccountData.getInstance().getNationalNumber())){
			if(str.startsWith(IMConstants.COUNTRY_CODE_CHINA)){
				return str.substring(IMConstants.COUNTRY_CODE_CHINA.length(),str.length());
			}else if(str.startsWith(IMConstants.COUNTRY_CODE_SCHINA)){
				return str.substring(IMConstants.COUNTRY_CODE_SCHINA.length(),str.length());
				
			}
		}
		return str;
	}
	
	public static String getHideMobile(String mobile){
		if(TextUtils.isEmpty(mobile)){
			return "";
		}
		if(mobile.length() <= 3){
			return mobile;
		}
		String str = mobile.substring(0, 3);
		for(int i=3;i<mobile.length();i++){
			str += "X";
		}
		return str;
	}
	
	/** 
	* 根据不同的国家账户，跳转到联系人详情界面时生成正确的JID
	*/
	public static String getInfosJid(String mobile){
		
		String m = mobile.replaceAll(" ", "");
		if(!IMConstants.COUNTRY_CODE_CHINA.equals(AccountData.getInstance().getNationalNumber())){
			if(mobile.startsWith("00") || mobile.startsWith("+")){
				
			}else{
				if((mobile.startsWith("+86")) || (mobile.startsWith("0086")) ){
				}else{
					m = IMConstants.COUNTRY_CODE_SCHINA + mobile;
				}
			}
		}
		return m;
	}
	/** 
	* Description: 根据不同的国家账户，创建聊天时生成正确的JID
	* @param mOnconId
	* @return     
	* String 
	*/
	public static String getCreateChatJID(String mOnconId){
		String toWho = null;
		//如果手机号前面带有中国的国家编码，那么发送消息目的地的参数是手机号码，否则是国家编码+手机号码
		mOnconId = mOnconId.replaceAll(" ", "");
		if(mOnconId.startsWith("+86")){
			toWho = mOnconId.substring("+86".length(),mOnconId.length());
		}else if(mOnconId.startsWith("0086")){
			toWho = mOnconId.substring("0086".length(),mOnconId.length());
		}else{
			if(IMConstants.COUNTRY_CODE_CHINA.equals(AccountData.getInstance().getNationalNumber())){
				toWho = mOnconId;
			}else{
				toWho = AccountData.getInstance().getNationalNumber()+mOnconId;
			}
		}
		return toWho;
	}
	
	
	
	public static String getStringByTime(long time){
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date(time);
			String text = dateFormat.format(date);
			if(text==null){
				text = "";
			}
			return text;
		} catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "";
	}
	
	public static String getMsgTime(long time){
		try{
			String text = "";
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(time);
			Calendar currentDate = Calendar.getInstance();
			
			if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
					date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
					date.get(Calendar.DATE) == currentDate.get(Calendar.DATE)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("a HH:mm");
				text = dateFormat.format(date.getTime());
			}else if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				text = dateFormat.format(date.getTime());
			}else{
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				text = dateFormat.format(date.getTime());			
			}
			if(text==null){
				text = "";
			}
			return text;
		} catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "";
	}
	
	public static String getVideoTime(long time){
		try{
			String text = "";
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(time);
			Calendar currentDate = Calendar.getInstance();
			
			if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
					date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
					date.get(Calendar.DATE) == currentDate.get(Calendar.DATE)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
				text = dateFormat.format(date.getTime());
			}else if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				text = dateFormat.format(date.getTime());
			}else{
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				text = dateFormat.format(date.getTime());			
			}
			if(text==null){
				text = "";
			}
			return text;
		} catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "";
	}
	
	public static String getVideoTime2(long time){
		try{
			String text = "";
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(time);
			Calendar currentDate = Calendar.getInstance();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("d日 HH:mm");
			text = dateFormat.format(date.getTime());			
			if(text==null){
				text = "";
			}
			return text;
		} catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "";
	}
	
	public static String getVideoMonth(long time){
		try{
			String text = "";
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(time);
			Calendar currentDate = Calendar.getInstance();
			
			if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("M月");
				text = dateFormat.format(date.getTime());
			}else{
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月");
				text = dateFormat.format(date.getTime());			
			}
			if(text==null){
				text = "";
			}
			return text;
		} catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return "";
	}

	@SuppressLint("SimpleDateFormat")
	public static String getArtStringByTime(long time){
		String text = "";
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		Calendar currentDate = Calendar.getInstance();
		
		if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
				date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
				date.get(Calendar.DATE) == currentDate.get(Calendar.DATE)){
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			text = dateFormat.format(date.getTime());
		}else if(date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)){
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
			text = dateFormat.format(date.getTime());
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			text = dateFormat.format(date.getTime());			
		}
		
		
		if(text == null){
			text = "";
		}
		return text;
	}
	
	
	
	public static String getNewsPicName(String url){
		int index = url.lastIndexOf("/");
		String name = url.substring(index+1);
		return name;
	}
	
	public static String getKey(String s){
		int index = s.indexOf("=");
		if(index == -1) {
			return null;
		}
		return s.substring(0,index);
	}
	
	public static String getValue(String s){
		int index = s.indexOf("=");
		if(index == -1){
			return null;
		}
		String value = s.substring(index+1);
		if("null".equals(value)){
			return null;
		}
		return value;
	}
	
	public static String genIntercomConfNo(String no, String noType){
		if(Type.GROUP.toString().equals(noType)){
			return no;
		}else{
			if(AccountData.getInstance().getBindphonenumber().compareTo(no) >= 0){
				return no + "&" + AccountData.getInstance().getBindphonenumber();
			}else{
				return AccountData.getInstance().getBindphonenumber() + "&" + no;
			}
		}
	}
	
	public static String getMessageBrief(SIXmppMessage msg, PersonController mPersonController){
		String msgBrief = "";
		HashMap<String, String> params;
		switch (msg.getContentType()) {
			case TYPE_TEXT:
				msgBrief = Html.fromHtml(msg.getTextContent()).toString();
			break;
		case TYPE_IMAGE:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.image) + "]";
			break;
		case TYPE_AUDIO:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.record) + "]";
			break;
		case TYPE_LOC:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.location) + "]";
			break;
		case TYPE_DYN_EXP:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.dyn_exp) + "]";
			break;
		case TYPE_TALK_PIC:
			break;
		case TYPE_SNAP_PIC:
			break;
		case TYPE_APP_MSG:
			break;
		case TYPE_FILE:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.file_msg) + "]";
			break;
		case TYPE_SYSTEM:
			if("900".equals(msg.getFrom()) && msg.getTextContent() != null && msg.getTextContent().startsWith("m1_chatroom_msg@@@lz-oncon@@@v1.0")){//圈子管理消息
				
			}else if("900".equals(msg.getFrom()) && msg.getTextContent() != null && msg.getTextContent().startsWith("m1_extend_msg@@@lz-oncon@@@v1.0|||type=15|||subtype=1")){//新建圈子消息
				
			}else if("900".equals(msg.getFrom()) && msg.getTextContent() != null && msg.getTextContent().startsWith("m1_extend_msg@@@lz-oncon@@@v1.0|||type=15|||subtype=2")){//删除圈子消息
				
			}else if("900".equals(msg.getFrom()) && msg.getTextContent() != null && msg.getTextContent().startsWith("m1_extend_msg@@@lz-oncon@@@v1.0|||type=15|||subtype=3")){//设置管理员消息
				
			}else{
				msgBrief = msg.getTextContent() == null ? "" : msg.getTextContent();
			}
			break;
		case TYPE_GROUP_SYS_NOTI:
			msgBrief = parseGroupSysNoti(msg);
			break;
		case TYPE_HTML_TEXT:
			String msgTemp = msg.getTextContent().replaceAll("m1_extend_msg@@@lz-oncon@@@v1.0\\|\\|\\|type=14\\|\\|\\|html=", "");
			msgTemp = new String(Base64.decode(msgTemp.getBytes(),Base64.DEFAULT));
			msgBrief = Html.fromHtml(msgTemp).toString();
			break;
		case TYPE_PUBLICACCOUNT_NAMECARD:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.name_card) + "]";
			break;
		case TYPE_IMAGE_TEXT:
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.image_text) + "]";
			break;
		case TYPE_VIDEO_CONF:
			msgBrief = parseExtendMsg(msg);
			break;
		case TYPE_LINK_MSG:
			params = IMMessageFormat.parseExtMsg(msg.getTextContent());
			msgBrief = "[" + MyApplication.getInstance().getString(R.string.link_msg) + "]" + (params.containsKey("title") ? params.get("title") : "");
			break;
		case TYPE_CUSTOM_PROTOCOL:
			if(msg.getTextContent().indexOf("recommand_friend") > -1){
				params = OnconIMMessage.parseCustomProtocol(msg.getTextContent());
				String mobile = params.get("recommandAccount");
				if(msg.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
					msgBrief = MyApplication.getInstance().getString(R.string.recommand_friend_msg_fmt, mPersonController.findNameByMobile(msg.getFrom()), mPersonController.findNameByMobile(mobile));
				}else{
					msgBrief = MyApplication.getInstance().getString(R.string.recommand_friend_msg_fmt2, mPersonController.findNameByMobile(msg.getTo()), mPersonController.findNameByMobile(mobile));
				}
			} else if(msg.getTextContent().indexOf("invite_video") > -1){
				params = OnconIMMessage.parseCustomProtocol(msg.getTextContent());
				if(msg.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
					msgBrief = MyApplication.getInstance().getString(R.string.invite_video_msg_fmt2, params.get("nick"), params.get("videoTitle"));
				}
			} else if(msg.getTextContent().indexOf("private_bullet") > -1){
				params = OnconIMMessage.parseCustomProtocol(msg.getTextContent());
				if(msg.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
					msgBrief = params.get("msg");
				}
			} else if(msg.getTextContent().indexOf("friend_status") > -1){
				params = OnconIMMessage.parseCustomProtocol(msg.getTextContent());
				if(msg.getSourceType().ordinal() == SourceType.RECEIVE_MESSAGE.ordinal()){
					String from = mPersonController.findNameByMobile(msg.getFrom());
					if("1".equals(params.get("type"))){
						msgBrief = MyApplication.getInstance().getString(R.string.friend_status_msg_fmt, from);
					}
				/*	else if("2".equals(params.get("type")) || "3".equals(params.get("type")))
						msgBrief = MyApplication.getInstance().getString(R.string.friend_status_msg_fmt2, from);*/
				}
			} else{
				msgBrief = IMMessageFormat.UNKNOWN_MSG;
			}
			break;
		default:
			msgBrief = IMMessageFormat.UNKNOWN_MSG;
			break;
		}
		return msgBrief;
	}
	
	public static int getImageId(String s){
		if(imagemap == null){
			return 0;
		} else if(imagemap.containsKey(s)){
			return imagemap.get(s);
		} else {
			return imagemap.get(sEmpty);
		}
	}
	
	public static boolean isKickMe(SIXmppMessage msg){
		boolean isKickMe = false;
		String memberJID = "", opt = "";
		HashMap<String, String> elements = IMMessageFormat.parseExtMsg(msg.getTextContent());
		if (elements != null && elements.size() > 0) {
			memberJID = elements.containsKey("memberJID") ? elements.get("memberJID") : "";
			opt = elements.containsKey("opt") ? elements.get("opt") : "";
		}
		try {
			String[] members = memberJID.split("&&");
			if (opt.equals("invite") && members != null && members.length > 0) {
			} else if (opt.equals("kick") && members != null && members.length > 0) {// 踢人
				for (int i=0;i<members.length;i++) {
					if (members[i].equals(AccountData.getInstance().getBindphonenumber())) {
						isKickMe = true;
					}
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return isKickMe;
	}
	
	public static String parseGroupSysNoti(SIXmppMessage msg){
		String msgStr = "";
		String groupName = "", joinerName = "", subtype = "", adminName = "";
		HashMap<String, String> elements = IMMessageFormat.parseExtMsg(msg.getTextContent());
		if (elements != null && elements.size() > 0) {
			groupName = elements.containsKey("groupName") ? elements.get("groupName") : "";
			joinerName = elements.containsKey("joinerName") ? elements.get("joinerName") : "";
			subtype = elements.containsKey("subtype") ? elements.get("subtype") : "";
			adminName = elements.containsKey("adminName") ? elements.get("adminName") : "";
		}
		try {
			if ("1".equals(subtype)) {
				msgStr = MyApplication.getInstance().getString(R.string.join_group_msg, joinerName, groupName);
			} else if ("2".equals(subtype)) {
				msgStr = MyApplication.getInstance().getString(R.string.audit_ok_msg, adminName);
			} else if ("3".equals(subtype)) {
				msgStr = MyApplication.getInstance().getString(R.string.audit_refuse_msg, adminName);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return msgStr;
	}
	
	public static String genIMResource(){
		String res = "";
		try{
			String UDID = DeviceUtils.getUDID();
			res += "_" + UDID.substring(UDID.length() - 6);
		}catch(Exception e){}
		return res;
	}
	
	public static void sortMsgs(ArrayList<SIXmppMessage> msgs){
		try {
			Collections.sort(msgs, new CompareSIXmppMessage());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public static String getFilePathFromResourceUri(Uri uri) {
		if (uri == null) {
			return null;
		}
		ContentResolver resolver = MyApplication.getInstance().getContentResolver();
		Cursor cursor  = null;
		try{
			cursor= resolver.query(uri, null, null, null, null);
			if (cursor == null) {
				return null;
			}
			if (cursor.moveToFirst()) {
				return cursor.getString(1);
			}
		}catch(Exception e){
		}finally{
			try{
				if(cursor != null)cursor.close();
			}catch(Exception e){}
		}
		return null;
	}
	
	public static String parseExtendMsg(SIXmppMessage msg){
		String msgStr = MyApplication.getInstance().getString(R.string.im_unknown_type_msg);
		String type = "", subtype = "", groupId = "", jid = "", optjid = "", userjid = "" , role = "";
		HashMap<String, String> elements = IMMessageFormat.parseExtMsg(msg.getTextContent());
		if (elements != null && elements.size() > 0) {
			type = elements.containsKey("type") ? elements.get("type") : "";
			subtype = elements.containsKey("subtype") ? elements.get("subtype") : "";
			groupId = elements.containsKey("groupId") ? elements.get("groupId") : "";
			jid = elements.containsKey("jid") ? elements.get("jid") : "";
			optjid = elements.containsKey("optjid") ? elements.get("optjid") : "";
			userjid = elements.containsKey("userjid") ? elements.get("userjid") : "";
			role = elements.containsKey("role") ? elements.get("role") : "";
		}
		try {
			if("15".equals(type)){//群操作通知
				if("1".equals(subtype)){//新建群通知群成员,只有一个群成员即：owner
				}else if("2".equals(subtype)){//当销毁群的时候通知群里所有的人
				}else if("3".equals(subtype)){//当修改群成员角色的时候通知被设置的成员
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return msgStr;
	}
}
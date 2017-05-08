package com.lz.oncon.app.im.ui;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.lb.common.util.ResourceUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.ui.view.MiniIlbcPlayerView;
import com.lz.oncon.app.im.ui.view.MsgFileView;
import com.lz.oncon.app.im.ui.view.MsgFriendStatusView;
import com.lz.oncon.app.im.ui.view.MsgHtmlTextView;
import com.lz.oncon.app.im.ui.view.MsgImageView;
import com.lz.oncon.app.im.ui.view.MsgInviteVideoView;
import com.lz.oncon.app.im.ui.view.MsgPrivateBulletView;
import com.lz.oncon.app.im.ui.view.MsgRecommandFriendView;
import com.lz.oncon.app.im.ui.view.MsgSystemView;
import com.lz.oncon.app.im.util.ExternalStorageUtil;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.app.im.util.SmileUtils;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.GifFaceData;
import com.lz.oncon.data.db.FaceHelper;
import com.lz.oncon.widget.GifStaticImageView;
import com.lz.oncon.widget.LocationView;

public class IMMessageFormat {
	public static final String FILE_TEMP_DIC = Environment.getExternalStorageDirectory() + "/" + Constants.PACKAGENAME + "/oncon/temp/";
	public static final String IMAGE_MSG = MyApplication.getInstance().getString(R.string.image);
	public static final String FILE_MSG = MyApplication.getInstance().getString(R.string.file_msg);
	public static final String UNKNOWN_MSG = MyApplication.getInstance().getString(R.string.im_unknown_type_msg);
	public static final String HAVE_CANNOT_ANALYZE_POST_MSG = MyApplication.getInstance().getString(R.string.havepost);
	
	public static final int msg_otherwidth = MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.app_im_msg_otherwidth);
	public static final int msgbg_paddinglong = MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.app_im_msgbg_padding_long);
	public static final int msgbg_paddingshort = MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.app_im_msgbg_padding_short);

	public static SpannableString parseMessageForDisplay(Context context, SIXmppMessage msg) {
		switch (msg.getContentType()) {
		case TYPE_TEXT: {
			String html = TextUtils.htmlEncode(msg.getTextContent());
			return new SpannableString(html);
		}
		case TYPE_HTML_TEXT:
			String msgTemp = msg.getTextContent().replaceAll("m1_extend_msg@@@lz-oncon@@@v1.0\\|\\|\\|type=14\\|\\|\\|html=", "");
			msgTemp = new String(Base64.decode(msgTemp.getBytes(), Base64.DEFAULT));
			return new SpannableString(msgTemp);
		default:
			return new SpannableString(UNKNOWN_MSG);
		}
	}
	
	public static String[] getMusicInfo(String msg){
		String[] result = new String[6];
		HashMap<String, String> elements = IMMessageFormat.parseExtMsg(msg);
		if (elements != null && elements.size() > 0) {
			result[0] = elements.containsKey("songId") ? elements.get("songId") : "";
			result[1] = elements.containsKey("songName") ? elements.get("songName") : "";
			result[2] = elements.containsKey("singer") ? elements.get("singer") : "";
			result[3] = elements.containsKey("songPath") ? elements.get("songPath") : "";
			result[4] = elements.containsKey("bigImgPath") ? elements.get("bigImgPath") : "";
			result[5] = elements.containsKey("smallImgPath") ? elements.get("smallImgPath") : "";
		}
		return result;
	}

	/**
	 * 获取定位的地址
	 * 
	 * @param msg
	 * @return
	 */
	public static String[] getLocString(String msg) {
		String[] result = new String[3];
		HashMap<String, String> elements = IMMessageFormat.parseExtMsg(msg);
		if (elements != null && elements.size() > 0) {
			result[0] = elements.containsKey("loc") ? elements.get("loc") : "";
			result[1] = elements.containsKey("long") ? elements.get("long") : "";
			result[2] = elements.containsKey("lat") ? elements.get("lat") : "";
		}
		return result;
	}

	/**
	 * 获取表情的名�?
	 * 
	 * @param msg
	 * @return
	 */
	public static String getFaceName(String msg) {
		String result = "";
		HashMap<String, String> elements = IMMessageFormat.parseExtMsg(msg);
		if (elements != null && elements.size() > 0) {
			result = elements.containsKey("name") ? elements.get("name") : "";
		}
		return result;
	}

	public static SpannableString resourceImageToSapSpannableString(Context context, int resourceId, String startText, String endText) {
		if (context == null) {
			return null;
		}

		ImageSpan imageSpan = new ImageSpan(context, resourceId);
		if (startText == null)
			startText = "";
		if (endText == null)
			endText = "";
		String source = "[图片]";
		SpannableString spannableString = new SpannableString(startText + source + endText);
		spannableString.setSpan(imageSpan, startText.length(), startText.length() + source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannableString;
	}

	public static SpannableString imageToSpannableString(Context context, String filePath, String startText, String endText) {
		ExternalStorageUtil.show(context);
		if (context == null) {
			return null;
		}
		Bitmap bitmap = null;
		if (TextUtils.isEmpty(filePath)) {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultpic);
		} else {
			bitmap = BitmapFactory.decodeFile(filePath);
		}
		ImageSpan imageSpan = new ImageSpan(context, bitmap);

		if (startText == null)
			startText = "";
		if (endText == null)
			endText = "";
		String source = "[图片]";
		SpannableString spannableString = new SpannableString(startText + source + endText);
		BitmapDrawable d = (BitmapDrawable) imageSpan.getDrawable();
		if (d != null && d.getBitmap() != null) {
			spannableString.setSpan(imageSpan, startText.length(), startText.length() + source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannableString;
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
	
	public static View parseMsgView(final Context context, SIXmppMessage d, int pos){
		if (d.getContentType() == SIXmppMessage.ContentType.TYPE_IMAGE) {// 照片
			MsgImageView miv = new MsgImageView(context);
			miv.setMessage(d);
			return miv;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_AUDIO) {// 录音
			MiniIlbcPlayerView playerView = new MiniIlbcPlayerView(context);
			playerView.setMessage(d);
			return playerView;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_LOC) {// 位置服务
			LocationView locationView = new LocationView(context);
			locationView.setMessage(d);
			return locationView;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_DYN_EXP) {// 动态表情
			final String resultHasExt = IMMessageFormat.getFaceName(d.getTextContent());
			int resId = 0;
			String result = "";
			if (resultHasExt.indexOf(".") >= 0) {
				result = resultHasExt.substring(0, resultHasExt.indexOf("."));
			}
			final GifStaticImageView faceView = new GifStaticImageView(context);
			GifFaceData gif = FaceHelper.getInstance(AccountData.getInstance().getUsername()).findImageByImageName(result);
			if (gif != null && gif.getIsdefault() != null && gif.getIsdefault().equals("0")) {
				try {
					resId = ResourceUtil.getRawIdx(result);
					faceView.setImageResource(resId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (gif != null && gif.getIsdefault() != null && gif.getIsdefault().equals("1")) {
				String faceName = result.concat(".").concat(gif.getExtension_name());
				String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(faceName);
				String picRemoteUrl = gif.getSuburl().concat(faceName);
				FaceHelper.loadGifFaceNew(context, picRemoteUrl, picLocalPath, faceName, faceView, gif);
			} else {// 数据库不存在，从网络下载
				final String mobile = AccountData.getInstance().getBindphonenumber();
				faceView.setImageResource(R.drawable.defaultpic);
				if (!TextUtils.isEmpty(mobile)) {
					final Handler handler = new Handler();
					handler.post(new Runnable() {
						@Override
						public void run() {
							//FIXME 下载动态表情
						}
					});
				}
			}
			return faceView;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_SYSTEM// 系统消息
				) {
			MsgSystemView systemView = new MsgSystemView(context);
			systemView.setMessage(d);
			return systemView;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_FILE) {// 文件
			MsgFileView fileView = new MsgFileView(context);
			fileView.setMessage(d);
			return fileView;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_2) { //手机看帖HTML
//				HTMLLinkedTextView htmlLinkedTextView = new HTMLLinkedTextView(context);
//				htmlLinkedTextView.setMessage(d);
//				return htmlLinkedTextView;
			MsgHtmlTextView msgView = new MsgHtmlTextView(context);
			msgView.setMessage(d);
			return msgView;
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_GENERAL) { //通用版HTML消息
			MsgHtmlTextView msgView = new MsgHtmlTextView(context);
			msgView.setMessage(d);
			return msgView;
		} else if(d.getContentType() == SIXmppMessage.ContentType.TYPE_CUSTOM_PROTOCOL//自定义协议
				&& d.getTextContent().indexOf("recommand_friend") > -1//1.16 推荐给好友接口
				){
			MsgRecommandFriendView msgView = new MsgRecommandFriendView(context);
			msgView.setMessage(d);
			return msgView;
		} else if(d.getContentType() == SIXmppMessage.ContentType.TYPE_CUSTOM_PROTOCOL//自定义协议
				&& d.getTextContent().indexOf("invite_video") > -1//1.41邀请观看视频信息
				){
			MsgInviteVideoView msgView = new MsgInviteVideoView(context);
			msgView.setMessage(d);
			return msgView;
		} else if(d.getContentType() == SIXmppMessage.ContentType.TYPE_CUSTOM_PROTOCOL//自定义协议
				&& d.getTextContent().indexOf("private_bullet") > -1//1.34（非服务器）直播间内发送私聊信息(直播间@消息）
				){
			MsgPrivateBulletView msgView = new MsgPrivateBulletView(context);
			msgView.setMessage(d);
			return msgView;
		} else if(d.getContentType() == SIXmppMessage.ContentType.TYPE_CUSTOM_PROTOCOL//自定义协议
				&& d.getTextContent().indexOf("friend_status") > -1//1.39 推送好友当前操作状态
				){
			MsgFriendStatusView msgView = new MsgFriendStatusView(context);
			msgView.setMessage(d);
			return msgView;
		} else {
				SpannableString msg = parseMessageForDisplay(context, d);
				TextView textView = new TextView(context);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(15);
				textView.setMaxWidth(BaseActivity.screenWidth - msg_otherwidth - msgbg_paddingshort - msgbg_paddinglong);
				String msgStr = msg.toString();
				msgStr = msgStr.replaceAll("\r\n", "<br/>");
				msgStr = msgStr.replaceAll("\n", "<br/>");
				msgStr = msgStr.replaceAll("\r", "<br/>");
				Spannable afterlinkify = linkifyHtml(msgStr, Linkify.ALL);
				textView.setText(SmileUtils.getSmiledText(context, afterlinkify));
				textView.setMovementMethod(LinkMovementMethod.getInstance());
				return textView;
		}
	}
	
	public static Spannable linkifyHtml(String html, int linkifyMask) {
	    Spanned text = Html.fromHtml(html);
	    URLSpan[] currentSpans = text.getSpans(0, text.length(), URLSpan.class);

	    SpannableString buffer = new SpannableString(text);
	    Linkify.addLinks(buffer, linkifyMask);

	    for (URLSpan span : currentSpans) {
	        int end = text.getSpanEnd(span);
	        int start = text.getSpanStart(span);
	        buffer.setSpan(span, start, end, 0);
	    }
	    return buffer;
	}
}
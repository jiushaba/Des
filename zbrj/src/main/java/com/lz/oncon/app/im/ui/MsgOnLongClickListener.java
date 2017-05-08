package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.lb.common.util.Clipboard;
import com.lb.common.util.Constants;
import com.lb.common.util.ShareUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.ShareAllEdit;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.core.im.core.OnconIMMessage;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.util.WeiXinShareUtil;
import com.lz.oncon.widget.ImageTextSharePopupWindow;
import com.lz.oncon.widget.ImageTextSharePopupWindow.SEND_TYPE;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.SimpleShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class MsgOnLongClickListener implements OnLongClickListener {

	private ArrayList<SIXmppMessage> mDatas;
	private Context mContext;
	private IWXAPI api;
	String[] itemMunu;
	String[] mediaItemMunu;
	String[] mediaItemMunuWithColl;
	String[] twoofmediaItemMunu;
	String[] threeofmediaItemMunu;
	String[] shareType;
	String[] share_wx, share_qq, share_html,share_oncon;
	Builder mLongClickMenuBuilder;
	Builder builder_share;
	//UMSocialService mUMSocialService;
	String mOnconId;

	public MsgOnLongClickListener(Context context,
			ArrayList<SIXmppMessage> mDatas, String mOnconId) {
		this.mContext = context;
		this.mDatas = mDatas;
		this.mOnconId = mOnconId;
		api = WXAPIFactory.createWXAPI(mContext, Constants.APPID);
		itemMunu = mContext.getResources()
				.getStringArray(R.array.msg_item_menu);
		mediaItemMunu = mContext.getResources().getStringArray(
				R.array.msg_media_item_menu);
		mediaItemMunuWithColl = mContext.getResources().getStringArray(
				R.array.msg_media_item_menu_with_coll);
		share_wx = mContext.getResources()
				.getStringArray(R.array.wx_share_menu);
		share_qq = mContext.getResources()
				.getStringArray(R.array.qq_share_menu);
		share_html = mContext.getResources().getStringArray(
				R.array.share_html_menu);
		share_oncon = mContext.getResources()
				.getStringArray(R.array.oncon_share_menu);
		twoofmediaItemMunu = new String[] { mediaItemMunu[0], mediaItemMunu[1] };
		threeofmediaItemMunu = new String[] { mediaItemMunuWithColl[0],
				mediaItemMunuWithColl[1], mediaItemMunuWithColl[2] };
		shareType = mContext.getResources().getStringArray(R.array.share_type);
		mLongClickMenuBuilder = new Builder(mContext);
		builder_share = new Builder(mContext);
		//registerPlatform();
		//mUMSocialService = UMServiceFactory.getUMSocialService("android",
		//		RequestType.SOCIAL);
		ShareUtil.registerPlatform((Activity)mContext,mContext.getApplicationContext());
	}

	
	@Override
	public boolean onLongClick(View v) {
		if (!api.isWXAppInstalled()) { // 检查是否安装微信，如未安装在不显示 分享到微信
			itemMunu = new String[] { itemMunu[0], itemMunu[1], itemMunu[2],
					itemMunu[3], itemMunu[4], itemMunu[5] };
			mediaItemMunu = new String[] { mediaItemMunu[0], mediaItemMunu[1],
					mediaItemMunu[2] };
			mediaItemMunuWithColl = new String[] { mediaItemMunuWithColl[0],
					mediaItemMunuWithColl[1], mediaItemMunuWithColl[2],
					mediaItemMunuWithColl[3], mediaItemMunuWithColl[4] };
		} else {
			api.registerApp(Constants.APPID);
		}

		final int position = (Integer) v.getTag(R.id.tag_position);
		final SIXmppMessage d = mDatas.get(position);
		if (d.getContentType() == SIXmppMessage.ContentType.TYPE_TEXT) { // 文本
				mLongClickMenuBuilder.setItems(itemMunu,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 5) {
									doFollowMenu(WHICH__SHARE, position,
											WeiXinShareUtil.TEXT);
								}  else if (which == 6) {
									if (itemMunu.length == 9) {// wx
										shareTextToWX(d);
									} else if (itemMunu.length == 8) {// email
										shareToEmail(d);
									}
								} else if (which == 7) {
									if (itemMunu.length == 9) {// email
										shareToEmail(d);
									} else if (itemMunu.length == 8) {// sms
										shareToSMS(d);
									}
								} else if (which == 8) {// sms
									shareToSMS(d);
								} else {
									doFollowMenu(which, position, 0);
								}
							}
						});
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_IMAGE) { // 图片
			mLongClickMenuBuilder.setItems(mediaItemMunuWithColl,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 2) {
								doFollowMenu(WHICH__COLL, position, 0);
							} else if (which == 3) {
								doFollowMenu(WHICH__TRANSMIT, position,
										WeiXinShareUtil.IMAGE);
							} else if (which == 4) {
								doFollowMenu(WHICH__SHARE, position,
										WeiXinShareUtil.IMAGE);
							}  else if (which == 5) {
								if (mediaItemMunuWithColl.length == 7) {// wx
									shareImageToWX(d);
								} else if (mediaItemMunuWithColl.length == 6) {// email
									shareToEmail(d);
								}
							} else if (which == 6) {
								shareToEmail(d);
							} else {
								doFollowMenu(which, position, 0);
							}
						}
					});
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_TALK_PIC) { // 说图
			mLongClickMenuBuilder.setItems(mediaItemMunu,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 2) {
								doFollowMenu(WHICH__SHARE, position,
										WeiXinShareUtil.TALK_IMAGE);
							}  else if (which == 3) {
								if (mediaItemMunu.length == 5) {// wx
									shareImageToWX(d);
								} else if (mediaItemMunu.length == 4) {// email
									shareToEmail(d);
								}
							} else if (which == 4) {
								shareToEmail(d);
							} else {
								doFollowMenu(which, position, 0);
							}
						}
					});
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_DYN_EXP) { // 动态表情
			mLongClickMenuBuilder.setItems(twoofmediaItemMunu,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							doFollowMenu(which, position, 0);
						}
					});
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_AUDIO
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_LOC) {
			mLongClickMenuBuilder.setItems(threeofmediaItemMunu,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 2) {
								doFollowMenu(WHICH__COLL, position, 0);
							} else {
								doFollowMenu(which, position, 0);
							}
						}
					});
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_2) {
			mLongClickMenuBuilder.setItems(share_html, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SIXmppMessage message = cloneSIXmppMessage(d);
					String htmlMsgTemp = message.getTextContent().replaceAll("m1_extend_msg@@@lz-oncon@@@v1.0\\|\\|\\|type=16\\|\\|\\|", "");
					String[] htmlMsgtemp2 = htmlMsgTemp.split("\\|\\|\\|");
					String url = null;
					if(htmlMsgtemp2.length > 2){
						String[] web = htmlMsgtemp2[2].replaceAll("webhtml=", "").split("\\|\\|\\|");
						htmlMsgTemp = new String(Base64.decode(web[0].getBytes(), Base64.DEFAULT));
					}					
					
					int end = htmlMsgTemp.length();
					Spanned spannedHtml = Html.fromHtml(htmlMsgTemp);
					String content = spannedHtml.toString();
					URLSpan[] urls = spannedHtml.getSpans(0, end, URLSpan.class);
					if(null != urls && urls.length > 0){
						url = urls[0].getURL();
					}
					message.setTextContent(content+url);
					if (which == 0) {
						//doFollowMenu(WHICH__SHARE, position,
						//		WeiXinShareUtil.TEXT);
						Intent intent = new Intent(mContext, ShareAllEdit.class);
						Bundle bundle = new Bundle();
						bundle.putInt("MESSAGE_TYPE", WeiXinShareUtil.TEXT);
						bundle.putString("MESSAGE_CONTENT", message.getTextContent());
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					} else if (which == 1) {
						if (share_html.length == 5) {// oncon
							shareToOncon(message, content, url,position);
						} else if (share_html.length == 4) {// wx
							shareWebToWX(content,url);
						}
					} else if (which == 2) {
						if (share_html.length == 5) {// wx
							shareWebToWX(content,url);
						} else if (share_html.length == 4) {// email
							shareToEmail(message);
						}
					} else if (which == 3) {
						if (share_html.length == 5) {// email
							shareToEmail(message);
						} else if (share_html.length == 4) {// sms
							shareToSMS(message);
						}
					} else if (which == 4) {// sms
						shareToSMS(message);
					} else {
						doFollowMenu(which, position, 0);
					}
				}
			});
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_GENERAL) {
			mLongClickMenuBuilder.setItems(share_html, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SIXmppMessage message = cloneSIXmppMessage(d);
					String htmlMsgTemp = message.getTextContent().replaceAll("m1_extend_msg@@@lz-oncon@@@v1.0\\|\\|\\|type=26\\|\\|\\|", "");
					String[] htmlMsgtemp2 = htmlMsgTemp.split("\\|\\|\\|");
					String url = null;
					if(htmlMsgtemp2.length > 1){
						String[] web = htmlMsgtemp2[1].replaceAll("whtml=", "").split("\\|\\|\\|");
						htmlMsgTemp = new String(Base64.decode(web[0].getBytes(), Base64.DEFAULT));
					}					
					
					int end = htmlMsgTemp.length();
					Spanned spannedHtml = Html.fromHtml(htmlMsgTemp);
					String content = spannedHtml.toString();
					URLSpan[] urls = spannedHtml.getSpans(0, end, URLSpan.class);
					if(null != urls && urls.length > 0){
						url = urls[0].getURL();
					}
					message.setTextContent(content+url);
					if (which == 0) {
						//doFollowMenu(WHICH__SHARE, position,
						//		WeiXinShareUtil.TEXT);
						Intent intent = new Intent(mContext, ShareAllEdit.class);
						Bundle bundle = new Bundle();
						bundle.putInt("MESSAGE_TYPE", WeiXinShareUtil.TEXT);
						bundle.putString("MESSAGE_CONTENT", message.getTextContent());
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					} else if (which == 1) {
						if (share_html.length == 5) {// oncon
							shareToOncon(message, content, url,position);
						} else if (share_html.length == 4) {// wx
							shareWebToWX(content,url);
						}
					} else if (which == 2) {
						if (share_html.length == 5) {// wx
							shareWebToWX(content,url);
						} else if (share_html.length == 4) {// email
							shareToEmail(message);
						}
					} else if (which == 3) {
						if (share_html.length == 5) {// email
							shareToEmail(message);
						} else if (share_html.length == 4) {// sms
							shareToSMS(message);
						}
					} else if (which == 4) {// sms
						shareToSMS(message);
					} else {
						doFollowMenu(which, position, 0);
					}
				}
			});
		}else {
			mLongClickMenuBuilder.setItems(twoofmediaItemMunu,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							doFollowMenu(which, position, 0);
						}
					});
		}
		mLongClickMenuBuilder.show();
		return true;
	}

	private void shareTextToWX(final SIXmppMessage d) {
		mLongClickMenuBuilder.setItems(share_wx,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						WeiXinShareUtil.wx_share_text(mContext, which,
								d.getTextContent());
					}
				});
		mLongClickMenuBuilder.show();
	}
	
	private void shareToOncon(final SIXmppMessage d,final String title,final String url,final int pos) {
		mLongClickMenuBuilder.setItems(share_oncon,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0){						
							if(!TextUtils.isEmpty(url)){//帖子
								Clipboard.setText(mContext, url);
								Intent intent1 = new Intent(mContext,
										ContactMsgCenterActivity.class);
								intent1.putExtra(ContactMsgCenterActivity.LAUNCH_MODE,
										ContactMsgCenterActivity.LAUNCH_MODE_TRANSMIT);
								intent1.putExtra("contentType", d.getContentType().ordinal());
								mContext.startActivity(intent1);
							}else if(!TextUtils.isEmpty(d.getImagePath())){
								
							}else{
								doFollowMenu(4, pos, 0);
							}
							
						}else{
							if(!TextUtils.isEmpty(url)){
								showShareDialog(SEND_TYPE.SEND_FRIEND_CIRCLE,d,title,url);
							}else if(!TextUtils.isEmpty(d.getImagePath())){
								Fc_PicConstants.selectlist.clear();
							}												
						}
					}
				});
		mLongClickMenuBuilder.show();
	}
	
	private void showShareDialog(SEND_TYPE sendType,final SIXmppMessage d,String title,String url){
		if(mContext instanceof Activity){
			ImageTextSharePopupWindow sharePopupWindow = new ImageTextSharePopupWindow(mContext);
			sharePopupWindow.setData(d.getId(), title, title, d.getFrom(), d.getNickname(), url, d.getImagePath(), false,ContactMsgCenterActivity.LAUNCH_MODE_IMAGETEXTMSG);
			sharePopupWindow.setType(sendType);
			sharePopupWindow.showAtLocation(((Activity)mContext).getWindow().getDecorView(),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		}		
	}
	
	private void shareWebToWX(final String content,final String url) {
		mLongClickMenuBuilder.setItems(share_wx,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SHARE_MEDIA shareType = null;
						SimpleShareContent shareContent = null;
						if(which == 0){
							shareType = SHARE_MEDIA.WEIXIN;
							shareContent = new WeiXinShareContent();							
						}else if(which == 1){
							shareType = SHARE_MEDIA.WEIXIN_CIRCLE;
							shareContent = new CircleShareContent();
						}
						((BaseShareContent) shareContent).setTitle(content);						
						shareContent.setShareContent(content);
						shareContent.setShareImage(new UMImage(mContext, R.drawable.icon_group_small));
						((BaseShareContent) shareContent).setTargetUrl(url);
						ShareUtil.shareTo(mContext,shareType, shareContent);
					}
				});
		mLongClickMenuBuilder.show();
	}
	
	private void shareImageToWX(final SIXmppMessage d) {
		mLongClickMenuBuilder
		.setItems(
				share_wx,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						WeiXinShareUtil
								.wx_share_image(
										mContext,
										which,
										d.getImagePath(),
										d.getThumbnailPath());
					}
				});
		mLongClickMenuBuilder.show();
	}
/*	
	private void shareToQQ(final SIXmppMessage d) {
		mLongClickMenuBuilder.setItems(share_qq,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						QQShareUtil.shareToQQ(
								(Activity) mContext,
								which,
								d.getTextContent(),
								d.getImagePath(), null);
					}
				});
		mLongClickMenuBuilder.show();
	}
*/	
	private void shareToEmail(final SIXmppMessage d) {
		MailShareContent mailShareContent = new MailShareContent();
		mailShareContent.setShareContent(d
				.getTextContent());
		mailShareContent.setShareImage(new UMImage(
				mContext, d.getImagePath()));
		ShareUtil.shareTo(mContext, SHARE_MEDIA.EMAIL,
				mailShareContent);
	}
	
	private void shareToSMS(final SIXmppMessage d) {
		SmsShareContent smsShareContent = new SmsShareContent();
		smsShareContent.setShareContent(d
				.getTextContent());
		ShareUtil.shareTo(mContext,
				SHARE_MEDIA.SMS, smsShareContent);
	}
/*
	private void registerPlatform() {
		mUMSocialService = UMServiceFactory.getUMSocialService("android",
				RequestType.SOCIAL);
		mUMSocialService.getConfig().enableSIMCheck(false);
		mUMSocialService.getConfig().closeToast();
		// 添加新浪的SSO授权支持
		mUMSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO支持
		mUMSocialService.getConfig().setSsoHandler(new TencentWBSsoHandler());

		UMWXHandler wxHandler = new UMWXHandler(mContext,
				Constants.APPID);
		wxHandler.addToSocialSDK();

		UMWXHandler wxCircleHandler = new UMWXHandler(
				mContext, Constants.APPID);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

//		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
//				(Activity)mContext, Constants.QQ_APPID,
//				Constants.QQ_APPKEY);
//		qqSsoHandler.addToSocialSDK();
//
//		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
//				(Activity)mContext, Constants.QQ_APPID,
//				Constants.QQ_APPKEY);
//		qZoneSsoHandler.addToSocialSDK();

		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();

		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
	}
	*/
	private static final int WHICH__DELETE = 0;
	private static final int WHICH__DELETEALL = 1;
	private static final int WHICH__COPY = 2;
	private static final int WHICH__COLL = 3;
	private static final int WHICH__TRANSMIT = 4;// 转发
	private static final int WHICH__SHARE = 5;

	/**
	 * Description: 根据弹出的菜单选项不同操作
	 * 
	 * @param which
	 *            用户的选择
	 * @param msg
	 *            当前的消息
	 * @param pos
	 *            当前消息的位置 void
	 * @param flag
	 *            分享内容是否为动态表情
	 */
	private void doFollowMenu(int which, final int pos, final int type) {
		SIXmppMessage msg = mDatas.get(pos);
		switch (which) {
		case WHICH__COPY: {
			String msgText = msg.getTextContent();
			Clipboard.setText(mContext, msgText);
		}
			break;
		case WHICH__DELETE: {
			ImData.getInstance().deleteMessageData(mOnconId, msg.getId());
		}
			break;
		case WHICH__DELETEALL: {
			ImData.getInstance().deleteMessageData(mOnconId);
		}
			break;
		case WHICH__COLL:
			break;
		case WHICH__SHARE:
			Intent intent = new Intent(mContext, ShareAllEdit.class);
			Bundle bundle = new Bundle();
			switch (type) {
			case WeiXinShareUtil.TEXT:
				bundle.putInt("MESSAGE_TYPE", WeiXinShareUtil.TEXT);
				bundle.putString("MESSAGE_CONTENT", msg.getTextContent());
				break;
			case WeiXinShareUtil.IMAGE:
				bundle.putInt("MESSAGE_TYPE", WeiXinShareUtil.IMAGE);
				bundle.putString("MESSAGE_IMAGE_PATH", msg.getImagePath());
				bundle.putString("MESSAGE_IMAGE_THUM", msg.getThumbnailPath());
				break;
			case WeiXinShareUtil.TALK_IMAGE:
				bundle.putInt("MESSAGE_TYPE", WeiXinShareUtil.TALK_IMAGE);
				bundle.putString("MESSAGE_IMAGE_PATH", msg.getImagePath());
				bundle.putString("MESSAGE_IMAGE_THUM", msg.getThumbnailPath());
				break;
			case WeiXinShareUtil.DYN_EXP:
				bundle.putInt("MESSAGE_TYPE", WeiXinShareUtil.DYN_EXP);
				break;
			default:
				break;
			}
			intent.putExtras(bundle);
			mContext.startActivity(intent);
			break;
		case WHICH__TRANSMIT:
			String msgText = OnconIMMessage.genMsgBody(msg);
			Clipboard.setText(mContext, msgText);
			Intent intent1 = new Intent(mContext, ContactMsgCenterActivity.class);
			intent1.putExtra(ContactMsgCenterActivity.LAUNCH_MODE, ContactMsgCenterActivity.LAUNCH_MODE_TRANSMIT);
			intent1.putExtra("contentType", msg.getContentType().ordinal());
			mContext.startActivity(intent1);
			break;
		}
	}
	private SIXmppMessage cloneSIXmppMessage(SIXmppMessage d){
		SIXmppMessage message = new SIXmppMessage();
		message.setFrom(d.getFrom());
		message.setContentType(d.getContentType());
		message.setId(d.getId());
		message.setImagePath(d.getImagePath());
		message.setNickname(d.getNickname());
		message.setTextContent(d.getTextContent());
		return message;
	}
}
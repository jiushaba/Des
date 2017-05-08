package com.lb.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.widget.InfoToast;
import com.tencent.connect.share.QQShare;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.SimpleShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareUtil {

	public static void registerPlatform(Activity activity,Context context) {
		UMSocialService mUMSocialService = UMServiceFactory.getUMSocialService("android",
				RequestType.SOCIAL);
		mUMSocialService.getConfig().enableSIMCheck(false);
		mUMSocialService.getConfig().closeToast();
		// 添加新浪的SSO授权支持
		//mUMSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO支持
		//mUMSocialService.getConfig().setSsoHandler(new TencentWBSsoHandler());
		

		UMWXHandler wxHandler = new UMWXHandler(context,
				Constants.APPID ,Constants.APPSECRET);
		wxHandler.addToSocialSDK();

		UMWXHandler wxCircleHandler = new UMWXHandler(
				context, Constants.APPID,Constants.APPSECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
				activity, Constants.QQ_APPID,
				Constants.QQ_APPKEY);
		qqSsoHandler.addToSocialSDK();
		
		/*QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				activity, Constants.QQ_APPID,
				Constants.QQ_APPKEY);
		qZoneSsoHandler.addToSocialSDK();*/

		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();

		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
	}
	
	public static void shareTo(final Context context,
			SHARE_MEDIA shareMedia, SimpleShareContent content) {
		try {
			UMSocialService mController = UMServiceFactory.getUMSocialService(
					"com.umeng.share");		
			mController.setShareMedia(content);

			mController.postShare(context, shareMedia, new SnsPostListener() {
				@Override
				public void onStart() {
				}

				@Override
				public void onComplete(SHARE_MEDIA arg0, int arg1,
						SocializeEntity arg2) {
					if (arg1 == 200) {
						if(arg0 != SHARE_MEDIA.EMAIL && arg0 != SHARE_MEDIA.SMS){
							InfoToast.makeText(context
									, context.getString(R.string.more_weibo_sharefinish)
									, Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
						}
					} else {
						InfoToast.makeText(context
								, context.getString(R.string.more_weibo_sharefail)
								, Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
					}
				}
			});
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	public static void shareToSinaTencent(final BaseActivity activity,
			Bitmap bitmap_image, String share_text, boolean flag_sian,
			String sina_at_name, boolean flag_tencent, String tencent_at_name) {
		// 分享的文本内容
		UMShareMsg umsm;
		UMSocialService mController = UMServiceFactory
				.getUMSocialService("com.umeng.share");
		// 添加新浪和QQ空间的SSO授权支持
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO支持
//		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// SocializeConfig config =
		// MyApplication.getInstance().umService.getConfig();
		final SocializeConfig config = MyApplication.getInstance().umService
				.getConfig();

		if (flag_sian) {
			if (!OauthHelper.isAuthenticatedAndTokenNotExpired(activity, SHARE_MEDIA.SINA)) {
				oauth(activity, SHARE_MEDIA.SINA);
				return;
			}
		}
		if (flag_tencent) {
			if (!OauthHelper.isAuthenticatedAndTokenNotExpired(activity, SHARE_MEDIA.TENCENT)) {
				oauth(activity, SHARE_MEDIA.TENCENT);
				return;
			}
		}

		if (flag_sian) {
			umsm = new UMShareMsg();
			umsm.setMediaData(new UMImage(activity, bitmap_image));

			if (sina_at_name == null) {
				sina_at_name = "";
			}

			share_text = sina_at_name + share_text;
			int sum = StringUtils.strlen(share_text);
			int slength = 140 - StringUtils.strlen(activity
					.getString(R.string.sharefrom_sina)); // 119
			int a;
			while (sum > slength) {
				a = share_text.length();
				share_text = share_text.substring(0, a - 1);
				sum = StringUtils.strlen(share_text);
			}
			umsm.mText = share_text
					+ activity.getString(R.string.sharefrom_sina);
			MyApplication.getInstance().umService.setShareContent(umsm.mText);
			config.addFollow(SHARE_MEDIA.SINA, Constants.WEIBO_SINA_GOVEMENT_ID);
			MyApplication.getInstance().umService.setConfig(config);
			MyApplication.getInstance().umService.postShare(activity,
					SHARE_MEDIA.SINA, new SnsPostListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(SHARE_MEDIA arg0, int arg1,
								SocializeEntity arg2) {
							if (arg1 == 200) {
								activity.toastToMessage(R.string.share_sina_s);
							} else {
								activity.toastToMessage(MyApplication
										.getInstance().getString(
												R.string.share_sina_f)
										+ arg1);
							}
						}
					});
		}

		if (flag_tencent) {
			umsm = new UMShareMsg();
			umsm.setMediaData(new UMImage(activity, bitmap_image));

			if (tencent_at_name == null) {
				tencent_at_name = "";
			}

			share_text = tencent_at_name + share_text;
			int sum = StringUtils.strlen(share_text);
			int tlength = 140 - StringUtils.strlen(activity
					.getString(R.string.sharefrom_tencent));
			int a;
			while (sum > tlength) {
				a = share_text.length();
				share_text = share_text.substring(0, a - 1);
				sum = StringUtils.strlen(share_text);
			}
			umsm.mText = share_text
					+ MyApplication.getInstance().getString(
							R.string.sharefrom_tencent);
			MyApplication.getInstance().umService.setShareContent(umsm.mText);
			config.addFollow(SHARE_MEDIA.TENCENT,
					Constants.WEIBO_QQ_GOVEMENT_ID);
			MyApplication.getInstance().umService.setConfig(config);
			MyApplication.getInstance().umService.postShare(activity,
					SHARE_MEDIA.TENCENT, new SnsPostListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(SHARE_MEDIA arg0, int arg1,
								SocializeEntity arg2) {
							if (arg1 == 200) {
								activity.toastToMessage(R.string.share_tencent_s);
							} else {
								activity.toastToMessage(activity
										.getString(R.string.share_tencent_f)
										+ arg1);
							}
						}
					});
		}
	}

	// 授权操作
	public static void oauth(final BaseActivity activity,
			final SHARE_MEDIA platform) {
		MyApplication.getInstance().umService.doOauthVerify(activity, platform,
				new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA arg0) {
					}

					@Override
					public void onError(SocializeException arg0,
							SHARE_MEDIA arg1) {
					}

					@Override
					public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
						if (platform == SHARE_MEDIA.SINA) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.toastToMessage(R.string.oauth_success);
								}
							});
						}
						if (platform == SHARE_MEDIA.TENCENT) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.toastToMessage(R.string.oauth_success);
								}
							});
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA arg0) {
					}
				});
	}
}
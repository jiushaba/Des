package com.lz.oncon.app.im.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.widget.Toast;

import com.xuanbo.xuan.R;
import com.lz.oncon.application.MyApplication;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeiXinShareUtil {
	
	public static final int TEXT = 1001;
	public static final int IMAGE = 1002;
	public static final int TALK_IMAGE = 1003;
	public static final int DYN_EXP = 1004;
	public static final int AD = 1005;
	public static final int AUDIO = 1006;
	
	private static IWXAPI api = WXAPIFactory.createWXAPI(MyApplication.getInstance(), com.lb.common.util.Constants.APPID);
	
	public static void wx_share_text(Context context, int which, String content){
		if (content == null || content.length() == 0) {
			return;
		}
		
		int isFriendCircle = isFriendCircle(context, which);
		if(isFriendCircle == -1)return;

		WXTextObject textObj = new WXTextObject();
		textObj.text = content;

		WXMediaMessage wxmm = new WXMediaMessage();
		wxmm.mediaObject = textObj;
		wxmm.description = content;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text");
		req.message = wxmm;
		req.scene = isFriendCircle;
		api.sendReq(req);
	}
	
	public static void wx_share_image(Context context, int which, String imagePath, String thumbnailPath){
		int isFriendCircle = isFriendCircle(context, which);
		if(isFriendCircle == -1)return;
		
		String img = null;
		File file = null;
		if (!TextUtils.isEmpty(imagePath)) {
			file = new File(imagePath);
			if (!file.exists()) {
				if (!TextUtils.isEmpty(thumbnailPath)) {
					file = new File(thumbnailPath);
					if (!file.exists()) {
						return;
					} else {
						img = thumbnailPath;
					}
				} else {
					return;
				}
			} else {
				img = imagePath;
			}
		} else {
			if (!TextUtils.isEmpty(thumbnailPath)) {
				file = new File(thumbnailPath);
				if (!file.exists()) {
					return;
				} else {
					img = thumbnailPath;
				}
			} else {
				return;
			}
		}

		Bitmap bmp = BitmapFactory.decodeFile(img);
		sendByWX(api, context.getString(R.string.share_wx_form_yixin), bmp, isFriendCircle);
	}
	
	private static int isFriendCircle(Context context, int which){
		int isFriendCircle = SendMessageToWX.Req.WXSceneSession;
		if (which == 0) {
			isFriendCircle = SendMessageToWX.Req.WXSceneSession;
		} else if (which == 1) {
			isFriendCircle = SendMessageToWX.Req.WXSceneTimeline;
			if (api.getWXAppSupportAPI() < com.lb.common.util.Constants.TIMELINE_SUPPORTED_VERSION) {
				Toast.makeText(context, context.getString(R.string.share_wx_no_circle), Toast.LENGTH_SHORT).show();
				return -1;
			}
		}
		return isFriendCircle;
	}
	
	private static void sendByWX(final IWXAPI api, String shareContent, Bitmap shareImage, int isFriendCircle) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = MyApplication.getInstance().getString(R.string.share_wx_link);
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = MyApplication.getInstance().getString(R.string.share_wx_yixin);
		msg.description = shareContent;

		if (shareImage != null) {
			byte[] b = bmpToByteArray(shareImage, true);
			shareImage.recycle();
			if (b != null) {
				Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
				bmp.recycle();
				thumbBmp = compressImage(thumbBmp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				thumbBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);

				msg.thumbData = baos.toByteArray();
			}
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = isFriendCircle;
		api.sendReq(req);
	}
	
	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 32) { // 循环判断如果压缩后图片是否大于32kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
			if (options == 10) {
				break;
			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}

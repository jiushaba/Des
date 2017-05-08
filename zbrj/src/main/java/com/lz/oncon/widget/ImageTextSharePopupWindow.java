package com.lz.oncon.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lb.common.util.ShareUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.app.im.util.IMUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.SimpleShareContent;

public class ImageTextSharePopupWindow extends PopupWindow {

	private TextView title, brief, author, cancel, share;
	private EditText content;
	private ImageView image;
	private View mMenuView;
	private String titlestr;
	private Context mContext;
	//private Bitmap b;
	private LinearLayout popLL;
	private SEND_TYPE mSendType;
	private SimpleShareContent mShareContent;
	private SHARE_MEDIA mShareMedia;

	public enum SEND_TYPE {
		SEND_FRIEND, // 推荐给朋友
		SEND_FRIEND_CIRCLE, // 分享到人脉圈
		SHARE_THIRD_PARTY, // 分享到第三方
	};

	public void setShareContent(SimpleShareContent shareContent) {
		this.mShareContent = shareContent;
	}

	public void setShareMedia(SHARE_MEDIA shareMedia) {
		this.mShareMedia = shareMedia;
	}

	public ImageTextSharePopupWindow(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(
				R.layout.widget_imagetextshare_popupwindow, null);
		title = (TextView) mMenuView.findViewById(R.id.title);
		brief = (TextView) mMenuView.findViewById(R.id.brief);
		author = (TextView) mMenuView.findViewById(R.id.author);
		cancel = (TextView) mMenuView.findViewById(R.id.cancel);
		share = (TextView) mMenuView.findViewById(R.id.share);
		content = (EditText) mMenuView.findViewById(R.id.content);
		image = (ImageView) mMenuView.findViewById(R.id.image);
		popLL = (LinearLayout) mMenuView.findViewById(R.id.pop_layout);
		// 取消按钮
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {// 销毁弹出框
				dismiss();
			}
		});
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		mMenuView.setFocusable(true);
		mMenuView.setFocusableInTouchMode(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x00000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(null);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int top = popLL.getTop();
				int bottom = popLL.getBottom();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < top || y > bottom) {
						dismiss();
					}
				}
				return true;
			}
		});

		mMenuView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// 手机键盘上的返回键
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					dismiss();
					break;
				}
				return false;
			}
		});
	}

	public void setType(SEND_TYPE sendType) {
		mSendType = sendType;
	}

	public void setData(String msgId, final String titleStr,
			final String briefStr, final String pub_accountStr,
			final String authorStr, final String desc_urlStr,
			final String image_urlStr, boolean isSend,final int messageType) {
		try {
			titlestr = titleStr;
			title.setText(titlestr);
			brief.setText(briefStr);
			if (null == authorStr || "".equals(authorStr)) {
				author.setVisibility(View.GONE);
			} else {
				author.setText(mContext.getString(R.string.come_from) + "  "
						+ authorStr);
			}

			//desc_url = desc_urlStr;

			String bigUrl = image_urlStr;// 这里获取图片的url路径
			String picBigName = TextUtils.isEmpty(bigUrl) ? "" : IMUtil
					.getNewsPicName(bigUrl);// 这里获取图片的名字
			if("".equals(picBigName)){
				image.setImageResource(R.drawable.icon_group_small);
			}else{
				String pathBig = IMConstants.PATH_NEWS_PICTURE + msgId + picBigName;// 这里定义图片保存到本地的路径
			}
			

			share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {// 分享
					String share_text = content.getText().toString();
					if (mSendType == null) {
					} else if (mSendType.ordinal() == SEND_TYPE.SEND_FRIEND
							.ordinal()) {
						Intent intent = new Intent(mContext,
								ContactMsgCenterActivity.class);
						intent.putExtra(
								ContactMsgCenterActivity.LAUNCH_MODE,
								messageType);
						intent.putExtra("title", titleStr);
						intent.putExtra("brief", briefStr);
						intent.putExtra("image_url", image_urlStr);
						intent.putExtra("detail_url", desc_urlStr);
						intent.putExtra("pub_account", pub_accountStr);
						intent.putExtra("author", authorStr);
						intent.putExtra("share_text", share_text);
						mContext.startActivity(intent);
					} else if (mSendType.ordinal() == SEND_TYPE.SHARE_THIRD_PARTY
							.ordinal()) {
						if (null != mShareMedia) {
							ShareUtil.shareTo((BaseActivity) mContext,
									mShareMedia, mShareContent);
						}
						// ShareUtil.shareToSinaTencent((BaseActivity)mContext,
						// b, share_text + " " + desc_url, true, "", false, "");
					}
					dismiss();
				}
			});
		} catch (Exception e) {
		}
	}
}
package com.lz.oncon.app.im.ui.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.ui.IMMessageFormat;
import com.lz.oncon.app.im.ui.MsgOnClickListener;
import com.lz.oncon.app.im.ui.MsgOnLongClickListener;
import com.lz.oncon.app.im.ui.ReSendOnClickListener;
import com.lz.oncon.app.im.ui.view.MiniIlbcPlayerView;
import com.lz.oncon.app.im.ui.view.MsgDeviceView;
import com.lz.oncon.app.im.ui.view.MsgRoundAngleImageView;
import com.lz.oncon.app.im.ui.view.MsgSendStatusView;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.data.AccountData;

public class IMMessageCommonListViewAdapter extends BaseAdapter {
	private ArrayList<SIXmppMessage> mDatas;
	private LayoutInflater mInflater;
	private String mOnconId = "", mNickName;
	private static Context mContext;
	private PersonController mPersonController;
	private IMThreadData.Type threadType;
	private PersonData person;

	public static String audioPath = "";
	private Map<String, MiniIlbcPlayerView> audioMap = Collections.synchronizedMap(new WeakHashMap<String, MiniIlbcPlayerView>());
	OnLongClickListener mOnLongClickListener;
	OnClickListener resendOnClickListener, mOnClickListener;
	private int paddingTop, paddingBottom, paddingShort, paddingLong;

	public Map<String, MiniIlbcPlayerView> getAudioMap() {
		return audioMap;
	}

	public void setAudioMap(Map<String, MiniIlbcPlayerView> audioMap) {
		this.audioMap = audioMap;
	}

	/**
	 * 通用的构造函数
	 * 
	 * @author chenyya
	 * @param context
	 *            必须
	 * @param sIPController
	 *            没有此参数可传null(用于点对点)
	 * @param onconid
	 *            必须
	 * @param nickName
	 *            没有此参数可传null(用于点对点)
	 * @param mDatas
	 *            必须
	 * @param isGroup
	 *            必须,判断是否是圈聊
	 * 
	 */
	public IMMessageCommonListViewAdapter(Context context, String onconid, String nickName, ArrayList<SIXmppMessage> mDatas, IMThreadData.Type threadType) {
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
		mOnconId = onconid;
		this.threadType = threadType;
		if (threadType.ordinal() != IMThreadData.Type.GROUP.ordinal()) {
			mNickName = nickName;
		}
		this.mDatas = mDatas;
		mOnLongClickListener = new MsgOnLongClickListener(mContext, mDatas, mOnconId);
		if (threadType.ordinal() == IMThreadData.Type.GROUP.ordinal()) {
			resendOnClickListener = new ReSendOnClickListener(mContext, mDatas, mOnconId, SIXmppThreadInfo.Type.GROUP);
		}else if(threadType.ordinal() == IMThreadData.Type.BATCH.ordinal()){
			resendOnClickListener = new ReSendOnClickListener(mContext, mDatas, mOnconId, SIXmppThreadInfo.Type.BATCH);
		} else {
			resendOnClickListener = new ReSendOnClickListener(mContext, mDatas, mOnconId, SIXmppThreadInfo.Type.P2P);
		}

		mOnClickListener = new MsgOnClickListener(mContext, mDatas, mNickName, mOnconId);
		
		paddingTop = mContext.getResources().getDimensionPixelSize(R.dimen.app_im_msgbg_padding_top);
		paddingBottom = mContext.getResources().getDimensionPixelSize(R.dimen.app_im_msgbg_padding_bottom);
		paddingShort = mContext.getResources().getDimensionPixelSize(R.dimen.app_im_msgbg_padding_short);
		paddingLong = mContext.getResources().getDimensionPixelSize(R.dimen.app_im_msgbg_padding_long);
		
		mPersonController = new PersonController();
		person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas == null ? null : mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		String leftName = "";
		SIXmppMessage d = mDatas.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.app_im_message_listitem, null);
			holder.messageBg = (LinearLayout) convertView.findViewById(R.id.im_message_listitem_Layout_messagebg);
			holder.message = (LinearLayout) convertView.findViewById(R.id.im_message_listitem_msg);
			holder.errorStatus = (ImageView) convertView.findViewById(R.id.im_message_listitem_status);
			holder.loadingStatus = (ProgressBar) convertView.findViewById(R.id.im_message_listitem_loadingstatus);
			holder.leftHeadImage = (MsgRoundAngleImageView) convertView.findViewById(R.id.im_message_listitem_lefthead);
			holder.rightHeadImage = (MsgRoundAngleImageView) convertView.findViewById(R.id.im_message_listitem_righthead);
			holder.timeTextView = (TextView) convertView.findViewById(R.id.im_message_listitem_TextView_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String time = IMUtil.getMsgTime(d.getTime());
		String preitemtime = "";
		if(position > 0){
			SIXmppMessage pred = mDatas.get(position - 1);
			preitemtime = IMUtil.getMsgTime(pred.getTime());
		}
		if (TextUtils.isEmpty(time)) {
			holder.timeTextView.setVisibility(View.GONE);
		} else if(time.equals(preitemtime)){
			holder.timeTextView.setVisibility(View.GONE);
		}else {
			holder.timeTextView.setText(time);
			holder.timeTextView.setVisibility(View.VISIBLE);
		}
		holder.errorStatus.setVisibility(View.GONE);
		holder.loadingStatus.setVisibility(View.GONE);
		if (d.getContentType() == SIXmppMessage.ContentType.TYPE_INTERCOM 
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_SYSTEM 
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_NEWS
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_VIDEO_CONF) {
			holder.leftHeadImage.setVisibility(View.GONE);
			holder.rightHeadImage.setVisibility(View.GONE);
			holder.messageBg.setGravity(Gravity.CENTER);
			holder.messageBg.setPadding(0, paddingTop, 0, paddingBottom);
		} else {
			if (d.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE) {
				if (threadType.ordinal() == IMThreadData.Type.GROUP.ordinal()) {
					leftName = mPersonController.findNameByMobile(d.getFrom());
				}

				holder.leftHeadImage.setVisibility(View.VISIBLE);
				holder.rightHeadImage.setVisibility(View.GONE);
				holder.leftHeadImage.setMobile(d.getFrom());
				if (threadType.ordinal() == IMThreadData.Type.GROUP.ordinal()) {
					holder.leftHeadImage.setMobile(d.getFrom());
				} else {
					holder.leftHeadImage.setMobile(mOnconId);
				}
				
				holder.messageBg.setGravity(Gravity.LEFT);
				holder.messageBg.setPadding(paddingShort, paddingTop, paddingLong, paddingBottom);
			} else if (d.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE) {
				holder.leftHeadImage.setVisibility(View.GONE);
				holder.rightHeadImage.setVisibility(View.VISIBLE);
				holder.rightHeadImage.setPerson(person.account, person.image);
				holder.messageBg.setGravity(Gravity.RIGHT);
				switch (d.getStatus()) {
				case STATUS_DRAFT:
					holder.loadingStatus.setVisibility(View.VISIBLE);
					break;
				case STATUS_ERROR:
					holder.errorStatus.setVisibility(View.VISIBLE);
					holder.errorStatus.setTag(R.id.tag_position, position);
					holder.errorStatus.setOnClickListener(resendOnClickListener);
					break;
				default:
					break;
				}
				holder.messageBg.setPadding(paddingLong, paddingTop, paddingShort, paddingBottom);
			}
		}

		holder.message.removeAllViews();
		View view = IMMessageFormat.parseMsgView(mContext, d, position);
		if (d.getContentType() == SIXmppMessage.ContentType.TYPE_INTERCOM 
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_SYSTEM 
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_NEWS
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_IMAGE_TEXT
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_2
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_GENERAL
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_LINK_MSG
				|| d.getContentType() == SIXmppMessage.ContentType.TYPE_VIDEO_CONF) {
			view.setBackgroundColor(Color.TRANSPARENT);
		} else if (d.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE) {
			view.setBackgroundResource(R.drawable.bg_msg_income);
		} else if (d.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE) {
			view.setBackgroundResource(R.drawable.bg_msg_outgo);
		}

		view.getBackground().setAlpha(255);
		view.setTag(R.id.tag_position, position);
		view.setOnLongClickListener(mOnLongClickListener);
		if(d.getContentType() != SIXmppMessage.ContentType.TYPE_AUDIO){
			view.setOnClickListener(mOnClickListener);
		}
		holder.message.addView(view);
		if (d.getContentType() == SIXmppMessage.ContentType.TYPE_AUDIO) {
			audioMap.put(d.getAudioPath(), (MiniIlbcPlayerView) view);
		} else if (d.getContentType() == SIXmppMessage.ContentType.TYPE_DYN_EXP) {
			view.getBackground().setAlpha(0);
		}
		// 如果是接受的消息添加文本-显示来自什么设备
		if (d.getContentType() != SIXmppMessage.ContentType.TYPE_INTERCOM 
				&& d.getContentType() != SIXmppMessage.ContentType.TYPE_SYSTEM 
				&& d.getContentType() != SIXmppMessage.ContentType.TYPE_NEWS
				&& d.getContentType() != SIXmppMessage.ContentType.TYPE_VIDEO_CONF) {
			if (d.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE) {// 来源
				if (threadType.ordinal() == IMThreadData.Type.GROUP.ordinal()) {
					MsgDeviceView deviceView = new MsgDeviceView(mContext);
					deviceView.setFromName(leftName);
					deviceView.setMessage(d);
					holder.message.addView(deviceView);
				} else {
					MsgDeviceView deviceView = new MsgDeviceView(mContext);
					deviceView.setMessage(d);
					if (deviceView.hasDeviceType) {
						holder.message.addView(deviceView);
					}
				}
			} else if (d.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE) {
				MsgSendStatusView sendStatusView = new MsgSendStatusView(mContext);
				sendStatusView.setMessage(d);
				if (sendStatusView.hasSendStatus) {
					holder.message.addView(sendStatusView);
				}
			}
		}
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
		super.notifyDataSetChanged();
	}

	static class ViewHolder {
		LinearLayout messageBg;
		LinearLayout message;
		ImageView errorStatus;
		ProgressBar loadingStatus;
		MsgRoundAngleImageView leftHeadImage;
		MsgRoundAngleImageView rightHeadImage;
		TextView timeTextView;
	}

}

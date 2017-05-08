package com.lz.oncon.app.im.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.lb.video.activity.VideoPlayerActivity;
import com.lb.video.job.VideoStartAsyncTask;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.activity.BaiduMapActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.core.im.core.OnconIMMessage;
import com.lz.oncon.application.AppUtil;

public class MsgOnClickListener implements OnClickListener {
	
	private ArrayList<SIXmppMessage> mDatas;
	private Context mContext;
	@SuppressWarnings("unused")
	private String mNickName, mOnconId;
	
	public MsgOnClickListener(Context context, ArrayList<SIXmppMessage> mDatas
			, String mNickName, String mOnconId){
		this.mContext = context;
		this.mDatas = mDatas;
		this.mNickName = mNickName;
		this.mOnconId = mOnconId;
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int position = (Integer) v.getTag(R.id.tag_position);
		SIXmppMessage d = mDatas.get(position);
		switch (d.getContentType()) {
		case TYPE_IMAGE:
			intent = new Intent(mContext, ImageBatchShowActivity.class);
			intent.putExtra("onconId", mOnconId);
			intent.putExtra("msgId", d.getId());
			mContext.startActivity(intent);
			break;
		case TYPE_AUDIO:
			break;
		case TYPE_TALK_PIC:
			break;
		case TYPE_SNAP_PIC:// 闪图
			break;
		case TYPE_LOC:// 位置
			String[] result = IMMessageFormat.getLocString(d.getTextContent());
			if (result != null && !TextUtils.isEmpty(result[0]) && !TextUtils.isEmpty(result[1]) && !TextUtils.isEmpty(result[2])) {
				intent = new Intent(mContext, BaiduMapActivity.class);
				intent.putExtra("longitude", result[1]);
				intent.putExtra("latitude", result[2]);
				intent.putExtra("address", result[0]);
				mContext.startActivity(intent);
			}
			break;
		case TYPE_DYN_EXP:// 动态表情
			String result1 = IMMessageFormat.getFaceName(d.getTextContent());
			if (result1.indexOf(".") != -1) {
				result1 = result1.substring(0, result1.indexOf("."));
			}
			try {
				// resId = rawResourceUtil.getStringIdx(result1);
				intent = new Intent(mContext, GifSHowActivity.class);
				intent.putExtra("mResID", result1);
				mContext.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_FILE:// 文件
			break;
		case TYPE_TEXT://
			break;
		case TYPE_MUSIC: //音乐 
			break;
		case TYPE_HTML_TEXT_2:
			break;
		case TYPE_HTML_TEXT_GENERAL://通用HTML
			break;
		case TYPE_PUBLICACCOUNT_NAMECARD:
			break;
		case TYPE_CUSTOM_PROTOCOL:
			if(d.getTextContent().startsWith("@custom_protocol@:recommand_friend")){
				HashMap<String, String> params = OnconIMMessage.parseCustomProtocol(d.getTextContent());
				PersonController.go2Detail(mContext, params.get("recommandAccount"));
			}else if(d.getTextContent().startsWith("@custom_protocol@:invite_video")){
				HashMap<String, String> params = OnconIMMessage.parseCustomProtocol(d.getTextContent());
				VideoStartAsyncTask task = new VideoStartAsyncTask(mContext , params.get("videoID"));
				AppUtil.execAsyncTask(task);
			}else if(d.getTextContent().startsWith("@custom_protocol@:friend_status")){
				HashMap<String, String> params = OnconIMMessage.parseCustomProtocol(d.getTextContent());
				VideoStartAsyncTask task = new VideoStartAsyncTask(mContext , params.get("videoID"));
				AppUtil.execAsyncTask(task);
			}
			break;
		default:
			break;
		}
	}
}
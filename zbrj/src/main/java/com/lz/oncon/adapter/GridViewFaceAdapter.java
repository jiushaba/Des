package com.lz.oncon.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lb.common.util.ResourceUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppChat;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.GifFaceData;
import com.lz.oncon.data.db.FaceHelper;

public class GridViewFaceAdapter extends BaseAdapter {
	// 定义Context
	private Context mContext;
	// 定义整型数组 即图片源
	private ArrayList<GifFaceData> mImageIds;
	FaceHelper cHelper = null;
	private ArrayList<SIXmppMessage> msgs;
	private SIXmppChat mChat;
	private String mOnconId = "";
	private Handler mHandler;
	private String mobile;
	private String class_name;
	private SIXmppThreadInfo.Type mtype;

	// private LoadImageAysnc loadImageAys=null;

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public GridViewFaceAdapter(Context c, String class_name, ArrayList<SIXmppMessage> msgs, String mobile,SIXmppThreadInfo.Type mtype) {
		this.msgs = msgs;
		mContext = c;
		this.mobile = mobile;
		this.class_name = class_name;
		this.mtype = mtype;
		cHelper = FaceHelper.getInstance(AccountData.getInstance().getUsername());
		mImageIds = cHelper.findByClassNameAndNoClass(class_name);
		GifFaceData gifDataSpec = new GifFaceData();
		gifDataSpec.setSpec(true);
		mImageIds.add(gifDataSpec);
		// loadImageAys = new LoadImageAysnc(c);
	}

	// 获取图片的个数
	public int getCount() {
		if (mImageIds == null) {
			return 0;
		} else {
			return mImageIds.size();
		}
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		if (mImageIds != null && position >= 0 && position < mImageIds.size()) {
			return mImageIds.get(position);
		} else {
			return null;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_gridview_face_listview_item, null);
			holder.head = (ImageView) convertView.findViewById(R.id.item_face_image);
			holder.tv = (TextView) convertView.findViewById(R.id.item_face_image_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GifFaceData select_facedata = mImageIds.get(position);

		if (select_facedata.isSpec()&& position == (mImageIds.size()-1)) {
			holder.head.setImageResource(R.drawable.icon_add);
			holder.tv.setText(R.string.more);
			holder.head.setOnClickListener(mHeadClickListenerA);
		} else {
			String result = select_facedata.getImage_name();
			if (result.indexOf(".") != -1) {
				result = result.substring(0, result.indexOf("."));
			}
			if (select_facedata.getIsdefault() != null && select_facedata.getIsdefault().equals("0")) {
				int resId = 0;
				try {
					resId = ResourceUtil.getRawIdx(result);
					holder.head.setImageResource(resId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				String faceName = result.concat(".").concat(select_facedata.getExtension_name());
				String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(faceName);
				String picRemoteUrl = select_facedata.getSuburl().concat(faceName);
				FaceHelper.loadGifFaceNew(mContext, picRemoteUrl, picLocalPath, faceName, holder.head, select_facedata);
			}
			holder.tv.setText(select_facedata.getImage_des());
			holder.head.setTag(select_facedata);
			holder.head.setOnClickListener(mHeadClickListenerB);
		}

		return convertView;

	}

	class ViewHolder {
		public ImageView head;
		public TextView tv;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public SIXmppChat getmChat() {
		return mChat;
	}

	public void setmChat(SIXmppChat mChat) {
		this.mChat = mChat;
	}

	public String getmOnconId() {
		return mOnconId;
	}

	public void setmOnconId(String mOnconId) {
		this.mOnconId = mOnconId;
	}

	private FaceGroupLister mFaceListener;

	public FaceGroupLister getmFaceListener() {
		return mFaceListener;
	}

	public void setmFaceListener(FaceGroupLister mFaceListener) {
		this.mFaceListener = mFaceListener;
	}

	public interface FaceGroupLister {
		public void faceSendResult();
	}

	private OnClickListener mHeadClickListenerA = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
		}
	};

	final Handler cwjHandler = new Handler();

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			notifyDataSetChanged();
		}
	};

	private OnClickListener mHeadClickListenerB = new OnClickListener() {
		@Override
		public void onClick(View v) {
			playFromRawFile();
			GifFaceData select_facedata = ((GifFaceData) v.getTag());
			if(SIXmppThreadInfo.Type.P2P.ordinal() == mtype.ordinal()){
				if (mChat != null) {
					SIXmppMessage xmppMessage = mChat.sendDynExpMessage(select_facedata.getImage_name().concat(".").concat(select_facedata.getExtension_name()), select_facedata.getImage_des(),mtype);
					ImData.getInstance().addMessageData(mOnconId, xmppMessage);
					msgs.add(xmppMessage);
				}
			}else if(SIXmppThreadInfo.Type.GROUP.ordinal() == mtype.ordinal()){
			}
			if (mFaceListener != null) {
				mFaceListener.faceSendResult();
			}
		}
	};

	private void playFromRawFile() {
		// 使用MediaPlayer.create()获得的
		// MediaPlayer对象默认设置了数据源并初始化完成了
		MediaPlayer player = MediaPlayer.create(mContext, R.raw.msg3);
		player.start();
	}
}

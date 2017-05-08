package com.lz.oncon.app.im.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.lb.common.util.ImageUtil;
import com.lb.video.job.VideoStartAsyncTask;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.api.SIXmppMessage.SendStatus;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.app.im.data.IMNotification;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.data.IMThreadData.Type;
import com.lz.oncon.app.im.data.IMThreadData.VideoStatus;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.app.im.util.SmileUtils;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.widget.HeadImageView;

public class IMListViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private PersonController mPersonController;
	private ArrayList<String> mIndexs;
	LayoutParams wwlp, lp;
	private HashMap<String, IMThreadData> mDatas;

	public IMListViewAdapter(Context context, ArrayList<String> indexs, HashMap<String, IMThreadData> datas) {
		this.mInflater = LayoutInflater.from(context);
		mContext = context;
		mPersonController = new PersonController();
		mIndexs = indexs;
		mDatas = datas;
		int h = ImageUtil.convertDipToPx(mContext, 45);
		int ww = ImageUtil.convertDipToPx(mContext, 45);
		wwlp = new LayoutParams(ww, h);
		lp = new LayoutParams(h, h);
		wwlp.addRule(RelativeLayout.ALIGN_PARENT_TOP); 
		wwlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP); 
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	}

	@Override
	public int getCount() {
		return mIndexs == null ? 0 : mIndexs.size();
	}

	@Override
	public Object getItem(int position) {
		return mIndexs == null ? null : mIndexs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.app_im_thread_listitem, null);
			holder.rootLL = (LinearLayout) convertView.findViewById(R.id.root_LL);
			holder.sendStatus = (ImageView) convertView.findViewById(R.id.sendstatus);
			holder.message = (TextView) convertView.findViewById(R.id.msg);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.headImage = (HeadImageView) convertView.findViewById(R.id.head);
			holder.noti1 = (TextView) convertView.findViewById(R.id.head_noti1);
			holder.noti2 = (TextView) convertView.findViewById(R.id.head_noti2);
			holder.videoStatus = (TextView) convertView.findViewById(R.id.video_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		IMThreadData threadData = mDatas.get(mIndexs.get(position));
		yRefreshView(holder, threadData);
		return convertView;
	}

	static class ViewHolder {
		public TextView name;
		public ImageView sendStatus;
		public TextView message;
		public TextView time;
		public HeadImageView headImage;
		public TextView noti1, noti2;
		public LinearLayout rootLL;
		public TextView videoStatus;
	}

	private void yRefreshView(ViewHolder holder, final IMThreadData threadData){
		if (threadData != null) {
			String id = threadData.getId();
			String name = threadData.getNickName();
			IMNotification notification = IMNotification.getInstance();
			int noticationCount = notification.getNewMessageNoticationCount(id);
			if(ImData.getInstance().hasSetTopChat(id)){
				holder.rootLL.setBackgroundResource(R.drawable.im_thread_list_item_topchat_bg);
			}else{
				holder.rootLL.setBackgroundResource(R.drawable.msg_item_bg);
			}
			if (threadData.getType() == Type.P2P) {
				if(SIXmppThreadInfo.ID_STRANGER.equals(id)){
					name = mContext.getString(R.string.stranger);
					holder.headImage.setImageResource(R.drawable.head_stranger);
				}else if(Constants.NO_900.equals(id)){
					name = mContext.getString(R.string.xuanbotuandui_name);
					holder.headImage.setImageResource(R.drawable.head_900);
				}else if(Constants.NO_901.equals(id)){
					name = mContext.getString(R.string.haibao_name);
					holder.headImage.setImageResource(R.drawable.head_901);
				}else{
					if(TextUtils.isEmpty(threadData.getNickName()) || id.equals(threadData.getNickName())){
						name = mPersonController.findNameByMobile(id);
					}
					holder.headImage.setMobile(id);
				}
				if(threadData.videoStatus.ordinal() == VideoStatus.NONE.ordinal()){
					holder.videoStatus.setVisibility(View.GONE);
				}else{
					holder.videoStatus.setVisibility(View.VISIBLE);
					if(threadData.videoStatus.ordinal() == VideoStatus.LIVING.ordinal()){
						holder.videoStatus.setText(R.string.live_broadcasting);
					}else{
						holder.videoStatus.setText(R.string.watching);
					}
					holder.videoStatus.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							VideoStartAsyncTask task = new VideoStartAsyncTask(mContext , threadData.videoID);
							AppUtil.execAsyncTask(task);
						}
					});
				}
			} else if (threadData.getType() == Type.GROUP) {
			} else if (threadData.getType() == Type.BATCH) {
				//群发
				String[] ids = id.split(",");
				StringBuffer sb = new StringBuffer();
				if(ids != null && ids.length > 0){
					ArrayList<String> members = new ArrayList<String>();
					if(ids != null){
						for(int i=0;i<ids.length && i <4;i++){
							members.add(ids[i]);
						}
					}
					holder.headImage.setGroupId(id, members);
					int idx = 0;
					for(String tempId:ids){
						if(idx > 0){
							sb.append(",");
						}
						sb.append(mPersonController.findNameByMobile(tempId));
						idx ++;
					}
				}
				name = sb.toString();
			}
			if (noticationCount > 0) {
				holder.noti1.setVisibility(View.VISIBLE);
				holder.noti2.setVisibility(View.VISIBLE);
				if(noticationCount <= 99){
					holder.noti2.setText(mContext.getString(R.string.new_msg_noti_fmt1, noticationCount));
//					holder.noti.setLayoutParams(lp);
				}else{
					holder.noti2.setText(mContext.getString(R.string.new_msg_noti_fmt1, "99+"));
//					holder.noti.setLayoutParams(wwlp);
				}
			}else{
				holder.noti1.setVisibility(View.GONE);
				holder.noti2.setVisibility(View.GONE);
			}
			holder.name.setText(name);
			SIXmppMessage theLastMessage = threadData.getMsgs().size() > 0 ? threadData.getMsgs().get(0) : null;
			if (theLastMessage != null) {
				holder.message.setText(SmileUtils.getSmiledText(mContext, IMUtil.getMessageBrief(theLastMessage, mPersonController)));
				holder.time.setText(IMUtil.getArtStringByTime(theLastMessage.getTime()));
				if(SourceType.SEND_MESSAGE.ordinal() == theLastMessage.getSourceType().ordinal()){
					if(SendStatus.STATUS_DRAFT.ordinal() == theLastMessage.getStatus().ordinal()){
						holder.sendStatus.setVisibility(View.VISIBLE);
						holder.sendStatus.setImageResource(R.drawable.ic_msg_state_sending);
					}else if(SendStatus.STATUS_ERROR.ordinal() == theLastMessage.getStatus().ordinal()){
						holder.sendStatus.setVisibility(View.VISIBLE);
						holder.sendStatus.setImageResource(R.drawable.ic_msg_state_failed);
					}else{
						holder.sendStatus.setVisibility(View.GONE);
					}
				}else{
					holder.sendStatus.setVisibility(View.GONE);
				}
			}else{
				holder.message.setText("");
				holder.time.setText("");
			}
		}
	}
}
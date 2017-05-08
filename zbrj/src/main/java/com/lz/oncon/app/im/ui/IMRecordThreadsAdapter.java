package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.IMThreadData.Type;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.app.im.util.SmileUtils;
import com.lz.oncon.widget.HeadImageView;

public class IMRecordThreadsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private PersonController mPersonController;
	private ArrayList<IMThreadData> mDatas;
	LayoutParams wwlp, lp;

	public IMRecordThreadsAdapter(Context context, ArrayList<IMThreadData> datas) {
		this.mInflater = LayoutInflater.from(context);
		mContext = context;
		mPersonController = new PersonController();
		mDatas = datas;
		int h = ImageUtil.convertDipToPx(mContext, 20);
		int ww = ImageUtil.convertDipToPx(mContext, 30);
		wwlp = new LayoutParams(ww, h);
		lp = new LayoutParams(h, h);
		wwlp.addRule(RelativeLayout.ALIGN_PARENT_TOP); 
		wwlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP); 
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
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
		IMThreadData threadData = mDatas.get(position);
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

	private void yRefreshView(ViewHolder holder, IMThreadData threadData){
		if (threadData != null) {
			String id = threadData.getId();
			String name = threadData.getNickName();
			holder.rootLL.setBackgroundResource(R.drawable.msg_item_bg);
			holder.videoStatus.setVisibility(View.GONE);
			if (threadData.getType() == Type.P2P) {
				if(TextUtils.isEmpty(threadData.getNickName()) || id.equals(threadData.getNickName())){
					name = mPersonController.findNameByMobile(id);
				}
				holder.headImage.setMobile(id);
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
			holder.noti1.setVisibility(View.GONE);
			holder.noti2.setVisibility(View.GONE);
			holder.time.setVisibility(View.GONE);
			holder.sendStatus.setVisibility(View.GONE);
			holder.name.setText(name);
			if (threadData.getMsgs().size() == 1) {
				SIXmppMessage theLastMessage = threadData.getMsgs().get(0);
				holder.message.setText(SmileUtils.getSmiledText(mContext, IMUtil.getMessageBrief(theLastMessage, mPersonController)));
			}else{
				holder.message.setText(mContext.getString(R.string.relative_msg_records, threadData.getMsgs().size()));
			}
		}
	}
}
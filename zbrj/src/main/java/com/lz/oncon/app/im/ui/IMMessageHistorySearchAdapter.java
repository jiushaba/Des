package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import android.content.Context;
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
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.app.im.util.SmileUtils;
import com.lz.oncon.widget.HeadImageView;

public class IMMessageHistorySearchAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private PersonController mPersonController;
	LayoutParams wwlp, lp;
	private ArrayList<SIXmppMessage> mDatas;

	public IMMessageHistorySearchAdapter(Context context, ArrayList<SIXmppMessage> datas) {
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
		SIXmppMessage data = mDatas.get(position);
		yRefreshView(holder, data);
		holder.videoStatus.setVisibility(View.GONE);
		holder.noti1.setVisibility(View.GONE);
		holder.noti2.setVisibility(View.GONE);
		holder.sendStatus.setVisibility(View.GONE);
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

	private void yRefreshView(ViewHolder holder, SIXmppMessage msg){
		String id = msg.getFrom();
		String name = mPersonController.findNameByMobile(id);
		holder.headImage.setMobile(id);
		holder.name.setText(name);
		holder.message.setText(SmileUtils.getSmiledText(mContext, IMUtil.getMessageBrief(msg, mPersonController)));
		holder.time.setText(IMUtil.getArtStringByTime(msg.getTime()));
	}
}
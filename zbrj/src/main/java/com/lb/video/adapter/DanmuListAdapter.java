package com.lb.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danmu.data.DanmuContentData;
import com.lb.common.util.AlertDialogListener;
import com.lb.common.util.DisUtil;
import com.lb.common.util.StringUtils;
import com.lb.video.activity.RecordActivity;
import com.lb.video.data.ShareData;
import com.lb.video.util.DialogUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.api.CustomProtocolDealer;
import com.lz.oncon.api.CustomProtocolDealerManager;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.data.AccountData;
/**
 * 弹幕列表adapter
 * @author zhanglijun
 *
 */
public class DanmuListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<DanmuContentData> datas = new java.util.ArrayList<DanmuContentData>();
	private ShareData shareData;
	private PersonController pController = new PersonController();
	public DanmuListAdapter(Context context ,List<DanmuContentData> datas,ShareData shareData ) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		if(datas != null)
			this.datas = datas;
		this.shareData = shareData;
	}
	public DanmuListAdapter(Context context ,ShareData shareData ){
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.shareData = shareData;
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		if(datas.size()>position)
			return datas.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DanmuContentData data = (DanmuContentData)getItem(position);
		if(data == null)
			return null;
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.video_danmu_list_item, null);
			holder = new ViewHolder();
			holder.nick = (TextView) convertView.findViewById(R.id.danmu_list_nick);
			holder.content = (TextView) convertView.findViewById(R.id.danmu_list_content);
			holder.distance = (TextView) convertView.findViewById(R.id.danmu_list_distance);
			holder.time = (TextView) convertView.findViewById(R.id.danmu_list_time);
			holder.kickView = convertView.findViewById(R.id.danmu_list_kick);
			holder.gagView = convertView.findViewById(R.id.danmu_list_gag);
			holder.relative = (ImageView) convertView.findViewById(R.id.danmu_list_relative);
			holder.kickLine = convertView.findViewById(R.id.kick_line);
			holder.gagLine = convertView.findViewById(R.id.gag_line);
			holder.relative.setSelected(true);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nick.setText(data.nick);
		holder.content.setText(data.msg);
		holder.distance.setText(data.distance);
		holder.time.setText(data.time);
		convertView.setTag(holder);
		holder.relative.setSelected(pController.isFriend(data.account));
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				showShareResultDialog(view.getId(),data.account ,data.nick,view);
			}
		};
		
		if(shareData != null){
			holder.kickView.setOnClickListener(clickListener);
			holder.gagView.setOnClickListener(clickListener);
		}else{
			holder.kickView.setVisibility(View.GONE);
			holder.gagView.setVisibility(View.GONE);
			holder.gagLine.setVisibility(View.GONE);
			holder.kickLine.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	/*public void addData(List<DanmuContentData> datas){
		this.datas.addAll(datas);
	}*/
	public void addData(DanmuContentData data){
		datas.add(0,data);
	}
	 
	private void showShareResultDialog(final int actionId,final String account , final String nick,final View view) {
		String text = null;
		switch (actionId) {
		case R.id.danmu_list_kick:
			text = "确定将"+nick+"踢出";
			break;
		case R.id.danmu_list_gag:
			text = "确定将"+nick+"禁言";
		}
		Dialog dialog = DialogUtil.showTipDialog(mContext, text,
				R.string.ok, R.string.cancel, true,
				new AlertDialogListener() {
					@Override
					public void positive() {
						doAction(actionId ,account,view);
					}
					@Override
					public void negative() {
						
					}
				});
	}
	private void doAction(int actionId, String account,View view) {
		switch (actionId) {
		case R.id.danmu_list_kick:
			ImCore.getInstance().getCustomProtocolDealerManager()
					.createDealer(account)
					.kick_off_video(shareData.videoId,shareData.videoTitle);
			view.setSelected(true);
			break;
		case R.id.danmu_list_gag:
			ImCore.getInstance()
					.getCustomProtocolDealerManager()
					.createDealer(account)
					.mute_video(shareData.videoId,shareData.videoTitle);
			view.setSelected(true);
		}
		
	}
	public List<DanmuContentData> getData(){
		return datas;
	}
	public void testData(){
		for(int i=0; i<100 ;i++){
			DanmuContentData contentData = new DanmuContentData();
			contentData.nick=i+"";
			contentData.msg="消息"+i;
			contentData.distance = i+"";
			contentData.time="00:0"+i;
			contentData.account="15910514083";
			addData(contentData);
		}
	}
	static class ViewHolder {
		TextView nick ;
		TextView content ;
		TextView distance ;
		TextView time ;
		View kickView;
		View gagView ;
		View kickLine;
		View gagLine;
		ImageView relative;
	}
	
}

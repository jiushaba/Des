package com.lb.zbrj.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lz.oncon.widget.EllipsizeTextView;
import com.lz.oncon.widget.HeadImageView;

public class FocusListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mList;
	private LayoutInflater layoutInflater;
	private PersonController mPersonController;

	public FocusListAdapter(Context context, List<Object> list) {
		this.mContext = context;
		mList = list;
		layoutInflater = LayoutInflater.from(context);
		mPersonController = new PersonController();
	}

	public List<Object> getList() {
		return mList;
	}

	public void setList(List<Object> list) {
		this.mList = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ChooseViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.activity_fans_list_item, null);
			holder = new ChooseViewHolder();
			holder.signatureLayout = (RelativeLayout) convertView.findViewById(R.id.signatureLayout);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.name);
			holder.iconView = (HeadImageView) convertView.findViewById(R.id.icon);
			holder.sigTV = (EllipsizeTextView) convertView.findViewById(R.id.signatureTV);
			holder.sigTV.setMaxLines(2);
			holder.isFriendV = (ImageView) convertView.findViewById(R.id.isFriend);
			convertView.setTag(holder);
		} else {
			holder = (ChooseViewHolder) convertView.getTag();
		}
		setPerson((FansData)mList.get(position), holder);
		convertView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				PersonController.go2Detail(mContext, ((FansData)mList.get(position)).account);
			}
		});
		return convertView;
	}

	public void addMoreData(List<FansData> videoList) {
		if (null == videoList || videoList.size() == 0) {
			return;
		}
		videoList.remove(0);
		this.mList.addAll(videoList);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null == mList) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	private void setPerson(final FansData data, final ChooseViewHolder holder){
		PersonData person = mPersonController.findPerson(data.account);
		if(TextUtils.isEmpty(person.nickname)){
			if(TextUtils.isEmpty(data.nick)){
				holder.nameTextView.setText(data.account);
			}else{
				holder.nameTextView.setText(data.nick);
			}
		}else{
			holder.nameTextView.setText(person.nickname);
		}
		if(TextUtils.isEmpty(person.image)){
			ImageLoader.displayHeadImage(data.imageurl, holder.iconView);
		}else{
			ImageLoader.displayHeadImage(person.image, holder.iconView);
		}
		holder.signatureLayout.setVisibility(View.INVISIBLE);
		if(!TextUtils.isEmpty(person.label)){
			holder.sigTV.setText(person.label);
			holder.signatureLayout.setVisibility(View.INVISIBLE);
		}else{
			holder.signatureLayout.setVisibility(View.INVISIBLE);
		}
		if(data.isFocused == 1){
			holder.isFriendV.setVisibility(View.VISIBLE);
		}else{
			holder.isFriendV.setVisibility(View.GONE);
		}
	}

	static class ViewHolder {
		HeadImageView avatar;
		ImageView view, recommand;
		TextView name, watch, distance, comment, like, title, live;
	}
	
	static class ChooseViewHolder {
		RelativeLayout signatureLayout;
		public TextView nameTextView;
		public HeadImageView iconView;
		EllipsizeTextView sigTV;
		ImageView isFriendV;
	}
}

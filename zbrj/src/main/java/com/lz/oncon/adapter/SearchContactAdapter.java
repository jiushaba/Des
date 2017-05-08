package com.lz.oncon.adapter;

import java.util.ArrayList;

import com.xuanbo.xuan.R;
import com.lz.oncon.data.FriendData;
import com.lz.oncon.widget.HeadImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchContactAdapter extends BaseAdapter {
	private Context mContext;
	@SuppressWarnings("rawtypes")
	private ArrayList mList;
	public SearchContactAdapter(Context context, @SuppressWarnings("rawtypes") ArrayList list){
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.search_lv_item, null);
			viewHolder = new ViewHolder();
			viewHolder.head_iv = (HeadImageView) convertView.findViewById(R.id.search_head);
			viewHolder.name_tv = (TextView) convertView.findViewById(R.id.search_name);
			viewHolder.search_dep = (TextView) convertView.findViewById(R.id.search_dep);
			viewHolder.search_position = (TextView) convertView.findViewById(R.id.search_position);
			viewHolder.search_group_head_ll = (LinearLayout) convertView.findViewById(R.id.search_group_head_ll);
			viewHolder.headImages = new HeadImageView[4];
			viewHolder.headImages[0] = (HeadImageView) convertView.findViewById(R.id.item_head1);
			viewHolder.headImages[1] = (HeadImageView) convertView.findViewById(R.id.item_head2);
			viewHolder.headImages[2] = (HeadImageView) convertView.findViewById(R.id.item_head3);
			viewHolder.headImages[3] = (HeadImageView) convertView.findViewById(R.id.item_head4);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(mList.get(position) instanceof FriendData){
			viewHolder.head_iv.setVisibility(View.VISIBLE);
			viewHolder.search_group_head_ll.setVisibility(View.GONE);
			FriendData mem = (FriendData)mList.get(position);
			
			viewHolder.name_tv.setText(mem.getContactName());
			viewHolder.search_dep.setText("");
			viewHolder.search_position.setVisibility(View.VISIBLE);
			viewHolder.search_position.setText(mem.getMobile());
			viewHolder.head_iv.setMobile(mem.getMobile());
		}
		return convertView;
	}
	
	static class ViewHolder{
		HeadImageView head_iv;
		TextView name_tv;
		TextView search_dep;
		TextView search_position;
		LinearLayout search_group_head_ll;
		HeadImageView[] headImages;
	}
}
package com.lz.oncon.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.app.im.ui.view.MsgRoundAngleImageView;

public class BlackListAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<String> blackListDatas;
	private PersonController mPersonController;

	public BlackListAdapter(Context context, ArrayList<String> blackListDatas) {
		mContext = context;
		this.blackListDatas = blackListDatas;
		mPersonController = new PersonController();
	}
	
	public void setBlackListDatas(ArrayList<String> blackListDatas) {
		this.blackListDatas = blackListDatas;
	}
	
	@Override
	public int getCount() {
		return blackListDatas.size();
	}

	@Override
	public String getItem(int position) {
		return blackListDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView==null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView=inflater.inflate(R.layout.black_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.head=(MsgRoundAngleImageView) convertView.findViewById(R.id.head);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		String phone = blackListDatas.get(position);
		String name = mPersonController.findNameByMobile(blackListDatas.get(position));
		holder.name.setText(name);
		holder.head.setMobile(phone);
		
		return convertView;
	}
	
	static class ViewHolder {
		MsgRoundAngleImageView head;
		TextView name;
	}
		
		

}

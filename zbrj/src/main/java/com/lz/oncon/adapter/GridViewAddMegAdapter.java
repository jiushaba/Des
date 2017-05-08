package com.lz.oncon.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.app.im.data.IMLargeClass;

public class GridViewAddMegAdapter extends BaseAdapter {
	// 定义Context
	private Context	mContext;
	// 定义整型数组 即图片源
	private ArrayList<IMLargeClass> mImageIds;
	private Handler mHandler;

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public GridViewAddMegAdapter(Context c,ArrayList<IMLargeClass> msgs)
	{
		mContext = c;
		mImageIds = msgs;
	}
	
	// 获取图片的个数
	public int getCount()
	{
		if (mImageIds == null) {
			return 0;
		} else {
			return mImageIds.size();
		}
	}

	// 获取图片在库中的位置
	public IMLargeClass getItem(int position)
	{
		if (mImageIds != null && position >= 0 && position<mImageIds.size()) {
			return mImageIds.get(position);
		} else {
			return null;
		}
	}


	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_gridview_imadd_listview_item,
					null);
			holder.head = (ImageView) convertView
					.findViewById(R.id.im_message__button_class);
			holder.tv = (TextView)convertView.findViewById(R.id.im_message__button_class_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 IMLargeClass select_facedata = mImageIds.get(position);
		 holder.head.setImageResource(select_facedata.getImageResourceId());
		 holder.tv.setText(select_facedata.getTextViewResourceId());
		
		return convertView;
	}
	
	class ViewHolder {
		public ImageView head;
		public TextView tv;
	}

	@Override
	public long getItemId(int position) {
		return mImageIds == null ? 0: mImageIds.get(position).getImageResourceId();
	}
	

}

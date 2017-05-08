package com.lz.oncon.activity.friendcircle;
/**
 * 发送图文中的GridView的adapter
 */
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.fc.selectimage.Bimp;
import com.lz.oncon.activity.fc.selectimage.ImageItem;

public class FriendCircleAddImgAdapter extends BaseAdapter {
	
	public Context mContext;
	public ArrayList<ImageItem> mList;
	public String ONEPLUS = "plus";
	
	public FriendCircleAddImgAdapter(Context context, ArrayList<ImageItem> list){
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList==null?null:mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView==null){
			LayoutInflater in = LayoutInflater.from(mContext);
			convertView = in.inflate(R.layout.friendcircle_txtimg_gridview_item, null);
			vh = new ViewHolder();
			vh.iv = (ImageView) convertView.findViewById(R.id.friendcircle_item_gridview_item_iv);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		if(mList.get(position).imagePath!=null&&mList.get(position).imagePath.equals(ONEPLUS)){
			vh.iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_add));
		}else{
			try {
				if(mList.get(position).imagePath!=null){
					vh.iv.setImageBitmap(Bimp.revitionImageSize(mList.get(position).imagePath));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return convertView;
	}
	
	static class ViewHolder{
		ImageView iv;
	}

}

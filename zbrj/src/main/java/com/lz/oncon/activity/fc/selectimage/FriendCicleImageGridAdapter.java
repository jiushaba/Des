package com.lz.oncon.activity.fc.selectimage;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.friendcircle.image.Fc_PicPreviewOrSelectActivity.TextCallback;

public class FriendCicleImageGridAdapter extends BaseAdapter {

	private TextCallback textcallback = null;
	private Context act;
	private List<ImageItem> dataList;
	private int need_select;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public FriendCicleImageGridAdapter(Context act, List<ImageItem> list,int need_select,ImageLoader imageLoader,DisplayImageOptions options) {
		this.act = act;
		this.dataList = list;
		this.need_select =need_select;
		this.imageLoader = imageLoader;
		this.options = options;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.fc_item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ImageItem item = dataList.get(position);
		holder.iv.setTag(StringUtils.repNull(item.imagePath));
		if (item.isCamera) {
			holder.iv.setImageResource(R.drawable.fc_camera_default);
			holder.selected.setVisibility(View.GONE);
		} else {
			holder.selected.setVisibility(View.VISIBLE);
			holder.iv.setImageResource(R.drawable.fc_select_defalt);
//			loader.displayImage(item.imagePath, item.imagePath, holder.iv, false, "", -1);
			String path_temp = "";
			if(!TextUtils.isEmpty(item.imagePath)){
				if(item.imagePath.indexOf("file:///")<0){
					path_temp = "file:///".concat(item.imagePath);
				}else{
					path_temp = item.imagePath;
				}
			}
			imageLoader.displayImage(path_temp, holder.iv, options);
			if(Fc_PicConstants.fc_selected_Pic_List.containsKey(StringUtils.repNull(item.imagePath))){
				holder.selected.setImageResource(R.drawable.fc_select_image);
				item.isSelected = true;
			} else {
				holder.selected.setImageResource(R.drawable.fc_no_select_image);
				item.isSelected = false;
			}
			holder.selected.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ImageItem item1 = dataList.get(position);
					String path = item1.imagePath;
					if (Fc_PicConstants.fc_selected_Pic_List.size() < need_select) {
						item1.isSelected = !item1.isSelected;
						if (item1.isSelected) {//选中
								holder.selected.setImageResource(R.drawable.fc_select_image);
								Fc_PicConstants.fc_selected_Pic_List.put(path, item1);
								if (textcallback != null)
									textcallback.onListen(Fc_PicConstants.fc_selected_Pic_List.size());
						} else if (!item1.isSelected) {//取消选中
								holder.selected.setImageResource(R.drawable.fc_no_select_image);
								Fc_PicConstants.fc_selected_Pic_List.remove(path);
								if (textcallback != null)
									textcallback.onListen(Fc_PicConstants.fc_selected_Pic_List.size());
						}
					} else if (Fc_PicConstants.fc_selected_Pic_List.size() >= (need_select)) {
						if (item1.isSelected) {
								holder.selected.setImageResource(R.drawable.fc_no_select_image);
								Fc_PicConstants.fc_selected_Pic_List.remove(path);
								if (textcallback != null)
									textcallback.onListen(Fc_PicConstants.fc_selected_Pic_List.size());
						} else {
							if (textcallback != null)
								textcallback.onLimitInfo();
						}
					}
				}

			});
		}
		return convertView;
	}
	
}

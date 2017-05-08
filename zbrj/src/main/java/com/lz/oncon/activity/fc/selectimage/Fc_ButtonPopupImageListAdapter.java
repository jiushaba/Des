package com.lz.oncon.activity.fc.selectimage;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuanbo.xuan.R;

public class Fc_ButtonPopupImageListAdapter extends BaseAdapter {

	Context act;
	List<ImageBucket> dataList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public Fc_ButtonPopupImageListAdapter(Context act, List<ImageBucket> list) {
		this.act = act;
		dataList = list;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.fc_select_defalt)
		.showImageForEmptyUri(R.drawable.fc_select_defalt)
		.showImageOnFail(R.drawable.fc_select_defalt)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public List<ImageBucket> getDataList() {
		return dataList;
	}

	public void setDataList(List<ImageBucket> dataList) {
		this.dataList = dataList;
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
		private ImageView image_bucket;
		private TextView image_list_name;
		private TextView image_list_num;
		private ImageView selected;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(act).inflate(R.layout.fc_popupwindow_image_list, null);
			holder.image_bucket = (ImageView) convertView.findViewById(R.id.image_bucket);
			holder.image_list_name = (TextView) convertView.findViewById(R.id.image_list_name);
			holder.image_list_num = (TextView) convertView.findViewById(R.id.image_list_num);
			holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ImageBucket item = dataList.get(position);
		if(position!=0){
			String count = String.format(act.getString(R.string.fc_piece), item.imageList.size());
			holder.image_list_num.setText("" + count);
		}else{
			holder.image_list_num.setVisibility(View.GONE);
		}
		holder.image_list_name.setText(item.bucketName);
		holder.selected.setVisibility(View.GONE);
		if (item.imageList != null && item.imageList.size() > 0) {
			String sourcePath = "";
			if(position!=0){
				sourcePath = item.imageList.get(0).imagePath;
			}else{
				sourcePath = item.imageList.get(1).imagePath;
			}
			holder.image_bucket.setTag(sourcePath);
			holder.image_bucket.setImageResource(R.drawable.fc_select_defalt);
			String path_temp = "";
			if(!TextUtils.isEmpty(sourcePath)){
				if(sourcePath.indexOf("file:///")<0){
					path_temp = "file:///".concat(sourcePath);
				}else{
					path_temp = sourcePath;
				}
			}
			imageLoader.displayImage(path_temp, holder.image_bucket, options);
		} else {
			holder.image_bucket.setImageBitmap(null);
		}
		return convertView;
	}
}

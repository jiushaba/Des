package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.VideoData;

public class FriendCicleMessageAdapter extends BaseAdapter {
	public Context mContext;
	public ArrayList<VideoData> mList;
	private PersonController mPersonController;

	public FriendCicleMessageAdapter(Context c, ArrayList<VideoData> list) {
		this.mContext = c;
		this.mList = list;
		mPersonController = new PersonController();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			LayoutInflater in = LayoutInflater.from(mContext);
			convertView = in.inflate(R.layout.friendcircle_message_item, null);
			vh = new ViewHolder();
			vh.fc_item_message_head = (com.lz.oncon.widget.HeadImageView) convertView.findViewById(R.id.fc_item_message_head);
			vh.fc_dyminc_name = (TextView) convertView.findViewById(R.id.fc_dyminc_name);
			vh.fc_dyminc_desc = (TextView) convertView.findViewById(R.id.fc_dyminc_desc);
			vh.fc_dyminc_up = (ImageView) convertView.findViewById(R.id.fc_dyminc_up);
			vh.fc_dyminc_time = (TextView) convertView.findViewById(R.id.fc_dyminc_time);
			vh.fc_dyminc_text = (TextView) convertView.findViewById(R.id.fc_dyminc_text);
			vh.fc_dyminc_image = (ImageView) convertView.findViewById(R.id.fc_dyminc_image);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		VideoData fcMessage = mList.get(position);
		String contact_name = "";
		if (!TextUtils.isEmpty(fcMessage.operator)) {
			contact_name = mPersonController.findNameByMobile(fcMessage.operator);
		}
		if (!TextUtils.isEmpty(fcMessage.operator)) {
			vh.fc_item_message_head.setMobile(fcMessage.operator);
		}
		vh.fc_dyminc_name.setText(contact_name);

		if (fcMessage.likes != null && ("3".equals(fcMessage.sub_type) || "4".equals(fcMessage.sub_type))) {
			vh.fc_dyminc_desc.setVisibility(View.GONE);
			vh.fc_dyminc_up.setVisibility(View.VISIBLE);
		} else {
			if (fcMessage.comments != null && fcMessage.comments.size() > 0) {
				vh.fc_dyminc_desc.setVisibility(View.VISIBLE);
				vh.fc_dyminc_up.setVisibility(View.GONE);
				String string = fcMessage.comments.get(0).content;
				vh.fc_dyminc_desc.setText(StringUtils.repNull(string));
			} else if (fcMessage.likes != null && fcMessage.likes.size() > 0) {
				vh.fc_dyminc_desc.setVisibility(View.GONE);
				vh.fc_dyminc_up.setVisibility(View.VISIBLE);
			}
		}
		if (!TextUtils.isEmpty(fcMessage.optime)) {
			vh.fc_dyminc_time.setText(FC_TimeUtils.getNoReadMessagDate(mContext, Long.parseLong(fcMessage.optime)));
		}
//		if (fcMessage.getList_photo() != null && fcMessage.getList_photo().size() > 0) {
//			vh.fc_dyminc_text.setVisibility(View.GONE);
//			vh.fc_dyminc_image.setVisibility(View.VISIBLE);
//			String imageUrl = Constants.URL_FILE_SERVICE_TEST + fcMessage.getList_photo().get(0).getMid();
//			final String imagePath = Constants.FRIENDCIRCLE_IMGPATH + fcMessage.getList_photo().get(0).getMid();
//			// 加载小图
//			try {
//				AsyncImageLoader asyncImageLoader = AsyncImageLoader.getInstance();
//				if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(fcMessage.getList_photo().get(0).getMid())) {
//					asyncImageLoader.loadDrawable(imageUrl, imagePath, new ImageCallback() {
//						@Override
//						public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//							if (imageDrawable != null) {
//								Bitmap b = ((BitmapDrawable) imageDrawable).getBitmap();
//								vh.fc_dyminc_image.setImageBitmap(b);
//							} else {
//								vh.fc_dyminc_image.setImageResource(R.drawable.defaultpic);
//							}
//						}
//					});
//				} else {
//					vh.fc_dyminc_image.setImageResource(R.drawable.defaultpic);
//				}
//			} catch (Exception e) {
//			}
//		} else 
			if (!TextUtils.isEmpty(fcMessage.title)) {
			vh.fc_dyminc_text.setVisibility(View.VISIBLE);
			vh.fc_dyminc_image.setVisibility(View.GONE);
			vh.fc_dyminc_text.setText(fcMessage.title);
		}
		return convertView;
	}

	static class ViewHolder {
		com.lz.oncon.widget.HeadImageView fc_item_message_head;// 操作者的头像
		TextView fc_dyminc_name;// 操作者的名字
		TextView fc_dyminc_desc;// 如果回复，显示文本
		ImageView fc_dyminc_up;// 否则显示点赞
		TextView fc_dyminc_time;// 操作者时间
		TextView fc_dyminc_text;// 帖子原文
		ImageView fc_dyminc_image;// 别人的头像

	}

}

package com.lz.oncon.activity.friendcircle;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lb.common.util.ImageLoader;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.ReplyData;
import com.lb.zbrj.data.VideoData;

public class MsgHistoryListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mList;
	private LayoutInflater layoutInflater;
	private PersonController mPersonController;
	
	public MsgHistoryListAdapter(Context context, List<Object> list) {
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
		final ViewHolder vh;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.friendcircle_message_item, null);
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
		ReplyData fcMessage = (ReplyData)mList.get(position);
		String contact_name = "";
		if (!TextUtils.isEmpty(fcMessage.account)) {
			contact_name = mPersonController.findNameByMobile(fcMessage.account);
		}
		if (!TextUtils.isEmpty(fcMessage.account)) {
			vh.fc_item_message_head.setMobile(fcMessage.account);
		}
		vh.fc_dyminc_name.setText(contact_name);

//		if (fcMessage.likes != null && ("3".equals(fcMessage.sub_type) || "4".equals(fcMessage.sub_type))) {
//			vh.fc_dyminc_desc.setVisibility(View.GONE);
//			vh.fc_dyminc_up.setVisibility(View.VISIBLE);
//		} else {
//			if (fcMessage.comments != null && fcMessage.comments.size() > 0) {
				vh.fc_dyminc_desc.setVisibility(View.VISIBLE);
				vh.fc_dyminc_up.setVisibility(View.GONE);
//				String string = fcMessage.comments.get(0).content;
				vh.fc_dyminc_desc.setText(StringUtils.repNull(fcMessage.msg));
//			} else if (fcMessage.likes != null && fcMessage.likes.size() > 0) {
//				vh.fc_dyminc_desc.setVisibility(View.GONE);
//				vh.fc_dyminc_up.setVisibility(View.VISIBLE);
//			}
//		}
				//FIXME 缺少时间
//		if (!TextUtils.isEmpty(fcMessage.optime)) {
//			vh.fc_dyminc_time.setText(FC_TimeUtils.getNoReadMessagDate(mContext, Long.parseLong(fcMessage.optime)));
//		}
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
//			if (!TextUtils.isEmpty(fcMessage.title)) {
//			vh.fc_dyminc_text.setVisibility(View.VISIBLE);
				ImageLoader.displayPicImage(fcMessage.imageurl, vh.fc_dyminc_image);
				vh.fc_dyminc_image.setVisibility(View.VISIBLE);
//			vh.fc_dyminc_text.setText(fcMessage.title);
//		}
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

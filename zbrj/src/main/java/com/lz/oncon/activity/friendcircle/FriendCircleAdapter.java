package com.lz.oncon.activity.friendcircle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lb.common.util.Clipboard;
import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.ImageLoader;
import com.lb.video.activity.VideoPlayerActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.CommentData;
import com.lb.zbrj.data.LikeData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity.ShowCommentLayoutInterface;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HeadImageView;

public class FriendCircleAdapter extends BaseAdapter {
	public Context mContext;
	public ArrayList<VideoData> mList;
	public FriendCircleItemGridViewAdapter adapter;
	public ShowCommentLayoutInterface mSci;
	private Handler yHandler;
	public LayoutInflater in_up;
	public View view_up;
	private String mobile;
	private NetIF_ZBRJ ni;

	public FriendCircleAdapter(Context c, ArrayList<VideoData> list, String mobile
			, Handler yHandler, ShowCommentLayoutInterface sci) {
		this.mContext = c;
		this.mList = list;
		this.mobile = mobile;
		this.mSci = sci;
		this.yHandler = yHandler;
		ni = new NetIF_ZBRJ(mContext);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			LayoutInflater in = LayoutInflater.from(mContext);
			convertView = in.inflate(R.layout.fc_activity_friendcircle_item, null);
			vh = new ViewHolder();

			vh.fc_item_avatar = (HeadImageView) convertView.findViewById(R.id.fc_item_avatar);
			vh.fc_item_username = (TextView) convertView.findViewById(R.id.fc_item_username);
			vh.fc_item_title = (TextView) convertView.findViewById(R.id.fc_item_title);
			vh.fc_item_txtContent = (TextView) convertView.findViewById(R.id.fc_item_txtContent);
			vh.fc_item_videoview = (ImageView) convertView.findViewById(R.id.fc_item_videoview);
			vh.fc_item_gridview = (GridView) convertView.findViewById(R.id.fc_item_gridview);
			vh.fc_item_time = (TextView) convertView.findViewById(R.id.fc_item_time);
			vh.fc_item_delete = (TextView) convertView.findViewById(R.id.fc_item_delete);
			vh.fc_item_commentANDup_popup = (TextView) convertView.findViewById(R.id.fc_item_commentANDup_popup);
			vh.fc_item_comment_ll = (LinearLayout) convertView.findViewById(R.id.fc_item_comment_ll);
			vh.fc_item_up_ll = (LinearLayout) convertView.findViewById(R.id.fc_item_up_ll);
			vh.fc_item_layout = (LinearLayout) convertView.findViewById(R.id.fc_item_layout);
			vh.fc_share_layout = (LinearLayout) convertView.findViewById(R.id.fc_share_layout);
			vh.share_tv = (TextView) convertView.findViewById(R.id.share_tv);
			vh.month_v = (TextView) convertView.findViewById(R.id.month_v);
			vh.type_v = (ImageView) convertView.findViewById(R.id.fc_item_typeview);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		// 获取每条的动态数据
		final VideoData dynamic = mList.get(position);
		vh.fc_item_title.setText(dynamic.title);
//		String contact_name = person.nickname;
		// 头像
//		ImageLoader.getInstance().displayImage(person.image, vh.fc_item_avatar, MyApplication.getInstance().avatarOptions);
//		if (!TextUtils.isEmpty(contact_name)) {
//			vh.fc_item_avatar.setOnClickListener(new HeadImageListener(mContext, contact_name, dynamic.account));
//		}
		// 姓名
//		if (!TextUtils.isEmpty(contact_name)) {
//			vh.fc_item_username.setText(contact_name);
//			vh.fc_item_username.setOnClickListener(new HeadImageListener(mContext, contact_name, dynamic.account));
//		} else {
//			vh.fc_item_username.setText(dynamic.account);
//		}
//		vh.fc_item_username.setTextColor(mContext.getResources().getColor(R.color.friendc_textlink_color));
		vh.fc_share_layout.removeAllViews();
//		if ("2".equals(dynamic.getPostType())) {// 1为普通图文2为分享图文
//			vh.share_tv.setVisibility(View.VISIBLE);
//			vh.fc_share_layout.setVisibility(View.VISIBLE);
//			vh.fc_item_txtContent.setVisibility(View.GONE);
//			Fc_shareToFriends fc_share = new Fc_shareToFriends(mContext);
//			fc_share.setValue(dynamic.getDetail(), dynamic.getIcon(), dynamic.getShareContent(), dynamic.getLink(),dynamic.getTitle());
//			vh.fc_share_layout.addView(fc_share);
//		} else {
			vh.share_tv.setVisibility(View.GONE);
//		}
		// 动态文本
//		if (TextUtils.isEmpty(dynamic.getShareContent())&&!TextUtils.isEmpty(dynamic.getDetail())) {//分享内容概要 | 详细(博客内容)
//			try {
//				vh.fc_item_txtContent.setVisibility(View.VISIBLE);
//				String srcString = StringUtils.repNull(dynamic.getDetail());
//				vh.fc_item_txtContent.setText(Fc_TextToAutoLin.getFc_TextToAutoLin(srcString));
//				vh.fc_item_txtContent.setMovementMethod(LinkMovementMethod.getInstance());
//				vh.fc_item_txtContent.setOnLongClickListener(new FriendCircle_comment_Listener(srcString));
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
			vh.fc_item_txtContent.setVisibility(View.GONE);
//		}
		if (!TextUtils.isEmpty(dynamic.dateTime)) {
			Date date = DateUtil.getDateTime(dynamic.dateTime, "-", ":");
			if(date == null){
				vh.fc_item_time.setText("");
			}else{
				vh.fc_item_time.setText(IMUtil.getVideoTime2(date.getTime()));
			}
		}
		if(position > 0 && DateUtil.isSameYM(dynamic.dateTime, mList.get(position - 1).dateTime)){
			vh.month_v.setVisibility(View.INVISIBLE);
		}else{
			vh.month_v.setVisibility(View.VISIBLE);
			Date date = DateUtil.getDateTime(dynamic.dateTime, "-", ":");
			if(date == null){
				vh.month_v.setText("");
			}else{
				vh.month_v.setText(IMUtil.getVideoMonth(date.getTime()));
			}
		}
		//暂时不能删除动态
		if (mobile.equals(AccountData.getInstance().getBindphonenumber())) {
			vh.fc_item_delete.setVisibility(View.VISIBLE);
			vh.fc_item_delete.setOnClickListener(new ItemDeleteDynamic(mContext, dynamic.videoID, position));
		} else {
			vh.fc_item_delete.setVisibility(View.INVISIBLE);
		}

		// 图片
		LayoutParams params;
		int height = 0;
//		if (TextUtils.isEmpty(dynamic.videoImage)) {
//			height = 0;
//			vh.fc_item_videoview.setVisibility(View.GONE);
//			vh.fc_item_gridview.setVisibility(View.GONE);
//		} else {
			vh.fc_item_videoview.setVisibility(View.VISIBLE);
//			vh.fc_item_gridview.setVisibility(View.VISIBLE);
//			if (dynamic.getList_photo().size() == 1) {
//				height = LayoutParams.WRAP_CONTENT;
//			} else if (dynamic.getList_photo().size() <= 3) {
//				height = DensityUtil.Dp2Px(mContext, 110);
//			} else if (dynamic.getList_photo().size() > 3 && dynamic.getList_photo().size() <= 6) {
//				height = DensityUtil.Dp2Px(mContext, 112 * 2);
//			} else if (dynamic.getList_photo().size() > 6) {
//				height = DensityUtil.Dp2Px(mContext, 112 * 3);
//			}
			height = mContext.getResources().getDimensionPixelSize(R.dimen.fc_videoview_height);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
//			params = new LayoutParams(LayoutParams.WRAP_CONTENT, height);
//			vh.fc_item_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
			vh.fc_item_videoview.setLayoutParams(params);
//			vh.fc_item_gridview.setLayoutParams(params);
			ImageLoader.displayPicImage(dynamic.videoImage, vh.fc_item_videoview);
			vh.fc_item_videoview.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					VideoPlayerActivity.start(mContext, dynamic);
				}
			});
//			ArrayList<String> images = new ArrayList<String>();
//			images.add(dynamic.imageUrl);
//			adapter = new FriendCircleItemGridViewAdapter(mContext, images);
//			vh.fc_item_gridview.setAdapter(adapter);
//		}
		vh.fc_item_gridview.setOnItemClickListener(new GridViewListener(mContext, dynamic));

		// 评论
		ArrayList<CommentData> comments = dynamic.comments;
		//点赞
		ArrayList<LikeData> ups = dynamic.likes;
		vh.fc_item_comment_ll.removeAllViews();
		if (comments != null) {
			if (comments.size() > 0) {
				vh.fc_item_layout.setVisibility(View.VISIBLE);
				vh.fc_item_comment_ll.setVisibility(View.VISIBLE);
			} else {
				if (ups == null || ups.size() == 0) {
					vh.fc_item_layout.setVisibility(View.GONE);
				}
				vh.fc_item_comment_ll.setVisibility(View.GONE);
			}
			for (int i = (comments.size() - 1); i >= 0; i--) {
				addComment(comments.get(i), vh, position);
			}
			showMoreComments(dynamic, vh, position);
		} else {
			vh.fc_item_layout.setVisibility(View.GONE);
		}
		// 点赞
		boolean onlyOne = true;
		boolean isUp = false;

		in_up = LayoutInflater.from(mContext);
		vh.fc_item_up_ll.removeAllViews();
		if (ups != null) {
			if (ups.size() > 0) {
				vh.fc_item_layout.setVisibility(View.VISIBLE);
				vh.fc_item_up_ll.setVisibility(View.VISIBLE);
			} else {
				if (comments == null || (comments != null && comments.size() == 0)) {
					vh.fc_item_layout.setVisibility(View.GONE);
				}
				vh.fc_item_up_ll.setVisibility(View.GONE);
			}
			String name = "";
			String name_up = "";
			FeedLikeUserLayout cl = null;
			cl = new FeedLikeUserLayout(mContext);
			for (int i = (ups.size() - 1); i >= 0; i--) {
				if (!TextUtils.isEmpty(ups.get(i).likeAccount)) {
					name = ups.get(i).nick;
					if (!TextUtils.isEmpty(name)) {
						name_up += (name);
						if (i == 0){
							cl.setValue(name, mobile, ups.get(i).likeAccount, false);
						} else {
							cl.setValue(name, mobile, ups.get(i).likeAccount, true);
						}
					}

					// 判断是否被点赞
					if (onlyOne) {
						if (AccountData.getInstance().getBindphonenumber().equals(ups.get(i).likeAccount)) {
							isUp = true;
							onlyOne = false;
						} else {
							isUp = false;
						}
					}
				}
			}
			if (cl != null) {
				vh.fc_item_up_ll.addView(cl);
			}
			if (!TextUtils.isEmpty(name_up)) {
				// view_up = in_up.inflate(R.layout.fc_up_layout, null);
				// vh.fc_item_up_ll.addView(view_up, 0);
				vh.fc_item_up_ll.setVisibility(View.VISIBLE);
				vh.fc_item_layout.setVisibility(View.VISIBLE);
			} else {
				vh.fc_item_up_ll.setVisibility(View.GONE);
			}
		} else {
			vh.fc_item_up_ll.setVisibility(View.GONE);
		}

		// 评论点赞pop
		MyListener ml = new MyListener(mContext, mSci, position, dynamic.videoID, isUp);
		vh.fc_item_commentANDup_popup.setOnClickListener(ml);

		return convertView;
	}

	static class ViewHolder {
		HeadImageView fc_item_avatar;
		TextView fc_item_username;
		TextView fc_item_title;
		TextView fc_item_txtContent;
		ImageView fc_item_videoview;
		GridView fc_item_gridview;
		TextView fc_item_time;
		TextView fc_item_delete;
		TextView fc_item_commentANDup_popup;
		LinearLayout fc_item_comment_ll;
		LinearLayout fc_item_up_ll;
		LinearLayout fc_item_layout;
		LinearLayout fc_share_layout;
		TextView share_tv;
		TextView month_v;
		ImageView type_v;
	}

	/**
	 * 删除 动态
	 * 
	 * @param feedId
	 */
	public void delDynamic(final String feedId, final int position) {
		try {
			new NetIFUI_ZBRJ(mContext, new NetInterfaceListener() {
				@Override
				public void finish(NetInterfaceStatusDataStruct nsdf) {
					if (nsdf != null && nsdf.getStatus() != null) {
						if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) { // success
							Message m = new Message();
							mList.remove(position);
							m.what = FriendCircleActivity.DEL_DYNAMIC_SUC;
							yHandler.sendMessage(m);
						} else { // fails errorCode
							yHandler.sendEmptyMessage(FriendCircleActivity.DEL_DYNAMIC_FAIL);
						}
					} else { // other error
						yHandler.sendEmptyMessage(FriendCircleActivity.DEL_DYNAMIC_FAIL);
					}
				}
			}).m1_cancel_save(feedId);

		} catch (Exception e) {
			e.getStackTrace();
			yHandler.sendEmptyMessage(FriendCircleActivity.DEL_DYNAMIC_FAIL);
		}
	}

	/**
	 * 删除动态
	 * 
	 * @author Administrator
	 * 
	 */
	public class ItemDeleteDynamic implements OnClickListener {
		private String feedId;
		private Context mc;
		private int position;

		public ItemDeleteDynamic(Context c, String feedId, int position) {
			this.mc = c;
			this.feedId = feedId;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(mc).setTitle(mc.getString(R.string.memo)).setPositiveButton(mc.getString(R.string.fc_confirm), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					delDynamic(feedId, position);
				}
			}).setNegativeButton(mc.getString(R.string.fc_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setMessage(mc.getString(R.string.fc_delete_dynamic)).show();
		}

	}

	private void choiceGroupType(final String s) {
		String[] groupTypes1 = { mContext.getResources().getString(R.string.fc_del_dynamic_copy) };
		new AlertDialog.Builder(mContext).setItems(groupTypes1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Clipboard.setText(mContext, s);
			}
		}).show();

	}

	public class FriendCircle_comment_Listener implements OnLongClickListener {
		private String string;

		public FriendCircle_comment_Listener(String s) {
			this.string = s;
		}

		@Override
		public boolean onLongClick(View v) {
			choiceGroupType(string);
			return false;
		}

	}
	
	private void addComment(CommentData comment, ViewHolder vh, int position){
		String name_comment;
		String replay_name = "";
		String replay_mobile = "";
		if (!TextUtils.isEmpty(comment.commentAccount)) {
			name_comment = comment.nick;
			if (!TextUtils.isEmpty(comment.commentToAccount)) {
				replay_mobile = comment.commentToAccount;
				replay_name = comment.commentToNick;
			}
			if (!TextUtils.isEmpty(name_comment)) {
				try {
					CommentLayout cl = new CommentLayout(mContext, mSci, position);
					cl.setValue(name_comment, URLDecoder.decode(comment.content, "utf-8"), comment.commentAccount, comment.commentID
							, comment.type, replay_name, replay_mobile);
					vh.fc_item_comment_ll.addView(cl);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void showMoreComments(final VideoData dynamic, final ViewHolder vh, final int position){
		if(!dynamic.isShowAllComment
				&& dynamic.comments.size() == 10){//
			final ImageView iv = new ImageView(mContext);
			iv.setImageResource(R.drawable.icon_blue_arrow_down);
			iv.setTag(R.id.expand);
			vh.fc_item_comment_ll.addView(iv);
			dynamic.expandCommentIdx = 10;
			iv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					vh.fc_item_comment_ll.removeView(iv);
					new Thread(){
						public void run(){
							final NetInterfaceStatusDataStruct result = ni.m1_get_comment(dynamic.videoID);
							if(Constants.RES_SUCCESS.equals(result.getStatus())){
								((BaseActivity)mContext).runOnUiThread(new Runnable(){
									@SuppressWarnings("unchecked")
									public void run(){
										if(result.getObj() != null
												&& result.getObj() instanceof ArrayList
												&& ((ArrayList<CommentData>)result.getObj()).size() > 10){
											ArrayList<CommentData> list = (ArrayList<CommentData>)result.getObj();
											for(int i=10;i<list.size();i++){
												dynamic.comments.add(list.get(i));
												addComment(list.get(i), vh, position);
											}
										}
									}
								});
								dynamic.isShowAllComment = true;
							}else{
								((BaseActivity)mContext).runOnUiThread(new Runnable(){
									public void run(){
										vh.fc_item_comment_ll.addView(iv);
										((BaseActivity)mContext).toastToMessage(mContext.getString(R.string.query_fail));
									}
								});
							}
						}
					}.start();
				}
			});
		}
	}
}

package com.lz.oncon.activity.friendcircle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.CommentData;
import com.lb.zbrj.data.LikeData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.common.util.Clipboard;
import com.lb.common.util.StringUtils;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity.ShowCommentLayoutInterface;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HeadImageView;

public class FriendCircleMessageDetailActivity extends BaseActivity implements OnItemClickListener, ShowCommentLayoutInterface, SynPersonInfoListener {

	private HeadImageView fc_item_avatar;
	private TextView fc_item_username;
	private TextView fc_item_txtContent;
	private GridView fc_item_gridview;
	private TextView fc_item_time;
	private TextView fc_item_delete;
	private TextView fc_item_commentANDup_popup;
	private LinearLayout fc_item_comment_ll;
	private LinearLayout fc_item_up_ll;
	private LinearLayout fc_item_layout;
	private LinearLayout fc_share_layout;
	private TextView share_tv;

	private EditText et_content;
	private Button btn_send;

	private VideoData dynamic = null;
	private PersonController mPersonController;
	private PersonData person;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initController();
		initViews();
		setValues();
		setListeners();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		MyApplication.getInstance().removeListener(Constants.LISTENER_SYN_PERSONINFO, this);
	}

	public void initContentView() {
		setContentView(R.layout.fc_activity_friendcircle_detailmessage);
	}

	public void initController() {
		mPersonController = new PersonController();
	}

	public void initViews() {
		fc_item_avatar = (HeadImageView) findViewById(R.id.fc_item_avatar);
		fc_item_username = (TextView) findViewById(R.id.fc_item_username);
		fc_item_txtContent = (TextView) findViewById(R.id.fc_item_txtContent);
		fc_item_gridview = (GridView) findViewById(R.id.fc_item_gridview);
		fc_item_time = (TextView) findViewById(R.id.fc_item_time);
		fc_item_delete = (TextView) findViewById(R.id.fc_item_delete);
		fc_item_commentANDup_popup = (TextView) findViewById(R.id.fc_item_commentANDup_popup);
		fc_item_comment_ll = (LinearLayout) findViewById(R.id.fc_item_comment_ll);
		fc_item_up_ll = (LinearLayout) findViewById(R.id.fc_item_up_ll);
		fc_item_layout = (LinearLayout) findViewById(R.id.fc_item_layout);
		et_content = (EditText) findViewById(R.id.friendcircle_comment_rl_et);
		btn_send = (Button) findViewById(R.id.friendcircle_comment_rl_btn);
		fc_share_layout = (LinearLayout) findViewById(R.id.fc_share_layout);
		share_tv = (TextView) findViewById(R.id.share_tv);
		btn_send.setOnClickListener(new MyOnClickListener(-1, ""));
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			dynamic = (VideoData) bundle.getSerializable("dynamic");
			phone = getIntent().hasExtra("mobile") ? getIntent().getStringExtra("mobile") : AccountData.getInstance().getBindphonenumber();
			person = mPersonController.findPerson(phone);
		}
	}
	
	private void setPersonInfo(){
		String contact_name = mPersonController.findNameByMobile(person.account);
		// 头像
		fc_item_avatar.setPerson(person.account, person.image);
		if (!TextUtils.isEmpty(contact_name)) {
			fc_item_avatar.setOnClickListener(new HeadImageListener(FriendCircleMessageDetailActivity.this, contact_name, phone));
		}
		// 姓名
		if (!TextUtils.isEmpty(contact_name)) {
			fc_item_username.setText(contact_name);
			fc_item_username.setOnClickListener(new HeadImageListener(FriendCircleMessageDetailActivity.this, contact_name, phone));
		} else {
			fc_item_username.setText(phone);
		}
	}

	public void setValues() {
		if (dynamic != null) {
			setPersonInfo();
			fc_share_layout.removeAllViews();
//			if ("2".equals(StringUtils.repNull(dynamic.getPostType()))) {// 分享
//				share_tv.setVisibility(View.VISIBLE);
//				fc_share_layout.setVisibility(View.VISIBLE);
//				fc_item_txtContent.setVisibility(View.GONE);
//				Fc_shareToFriends fc_share = new Fc_shareToFriends(FriendCircleMessageDetailActivity.this);
//				fc_share.setValue(dynamic.getDetail(), dynamic.getIcon(), dynamic.getShareContent(), dynamic.getLink(),dynamic.getTitle());
//				fc_share_layout.addView(fc_share);
//			} else {
				share_tv.setVisibility(View.GONE);
//			}
			
			// 动态文本
//			if (TextUtils.isEmpty(dynamic.getShareContent())&&!TextUtils.isEmpty(dynamic.getDetail())) {
//				try {
//					fc_item_txtContent.setVisibility(View.VISIBLE);
//					String srcString = StringUtils.repNull(dynamic.getDetail());
//					fc_item_txtContent.setText(Fc_TextToAutoLin.getFc_TextToAutoLin(srcString));
//					fc_item_txtContent.setMovementMethod(LinkMovementMethod.getInstance());
//					fc_item_txtContent.setOnLongClickListener(new FriendCircle_comment_Listener(srcString));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} else {
				fc_item_txtContent.setVisibility(View.GONE);
//			}

			if (!TextUtils.isEmpty(dynamic.dateTime)) {
				fc_item_time.setText(FC_TimeUtils.getTime(FriendCircleMessageDetailActivity.this, Long.valueOf(dynamic.dateTime)));
			}
			//FIXME 不能删除动态
//			if (person.account.equals(AccountData.getInstance().getBindphonenumber())) {
//				fc_item_delete.setVisibility(View.VISIBLE);
//				fc_item_delete.setOnClickListener(new ItemDeleteDynamic(FriendCircleMessageDetailActivity.this, dynamic.videoID));
//			} else {
				fc_item_delete.setVisibility(View.GONE);
//			}

			// 图片
			LayoutParams params;
			int height = 0;
			if (TextUtils.isEmpty(dynamic.videoImage)) {
				height = 0;
				fc_item_gridview.setVisibility(View.GONE);
			} else {
				fc_item_gridview.setVisibility(View.VISIBLE);
//				if (dynamic.getList_photo().size() == 1) {
					height = LayoutParams.WRAP_CONTENT;
//				}
//				else if (dynamic.getList_photo().size() <= 3) {
//					height = DensityUtil.Dp2Px(FriendCircleMessageDetailActivity.this, 110);
//				} else if (dynamic.getList_photo().size() > 3 && dynamic.getList_photo().size() <= 6) {
//					height = DensityUtil.Dp2Px(FriendCircleMessageDetailActivity.this, 112 * 2);
//				} else if (dynamic.getList_photo().size() > 6) {
//					height = DensityUtil.Dp2Px(FriendCircleMessageDetailActivity.this, 112 * 3);
//				}
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, height);
				fc_item_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
				fc_item_gridview.setLayoutParams(params);
				ArrayList<String> images = new ArrayList<String>();
				images.add(dynamic.imageUrl);
				FriendCircleItemGridViewAdapter adapter = new FriendCircleItemGridViewAdapter(FriendCircleMessageDetailActivity.this, images);
				fc_item_gridview.setAdapter(adapter);
				fc_item_gridview.setOnItemClickListener(new GridViewListener(FriendCircleMessageDetailActivity.this, dynamic));
			}

			// 评论
			ArrayList<CommentData> comments = dynamic.comments;
			ArrayList<LikeData> ups = dynamic.likes;
			fc_item_comment_ll.removeAllViews();
			if (comments != null) {
				if (comments.size() > 0) {
					fc_item_layout.setVisibility(View.VISIBLE);
					fc_item_comment_ll.setVisibility(View.VISIBLE);
				} else {
					if (ups.size() == 0) {
						fc_item_layout.setVisibility(View.GONE);
					}
					fc_item_comment_ll.setVisibility(View.GONE);
				}
				String name_comment;
				String replay_name = "";
				String replay_mobile = "";
				for (int i = (comments.size()-1); i >= 0; i--) {
					if (!TextUtils.isEmpty(comments.get(i).commentAccount)) {
						name_comment = comments.get(i).nick;
						if (!TextUtils.isEmpty(comments.get(i).commentToAccount)) {
							replay_mobile = comments.get(i).commentToAccount;
							replay_name = comments.get(i).commentToNick;
						}
						if (!TextUtils.isEmpty(name_comment)) {
							try {
								CommentDeailLayout cl = new CommentDeailLayout(FriendCircleMessageDetailActivity.this, this);
								boolean isShowType = false;
								if(i == (comments.size()-1)){
									isShowType = true;
								}else{
									isShowType = false;
								}
								cl.setValue(name_comment, URLDecoder.decode(comments.get(i).content, "utf-8")
										, StringUtils.repNull(comments.get(i).commentAccount)
										, StringUtils.repNull(comments.get(i).commentID)
										, comments.get(i).type + "", replay_name, replay_mobile
										, ""
//										, StringUtils.repNull(comments.get(i).getCreateTime())
										, isShowType,i);
								fc_item_comment_ll.addView(cl);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				fc_item_layout.setVisibility(View.GONE);
			}

			// 点赞
			boolean onlyOne = true;
			boolean isUp = false;

			LayoutInflater in_up = LayoutInflater.from(FriendCircleMessageDetailActivity.this);
			fc_item_up_ll.removeAllViews();
			if (ups != null) {
				if (ups.size() > 0) {
					fc_item_layout.setVisibility(View.VISIBLE);
					fc_item_up_ll.setVisibility(View.VISIBLE);
				} else {
					if (comments==null||(comments!=null&&comments.size() == 0)) {
						fc_item_layout.setVisibility(View.GONE);
					}
					fc_item_up_ll.setVisibility(View.GONE);
				}
				String name = "";
				String name_up = "";
				FeedLikeUserDetailLayout cl = null;
				cl = new FeedLikeUserDetailLayout(FriendCircleMessageDetailActivity.this);
				for (int i = (ups.size()-1); i >= 0; i--) {
					if (!TextUtils.isEmpty(ups.get(i).likeAccount)) {
						name = ups.get(i).nick;
						if (!TextUtils.isEmpty(name)) {
							name_up += (name);
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
				cl.setValue(ups);
				if (cl != null) {
					fc_item_up_ll.addView(cl);
				}
				if (!TextUtils.isEmpty(name_up)) {
					fc_item_up_ll.setVisibility(View.VISIBLE);
					fc_item_layout.setVisibility(View.VISIBLE);
				} else {
					fc_item_up_ll.setVisibility(View.GONE);
				}
			} else {
				fc_item_up_ll.setVisibility(View.GONE);
			}

			// 评论点赞pop
			MyListener ml = new MyListener(FriendCircleMessageDetailActivity.this, this, -1, dynamic.videoID, isUp);
			fc_item_commentANDup_popup.setOnClickListener(ml);
		}
	}

	public void setListeners() {
		MyApplication.getInstance().addListener(Constants.LISTENER_SYN_PERSONINFO, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		yHandler.sendEmptyMessage(REFRESH_ADAPTER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击返回按钮，退出
		case R.id.friendcircle_back:
			finish();
			break;
		case R.id.friendcircle_clear:
			break;
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

		public ItemDeleteDynamic(Context c, String feedId) {
			this.mc = c;
			this.feedId = feedId;
		}

		@Override
		public void onClick(View v) {
			//FIXME 不能删除动态
//			new AlertDialog.Builder(mc).setTitle(mc.getString(R.string.memo)).setPositiveButton(mc.getString(R.string.fc_confirm), new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					delDynamic(feedId);
//				}
//			}).setNegativeButton(mc.getString(R.string.fc_cancel), new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			}).setMessage(mc.getString(R.string.fc_delete_dynamic)).show();
		}

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

	private void choiceGroupType(final String s) {
		String[] groupTypes1 = { getResources().getString(R.string.fc_del_dynamic_copy) };
		new AlertDialog.Builder(FriendCircleMessageDetailActivity.this).setItems(groupTypes1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Clipboard.setText(FriendCircleMessageDetailActivity.this, s);
			}
		}).show();

	}

	public class MyOnClickListener implements OnClickListener {
		public int mIndex;
		private String pid;

		public MyOnClickListener(int index, String pid) {
			this.mIndex = index;
			this.pid = pid;
		}

		@Override
		public void onClick(View v) {
			if (TextUtils.isEmpty(et_content.getText().toString())) {
				yHandler.sendEmptyMessage(COMMENTISNULL);
			} else {
				threadForComment(mIndex, pid);
			}
		}
	}

	// 提交评论线程
	public void threadForComment(final int index, final String pid) {
		try {
			final CommentData comment = new CommentData();
			comment.commentAccount = dynamic.account;
			comment.comentVideoID = dynamic.videoID;
			comment.commentID = StringUtils.repNull(pid);
			comment.content = et_content.getText().toString();
			new NetIFUI_ZBRJ(FriendCircleMessageDetailActivity.this, new NetInterfaceListener() {
				@Override
				public void finish(NetInterfaceStatusDataStruct niStatusData) {
					if (niStatusData != null && niStatusData.getStatus() != null) {
						if (niStatusData.getStatus().equals(Constants.RES_SUCCESS)) { // success
							Message m = new Message();
							m.what = SUCCESS_COMMENT;
							m.arg1 = index;
//							CommentData comment = (CommentData) niStatusData.getObj();
							m.obj = comment;
							yHandler.sendMessage(m);
						} else { // fails
							yHandler.sendEmptyMessage(COMMENTIS_FAIL);
						}
					} else { // nowhy, debug or ask myyule interface developer
						yHandler.sendEmptyMessage(COMMENTIS_FAIL);
					}
				}
			}).m1_comment(comment);
		} catch (Exception e) {
			e.getStackTrace();
			yHandler.sendEmptyMessage(COMMENTIS_FAIL);
		}
	}

	/**
	 * 删除评论
	 * 
	 * @param index
	 * @param pid
	 */
	public void deteleForComment(final int index, final String pid) {
		try {
			new NetIFUI_ZBRJ(FriendCircleMessageDetailActivity.this, new NetInterfaceListener() {
				@Override
				public void finish(NetInterfaceStatusDataStruct nsdf) {
					if (nsdf != null && nsdf.getStatus() != null) {
						if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) { // success
							Message m = new Message();
							m.what = DETELE_COMMENT_SUC;
							m.arg1 = index;
							m.obj = pid;
							yHandler.sendMessage(m);

						} else { // fails errorCode
							yHandler.sendEmptyMessage(DETELE_COMMENT_FAIL);
						}
					} else { // other error
						yHandler.sendEmptyMessage(DETELE_COMMENT_FAIL);
					}
				}
			}).m1_del_comment(dynamic.videoID, StringUtils.repNull(pid));
		} catch (Exception e) {
			e.getStackTrace();
			yHandler.sendEmptyMessage(DETELE_COMMENT_FAIL);
		}
	
	}

	@Override
	public void showCommentLayout() {
		// rl_comment.setVisibility(View.VISIBLE);
		// listview.clearFocus();
		et_content.requestFocus();
		et_content.setFocusable(true);
		et_content.setFocusableInTouchMode(true);

		yHandler.sendEmptyMessageDelayed(SHOW_KEYBOARD, 100);
	}

	@Override
	public void sendComment(int index) {// 评论
		btn_send.setOnClickListener(new MyOnClickListener(index, ""));
	}

	@Override
	public void refreshAfterUpOperator(int index, int which) {
		Message m = new Message();
		if (which == 0) {
			m.what = REFRESH_AFTER_UP;
		} else if (which == 1) {
			m.what = REFRESH_AFTER_CANCEL_UP;
		}
		m.obj = index;
		yHandler.sendMessage(m);
	}

	@Override
	public void sendComment(int index, String pid) {// 回复
		btn_send.setOnClickListener(new MyOnClickListener(index, pid));
	}

	@Override
	public void deteltComment(int index, String pid) {
		deteleForComment(index, pid);
	}

	public static final int SUCCESS_COMMENT = 1;// 评论成功
	public static final int COMMENTIS_FAIL = 2;// 评论失败
	public static final int REFRESH_AFTER_UP = 3;// 点赞成功
	public static final int REFRESH_AFTER_CANCEL_UP = 4;// 取消点赞
	public static final int REFRESH_ADAPTER = 5;// 增加评论刷新
	public static final int DEL_DYNAMIC_SUC = 6;// 删除动态成功
	public static final int DEL_DYNAMIC_FAIL = 7;// 删除动态失败
	public static final int DETELE_COMMENT_SUC = 8;// 删除评论成功
	public static final int DETELE_COMMENT_FAIL = 9;// 删除评论失败
	public static final int SHOW_KEYBOARD = 10;// 显示键盘
	public static final int COMMENTISNULL = 11;// 校验输出

	@SuppressLint("HandlerLeak")
	public Handler yHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_COMMENT:// 评论成功
				btn_send.setOnClickListener(new MyOnClickListener(-1, ""));
				toastToMessage(R.string.fc_comment_success);
				CommentData comment_temp = (CommentData) msg.obj;
				int type = 0;
				String toMobile = "";
				if (comment_temp != null) {
					type = comment_temp.type;
					toMobile = comment_temp.commentToAccount;
				}
				ArrayList<CommentData> comments = dynamic.comments;
				CommentData comment = new CommentData();
				comment.type = type;
				comment.commentAccount = AccountData.getInstance().getBindphonenumber();
				comment.content = et_content.getText().toString();
				if (type != 0) {
					if (!TextUtils.isEmpty(toMobile)) {
						comment.commentToAccount = toMobile;
					}
				}
				comments.add(0, comment);
				ImCore.getInstance().getCustomProtocolDealerManager().createDealer(toMobile).comment_notify(comment);
				yHandler.sendEmptyMessage(REFRESH_ADAPTER);
				et_content.setText("");
				hideInputMethod(et_content);
				break;
			case COMMENTIS_FAIL:// 评论失败
				toastToMessage(R.string.fc_comment_fail);
				break;
			case REFRESH_AFTER_UP:// 点赞成功
				ArrayList<LikeData> ul = dynamic.likes;
				LikeData up = new LikeData();
				up.likeAccount = AccountData.getInstance().getBindphonenumber();
				ul.add(up);
				yHandler.sendEmptyMessage(REFRESH_ADAPTER);
				break;
			case REFRESH_AFTER_CANCEL_UP:// 取消点赞
				ArrayList<LikeData> ul1 = dynamic.likes;
				if (ul1 != null && ul1.size() > 0) {
					for (int i = 0; i < ul1.size(); i++) {
						if (ul1.get(i).likeAccount.equals(AccountData.getInstance().getBindphonenumber())) {
							ul1.remove(i);
						}
					}
				}
				yHandler.sendEmptyMessage(REFRESH_ADAPTER);
				break;
			case REFRESH_ADAPTER:// 刷新
				setValues();
				break;
			case DEL_DYNAMIC_SUC:// 删除动态成功
				FriendCircleMessageDetailActivity.this.finish();
				break;
			case DEL_DYNAMIC_FAIL:// 删除动态失败
				toastToMessage(R.string.fc_del_dynamic_fail);
				break;
			case DETELE_COMMENT_SUC:// 删除评论成功
				String commentId = (String) msg.obj;
				ArrayList<CommentData> comments2 = dynamic.comments;
				if (comments2 != null) {
					for (int i = 0; i < comments2.size(); i++) {
						CommentData sc = comments2.get(i);
						if (sc != null && commentId.equals(sc.commentID)) {
							comments2.remove(i);
							yHandler.sendEmptyMessage(REFRESH_ADAPTER);
							break;
						}
					}
				}
				break;
			case DETELE_COMMENT_FAIL:// 删除评论
				toastToMessage(R.string.fc_detele_comment_fail);
				break;
			case SHOW_KEYBOARD:
				showInputMethod(et_content);
				break;
			case COMMENTISNULL:
				toastToMessage(R.string.fc_comment_content_isnonull);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 显示键盘
	 * 
	 * @param v
	 */
	private void showInputMethod(View v) {
		InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(FriendCircleMessageDetailActivity.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(v, 0);
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param v
	 */
	private void hideInputMethod(View v) {
		InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(FriendCircleMessageDetailActivity.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public void syn(PersonData person) {
		if(person != null && person.account.equals(phone)){
			this.person = person;
			this.setPersonInfo();
		}
	}

}

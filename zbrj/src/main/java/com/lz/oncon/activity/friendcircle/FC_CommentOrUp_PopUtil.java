package com.lz.oncon.activity.friendcircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity.ShowCommentLayoutInterface;

public class FC_CommentOrUp_PopUtil {
	private Context mContext;
	private ShowCommentLayoutInterface mSci;
	private int mPosition;
	private String mFeedId;
	private boolean mIsUp;
	
	private PopupWindow popupwindow;
	
	private View view;
	private LayoutInflater inflater;
	
	private LinearLayout ll_comment;
	private LinearLayout ll_up;
	private TextView tv_up;
	
	private NetIF_ZBRJ ni;
	
	public FC_CommentOrUp_PopUtil(Context context, ShowCommentLayoutInterface sci, int position, String feedId, boolean isUp){
		this.mContext = context;
		this.mSci = sci;
		this.mPosition = position;
		this.mFeedId = feedId;
		this.mIsUp = isUp;
		
		initController();
		initView();
		setListener();
	}
	
	public void initController(){
		ni = new NetIF_ZBRJ(mContext);
	}
	
	public void initView(){
		popupwindow = new PopupWindow(mContext);
		inflater = LayoutInflater.from(mContext);

		view = inflater.inflate(R.layout.friendcircle_comment_or_up_pop, null);
		ll_up = (LinearLayout) view.findViewById(R.id.fc_comment_up_pop_up);
		ll_comment = (LinearLayout) view.findViewById(R.id.fc_comment_up_pop_comment);
		tv_up = (TextView) view.findViewById(R.id.fc_comment_up_pop_up_tv);
		
		if(mIsUp){
			tv_up.setText(R.string.fc_cancel);
		}else{
			tv_up.setText(R.string.fc_up);
		}
	}
	
	public PopupWindow getPopupwindown(){
		popupwindow.setContentView(view);
		
		// 设置popupwindow的高度
		popupwindow.setWidth(LayoutParams.WRAP_CONTENT);
		popupwindow.setHeight(LayoutParams.WRAP_CONTENT);

		// 设置PopupWindow外部区域是否可触摸
		popupwindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popupwindow.setTouchable(true); // 设置PopupWindow可触摸
		popupwindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		// 实例化一个ColorDrawable颜色为透明
//		ColorDrawable dw = new ColorDrawable(0x00000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
//		popupwindow.setBackgroundDrawable(dw);
		// 设置popupwindow进出动画
//		popupwindow.setAnimationStyle(R.style.music_popwin_anim_style);

		return popupwindow;
	}
	
	public void setListener(){
		ll_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupwindow.dismiss();
				mSci.showCommentLayout();
				mSci.sendComment(mPosition);
			}
		});
		
		ll_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupwindow.dismiss();
				if(mIsUp){//取消点赞
					threadForCancelUp(mFeedId);
				}else{
					threadForUp(mFeedId);
				}
			}
		});
	}
	
	// 点赞
	public void threadForUp(final String feedId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				NetInterfaceStatusDataStruct nsdf = ni.m1_give_like(feedId);
				if (nsdf != null && nsdf.getStatus() != null) {
					if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) {
						yHandler.sendEmptyMessage(SUCCESS_UP);
					} else {
					}
				} else {
				}
			}
		}).start();
	}
	
	// 取消点赞
	public void threadForCancelUp(final String feedId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
					NetInterfaceStatusDataStruct nsdf = ni.m1_cancel_like(feedId);
					if (nsdf != null && nsdf.getStatus() != null) {
						if (nsdf.getStatus().equals(Constants.RES_SUCCESS)) {
							yHandler.sendEmptyMessage(SUCCESS_CANCEL_UP);
						} else {

						}
					} else {

					}
			}
		}).start();
	}
	
	public static final int SUCCESS_UP = 1;
	public static final int SUCCESS_CANCEL_UP = 2;
	
	@SuppressLint("HandlerLeak")
	public Handler yHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_UP:
				mSci.refreshAfterUpOperator(mPosition, 0);
//				InfoToast.makeText(mContext, mContext.getString(R.string.fc_up_success), Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
				break;
			case SUCCESS_CANCEL_UP:
				mSci.refreshAfterUpOperator(mPosition, 1);
//				InfoToast.makeText(mContext, mContext.getString(R.string.fc_up_fails), Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	

}

package com.lz.oncon.activity.friendcircle;

/**
 * 朋友圈自定义ListView
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.lb.common.util.Log;
import com.xuanbo.xuan.R;

public class FriendCircleListView extends ListView implements OnScrollListener {
	// 拖拉ListView枚举所有状态
	public enum DListViewState {
		LV_NORMAL, // 普通状态
		LV_PULL_REFRESH, // 下拉状态（为超过mHeadViewHeight）
		LV_RELEASE_REFRESH, // 松开可刷新状态（超过mHeadViewHeight）
		LV_LOADING;// 加载状态
	}

	private int mFirstItemIndex = -1;// 当前视图能看到的第一个项的索引
	private boolean mIsRecord = false;// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private int mStartY, mMoveY;// 按下是的y坐标,move时的y坐标
	private DListViewState mlistViewState = DListViewState.LV_NORMAL;// 拖拉状态.(自定义枚举)
	private final static int RATIO = 2;// 手势下拉距离比.
	private OnRefreshListener onRefreshListener;// 下拉刷新接口（自定义）
	private boolean isScroller = true;// 是否屏蔽ListView滑动。

	private LayoutInflater inflater;
	private View headerView;
//	private HeadImageView headerView_avatar;
//	private TextView headerView_nickname, headerView_sex, headerView_video;
	private int longestDistance;
	public FriendCircleNotificationNum friendcircle_noti = null;
	private String mobile;
//	private PersonController mPersonController;

	public String getMobile() {
		return mobile;
	}

//	public void setMobile(String mobile) {
//		this.mobile = mobile;
//		PersonData person = mPersonController.findPerson(mobile);
//		headerView_nickname.setText(mPersonController.findNameByMobile(person.account));
//		headerView_sex.setText(person == null ? "" : person.sex == 1 ? getContext().getString(R.string.female_sign) : getContext().getString(R.string.male_sign));
//		headerView_video.setText(person.videoNums + "");
//		headerView_avatar.setPerson(mobile, person.image);
//	}

	public FriendCircleListView(Context context) {
		super(context, null);
		initDragListView(context);
	}

	public FriendCircleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDragListView(context);
	}

	public FriendCircleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initDragListView(context);
	}

	// 注入下拉刷新接口
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

	/***
	 * 初始化ListView
	 */
	public void initDragListView(Context context) {
		longestDistance = context.getResources().getDimensionPixelSize(R.dimen.fc_title_height);
		initHeadView(context);// 初始化该head.
		setOnScrollListener(this);// ListView滚动监听
//		mPersonController = new PersonController();
		setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	/***
	 * 初始话头部HeadView
	 * 
	 * @param context
	 *            上下文
	 * @param time
	 *            上次更新时间
	 */
	private void initHeadView(Context context) {
		inflater = LayoutInflater.from(context);
		headerView = inflater.inflate(R.layout.friendcircle_headerview, null);
//		headerView_avatar = (HeadImageView) headerView.findViewById(R.id.head);
//		headerView_nickname = (TextView) headerView.findViewById(R.id.nickname);
//		headerView_sex = (TextView) headerView.findViewById(R.id.sex_v);
//		headerView_video = (TextView) headerView.findViewById(R.id.video_num);

		addHeaderView(headerView);
//		setListener(context);
	}
	
	/**
	 * 增加消息提醒
	 */
	public void addMessageHead(Context context){
		/**
		 * 新消息提示,条件从数据库中来
		 */
		if(friendcircle_noti == null){
			friendcircle_noti = new FriendCircleNotificationNum(context);
			friendcircle_noti.setVisibility(View.GONE);
		}
	}
	
//	public void setMessageNum(final Context context,String num,String mobile,final ArrayList<VideoData> list ){
//		
//		if(num == null){
//			 if(getHeaderViewsCount()>1&&friendcircle_noti!=null){
//				removeHeaderView(friendcircle_noti);
//				friendcircle_noti = null;
//			}
//			return;
//		}
//		if(num!=null&&Integer.parseInt(num) <= 0){
//			 if(getHeaderViewsCount()>1&&friendcircle_noti!=null){
//					removeHeaderView(friendcircle_noti);
//					friendcircle_noti = null;
//				}
//			return;
//		}
//		if(list == null){
//			 if(getHeaderViewsCount()>1&&friendcircle_noti!=null){
//					removeHeaderView(friendcircle_noti);
//					friendcircle_noti = null;
//				}
//			return;
//		}
//		if(friendcircle_noti == null){
//			friendcircle_noti = new FriendCircleNotificationNum(context);
//			addHeaderView(friendcircle_noti, null, false);
//		}
//		friendcircle_noti.setValue(context,num, mobile);
//		if(friendcircle_noti.getVisibility() == View.GONE){
//			friendcircle_noti.setVisibility(View.VISIBLE);
//		}
//		friendcircle_noti.meaasge_notification_layout.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(context, FriendCircleMessageActivity.class);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				ArrayList<VideoData> listtemp = new ArrayList<VideoData>();
//				listtemp.addAll(list);
//				i.putExtra("list", listtemp);
//				MyApplication.getInstance().startActivity(i);
//				if (list != null && list.size() > 0) {
//					for (VideoData sDynamic:list) {
//						FriendCircleCacheUtil.removeDataCache(sDynamic.post_id, MyApplication.getInstance());
//					}
//					list.clear();
//					new FCHelper(AccountData.getInstance().getUsername()).clearAllFcNoti();
//				}
//			}
//		});
//	}

//	public void setListener(final Context context) {
//		headerView_avatar.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			}
//		});
//	}

	/***
	 * touch 事件监听
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		// 按下
		case MotionEvent.ACTION_DOWN:
			doActionDown(ev);
			break;
		// 移动
		case MotionEvent.ACTION_MOVE:
			doActionMove(ev);
			break;
		// 抬起
		case MotionEvent.ACTION_UP:
			doActionUp(ev);
			break;
		default:
			break;
		}
		/***
		 * 如果是ListView本身的拉动，那么返回true，这样ListView不可以拖动.
		 * 如果不是ListView的拉动，那么调用父类方法，这样就可以上拉执行.
		 */
		if (isScroller) {
			return super.onTouchEvent(ev);
		} else {
			return true;
		}
	}

	/***
	 * 摁下操作
	 * 
	 * 作用：获取摁下是的y坐标
	 * 
	 * @param event
	 */
	void doActionDown(MotionEvent event) {
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
	}

	public int yaoxm;

	/***
	 * 拖拽移动操作
	 * 
	 * @param event
	 */
	void doActionMove(MotionEvent event) {
		mMoveY = (int) event.getY();// 获取实时滑动y坐标
		// 检测是否是一次touch事件.
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
		/***
		 * 如果touch关闭或者正处于Loading状态的话 return.
		 */
		if (mIsRecord == false || mlistViewState == DListViewState.LV_LOADING) {
			return;
		}
		// 向下啦headview移动距离为y移动的一半.（比较友好）
		int offset = (mMoveY - mStartY) / RATIO;
		yaoxm = offset;

		switch (mlistViewState) {
		// 普通状态
		case LV_NORMAL: {
			// 如果<0，则意味着上滑动.
			if (offset > 0 && offset <= longestDistance) {
				// 设置headView的padding属性.
				headerView.setPadding(0, offset, 0, 0);
				switchViewState(DListViewState.LV_PULL_REFRESH);// 下拉状态
			} else if (offset > longestDistance) {
				headerView.setPadding(0, offset, 0, 0);
				switchViewState(DListViewState.LV_RELEASE_REFRESH);// 下拉刷新状态
			} else {
				headerView.setPadding(0, 0, 0, 0);
				switchViewState(DListViewState.LV_NORMAL);// 正常状态
			}
		}
			break;
		// 下拉状态
		case LV_PULL_REFRESH: {
			setSelection(0);// 选中第一项，可选.
			// 设置headView的padding属性.
			headerView.setPadding(0, offset, 0, 0);
			if (offset < 0) {
				/***
				 * 要明白为什么isScroller = false;
				 */
				isScroller = false;
				switchViewState(DListViewState.LV_NORMAL);// 普通状态
				Log.e("jj", "isScroller=" + isScroller);
			} else if (offset > 0 && offset <= longestDistance) {
				switchViewState(DListViewState.LV_PULL_REFRESH);// 下拉状态
			} else if (offset > longestDistance) {// 如果下拉的offset超过headView的高度则要执行刷新.
				switchViewState(DListViewState.LV_RELEASE_REFRESH);// 更新为可刷新的下拉状态.
			}
		}
			break;
		// 可刷新状态
		case LV_RELEASE_REFRESH: {
			setSelection(0);
			// 设置headView的padding属性.
			headerView.setPadding(0, offset, 0, 0);
			// 下拉offset>0，但是没有超过headView的高度.那么要goback 原装.
			if (offset < 0) {
				switchViewState(DListViewState.LV_NORMAL);
			} else if (offset <= longestDistance) {
				switchViewState(DListViewState.LV_PULL_REFRESH);
			} else if (offset > longestDistance) {
				switchViewState(DListViewState.LV_RELEASE_REFRESH);
			}
		}
			break;
		default:
			return;
		}
		;
	}

	/***
	 * 手势抬起操作
	 * 
	 * @param event
	 */
	public void doActionUp(MotionEvent event) {
		mIsRecord = false;// 此时的touch事件完毕，要关闭。
		isScroller = true;// ListView可以Scrooler滑动.
		// 处理相应状态.
		switch (mlistViewState) {
		case LV_LOADING:
			break;
		// 普通状态
		case LV_NORMAL:
			break;
		// 下拉状态
		case LV_PULL_REFRESH:
			launchResetListViewThread(yaoxm);
			// headerView.setPadding(0, 0, 0, 0);
			switchViewState(DListViewState.LV_NORMAL);
			break;
		// 刷新状态
		case LV_RELEASE_REFRESH: // 刷新完成后记得修改listview的状态为LV_NORMAL
			launchResetListViewThread(yaoxm);
			// headerView.setPadding(0, 0, 0, 0);
			switchViewState(DListViewState.LV_LOADING);
			onRefresh();// 下拉刷新
			break;
		}
	}

	// 切换headview视图
	public void switchViewState(DListViewState state) {

		switch (state) {
		// 普通状态
		case LV_NORMAL:
			break;
		// 下拉状态
		case LV_PULL_REFRESH:
			// 是有可刷新状态（LV_RELEASE_REFRESH）转为这个状态才执行，其实就是你下拉后在上拉会执行.
			break;
		// 松开刷新状态
		case LV_RELEASE_REFRESH:
			break;
		// 加载状态
		case LV_LOADING:
			break;
		default:
			return;
		}
		// 切记不要忘记时时更新状态。
		mlistViewState = state;
	}

	/***
	 * 下拉刷新
	 */
	private void onRefresh() {
		if (onRefreshListener != null) {
			onRefreshListener.onRefresh();
		}
	}

	/***
	 * 下拉刷新完毕
	 */
	public void onRefreshComplete() {
		headerView.setPadding(0, 0, 0, 0);// 回归.
		switchViewState(DListViewState.LV_NORMAL);//
	}


	public void setFirstItemIndex(int index) {
		mFirstItemIndex = index;
	}

	/***
	 * 自定义接口
	 */
	public interface OnRefreshListener {
		/***
		 * 下拉刷新执行
		 */
		void onRefresh();
	}

	public void launchResetListViewThread(int distance) {
		new Thread(new ResetListViewThread(distance)).start();
	}

	/**
	 * ListView 复位线程
	 * 
	 * @author yao
	 * 
	 */
	class ResetListViewThread implements Runnable {
		private static final int TIME = 30;
		int distanceOfEach;

		private ResetListViewThread(int distance) {
			distanceOfEach = distance / TIME;
		}

		@Override
		public void run() {
			for (int i = 0; i < TIME; i++) {
				if (i == (TIME - 1)) {
					yaoxm = 0;
				} else {
					yaoxm = yaoxm - distanceOfEach;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Message m = Message.obtain();
				m.what = RESETLISTVIEW;
				m.obj = yaoxm;
				mHandler.sendMessage(m);
			}
		}

	}

	public static final int RESETLISTVIEW = 0; // ListView复位
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RESETLISTVIEW:
				int d = (Integer) msg.obj;
				headerView.setPadding(0, d, 0, 0);
				if (d == 0) {
					headerView.setPadding(0, 0, 0, 0);
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
	}

}
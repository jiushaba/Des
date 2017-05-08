package com.lz.oncon.app.im.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.lb.common.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xuanbo.xuan.R;
import com.lz.oncon.widget.SearchBar;

/***
 * 自定义拖拉ListView
 * 
 * @author zhangjia
 * 
 */
public class IMListView extends ListView implements OnScrollListener{
	
	private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;
	public int getScrollState() {
		return scrollState;
	}

	// 拖拉ListView枚举所有状态
	private enum DListViewState {
		LV_NORMAL, // 普通状态
		LV_PULL_REFRESH, // 下拉状态（为超过mHeadViewHeight）
		LV_RELEASE_REFRESH, // 松开可刷新状态（超过mHeadViewHeight）
		LV_LOADING;// 加载状态
	}

	private LinearLayout mHeadView;// 头部headView
//	public SearchBar search_bar;
	public ImageView search_view;

	private int adHeight;//广告图片高度

	private Animation animation, reverseAnimation;// 旋转动画，旋转动画之后旋转动画.

	private int mFirstItemIndex = -1;// 当前视图能看到的第一个项的索引

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecord = false;

	private int mStartY, mMoveY;// 按下是的y坐标,move时的y坐标

	private DListViewState mlistViewState = DListViewState.LV_NORMAL;// 拖拉状态.(自定义枚举)

	private final static int RATIO = 2;// 手势下拉距离比.

	private boolean mBack = false;// headView是否返回.

	private OnRefreshListener onRefreshListener;// 下拉刷新接口（自定义）

	private boolean isScroller = true;// 是否屏蔽ListView滑动。
	
	private ImageView adView; //下拉的的图片

	public IMListView(Context context) {
		super(context, null);
		initDragListView(context);
	}

	public IMListView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		initHeadView(context);// 初始化该head.
		
		setOnScrollListener(this);// ListView滚动监听
	}

	/***
	 * 初始话头部HeadView
	 * 
	 * @param context
	 *            上下文
	 * @param time
	 *            上次更新时间
	 */
	public void initHeadView(Context context) {
		mHeadView = new LinearLayout(context);
		mHeadView.setOrientation(LinearLayout.VERTICAL);
		adView = new ImageView(context);
		adHeight = context.getResources().getDimensionPixelSize(R.dimen.im_ad_height);
		mHeadView.addView(adView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, adHeight));
		search_view = new ImageView(context);
		search_view.setImageResource(R.drawable.bg_search_input);
		mHeadView.addView(search_view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		addHeaderView(mHeadView, null, false);// 将初始好的ListView add进拖拽ListView
		// 在这里我们要将此headView设置到顶部不显示位置.
		mHeadView.setPadding(0, -1 * adHeight, 0, 0);

		initAnimation();// 初始化动画
	}
	
	public void setAdViewImg(Bitmap pic) {
		adView.setImageBitmap(pic);
	}
	
	public void setAdViewImg(Drawable drawable) {
		adView.setImageDrawable(drawable);
	}

	/***
	 * 初始化动画
	 */
	private void initAnimation() {
		// 旋转动画
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());// 匀速
		animation.setDuration(250);
		animation.setFillAfter(true);// 停留在最后状态.
		// 反向旋转动画
		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
	}

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

		switch (mlistViewState) {
		// 普通状态
		case LV_NORMAL: {
			// 如果<0，则意味着上滑动.
			if (offset > 0) {
				// 设置headView的padding属性.
				mHeadView.setPadding(0, offset - adHeight, 0, 0);
				switchViewState(DListViewState.LV_PULL_REFRESH);// 下拉状态
			}

		}
			break;
		// 下拉状态
		case LV_PULL_REFRESH: {
			setSelection(0);// 选中第一项，可选.
			// 设置headView的padding属性.
			mHeadView.setPadding(0, offset - adHeight, 0, 0);
			if (offset < 0) {
				/***
				 * 要明白为什么isScroller = false;
				 */
				isScroller = false;
				switchViewState(DListViewState.LV_NORMAL);// 普通状态
				Log.e("jj", "isScroller=" + isScroller);
			} else if (offset > adHeight) {// 如果下拉的offset超过headView的高度则要执行刷新.
				switchViewState(DListViewState.LV_RELEASE_REFRESH);// 更新为可刷新的下拉状态.
			}
		}
			break;
		// 可刷新状态
		case LV_RELEASE_REFRESH: {
			setSelection(0);
			// 设置headView的padding属性.
			mHeadView.setPadding(0, offset - adHeight, 0, 0);
			// 下拉offset>0，但是没有超过headView的高度.那么要goback 原装.
			if (offset >= 0 && offset <= adHeight) {
				mBack = true;
				switchViewState(DListViewState.LV_PULL_REFRESH);
			} else if (offset < 0) {
				switchViewState(DListViewState.LV_NORMAL);
			} else {

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
		mBack = false;
		// 处理相应状态.
		switch (mlistViewState) {
		case LV_LOADING:
			break;
		// 普通状态
		case LV_NORMAL:
			break;
		// 下拉状态
		case LV_PULL_REFRESH:
			mHeadView.setPadding(0, -1 * adHeight, 0, 0);
			switchViewState(DListViewState.LV_NORMAL);
			break;
		// 刷新状态
		case LV_RELEASE_REFRESH:
			mHeadView.setPadding(0, 0, 0, 0);
			switchViewState(DListViewState.LV_LOADING);
			onRefresh();// 下拉刷新
			break;
		}

	}

	// 切换headview视图
	private void switchViewState(DListViewState state) {

		switch (state) {
		// 普通状态
		case LV_NORMAL: 
			break;
		// 下拉状态
		case LV_PULL_REFRESH:
			// 是有可刷新状态（LV_RELEASE_REFRESH）转为这个状态才执行，其实就是你下拉后在上拉会执行.
			if (mBack) {
				mBack = false;
			}
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
		mHeadView.setPadding(0, -1 * adHeight, 0, 0);// 回归.
		switchViewState(DListViewState.LV_NORMAL);//
	}

	/***
	 * ListView 滑动监听
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
	}

	/***
	 * 自定义接口
	 */
	public interface OnRefreshListener {
		/***
		 * // 下拉刷新执行
		 */
		void onRefresh();
	}
}
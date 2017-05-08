
package com.lz.oncon.activity.connections.widget.pulltorefresh;

import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.lz.oncon.activity.connections.widget.pulltorefresh.PullToRefreshBase.State;

import android.view.View;
import android.view.animation.Interpolator;

public interface IPullToRefresh<T extends View> {

	/**
	 * Demos the Pull-to-Refresh functionality to the user so that they are
	 * aware it is there. This could be useful when the user first opens your
	 * app, etc. The animation will only happen if the Refresh View (ListView,
	 * ScrollView, etc) is in a state where a Pull-to-Refresh could occur by a
	 * user's touch gesture (i.e. scrolled to the top/bottom).
	 * 
	 * @return true - if the Demo has been started, false if not.
	 */
	public boolean demo();

	/**
	 * 获取当前处于什么模式. 仅在 <code>Mode.BOTH</code> 模式下有用.
	 * 
	 * @return 当前处于什么模式
	 */
	public Mode getCurrentMode();

	/**
	 * 返回 Touch事件是否被过滤. 如果返回true，视图只有在Y轴变动比x轴大的时候才使用touch事件. 这就意味着
	 * 视图在横向滚动视图中将不起作用，例如ViewPager.
	 * 
	 * @return boolean - true 视图在过滤touch事件
	 */
	public boolean getFilterTouchEvents();

	/**
	 * Returns a proxy object which allows you to call methods on all of the
	 * LoadingLayouts (the Views which show when Pulling/Refreshing).
	 * <p />
	 * You should not keep the result of this method any longer than you need
	 * it.
	 * 
	 * @return Object which will proxy any calls you make on it, to all of the
	 *         LoadingLayouts.
	 */
	public ILoadingLayout getLoadingLayoutProxy();

	/**
	 * Returns a proxy object which allows you to call methods on the
	 * LoadingLayouts (the Views which show when Pulling/Refreshing). The actual
	 * LoadingLayout(s) which will be affected, are chosen by the parameters you
	 * give.
	 * <p />
	 * You should not keep the result of this method any longer than you need
	 * it.
	 * 
	 * @param includeStart - Whether to include the Start/Header Views
	 * @param includeEnd - Whether to include the End/Footer Views
	 * @return Object which will proxy any calls you make on it, to the
	 *         LoadingLayouts included.
	 */
	public ILoadingLayout getLoadingLayoutProxy(boolean includeStart, boolean includeEnd);

	/**
	 * 获取视图的模式. 如果是<code>Mode.BOTH</code>, 可使用 <code>getCurrentMode()</code> 来检查当前具体处于那种模式
	 * @return 视图设置的模式
	 */
	public Mode getMode();

	/**
	 * Get the Wrapped Refreshable View. Anything returned here has already been
	 * added to the content view.
	 * 
	 * @return The View which is currently wrapped
	 */
	public T getRefreshableView();

	/**
	 * Get whether the 'Refreshing' View should be automatically shown when
	 * refreshing. Returns true by default.
	 * 
	 * @return - true if the Refreshing View will be show
	 */
	public boolean getShowViewWhileRefreshing();

	/**
	 * @return - The state that the View is currently in.
	 */
	public State getState();

	/**
	 * Whether Pull-to-Refresh is enabled
	 * 
	 * @return enabled
	 */
	public boolean isPullToRefreshEnabled();

	/**
	 * Gets whether Overscroll support is enabled. This is different to
	 * Android's standard Overscroll support (the edge-glow) which is available
	 * from GINGERBREAD onwards
	 * 
	 * @return true - if both PullToRefresh-OverScroll and Android's inbuilt
	 *         OverScroll are enabled
	 */
	public boolean isPullToRefreshOverScrollEnabled();

	/**
	 * Returns whether the Widget is currently in the Refreshing mState
	 * 
	 * @return true if the Widget is currently refreshing
	 */
	public boolean isRefreshing();

	/**
	 * Returns whether the widget has enabled scrolling on the Refreshable View
	 * while refreshing.
	 * 
	 * @return true if the widget has enabled scrolling while refreshing
	 */
	public boolean isScrollingWhileRefreshingEnabled();

	/**
	 * Mark the current Refresh as complete. Will Reset the UI and hide the
	 * Refreshing View
	 */
	public void onRefreshComplete();

	/**
	 * Set the Touch Events to be filtered or not. If set to true, then the View
	 * will only use touch events where the difference in the Y-axis is greater
	 * than the difference in the X-axis. This means that the View will not
	 * interfere when it is used in a horizontal scrolling View (such as a
	 * ViewPager), but will restrict which types of finger scrolls will trigger
	 * the View.
	 * 
	 * @param filterEvents - true if you want to filter Touch Events. Default is
	 *            true.
	 */
	public void setFilterTouchEvents(boolean filterEvents);

	/**
	 * Set the mode of Pull-to-Refresh that this view will use.
	 * 
	 * @param mode - Mode to set the View to
	 */
	public void setMode(Mode mode);

	/**
	 * Set OnPullEventListener for the Widget
	 * 
	 * @param listener - Listener to be used when the Widget has a pull event to
	 *            propogate.
	 */
	public void setOnPullEventListener(OnPullEventListener<T> listener);

	/**
	 * Set OnRefreshListener for the Widget
	 * 
	 * @param listener - Listener to be used when the Widget is set to Refresh
	 */
	public void setOnRefreshListener(OnRefreshListener<T> listener);

	/**
	 * Set OnRefreshListener for the Widget
	 * 
	 * @param listener - Listener to be used when the Widget is set to Refresh
	 */
	public void setOnRefreshListener(OnRefreshListener2<T> listener);

	/**
	 * Sets whether Overscroll support is enabled. This is different to
	 * Android's standard Overscroll support (the edge-glow). This setting only
	 * takes effect when running on device with Android v2.3 or greater.
	 * 
	 * @param enabled - true if you want Overscroll enabled
	 */
	public void setPullToRefreshOverScrollEnabled(boolean enabled);

	/**
	 * Sets the Widget to be in the refresh state. The UI will be updated to
	 * show the 'Refreshing' view, and be scrolled to show such.
	 */
	public void setRefreshing();

	/**
	 * Sets the Widget to be in the refresh state. The UI will be updated to
	 * show the 'Refreshing' view.
	 * 
	 * @param doScroll - true if you want to force a scroll to the Refreshing
	 *            view.
	 */
	public void setRefreshing(boolean doScroll);

	/**
	 * Sets the Animation Interpolator that is used for animated scrolling.
	 * Defaults to a DecelerateInterpolator
	 * 
	 * @param interpolator - Interpolator to use
	 */
	public void setScrollAnimationInterpolator(Interpolator interpolator);

	/**
	 * By default the Widget disables scrolling on the Refreshable View while
	 * refreshing. This method can change this behaviour.
	 * 
	 * @param scrollingWhileRefreshingEnabled - true if you want to enable
	 *            scrolling while refreshing
	 */
	public void setScrollingWhileRefreshingEnabled(boolean scrollingWhileRefreshingEnabled);

	/**
	 * A mutator to enable/disable whether the 'Refreshing' View should be
	 * automatically shown when refreshing.
	 * 
	 * @param showView
	 */
	public void setShowViewWhileRefreshing(boolean showView);

}
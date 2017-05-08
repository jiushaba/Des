package com.lz.oncon.activity.connections.widget.pulltorefresh;

import android.view.View;

/**
 * 该接口允许PullToRefreshBase截获对AdapterView.setEmptyView()的调用
 */
public interface EmptyViewMethodAccessor {

	/**
	 * 在 AdapterView.setEmptyView()里调用
	 * 
	 * @param View
	 *            to set as Empty View
	 */
	public void setEmptyViewInternal(View emptyView);

	/**
	 * Should call PullToRefreshBase.setEmptyView() which will then
	 * automatically call through to setEmptyViewInternal()
	 * 
	 * @param View
	 *            to set as Empty View
	 */
	public void setEmptyView(View emptyView);

}

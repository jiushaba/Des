package com.lz.oncon.app.im.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewMeasureUtil {

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int itemHeight = 0;
		if (listAdapter.getCount() > 0) {
			View listItem = listAdapter.getView(0, null, listView);
			if(listItem!=null){
				listItem.measure(0, 0);
				itemHeight = listItem.getMeasuredHeight();
			}
		}
		totalHeight = listAdapter.getCount() * itemHeight + 10;

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ listView.getPaddingTop() + listView.getPaddingBottom();
		listView.setLayoutParams(params);
	}
}
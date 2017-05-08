package com.lz.oncon.activity.connections.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewInScrollView extends ListView {
	public ListViewInScrollView(Context context) {
		super(context);
	}

	public ListViewInScrollView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		adjustHeight();
	}

	private void adjustHeight() {
		ListAdapter adapter = getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, this);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = totalHeight
				+ (getDividerHeight() * (adapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		setLayoutParams(params);
	}

}
package com.lz.oncon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CNFListView extends ListView {

	public CNFListView(Context context) {
		super(context);
	}

	public CNFListView(Context context, AttributeSet as) {
		super(context, as);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}

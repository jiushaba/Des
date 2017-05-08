package com.lz.oncon.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.xuanbo.xuan.R;

public class LineGridView extends GridView {
	private List<Object> mList;
	public int count;
	private int total_num;
	private int COLUMN = 4;

	public List<Object> getmList() {
		return mList;
	}

	public void setmList(List<Object> mList) {
		this.mList = mList;
		total_num = (mList == null ? 0 : mList.size() % COLUMN == 0 ? mList.size() / COLUMN : mList.size() / COLUMN + 1);
	}

	public LineGridView(Context context) {
		super(context);
	}

	public LineGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LineGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		View localView1 = getChildAt(0);
		int column = getWidth() / localView1.getWidth();
		int childCount = getChildCount();
		Paint localPaint;
		localPaint = new Paint();
		localPaint.setStyle(Paint.Style.STROKE);
		localPaint.setColor(getContext().getResources().getColor(R.color.list_division));
		for (int i = 0; i < childCount; i++) {
			View cellView = getChildAt(i);
			if ((i + 1) % column == 0) {// 最后一列
				canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);//横线
			} else if ((i + 1) > (childCount - (childCount % column))) {// 小于4个
				if (mList != null && mList.size() != 1) {
				canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);//竖线
				}
			} else {
				canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);//竖线
				canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);//横线
			}
		}
	}
}
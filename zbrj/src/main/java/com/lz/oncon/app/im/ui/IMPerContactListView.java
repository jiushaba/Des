package com.lz.oncon.app.im.ui;

import java.util.List;

import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lz.oncon.adapter.ChooserAdapter;
import com.lz.oncon.adapter.ChooserAdapter.OnCheckChangedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class IMPerContactListView extends ListView {

	private Context mContext;
	private PersonController mPersonController;

	private ChooserAdapter mAdapter;
	private List<FansData> mDatas;
	private OnCheckChangedListener mOnCheckChangedListener;
	
	public OnCheckChangedListener getmOnCheckChangedListener() {
		return mOnCheckChangedListener;
	}

	public void setmOnCheckChangedListener(
			OnCheckChangedListener mOnCheckChangedListener) {
		this.mOnCheckChangedListener = mOnCheckChangedListener;
	}

	public IMPerContactListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		this.mContext = context;
		mPersonController = new PersonController();
	}

	public void update(String keyword) {
		mDatas = mPersonController.getFriends(keyword);
		
		if (mAdapter != null) {
			mAdapter = (ChooserAdapter) getAdapter();
			mAdapter.setList(mDatas);
			mAdapter.notifyDataSetChanged();
		}
		invalidate();
	}

	public void search(String keyword) {
	
	}

}

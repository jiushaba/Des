package com.lb.zbrj.adapter;

import java.util.List;

import com.xuanbo.xuan.R;
import com.lb.zbrj.data.VideoTagData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoTagAdapter extends BaseAdapter {
	
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的列表） */
	public List<VideoTagData> mList;
	/** TextView 内容 */
	private TextView item_text;
	/** 要删除的position */
	public int remove_position = -1;

	public VideoTagAdapter(Context context, List<VideoTagData> list) {
		this.context = context;
		this.mList = list;
	}
	
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public VideoTagData getItem(int position) {
		if (mList != null && mList.size() != 0) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_channel_mng_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		VideoTagData data = getItem(position);
		item_text.setText(data.tag);
		if ((position == 0) || (position == 1)){
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
			item_text.setEnabled(false);
		}
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + mList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}
		return view;
	}

	/** 添加列表 */
	public void addItem(VideoTagData data) {
		mList.add(data);
		notifyDataSetChanged();
	}

	/** 拖动变更排序 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		VideoTagData dragItem = getItem(dragPostion);
		if (dragPostion < dropPostion) {
			dragItem.seq = dropPostion + 1;
			mList.add(dropPostion + 1, dragItem);
			mList.remove(dragPostion);
		} else {
			dragItem.seq = dropPostion;
			mList.add(dropPostion, dragItem);
			mList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}
	
	/** 获取列表 */
	public List<VideoTagData> getLst() {
		return mList;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除列表 */
	public void remove() {
		mList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
	}
	
	/** 设置列表 */
	public void setListDate(List<VideoTagData> list) {
		mList = list;
	}
	
	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}
}
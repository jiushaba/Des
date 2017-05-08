package com.lz.oncon.activity.connections.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.xuanbo.xuan.R;

public class CheckItemListAdapter extends ArrayAdapter<CheckItem> {
	private AdapterView.OnItemSelectedListener onItemSelectedListener;
	private int resourceId;
	private int selectMode;

	public CheckItemListAdapter(Context context, int resourceId,
			List<CheckItem> itemList,
			AdapterView.OnItemSelectedListener onItemSelectedListener) {
		this(context, resourceId, itemList, onItemSelectedListener, 0);
	}

	public CheckItemListAdapter(Context context, int resourceId,
			List<CheckItem> itemList,
			AdapterView.OnItemSelectedListener onItemSelectedListener,
			int selectMode) {
		super(context, resourceId, itemList);
		this.resourceId = resourceId;
		this.onItemSelectedListener = onItemSelectedListener;
		this.selectMode = selectMode;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout linearLayout = new LinearLayout(getContext());
		((LayoutInflater) getContext().getSystemService("layout_inflater"))
				.inflate(this.resourceId, linearLayout, true);
		final CheckItem checkItem = (CheckItem) getItem(position);
		((TextView) linearLayout.findViewById(R.id.check_list_item_label))
				.setText(checkItem.label);
		ImageView checkStatus = (ImageView) linearLayout
				.findViewById(R.id.check_list_item_status);
		if (checkItem.checked) {
			checkStatus.setImageResource(R.drawable.purpose_select_icon);
		} else {
			checkStatus.setImageResource(R.drawable.purpose_un_select_icon);
		}
		if(null != onItemSelectedListener){
			linearLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (selectMode == 1) {
						if (onItemSelectedListener != null) {
							onItemSelectedListener
									.onItemSelected(null, null, position, 0L);
						} else if (!checkItem.checked) {
								if (onItemSelectedListener != null) {
									onItemSelectedListener
											.onItemSelected(null, null, position,
													0L);
								}
								checkItem.checked = true;
								for (int i = 0; i <getCount(); i++) {
									CheckItem checkItemInner = (CheckItem) getItem(i);
									if (checkItemInner == checkItem)
										continue;
									checkItemInner.checked = false;
								}
								notifyDataSetChanged();
							}
					}

				}
			});
		}
		

		return linearLayout;
	}
}
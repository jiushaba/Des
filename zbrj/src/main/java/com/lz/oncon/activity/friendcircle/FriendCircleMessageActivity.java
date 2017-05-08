package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.db.FCHelper;

public class FriendCircleMessageActivity extends BaseActivity implements OnItemClickListener {

	private ListView fc_message_list;
	private TextView friendcircle_clear;
	private FriendCicleMessageAdapter adapter;
	private ArrayList list = new ArrayList();
//	private View lvActive_footer;FIXME 暂不启用

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initController();
		initViews();
		setValues();
		setListeners();
	}

	public void initContentView() {
		setContentView(R.layout.fc_activity_friendcircle_message);
		friendcircle_clear = (TextView) findViewById(R.id.friendcircle_clear);
	}

	public void initController() {
	}

	public void initViews() {
		fc_message_list = (ListView) findViewById(R.id.fc_message_list);
//		lvActive_footer = getLayoutInflater().inflate(R.layout.fc_listview_footer, null);
//		fc_message_list.addFooterView(lvActive_footer);
	}

	@SuppressWarnings("unchecked")
	public void setValues() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			list = (ArrayList<VideoData>) bundle.getSerializable("list");
		}
		if (!(list != null && list.size() > 0)) {
			friendcircle_clear.setVisibility(View.GONE);
		}
		adapter = new FriendCicleMessageAdapter(this, list);
		fc_message_list.setAdapter(adapter);
		fc_message_list.setOnItemClickListener(this);
	}

	public void setListeners() {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if (view == lvActive_footer) {
//			final ArrayList<VideoData> list1 = new ArrayList<VideoData>();
//			HashMap<String, VideoData> sdMap = new FCHelper(AccountData.getInstance().getUsername()).getAll_ReadFcNoti();
//			if (sdMap != null && sdMap.size() > 0) {
//				Iterator<String> iterator = sdMap.keySet().iterator();
//				while (iterator.hasNext()) {
//					String key = iterator.next().toString();
//					VideoData sd_temp = (VideoData) sdMap.get(key);
//					if (sd_temp != null) {
//						VideoData source_dynamic = (VideoData) FriendCircleCacheUtil.readObject(key, MyApplication.getInstance());
//						if (source_dynamic != null) {
//							list1.add(source_dynamic);
//						} else {
//							list1.add(sd_temp);
//						}
//					}
//				}
//				if (list != null && list.size() > 0) {
//					fc_message_list.removeFooterView(lvActive_footer);
//					list.addAll(list1);
//					Collections.sort(list, new SortDynamicByDate());
//					adapter.notifyDataSetChanged();
//				}
//			}
//
//		} else {
			//FIXME 暂不启用
//			Intent i = new Intent(FriendCircleMessageActivity.this, FriendCircleMessageDetailActivity.class);
//			i.putExtra("dynamic", (VideoData) list.get(position));
//			startActivity(i);
//		}
	}
	
	class SortDynamicByDate implements Comparator<Source_Dynamic> {
		public int compare(Source_Dynamic o1, Source_Dynamic o2) {
			Source_Dynamic s1 = (Source_Dynamic) o1;
			Source_Dynamic s2 = (Source_Dynamic) o2;
			return s2.optime.compareTo(s1.optime);
		}
	}

	/**
	 * 返回，数据库修改状态
	 */
	private void doBack() {
		if (list != null && list.size() > 0) {
			friendcircle_clear.setVisibility(View.VISIBLE);
			for (int i = 0; i < list.size(); i++) {
				VideoData sDynamic = (VideoData) list.get(i);
				sDynamic.states = "1";
				new FCHelper(AccountData.getInstance().getUsername()).addOrUpdateFcNoti(sDynamic.videoID, sDynamic);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击返回按钮，退出
		case R.id.friendcircle_back:
			doBack();
			finish();
			break;
		case R.id.friendcircle_clear:
			new AlertDialog.Builder(FriendCircleMessageActivity.this).setMessage(R.string.fc_clear_all_message).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							VideoData sDynamic = (VideoData) list.get(i);
							FriendCircleCacheUtil.removeDataCache(sDynamic.post_id, MyApplication.getInstance());
							list.remove(i);
						}
						list.clear();
						new FCHelper(AccountData.getInstance().getUsername()).clearAllFcNoti();
						adapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			}).create().show();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		doBack();
		return super.onKeyDown(keyCode, event);
	}
}

package com.lz.oncon.app.im.ui.common;

import java.util.ArrayList;

import com.xuanbo.xuan.R;
import com.lz.oncon.adapter.GridViewFaceAdapter;
import com.lz.oncon.adapter.HorizontialListViewAadapter;
import com.lz.oncon.adapter.GridViewFaceAdapter.FaceGroupLister;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.GifFaceData;
import com.lz.oncon.data.db.FaceHelper;
import com.lz.oncon.widget.HorizontialListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

public class IMMessageFaceBar extends LinearLayout {
	
	GridView tweet_detail_foot_faces;
	HorizontialListView typeSearchBar_typeBar;
	
	ArrayList<SIXmppMessage> msgs;
	ArrayList<GifFaceData> mfaceDatas;
	IMThreadData.Type mType;
	String mOnconId;
	
	HorizontialListViewAadapter hori_listViewAdapter;
	GridViewFaceAdapter grid_listFaceAdapter;
	FaceHelper cHelper;

	public IMMessageFaceBar(Context context) {
		super(context);
		init();
	}
	
	public IMMessageFaceBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public IMMessageFaceBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.app_im_message_facebar, this);
		
		cHelper = FaceHelper.getInstance(AccountData.getInstance().getUsername());
		mfaceDatas = cHelper.findClassCountByType("1");
		if (mfaceDatas != null) {
			GifFaceData gifDataSpec = new GifFaceData();
			gifDataSpec.setSpec(true);
			mfaceDatas.add(gifDataSpec);
		}
		
		tweet_detail_foot_faces = (GridView) findViewById(R.id.tweet_detail_foot_faces);
		typeSearchBar_typeBar = (HorizontialListView) findViewById(R.id.typeSearchBar_typeBar);
		
		if (hori_listViewAdapter == null) {
			hori_listViewAdapter = new HorizontialListViewAadapter(getContext(), mfaceDatas);
		}
		hori_listViewAdapter.setTabHost(0);
		typeSearchBar_typeBar.setAdapter(hori_listViewAdapter);

		typeSearchBar_typeBar.setOnItemClickListener(mOnItemClickListener);

		
	}
	
	public void setThread(IMThreadData.Type type, String onconId, ArrayList<SIXmppMessage> msgs){
		mType = type;
		mOnconId = onconId;
		this.msgs = msgs;
		if(SIXmppThreadInfo.Type.GROUP.ordinal() == mType.ordinal()){
		}else if(SIXmppThreadInfo.Type.P2P.ordinal() == mType.ordinal()){
			grid_listFaceAdapter = new GridViewFaceAdapter(getContext(), ((GifFaceData) (hori_listViewAdapter.getItem(hori_listViewAdapter.getTabHost()))).getClass_name()
					, msgs, AccountData.getInstance().getBindphonenumber(),SIXmppThreadInfo.Type.P2P);
			grid_listFaceAdapter.setmChat(ImCore.getInstance().getChatManager().createChat(mOnconId));
			grid_listFaceAdapter.setmOnconId(mOnconId);
			tweet_detail_foot_faces.setAdapter(grid_listFaceAdapter);
		}else if(SIXmppThreadInfo.Type.BATCH.ordinal() == mType.ordinal()){
			grid_listFaceAdapter = new GridViewFaceAdapter(getContext(), ((GifFaceData) (hori_listViewAdapter.getItem(hori_listViewAdapter.getTabHost()))).getClass_name()
					, msgs, AccountData.getInstance().getBindphonenumber(),SIXmppThreadInfo.Type.BATCH);
			grid_listFaceAdapter.setmChat(ImCore.getInstance().getChatManager().createChat(mOnconId));
			grid_listFaceAdapter.setmOnconId(mOnconId);
			tweet_detail_foot_faces.setAdapter(grid_listFaceAdapter);
		}
	}
	
	public void setFaceGroupLister(FaceGroupLister faceGroupLister){
		grid_listFaceAdapter.setmFaceListener(faceGroupLister);
	}
	
	public void refresh(){
		grid_listFaceAdapter.notifyDataSetChanged();
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			hori_listViewAdapter.setTabHost(arg2);
			GifFaceData facedata = hori_listViewAdapter.getmDatas().get(arg2);
			if (facedata.isSpec()) {
				if (!TextUtils.isEmpty(AccountData.getInstance().getBindphonenumber())) {
					
				}

				return;
			} else {
				hori_listViewAdapter.notifyDataSetChanged();
				if(SIXmppThreadInfo.Type.GROUP.ordinal() == mType.ordinal()){
				}else if(SIXmppThreadInfo.Type.P2P.ordinal() == mType.ordinal()){
					grid_listFaceAdapter = new GridViewFaceAdapter(getContext(), ((GifFaceData) (hori_listViewAdapter.getItem(hori_listViewAdapter.getTabHost()))).getClass_name()
							, msgs, AccountData.getInstance().getBindphonenumber(),SIXmppThreadInfo.Type.P2P);
					grid_listFaceAdapter.setmChat(ImCore.getInstance().getChatManager().createChat(mOnconId));
				}else if(SIXmppThreadInfo.Type.BATCH.ordinal() == mType.ordinal()){
					grid_listFaceAdapter = new GridViewFaceAdapter(getContext(), ((GifFaceData) (hori_listViewAdapter.getItem(hori_listViewAdapter.getTabHost()))).getClass_name()
							, msgs, AccountData.getInstance().getBindphonenumber(),SIXmppThreadInfo.Type.BATCH);
				}
				grid_listFaceAdapter.setmOnconId(mOnconId);
				tweet_detail_foot_faces.setAdapter(grid_listFaceAdapter);
				grid_listFaceAdapter.notifyDataSetChanged();
			}
		}
	};
	
	final Handler cwjHandler = new Handler();

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (hori_listViewAdapter != null)
				hori_listViewAdapter.notifyDataSetChanged();
		}
	};
}
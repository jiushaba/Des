package com.lz.oncon.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.lb.common.util.ImageLoaderForAdapter;
import com.lb.common.util.SocialPicPath;
import com.xuanbo.xuan.R;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.widget.TitleView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.UMFriend;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchFriendsListener;

public class ShareTabSocialChooseActivity extends TabBaseActivity implements
		ListView.OnScrollListener {
	private TitleView title;
	private EditText et_search;

	private TextView sina_no_data_tv;
	private LinearLayout sina_pb;
	private ListView sina_listview;
	private TextView tencent_no_data_tv;
	private LinearLayout tencent_pb;
	private ListView tencent_listview;

	private LinearLayout sl;
	private LinearLayout tl;

	private TabHost tabHost;
	private TabHost.TabSpec spec;

	private Intent intent;
	private Bundle b;
	private boolean sina = false;
	private boolean tencent = false;

	private Sina_Attention_Adapter sina_adapter;
	private Tencent_Attention_Adapter tencent_adapter;
	private Sina_Attention_Adapter1 sina_adapter1;
	private Tencent_Attention_Adapter1 tencent_adapter1;

	private ArrayList<Integer> sina_isSelected = new ArrayList<Integer>();
	private ArrayList<Integer> sina_isSelected1 = new ArrayList<Integer>();
	private ArrayList<Integer> tencent_isSelected = new ArrayList<Integer>();
	private ArrayList<Integer> tencent_isSelected1 = new ArrayList<Integer>();
	
	private ArrayList<String> ss = new ArrayList<String>();
	private ArrayList<String> ss1 = new ArrayList<String>();
	private ArrayList<String> tt = new ArrayList<String>();
	private ArrayList<String> tt1 = new ArrayList<String>();
	private ArrayList<String> ttr = new ArrayList<String>();
	private ArrayList<String> ttr1 = new ArrayList<String>();
	
	ArrayList<String> salist = new ArrayList<String>();
	ArrayList<String> talist = new ArrayList<String>();
	ArrayList<String> talistr = new ArrayList<String>();
	
	ArrayList<String> nList = new ArrayList<String>();
	ArrayList<String> nIconList = new ArrayList<String>();
	ArrayList<String> tnList = new ArrayList<String>();
	ArrayList<String> tnList_r = new ArrayList<String>();
	ArrayList<String> tnIconList = new ArrayList<String>();
	
	
	private List<UMFriend> tencentlist;
	private List<UMFriend> sinalist;

	private LinearLayout slated;
	private ListView slvated;

	private LinearLayout tlated;
	private ListView tlvated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();

	}

	public void initController() {
		intent = getIntent();
		b = intent.getExtras();
		if (b != null) {
			sina = b.getBoolean("sina");
			tencent = b.getBoolean("tencent");
		}
	}

	public void initContentView() {
		setContentView(R.layout.speak_image_share_choose_contacts);
	}

	public void initViews() {
		title = (TitleView) findViewById(R.id.title);

		tabHost = getTabHost();
		spec = tabHost.newTabSpec("0")
				.setIndicator(createTabView(R.string.weibo_share_sina_title))
				.setContent(R.id.choose_sina_ll);
		tabHost.addTab(spec);
		spec = tabHost.newTabSpec("1")
				.setIndicator(createTabView(R.string.weibo_share_qq_title))
				.setContent(R.id.choose_tencent_ll);
		tabHost.addTab(spec);
		if (sina) {
			tabHost.setCurrentTab(0);
		} else {
			if (tencent) {
				tabHost.setCurrentTab(1);
			}
		}
		updateTabBackground(tabHost);

		et_search = (EditText) findViewById(R.id.choose_search);

		sina_no_data_tv = (TextView) findViewById(R.id.choose_sina_no_data);
		sina_pb = (LinearLayout) findViewById(R.id.choose_sina_pb);
		sina_listview = (ListView) findViewById(R.id.choose_sina_lv);

		tencent_no_data_tv = (TextView) findViewById(R.id.choose_tencent_no_data);
		tencent_pb = (LinearLayout) findViewById(R.id.choose_tencent_pb);
		tencent_listview = (ListView) findViewById(R.id.choose_tencent_lv);

		sl = (LinearLayout) findViewById(R.id.choose_sina_friendlist);
		tl = (LinearLayout) findViewById(R.id.choose_tencent_friendlist);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				updateTabBackground(tabHost);
			}
		});

		slated = (LinearLayout) findViewById(R.id.choose_sina_friendlist_ated);
		slvated = (ListView) findViewById(R.id.choose_sina_lv_ated);

		tlated = (LinearLayout) findViewById(R.id.choose_tencent_friendlist_ated);
		tlvated = (ListView) findViewById(R.id.choose_tencent_lv_ated);

	}

	private void updateTabBackground(final TabHost tabHost) {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			View vvv = tabHost.getTabWidget().getChildAt(i);
			if (tabHost.getCurrentTab() == i) {
				// 选中后的背景
				vvv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.more_top_pressed));
			} else {
				// 非选择的背景
				vvv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.more_top));
			}
		}
	}

	public void setValues() {
		if (sina) {
			sina_no_data_tv.setVisibility(View.GONE);
			sina_pb.setVisibility(View.VISIBLE);
			sina_listview.setVisibility(View.GONE);
			sina_listview.setOnScrollListener(this);
			slated.setVisibility(View.GONE);
			slvated.setVisibility(View.GONE);
			// 加载sina_listview数据
			getFriendSina(SHARE_MEDIA.SINA);
		} else {
			sina_no_data_tv.setVisibility(View.VISIBLE);
			sina_listview.setVisibility(View.GONE);
			sina_pb.setVisibility(View.GONE);
			sina_no_data_tv.setText(getString(R.string.share_no_auo_no_choose));
		}

		if (tencent) {
			tencent_no_data_tv.setVisibility(View.GONE);
			tencent_pb.setVisibility(View.VISIBLE);
			tencent_listview.setVisibility(View.GONE);
			tencent_listview.setOnScrollListener(this);
			tlated.setVisibility(View.GONE);
			tlvated.setVisibility(View.GONE);
			// 加载tencent_listview数据
			getFriendTencent(SHARE_MEDIA.TENCENT);
		} else {
			tencent_no_data_tv.setVisibility(View.VISIBLE);
			tencent_listview.setVisibility(View.GONE);
			tencent_pb.setVisibility(View.GONE);
			tencent_no_data_tv
					.setText(getString(R.string.share_no_auo_no_choose));
		}

		et_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		et_search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				uiHandler.sendEmptyMessage(SEARCH_AT_CONTACTS);
				return true;
			}
		});
	}

	public void getFriendSina(final SHARE_MEDIA platform) {
		MyApplication.getInstance().umService.getFriends(
				ShareTabSocialChooseActivity.this, new FetchFriendsListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onComplete(int arg0, List<UMFriend> arg1) {
						Message m = new Message();
						m.what = GET_DATA_SUCCESS_SINA;
						m.obj = arg1;
						uiHandler.sendMessage(m);
					}
				}, platform);
	}

	public void getFriendTencent(final SHARE_MEDIA platform) {
		MyApplication.getInstance().umService.getFriends(
				ShareTabSocialChooseActivity.this, new FetchFriendsListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onComplete(int arg0, List<UMFriend> arg1) {
						Message m = new Message();
						m.what = GET_DATA_SUCCESS_TENCENT;
						m.obj = arg1;
						uiHandler.sendMessage(m);
					}
				}, platform);
	}

	public void setListeners() {
		sina_listview.setOnScrollListener(this);
		tencent_listview.setOnScrollListener(this);

		// 新浪 item单击事件
		sina_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				holder.cb.toggle();

				Sina_Attention_Adapter.getIsSelected().put(arg2,
						holder.cb.isChecked());
				sina_adapter.notifyDataSetChanged();

				if (holder.cb.isChecked()) {
					sina_isSelected.add(arg2);
				} else {
					if (sina_isSelected != null) {
						for (int i = 0; i < sina_isSelected.size(); i++) {
							if (sina_isSelected.get(i) == arg2) {
								sina_isSelected.remove(i);
							}
						}
					}
				}
			}
		});

		// 腾讯 item单击事件
		tencent_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				holder.cb.toggle();

				Tencent_Attention_Adapter.getIsSelected().put(arg2,
						holder.cb.isChecked());
				tencent_adapter.notifyDataSetChanged();

				if (holder.cb.isChecked()) {
					tencent_isSelected.add(arg2);
				} else {
					if (tencent_isSelected != null) {
						for (int i = 0; i < tencent_isSelected.size(); i++) {
							if (tencent_isSelected.get(i) == arg2) {
								tencent_isSelected.remove(i);
							}
						}
					}
				}
			}
		});

		slvated.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				holder.cb.toggle();

				Sina_Attention_Adapter1.getIsSelected().put(arg2,
						holder.cb.isChecked());
				sina_adapter1.notifyDataSetChanged();

				if (holder.cb.isChecked()) {
					sina_isSelected1.add(arg2);
				} else {
					if (sina_isSelected1 != null) {
						for (int i = 0; i < sina_isSelected1.size(); i++) {
							if (sina_isSelected1.get(i) == arg2) {
								sina_isSelected1.remove(i);
							}
						}
					}
				}
			}
		});

		tlvated.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				holder.cb.toggle();

				Tencent_Attention_Adapter1.getIsSelected().put(arg2,
						holder.cb.isChecked());
				tencent_adapter1.notifyDataSetChanged();

				if (holder.cb.isChecked()) {
					tencent_isSelected1.add(arg2);
				} else {
					if (tencent_isSelected1 != null) {
						for (int i = 0; i < tencent_isSelected1.size(); i++) {
							if (tencent_isSelected1.get(i) == arg2) {
								tencent_isSelected1.remove(i);
							}
						}
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_title_TV_right:
			uiHandler.sendEmptyMessage(RETURN_AT_CONTACTS_LIST);
			break;
		case R.id.common_title_TV_left:
			ShareTabSocialChooseActivity.this.finish();
			break;
		default:
			break;
		}
	}

	private View createTabView(int resId) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator,
				null);
		TextView tv = (TextView) view.findViewById(R.id.tab_tv);
		tv.setText(resId);
		return view;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE: //
			if(sina && sina_adapter!=null){
				sina_adapter.setBusy(false);
				sina_adapter.notifyDataSetChanged();
			}
			if(tencent && tencent_adapter!=null){
				tencent_adapter.setBusy(false);
				tencent_adapter.notifyDataSetChanged();
			}
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			if(sina && sina_adapter!=null){
				sina_adapter.setBusy(false);
				sina_adapter.notifyDataSetChanged();
			}
			if(tencent && tencent_adapter!=null){
				tencent_adapter.setBusy(false);
				tencent_adapter.notifyDataSetChanged();
			}
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			if(sina && sina_adapter!=null){
				sina_adapter.setBusy(true);
			}
			if(tencent && tencent_adapter!=null){
				tencent_adapter.setBusy(true);
			}
			break;
		default:
			break;
		}

	}

	public static class Sina_Attention_Adapter1 extends BaseAdapter {

		private Context c;
		private static ArrayList<String> list;
		private static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>(); // sina
		private LayoutInflater inflater;

		public Sina_Attention_Adapter1(Context mContext, ArrayList<String> mList) {
			this.c = mContext;
			this.list = mList;
			inflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return list != null ? list.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.speak_image_share_choose_contacts_item, null);
				viewHolder.iv = (ImageView) convertView
						.findViewById(R.id.choose_attention_iv);
				viewHolder.tv = (TextView) convertView
						.findViewById(R.id.choose_attention_name);
				viewHolder.cb = (CheckBox) convertView
						.findViewById(R.id.choose_attention_cb);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(BitmapFactory.decodeFile(SocialPicPath.getSaveFilePath(SocialPicPath.SINA_PIC_PATH)+list.get(position)) != null){
				viewHolder.iv.setImageBitmap(BitmapFactory.decodeFile(SocialPicPath.getSaveFilePath(SocialPicPath.SINA_PIC_PATH)+list.get(position)));
			}else{
				viewHolder.iv.setImageResource(R.drawable.avatar_img_loading);
			}
			viewHolder.tv.setText(list.get(position));
			viewHolder.cb.setChecked(getIsSelected().get(position));

			return convertView;
		}

		public static void setIsSelected(HashMap<Integer, Boolean> is) {
			isSelected = is;
		}

		public static HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public static ArrayList<String> getContacts() {
			return list;
		}
	}

	public static class Sina_Attention_Adapter extends BaseAdapter {

		private Context c;
		private static ArrayList<String> list;
		private static ArrayList<String> iconlist;
		private static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>(); // sina
		private LayoutInflater inflater;
		private boolean busy = false;
		private ImageLoaderForAdapter loader;
		
		public void setBusy(boolean busy){
			this.busy = busy;
		}

		public Sina_Attention_Adapter(Context mContext, ArrayList<String> mList, ArrayList<String> miconList) {
			this.c = mContext;
			this.list = mList;
			this.iconlist = miconList;
			inflater = LayoutInflater.from(c);
			loader = new ImageLoaderForAdapter();
		}

		@Override
		public int getCount() {
			return list != null ? list.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.speak_image_share_choose_contacts_item, null);
				viewHolder.iv = (ImageView) convertView
						.findViewById(R.id.choose_attention_iv);
				viewHolder.tv = (TextView) convertView
						.findViewById(R.id.choose_attention_name);
				viewHolder.cb = (CheckBox) convertView
						.findViewById(R.id.choose_attention_cb);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.iv.setTag(iconlist.get(position));
			if(!busy){
				loader.displayImage(iconlist.get(position), SocialPicPath.getSaveFilePath(SocialPicPath.SINA_PIC_PATH)+list.get(position), viewHolder.iv, false, list.get(position), SocialPicPath.SINA_PIC_PATH);
			}else{
//				loader.displayImage(iconlist.get(position), SocialPicPath.getSaveFilePath(SocialPicPath.SINA_PIC_PATH)+list.get(position), viewHolder.iv, true, list.get(position), SocialPicPath.SINA_PIC_PATH);
				File f = new File(SocialPicPath.getSaveFilePath(SocialPicPath.SINA_PIC_PATH)+list.get(position));
				Bitmap b = null;
				if (f != null && f.exists()){
					b = ImageLoaderForAdapter.decodeFile(f);
				}
				if (b != null){
					viewHolder.iv.setImageBitmap(b);
				}else{
					viewHolder.iv.setImageResource(R.drawable.avatar_img_loading);
				}
			}
			viewHolder.tv.setText(list.get(position));
			viewHolder.cb.setChecked(getIsSelected().get(position));

			return convertView;
		}

		public static void setIsSelected(HashMap<Integer, Boolean> is) {
			isSelected = is;
		}

		public static HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public static ArrayList<String> getContacts() {
			return list;
		}
	}

	public static class Tencent_Attention_Adapter1 extends BaseAdapter {

		private Context c;
		private static ArrayList<String> list;
		private static ArrayList<String> rlist;
		private static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>(); // tencent
		private LayoutInflater inflater;

		public Tencent_Attention_Adapter1(Context mContext,
				ArrayList<String> mList, ArrayList<String> rList) {
			this.c = mContext;
			this.list = mList;
			this.rlist = rList;
			inflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return rlist != null ? rlist.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return rlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.speak_image_share_choose_contacts_item, null);
				viewHolder.iv = (ImageView) convertView
						.findViewById(R.id.choose_attention_iv);
				viewHolder.tv = (TextView) convertView
						.findViewById(R.id.choose_attention_name);
				viewHolder.cb = (CheckBox) convertView
						.findViewById(R.id.choose_attention_cb);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if(BitmapFactory.decodeFile(SocialPicPath.getSaveFilePath(SocialPicPath.TENCENT_PIC_PATH)+rlist.get(position)) != null){
				viewHolder.iv.setImageBitmap(BitmapFactory.decodeFile(SocialPicPath.getSaveFilePath(SocialPicPath.TENCENT_PIC_PATH)+rlist.get(position)));
			}else{
				viewHolder.iv.setImageResource(R.drawable.avatar_img_loading);
			}
			viewHolder.tv.setText(rlist.get(position));
			viewHolder.cb.setChecked(getIsSelected().get(position));

			return convertView;
		}

		public static void setIsSelected(HashMap<Integer, Boolean> is) {
			isSelected = is;
		}

		public static HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public static ArrayList<String> getContacts() {
			return list;
		}

		public static ArrayList<String> getContactsr() {
			return rlist;
		}
	}

	public static class Tencent_Attention_Adapter extends BaseAdapter {

		private Context c;
		private static ArrayList<String> list;
		private static ArrayList<String> rlist;
		private static ArrayList<String> iconlist;
		private static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>(); // tencent
		private LayoutInflater inflater;
		private boolean busy = false;
		private ImageLoaderForAdapter loader;
		
		public void setBusy(boolean busy){
			this.busy = busy;
		}

		public Tencent_Attention_Adapter(Context mContext,
				ArrayList<String> mList, ArrayList<String> rList, ArrayList<String> iconList) {
			this.c = mContext;
			this.list = mList;
			this.rlist = rList;
			this.iconlist = iconList;
			inflater = LayoutInflater.from(c);
			loader = new ImageLoaderForAdapter();
		}

		@Override
		public int getCount() {
			return rlist != null ? rlist.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return rlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.speak_image_share_choose_contacts_item, null);
				viewHolder.iv = (ImageView) convertView
						.findViewById(R.id.choose_attention_iv);
				viewHolder.tv = (TextView) convertView
						.findViewById(R.id.choose_attention_name);
				viewHolder.cb = (CheckBox) convertView
						.findViewById(R.id.choose_attention_cb);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.iv.setTag(iconlist.get(position));
			if(!busy){
				loader.displayImage(iconlist.get(position), SocialPicPath.getSaveFilePath(SocialPicPath.TENCENT_PIC_PATH)+rlist.get(position), viewHolder.iv, false, rlist.get(position), SocialPicPath.TENCENT_PIC_PATH);
			}else{
//				loader.displayImage(iconlist.get(position), SocialPicPath.getSaveFilePath(SocialPicPath.TENCENT_PIC_PATH)+rlist.get(position), viewHolder.iv, true, rlist.get(position), SocialPicPath.TENCENT_PIC_PATH);
				File f = new File(SocialPicPath.getSaveFilePath(SocialPicPath.TENCENT_PIC_PATH)+rlist.get(position));
				Bitmap b = null;
				if (f != null && f.exists()){
					b = ImageLoaderForAdapter.decodeFile(f);
				}
				if (b != null){
					viewHolder.iv.setImageBitmap(b);
				}else{
					viewHolder.iv.setImageResource(R.drawable.avatar_img_loading);
				}
			}
			viewHolder.tv.setText(rlist.get(position));
			viewHolder.cb.setChecked(getIsSelected().get(position));

			return convertView;
		}

		public static void setIsSelected(HashMap<Integer, Boolean> is) {
			isSelected = is;
		}

		public static HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public static ArrayList<String> getContacts() {
			return list;
		}

		public static ArrayList<String> getContactsr() {
			return rlist;
		}
	}

	static class ViewHolder {
		ImageView iv;
		TextView tv;
		CheckBox cb;
	}

	public static final int GET_DATA_SUCCESS_SINA = 1001;
	public static final int GET_DATA_SUCCESS_TENCENT = 1002;
	public static final int RETURN_AT_CONTACTS_LIST = 1003;
	public static final int SEARCH_AT_CONTACTS = 1004;

	private UIHandler uiHandler = new UIHandler();
	private int index;

	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_DATA_SUCCESS_SINA:
				String sinaated = MyApplication.getInstance().mPreferencesMan
						.getSinaAted();
				if (sinaated != null && !"".equals(sinaated)) {
					String[] sinas = sinaated.split("@");
					salist = new ArrayList<String>();
					int indexx = 0;
					for (int i = 1; i < sinas.length; i++) {
						salist.add(sinas[i].trim());
						Sina_Attention_Adapter1.isSelected.put(indexx, false);
						indexx++;
						if (indexx == 3) {
							break;
						}
					}
					sina_adapter1 = new Sina_Attention_Adapter1(
							ShareTabSocialChooseActivity.this, salist);
					slvated.setAdapter(sina_adapter1);
					slated.setVisibility(View.VISIBLE);
					slvated.setVisibility(View.VISIBLE);
				}

				sinalist = (List<UMFriend>) msg.obj;
				nList = new ArrayList<String>();
				nIconList = new ArrayList<String>();
				index = 0;
				if (sinalist != null) {
					for (UMFriend f : sinalist) {
						nList.add(f.getLinkName());
						nIconList.add(f.getIcon());
						Sina_Attention_Adapter.isSelected.put(index, false);
						index++;
					}

					sina_adapter = new Sina_Attention_Adapter(
							ShareTabSocialChooseActivity.this, nList,nIconList);
					sina_listview.setAdapter(sina_adapter);
					
					sina_no_data_tv.setVisibility(View.GONE);
					sina_pb.setVisibility(View.GONE);
					sina_listview.setVisibility(View.VISIBLE);
					sl.setVisibility(View.VISIBLE);
				} else {
					sina_no_data_tv.setVisibility(View.VISIBLE);
					sina_pb.setVisibility(View.GONE);
				}

				break;
			case GET_DATA_SUCCESS_TENCENT:
				String tencentated = MyApplication.getInstance().mPreferencesMan
						.getTencentAted();
				String tencentatedreal = MyApplication.getInstance().mPreferencesMan
						.getTencentRealAted();
				if (tencentated != null && !"".equals(tencentated)) {
					String[] sinas = tencentated.split("@");
					String[] sinasr = tencentatedreal.split("@");
					talist = new ArrayList<String>();
					talistr = new ArrayList<String>();
					int indexxx = 0;
					for (int i = 1; i < sinas.length; i++) {
						talist.add(sinas[i].trim());
						talistr.add(sinasr[i].trim());
						Tencent_Attention_Adapter1.isSelected.put(indexxx,
								false);
						indexxx++;
						if (indexxx == 3) {
							break;
						}
					}
					tencent_adapter1 = new Tencent_Attention_Adapter1(
							ShareTabSocialChooseActivity.this, talist, talistr);
					tlvated.setAdapter(tencent_adapter1);
					tlated.setVisibility(View.VISIBLE);
					tlvated.setVisibility(View.VISIBLE);
				}

				tencentlist = (List<UMFriend>) msg.obj;
				tnList = new ArrayList<String>();
				tnList_r = new ArrayList<String>();
				tnIconList = new ArrayList<String>();
				index = 0;
				if (tencentlist != null) {
					for (UMFriend f : tencentlist) {
						tnList.add(f.getLinkName()); // @用
						tnList_r.add(f.getName()); // item显示用
						tnIconList.add(f.getIcon());
						Tencent_Attention_Adapter.isSelected.put(index, false);
						index++;
					}

					tencent_adapter = new Tencent_Attention_Adapter(
							ShareTabSocialChooseActivity.this, tnList, tnList_r, tnIconList);
					tencent_listview.setAdapter(tencent_adapter);

					tencent_no_data_tv.setVisibility(View.GONE);
					tencent_pb.setVisibility(View.GONE);
					tencent_listview.setVisibility(View.VISIBLE);
					tl.setVisibility(View.VISIBLE);
				} else {
					tencent_no_data_tv.setVisibility(View.VISIBLE);
					tencent_pb.setVisibility(View.GONE);
				}

				break;
			case RETURN_AT_CONTACTS_LIST:
				String sina_names = "";
				String tencent_name = "";
				String tencent_namer = "";
				if (sina) {
					ss = sina_adapter.getContacts();
					ss1 = sina_adapter1.getContacts();
					StringBuffer ssb = new StringBuffer();
					StringBuffer ssb1 = new StringBuffer();
					int reduces = 0;
					if (ss1 != null && ss != null) {
						for (int i = 0; i < ss.size(); i++) {
							for (int j = 0; j < ss1.size(); j++) {
								if (ss.get(i).equals(ss1.get(j))) {
									ss1.remove(j);
									reduces++;
								}
							}
						}
					}
					for (int i = 0; i < sina_isSelected.size(); i++) {
						ssb.append("@").append(ss.get(sina_isSelected.get(i)))
								.append(" ");
					}
					for (int i = 0; i < sina_isSelected1.size()-reduces; i++) {
						ssb1.append("@")
								.append(ss1.get(sina_isSelected1.get(i)))
								.append(" ");
					}
					sina_names = ssb.toString() + ssb1.toString();
					b.putString("at_sina", sina_names);
					if(sina_names != null && !"".equals(sina_names)){
						MyApplication.getInstance().mPreferencesMan
						.setSinaAted(sina_names);
					}
				}

				if (tencent) {
					tt = tencent_adapter.getContacts();
					ttr = tencent_adapter.getContactsr();
					tt1 = tencent_adapter1.getContacts();
					ttr1 = tencent_adapter1.getContactsr();
					StringBuffer tsb = new StringBuffer();
					StringBuffer tsbr = new StringBuffer();
					StringBuffer tsb1 = new StringBuffer();
					StringBuffer tsbr1 = new StringBuffer();
					int reducet = 0;
					if (tt1 != null && tt != null) {
						for (int i = 0; i < tt.size(); i++) {
							for (int j = 0; j < tt1.size(); j++) {
								if (tt.get(i).equals(tt1.get(j))) {
									tt1.remove(j);
									ttr1.remove(j);
									reducet++;
								}
							}
						}
					}
					for (int i = 0; i < tencent_isSelected.size(); i++) {
						tsb.append("@")
								.append(tt.get(tencent_isSelected.get(i)))
								.append(" ");
						tsbr.append("@")
								.append(ttr.get(tencent_isSelected.get(i)))
								.append(" ");
					}
					for (int i = 0; i < tencent_isSelected1.size()-reducet; i++) {
						tsb1.append("@")
								.append(tt1.get(tencent_isSelected1.get(i)))
								.append(" ");
						tsbr1.append("@")
								.append(ttr1.get(tencent_isSelected1.get(i)))
								.append(" ");
					}

					tencent_name = tsb.toString() + tsb1.toString();
					tencent_namer = tsbr.toString() + tsbr1.toString();
					b.putString("at_tencent", tencent_name);
					if(tencent_name != null && !"".equals(tencent_name)){
						MyApplication.getInstance().mPreferencesMan
						.setTencentAted(tencent_name);
						MyApplication.getInstance().mPreferencesMan
						.setTencentRealAted(tencent_namer);
					}
				}
				ShareTabSocialChooseActivity.this.finish();
				break;
			case SEARCH_AT_CONTACTS:

				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShareTabSocialChooseActivity.this.finish();
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ss != null){
			ss.clear();
		}
		if(ss1 != null){
			ss1.clear();
		}
		if(tt != null){
			tt.clear();
		}
		if(tt1 != null){
			tt1.clear();
		}
		if(ttr != null){
			ttr.clear();
		}
		if(ttr1 != null){
			ttr1.clear();
		}
		if(sinalist != null){
			sinalist.clear();
		}
		if(tencentlist != null){
			tencentlist.clear();
		}
		if(Sina_Attention_Adapter1.list != null){
			Sina_Attention_Adapter1.list.clear();
		}
		if(Sina_Attention_Adapter1.isSelected != null){
			Sina_Attention_Adapter1.isSelected.clear();
		}
		if(Sina_Attention_Adapter.list != null){
			Sina_Attention_Adapter.list.clear();
		}
		if(Sina_Attention_Adapter.isSelected != null){
			Sina_Attention_Adapter.isSelected.clear();
		}
		if(Tencent_Attention_Adapter1.list != null){
			Tencent_Attention_Adapter1.list.clear();
		}
		if(Tencent_Attention_Adapter1.rlist != null){
			Tencent_Attention_Adapter1.rlist.clear();
		}
		if(Tencent_Attention_Adapter1.isSelected != null){
			Tencent_Attention_Adapter1.isSelected.clear();
		}
		if(Tencent_Attention_Adapter.list != null){
			Tencent_Attention_Adapter.list.clear();
		}
		if(Tencent_Attention_Adapter.rlist != null){
			Tencent_Attention_Adapter.rlist.clear();
		}
		if(Tencent_Attention_Adapter.isSelected != null){
			Tencent_Attention_Adapter.isSelected.clear();
		}
		if(salist != null){
			salist.clear();
		}
		if(talist != null){
			talist.clear();
		}
		if(talistr != null){
			talistr.clear();
		}
		if(nList != null){
			nList.clear();
		}
		if(tnList != null){
			tnList.clear();
		}
		if(tnList_r != null){
			tnList_r.clear();
		}
		if(tnIconList != null){
			tnIconList.clear();
		}
		if(nIconList != null){
			nIconList.clear();
		}
	}

}

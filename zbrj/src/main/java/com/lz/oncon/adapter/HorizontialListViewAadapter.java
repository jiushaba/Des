package com.lz.oncon.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lb.common.util.ImageLoader;
import com.lb.common.util.ResourceUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.GifFaceData;
import com.lz.oncon.data.db.FaceHelper;

public class HorizontialListViewAadapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<GifFaceData> mDatas;
	private int tabHost;
	private String class_name = "";

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	FaceHelper cHelper = null;

	public int getTabHost() {
		return tabHost;
	}

	public void setTabHost(int tabHost) {
		this.tabHost = tabHost;
		System.out.println("点击第" + tabHost + "分类");
	}

	public ArrayList<GifFaceData> getmDatas() {
		return mDatas;
	}

	public void setmDatas(ArrayList<GifFaceData> mDatas) {
		this.mDatas = mDatas;
	}

	public HorizontialListViewAadapter(Context c, ArrayList<GifFaceData> mDatas) {
		mContext = c;
		this.mDatas = mDatas;
		cHelper = FaceHelper.getInstance(AccountData.getInstance().getUsername());
		// mDatas = cHelper.findClassCountByType("1");
		// if(mDatas!=null){
		// GifFaceData gifDataSpec = new GifFaceData();
		// gifDataSpec.setSpec(true);
		// mDatas.add(gifDataSpec);
		// }
	}

	@Override
	public int getCount() {
		if (mDatas == null) {
			return 0;
		} else {
			return mDatas.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mDatas != null && position >= 0 && position < mDatas.size()) {
			return mDatas.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_horizontial_listview_item, null);
			holder.head = (ImageView) convertView.findViewById(R.id.message_class_face);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GifFaceData facedata = mDatas.get(position);
		if (facedata.isSpec()) {
			holder.head.setImageResource(R.drawable.icon_add);
		}else if (tabHost == position) {
			String class_name = facedata.getClass_name();
			setClass_name(class_name);
			GifFaceData select_facedata = cHelper.findPressImageByType("2", class_name);
			if (select_facedata != null) {
					String result = select_facedata.getImage_name();
					if (result.indexOf(".") != -1) {
						result = result.substring(0, result.indexOf("."));
					}
					if (select_facedata.getIsdefault() != null && select_facedata.getIsdefault().equals("0")) {
						int resId;
						try {
							resId = ResourceUtil.getRawIdx(result);
							holder.head.setImageResource(resId);
						} catch (Exception e) {
							e.printStackTrace();
							String path1 = IMConstants.PATH_FACE_PICTURE + select_facedata.getImage_name().concat(".").concat(select_facedata.getExtension_name());
							holder.head.setImageBitmap(ImageLoader.loadRoundBitmapFromFile(path1));
						}
					} else if (select_facedata.getIsdefault() != null && select_facedata.getIsdefault().equals("1")) {
						String faceName = result.concat(".").concat(select_facedata.getExtension_name());
						String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(faceName);
						String picRemoteUrl = select_facedata.getSuburl().concat(faceName);
//						FaceHelper.loadGifFaceNew(mContext, picRemoteUrl, picLocalPath, faceName, holder.head, select_facedata);
						ImageLoader.getInstance().displayImage(picRemoteUrl, picLocalPath, holder.head, false, faceName);
					}
			}else{
				String result = facedata.getImage_name();
				if (result.indexOf(".") != -1) {
					result = result.substring(0, result.indexOf("."));
				}
				String faceName = result.concat(".").concat(facedata.getExtension_name());
				String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(faceName);
				String picRemoteUrl = facedata.getSuburl().concat(faceName);
//				FaceHelper.loadGifFaceNew(mContext, picRemoteUrl, picLocalPath, faceName, holder.head, facedata);
				ImageLoader.getInstance().displayImage(picRemoteUrl, picLocalPath, holder.head, false, faceName);
			}
		} else {
			String result = facedata.getImage_name();
			if (result.indexOf(".") != -1) {
				result = result.substring(0, result.indexOf("."));
			}
			if (facedata.getIsdefault() != null && facedata.getIsdefault().equals("0")) {
				int resId;
				try {
					resId = ResourceUtil.getRawIdx(result);
					holder.head.setImageResource(resId);
				} catch (Exception e) {
					e.printStackTrace();
//					String path1 = IMConstants.PATH_FACE_PICTURE + facedata.getImage_name().concat(".").concat(facedata.getExtension_name());
//					holder.head.setImageBitmap(HeadBitmapData.loadRoundBitmapFromFile(path1));
					holder.head.setImageResource(R.drawable.icon_add);
				}
			} else if (facedata.getIsdefault() != null && facedata.getIsdefault().equals("1")) {
				String faceName = facedata.getImage_name().concat(".").concat(facedata.getExtension_name());
				String picLocalPath = IMConstants.PATH_FACE_PICTURE.concat(faceName);
				String picRemoteUrl = facedata.getSuburl().concat(faceName);
//				FaceHelper.loadGifFaceNew(mContext, picRemoteUrl, picLocalPath, faceName, holder.head, facedata);
				ImageLoader.getInstance().displayImage(picRemoteUrl, picLocalPath, holder.head, false, faceName);
			}
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView head;
	}
}

package com.lz.oncon.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.lb.common.util.Log;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.imagefilter.instance.BlackWhiteFilter;
import com.lz.oncon.imagefilter.instance.ColorToneFilter;
import com.lz.oncon.imagefilter.instance.EdgeFilter;
import com.lz.oncon.imagefilter.instance.HslModifyFilter;
import com.lz.oncon.imagefilter.instance.IImageFilter;
import com.lz.oncon.imagefilter.instance.Image;
import com.lz.oncon.imagefilter.instance.VideoFilter;
import com.lz.oncon.imagefilter.instance.YCBCrLinearFilter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class ImageFilterActivity extends BaseActivity {

	private ImageView imageView;
	String image_path;
	Bitmap bitmap;
	Button ok;
	TextView title_TV_left, title_TV_right;
	Builder builder;
	String[] filterDscr = { "原图", "高清", "湖水", "日系", "怀旧", "黑白", "素描" };
	String onconId;
	RelativeLayout titleLayout, nofilterBottom, filterBottom, galleryFilterLayout;
	String picPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_filter_pre);
		Bundle b = getIntent().getExtras();
		if (b != null && b.containsKey("image_path")) {
			image_path = b.getString("image_path");
			onconId = b.getString("data");
		}
		imageView = (ImageView) findViewById(R.id.imgfilter);
		filterBottom = (RelativeLayout) findViewById(R.id.filterBottom);
		builder = new Builder(this);
		bitmap = BitmapFactory.decodeFile(image_path);
		imageView.setImageBitmap(bitmap);
		LoadImageFilter();

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sendBtn:
			// 如果图片没有用滤镜，那么图片就用之前的图片发送即可，如果添加了滤镜效果，那么还要保存添加滤镜效果的图片到本地，然后再发送
			try {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(image_path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Intent iFilter = new Intent();
			iFilter.putExtra("imagepath", image_path);
			setResult(RESULT_OK, iFilter);
			finish();
			break;
		case R.id.bottomleft:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.rotate_IV:
			// 用于拍照后旋转
			try{
				bitmap = SystemCamera.rotatePic(bitmap);
				imageView.setImageBitmap(bitmap);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			break;
		}
	}

	private void LoadImageFilter() {
		Gallery gallery = (Gallery) findViewById(R.id.galleryFilter);
		final ImageFilterAdapter filterAdapter = new ImageFilterAdapter(
				ImageFilterActivity.this);
		gallery.setAdapter(new ImageFilterAdapter(ImageFilterActivity.this));
		gallery.setSelection(2);
		gallery.setAnimationDuration(3000);
		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				FilterInfo info = filterAdapter.getItem(position);

				if ("原图".equals(info.getDescription())) {
					bitmap = BitmapFactory.decodeFile(image_path);
					imageView.setImageBitmap(bitmap);
				} else {
					IImageFilter filter = info.getFilter();
					new ProcessImageTask(ImageFilterActivity.this, filter).execute();
				}
			}
		});
	}

	public class ProcessImageTask extends AsyncTask<Void, Void, Bitmap> {
		private IImageFilter filter;

		public ProcessImageTask(Activity activity, IImageFilter imageFilter) {
			this.filter = imageFilter;
			showProgressDialog(R.string.apply_colour, false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		public Bitmap doInBackground(Void... params) {
			Image img = null;
			try {
				img = new Image(bitmap);
				if (filter != null) {
					img = filter.process(img);
					img.copyPixelsFromBuffer();
				}
				bitmap = img.getImage();
				return img.getImage();
			} catch (Exception e) {
				if (img != null && img.destImage.isRecycled()) {
					img.destImage.recycle();
					img.destImage = null;
					System.gc();
				}
			} finally {
				if (img != null && img.image.isRecycled()) {
					img.image.recycle();
					img.image = null;
					System.gc();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				super.onPostExecute(result);
				imageView.setImageBitmap(result);
			}
			hideProgressDialog();
		}
	}

	private class FilterInfo {
		int filterID;
		IImageFilter filter;
		String description;

		public FilterInfo(int filterID, IImageFilter filter, String description) {
			this.filterID = filterID;
			this.filter = filter;
			this.description = description;
		}

		public IImageFilter getFilter() {
			return filter;
		}

		public String getDescription() {
			return description;
		}

	}

	public class ImageFilterAdapter extends BaseAdapter {
		private Context mContext;
		private List<FilterInfo> filterArray = new ArrayList<FilterInfo>();
		int id;

		public ImageFilterAdapter(Context c) {
			mContext = c;
			filterArray.add(new FilterInfo(R.drawable.filter_source,
					new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_STAGGERED),
					filterDscr[0]));
			filterArray.add(new FilterInfo(R.drawable.filter_gaoqing,
					new YCBCrLinearFilter(new YCBCrLinearFilter.Range(-0.3f, 0.3f)), 
					filterDscr[1]));
			filterArray.add(new FilterInfo(R.drawable.filter_hushui,
					new YCBCrLinearFilter(new YCBCrLinearFilter.Range(-0.276f, 0.163f), new YCBCrLinearFilter.Range(-0.202f, 0.5f)),
					filterDscr[2]));
			filterArray.add(new FilterInfo(R.drawable.filter_rixi,
					new HslModifyFilter(150f), 
					filterDscr[3]));
			filterArray.add(new FilterInfo(R.drawable.filter_huaijiu,
					new ColorToneFilter(Color.rgb(33, 168, 254), 192),
					filterDscr[4]));
			filterArray.add(new FilterInfo(R.drawable.filter_black,
					new BlackWhiteFilter(), 
					filterDscr[5]));
			filterArray.add(new FilterInfo(R.drawable.filter_sumiao,
					new EdgeFilter(), 
					filterDscr[6]));
		}

		public int getCount() {
			return filterArray.size();
		}

		public FilterInfo getItem(int position) {
			return position < filterArray.size() ? filterArray.get(position)
					: null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Bitmap bmImg = BitmapFactory.decodeResource(mContext.getResources(), filterArray.get(position).filterID);
			FrameLayout layout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.filter_gallery_item, null);
			bmImg.recycle();
			ImageView imageview = (ImageView) layout.findViewById(R.id.filterLogo);
			imageview.setBackgroundResource(filterArray.get(position).filterID);
			TextView idT = (TextView) layout.findViewById(R.id.filterDesc);
			idT.setText(filterArray.get(position).getDescription());
			return layout;
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			System.gc();
		}
	}
}
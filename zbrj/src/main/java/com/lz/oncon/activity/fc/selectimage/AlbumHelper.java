package com.lz.oncon.activity.fc.selectimage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;

import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.lz.oncon.activity.friendcircle.image.Utils;

/**
 * 获取相册图片
 * 
 * @author Administrator
 * 
 */
public class AlbumHelper {
	Context context;
	ContentResolver cr;

	// 图片列表
	public static HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

	private static AlbumHelper instance;
	
	/**
	 * 是否创建了图片集
	 */
	boolean hasBuildImagesBucketList = false;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}

	/**
	 * 获取所有的图片list
	 */
	@SuppressWarnings("unused")
	void buildImagesBucketList() {
		long startTime = System.currentTimeMillis();

		// 构造相册索引
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME,Media.DATE_ADDED };
		// 得到一个游标
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		if (cur.moveToFirst()) {
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
			int date_addedIndex = cur.getColumnIndexOrThrow(Media.DATE_ADDED);
			// 获取图片总数
			int totalNum = cur.getCount();
			Log.d(Constants.LOG_TAG,"图片总数：" + totalNum);
			do {
				String _id = cur.getString(photoIDIndex);
				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String picasaId = cur.getString(picasaIdIndex);
				String date_added = cur.getString(date_addedIndex);
				
				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<ImageItem>();
					bucket.bucketName = bucketName;
				}
				if (!TextUtils.isEmpty(path)) {
						File f = new File(path);
						if(f.exists()&&Utils.isImage(f, f.getName())&&f.canRead()&&f.length()>0){
							String createTime = String.valueOf(f.lastModified());
							ImageItem imageItem = new ImageItem();
							imageItem.imageId = _id;
							imageItem.imagePath = path;
							imageItem.date_added = date_added;
							if (!TextUtils.isEmpty(createTime)) {
								imageItem.date_added = createTime;
							}
							imageItem.isCamera = false;
							imageItem.isSelected = false;
							bucket.imageList.add(imageItem);
						
						}
				}

			} while (cur.moveToNext());
//			Log.d(Constants.LOG_TAG,"过滤后总数量dataList.size()：" + dataList.size());
		}
		if (cur != null) {
			cur.close();
		}
		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d(Constants.LOG_TAG,"use time: " + (endTime - startTime) + " ms");
	}
	
	/**
	 * 得到图片集,包括所有图片集
	 * @param refresh
	 * @return
	 */
	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !hasBuildImagesBucketList)) {
			buildImagesBucketList();
		}
		List<ImageBucket> tmpList = null;
		tmpList = new ArrayList<ImageBucket>();
		if (bucketList != null) {
			Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr.next();
				ImageBucket ib = entry.getValue();
				if(ib!=null&&ib.imageList!=null&&ib.imageList.size()>0){
					tmpList.add(ib);
				}
			}
		}
		return tmpList;
	}
	
	/**
	 * 获取所有的图片list
	 */
	public List<ImageItem> getImageItemList(List<ImageBucket> bucketList) {
		List<ImageItem> dataList = null;
		dataList = new ArrayList<ImageItem>();
		if (bucketList != null) {
			for(int i=0;i<bucketList.size();i++){
				ImageBucket ib = bucketList.get(i);
				if(ib!=null&&ib.imageList!=null&&ib.imageList.size()>0){
					dataList.addAll(ib.imageList);
				}
			}
		}
		//按照时间由大到小排序
		Collections.sort(dataList, new Comparator<ImageItem>() {
			@Override
			public int compare(ImageItem lhs, ImageItem rhs) {
				return StringUtils.repNull(rhs.date_added).compareTo(StringUtils.repNull(lhs.date_added));
			}
			
		});
		return dataList;
	}
}

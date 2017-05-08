package com.lz.oncon.activity.fc.selectimage;

import java.util.List;

/**
 * 一个目录的相册对象
 * 
 * @author Administrator
 * 
 */
public class ImageBucket {
	public int count = 0;
	public String bucketName;
	public List<ImageItem> imageList;
	@Override
	public String toString() {
		return "ImageBucket [count=" + count + ", bucketName=" + bucketName + ", imageList.size()=" + imageList.size() + "]";
	}
	

}

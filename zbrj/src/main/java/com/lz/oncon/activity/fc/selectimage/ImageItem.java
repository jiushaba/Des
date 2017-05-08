package com.lz.oncon.activity.fc.selectimage;

import java.io.Serializable;

public class ImageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6352750583768738747L;
	public String imageId;
//	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
	public boolean isCamera = false;//是否照相机
	public String date_added;
	public boolean fromCamera = false;//来源照相机
	@Override
	public String toString() {
		return "ImageItem [imageId=" + imageId + ", imagePath=" + imagePath + ", isSelected=" + isSelected + ", isCamera=" + isCamera + ", date_added=" + date_added + ", fromCamera=" + fromCamera + "]";
	}
	
}

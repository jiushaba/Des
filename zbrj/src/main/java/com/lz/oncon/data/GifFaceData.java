package com.lz.oncon.data;


public class GifFaceData {

	private String  image_name = ""; //图片名
	private String  isdefault ="0";// 是否默认图片（0默认，1非默认）(修改后字段变为 0 代表资源中获取 ，1代表存储卡中获取)
	private String  image_des = "";// 图片描述
	private String  class_name ="";// 类名（未选中图片名）
	private String  isclassImage = "0";// 是否为类图片（0不是类图，1类图未选中图片 ，2类图选中图片）
	private String  extension_name ="png";// 图片后缀名
	private String  suburl = "";//网络下载图片的URL
	private boolean isSpec = false;//
	private boolean loadGifResult=false;//网络加载图示是否成功
	public boolean isLoadGifResult() {
		return loadGifResult;
	}
	public void setLoadGifResult(boolean loadGifResult) {
		this.loadGifResult = loadGifResult;
	}
	private int image_ResourceID;
	private int text_ResourceID;
	
	public int getText_ResourceID() {
		return text_ResourceID;
	}
	public void setText_ResourceID(int text_ResourceID) {
		this.text_ResourceID = text_ResourceID;
	}
	public int getImage_ResourceID() {
		return image_ResourceID;
	}
	public void setImage_ResourceID(int image_ResourceID) {
		this.image_ResourceID = image_ResourceID;
	}
	public String getSuburl() {
		return com.lb.common.util.StringUtils.repNull(suburl);
	}
	public void setSuburl(String suburl) {
		this.suburl = suburl;
	}
	public boolean isSpec() {
		return isSpec;
	}
	public void setSpec(boolean isSpec) {
		this.isSpec = isSpec;
	}
	public String getImage_name() {
		return com.lb.common.util.StringUtils.repNull(image_name);
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public String getIsdefault() {
		return com.lb.common.util.StringUtils.repNull(isdefault);
	}
	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}
	public String getImage_des() {
		return com.lb.common.util.StringUtils.repNull(image_des);
	}
	public void setImage_des(String image_des) {
		this.image_des = image_des;
	}
	public String getClass_name() {
		return com.lb.common.util.StringUtils.repNull(class_name);
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	public String getIsclassImage() {
		return com.lb.common.util.StringUtils.repNull(isclassImage);
	}
	public void setIsclassImage(String isclassImage) {
		this.isclassImage = isclassImage;
	}
	public String getExtension_name() {
		return com.lb.common.util.StringUtils.repNull(extension_name);
	}
	public void setExtension_name(String extension_name) {
		this.extension_name = extension_name;
	}
}

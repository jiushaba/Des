package com.lz.oncon.activity.friendcircle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lb.zbrj.data.VideoData;


public class Source_DynamicList implements Serializable{
	private static final long serialVersionUID = 19801980;
	protected String cacheKey;
	private List<VideoData> sourceDynamicList = new ArrayList<VideoData>();//我的足迹

	public List<VideoData> getSourceDynamicList() {
		return sourceDynamicList;
	}

	public void setSourceDynamicList(List<VideoData> sourceDynamicList) {
		this.sourceDynamicList = sourceDynamicList;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}

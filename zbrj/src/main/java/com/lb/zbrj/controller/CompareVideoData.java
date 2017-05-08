package com.lb.zbrj.controller;

import java.util.Comparator;

import com.lb.zbrj.data.VideoData;

public class CompareVideoData implements Comparator<Object> {

	@Override
	public int compare(Object obj, Object obj1) {
		if(obj == null || obj1 == null){
			return 0;
		}
		if(obj instanceof VideoData || obj1 instanceof VideoData){
			VideoData v = (VideoData)obj;
			VideoData v1  = (VideoData)obj1;
			if(v.dateTime != null && v1.dateTime != null ){
				return v1.dateTime.compareTo(v.dateTime);
			}
		}
		return 0;
	}


}

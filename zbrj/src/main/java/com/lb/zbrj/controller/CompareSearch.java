package com.lb.zbrj.controller;

import java.util.Comparator;

import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.VideoData;

public class CompareSearch implements Comparator<Object> {

	@Override
	public int compare(Object obj0, Object obj1) {
		try{
			if(obj0 instanceof VideoData){
				if(obj1 instanceof VideoData){
					return 0;
				}else{
					return 1;
				}
			}
			if(obj0 instanceof PersonData){
				if(obj1 instanceof PersonData){
					return 0;
				}else{
					return -1;
				}
			}
			return -1;
		}catch(Exception e){
			return -1;
		}
	}

}

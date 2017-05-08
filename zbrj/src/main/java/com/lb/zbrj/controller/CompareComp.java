package com.lb.zbrj.controller;

import java.util.Comparator;

import com.lb.zbrj.data.CompData;

public class CompareComp implements Comparator<Object> {

	@Override
	public int compare(Object msg0, Object msg1) {
		try{
			if(((CompData)msg0).sort == ((CompData)msg1).sort){
				return 0;
			}else if(((CompData)msg0).sort > ((CompData)msg1).sort){
				return 1;
			}else{
				return -1;
			}
		}catch(Exception e){
			return -1;
		}
	}

}

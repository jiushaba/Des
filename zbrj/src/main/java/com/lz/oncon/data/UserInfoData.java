package com.lz.oncon.data;

import java.io.Serializable;

public class UserInfoData implements Serializable {

	private static final long serialVersionUID = 6971418942909139362L;
	
	public String sex; //性别（1男 2女）
	public String district;  // 所属地区编码
	public String district_zh_cn; // 所属地区中文
	public String district_en; // 所属地区英文

}

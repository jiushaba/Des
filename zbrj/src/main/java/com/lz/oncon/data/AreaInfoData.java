package com.lz.oncon.data;

public class AreaInfoData {
	
	public String name_zh_cn; 
	public String name_en;    
	public String areacode;   
	public String level;      
	public String parent_code;
	public String zipcode;
	public static final String TYPE_COUNTRY = "-1";
	public static final String TYPE_PROVINCE = "0";
	public static final String TYPE_CITY = "1";
	public static final String TYPE_AREA = "2";
	
	public AreaInfoData(String name_zh_cn, String name_en, String areacode, String level, String parent_code, String zipcode) {
		this.name_zh_cn = name_zh_cn;
		this.name_en = name_en;
		this.areacode = areacode;
		this.level = level;
		this.parent_code = parent_code;
		this.zipcode = zipcode;
	}

}

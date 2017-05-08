package com.lz.oncon.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;

public class MenuData {
	
	public static final String TYPE_CLICK = "click";
	public static final String TYPE_VIEW = "view";
	
	public String name;
	public String type;
	public String key;
	public String url;
	public ArrayList<MenuData> sonMenus = new ArrayList<MenuData>();
	
	public static ArrayList<MenuData> parseMenus(String str){
		ArrayList<MenuData> list = new ArrayList<MenuData>();
		try {
			JSONArray arr = new JSONArray(str);
			if (arr.length() > 0) {
				for (int i = 0; i < arr.length(); i++) {
					JSONObject jjson = arr.getJSONObject(i);
					MenuData data = new MenuData();
					data.name = jjson.has("name") ? jjson.getString("name") : "";
					data.type = jjson.has("type") ? jjson.getString("type") : "";
					data.key = jjson.has("key") ? jjson.getString("key") : "";
					data.url = jjson.has("url") ? jjson.getString("url") : "";
					if(jjson.has("sub_button")){
						List<MenuData> sonlist = parseMenus(jjson.getString("sub_button"));
						data.sonMenus.addAll(sonlist);
					}
					list.add(data);
				}
			}
		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return list;
	}
	
	public static String getMenusStr(List<MenuData> menus){
		JSONArray arr = new JSONArray();
		if(menus != null && menus.size() > 0){
			for(MenuData menu:menus){
				JSONObject obj = new JSONObject();
				try {
					obj.put("type", menu.type);
					obj.put("name", menu.name);
					if(MenuData.TYPE_CLICK.equals(menu.type)){
						obj.put("key", menu.key);
					}else if(MenuData.TYPE_VIEW.equals(menu.type)){
						obj.put("url", menu.url);
					}
					if(menu.sonMenus != null && menu.sonMenus.size() > 0){
						obj.put("sub_button", getMenusStr(menu.sonMenus));
					}
				} catch (JSONException e) {
				}
				arr.put(obj);
			}
		}
		return arr.toString();
	}
}

package com.lz.oncon.data.db;

import java.io.File;
import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lb.common.util.FileCore;
import com.lb.common.util.ImageLoader;
import com.lb.common.util.StringUtils;
import com.lz.oncon.app.im.ui.news.AsyncImageLoader;
import com.lz.oncon.app.im.ui.news.AsyncImageLoader.ImageCallback;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.GifFaceData;

/**
 * 操作表情表
 * 
 * @author Administrator
 * 
 */
public class FaceHelper {

	public SQLiteDatabase db;

	private static FaceHelper instance;

	public static FaceHelper getInstance(String dbName) {
		if (instance == null) {
			instance = new FaceHelper(dbName);
		}
		return instance;
	}

	private FaceHelper(String dbName) {
		if (db == null) {
			db = DatabaseMan.getInstance().getDB(dbName);
		}
	}

	/**
	 * 表中是否存在该最近联系人 存在返回true，不存在返回false
	 * 
	 * @param current
	 * @return
	 */
	public static boolean isExist(SQLiteDatabase db, GifFaceData current) {
		String sql = "select * from gifface where image_name= ?";
		Cursor c = db.rawQuery(sql, new String[] { current.getImage_name()});
		boolean isExist = false;
		if (c != null) {
			if (c.moveToFirst()) {
				isExist = true;
			}
			c.close();
		}
		return isExist;
	}
	
	
	/**
	 * 插入一条数据
	 * 
	 * @param current
	 */
	public static void add(SQLiteDatabase db, GifFaceData current) {
		String sql = "insert into gifface (image_name,isdefault,image_des,class_name,isclassImage,extension_name,suburl) values (?,?,?,?,?,?,?)";
		db.execSQL(sql, new Object[] { current.getImage_name(), current.getIsdefault(), current.getImage_des(), current.getClass_name(), current.getIsclassImage(), current.getExtension_name(), current.getSuburl() });
	}

	/**
	 * 删除表中所有记录
	 * 
	 * @return
	 */
	public boolean delAll() {
		boolean result = db.delete("gifface", null, null) > 0;
		return result;
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	public boolean del(String imageName) {
		boolean result = db.delete("gifface", "image_name = ?", new String[] { imageName }) > 0;
		return result;
	}

	/**
	 * 更新动态图片
	 * 
	 * @param current
	 */
	public static void updateLt(SQLiteDatabase db, GifFaceData current) {
		String sql2 = "update gifface set image_name= ?,isdefault= ?,image_des= ?,class_name= ?,isclassImage= ?,extension_name= ?,suburl= ? where image_name= ?";
		db.execSQL(sql2, new Object[] { current.getImage_name(), current.getIsdefault(), current.getImage_des(), current.getClass_name(), current.getIsclassImage(), current.getExtension_name(), current.getSuburl(),current.getImage_name()});
	}

	/**
	 * 查找所有最近联系人
	 * 
	 * @return
	 */
	public ArrayList<GifFaceData> findAll() {
		String sql = "select * from gifface";
		Cursor c = db.rawQuery(sql, null);
		ArrayList<GifFaceData> list = null;
		if (c == null) {
			return null;
		}
		if (c.moveToFirst()) {
			list = new ArrayList<GifFaceData>();
			do {
				// image_name,isdefault,image_des,class_name,isclassImage,extension_name
				GifFaceData current = new GifFaceData();
				current.setImage_name(c.getString(c.getColumnIndex("image_name")));
				current.setIsdefault(c.getString(c.getColumnIndex("isdefault")));
				current.setImage_des(c.getString(c.getColumnIndex("image_des")));
				current.setClass_name(c.getString(c.getColumnIndex("class_name")));
				current.setIsclassImage(c.getString(c.getColumnIndex("isclassImage")));
				current.setExtension_name(c.getString(c.getColumnIndex("extension_name")));
				current.setSuburl(c.getString(c.getColumnIndex("suburl")));
				list.add(current);
			} while (c.moveToNext());
		}
		c.close();
		return list;
	}

	/**
	 * 查找大类下面的所有GIF图片
	 * 
	 * @param class_name
	 * @param isclassImage
	 *            0不是类图，1类图未选中图片 ，2类图选中图片
	 * @return
	 */
	public ArrayList<GifFaceData> findByClassNameAndNoClass(String class_name) {
		Cursor c = null;
		ArrayList<GifFaceData> list = new ArrayList<GifFaceData>();
		String sql = "select * from gifface where class_name = '" + class_name + "' and isclassImage = '" + 0 + "'";
		try{
			if(db == null)return list;
			c = db.rawQuery(sql, null);
			if(c == null)return list;
			if (c.moveToFirst()) {
				do {
					GifFaceData current = new GifFaceData();
					current.setImage_name(c.getString(c.getColumnIndex("image_name")));
					current.setIsdefault(c.getString(c.getColumnIndex("isdefault")));
					current.setImage_des(c.getString(c.getColumnIndex("image_des")));
					current.setClass_name(c.getString(c.getColumnIndex("class_name")));
					current.setIsclassImage(c.getString(c.getColumnIndex("isclassImage")));
					current.setExtension_name(c.getString(c.getColumnIndex("extension_name")));
					current.setSuburl(c.getString(c.getColumnIndex("suburl")));
					list.add(current);
				} while (c.moveToNext());
			}
		}catch(Exception e){
		}finally{
			try{
				if(c != null)c.close();
			}catch(Exception e){}
		}
		return list;
	}

	/**
	 * 查询几个分类
	 * 
	 * @param isclassImage
	 *            0不是类图，1类图未选中图片 ，2类图选中图片
	 * @return
	 */
	public ArrayList<GifFaceData> findClassCountByType(String isclassImage) {
		ArrayList<GifFaceData> list = new ArrayList<GifFaceData>();
		Cursor c = null;
		try{
			if (TextUtils.isEmpty(isclassImage)) {
				isclassImage = "1";
			}
			String sql = "select * from gifface where isclassImage = '" + isclassImage + "'";
			c = db.rawQuery(sql, null);
			if (c.moveToFirst()) {
				do {
					GifFaceData current = new GifFaceData();
					current.setImage_name(c.getString(c.getColumnIndex("image_name")));
					current.setIsdefault(c.getString(c.getColumnIndex("isdefault")));
					current.setImage_des(c.getString(c.getColumnIndex("image_des")));
					current.setClass_name(c.getString(c.getColumnIndex("class_name")));
					current.setIsclassImage(c.getString(c.getColumnIndex("isclassImage")));
					current.setExtension_name(c.getString(c.getColumnIndex("extension_name")));
					current.setSuburl(c.getString(c.getColumnIndex("suburl")));
					list.add(current);
				} while (c.moveToNext());
			}
		}catch(Exception e){
		}finally{
			try{
				if(c != null)c.close();
			}catch(Exception e){}
		}
		return list;
	}

	/**
	 * 查询选中的图片
	 * 
	 * @param isclassImage0不是类图
	 *            ，1类图未选中图片 ，2类图选中图片
	 * @param class_name
	 * @return
	 */
	public GifFaceData findPressImageByType(String isclassImage, String class_name) {
		GifFaceData current = null;
		if (isclassImage == null || isclassImage.length() == 0) {
			isclassImage = "2";
		}
		String sql = "select * from gifface where isclassImage = '" + isclassImage + "' and class_name = '" + class_name + "'";
		Cursor c = db.rawQuery(sql, null);
		ArrayList<GifFaceData> list = null;
		if (c == null) {
			return null;
		}
		if (c.moveToFirst()) {
			list = new ArrayList<GifFaceData>();
			do {
				current = new GifFaceData();
				current.setImage_name(c.getString(c.getColumnIndex("image_name")));
				current.setIsdefault(c.getString(c.getColumnIndex("isdefault")));
				current.setImage_des(c.getString(c.getColumnIndex("image_des")));
				current.setClass_name(c.getString(c.getColumnIndex("class_name")));
				current.setIsclassImage(c.getString(c.getColumnIndex("isclassImage")));
				current.setExtension_name(c.getString(c.getColumnIndex("extension_name")));
				current.setSuburl(c.getString(c.getColumnIndex("suburl")));
				list.add(current);
			} while (c.moveToNext());
		}
		c.close();
		return current;
	}

	/**
	 * 通过图片文件名，查询GIF 动画
	 * 
	 * @param image_name
	 * @return
	 */
	public GifFaceData findImageByImageName(String image_name) {
		GifFaceData current = null;
		// String sql = "select * from gifface where image_name=? ";
		// Cursor c = db.rawQuery(sql, new String[]{current.getImage_name()});

		String sql = "select * from gifface where image_name = '" + image_name + "'";
		Cursor c = db.rawQuery(sql, null);
		ArrayList<GifFaceData> list = null;
		if (c == null) {
			return null;
		}
		if (c.moveToFirst()) {
			list = new ArrayList<GifFaceData>();
			do {
				current = new GifFaceData();
				current.setImage_name(c.getString(c.getColumnIndex("image_name")));
				current.setIsdefault(c.getString(c.getColumnIndex("isdefault")));
				current.setImage_des(c.getString(c.getColumnIndex("image_des")));
				current.setClass_name(c.getString(c.getColumnIndex("class_name")));
				current.setIsclassImage(c.getString(c.getColumnIndex("isclassImage")));
				current.setExtension_name(c.getString(c.getColumnIndex("extension_name")));
				current.setSuburl(c.getString(c.getColumnIndex("suburl")));
				list.add(current);
			} while (c.moveToNext());
		}
		c.close();
		return current;
	}

	/**
	 * 异步加载图片
	 * 
	 * @param asyncImageLoader
	 * @param picRemoteUrl
	 * @param picLocalPath
	 * @param faceName
	 *            带后缀的
	 * @param iv
	 * @param facedata
	 */
	public static void loadGifFace(AsyncImageLoader asyncImageLoader, String picRemoteUrl, String picLocalPath, final String faceName, final ImageView iv, final GifFaceData facedata, final boolean fromBG) {
		File imageFile = new File(picLocalPath);
		if (imageFile.exists()) {
			asyncImageLoader.loadDrawable(picLocalPath, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					// iv.setBackgroundDrawable(imageDrawable);
					if (fromBG) {
						iv.setBackgroundDrawable(imageDrawable);
					} else {
						iv.setImageDrawable(imageDrawable);
					}
				}
			}, IMConstants.lOAD_FROM_SDCARD);
		} else {
			asyncImageLoader.loadDrawable(picRemoteUrl, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (fromBG) {
						iv.setBackgroundDrawable(imageDrawable);
					} else {
						iv.setImageDrawable(imageDrawable);
					}
					if (FileCore.writeFaceToLocal(faceName, imageDrawable)) {
					}
				}
			}, IMConstants.LOAD_FROM_SERVER);
		}
		if (!FaceHelper.isExist(FaceHelper.getInstance(AccountData.getInstance().getUsername()).db, facedata)) {
			FaceHelper.add(FaceHelper.getInstance(AccountData.getInstance().getUsername()).db, facedata);
		}
	}

	public static void loadGifFaceNew(Context c, String picRemoteUrl, String picLocalPath, String faceName, ImageView iv, GifFaceData facedata) {
		ImageLoader.getInstance().displayImage(picRemoteUrl, picLocalPath, iv, false, faceName);
		boolean isResult = (!FaceHelper.isExist(FaceHelper.getInstance(AccountData.getInstance().getUsername()).db, facedata));
		if (isResult) {
			FaceHelper.add(FaceHelper.getInstance(AccountData.getInstance().getUsername()).db, facedata);
		}
	}

	/**
	 * 获取图片保存数据库
	 * 
	 * @param json
	 * @param cHelper
	 * @param db
	 */
	public static void getAllDefaultFace(String json, SQLiteDatabase db, boolean isDefault) {
		if (json != null) {
			JSONObject json_allApp = null;
			try {
				json_allApp = new JSONObject(json);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (json_allApp == null) {
				return;
			} else {
				try {
					JSONArray arr = json_allApp.getJSONArray("response");
					JSONObject jjson;
					JSONObject jjson1;
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							jjson = arr.getJSONObject(i);
							String selected_image = StringUtils.repNull(jjson.getString("selected_image"));
							String image = StringUtils.repNull(jjson.getString("image"));
							String suburl = StringUtils.repNull(jjson.getString("suburl"));
							if (image != null && image.length() > 0) {
								String image_extension = "png";
								if (image.indexOf(".") != -1) {
									image_extension = StringUtils.repNull(image.substring(image.indexOf(".") + 1));
									image = image.substring(0, image.indexOf("."));
								}
								GifFaceData cData = new GifFaceData();
								cData.setImage_name(image);
								if (isDefault) {
									cData.setIsdefault("0");
								} else {
									cData.setIsdefault("1");
								}
								cData.setClass_name(image);
								cData.setIsclassImage("1");
								cData.setExtension_name(image_extension);
								cData.setSuburl(suburl);
								if (!isExist(db, cData)) {
									add(db, cData);
								} else {
									updateLt(db,cData);
								}
							}
							if (selected_image != null && selected_image.length() > 0) {
								String image_extension = "png";
								if (selected_image.indexOf(".") != -1) {
									selected_image = selected_image.substring(0, selected_image.indexOf("."));
									image_extension = selected_image.substring(selected_image.indexOf(".") + 1);
								}
								GifFaceData cData = new GifFaceData();
								cData.setImage_name(selected_image);
								if (isDefault) {
									cData.setIsdefault("0");
								} else {
									cData.setIsdefault("1");
								}
								cData.setClass_name(image);
								cData.setIsclassImage("2");
								cData.setExtension_name(image_extension);
								cData.setSuburl(suburl);
								if (!isExist(db, cData)) {
									add(db, cData);
								} else {
									updateLt(db,cData);
								}
							}
							JSONArray arr1 = jjson.getJSONArray("list");
							if (arr1.length() > 0) {
								for (int a = 0; a < arr1.length(); a++) {
									jjson1 = arr1.getJSONObject(a);
									String name = jjson1.getString("name");
									String desc = jjson1.getString("desc");
									String image_extension = "png";
									if (name.indexOf(".") != -1) {
										image_extension = name.substring(name.indexOf(".") + 1);
										name = name.substring(0, name.indexOf("."));
									}
									GifFaceData cData = new GifFaceData();
									cData.setImage_name(name);
									if (isDefault) {
										cData.setIsdefault("0");
									} else {
										cData.setIsdefault("1");
									}
									cData.setImage_des(desc);
									cData.setClass_name(image);
									cData.setIsclassImage("0");
									cData.setExtension_name(image_extension);
									cData.setSuburl(suburl);
									if (!isExist(db, cData)) {
										add(db, cData);
									} else {
										// cHelper.updateLt(db,cData);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 通过类名解析JSON ，返回类下面所有图片
	 * 
	 * @param json
	 * @param db
	 * @param class_name
	 * @return
	 */
	public static ArrayList<GifFaceData> parseFaceJsonFromNet(String json, SQLiteDatabase db, String class_name) {
		ArrayList<GifFaceData> faceListData = null;
		if (json != null) {
			JSONObject json_allApp = null;
			try {
				json_allApp = new JSONObject(json);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			if (json_allApp == null) {
				return faceListData;
			} else {
				try {
					JSONArray arr = json_allApp.getJSONArray("response");
					JSONObject jjson;
					JSONObject jjson1;
					if (arr.length() > 0) {
						faceListData = new ArrayList<GifFaceData>();
						for (int i = 0; i < arr.length(); i++) {
							jjson = arr.getJSONObject(i);
							String selected_image = StringUtils.repNull(jjson.getString("selected_image"));
							String image = StringUtils.repNull(jjson.getString("image"));
							String suburl = StringUtils.repNull(jjson.getString("suburl"));
							if ((image != null && image.contains((StringUtils.repNull(class_name)))) || (selected_image != null && selected_image.contains((StringUtils.repNull(class_name))))) {
								if (image != null && image.length() > 0) {
									String image_extension = "png";
									if (image.indexOf(".") != -1) {
										image_extension = StringUtils.repNull(image.substring(image.indexOf(".") + 1));
										image = image.substring(0, image.indexOf("."));
									}
									GifFaceData cData = new GifFaceData();
									cData.setImage_name(image);
									// if(isDefault){
									// cData.setIsdefault("0");
									// }else{
									cData.setIsdefault("1");
									// }
									cData.setClass_name(image);
									cData.setIsclassImage("1");
									cData.setExtension_name(image_extension);
									cData.setSuburl(suburl);
									// if (!isExist(db, cData)) {
									// // add(db,cData);
									// // faceListData.add(cData); 类图不需要加载
									// } else {
									// // cHelper.updateLt(db,cData);
									// }
								}
								if (selected_image != null && selected_image.length() > 0) {
									String image_extension = "png";
									if (selected_image.indexOf(".") != -1) {
										image_extension = selected_image.substring(selected_image.indexOf(".") + 1);
										selected_image = selected_image.substring(0, selected_image.indexOf("."));
									}
									GifFaceData cData = new GifFaceData();
									cData.setImage_name(selected_image);
									// if(isDefault){
									// cData.setIsdefault("0");
									// }else{
									cData.setIsdefault("1");
									// }
									cData.setClass_name(image);
									cData.setIsclassImage("2");
									cData.setExtension_name(image_extension);
									cData.setSuburl(suburl);
									// if (!isExist(db, cData)) {
									// // add(db,cData);
									// // faceListData.add(cData); 类图不需要加载
									// } else {
									// // cHelper.updateLt(db,cData);
									// }
								}
								JSONArray arr1 = jjson.getJSONArray("list");
								if (arr1.length() > 0) {
									for (int a = 0; a < arr1.length(); a++) {
										jjson1 = arr1.getJSONObject(a);
										String name = jjson1.getString("name");
										String desc = jjson1.getString("desc");
										String image_extension = "png";
										if (name.indexOf(".") != -1) {
											image_extension = name.substring(name.indexOf(".") + 1);
											name = name.substring(0, name.indexOf("."));
										}
										GifFaceData cData = new GifFaceData();
										cData.setImage_name(name);
										cData.setIsdefault("1");
										cData.setImage_des(desc);
										cData.setClass_name(image);
										cData.setIsclassImage("0");
										cData.setExtension_name(image_extension);
										cData.setSuburl(suburl);
										if (!isExist(db, cData)) {
											faceListData.add(cData);
										} else {
										}
									}
								}
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return faceListData;
	}

	/**
	 * 通过类名解析JSON ，返回类下面所有图片
	 * 
	 * @param json
	 * @param db
	 * @param class_name
	 * @return
	 */
	public static ArrayList<GifFaceData> parseFaceJsonAddNewClass(String json, SQLiteDatabase db) {
		ArrayList<GifFaceData> faceListData = null;
		if (json != null) {
			JSONObject json_allApp = null;
			try {
				json_allApp = new JSONObject(json);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			if (json_allApp == null) {
				return faceListData;
			} else {
				try {
					JSONArray arr = json_allApp.getJSONArray("response");
					JSONObject jjson;
					JSONObject jjson1;
					if (arr.length() > 0) {
						faceListData = new ArrayList<GifFaceData>();
						for (int i = 0; i < arr.length(); i++) {
							jjson = arr.getJSONObject(i);
							String selected_image = StringUtils.repNull(jjson.getString("selected_image"));
							String image = StringUtils.repNull(jjson.getString("image"));
							String suburl = StringUtils.repNull(jjson.getString("suburl"));
							// if((image!=null&&image.contains((StringUtils.repNull(class_name))))||(selected_image!=null&&selected_image.contains((StringUtils.repNull(class_name))))){
							if (image != null && image.length() > 0) {
								String image_extension = "png";
								if (image.indexOf(".") != -1) {
									image_extension = StringUtils.repNull(image.substring(image.indexOf(".") + 1));
									image = image.substring(0, image.indexOf("."));
								}
								GifFaceData cData = new GifFaceData();
								cData.setImage_name(image);
								// if(isDefault){
								// cData.setIsdefault("0");
								// }else{
								cData.setIsdefault("1");
								// }
								cData.setClass_name(image);
								cData.setIsclassImage("1");
								cData.setExtension_name(image_extension);
								cData.setSuburl(suburl);
								if (!isExist(db, cData)) {
									add(db, cData);
									faceListData.add(cData);
								} else {
									// cHelper.updateLt(db,cData);
								}
							}
							if (selected_image != null && selected_image.length() > 0) {
								String image_extension = "png";
								if (selected_image.indexOf(".") != -1) {
									image_extension = selected_image.substring(selected_image.indexOf(".") + 1);
									selected_image = selected_image.substring(0, selected_image.indexOf("."));
								}
								GifFaceData cData = new GifFaceData();
								cData.setImage_name(selected_image);
								// if(isDefault){
								// cData.setIsdefault("0");
								// }else{
								cData.setIsdefault("1");
								// }
								cData.setClass_name(image);
								cData.setIsclassImage("2");
								cData.setExtension_name(image_extension);
								cData.setSuburl(suburl);
								if (!isExist(db, cData)) {
									add(db, cData);
									// faceListData.add(cData);
								} else {
									// cHelper.updateLt(db,cData);
								}
							}
							// JSONArray arr1 = jjson.getJSONArray("list");
							// if (arr1.length() > 0) {
							// for (int a = 0; a < arr1.length(); a++) {
							// jjson1 = arr1.getJSONObject(a);
							// String name = jjson1.getString("name");
							// String desc = jjson1.getString("desc");
							// String image_extension = "png";
							// if (name.indexOf(".") != -1) {
							// image_extension =
							// name.substring(name.indexOf(".") + 1);
							// name = name.substring(0, name.indexOf("."));
							// }
							// GifFaceData cData = new GifFaceData();
							// cData.setImage_name(name);
							// cData.setIsdefault("1");
							// cData.setImage_des(desc);
							// cData.setClass_name(image);
							// cData.setIsclassImage("0");
							// cData.setExtension_name(image_extension);
							// cData.setSuburl(suburl);
							// if (!isExist(db, cData)) {
							// faceListData.add(cData);
							// } else {
							// }
							// }
							// }
							// break;
							// }
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (faceListData != null) {
						return faceListData;
					} else {
						return null;
					}
				}
			}
		}
		return faceListData;
	}

	/**
	 * 通过文件名在JSON串中找对应的文件
	 * 
	 * @param json
	 * @param db
	 * @param image_name带后缀名
	 * @return
	 */
	public static GifFaceData parseFaceJsonFromNetToimage_name(String json, SQLiteDatabase db, String image_name) {
		GifFaceData faceListData = null;
		if (json != null) {
			JSONObject json_allApp = null;
			try {
				json_allApp = new JSONObject(json);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			if (json_allApp == null) {
				return null;
			} else {
				try {
					JSONArray arr = json_allApp.getJSONArray("response");
					JSONObject jjson;
					JSONObject jjson1;
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							jjson = arr.getJSONObject(i);
							if (jjson.toString().contains(StringUtils.repNull(image_name))) {
								String selected_image = StringUtils.repNull(jjson.getString("selected_image"));
								String image = StringUtils.repNull(jjson.getString("image"));
								String suburl = StringUtils.repNull(jjson.getString("suburl"));
								JSONArray arr1 = jjson.getJSONArray("list");
								if (arr1.length() > 0) {
									for (int a = 0; a < arr1.length(); a++) {
										jjson1 = arr1.getJSONObject(a);
										String name = jjson1.getString("name");
										if (name != null && name.contains(StringUtils.repNull(image_name))) {
											String desc = jjson1.getString("desc");
											String image_extension = "png";
											if (name.indexOf(".") != -1) {
												image_extension = name.substring(name.indexOf(".") + 1);
												name = name.substring(0, name.indexOf("."));
											}
											GifFaceData cData = new GifFaceData();
											cData.setImage_name(name);
											cData.setIsdefault("1");
											cData.setImage_des(desc);
											cData.setClass_name(image);
											cData.setIsclassImage("0");
											cData.setExtension_name(image_extension);
											cData.setSuburl(suburl);
											if (!isExist(db, cData)) {
												faceListData = cData;
											}
										}
									}
								}
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return faceListData;
	}
}

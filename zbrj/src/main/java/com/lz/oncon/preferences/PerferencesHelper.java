package com.lz.oncon.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * 配置文件Helper
 * 主要负责对配置文件进行升级以及初始化
 */
public abstract class PerferencesHelper {

	private final Context mContext;
	private final String mName;
	private final int mNewVersion;

	private SharedPreferences sp = null;
	private boolean mIsInitializing = false;
	
	public PerferencesHelper(Context context, String name, int version) {
		if (version < 1)
			throw new IllegalArgumentException("Version must be >= 1, was "
					+ version);

		mContext = context;
		mName = name;
		mNewVersion = version;
	}

	public synchronized SharedPreferences getSharedPreferences() {
		if (sp != null) {
			return sp;
		}

		if (mIsInitializing) {
			throw new IllegalStateException(
					"getSharedPreferences called recursively");
		}

		try {
			mIsInitializing = true;
			if (mName == null) {
				throw new IllegalStateException("SharedPreferences is null");
			} else {
				sp = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
			}

			int version = getVersion();
			if (version != mNewVersion) {

				if (version == 0) {
					onCreate(sp);
				} else {
					if (version > mNewVersion) {

					}
					onUpgrade(sp, version, mNewVersion);
				}
				setVersion(mNewVersion);
			}
			return sp;

		} finally {
			mIsInitializing = false;
		}
	}

	public int getVersion() {
		return sp.getInt("version", 0);
	}

	private void setVersion(int version) {
		Editor editor = sp.edit();
		editor.putInt("version", version);
		editor.commit();
	}

	public abstract void onCreate(SharedPreferences sp);

	public abstract void onUpgrade(SharedPreferences sp, int oldVersion,
			int newVersion);
}
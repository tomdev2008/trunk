package com.example.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.app.ExampleApp;

public class Preferences {
	private static Preferences prefsUtils;
	private static SharedPreferences preferences;

	// 通过共享名称获取SharedPreferences
	public static Preferences getInstance(String prefs) {
		if (prefsUtils == null) {
			prefsUtils = new Preferences();
		}
		preferences = ExampleApp.getInstance().getSharedPreferences(prefs,
				Activity.MODE_PRIVATE);
		return prefsUtils;
	}

	public boolean isFirstInstall() {
		return preferences.getBoolean("firstInstall", false);
	}
	
	public void setFirstInstall(boolean first) {
		Editor editor = preferences.edit();
		editor.putBoolean("firstInstall", first);
		editor.commit();;
	}

	// 设置消息通知方式，默认为铃声
	public void setNotification(int type) {
		Editor editor = preferences.edit();
		editor.putInt("type", type);
		editor.putBoolean("receiver", true);
		editor.commit();
	}

	// 得到用户设置的消息通知方式，默认为铃声
	public static int getNotification() {
		switch (preferences.getInt("type", 0)) {
		case 1:
			return Notification.DEFAULT_SOUND;
		case 2:
			return Notification.DEFAULT_VIBRATE;
		case 3:
			return Notification.DEFAULT_ALL;
		default:
			return 0;
		}
	}
}

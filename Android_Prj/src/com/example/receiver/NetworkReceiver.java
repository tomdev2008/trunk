package com.example.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.app.ExampleApp;
import com.example.utils.Logger;

/**
 * @author Administrator
 *
 */
public class NetworkReceiver extends BroadcastReceiver {
	/** Anything worse than or equal to this will show 0 bars. */
    private static final int MIN_RSSI = -100;

    /** Anything better than or equal to this will show the max bars. */
    private static final int MAX_RSSI = -55;

    public static final int RSSI_LEVELS = 5;
	private static WifiManager wifiManager= (WifiManager) ExampleApp.getInstance()
			.getSystemService(Context.WIFI_SERVICE);
	private static ConnectivityManager cManager = (ConnectivityManager) ExampleApp.getInstance()
			.getSystemService(Context.CONNECTIVITY_SERVICE);
	private static boolean isWifiConnected;
	public NetworkReceiver() {
		super();
		Logger.log("NetworkReceiver", wifiManager + " / " + cManager);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.log("NetWorkReceiver", "onReceive", action);
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			isWifiConnected=false;
			isAvailable();
		}
	}

	public static boolean isAvailable() {
		NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			State state = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
			Logger.log("NetWorkReceiver", "wifi", state.name());
			if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
				isWifiConnected=true;
				speakWifiState(wifiManager.getWifiState());
				return true;
			}
			state = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState(); // 获取网络连接状态
			Logger.log("NetWorkReceiver", "gprs", state.name());
			if (State.CONNECTED == state) { // 判断是否正在使用GPRS网络
//				TtsSpeakRule.speaking(1, "GPRS网络");
				return true;
			}
		} else {
//			TtsSpeakRule.speaking(1, "未连接网络");
			return false;
		}
		return false;
	}

	public static void speakWifiState(int wifi_state) {
		Logger.log("speakWifiState", wifi_state);
//		TtsSpeakRule.speaking(1, "WIFI网络");
		switch (wifi_state) {
		case WifiManager.WIFI_STATE_DISABLING:
//			TtsSpeakRule.speaking(1, "正在断开");
			break;
		case WifiManager.WIFI_STATE_DISABLED:
//			TtsSpeakRule.speaking(1, "不可用");
			break;
		case WifiManager.WIFI_STATE_ENABLING:
//			TtsSpeakRule.speaking(1, "正在连接");
			break;
		case WifiManager.WIFI_STATE_ENABLED:
//			TtsSpeakRule.speaking(1, "正常");
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
//			TtsSpeakRule.speaking(1, "未知状态");
			break;
		}
	}
	
	public static String getWifiState() {
		int wifi_state=wifiManager.getWifiState();
		WifiInfo info = wifiManager.getConnectionInfo();
		Logger.log("speakWifiState", wifi_state+"/"+info);
		switch (wifi_state) {
		case WifiManager.WIFI_STATE_DISABLING:
			return "正在断开";
		case WifiManager.WIFI_STATE_DISABLED:
			return "不可用";
		case WifiManager.WIFI_STATE_ENABLING:
			return "正在连接";
		case WifiManager.WIFI_STATE_ENABLED:
			int singal=getSignalLevel(info.getRssi(), 5);
			if (singal!=0&&isWifiConnected) {
				return "已连接"+singal+"格信号";
			}
			return "未连接";	
		default :
			return "未知状态";
		}
	}
	
	public static int getSignalLevel(int rssi, int numLevels) {
        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return numLevels - 1;
        } else {
            int partitionSize = (MAX_RSSI - MIN_RSSI) / (numLevels - 1);
            return (rssi - MIN_RSSI) / partitionSize;
        }
	}
	
	public static void setWifi(){
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		} else {
			wifiManager.setWifiEnabled(true);
		}
	}
}

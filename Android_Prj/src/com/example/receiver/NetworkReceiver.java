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
    private static final int MIN_RSSI = -100;
    private static final int MAX_RSSI = -55;
    private static final int RSSI_LEVELS = 5;
    
    public static boolean isAvailable;
    
	private static WifiManager wifiManager;
	private static ConnectivityManager cManager ;
	
	public NetworkReceiver() {
		super();
		Logger.log(this, wifiManager + " / " + cManager);
		wifiManager= (WifiManager) ExampleApp.getInstance()
				.getSystemService(Context.WIFI_SERVICE);
		cManager = (ConnectivityManager) ExampleApp.getInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.log(this, "onReceive", action);
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			setNetwork();
		}
	}

	private void setNetwork() {
		NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			State state = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState(); // 获取网络连接状态
			Logger.log(this, "wifi", state.name());
			if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
				isAvailable=true;
			}
			state = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState(); // 获取网络连接状态
			Logger.log(this, "gprs", state.name());
			if (State.CONNECTED == state) { // 判断是否正在使用GPRS网络
				isAvailable=true;
			}
		} else {
			isAvailable=false;
		}
	}

	public static void getWifiState(int wifi_state) {
		Logger.log("getWifiState", wifi_state);
		switch (wifi_state) {
		case WifiManager.WIFI_STATE_DISABLING:
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			break;
		}
	}
	
	public static String getWifiState() {
		int wifi_state=wifiManager.getWifiState();
		WifiInfo info = wifiManager.getConnectionInfo();
		Logger.log("getWifiState", wifi_state+"/"+info);
		switch (wifi_state) {
		case WifiManager.WIFI_STATE_DISABLING:
			return "正在断开";
		case WifiManager.WIFI_STATE_DISABLED:
			return "不可用";
		case WifiManager.WIFI_STATE_ENABLING:
			return "正在连接";
		case WifiManager.WIFI_STATE_ENABLED:
			int singal=getSignalLevel(info.getRssi(), 5);
			if (singal!=0&&isAvailable) {
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
	
	public static void setWifiState(){
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		} else {
			wifiManager.setWifiEnabled(true);
		}
	}
}

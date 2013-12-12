package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.example.app.ExampleApp;
import com.example.map.IMapCallBack;
import com.example.utils.Logger;

public class BaseService extends Service implements IMapCallBack{
	@Override
	public IBinder onBind(Intent intent) {
		log("onBind",intent);
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		log("onCreate");
	}
	
	public void requestLocation(boolean scanSpan,int ongoing){
		log("requestLocation",ongoing);
		ExampleApp.getInstance().setLocationParams(scanSpan);
		ExampleApp.registerLocationListener(this,ongoing);
		ExampleApp.startLocation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		log("onDestroy");
		ExampleApp.stopLocation();
		ExampleApp.getInstance().setLocationParams(false);
		ExampleApp.unregisterLocationListener(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		log("handleMessage",msg.what);
		return false;
	}

	public void log(Object what){
		Logger.log(this, what);
	}
	
	public void log(Object which,Object what){
		Logger.log(which, what);
	}
	
	public void log(String method,Object what){
		Logger.log(this,method, what);
	}
	
	public void toast(int toast){
		Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
	}
	
	public void toast(String toast){
		Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onError(Object what, int errorCode) {
		Logger.log("onError",what);
	}
}

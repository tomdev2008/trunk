package com.example.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.app.ExampleApp;
import com.example.map.IMapCallBack;
import com.example.utils.Logger;
import com.example.utils.Waiter;
import com.example.view.TitleLayout;

public class BaseActivity extends Activity implements IMapCallBack{
	private Waiter waiter;
	private TitleLayout titleLayout;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		waiter=Waiter.getInstance();
		titleLayout=TitleLayout.getInstance();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork() // 这里可以替换为detectAll()
		.penaltyLog() // 打印logcat
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
		.penaltyLog() // 打印logcat
		.penaltyDeath().build());
	}
	public void createTitle(int title){
		titleLayout.createTitle(this,title);
	}
	
	public void createTitle(int title,boolean left,boolean right){
		titleLayout.createTitle(this, left, right);
		titleLayout.setTitle(title);
	}
	
	public void setLeftButton(int left){
		titleLayout.setLeftButton(left);
	}
	
	public void setRightButton(int right){
		titleLayout.setRightButton(right);
	}
	
	
	public void registerSearchListener(int iCallType){
		log(this, "setSearchCallBack");
		ExampleApp.registerSearchListener(iCallType, this);
	}
	
	public void requestLocation(int ongoing){
		log(this, "requestLocation");
		ExampleApp.registerLocationListener(this, ongoing);
		ExampleApp.startLocation();
		ExampleApp.stopLocation();
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
	
	public void startWaitting(String waiting,boolean able){
		waiter.startWaitting(this, waiting,able);
	}
	
	public void stopWatting(){
		waiter.stopWatting();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	
	@Override
	public void onError(Object what, int errorCode) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log(this, "onDestroy");
		ExampleApp.unregisterLocationListener(this);
	}
	
}

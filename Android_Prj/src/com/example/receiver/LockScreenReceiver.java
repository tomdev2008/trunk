package com.example.receiver;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.utils.Logger;

@SuppressWarnings("deprecation")
@SuppressLint({ "NewApi", "SimpleDateFormat", "Wakelock" })
public class LockScreenReceiver extends BroadcastReceiver {
	//声明键盘锁  
	private KeyguardLock keyLock;
	//声明电源管理器  
	private PowerManager pManager;  
	// 声明键盘管理器  
	private KeyguardManager kManager;   
	private PowerManager.WakeLock wakeLock; 
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat();

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.log("LockScreenReceiver", "onReceive", intent.getAction());
		if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			dateFormat = new SimpleDateFormat("HH时mm分");
		}
		else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
			releaseWakeLock();
		}
		else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
		}
	}
	private void disableKeyguardLock(Context context){
		if (pManager==null) {
			pManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);  
		}
		if (kManager==null) {
			kManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);  
		}
		if (wakeLock==null) {
			//点亮亮屏  
			wakeLock = pManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP 
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "YD");  
		}
		if (keyLock==null) {
			//初始化键盘锁，可以锁定或解开键盘锁  
			keyLock = kManager.newKeyguardLock("");
		}
		Logger.log("LockScreenReceiver", "disableKeyguardLock",pManager+"/"+kManager+"/"+wakeLock+"/"+keyLock);
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();  
		}
		Logger.log("LockScreenReceiver", "disableKeyguardLock~~~",wakeLock.isHeld());
		keyLock.disableKeyguard();    
	}
	
	private void releaseWakeLock(){
		if (wakeLock!=null&&wakeLock.isHeld()) {
			wakeLock.release();
		}
	}
}

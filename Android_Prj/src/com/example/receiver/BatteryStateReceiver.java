package com.example.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.utils.Logger;

@SuppressLint("NewApi")
public class BatteryStateReceiver extends BroadcastReceiver{
	private static int battery;
    @SuppressLint("InlinedApi")
	public BatteryStateReceiver() {
		super();
	}
	@Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra("level", 0);  //电池电量等级 
	    int scale = intent.getIntExtra("scale", 100);  //电池满时百分比 
	    int status = intent.getIntExtra("status", 0);  //电池状态 
		battery=level * 100 / scale;
		Logger.log(this, "onReceive", status+" /"+battery);
//        if (Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())) {  
//          	TtsSpeakRule.speaking(1,"电量已满，可以正常使用手机");
//        }  
//        else if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {  
//            TtsSpeakRule.speaking(1,"电量过低不到"+battery+"%的电了请尽快充电");
//        }  
//        else {
//        	if (battery==100) {
//    			return;
//    		}
//        	if (battery!=0&&battery%10==0) {
//        		if (status==BatteryManager.BATTERY_STATUS_CHARGING)
//            		speakCurrBattery(true, battery);
//        		else
//        		speakCurrBattery(false, battery);
//        	}
//        }
        
    }
    public static void speakCurrBattery(boolean charging,int battery){
//    	 if (charging) 
//    		 TtsSpeakRule.speaking(1,"已充满"+battery+"%的电");
//    	 else
//    		TtsSpeakRule.speaking(1,"当前剩余"+battery+"%的电");
    }
    
    public static void speakCurrBattery(){
    	Logger.log("speakCurrBattery", battery);
    }
    
    public static String getCurrBattery(){
    	Logger.log("getCurrBattery", battery);
    	return battery+"%";
    }
}

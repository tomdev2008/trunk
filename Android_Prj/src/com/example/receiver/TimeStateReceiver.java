package com.example.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.utils.Logger;

public class TimeStateReceiver extends BroadcastReceiver{
	private GregorianCalendar calendar = new GregorianCalendar();  
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat dateFormat=new SimpleDateFormat();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.log(this, "onReceive", action);
        if (Intent.ACTION_TIME_CHANGED.equals(action)
        		||Intent.ACTION_TIME_TICK.equals(action)) {
        	speakCurrState(0);
        }
        if (Intent.ACTION_DATE_CHANGED.equals(action)) {
        	speakCurrState(1);
        }
    }
    
	@SuppressLint("SimpleDateFormat")
	private void speakCurrState(int type){
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		if(type==0){
			Date d = new Date();
			calendar.setTime(d);
//			if( (calendar.get(Calendar.MINUTE)==0) && (calendar.get(Calendar.SECOND)==0) )
//				TtsSpeakRule.speaking(1,"北京时间"+calendar.get(Calendar.HOUR_OF_DAY) + "点整");
		}
		else{
			dateFormat=new SimpleDateFormat("yyyy年MM月dd日 E");
//			TtsSpeakRule.speaking(1,"当前日期"+dateFormat.format(new Date()));
		}
	}
	
}

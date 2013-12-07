package com.yunva.waya.helper;

import android.util.Log;

public class Logger {
	private static final String TAG="yunva-waya";
	private static boolean LOGGERV=true;
	private static boolean LOGGERD=false;
	private static boolean LOGGERI =false;
	private static boolean LOGGERW=false;
	private static boolean LOGGERE=false;
	public static void log(Object which,Object what){
		if (which.getClass().getSimpleName().equals("String")) {
			which=which.toString();
		}
		else {
			which=which.getClass().getSimpleName();
		}
		
		if(what==null)
			what="nothing";
		if(LOGGERV){
			Log.v(TAG+" "+which, what.toString());
		}
		if (LOGGERD) {
			Log.d(TAG+" "+which, what.toString());
		}
		if (LOGGERI) {
			Log.i(TAG+" "+which, what.toString());
		}
		if (LOGGERW) {
			Log.w(TAG+" "+which, what.toString());
		}
		if (LOGGERE) {
			Log.e(TAG+" "+which, what.toString());
		}
	}
	
	public static void log(Object cls,String method,Object what){
		if (cls.getClass().getSimpleName().equals("String")) {
			cls=cls.toString();
		}
		else {
			cls=cls.getClass().getSimpleName();
		}
		if (method==null) 
			method="method";
		if(what==null)
			what="nothing";
		if(LOGGERV){
			Log.v(TAG+" "+cls.toString()+" "+method, what.toString());
		}
		if (LOGGERD) {
			Log.d(TAG+" "+cls.toString()+" "+method, what.toString());
		}
		if (LOGGERI) {
			Log.i(TAG+" "+cls.toString()+" "+method, what.toString());
		}
		if (LOGGERW) {
			Log.w(TAG+" "+cls.toString()+" "+method, what.toString());
		}
		if (LOGGERE) {
			Log.e(TAG+" "+cls.toString()+" "+method, what.toString());
		}
	}
	
	public static void setLogLevel(int level){
		resetLevel();
		switch (level) {
			case 0:
				LOGGERV=true;
				break;
			case 1:
				LOGGERD=true;
				break;
			case 2:
				LOGGERI=true;
				break;
			case 3:
				LOGGERW=true;
				break;
			case 4:
				LOGGERE=true;
				break;
		}
	}
	private static void resetLevel(){
		LOGGERV=false;
		LOGGERD=false;
		LOGGERI=false;
		LOGGERW=false;
		LOGGERE=false;
	}
}

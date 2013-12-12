package com.example.utils;

import android.util.Log;

public class Logger {
	private static final String TAG="example";
	private static boolean LOGGERV=false;
	private static boolean LOGGERD=false;
	private static boolean LOGGERI=true;
	private static boolean LOGGERW=false;
	private static boolean LOGGERE=false;
	public static void log(Object method,Object what){
		if (method.getClass().getSimpleName().equals("String")) {
			method=method.toString();
		}
		else {
			method=method.getClass().getSimpleName();
		}
		if(what==null)
			what="nothing";
		if(LOGGERV){
			Log.v(TAG+" "+method, what.toString());
		}
		else if (LOGGERD) {
			Log.d(TAG+" "+method, what.toString());
		}
		else if (LOGGERI) {
			Log.i(TAG+" "+method, what.toString());
		}
		else if (LOGGERW) {
			Log.w(TAG+" "+method, what.toString());
		}
		else {
			Log.e(TAG+" "+method, what.toString());
		}
	}
	
	public static void log(Object cls,Object method,Object what){
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
			Log.v(TAG+" "+cls.toString()+" "+method.toString(), what.toString());
		}
		else if (LOGGERD) {
			Log.d(TAG+" "+cls.toString()+" "+method.toString(), what.toString());
		}
		else if (LOGGERI) {
			Log.i(TAG+" "+cls.toString()+" "+method.toString(), what.toString());
		}
		else if (LOGGERW) {
			Log.w(TAG+" "+cls.toString()+" "+method.toString(), what.toString());
		}
		else{
			Log.e(TAG+" "+cls.toString()+" "+method.toString(), what.toString());
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

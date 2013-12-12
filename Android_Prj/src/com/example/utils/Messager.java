package com.example.utils;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.example.activity.R;
import com.example.chat.ChattingActivity;


public class Messager {
	public static final int MSG_UPDATE=10;
	public static final int MSG_UPLOAD=11;
	public static final int MSG_DOWNLOAD=13;
	
	public static final int MSG_LOGIN=13;
	public static final int MSG_REGISTER=14;
	
	
	public static final int MSG_FILE_NOTFOUND=100;
	public static final int MSG_DOWN_SUCCESS=101;
	public static final int MSG_DOWN_FAILURE=102;
	
	public static final int MSG_UNKOWN_FILESIZE=103;
	
	public static final int MSG_UPLOAD_SUCCESS=104;
	public static final int MSG_UPLOAD_FAILURE=105;
	
	public static final int MSG_SUCCESS=200;
	public static final int MSG_CREATED=201;
	public static final int MSG_ACCEPTED=202;
	public static final int MSG_NO_CONTENT=204;
	
	public static final int MSG_BAD_REQUEST=400;
	public static final int MSG_UNAUTHORIZED=401;
	
	public static final int MSG_FORBIDDEN=403;
	public static final int MSG_NOT_FOUND=404;
	
	public static final int MSG_METHOD_NOT_ALLOWED=405;
	public static final int MSG_NOT_ACCEPTABLE=406;
	public static final int MSG_REQUEST_TIMEOUT=408;
	
	public static final int MSG_LENGTH_REQUIRED=411;
	public static final int MSG_PRECONDITION_FAILED=412;
	public static final int MSG_REQUEST_ENTITY_TOO_LARGE=413;
	public static final int MSG_UNSUPPORTED_MEDIA_TYPE=415;
	public static final int MSG_INTERNAL_SERVER_ERROR=500;
	public static final int MSG_NOT_IMPLEMENTED=501;
	public static final int MSG_SERVICE_UNAVAILABLE=503;
	
	public static Message getMessage(int what,int arg1,int arg2,Object obj){
		Message msg=Message.obtain();
		msg.what=what;
		msg.arg1=arg1;
		msg.arg2=arg2;
		msg.obj=obj;
		return msg;
	}
	public static Message getMessage(int what,int arg1,Object obj){
		Message msg=Message.obtain();
		msg.what=what;
		msg.arg1=arg1;
		msg.obj=obj;
		return msg;
	}
	public static Message getMessage(int what,Object obj){
		Message msg=Message.obtain();
		msg.what=what;
		msg.obj=obj;
		return msg;
	}
	
	@SuppressWarnings("deprecation")
	public static void notificationSysMms(Context context,String mmsState ){
		NotificationManager nManager = (NotificationManager)context.
				getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification= new Notification(R.drawable.logo,
				context.getString(R.string.new_notice), System.currentTimeMillis());
		Intent intent = new Intent(context,ChattingActivity.class);
		intent.setAction(mmsState);
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, R.string.app_name, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Preferences prefsUtils=Preferences.getInstance("test");
		@SuppressWarnings("static-access")
		int notificationDefaults=prefsUtils.getNotification(); 
		if(notificationDefaults!=0)
			notification.defaults |= notificationDefaults; 
		notification.setLatestEventInfo(context,context.getString(R.string.new_notice),
				1+context.getString(R.string.message_count), contentIntent);
		nManager.notify(2, notification);
	}
	public static void startSysNotice(Context context,String mmsState){
		Intent intent=new Intent();
		intent.setAction(mmsState);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setClass(context, ChattingActivity.class);
		context.startActivity(intent);
	}
}

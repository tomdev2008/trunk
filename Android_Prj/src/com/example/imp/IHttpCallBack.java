package com.example.imp;

import android.os.Handler.Callback;

public  interface IHttpCallBack extends Callback{
	public void onError(Object what,int errorCode);
}

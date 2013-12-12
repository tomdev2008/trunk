package com.example.map;

import android.os.Handler.Callback;

public  interface IMapCallBack extends Callback{
	public void onError(Object what,int errorCode);
}

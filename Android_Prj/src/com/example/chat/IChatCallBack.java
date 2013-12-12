package com.example.chat;

import android.os.Handler.Callback;

/**
 * 接收并处理消息的接口
 */
public interface IChatCallBack extends Callback{
	public void onError(int errorCode);
}

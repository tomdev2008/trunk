package com.example.imp;


import android.os.Bundle;

public interface IHttpRequest {
	void login(IHttpCallBack callBack,Bundle bundle);
	void register(IHttpCallBack callBack,Bundle bundle);
	void upload(IHttpCallBack callBack,Bundle bundle);
	void connect(IHttpCallBack callBack,Object result);
	void disconnect();
}

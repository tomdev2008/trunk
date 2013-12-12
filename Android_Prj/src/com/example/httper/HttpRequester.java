package com.example.httper;

import android.os.Bundle;
import android.os.Message;

import com.example.imp.IHttpCallBack;
import com.example.imp.IHttpRequest;
import com.example.utils.Logger;

public class HttpRequester implements IHttpRequest {

	private static HttpRequester requester;

	public static HttpRequester getInstance() {
		Logger.log(HttpRequester.class, "getInstance");
		if (requester == null) {
			requester = new HttpRequester();
		}
		return requester;
	}

	private void startHttpRequest(int what, IHttpCallBack callBack,
			Bundle bundle) {
		Logger.log(this, "startHttpRequest",what);
		Message message = Message.obtain();
		message.what = what;
		message.obj = callBack;
		message.setData(bundle);
		HttpHandler.handleMessage(message);
	}

	@Override
	public void login(IHttpCallBack callBack, Bundle bundle) {
		Logger.log("login",
				callBack + "/" + bundle.getString("userName"));
		startHttpRequest(0, callBack, bundle);
	}

	@Override
	public void register(IHttpCallBack callBack, Bundle bundle) {
		Logger.log("register",
				callBack + "/" + bundle.getString("userName"));
		startHttpRequest(1, callBack, bundle);
	}

	@Override
	public void connect(IHttpCallBack callBack, Object result) {
	}

	@Override
	public void disconnect() {

	}

	/**
	 * @param path
	 *            上传路径
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件 Map<String, String> params = new HashMap<String,
	 *            String>(); params.put("method", "save"); params.put("title",
	 *            title); params.put("timelength", timelength); //上传音频文件
	 *            uploadFile(file) FormFile formfile = new FormFile("02.mp3",
	 *            uploadFile, "video", "audio/mpeg");
	 *            SocketHttpRequester.post("xxx", params, formfile);
	 */
	@Override
	public void upload(final IHttpCallBack iCallBack,final Bundle bundle) {
		Logger.log("upload", bundle.toString());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpHandler.post(iCallBack, bundle);
			}
		});
	
	}

}

package com.example.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.chat.BaseActivity;
import com.example.entity.FormEntity;
import com.example.httper.HttpRequester;
import com.example.imp.IHttpCallBack;
import com.example.utils.FileUtils;
import com.example.utils.Messager;

public class UploadActivity extends BaseActivity implements IHttpCallBack,OnClickListener{
	private static final String UPLOAD_URL = "http://192.168.1.136:8080/WebLogin/upload";
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.upload_layout);
		 log(this, "onCreate");
		 findViewById(R.id.btn_upload).setOnClickListener(this);
	}

	@Override
	public void onError(Object what, int errorCode) {
		log("onError", what+"/"+errorCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload:
			File file=new File(FileUtils.STORAGE+"/com.example.activity/apk3.apk");
			FormEntity entity=new FormEntity(file, "apk/zip");
			Bundle bundle=new Bundle();
			bundle.putSerializable("entity", entity);
			bundle.putString("userName", "pamsa");
			bundle.putString("uploadUrl", UPLOAD_URL);
			HttpRequester.getInstance().upload(this, bundle);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		int what=msg.what;
		switch (what) {
		case Messager.MSG_DOWNLOAD:
//			log(this, "handleMessage downing", pBar.getProgress()+"---"+pBar.getMax());
			break;

		default:
			break;
		}
		return false;
	}

}

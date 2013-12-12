package com.example.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.example.chat.BaseActivity;
import com.example.httper.HttpRequester;
import com.example.imp.IHttpCallBack;

public class ExampleActivity extends BaseActivity implements IHttpCallBack {
	private HttpRequester requester = HttpRequester.getInstance();
	private SparseArray<Object> sparseArray = new SparseArray<Object>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log( "onCreate");
//		setData();
	}

	private void setData() {
		for (int i = 0; i < 10; i++) {
			sparseArray.put(0, requester);
		}
		sparseArray.put(9, "111");
		log( "onCreate", sparseArray.size());
		int length=sparseArray.size();
		for (int it =0; it <length; it++) {
			Object obj = sparseArray.valueAt(it);
			log( "onCreate~", obj);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log( "onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		log( "onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		log( "onKeyUp");
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		log( "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		log( "onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();
		log("onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		log( "onStop");
	}

	public HttpRequester getRequester() {
		log( "getRequester");
		return requester;
	}

	@Override
	public void onError(Object what, int errorCode) {

	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
}

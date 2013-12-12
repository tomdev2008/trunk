package com.example.down;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.example.activity.R;
import com.example.activity.R.id;
import com.example.activity.R.layout;
import com.example.chat.BaseActivity;
import com.example.imp.IHttpCallBack;
import com.example.utils.Messager;

public class DownloadActivity extends BaseActivity implements IHttpCallBack,OnClickListener{
	private static final String DOWN_URL = "http://192.168.1.196:8080/WebLogin/apk";
	private static final String[] DOWN_APK = {DOWN_URL+"/apk1.apk",DOWN_URL+"/apk2.apk",
		DOWN_URL+"/apk3.apk",DOWN_URL+"/apk4.apk"};
	private Map<String, ProgressBar> pBars = new HashMap<String, ProgressBar>();
	private ProgressBar pBar1,pBar2,pBar3,pBar4;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.layout_main);
		 findViewById(R.id.btn_down1).setOnClickListener(this);
		 findViewById(R.id.btn_down2).setOnClickListener(this);
		 findViewById(R.id.btn_down3).setOnClickListener(this);
		 findViewById(R.id.btn_down4).setOnClickListener(this);
		 pBar1=(ProgressBar) findViewById(R.id.pbar_apk1);
		 pBar2=(ProgressBar) findViewById(R.id.pbar_apk2);
		 pBar3=(ProgressBar) findViewById(R.id.pbar_apk3);
		 pBar4=(ProgressBar) findViewById(R.id.pbar_apk4);
		 findViewById(R.id.btn_ok).setOnClickListener(this);
		 pBars.put(DOWN_APK[0], pBar1);
		 pBars.put(DOWN_APK[1], pBar2);
		 pBars.put(DOWN_APK[2], pBar3);
		 pBars.put(DOWN_APK[3], pBar4);
	}
	
	/**
	 * 83 * 响应开始下载按钮的点击事件 84
	 */
	public void startDownload(int downId) {
		String downUrl = DOWN_APK[downId];
		log( "startDownload",downUrl);
		Downloader downloader =Downloader.getInstance();
//		if (downloader.isDownloading(downUrl))
//			return;
		// 得到下载信息类的个数组成集合
		 try {
			
			ProgressBar bar=pBars.get(downUrl);
			bar.setMax(downloader.download(this, downUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 响应暂停下载按钮的点击事件
	 */
	public void pauseDownload(String downUrl) {
		
	}

	@Override
	public void onError(Object what, int errorCode) {
		log("onError", what+"/"+errorCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_down1:
			startDownload(0);
			break;
		case R.id.btn_down2:
			startDownload(1);		
			break;
		case R.id.btn_down3:
			startDownload(2);
			break;
		case R.id.btn_down4:
			startDownload(3);
			break;
		case R.id.btn_ok:
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
			String downUrl=(String) msg.obj;
			ProgressBar pBar=pBars.get(downUrl);
			pBar.incrementProgressBy(msg.arg1);
//			log( "handleMessage downing", pBar.getProgress()+"---"+pBar.getMax());
			if (pBar.getProgress() == pBar.getMax()) {
//				ToastUtils.show("下载完成");
//				log( "handleMessage", "下载完成");
			}
			break;

		default:
			break;
		}
		return false;
	}

}

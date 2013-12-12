package com.example.down;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Message;
import android.widget.ProgressBar;

import com.example.app.ExampleApp;
import com.example.imp.IHttpCallBack;
import com.example.utils.FileUtils;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class AppUpdater implements IHttpCallBack{
	//下载更新URL
	private static final String UPDATE_DOWNURL = "http://www.www.baidu.com/test_update/update_test.apk";
	//检查更新URL
	private static final String UPDATE_CHECKURL = "http://www.www.baidu.com/test_update/update_version.txt";
	private static final String UPDATE_APKNAME = "updateapk.apk";
	
	private Context context;

	public AppUpdater(ProgressBar pbar) {
		context = ExampleApp.getInstance();
		checkUpdate();
	}



	public void checkUpdate() {
		new Thread() {
			@Override
			public void run() {
				try {
					String verjson = checkNewVersion();
					JSONArray array = new JSONArray(verjson);
					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						int newVersionCode = Integer.parseInt(obj
								.getString("verCode"));
						if (newVersionCode > getCurrVersion()) {
							Downloader.getInstance().download(AppUpdater.this, UPDATE_DOWNURL);
						}
					}
				} catch (Exception e) {
					Logger.log("checkUpdate error", e);
				}
			};
		}.start();
	}

	private int getCurrVersion() {
		int curVersionCode;
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			curVersionCode = pInfo.versionCode;
			Logger.log("getCurVersion", curVersionCode);
		} catch (NameNotFoundException e) {
			Logger.log("getCurVersion error", e);
			curVersionCode = 111000;
		}
		return curVersionCode;
	}
	
	public String checkNewVersion() throws Exception {
		BufferedReader bufferedReader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
					"android");
			
			 HttpParams httpParams = client.getParams();
			 HttpConnectionParams.setConnectionTimeout(httpParams, 3*1000);
			 HttpConnectionParams.setSoTimeout(httpParams, 5000);
			 
			HttpGet request = new HttpGet();
			request.setHeader("Content-Type", "text/plain; charset=utf-8");
			request.setURI(new URI(UPDATE_CHECKURL));
			HttpResponse response = client.execute(request);
			bufferedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent(), "utf-8"));

			String line = "";
			StringBuffer stringBuffer = new StringBuffer("");
			String NL = System.getProperty("line.separator");
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + NL);
			}
			return stringBuffer.toString();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void onError(Object what, int errorCode) {
	}

	public void updateInstall() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.fromFile(new File(FileUtils.getDownPath(), UPDATE_APKNAME)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	@Override
	public boolean handleMessage(Message msg) {
		int what=msg.what;
		switch (what) {
		case Messager.MSG_UPDATE:
			
			break;

		default:
			break;
		}
		return false;
	}
}

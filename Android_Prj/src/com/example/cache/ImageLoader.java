package com.example.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import com.example.utils.Logger;

@SuppressLint("NewApi")
public class ImageLoader {
	private static final int KEEP_ALIVE = 3;
	private static final int CORE_POOL_SIZE = 5;
	private static final int MAX_POOL_SIZE = 256;
	private static final int DELAY_BEFORE_PURGE = 30 * 1000;
	private final static Executor execService = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());
	private Handler purgeHandler;
	private DiskLruCache diskLruCache;
	private MemoryCache memoryCache;
	private ConnectivityManager connectivity;

	public ImageLoader(Context context) {
		purgeHandler = new Handler();
		memoryCache = new MemoryCache();
		diskLruCache = new DiskLruCache(context);
		connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * 加载图片
	 * 
	 * @param imgUrl
	 * @param ImageView
	 */
	public void loadImage(ImageView imgView, String imgUrl) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(imgUrl);// 从缓存中读取
		Logger.log(this, "loadImage", imgUrl + "//" + bitmap);
		if (!isAvailable()) {
//			imgView.setBackgroundColor(Color.TRANSPARENT);
			return;
		}
		if (bitmap == null) {
			ImageLoadTask loadTask = new ImageLoadTask(imgView,imgUrl);
			loadTask.execute(execService);
		} else {
			imgView.setImageBitmap(bitmap);// 设为缓存图片
			imgView.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	/**
	 * 返回缓存，没有则返回空
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String imgUrl) {
		Bitmap bitmap = memoryCache.getBitmapFormMemory(imgUrl);
		if (bitmap != null) {
			return bitmap;
		}
		return diskLruCache.getBitmapFormLocal(imgUrl);// 从本地缓存中拿
	}


	public class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		private String imgUrl;
		private ImageView imgView;
		public ImageLoadTask(ImageView imgView,String imgUrl) {
			this.imgUrl = imgUrl;
			this.imgView=imgView;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			Logger.log(this, "doInBackground", params);
			Bitmap bitmap = loadBitmapFormNetwork(imgUrl);// 获取网络图片
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Logger.log(this, "onPostExecute", result);
			if (result == null) {
				return;
			}
			imgView.setImageBitmap(result);
			memoryCache.putBitmapToMemory(imgUrl, result);// 放入缓存
			
		}

		private Bitmap loadBitmapFormNetwork(String imgUrl) {
			Logger.log(this, "loadBitmapFormNetwork", imgUrl);
			Bitmap bitmap = null;
			HttpGet httpGet  = null;
			InputStream iStream = null;
			HttpClient client = AndroidHttpClient.newInstance("Android");
			HttpParams params = client.getParams();
			HttpConnectionParams.setSocketBufferSize(params, 5000);
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			try {
				httpGet = new HttpGet(imgUrl);
				HttpResponse response = client.execute(httpGet);
				int stateCode = response.getStatusLine().getStatusCode();
				if (stateCode != HttpStatus.SC_OK) {
					Logger.log(this, "loadImgFormNetwork", stateCode);
					return null;
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					iStream = entity.getContent();
					return diskLruCache.putBitmapToLocal(imgUrl, iStream);
				}
			} catch (ClientProtocolException e) {
				httpGet.abort();
				e.printStackTrace();
				Logger.log(this,
						"loadBitmapFormNetwork ClientProtocolException", e);
			} catch (IOException e) {
				httpGet.abort();
				e.printStackTrace();
				Logger.log(this, "loadBitmapFormNetwork IOException", e);
			} finally {
				if (iStream != null) {
					try {
						iStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				((AndroidHttpClient) client).close();
			}
			return bitmap;
		}
	}

	private boolean isAvailable() {
		NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			State state = connectivity.getNetworkInfo(
					ConnectivityManager.TYPE_WIFI).getState();
			Logger.log(this, "isAvailable wifi", state.name());
			if (State.CONNECTED == state) {
				return true;
			}
			state = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			Logger.log(this, "isAvailable gprs", state.name());
			if (State.CONNECTED == state) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 定时清理缓存
	 */
	private Runnable clearRunnable = new Runnable() {
		@Override
		public void run() {
			Logger.log(this, "clearRunnable", "run");
			diskLruCache.clearDataCache();
			memoryCache.clearMemoryCache();
		}
	};

	/**
	 * 重置缓存清理的timer
	 */
	public void resetPurgeTimer() {
		Logger.log(this, "resetPurgeTimer");
		purgeHandler.removeCallbacks(clearRunnable);
		purgeHandler.postDelayed(clearRunnable, DELAY_BEFORE_PURGE);
	}

}

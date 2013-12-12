package com.example.cache;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.example.utils.Logger;


@SuppressLint("NewApi")
public class MemoryCache{
	private static final int SECOND_CACHE_CAPACITY = 200;// 二级缓存的最大空间
	private static final int FIRST_CACHE_SIZE = 30 * 1024 * 1024;// 30M
	
	private final LruCache<String, Bitmap> firstImgCache = new LruCache<String, Bitmap>(
			FIRST_CACHE_SIZE) {
		@Override
		public int sizeOf(String imgUrl, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}

		@Override
		protected void entryRemoved(boolean evicted, String imgUrl,
				Bitmap oldValue, Bitmap newValue) {
			// 硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存区
			Logger.log(this, "entryRemoved", imgUrl);
			firstImgCache.remove(imgUrl);
			secondImgCache.put(imgUrl, new WeakReference<Bitmap>(oldValue));
		}
	};

	// 软引用
	private final static LinkedHashMap<String, WeakReference<Bitmap>> 
			secondImgCache = new LinkedHashMap<String, WeakReference<Bitmap>>(
			SECOND_CACHE_CAPACITY, 0.75f, true);
	public MemoryCache() {}
	
	public Bitmap getBitmapFormMemory(String imgUrl){
		if (imgUrl==null) {
			return null;
		}
		Logger.log(this, "getBitmapFormMemory", imgUrl);
		Bitmap bitmap;
		bitmap = getBitmapFromFirstCache(imgUrl);// 从一级缓存中拿
		if (bitmap != null) {
			return bitmap;
		}
		return getBitmapFromSecondCache(imgUrl);// 从二级缓存中拿
	}
	
	/**
	 * 从一级缓存中拿
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromFirstCache(String imgUrl) {
		Logger.log(this, "getBitmapFromFirstCache", imgUrl);
		Bitmap bitmap = null;
		synchronized (firstImgCache) {
			bitmap = firstImgCache.get(imgUrl);
			Logger.log(this, "getBitmapFromFirstCache", bitmap);
			if (bitmap != null) {// 将最近访问的元素放到链的头部，提高下一次访问该元素的检索速度（LRU算法）
				firstImgCache.remove(imgUrl);
				firstImgCache.put(imgUrl, bitmap);
			}
		}
		return bitmap;
	}

	/**
	 * 从二级缓存中拿
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromSecondCache(String imgUrl) {
		Logger.log(this, "getBitmapFromSecondCache", imgUrl);
		Bitmap bitmap = null;
		WeakReference<Bitmap> weakReference = secondImgCache.get(imgUrl);
		Logger.log(this, "getBitmapFromSecondCache", weakReference);
		if (weakReference != null) {
			bitmap = weakReference.get();
			if (bitmap == null) {// 由于内存吃紧，软引用已经被gc回收了
				secondImgCache.remove(imgUrl);
			}
		}
		return bitmap;
	}

	/**
	 * 放入第一缓存
	 * 
	 * @param ImgUrl
	 * @param Bitmap
	 */
	public void putBitmapToMemory(String imgUrl, Bitmap bitmap) {
		Logger.log(this, "putBitmapToMemory", imgUrl+"/"+bitmap);
		if (imgUrl == null || bitmap == null) {
			return;
		}
		synchronized (firstImgCache) {
			firstImgCache.put(imgUrl, bitmap);
		}
	}

	public void clearMemoryCache(){
		Logger.log(this, "clearMemoryCache");
		firstImgCache.evictAll();
		secondImgCache.clear();
	}
}

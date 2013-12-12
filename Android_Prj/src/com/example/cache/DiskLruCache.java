package com.example.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LruCache;

import com.example.activity.R;
import com.example.utils.Logger;
import com.example.utils.Toaster;

@SuppressLint("NewApi")
public class DiskLruCache {
	private static final int COMPRESS_PERCENT = 50;//图片压缩百分比
	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private static final int MAX_DATA_CAPACITY= 10* 1024 * 1024; // 10M
	private static final int MAX_DISK_CAPACITY= 100 * 1024 * 1024; // 100M
	private static final String STORAGE=Environment.getExternalStorageDirectory().
			getAbsolutePath();
	
	private final LruCache<String, Long> dataImgCache = new LruCache<String, Long>(
			MAX_DATA_CAPACITY) {
		@Override
		public int sizeOf(String imgUrl, Long value) {
			Logger.log(this, "sizeOf", imgUrl);
			return value.intValue();
		}

		@Override
		protected void entryRemoved(boolean evicted, String imgUrl,
				Long oldValue, Long newValue) {
			Logger.log(this, "entryRemoved", imgUrl);
			File file = getFileFromUrl(imgUrl);
			if (file != null)
				file.delete();
		}
	};
	
	private BitmapFactory.Options bmapOptions;
	private String dataCachePath,diskCachePath;
	public DiskLruCache(Context context) {
		bmapOptions = new BitmapFactory.Options();
		bmapOptions.inPurgeable = true; // bitmap 是否可被清理
		createDataCachePath(context);
		createDiskCachePath(context);
	}
	
	private void createDiskCachePath(Context context){
		if (usableDevice()) {
			File file = new File(STORAGE +File.separator+ 
					context.getPackageName()+File.separator+"cache");
			if (!file.exists()) {
				 file.mkdirs();
			}
			diskCachePath=file.getAbsolutePath();
		}
	}
	
	private void createDataCachePath(Context context){
		File file = new File(context.getCacheDir()
				.getAbsolutePath()+File.separator+"cache");
		if (!file.exists()) {
			 file.mkdirs();
		}
		diskCachePath=file.getAbsolutePath();
	}
	
	/**
	 * 缓存到本地
	 * @param imgUrl
	 * @param iStream
	 * @return
	 */
	public Bitmap putBitmapToLocal(String imgUrl, InputStream iStream){
		if (usableDevice())
			return putBitmapToDiskCache(imgUrl, iStream);
		else
			return putBitmapToDataCache(imgUrl, iStream);
	}
	
	/**
	 * 缓存到sdcard cache
	 * @param imgUrl
	 * @param iStream
	 * @return
	 */
	private Bitmap putBitmapToDiskCache(String imgUrl, InputStream stream) {
		File imgFile = getFileFromDisk(imgUrl);
		Logger.log("putBitmapToDiskCache inputStream", imgFile);
		if (!imgFile.exists()) {
			try {
				imgFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resetDiskCache(imgFile);
		try {
			FileOutputStream fStream = new FileOutputStream(imgFile);
			int len;
			byte[] bytes = new byte[IO_BUFFER_SIZE];
			while ((len = stream.read(bytes)) > 0) {
				fStream.write(bytes, 0, len);
			}
			fStream.flush();
			fStream.close();
			stream.close();
			return BitmapFactory.decodeStream(new FileInputStream(imgFile),
					null, bmapOptions);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log("putBitmapToDiskCache inputStream error", e);
		}
		
		return null;
	}
	
	/**
	 * 缓存到data cache
	 * 
	 * @param imgUrl
	 * @param InputStream
	 */
	private Bitmap putBitmapToDataCache(String imgUrl, InputStream stream) {
		boolean compress = false;
		File imgFile = getFileFromUrl(imgUrl);
		if (!imgFile.exists()) {
			try {
				imgFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resetDiskCache(imgFile);
		Logger.log("putBitmapToDiskCache", imgFile);
		try {
			FileOutputStream fos = new FileOutputStream(imgFile);
			Bitmap bitmap = BitmapFactory.decodeStream(stream);
			compress = bitmap.compress(CompressFormat.JPEG, COMPRESS_PERCENT,
					fos);
			Logger.log("putBitmapToDiskCache compress", compress);
			if (compress) {
				synchronized (dataImgCache) {
					dataImgCache.put(imgUrl, imgFile.length());
				}
			}
			fos.flush();
			fos.close();
			return BitmapFactory.decodeStream(new FileInputStream(imgFile),
					null, bmapOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从本地缓存取（分data分区及sdcard）
	 * @param imgUrl
	 * @return
	 */
	public Bitmap getBitmapFormLocal(String imgUrl) {
		if (imgUrl==null) {
			return null;
		}
		Logger.log(this, "getBitmapFormLocal", imgUrl);
		File bitmapFile = null;
		if (usableDevice())
			bitmapFile = getFileFromDisk(imgUrl);
		else
			bitmapFile = getFileFromUrl(imgUrl);
		if (bitmapFile==null||(!bitmapFile.exists())) {
			bitmapFile = null;
		}
		try {
			if (bitmapFile != null) {
				return BitmapFactory.decodeStream(new FileInputStream(
						bitmapFile), null, bmapOptions);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * @param imgUrl
	 * @return
	 */
	private File getFileFromUrl(String imgUrl) {
		String fileName = imgUrl.hashCode() + ".jpg";
		File cacheFile = new File(dataCachePath+ File.separator
				+ fileName);
		Logger.log(this, "getFileFromUrl", cacheFile);
		return cacheFile;
	}
	
	/**
	 * @param imgUrl
	 * @return
	 */
	private File getFileFromDisk(String imgUrl) {
		if (!usableDevice()) {
			return null;
		}
		File imgDiskDir = new File(diskCachePath);
		if (!imgDiskDir.exists()) {
			imgDiskDir.mkdirs();
		}
		String fileName = imgUrl.hashCode() + ".jpg";
		File cacheFile = new File(imgDiskDir.getAbsolutePath() + File.separator
				+ fileName);
		Logger.log(this, "getFileFromDisk", cacheFile);
		return cacheFile;
	}

	private boolean usableDevice() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		Toaster.show(R.string.unload_device);
		return false;
	}

	private void resetDiskCache(File imgFile){
		if (imgFile==null) {
			return;
		}
		if (imgFile.length()>=getFreeSpace()
				||getUsageSpace()>MAX_DISK_CAPACITY) {
			File fDiskDir = new File(diskCachePath);
			File[] diskFiles=fDiskDir.listFiles();
			for (File file:diskFiles) {
				file.delete();
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	private long getFreeSpace() {
		File fDiskDir = new File(diskCachePath);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
//			return fDiskDir.getUsableSpace();
//		} else {
			final StatFs stat = new StatFs(fDiskDir.getAbsolutePath());
			return (long) stat.getAvailableBlocks()
					* (long) stat.getBlockSize();
//		}
	}

	private long getUsageSpace() {
		long usage = 0;
		File imgDiskDir = new File(diskCachePath);
		for (final File cacheFile : imgDiskDir.listFiles()) {
			usage += cacheFile.length();
		}
		return usage;
	}
	
	public void clearDataCache(){
		dataImgCache.evictAll();
	}
}

package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

import com.example.activity.R;
import com.example.app.ExampleApp;

public class FileUtils {
	private static Context context=ExampleApp.getInstance();
	public static final String STORAGE=Environment.getExternalStorageDirectory().
																getAbsolutePath();
	public static boolean storageState(){
		if(Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)){
			return true;
		}
		Toaster.show(R.string.unload_device);
		return false;
	}
	
	public static String getDiskPath(){
		File file = new File(STORAGE +File.separator+ 
				context.getPackageName());
		if (!file.exists()) {
			 file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static String getDownPath(){
		File file = new File(getDiskPath()+
				File.separator+ "download");
		if (!file.exists()) {
			 file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static String getCachePath(){
		File file = new File(context.getCacheDir()
				.getAbsolutePath()+File.separator+"cache");
		if (!file.exists()) {
			 file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static String getDiskCachePath(){
		File file = new File(getDiskPath()+
				File.separator+"cache");
		if (!file.exists()) {
			 file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getVoicePath(){
		File file = new File(getDiskPath()+
				File.separator+"voice");
		if (!file.exists()) {
			 file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	private static File createSDFile(String filePath,String fileName) throws IOException {
		File file = new File(STORAGE +File.separator+ filePath);
		if (!file.exists()) {
			 file.mkdirs();
		}
		file = new File(file.getAbsoluteFile()+File.separator+ fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public static File writeFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			file = createSDFile(path,fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
}

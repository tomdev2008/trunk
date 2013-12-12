package com.example.utils;

import android.widget.Toast;

import com.example.app.ExampleApp;


public class Toaster {

	public static void show(int resId){
		Toast.makeText(ExampleApp.getInstance(), resId, 
				Toast.LENGTH_LONG).show();
	}
	
	public static void show(final String text){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(ExampleApp.getInstance(), text, 
						Toast.LENGTH_LONG).show();
			}
		}).start();
	
	}
	
	public static void show(int resId,String text){
		Toast.makeText(ExampleApp.getInstance(),
				ExampleApp.getInstance().getString(resId)+text, 
				Toast.LENGTH_LONG).show();
	}
}

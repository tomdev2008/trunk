package com.example.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.activity.R;
import com.example.utils.Logger;

public class ConvertFloatView {
	private static View floatLayout;
	private static TextView tv_convert;
	private static WindowManager wManager;
	private static WindowManager.LayoutParams wmParams ;

	private static void createFloatView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		floatLayout = inflater.inflate(R.layout.convert_layout, null);
		tv_convert = (TextView) floatLayout.findViewById(R.id.tv_convert);
		wManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		wmParams.gravity = Gravity.CENTER;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCH_MODAL;
		wmParams.width = LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		wManager.addView(floatLayout, wmParams);
		Logger.log("Convert createFloatView", floatLayout+"/"+tv_convert);
	}
 
	public static void showFloatView(Context context) {
		if (floatLayout!=null) 
			removeFloatView();
		createFloatView(context);
		Logger.log("Convert showFloatView", floatLayout);
	}

	public static void removeFloatView() {
		if (wManager != null)
			wManager.removeView(floatLayout);
		floatLayout=null;wManager=null;wmParams=null;
		Logger.log("Convert removeFloatView", floatLayout);
	}

	public static void convertNext(String convert) {
		Logger.log("Convert convertNext", convert + "/" + tv_convert);
		if (tv_convert!=null) {
			tv_convert.setText(convert);
		}
	}

}

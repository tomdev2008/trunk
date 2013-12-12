package com.example.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeRender {

	@SuppressLint("SimpleDateFormat")
	public static String getDate(String format) {
		SimpleDateFormat formatBuilder = new SimpleDateFormat(format);
		return formatBuilder.format(new Date());
	}

	public static String getDate() {
		return getDate("hh:mm:ss");
	}
	
}

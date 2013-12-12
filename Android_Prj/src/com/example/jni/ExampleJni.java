package com.example.jni;

import android.R.id;

public abstract class ExampleJni {
	static{
		System.loadLibrary("test");
	}
	public native static id getJniId();
}

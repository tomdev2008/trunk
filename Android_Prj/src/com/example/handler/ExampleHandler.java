package com.example.handler;

import com.example.utils.Logger;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class ExampleHandler extends Handler {
	private static ExampleHandler eHandler;
	
	public static ExampleHandler getInstance(){
		if (eHandler==null) {
			HandlerThread thread=new HandlerThread("ExampleHandler");
			thread.start();
			eHandler=new ExampleHandler(thread.getLooper());
		}
		return eHandler;
	}
	
	public ExampleHandler() {
		super();
	}

	public ExampleHandler(Looper looper) {
		super(looper);
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Logger.log(this, "handleMessage", msg);
		switch (msg.what) {
			case 0:
				break;
			default:
				break;
		}
	}
	
}

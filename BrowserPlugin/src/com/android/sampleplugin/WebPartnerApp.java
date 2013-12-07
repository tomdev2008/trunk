package com.android.sampleplugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.yunva.waya.helper.Logger;

public class WebPartnerApp extends Activity{  
	    
	static {
		// needed for jni calls
		try {
//			System.loadLibrary("sampleplugin");
			Logger.log("UCPartnerApp", "loadLibrary",true);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        /** Establish the activity's UI */
        setContentView(R.layout.main);
        
        startService(new Intent(getApplicationContext(),SamplePlugin.class));
    }

}
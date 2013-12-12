package com.example.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class MyNotifyListener extends BDNotifyListener{
	private IMapCallBack iCallBack;
	public void registerNotifyListener(IMapCallBack iCallBack) {
		this.iCallBack = iCallBack;
	}

	public void unregisterNotifyListener(IMapCallBack iCallBack) {
		this.iCallBack = null;
	}
	
	@Override
	public void SetNotifyLocation(double arg0, double arg1, float arg2,
			String arg3) {
		super.SetNotifyLocation(arg0, arg1, arg2, arg3);
		Logger.log(this, "SetNotifyLocation", arg0+"/"+arg1+"/"+arg2+"/"+arg3);
	}

	@Override
	public void onNotify(BDLocation bdLocation, float error) {
		super.onNotify(bdLocation, error);
		Logger.log(this, "onNotify", bdLocation+"/"+error);
		onNotify(bdLocation, error);
	}

	/**
	 * 处理通知
	 * @param object
	 * @param error
	 */
	private void onNotify(BDLocation bdLocation,int error){
		if (error!=0||bdLocation==null)
			iCallBack.onError(0, error);
		else 
			iCallBack.handleMessage(Messager.getMessage(error,bdLocation));
	}
	
}

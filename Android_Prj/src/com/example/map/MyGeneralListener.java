package com.example.map;

import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.example.activity.R;
import com.example.utils.Logger;
import com.example.utils.Toaster;

public class MyGeneralListener implements MKGeneralListener {

	@Override
	public void onGetNetworkState(int iError) {
		Toaster.show(R.string.net_failure);
	}

	@Override
	public void onGetPermissionState(int iError) {
		Logger.log(this, "onGetPermissionState", iError);
		if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
			// 授权Key错误：
			Toaster.show(R.string.authorize_error);
		}
	}

}

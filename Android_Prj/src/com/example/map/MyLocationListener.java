package com.example.map;

import java.util.HashMap;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class MyLocationListener implements BDLocationListener{
	private Map<String,MapCallBack> mCallBacks;
	public MyLocationListener() {
		Logger.log(this, "MyLocationListener");
		mCallBacks=new HashMap<String,MapCallBack>();
	}

	public void registerLocationListener(IMapCallBack iCallBack,int iCallType) {
		String key=iCallBack.getClass().getSimpleName();
		MapCallBack mCallBack=new MapCallBack();
		mCallBack.setiCallType(iCallType);
		mCallBack.setiCallBack(iCallBack); 
		Logger.log(this, "registerLocationListener",key);
		mCallBacks.put(key, mCallBack);
	}
	
	public void unregisterLocationListener(IMapCallBack iCallBack){
		String key=iCallBack.getClass().getSimpleName();
		Logger.log(this, "unregisterLocationListener",key);
		mCallBacks.remove(key);
	}
	
	public void clearLocationListener(){
		mCallBacks.clear();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		Logger.log(this, "onReceiveLocation",location);
		dispatchReceiveLocation(location);
	}
	
	private void dispatchReceiveLocation(BDLocation location){
		Logger.log(this, "dispatchReceiveLocation",mCallBacks.size());
		LocationEntity locationEntity=getLocationEntity(location);
		synchronized (mCallBacks) {
			for (Map.Entry<String,MapCallBack> entry:mCallBacks.entrySet()) {
				MapCallBack mCallBack=entry.getValue();
				if (enable(mCallBack)) {
					if (locationEntity==null) {
						mCallBack.getiCallBack().onError(0,0);
					}
					else{
						mCallBack.getiCallBack().handleMessage(
								Messager.getMessage(0,locationEntity));
					}
				}
			}
		}
	}

	private boolean enable(MapCallBack listener){
		int iCallType=listener.getiCallType();
		if (iCallType==0) {
			listener.setiCallType(-1);
			return true;
		}
		else if (iCallType==1) {
			return true;
		}
		return false;
	}
	
	private LocationEntity getLocationEntity(BDLocation location){
		if (location==null) {
			return null;
		}
		LocationEntity locationEntity=new LocationEntity();
		locationEntity.setLatitude((int) (location.getLatitude()*1e6));
		locationEntity.setLongitude((int) (location.getLongitude()*1e6));
		locationEntity.setCity(location.getCity());
		if (location.getLocType() == BDLocation.TypeGpsLocation) {
//			locationBuffer.append(location.getSpeed());
//			locationBuffer.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			locationEntity.setAddress(location.getAddrStr());
		}
		return locationEntity;
	}
	
	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		Logger.log(this, "onReceivePoi", poiLocation);
		if (poiLocation == null) {
			return;
		}
	}

}

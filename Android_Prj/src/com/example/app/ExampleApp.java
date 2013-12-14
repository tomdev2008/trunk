package com.example.app;

import android.app.Application;
import android.util.SparseArray;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKSearch;
import com.example.chat.BaseEntity;
import com.example.map.IMapCallBack;
import com.example.map.MyGeneralListener;
import com.example.map.MyLocationListener;
import com.example.map.MyNotifyListener;
import com.example.map.MySearchListener;
import com.example.utils.Logger;
import com.example.utils.Preferences;

public class ExampleApp extends Application {
	private final static String MAP_KEY="9044c8014a1eb0087607e353a77c002b";
	
	private static ExampleApp exampleApp;
	private static boolean isAuthorize;
	private static MKSearch mkSearch;
	private static LocationClient locationClient;
	private static BMapManager mapManager;
	
	private static MyNotifyListener notifyListener;
	private static MySearchListener searchListener;
	private static MyLocationListener locationListener;
	
	private static SparseArray<BaseEntity> baseArray;
	
	public static ExampleApp getInstance() {
		return exampleApp;
	}

	public static boolean isAuthorize() {
		return isAuthorize;
	}

	public static void setAuthorize(boolean isAuthorize) {
		ExampleApp.isAuthorize = isAuthorize;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.log(this, "onCreate");
		initBaiduMap();
		baseArray=new SparseArray<BaseEntity>();
		if (!Preferences.getInstance("test").isFirstInstall()) {
			Preferences.getInstance("test").setFirstInstall(true);
			Preferences.getInstance("test").setNotification(1);
		}
	}
	
	public static void registerListener(Object object){
		Logger.log("registerActivity", object.toString());
		BaseEntity baseEntity=new BaseEntity();
		baseEntity.setObject(object);
		baseArray.put(getHashCode(object), baseEntity);
	}
	
	public static void registerListener(Object object,boolean enable){
		Logger.log("registerActivity", object.toString());
		BaseEntity baseEntity=new BaseEntity();
		baseEntity.setObject(object);
		baseEntity.setEnable(enable);
		baseArray.put(getHashCode(object), baseEntity);
	}
	
	public static void unregisterAllListener() {
		for (int vari = 0; vari < baseArray.size(); vari++) {
			BaseEntity baseEntity=baseArray.valueAt(vari);
			Logger.log("unregisterAllActivity", baseEntity);
			baseEntity.finish();
		}
		baseArray.clear();baseArray=null;
	}
	
	public static void unregisterListener(Object obj) {
		BaseEntity baseEntity=baseArray.get(getHashCode(obj));
		Logger.log("unregisterActivity", baseEntity);
		baseArray.delete(getHashCode(obj));
		baseEntity.finish();
	}
	
	public static void modifyListener(Object obj,boolean enable) {
		Logger.log("modifyListener", obj, enable);
		BaseEntity baseEntity=baseArray.get(getHashCode(obj));
		if (baseEntity!=null) {
			baseEntity.setEnable(enable);
		}
		else {
			registerListener(obj);
			modifyListener(obj, enable);
		}
	}
	
	private static int getHashCode(Object obj){
		return obj.getClass().getSimpleName().hashCode();
	}
	
	public static boolean hasListener(Object obj){
		BaseEntity baseEntity=baseArray.get(getHashCode(obj));
		if (baseEntity==null) {
			return false;
		}
		return true;
	}
	
	private void initBaiduMap(){
		mapManager = new BMapManager(this);
		isAuthorize= mapManager
				.init(MAP_KEY, new MyGeneralListener());
		initMapSearch();
		notifyListener=new MyNotifyListener();
		locationClient.registerNotify(notifyListener);
		locationListener=new MyLocationListener();
		locationClient.registerLocationListener(locationListener); 
		setLocationParams(false);
		Logger.log(this, "initBaiduMap", mapManager+"/"+locationClient+"/"+isAuthorize);
	}
	
	@SuppressWarnings("static-access")
	private void initMapSearch(){
		Logger.log(this, "initMapSearch");
		if (!isAuthorize) {
			return;
		}
		mkSearch=new MKSearch();
		mkSearch.setPoiPageCapacity(50);
		searchListener=new MySearchListener();
		mkSearch.init(mapManager, searchListener);
		Logger.log(this, "initMapSearch",mkSearch+"/"+searchListener);
	}
	
	public void setLocationParams(boolean scanSpan){
		if (locationClient==null) {
			locationClient=new LocationClient(this);
		}
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		if (scanSpan) {
			option.setScanSpan(3000);//设置发起定位请求的间隔时间为5000ms
		}
		option.disableCache(false);//禁止启用缓存定位
		option.setPoiNumber(5);    //最多返回POI个数   
		option.setPoiDistance(1000); //poi查询距离        
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息   
		locationClient.setLocOption(option);
	}
	
	public MKSearch getMkSearch() {
		return mkSearch;
	}

	public static void registerLocationListener(IMapCallBack iCallBack,int ongoing){
		locationListener.registerLocationListener(iCallBack,ongoing);
	}
	
	public static void unregisterLocationListener(IMapCallBack iCallBack){
		locationListener.unregisterLocationListener(iCallBack);
	}
	
	public static void registerSearchListener(int callType,IMapCallBack iCallBack){
		searchListener.registerSearchListener(callType,iCallBack);
	}
	
	public static void unregisterSearchListener(IMapCallBack iCallBack){
		searchListener.unregisterSearchListener(iCallBack);
	}
	
	public static void startLocation(){
		if (locationClient != null)
			locationClient.start();
		if (locationClient.isStarted()){
			locationClient.requestLocation();
		}
	}
	
	public static void stopLocation(){
		if (locationClient!=null) 
			locationClient.stop();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Logger.log(this, "onLowMemory");
		System.gc();
		destroyMap();
	}
	
	private void destroyMap(){
		if (locationClient.isStarted()) {
			locationClient.stop();
			locationClient.removeNotifyEvent(notifyListener);
			locationClient.unRegisterLocationListener(locationListener);
		}
		locationClient=null;
		mapManager.stop();
		mapManager.destroy();
	}

}

package com.example.map;

import android.util.SparseArray;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class MySearchListener implements MKSearchListener {
	private SparseArray<MapCallBack> searchArrays;
	
	public MySearchListener() {
		searchArrays=new SparseArray<MapCallBack>();
	}

	public void registerSearchListener(int iCallType,IMapCallBack iCallBack) {
		Logger.log(this, "registerSearchListener", iCallType+"/"+iCallBack);
		MapCallBack mCallBack=new MapCallBack();
		mCallBack.setiCallType(iCallType);
		mCallBack.setiCallBack(iCallBack); 
		searchArrays.put((iCallType+iCallBack.hashCode()), mCallBack);
	}
	
	public void clearSearchListener(){
		Logger.log(this, "removeSearchListener");
		searchArrays.clear();
	}
	
	public void unregisterSearchListener(IMapCallBack iCallBack){
		Logger.log(this, "unregisterSearchListener", iCallBack);
		int length=searchArrays.size();
		for (int it = length-1; it >=0; it--) {
			MapCallBack mCallBack = searchArrays.get(it);
			if (mCallBack!=null&&mCallBack.getiCallBack().equals(iCallBack)) {
				Logger.log(this, "unregisterSearchListener~", it+"/"+mCallBack);
				searchArrays.remove(it);
			}
		}
	}
	
	//兴趣点检索
	@Override
	public void onGetPoiResult(MKPoiResult mkpoiresult, int type, int iError) {
		Logger.log(this, "onGetPoiResult", mkpoiresult+"/"+iError);
		dispatchSearchResult(mkpoiresult,iError);
	}

	//公交换乘检索
	@Override
	public void onGetTransitRouteResult(
			MKTransitRouteResult mktransitrouteresult, int iError) {
		Logger.log(this, "onGetTransitRouteResult", mktransitrouteresult+"/"+iError);
		dispatchSearchResult(mktransitrouteresult,iError);
	}

	//驾乘导航
	@Override
	public void onGetDrivingRouteResult(
			MKDrivingRouteResult mkdrivingrouteresult, int iError) {
		Logger.log(this, "onGetDrivingRouteResult", mkdrivingrouteresult+"/"+iError);
		dispatchSearchResult(mkdrivingrouteresult,iError);
	}

	//步行检索
	@Override
	public void onGetWalkingRouteResult(
			MKWalkingRouteResult mkwalkingrouteresult, int iError) {
		Logger.log(this, "onGetWalkingRouteResult", mkwalkingrouteresult+"/"+iError);
		dispatchSearchResult(mkwalkingrouteresult,iError);
	}

	//位置检索
	@Override
	public void onGetAddrResult(MKAddrInfo mkaddrinfo, int iError) {
		Logger.log(this, "onGetAddrResult", mkaddrinfo.addressComponents.city+"/"+iError);
		dispatchSearchResult(mkaddrinfo,iError);
	}

	//公交检索
	@Override
	public void onGetBusDetailResult(MKBusLineResult mkbuslineresult, int iError) {
		Logger.log(this, "onGetBusDetailResult", mkbuslineresult+"/"+iError);
		dispatchSearchResult(mkbuslineresult,iError);
	}

	//联想词信息检索
	@Override
	public void onGetSuggestionResult(MKSuggestionResult mksuggestionresult,
			int iError) {
		Logger.log(this, "onGetSuggestionResult", mksuggestionresult+"/"+iError);
		dispatchSearchResult( mksuggestionresult,iError);
	}

	//兴趣点详细检索
	@Override
	public void onGetPoiDetailSearchResult(int type, int iError) {
		Logger.log(this, "onGetPoiDetailSearchResult",type+"/"+iError);
		dispatchSearchResult(type, iError);
	}

	//信息分享
	@Override
	public void onGetShareUrlResult(MKShareUrlResult mkShareUrlResult, int type, int iError) {
		Logger.log(this, "onGetShareUrlResult", type+"/"+iError);
		dispatchSearchResult(mkShareUrlResult, type,iError);
	}

	/**
	 * 分发回调
	 * @param type
	 * @param error
	 */
	private void dispatchSearchResult(int type,int error){
		for (int it=0;it<searchArrays.size();it++) {
			MapCallBack mCallBack=searchArrays.valueAt(it);
			int iCallType=mCallBack.getiCallType();
			IMapCallBack iCallBack=mCallBack.getiCallBack();
			if (error!=0)
				iCallBack.onError(iCallType, error);
			else 
				iCallBack.handleMessage(Messager.getMessage(iCallType,type,error));
		}
	}
	
	/**
	 * 分发回调
	 * @param object
	 * @param error
	 */
	private void dispatchSearchResult(Object object,int error){
		for (int it=0;it<searchArrays.size();it++) {
			MapCallBack mCallBack=searchArrays.valueAt(it);
			int iCallType=mCallBack.getiCallType();
			IMapCallBack iCallBack=mCallBack.getiCallBack();
			if (error!=0)
				iCallBack.onError(iCallType, error);
			else 
				iCallBack.handleMessage(Messager.getMessage(iCallType,error,object));
		}
	}
	
	/**
	 * 分发回调
	 * @param object
	 * @param type
	 * @param error
	 */
	private void dispatchSearchResult(Object object,int type,int error){
		for (int it=0;it<searchArrays.size();it++) {
			MapCallBack mCallBack=searchArrays.valueAt(it);
			int iCallType=mCallBack.getiCallType();
			IMapCallBack iCallBack=mCallBack.getiCallBack();
			if (error!=0)
				iCallBack.onError(iCallType, error);
			else 
				iCallBack.handleMessage(Messager.getMessage(iCallType,error,type,object));
		}
	}
	
}

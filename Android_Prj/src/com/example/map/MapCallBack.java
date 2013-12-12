package com.example.map;

public class MapCallBack {
	private int iCallType;
	private IMapCallBack iCallBack;
	public int getiCallType() {
		return iCallType;
	}
	public void setiCallType(int iCallType) {
		this.iCallType = iCallType;
	}
	public IMapCallBack getiCallBack() {
		return iCallBack;
	}
	public void setiCallBack(IMapCallBack iCallBack) {
		this.iCallBack = iCallBack;
	}
	
	
}

package com.example.gesture;


public interface IGestureCallback {
	public boolean onDown(int pointerCount,int x,int y);
	public boolean onTapConfirmed(int pointerCount,int x,int y);
	public boolean onDoubleTap(int pointerCount,int x,int y);
	public boolean onScroll(int pointerCount,int x,int y);
	public boolean onFling(int pointerCount,int direction);
	public boolean onLongPressed(int pointerCount);
}

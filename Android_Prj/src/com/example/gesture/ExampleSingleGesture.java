package com.example.gesture;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.example.utils.Logger;

public class ExampleSingleGesture extends SimpleOnGestureListener {
	private IGestureCallback callBack;
	private final int FLICK_TIMEOUT = 250;
	public static final int FLICK_UP = 0;
	public static final int FLICK_RIGHT = 1;
	public static final int FLICK_LEFT = 2;
	public static final int FLICK_DOWN = 3;

	public ExampleSingleGesture(IGestureCallback callback) {
		callBack = callback;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Logger.log("singleGesture onDown", e.getX());
		return callBack.onDown(1,(int) e.getX(), (int) e.getY());
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Logger.log("singleGesture onSingleTapConfirmed", "");
		int y = (int) e.getY();
		int x = (int) e.getX();
		return callBack.onTapConfirmed(1,x, y);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Logger.log("singleGesture onDoubleTap", e.getPointerCount());
		return callBack.onDoubleTap(1,(int) e.getX(), (int) e.getY());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Logger.log("singleGesture onScroll", distanceX + "/"
				+ distanceY);
		long duration = e2.getEventTime() - e1.getDownTime();
		if (duration < FLICK_TIMEOUT) {
			return false;
		}
		float y = e2.getY();
		float x = e2.getX();
		return callBack.onScroll(1,(int) x, (int) y);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		long duration = e2.getEventTime() - e1.getDownTime();
		if (duration > FLICK_TIMEOUT) {
			return false;
		}
		float y = e1.getY();
		float x = e1.getX();

		float distanceY = e2.getY() - y;
		float distanceX = e2.getX() - x;

		boolean a = (distanceY > distanceX);
		boolean b = (distanceY > -distanceX);

		int direction = (a ? 2 : 0) | (b ? 1 : 0);
		Logger.log("singleGesture onFling", direction);
		return callBack.onFling(1,direction);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return super.onSingleTapUp(e);
	}
	
}

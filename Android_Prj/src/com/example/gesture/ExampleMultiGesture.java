package com.example.gesture;

import android.annotation.SuppressLint;

import com.example.utils.Logger;

/**
 * @author : pansha
 */
public class ExampleMultiGesture {
	private static final int MIN_VERTICAL_DISTANCE = 100;
	private static final int MIN_HORIZONTAL_DISTANCE = 50;

	public int multiClickCount;
	public int multiClickedFinger;
	public int multiClickedCount;

	private ExampleGesture aGesture;

	@SuppressLint("Recycle")
	public ExampleMultiGesture(ExampleGesture aGesture) {
		this.aGesture = aGesture;
	};

	public void isMultiLongPressed(int pointerCount) {
		if (multiClickedCount != 0) {
		} else {
			if (pointerCount==1) {
				
			}
			else if (pointerCount == 2) {
			} else if (pointerCount == 3) {
				
			}
		}
		Logger.log("MultiGesture  isMultiLongPressed", pointerCount + "/"+ multiClickedCount);
	}

	public void multiFingerClick(int pointerCount) {
		Logger.log("MultiGesture  multiFingerClick", pointerCount + "/"+ multiClickedCount);
		multiClickedCount = multiClickCount;
		multiClickedFinger = pointerCount;
//		AccessibilityUtils.startSpeakMsg(-1,
//				convertChar(pointerCount) + "指"
//						+ convertChar(multiClickedCount) + "击");
		if (pointerCount == 2) {
			
		} else if (pointerCount == 3) {
			if (multiClickCount == 1) {
			} else if (multiClickCount == 2) {

			} else if (multiClickCount == 3) {
			}  
		}
		else if (pointerCount == 4) {
		}
	}

	public boolean twoFingerZooming() {
		Logger.log("MultiGesture ","twoFingerGesture");
		float distanceX = aGesture.upX0 - aGesture.downX0;
		float distanceY = aGesture.upY0 - aGesture.downY0;
		float distanceX1 =aGesture. upX1 - aGesture.downX1;
		float distanceY1 =aGesture. upY1 - aGesture.downY1;
		boolean a = (distanceY > distanceX);
		boolean b = (distanceY > -distanceX);
		boolean a1 = (distanceY1 > distanceX1);
		boolean b1 = (distanceY1 > -distanceX1);

		int direction = (a ? 2 : 0) | (b ? 1 : 0);
		int direction1 = (a1 ? 2 : 0) | (b1 ? 1 : 0);
		if (direction == direction1) {
			return false;
		}
		if (Math.abs(distanceX) < 30 && Math.abs(distanceY) < 30
				&& Math.abs(distanceX1) < 30 && Math.abs(distanceY1) < 30) {
			return false;
		}
		Logger.log("MultiGesture twoFingerZoom", direction + "/"
				+ direction1);
		if (twoFingerZoom(aGesture.upX0, aGesture.upY0,aGesture. upX1, aGesture.upY1, 100) > 0) {
//			AccessibilityUtils.startSpeakMsg(-1, "双指放大");
			return true;
		} else if (twoFingerZoom(aGesture.upX0, aGesture.upY0, aGesture.upX1, aGesture.upY1, 100) < 0) {
//			AccessibilityUtils.startSpeakMsg(-1, "双指缩小");
			return false;
		}
		return false;
	}

	public void threeFingerGesture() {
		Logger.log("MultiGesture ","threeFingerGesture");
		final float distanceX = aGesture.upX0 - aGesture.downX0;
		final float distanceY = aGesture.upY0 - aGesture.downY0;
		final float distanceX1 =aGesture. upX1 - aGesture.downX1;
		final float distanceY1 =aGesture. upY1 - aGesture.downY1;
		final float distanceX2 =aGesture. upX2 - aGesture.downX2;
		final float distanceY2 =aGesture. upY2 - aGesture.downY2;

		boolean a = (distanceY > distanceX);
		boolean b = (distanceY > -distanceX);
		boolean a1 = (distanceY1 > distanceX1);
		boolean b1 = (distanceY1 > -distanceX1);
		boolean a2 = (distanceY2 > distanceX2);
		boolean b2 = (distanceY2 > -distanceX2);
		int direction = (a ? 2 : 0) | (b ? 1 : 0);
		int direction1 = (a1 ? 2 : 0) | (b1 ? 1 : 0);
		int direction2 = (a2 ? 2 : 0) | (b2 ? 1 : 0);

		if ((direction == direction1) && (direction1 == direction2)) {

			if (distanceX > MIN_HORIZONTAL_DISTANCE
					&& distanceX1 > MIN_HORIZONTAL_DISTANCE
					&& distanceX2 > MIN_HORIZONTAL_DISTANCE) {
//				AccessibilityUtils.startSpeakMsg(-1, "三右滑");
			} else if (distanceX < -MIN_HORIZONTAL_DISTANCE
					&& distanceX1 < -MIN_HORIZONTAL_DISTANCE
					&& distanceX2 < -MIN_HORIZONTAL_DISTANCE) {
//				AccessibilityUtils.startSpeakMsg(-1, "三左滑");
			} else if (distanceY > MIN_VERTICAL_DISTANCE
					&& distanceY1 > MIN_VERTICAL_DISTANCE
					&& distanceY2 > MIN_VERTICAL_DISTANCE) {
//				AccessibilityUtils.startSpeakMsg(-1, "三下滑开始全文朗读");
//				AccessibleFullReader.startFullReading(0);
			} else if (distanceY < -MIN_VERTICAL_DISTANCE
					&& distanceY < -MIN_VERTICAL_DISTANCE
					&& distanceY < -MIN_VERTICAL_DISTANCE) {
//				AccessibilityUtils.startSpeakMsg(-1, "三上滑开始焦点朗读");
			}
		} else {
			if (Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20
					&& Math.abs(distanceX1) < 20 && Math.abs(distanceY1) < 20
					&& Math.abs(distanceX2) < 20 && Math.abs(distanceY2) < 20) {
				return;
			}
			if (threeFingerZoom(aGesture.upX0, aGesture.upY0, aGesture.upX1,
					aGesture.upY1, aGesture.upX2, aGesture.upY2, 20) < 0) {
//				AccessibilityUtils.startSpeakMsg(-1, "三指缩小");
			} else if (threeFingerZoom(aGesture.upX0, aGesture.upY0,aGesture. upX1,
					aGesture. upY1, aGesture.upX2, aGesture.upY2, 20) > 0) {
//				AccessibilityUtils.startSpeakMsg(-1, "三指放大");
			}
		}
	}

	/** 两手指缩放手势判断 **/
	// 0:无操作 1：放大 -1：缩小
	private int twoFingerZoom(float x1, float y1, float x2, float y2,
			float distance) {
		// 初始两点距离
		double start = Math.sqrt(Math.pow(Math.abs(aGesture.downX0 - aGesture.downX1), 2)
				+ Math.pow(Math.abs(aGesture.downY0 - aGesture.downY1), 2));
		// 末两点距离
		double end = Math.sqrt(Math.pow(Math.abs(x1 - x2), 2)
				+ Math.pow(Math.abs(y1 - y2), 2));
		if (start + distance < end) {
			return 1;
		}
		if (start - end > distance) {
			return -1;
		}
		return 0;

	}

	/** 三手势缩放判断 **/
	private int threeFingerZoom(float x1, float y1, float x2, float y2,
			float x3, float y3, float distance) {
		Logger.log("MultiGesture threeFingerZoom", distance);
		// 初始三点距离
		double start0 = Math.sqrt(Math.pow(Math.abs(aGesture.downX0 - aGesture.downX1), 2)
				+ Math.pow(Math.abs(aGesture.downY0 - aGesture.downY1), 2));
		double start1 = Math.sqrt(Math.pow(Math.abs(aGesture.downX0 - aGesture.downX2), 2)
				+ Math.pow(Math.abs(aGesture.downY0 - aGesture.downY2), 2));
		double start2 = Math.sqrt(Math.pow(Math.abs(aGesture.downX2 - aGesture.downX1), 2)
				+ Math.pow(Math.abs(aGesture.downY2 -aGesture. downY1), 2));
		// 末三点距离
		double end0 = Math.sqrt(Math.pow(Math.abs(x1 - x2), 2)
				+ Math.pow(Math.abs(y1 - y2), 2));
		double end1 = Math.sqrt(Math.pow(Math.abs(x1 - x3), 2)
				+ Math.pow(Math.abs(y1 - y3), 2));
		double end2 = Math.sqrt(Math.pow(Math.abs(x3 - x2), 2)
				+ Math.pow(Math.abs(y3 - y2), 2));

		if (start0 + distance < end0 && (start1 + distance) < end1
				&& (start2 + distance < end2)) {
			// 两指放大
			return 1;
		}
		if (start0 - end0 > distance && (start1 - end1 > distance)
				&& (start2 - end2 > distance)) {
			return -1;
		}
		return 0;

	}

	public String convertChar(int number) {
		if (number == 1)
			return "单";
		else if (number == 2)
			return "双";
		else if (number == 3)
			return "三";
		else
			return "四";
	}

}

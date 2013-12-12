package com.example.gesture;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.utils.Logger;

public class ExampleGesture {
	private static final long MULTI_CLICK_DELAY = 500;
	private IGestureCallback igCallBack;
	private GestureDetector gestureDetector;
	private ExampleMultiGesture aMultiGesture;
	private ExampleSingleGesture aSingleGesture;
	
	public boolean twoMovingLocked; 
	public boolean multiFingerTouch; 
	public boolean twoFingerZooming; 
	public boolean multiLongPressed; 
	public boolean isMultiClickLocked;
	public boolean singleFingerMoving;
	public boolean singlePressedFlicking;
	
	public float downX, downY,downX0, downY0, 
					   downX1, downY1, downX2, downY2,
					   downX3, downY3;
	private float moveX0, moveY0,moveX1, moveY1,
						moveX2, moveY2;
	public float upX, upY,upX0, upY0, upX1, upY1,
					   upX2, upY2,upX3, upY3;
	
	private int moveDirection;
	private int pointerCount;
	private int currPointerCount;
	private int lastPointerCount;
	public int fingerMovingCount;
	
	private Handler multiClickHandler;
	private ArrayList<String> lTwoMoving;
	
	
	public ExampleGesture(Context context)
	{
		multiClickHandler=new Handler();
		lTwoMoving=new ArrayList<String>();
		aMultiGesture = new ExampleMultiGesture(this);
//		aSingleGesture = new ExampleSingleGesture(this);
		gestureDetector = new GestureDetector(context,aSingleGesture);
		gestureDetector.setIsLongpressEnabled(false);
	}
	
	public boolean dispatchTouchEvent(MotionEvent event) {
		pointerCount=event.getPointerCount();
		if (multiLongPressed) {
			if (event.getAction()==1) {
				resetVariable();
			}
			Logger.log("MultiGesture multiLongPressed", multiLongPressed+"/"+event.getAction());
			return false;
		}
		dispatchMultiGesture(event);
		if (isSingleGesture()) {
			if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
				event.setAction(MotionEvent.ACTION_DOWN);
				gestureDetector.onTouchEvent(event);
				event.setAction(MotionEvent.ACTION_UP);
			}
			gestureDetector.onTouchEvent(event);
		}
		return true;
	}
	
	private boolean isSingleGesture(){
		if (pointerCount == 1
				&&(!multiFingerTouch)
				&&(!twoMovingLocked)
				&&(!singlePressedFlicking)) {
			Logger.log("MultiGestures isSingleGesture",singlePressedFlicking);
			return true;
		}
		return false;
	}
	
	private void dispatchMultiGesture(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				singleFingerDown(event);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				multiFingerDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				multiFingerMoving(event);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				multiFingerUp(event);
				break;
			case MotionEvent.ACTION_UP:
				singleFingerUp(event);
				break;
		}
	}
	
	private void singleFingerDown(MotionEvent event){
		resetVariable();
		currPointerCount=pointerCount;
		lastPointerCount=pointerCount;
		aMultiGesture.multiClickCount++;
		downX=event.getX();downY=event.getY();
		Logger.log("MultiGestures singleFingerDown",aMultiGesture.multiClickCount);
	}
	
	private void multiFingerDown(MotionEvent event){
		Logger.log("MultiGestures multiFingerDown",currPointerCount + "/" + lastPointerCount);
		if (pointerCount!=currPointerCount) {
			fingerMovingCount=0;
		}
		multiFingerTouch=true;
		currPointerCount=pointerCount;
		lastPointerCount=pointerCount;
		downX0 = event.getX(0);
		downY0 = event.getY(0);
		if (pointerCount == 2) {
			downX1 = event.getX(1);
			downY1 = event.getY(1);
		}
		if (pointerCount == 3) {
			downX2 = event.getX(2);
			downY2 = event.getY(2);
		}
		if (pointerCount == 4) {
			downX3 = event.getX(3);
			downY3 = event.getY(3);
		}
	}
	
	private void multiFingerMoving(MotionEvent event){
		Logger.log("MultiGesture multiFingerMoving",fingerMovingCount+"/"+singlePressedFlicking);
		fingerMovingCount++;
		if (!singlePressedFlicking
				&&pointerCount==2) {
			lTwoMoving.add(event.getX()+"/"+event.getY()+"/"+event.getPressure());
		}
		if (!singlePressedFlicking
				&&fingerMovingCount>=100) {
			moveX0 = event.getX(0) - downX;
			moveY0 = event.getY(0) - downY;
			if (pointerCount == 2) {
				moveX1 = event.getX(1) - downX1;
				moveY1 = event.getY(1) - downY1;
			}
			if (pointerCount == 1) {
				if (!singleFingerMoving
						&&Math.abs(moveX0) < 10 && Math.abs(moveY0) < 10) {
					multiLongPressed = true;
				}
			} else if (pointerCount == 2) {
				if (Math.abs(moveX0) < 10 && Math.abs(moveY0) < 10
						&& Math.abs(moveX1) < 10 && Math.abs(moveY1) < 10) {
					multiLongPressed = true;
				}
			} else if (pointerCount == 3) {
				moveX2 = event.getX(2) - downX2;
				moveY2 = event.getY(2) - downY2;
				if (Math.abs(moveX0) < 10 && Math.abs(moveY0) < 10
						&& Math.abs(moveX1) < 10 && Math.abs(moveY1) < 10
						&& Math.abs(moveX2) < 10 && Math.abs(moveY2) < 10) {
					multiLongPressed = true;
				}
			}
		}
		if (multiLongPressed) {
			aMultiGesture.isMultiLongPressed(lastPointerCount);
			resetClickVariable();
		}
	}
	
	private void multiFingerUp(MotionEvent event){
		Logger.log("MultiGesture multiFingerUp",currPointerCount+"/"+lastPointerCount);
		upX0 = event.getX(0);
		upY0 = event.getY(0);
		if (pointerCount == 2) {
			upX1 = event.getX(1);
			upY1 = event.getY(1);
		}
		else if (pointerCount == 3) {
			upX2 = event.getX(2);
			upY2 = event.getY(2);
		}
		else if (pointerCount == 4) {
			upX3 = event.getX(3);
			upY3 = event.getY(3);
		}
		isSingleFlickGesture(event);
		currPointerCount=pointerCount;
	}
	
	private void singleFingerUp(MotionEvent event){
		Logger.log("MultiGesture singleFingerUp",currPointerCount+"/"+lastPointerCount+"/"+fingerMovingCount);
		if (singlePressedFlicking) {
			resetVariable();resetClickVariable();
		}
		else {
			if (lastPointerCount<2) {
				resetVariable();
				resetClickVariable();
				return;
			}
			if (currPointerCount==2
				&&lastPointerCount==2) {
				int direction=isTwoFingerGesture();
				if (direction!=-1) {
					upX=event.getX();upY=event.getY();
					return;
				}
				else if (aMultiGesture.twoFingerZooming()) {
					resetVariable();
					resetClickVariable();
					return;
				}
			}
			if (fingerMovingCount<=8) {
				if (isMultiClickLocked)
					return;
				isMultiClickLocked = true;
				multiClickHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if (fingerMovingCount<=8
							&&lastPointerCount>1
							&&aMultiGesture.multiClickCount!=0) {
							aMultiGesture.multiClickedFinger=lastPointerCount;
							aMultiGesture.multiFingerClick(lastPointerCount);
						}
						resetVariable();
						aMultiGesture.multiClickCount=0;
					}
				}, MULTI_CLICK_DELAY);
			}
			else{
				if(lastPointerCount==3){
					aMultiGesture.threeFingerGesture();
				}
				else if(lastPointerCount==4){
					
				}
				resetVariable();
				resetClickVariable();
			}
		}
	}
	
	private int isTwoFingerGesture(){
		final float distanceX =upX0 - downX;
		final float distanceY = upY0 - downY;
		final float distanceX1 = upX1- downX1;
		final float distanceY1 = upY1 - downY1;
		final boolean a = (distanceY > distanceX);
		final boolean b = (distanceY > -distanceX);
		final boolean a1 = (distanceY1 > distanceX1);
		final boolean b1 = (distanceY1 > -distanceX1);
		final int direction = (a ? 2 : 0) | (b ? 1 : 0);
		final int direction1 = (a1 ? 2 : 0) | (b1 ? 1 : 0);
		Logger.log("MultiGesture twoFingerMoving",+direction+"/"+direction1);
		if (direction==direction1) {
			if (direction==0||direction==3) {
				if (Math.abs(distanceY)>50
						&&Math.abs(distanceY1)>50)
					return direction;
			}
			if (direction==1||direction==2) {
				if (Math.abs(distanceX)>50
						&&Math.abs(distanceX1)>50) 		
					return direction;
			}
			return -1;
		}
		else 
			return -1;
	}
		
	private void isSingleFlickGesture(MotionEvent event){
		Logger.log("MultiGesture isSingleFlickGesture",lastPointerCount+"/"+currPointerCount
				+"/"+fingerMovingCount);
		if (lastPointerCount==2
				&&currPointerCount==2){
			final float distanceX =upX0 - downX;
			final float distanceY =upY0 - downY;
			final float distanceX1 =upX1 - downX1;
			final float distanceY1 =upY1 - downY1;
			if (Math.abs(distanceX)<30
				&&Math.abs(distanceY)<50
				&&Math.abs(distanceY1)>50) {
				final boolean a = (distanceY1 > distanceX1);
				final boolean b = (distanceY1 > -distanceX1);
				final int direction = (a ? 2 : 0) | (b ? 1 : 0);
				if (direction == 0||direction ==3) {
					resetClickVariable();
					singlePressedFlicking=true;
				}
			}
		}
	}
	
	public int getMoveDirection() {
		return moveDirection;
	}

	public void resetVariable(){
		Logger.log("MultiGesture", "resetVariable");
		lTwoMoving.clear();
		fingerMovingCount=0;
		multiFingerTouch=false;
		isMultiClickLocked=false;
		multiLongPressed=false;
		twoFingerZooming=false;
		singleFingerMoving=false;
		singlePressedFlicking=false;
	}
	
	public void resetClickVariable(){
		Logger.log("MultiGesture resetClickVariable", isMultiClickLocked);
		aMultiGesture.multiClickedCount=0;
		aMultiGesture.multiClickedFinger=0;
	}
	
}

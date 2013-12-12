package com.example.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.activity.R;

public class Waiter {
	private static Waiter dialogist;
	private PopupWindow popWindow;
	
	public static Waiter getInstance(){
		if (dialogist==null) {
			dialogist=new Waiter();
		}
		return dialogist;
	}
	@SuppressWarnings("deprecation")
	private void createPopWindow(Context context,String loading,boolean touchable) {
		Logger.log(this,"createPopWindow", loading);
		LayoutInflater inflater = LayoutInflater.from(context);
		View popLayout=inflater.inflate(R.layout.loading_layout, null);
		((TextView) popLayout.findViewById(R.id.tv_loading)).setText(loading);
		popWindow = new PopupWindow(popLayout, 
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 设置动画弹出效果
		popWindow.setTouchable(touchable);
		popWindow.setOutsideTouchable(false);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setAnimationStyle(R.style.popu_animation);
		Logger.log(this,"createPopWindow success", loading);
	}
	
	public void startWaitting(Context context,String loading,boolean touchable){
		Logger.log(this,"startWaitting", loading);
		if (popWindow!=null) {
			stopWatting();
		}
		createPopWindow(context, loading,touchable);
	}
	
	public void stopWatting() {
		if (popWindow!=null) {
			new Runnable() {
				@Override
				public void run() {
					popWindow.dismiss();
					popWindow=null;
				}
			};
		}
	}
}

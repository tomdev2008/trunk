package com.example.norule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.example.utils.Logger;

@SuppressLint("HandlerLeak")
public class NORuleView extends ScrollView {
	private OnScrollListener onScrollListener;
	
	public NORuleView(Context context) {
		super(context);
		Logger.log(this, "NORuleView");
	}

	public NORuleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NORuleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		Logger.log(this, "onScrollChanged");
		onScrollListener.onAutoScroll(l, t, oldl, oldt);
	}

	/**
	 * 定义接口
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

}

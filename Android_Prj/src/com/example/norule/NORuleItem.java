package com.example.norule;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.example.cache.ImageLoader;
import com.example.imp.IHttpCallBack;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class NORuleItem extends ImageView implements OnClickListener,
		OnLongClickListener {

	private String imgUrl;
	private int rowIndex;// 图片属于第几行
	private int itemWidth;
	private int columnIndex;// 图片属于第几列
	private int loaded_count;
	private IHttpCallBack iCallBack;

	public NORuleItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setListener();
	}

	public NORuleItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListener();
	}

	public NORuleItem(Context context) {
		super(context);
		setListener();
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getLoaded_count() {
		return loaded_count;
	}

	public void setLoaded_count(int loaded_count) {
		this.loaded_count = loaded_count;
	}

	public void setiCallBack(IHttpCallBack iCallBack) {
		this.iCallBack = iCallBack;
	}

	private void setListener() {
		setOnClickListener(this);
		setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View v) {
		Logger.log(this, "onLongClick", v.getId());
		return true;
	}

	@Override
	public void onClick(View v) {
		Logger.log(this, "onClick", v.getId());
	}

	/**
	 * 加载图片
	 */
	public void loadImage(ImageLoader loader) {
		loader.loadImage(NORuleItem.this, imgUrl);
		getDrawingCache(true);
		Bitmap bitmap=getDrawingCache();
		getDrawingCache(false);
		if (bitmap != null) {// 此处在线程过多时可能为null
			int width = bitmap.getWidth();// 获取真实宽高
			int height = bitmap.getHeight();
			LayoutParams layoutParams = getLayoutParams();
			int viewHeight = (height * getItemWidth()) / width;// 调整高度
			if (layoutParams == null) {
				layoutParams = new LayoutParams(getItemWidth(), viewHeight);
			}
			setImageBitmap(bitmap);
			setLayoutParams(layoutParams);
			Message msg=Messager.getMessage(1, width, viewHeight, NORuleItem.this);
			iCallBack.handleMessage(msg);
		}
	}

	/**
	 * 重新加载图片
	 */
	public void reloadImage(ImageLoader loader) {
		loader.loadImage(NORuleItem.this, imgUrl);
	}

}

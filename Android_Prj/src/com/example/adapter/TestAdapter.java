package com.example.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;
import com.example.app.ExampleApp;
import com.example.cache.ImageLoader;

public class TestAdapter extends BaseAdapter{
	private String[] imgUrls;
	private boolean loadingBusy;
	private LayoutInflater inflater;
	private ImageLoader loader;
	public TestAdapter(Context context,String[] imgUrls) {
		this.imgUrls=imgUrls;
		this.loader=new ImageLoader(context);
		this.inflater =LayoutInflater.from(ExampleApp.getInstance()) ;
	}
	
	public boolean isLoadingBusy() {
		return loadingBusy;
	}

	public void setLoadingBusy(boolean loadingBusy) {
		this.loadingBusy = loadingBusy;
	}

	@Override
	public int getCount() {
		return imgUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return imgUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(null == convertView){
			convertView = inflater.inflate(R.layout.test_item, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_test);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_test);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textView .setText(position+"test");
//		if (!isLoadingBusy()) {
			loader.loadImage(viewHolder.imageView, imgUrls[position]);
//		}
		return convertView;
	}
	
	private final class ViewHolder{
		TextView textView;
		ImageView imageView;
	}
}

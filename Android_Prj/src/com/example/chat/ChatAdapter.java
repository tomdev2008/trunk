package com.example.chat;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;
import com.example.cache.ImageLoader;
import com.example.entity.UserEntity;

public class ChatAdapter extends BaseAdapter{
	private Context context;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private ArrayList<UserEntity> lUserEntities;
   
    public ChatAdapter(Context context,
    		ArrayList<UserEntity> lUserEntities) {
        this.context = context;
        this.lUserEntities = lUserEntities;
        this.imageLoader=new ImageLoader(context);
        this.layoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return lUserEntities.size();
    }

    public Object getItem(int position) {
        return lUserEntities.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
	
    public View getView(final int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder = null;
	    if (convertView == null)
	    {	
	    	  viewHolder = new ViewHolder();
	    	  convertView = layoutInflater.inflate(R.layout.chat_item, null);
	    	  viewHolder.tv_missed= (TextView) convertView.findViewById(R.id.tv_missed);
	    	  viewHolder.tv_lastDate= (TextView) convertView.findViewById(R.id.tv_lastDate);
			  viewHolder.tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
			  viewHolder.iv_userHead = (ImageView) convertView.findViewById(R.id.iv_userHead);
			  viewHolder.tv_userInfo = (TextView) convertView.findViewById(R.id.tv_userInfo);
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	    UserEntity userEntity = lUserEntities.get(position);
	    viewHolder.tv_userName.setText(userEntity.getUser_name());
	    viewHolder.tv_userInfo.setText(userEntity.getUser_nick());
	    viewHolder.tv_lastDate.setText(userEntity.getLast_date());
	    final int missed=userEntity.getMissed();
	    if (missed==0) {
	    	viewHolder.tv_missed.setVisibility(View.GONE);
		}
	    else {
	    	viewHolder.tv_missed.setVisibility(View.VISIBLE);
	    	viewHolder.tv_missed.setText(missed+"");
		}
	    imageLoader.loadImage(viewHolder.iv_userHead, userEntity.getUser_head());
	    viewHolder.iv_userHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserEntity userEntity=(UserEntity) getItem(position);
				lookUserInfo(userEntity);
			}
		});
	    return convertView;
    }
    
    private void lookUserInfo(UserEntity userEntity){
    	Intent intent=new Intent();
    	intent.putExtra("uentity", userEntity);
		intent.setClass(context, ZoomImgActivity.class);
		context.startActivity(intent);
    }
    
    private class ViewHolder { 
    	private TextView tv_missed;
    	private TextView tv_lastDate;
    	private TextView tv_userInfo;
        private TextView tv_userName;
        private ImageView iv_userHead;
    }
}

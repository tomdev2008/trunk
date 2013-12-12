package com.example.chat;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;
import com.example.entity.UserEntity;

public class ChatAdapter extends BaseAdapter{
	private Context context;
    private LayoutInflater mInflater;
    private ArrayList<UserEntity> lUserEntities;
   
    public ChatAdapter(Context context,
    		ArrayList<UserEntity> lUserEntities) {
        this.context = context;
        this.lUserEntities = lUserEntities;
        mInflater = LayoutInflater.from(this.context);
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
	    	  convertView = mInflater.inflate(R.layout.chat_item, null);
	    	  viewHolder.tv_date= (TextView) convertView.findViewById(R.id.tv_date);
	    	  viewHolder.tv_missed= (TextView) convertView.findViewById(R.id.tv_missed);
			  viewHolder.tv_userName = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.tv_userInfos = (TextView) convertView.findViewById(R.id.tv_userInfos);
			  viewHolder.iv_userHead = (ImageView) convertView.findViewById(R.id.iv_userhead);
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	    UserEntity userEntity = lUserEntities.get(position);
	    viewHolder.tv_userName.setText(userEntity.getUser_name());
	    viewHolder.tv_userInfos.setText(userEntity.getUser_nick());
	    viewHolder.iv_userHead.setBackgroundResource(R.drawable.img_head);
	    return convertView;
    }
    
    private class ViewHolder { 
    	private TextView tv_date;
    	private TextView tv_missed;
        private TextView tv_userName;
        private TextView tv_userInfos;
        private ImageView iv_userHead;
    }
}

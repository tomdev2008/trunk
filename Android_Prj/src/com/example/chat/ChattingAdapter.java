package com.example.chat;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;
import com.example.cache.ImageLoader;
import com.example.entity.UserEntity;
import com.example.utils.Expression;

public class ChattingAdapter extends BaseAdapter {
	private Context context;
	private ViewHolder viewHolder;
	private AudioPalyer audioPalyer;
	private ImageLoader imageLoader;
	private LayoutInflater layoutInflater;
	private ArrayList<ChatEntity> lChatEntities;

	public ChattingAdapter(Context context, UserEntity userEntity,
			ArrayList<ChatEntity> lChatEntities) {
		this.context = context;
		this.lChatEntities = lChatEntities;
		this.imageLoader=new ImageLoader(context);
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	public void setlChatEntities(ArrayList<ChatEntity> lChatEntities) {
		this.lChatEntities = lChatEntities;
	}

	public int getCount() {
		return lChatEntities.size();
	}

	public Object getItem(int position) {
		return lChatEntities.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ChatEntity chatEntity = lChatEntities.get(position);
		if (convertView == null) {
			if (chatEntity.isChat_from()) {
				convertView = layoutInflater.inflate(R.layout.right_chatting, null);
			} else {
				convertView = layoutInflater.inflate(R.layout.left_chatting, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.tv_chat_date = (TextView) convertView
					.findViewById(R.id.tv_chat_date);
			viewHolder.tv_chat_content = (TextView) convertView
					.findViewById(R.id.tv_chat_content);
			viewHolder.iv_chat_img = (ImageView) convertView
					.findViewById(R.id.iv_chat_img);
			viewHolder.iv_chat_uhead = (ImageView) convertView
					.findViewById(R.id.iv_chat_uhead);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final String headIcon=chatEntity.getChat_uhead();
		if (headIcon != null && !headIcon.equals("")) {
			imageLoader.loadImage(viewHolder.iv_chat_uhead,headIcon);
		}
		viewHolder.tv_chat_date.setText(chatEntity.getChat_date());
		final int chat_type=chatEntity.getChat_type();
		if (chat_type == 1||chat_type == 2) {
			viewHolder.iv_chat_img.setVisibility(View.VISIBLE);
			viewHolder.tv_chat_content.setVisibility(View.GONE);
			if (chat_type==2) {
				String imgUrl=chatEntity.getChat_content();
				imageLoader.loadImage(viewHolder.iv_chat_img, imgUrl);
			}
		}else {
			viewHolder.iv_chat_img.setVisibility(View.GONE);
			viewHolder.tv_chat_content.setVisibility(View.VISIBLE);
			SpannableString spannableString = Expression.getExpressionString(
					context, chatEntity.getChat_content());
			viewHolder.tv_chat_content.setText(spannableString);
		}
		// 处理语音消息的单击事件
		viewHolder.iv_chat_img.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ChatEntity chatEntity = lChatEntities.get(position);
				String imgPath = chatEntity.getChat_content();
				if (imgPath != null && !imgPath.equals("")) {
					if (chatEntity.getChat_type()==1) {
						if (chatEntity.isChat_from()) {
							viewHolder.iv_chat_img
									.setImageResource(R.anim.anim_chatfrom_voice);
						} else {
							viewHolder.iv_chat_img
									.setImageResource(R.anim.anim_chatto_voice);
						}
						if (audioPalyer!=null) {
							audioPalyer.setPlaying(false);
							audioPalyer=null;
						}
						final AnimationDrawable aDrawable= (AnimationDrawable) viewHolder.iv_chat_img
								.getBackground();
						// 开启线程进行音频播放
						audioPalyer=new AudioPalyer(aDrawable, imgPath);
						audioPalyer.start();
					}
					else {
						//查看大图片
						String imgUrl=chatEntity.getChat_content();
						imgUrl=imgUrl.replace("thumb", "original");
						enlargeImage(imgUrl);
					}
				}
			}
		});
		return convertView;
	}
	
	private void enlargeImage(String imgUrl){
		Intent intent=new Intent();
		intent.putExtra("imgUrl", imgUrl);
		intent.setClass(context, ZoomImgActivity.class);
		context.startActivity(intent);
	}

	private class ViewHolder {
		private TextView tv_chat_date;
		private TextView tv_chat_content;
		private ImageView iv_chat_img;
		private ImageView iv_chat_uhead;
	}
}

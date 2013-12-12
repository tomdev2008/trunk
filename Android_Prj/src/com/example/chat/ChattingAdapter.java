package com.example.chat;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.MessageQueue.IdleHandler;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;
import com.example.entity.UserEntity;
import com.example.utils.Expression;

public class ChattingAdapter extends BaseAdapter{
	private Context context;
	private UserEntity userEntity;
    private LayoutInflater mInflater;
    private ArrayList<ChatEntity> lChatEntities;
   
    public ChattingAdapter(Context context, UserEntity userEntity,
    		ArrayList<ChatEntity> lChatEntities) {
        this.context = context;
        this.userEntity = userEntity;
        this.lChatEntities = lChatEntities;
        mInflater = LayoutInflater.from(this.context);
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
    
	public int getItemViewType(int position) {
		// 区别两种view的类型，标注两个不同的变量来分别表示各自的类型
		ChatEntity chatEntity = lChatEntities.get(position);
		if (chatEntity.getChat_target() == userEntity.getUser_name()) {
				return 0;
		} else {
				return 1;
		}
	}
    
	public int getViewTypeCount() {
		// 这个方法默认返回1，如果希望listview的item都是一样的就返回1，我们这里有两种风格，返回2
		return 2;
	}
	
    public View getView(final int position, View convertView, ViewGroup parent) {
    	final ChatEntity chatEntity = lChatEntities.get(position);
    	ViewHolder viewHolder = null;
	    if (convertView == null)
	    {	
	    	  if (chatEntity.getChat_target() == userEntity.getUser_name())
			  {	
				  convertView = mInflater.inflate(R.layout.right_chatting, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.left_chatting, null);
			  }
	    	  viewHolder = new ViewHolder();
			  viewHolder.tv_chatDate = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tv_chatUser = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.v_chatVoice = (View) convertView.findViewById(R.id.chat_msg_bg);
			  viewHolder.iv_userHead = (ImageView) convertView.findViewById(R.id.iv_userhead);
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	    viewHolder.tv_chatDate.setText(chatEntity.getChat_date());
	    viewHolder.tv_chatUser.setText(chatEntity.getChat_target());
	    SpannableString spannableString = Expression.getExpressionString(context, chatEntity.getChat_content()); 
	    TextView tvContent = (TextView) viewHolder.v_chatVoice.findViewById(R.id.tv_chatcontent);
	    final ImageView ivPlay = (ImageView) viewHolder.v_chatVoice.findViewById(R.id.iv_play_voice);
	    tvContent.setText(spannableString);
	    if(chatEntity.isChat_type()){
	    	ivPlay.setVisibility(View.VISIBLE);
	    }else{
	    	ivPlay.setVisibility(View.GONE);
	    }
	    //处理语音消息的单击事件
	    viewHolder.v_chatVoice.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("static-access")
			public void onClick(View v) {
				final String path = chatEntity.getChat_path();
				if(null!= path && !"".equals(path)){
					try {
						//根据类型选择左右不同的动画
						final int type = getItemViewType(position);
						if(type == 0){
							ivPlay.setBackgroundResource(R.anim.anim_chatto_voice);
						}else{
							ivPlay.setBackgroundResource(R.anim.anim_chatfrom_voice);
						}
						final AnimationDrawable animation = (AnimationDrawable) ivPlay.getBackground();
						context.getMainLooper().myQueue().addIdleHandler(new IdleHandler() {
							public boolean queueIdle() {
								animation.start();
								return false;
							}
						});
						//此处需开启线程进行音频播放，否则会堵塞UI
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									BufferedInputStream dis = new BufferedInputStream(
											new FileInputStream(path));
									int bufferSize = AudioRecord
											.getMinBufferSize(
													ChatManager.SAMPLE_RATE_IN_HZ,
													ChatManager.CHANNEL_CONFIG,
													ChatManager.AUDIO_FORMAT);
									byte[] buffer = new byte[bufferSize];
									//实例化AudioTrack  
									AudioTrack track = new AudioTrack(
											AudioManager.STREAM_MUSIC,
											ChatManager.SAMPLE_RATE_IN_HZ,
											ChatManager.CHANNEL_CONFIG,
											ChatManager.AUDIO_FORMAT,
											bufferSize, AudioTrack.MODE_STREAM);
									//开始播放  
									track.play();
									//由于AudioTrack播放的是流，所以，我们需要一边播放一边读取  
									int length = 0;
									while ((length = dis.read(buffer)) != -1) {
										//然后将数据写入到AudioTrack中
										track.write(buffer, 0, length);
									}
									track.stop();
									track.release();
									dis.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
//	    if(null!= msg.getChat_picture()){
//	    	viewHolder.ivUserImage.setImageBitmap(msg.getChat_picture());
//	    }
	    return convertView;
    }
    
    @SuppressWarnings("unused")
	private final class RecordTimeTask extends TimerTask{
		private AnimationDrawable animation;
		private ImageView ivPlay;
		private int type;
		public RecordTimeTask(AnimationDrawable animation, ImageView ivPlay, int type) {
			this.animation = animation;
			this.ivPlay = ivPlay;
			this.type = type;
		}
		public void run() {
			animation.stop();
			android.os.Message msg = handle.obtainMessage();
			msg.obj = ivPlay;
			msg.arg1 = this.type;
			handle.sendMessage(msg);
		}
    }
    
    @SuppressLint("HandlerLeak")
	private Handler handle = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ImageView ivPlay = (ImageView) msg.obj;
			int type = msg.arg1;
			if(type == 0){
				ivPlay.setBackgroundResource(R.drawable.chatto_voice_playing);
			}else{
				ivPlay.setBackgroundResource(R.drawable.chatfrom_voice_playing);
			}
		}
    };
    
    private class ViewHolder { 
    	private View v_chatVoice;
        private TextView tv_chatDate;
        private TextView tv_chatUser;
        private ImageView iv_userHead;
    }
}

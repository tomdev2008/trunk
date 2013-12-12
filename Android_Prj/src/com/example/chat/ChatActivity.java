package com.example.chat;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.activity.R;
import com.example.entity.UserEntity;

public class ChatActivity extends BaseActivity implements IChatCallBack,
		OnClickListener, OnItemClickListener {
	private ChatAdapter chatAdapter;
	private ChatManager chatManager;
	private ArrayList<UserEntity> lUserEntities;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		createTitle(R.string.app_name, false,true);
		setRightButton(R.string.login);
		createChatView();
		initVariable();
	}

	private void initVariable() {
		UserEntity userEntity=(UserEntity) 
				getIntent().getSerializableExtra("entity");
		chatManager=ChatManager.getInstance(this);
		chatManager.handleChat(this, 
				ChatManager.getMessage(3,userEntity.getUser_name()));
	}

	private void createChatView() {
		lUserEntities=new ArrayList<UserEntity>();
		ListView lv_chats = (ListView) findViewById(R.id.lv_chats);
		chatAdapter = new ChatAdapter(this,lUserEntities);
		lv_chats.setAdapter(chatAdapter);
		lv_chats.setOnItemClickListener(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		log( "handleMessage", msg.what);
		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		log( "onItemClick", position);
	}

	@Override
	public void onClick(View v) {
		log( "onClick", v);
	}

	@Override
	public void onError(int errorCode) {
		log( "onError", errorCode);
	}
	
}
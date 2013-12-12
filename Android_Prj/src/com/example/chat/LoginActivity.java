package com.example.chat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.activity.R;
import com.example.entity.UserEntity;
import com.example.utils.DataBase;
import com.example.utils.Toaster;

public class LoginActivity extends BaseActivity implements IChatCallBack,
		OnClickListener {
	private EditText et_name;
	private EditText et_pwd;
	private UserEntity userEntity;
	private ChatManager chatManager;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		createTitle(R.string.login,true,true);
		setLeftButton(R.string.click_back);
		setRightButton(R.string.register);
		createLoginView();
		initLoginData();
	}

	private void createLoginView() {
		chatManager=ChatManager.getInstance(this);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		et_name = (EditText) findViewById(R.id.et_name);
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_forget).setOnClickListener(this);
		findViewById(R.id.btn_register).setOnClickListener(this);
	}

	/**
	 * 初始化已注册的用户
	 */
	private void initLoginData() {
		try {
			ArrayList<UserEntity> lUserEntities = DataBase.queryRegistered();
			if (lUserEntities != null && lUserEntities.size() != 0) {
				userEntity = lUserEntities.get(0);
				log( "initLoginData", userEntity.getUser_name());
				et_name.setText(userEntity.getUser_name());
				et_pwd.setText(userEntity.getUser_pwd());
				log( "initLoginData", userEntity.getUser_nick());
//				 startLogin();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// else{
		// userEntity=(UserEntity) intent.getBundleExtra("bundle")
		// .getSerializable("userEntiey");
		// if (userEntity!=null) {
		// log( "initLoginData", userEntity.getUser_name());
		// et_name.setText(userEntity.getUser_name());
		// et_pwd.setText(userEntity.getUser_pwd());
		// startLogin();
		// }
		// }
	}

	/**
	 * 处理登录按钮事件
	 */
	public void startLogin() {
		final String name = et_name.getText().toString().trim();
		final String pwd = et_pwd.getText().toString().trim();
		if (name.equals("") || pwd.equals("")) {
			Toaster.show(R.string.tip_input);
			return;
		}
		userEntity=new UserEntity();
		userEntity.setUser_name(name);
		userEntity.setUser_pwd(pwd);
		startWaitting(getString(R.string.logining), false);
		chatManager.handleChat(this,ChatManager.getUMessage(1, userEntity));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			startLogin();
			break;
		case R.id.btn_register:
			startRegister();
			break;
		case R.id.btn_forget:
			break;
		default:
			break;
		}
	}

	private void startRegister() {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		intent.addFlags(1);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public boolean handleMessage(Message message) {
		stopWatting();
		Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
		intent.putExtra("userEntity", userEntity);
		startActivity(intent);
		return false;
	}
	
	@Override
	public void onError(int errorCode) {
		log( "onError", errorCode);
		stopWatting();
		Toaster.show(R.string.login_failure);
	}
}

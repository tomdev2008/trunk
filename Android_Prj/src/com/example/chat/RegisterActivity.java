package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.activity.R;
import com.example.activity.R.id;
import com.example.activity.R.layout;
import com.example.activity.R.string;
import com.example.entity.UserEntity;

public class RegisterActivity extends BaseActivity implements IChatCallBack,OnClickListener{
	private EditText et_accounts;
	private ChatManager chatManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        createTitle(R.string.register, true, true);
        setLeftButton(R.string.click_back);
        setRightButton(R.string.login);
        createRegisterView();
	}
	
	private void createRegisterView(){
		chatManager=ChatManager.getInstance(this);
		et_accounts = (EditText) findViewById(R.id.et_accounts);
        findViewById(R.id.btn_register).setOnClickListener(this);
	}

	/**
	 * 处理登录按钮事件
	 */
	public void startRegister(){
		log(this, "startRegister");
		final String accounts = et_accounts.getText().toString().trim();
		if(accounts.equals("")){
			toast(R.string.tip_input);
			return;
		}
		UserEntity userEntity=new UserEntity();
		userEntity.setUser_name("13612935693");
		userEntity.setUser_pwd("wells");
		userEntity.setUser_pwd("admins");
		chatManager.handleChat(this,ChatManager.getUMessage(1, userEntity));
	}

	@Override
	public void onError(int errorCode) {
		log("onError", errorCode);
	}
	
	@Override
	public void onClick(View v) {
		log("onClick", v.getId());
		switch (v.getId()) {
		case R.id.btn_register:
			startRegister();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message message) {
		log("handle", message.obj);
		Intent intent = new Intent(RegisterActivity.this,
				LoginActivity.class);
		intent.addFlags(1);
		intent.putExtra("bundle", message.getData());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		return false;
	}
}
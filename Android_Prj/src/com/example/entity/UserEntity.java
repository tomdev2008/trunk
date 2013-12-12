package com.example.entity;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * @author pansha
 *
 */
public class UserEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private String user_nick;		//当前用户标识
	private String user_name;		//用户登录名
	private String user_pwd;		//头像位图对象
	private Bitmap user_head; 	//上传的头像
	
	public UserEntity() {
	}

	public UserEntity(String user_nick, String user_name, String user_pwd,
			Bitmap user_head) {
		super();
		this.user_nick = user_nick;
		this.user_name = user_name;
		this.user_pwd = user_pwd;
		this.user_head = user_head;
	}

	public String getUser_nick() {
		return user_nick;
	}

	public void setUser_nick(String user_nick) {
		this.user_nick = user_nick;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}

	public Bitmap getUser_head() {
		return user_head;
	}

	public void setUser_head(Bitmap user_head) {
		this.user_head = user_head;
	}
	
}

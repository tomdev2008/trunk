package com.example.entity;

import java.io.Serializable;

/**
 * @author pansha
 *
 */
public class UserEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private int missed;					//未读消息数目
	private String user_nick;		//当前用户标识
	private String user_name;		//用户登录名
	private String user_pwd;		//头像位图对象
	private String user_head; 		//上传的头像
	private String last_date;		//最后聊天日期
	public UserEntity() {
	}
	public UserEntity(int missed, String user_nick, String user_name,
			String user_pwd, String user_head, String last_date) {
		super();
		this.missed = missed;
		this.user_nick = user_nick;
		this.user_name = user_name;
		this.user_pwd = user_pwd;
		this.user_head = user_head;
		this.last_date = last_date;
	}
	public int getMissed() {
		return missed;
	}
	public void setMissed(int missed) {
		this.missed = missed;
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
	public String getUser_head() {
		return user_head;
	}
	public void setUser_head(String user_head) {
		this.user_head = user_head;
	}
	public String getLast_date() {
		return last_date;
	}
	public void setLast_date(String last_date) {
		this.last_date = last_date;
	}

}

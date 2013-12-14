package com.example.chat;


/**
 * 消息
 * @author pansha
 * 
 */
public class ChatEntity {
	private int chat_type;
	private String chat_date;
	private String chat_uhead;
	private String chat_uname;
	private String chat_content;
	private boolean chat_from;
	public ChatEntity() {
	}
	public ChatEntity(int chat_type, String chat_date, String chat_uhead,
			String chat_uname, String chat_content, boolean chat_from) {
		super();
		this.chat_type = chat_type;
		this.chat_date = chat_date;
		this.chat_uhead = chat_uhead;
		this.chat_uname = chat_uname;
		this.chat_content = chat_content;
		this.chat_from = chat_from;
	}
	public int getChat_type() {
		return chat_type;
	}
	public void setChat_type(int chat_type) {
		this.chat_type = chat_type;
	}
	public String getChat_date() {
		return chat_date;
	}
	public void setChat_date(String chat_date) {
		this.chat_date = chat_date;
	}
	public String getChat_uhead() {
		return chat_uhead;
	}
	public void setChat_uhead(String chat_uhead) {
		this.chat_uhead = chat_uhead;
	}
	public String getChat_uname() {
		return chat_uname;
	}
	public void setChat_uname(String chat_uname) {
		this.chat_uname = chat_uname;
	}
	public String getChat_content() {
		return chat_content;
	}
	public void setChat_content(String chat_content) {
		this.chat_content = chat_content;
	}
	public boolean isChat_from() {
		return chat_from;
	}
	public void setChat_from(boolean chat_from) {
		this.chat_from = chat_from;
	}
	
}

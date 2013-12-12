package com.example.chat;


/**
 * 消息
 * @author pansha
 * 
 */
public class ChatEntity {
	private String chat_path;
	private String chat_date;
	private String chat_target;
	private String chat_content;
	private boolean chat_type;
	public ChatEntity() {
	}
	public ChatEntity(String chat_path, String chat_date,
			String chat_target, String chat_content, boolean chat_type) {
		super();
		this.chat_type = chat_type;
		this.chat_path = chat_path;
		this.chat_date = chat_date;
		this.chat_target = chat_target;
		this.chat_content = chat_content;
	}
	public String getChat_path() {
		return chat_path;
	}
	public void setChat_path(String chat_path) {
		this.chat_path = chat_path;
	}
	public String getChat_date() {
		return chat_date;
	}
	public void setChat_date(String chat_date) {
		this.chat_date = chat_date;
	}
	public String getChat_target() {
		return chat_target;
	}
	public void setChat_target(String chat_target) {
		this.chat_target = chat_target;
	}
	public String getChat_content() {
		return chat_content;
	}
	public void setChat_content(String chat_content) {
		this.chat_content = chat_content;
	}
	public boolean isChat_type() {
		return chat_type;
	}
	public void setChat_type(boolean chat_type) {
		this.chat_type = chat_type;
	}
	
}

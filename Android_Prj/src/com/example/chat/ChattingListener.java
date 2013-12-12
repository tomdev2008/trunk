package com.example.chat;

import org.jivesoftware.smack.ConnectionListener;

import com.example.utils.Logger;

public class ChattingListener implements ConnectionListener{
	
	private ChatManager chatManager;
	
	public ChattingListener(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

	@Override
	public void connectionClosed() {
		Logger.log(this, "connectionClosed");
		chatManager.setPresence(ChatManager.CHAT_STATE_OFFLINE);
	}

	@Override
	public void connectionClosedOnError(Exception exception) {
		Logger.log(this, "connectionClosedOnError",exception);
		chatManager.setPresence(ChatManager.CHAT_STATE_OFFLINE);
	}

	@Override
	public void reconnectingIn(int i) {
		Logger.log(this, "reconnectingIn",i);
	}

	@Override
	public void reconnectionFailed(Exception exception) {
		Logger.log(this, "reconnectionFailed",exception);
		chatManager.setPresence(ChatManager.CHAT_STATE_OFFLINE);
	}

	@Override
	public void reconnectionSuccessful() {
		Logger.log(this, "reconnectionSuccessful");
		chatManager.setPresence(ChatManager.CHAT_STATE_ONFLINE);
	}
	
}

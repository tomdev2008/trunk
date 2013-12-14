package com.example.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioFormat;
import android.os.Message;

import com.example.activity.R;
import com.example.chat.ChattingHelper.ChatColumns;
import com.example.entity.UserEntity;
import com.example.receiver.NetworkReceiver;
import com.example.utils.DataBase;
import com.example.utils.FileUtils;
import com.example.utils.Logger;
import com.example.utils.Toaster;

public class ChatManager {
	// 录制通道
	@SuppressWarnings("deprecation")
	public final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// 编码格式
	public final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	// 录制频率
	public final static int SAMPLE_RATE_IN_HZ = 8000;
	public static final int CHAT_CONN_ERROR = 0;
	public static final int CHAT_OFFLINE_ERROR = -1;
	public static final int CHAT_NORMAL_ERROR = -2;
	public static final int CHAT_VOICE_ERROR = -3;
	public static final int CHAT_LOGIN_ERROR = -4;
	public static final int CHAT_REGISTER_ERROR = -5;
	public static final int CHAT_CHATTING_ERROR = -6;

	public static final int CHAT_OFFLINE_OK = 1;
	public static final int CHAT_NORMAL_OK = 2;
	public static final int CHAT_VOICE_OK = 3;
	public static final int CHAT_LOGIN_OK = 4;
	public static final int CHAT_REGISTER_OK = 5;

	private static final int CHAT_REQ_LOGIN = 1;
	private static final int CHAT_REQ_REGISTER = 2;
	private static final int CHAT_REQ_MANAGE = 3;

	public static final int CHAT_STATE_OFFLINE = 7;
	public static final int CHAT_STATE_ONFLINE = 8;
	public static final int CHAT_STATE_BUSYING = 9;
	public static final int CHAT_STATE_AWAYED = 10;
	public static final int CHAT_STATE_HIDING = 11;

	private static final int PORT = 5222;
	private static final String SERVER = "192.168.10.105";

	private static IChatCallBack iCallBack;
	private XMPPConnection xmppConn;
	private static ChatManager chatManager;

	public static ChatManager getInstance(IChatCallBack iCallBack) {
		ChatManager.iCallBack = iCallBack;
		if (chatManager == null) {
			chatManager = new ChatManager();
		}
		return chatManager;
	}

	public void handleChat(final IChatCallBack iCallBack, final Message msg) {
		if (!isConnected()) {
			if (!openConnection()) {
				iCallBack.onError(R.string.conn_failure);
				return;
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				int what = msg.what;
				Logger.log(this, "handleMessage", what + "," + msg.obj);
				switch (what) {
				case CHAT_REQ_LOGIN:
					login((UserEntity) msg.obj);
					break;
				case CHAT_REQ_REGISTER:
					register((UserEntity) msg.obj);
					break;
				case CHAT_REQ_MANAGE:
					listRoster((String) msg.obj);
					break;
				default:
					break;
				}
			}
		}).start();
	}

	private boolean openConnection() {
		try {
			Logger.log(this, "openConnection");
			ConnectionConfiguration configuration = new ConnectionConfiguration(
					SERVER, PORT);
			configuration.setSendPresence(false);
			configuration.setVerifyChainEnabled(false);
			configuration.setReconnectionAllowed(true);
			configuration.setSASLAuthenticationEnabled(true);

			configuration
					.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
			configuration.setTruststorePath("/system/etc/security/cacerts.bks");
			configuration.setTruststorePassword("changeit");
			configuration.setTruststoreType("bks");
			xmppConn = new XMPPConnection(configuration);
			xmppConn.connect();
			xmppConn.addConnectionListener(new ChattingListener(this));
			return true;
		} catch (XMPPException xe) {
			xe.printStackTrace();
			return false;
		}
	}

	// 是否连接到服务
	private boolean isConnected() {
		if (NetworkReceiver.isAvailable) {
			if (xmppConn != null && xmppConn.isConnected()) {
				Logger.log(this, "isConnection", true);
				return true;
			}
			return false;
		}
		Logger.log(this, "isConnection", false);
		return false;
	}

	/**
	 * 注册用户 RegistrationSuccess = 0, 申请帐户，账户删除，密码修改成功 没有提供所有必须的信息 409: 用户名已经存在
	 * 
	 * @param userEntity
	 */
	private void register(UserEntity userEntity) {
		Registration registration = new Registration();
		registration.setType(IQ.Type.SET);
		registration.setTo(xmppConn.getServiceName());
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("username", userEntity.getUser_name());
		attributes.put("password", userEntity.getUser_pwd());
		attributes.put("usernick", userEntity.getUser_nick());
		registration.setAttributes(attributes);
		PacketCollector collector = xmppConn
				.createPacketCollector(new PacketIDFilter(registration
						.getPacketID()));
		xmppConn.sendPacket(registration);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());
		collector.cancel();// 停止请求results
		if (result == null) {
			Toaster.show(R.string.no_response);
			iCallBack.onError(CHAT_REGISTER_ERROR);
		} else if (result.getType() == IQ.Type.RESULT) {
			Toaster.show(R.string.register_success);
			userEntity.setUser_head("default");
			DataBase.insertUser(userEntity);
			iCallBack.handleMessage(getUMessage(CHAT_REGISTER_OK, userEntity));
		} else {
			Logger.log(this, "register", "error: "
					+ result.getError().toString());
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Toaster.show(R.string.response_exist);
			} else {
				Toaster.show(R.string.register_failure);
			}
			iCallBack.onError(CHAT_REGISTER_ERROR);
		}
	}

	/**
	 * 用户登陆
	 * 
	 * @param userEntity
	 */
	private void login(UserEntity userEntity) {
		try {
			xmppConn.login(userEntity.getUser_name(), userEntity.getUser_pwd());
			setPresence(CHAT_STATE_ONFLINE);
			iCallBack.handleMessage(getUMessage(CHAT_LOGIN_OK, null));
		} catch (XMPPException e) {
			e.printStackTrace();
			Logger.log("login error", e);
			iCallBack.onError(CHAT_LOGIN_ERROR);
		}
	}

	/**
	 * 更改用户状态
	 * 
	 * @param code
	 */
	public void setPresence(int code) {
		Presence presence;
		switch (code) {
		case CHAT_STATE_ONFLINE:
			presence = new Presence(Presence.Type.available);
			xmppConn.sendPacket(presence);
			Logger.log(this, "setPresence", "在线，" + presence.toXML());
			break;
		// case 1:
		// presence = new Presence(Presence.Type.available);
		// presence.setMode(Presence.Mode.chat);
		// xmppConn.sendPacket(presence);
		// Logger.log(this, "setPresence", "Q我吧，"+presence.toXML());
		// break;
		case CHAT_STATE_BUSYING:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			xmppConn.sendPacket(presence);
			Logger.log(this, "setPresence", "忙碌");
			break;
		case CHAT_STATE_AWAYED:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			xmppConn.sendPacket(presence);
			Logger.log(this, "setPresence", "离开," + presence.toXML());
			break;
		case CHAT_STATE_HIDING:
			Roster roster = xmppConn.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(xmppConn.getUser());
				presence.setTo(entry.getUser());
				xmppConn.sendPacket(presence);
				System.out.println(presence.toXML());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(xmppConn.getUser());
			presence.setTo(StringUtils.parseBareAddress(xmppConn.getUser()));
			xmppConn.sendPacket(presence);
			Logger.log(this, "setPresence", "隐身");
			break;
		case CHAT_STATE_OFFLINE:
			presence = new Presence(Presence.Type.unavailable);
			xmppConn.sendPacket(presence);
			Logger.log(this, "setPresence", "离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 0 for offline, 1 for online, 2 for away,3 for busy
	 * 
	 * @param user
	 * @return
	 */
	public String getPresence(String user) {
		Roster roster = xmppConn.getRoster();
		Presence availability = roster.getPresence(user);
		Mode userMode = availability.getMode();
		if (userMode == Mode.dnd) {
			return "忙碌";
		} else if (userMode == Mode.away || userMode == Mode.xa) {
			return "离开";
		} else if (availability.isAvailable()) {
			return "在线";
		}
		return "离线";
	}

	/**
	 * 获取所有聊天好友
	 * 
	 * @param userName
	 */
	@SuppressWarnings("unused")
	private void listRoster(final String userName) {
		Logger.log(this, "getChatTargets", userName);
		Roster roster = xmppConn.getRoster();
		Collection<RosterGroup> entriesGroup = roster.getGroups();
		for (RosterGroup group : entriesGroup) {
			Collection<RosterEntry> entries = group.getEntries();
			for (RosterEntry entry : entries) {
				Presence presence = roster.getPresence(entry.getUser());
				Logger.log(this, "user: " + entry.getUser());
				Logger.log(this, "tyep: " + entry.getType());
				Logger.log(this, "status: " + entry.getStatus());
				Logger.log(this, "groups: " + entry.getGroups());
			}
		}
	}

	/**
	 * 语音聊天
	 * 
	 * @param user
	 * @param file
	 * @param time
	 */
	public void chattingVoice(final IChatCallBack iCallBack, final String user,
			final File file, final long time) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String param = user + xmppConn.getServiceName() + "/spark";
				FileTransferManager manager = new FileTransferManager(xmppConn);
				OutgoingFileTransfer transfer = manager
						.createOutgoingFileTransfer(param);
				long timeOut = 1000000;
				long sleepMin = 3000;
				long spTime = 0;
				int rs = 0;

				try {
					transfer.sendFile(file, String.valueOf(time));
					rs = transfer.getStatus().compareTo(
							FileTransfer.Status.complete);
					while (rs != 0) {
						rs = transfer.getStatus().compareTo(
								FileTransfer.Status.complete);
						spTime = spTime + sleepMin;
						if (spTime > timeOut) {
							return;
						}
						Thread.sleep(sleepMin);
					}
				} catch (XMPPException e) {
					e.printStackTrace();
					iCallBack.onError(CHAT_CHATTING_ERROR);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 发送文件
	 * 
	 * @param user
	 * @param file
	 * @throws Exception
	 */
	private void chattingFile(String user, File file) throws Exception {
		user = user + xmppConn.getServiceName() + "/spark";
		FileTransferManager manager = new FileTransferManager(xmppConn);
		OutgoingFileTransfer transfer = manager
				.createOutgoingFileTransfer(user);
		long timeOut = 1000000;
		long sleepMin = 3000;
		long spTime = 0;
		int rs = 0;

		transfer.sendFile(file, "send file");
		rs = transfer.getStatus().compareTo(FileTransfer.Status.complete);
		while (rs != 0) {
			rs = transfer.getStatus().compareTo(FileTransfer.Status.complete);
			spTime = spTime + sleepMin;
			if (spTime > timeOut) {
				return;
			}
			Thread.sleep(sleepMin);
		}
	}

	/**
	 * 发送文本
	 * 
	 * @param user
	 * @param word
	 */
	public void chattingText(final String user, final String word) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String param = user + xmppConn.getServiceName() + "/spark";
				Chat chat = xmppConn.getChatManager().createChat(param,
						new MessageListener() {
							@Override
							public void processMessage(Chat chat,
									org.jivesoftware.smack.packet.Message msg) {
								Logger.log(this, "chattingText processMessage",
										chat + "," + msg);
								// iCallBack.handleMessage(msg);
							}
						});
				try {
					chat.sendMessage(word);
				} catch (XMPPException e) {
					e.printStackTrace();
					iCallBack.onError(CHAT_CHATTING_ERROR);
				}
			}
		}).start();

	}

	/**
	 * 保存录音文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveRecordFile(File file) throws IOException {
		if (FileUtils.storageState()) {
			FileInputStream fIStream = new FileInputStream(file);
			FileOutputStream fOStream = new FileOutputStream(file);
			int len = 0;
			byte[] bytes = new byte[2048];
			while ((len = fIStream.read(bytes)) != -1) {
				fOStream.write(bytes, 0, len);
			}
			fOStream.close();
			fIStream.close();
		}
	}

	/**
	 * 解析json字符串
	 * 
	 * @param json
	 * @return
	 */
	public static ChatEntity jsonToObject(String json) {
		try {
			JSONArray arrays = new JSONArray(json);
			JSONObject jsonObject = arrays.getJSONObject(0);
			String chat_uname = jsonObject.getString(ChatColumns.CHAT_TARGET); // 发送者
			String chat_content = jsonObject
					.getString(ChatColumns.CHAT_CONTENT); // 发送内容
			String chat_date = jsonObject.getString(ChatColumns.CHAT_DATE); // 发送时间
			ChatEntity chatEntity = new ChatEntity();
			chatEntity.setChat_content(chat_content);
			chatEntity.setChat_uname(chat_uname);
			chatEntity.setChat_date(chat_date);
			return chatEntity;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Message getUMessage(int what, UserEntity entity) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = entity;
		return message;
	}

	public static Message getCMessage(int what, ChatEntity entity) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = entity;
		return message;
	}

	public static Message getMessage(int what, Object obj) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = obj;
		return message;
	}

	private void closeConnection() {
		if (xmppConn != null) {
			if (xmppConn.isConnected()) {
				xmppConn.disconnect();
			}
			xmppConn = null;
		}
	}

}

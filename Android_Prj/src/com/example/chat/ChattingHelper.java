package com.example.chat;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.utils.Logger;

public class ChattingHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 3;
	private static final String DB_NAME = "chat.db";
	private static final String TABLE_NAME = "chatting";
	private String[] columns={ChatColumns.CHAT_TARGET,ChatColumns.CHAT_CONTENT ,
			ChatColumns.CHAT_DATE };
	private SQLiteDatabase database;
	public ChattingHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		Logger.log(this, "ChattingHelper", context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create_table = "create table " + TABLE_NAME + " ("
				+ ChatColumns._ID + " integer primary key,"
				+ ChatColumns.CHAT_TARGET + " text not null,"
				+ ChatColumns.CHAT_CONTENT + " text not null,"
				+ ChatColumns.CHAT_DATE + " text not null)";
		db.execSQL(create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public static final class ChatColumns implements BaseColumns {
		public static final String ID = "_id";
		public static final String CHAT_DATE = "chat_date";
		public static final String CHAT_TARGET = "chat_target";
		public static final String CHAT_CONTENT = "chat_content";
	}

	/**
	 * 添加聊天记录
	 * 
	 * @throws FileNotFoundException
	 */
	public long insertChatting(ChatEntity chatEntity) throws FileNotFoundException {
		database = this.getWritableDatabase();
		Logger.log(this, "insertChatting",database+","+chatEntity);
		ContentValues values = new ContentValues();
		values.put(ChatColumns.CHAT_DATE, chatEntity.getChat_date());
		values.put(ChatColumns.CHAT_TARGET, chatEntity.getChat_target());
		values.put(ChatColumns.CHAT_CONTENT, chatEntity.getChat_content());
		database.beginTransaction();
		long rowId = 0;
		try {
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		return rowId;
	}

	/**
	 * 清除用户聊天记录
	 */
	public int deleteChattings(int user_id) {
		database = this.getWritableDatabase();
		String where = null;
		where = ChatColumns._ID + "=" + user_id;
		int rows = database.delete(TABLE_NAME, where, null);
		return rows;
	}

	/**
	 * 查询聊天信息
	 */
	public ArrayList<ChatEntity> queryChattings() {
		database = this.getWritableDatabase();
		Cursor cursor = database.query(TABLE_NAME, columns, null,
				null, null, null, " DESC");
		ArrayList<ChatEntity> lChatEntities= new ArrayList<ChatEntity>();
		while (cursor.moveToNext()) {
			ChatEntity chatEntity = new ChatEntity();
			lChatEntities.add(chatEntity);
		}
		cursor.close();
		return lChatEntities;
	}
}

package com.example.down;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.utils.DataBase;
import com.example.utils.Logger;

public class DownloadHelper extends SQLiteOpenHelper {

	public DownloadHelper(Context context) {
		super(context, DataBase.DB_NAME, null, DataBase.DB_VERSION);
		Logger.log(this, "DownloadHelper");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String CREATE_TABLE = "create table downer("
				+ "_id integer primary key," + "_down_url text ,"
				+ "_thread_id integer," + "_down_completed integer);";
		database.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXIST DOWNER");
		onCreate(database);
	}

	public synchronized CompletedEntity getCompleted(String threadId,
			String downUrl) {
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database
				.rawQuery(
						"select _down_completed from downer where _thread_id=? and _down_url=?",
						new String[] { threadId, downUrl });
		CompletedEntity completedEntity = null;
		while (cursor.moveToNext()) {
			completedEntity = new CompletedEntity();
			completedEntity.setCompleted(cursor.getInt(cursor
					.getColumnIndex("_down_completed")));
		}
		cursor.close();
		database.close();
		return completedEntity;
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public synchronized void setCompleted(int threadId, int completed,
			String downUrl) {
		SQLiteDatabase database = this.getWritableDatabase();
		database.beginTransaction();
		try {
			database.execSQL(
					"insert into downer(_thread_id, _down_completed, _down_url) values(?,?,?)",
					new Object[] { threadId, completed, downUrl });
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		database.close();
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public synchronized void updateCompleted(int threadId, int completed,
			String downUrl) {
		SQLiteDatabase database = this.getWritableDatabase();
		database.beginTransaction();
		try {
			database.execSQL(
					"update downer set _down_completed=? where  _thread_id=? and _down_url=?",
					new Object[] { completed, threadId, downUrl });
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		database.close();
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * 
	 */
	public synchronized void deleteCompleted(int threadId, String downUrl) {
		SQLiteDatabase database = this.getWritableDatabase();
		database.execSQL(
				"delete from downer where _thread_id=? and _down_url=?",
				new Object[] { threadId, downUrl });
		database.close();
	}

}

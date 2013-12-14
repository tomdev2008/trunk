package com.example.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.app.ExampleApp;
import com.example.down.CompletedEntity;
import com.example.entity.UserEntity;

public class DataBase {
	public static final int DB_ITEM = 1;
	public static final int DB_ITEMS =2;
	public static final int DB_VERSION = 3;

	public static final String ID="_id";
	public static final String U_NAME = "user";
	public static final String D_NAME = "downer";
	public static final String DB_NAME= "test.db";
	public static final String AUTHORITY="com.example.provider";
	
	private static String[] USER_COLUMNS={
												    UserColumns.USER_NICK,
													UserColumns.USER_NAME,
													UserColumns.USER_HEAD ,
													UserColumns.USER_PWD };
	
	public static final Uri USER_URI=Uri.parse("content://"+AUTHORITY+"/"+U_NAME);
	public static final Uri DOWN_URI=Uri.parse("content://"+AUTHORITY+"/"+D_NAME);
	
	private final static ContentResolver resolver=ExampleApp.getInstance().getContentResolver();
	
	public static void installRawDBase(Context context) {
		Logger.log("installRawDBase", context);
//		SQLiteDatabase sqLiteDatabase = DownloadHelper.getReadableDatabase();
//		DBInstallTask installTask = new DBInstallTask(context);
//		installTask.execute(sqLiteDatabase.getPath());
//		if(sqLiteDatabase!=null){
//			sqLiteDatabase.close();
//			sqLiteDatabase=null;
//		}
	}
	
	public static void setDownloaded(int threadId,int completed, String downUrl) {
		ContentResolver resolver=ExampleApp.getInstance().
				getContentResolver();
		ContentValues contentValues = new ContentValues();
		contentValues.put("_down_url",downUrl);
		contentValues.put("_thread_id", threadId);
		contentValues.put("_down_completed", completed);
		long rowid =ContentUris.parseId(resolver.insert(DOWN_URI, contentValues));
		Logger.log("setDownloaded", rowid);
		contentValues.clear();
	}
	
	public static CompletedEntity getDownloaded(int threadId,String downUrl) {
		ContentResolver resolver=ExampleApp.getInstance().getContentResolver();
		String selection="_thread_id=? and _down_url=?";
		String[] selectionArgs=new String[] { downUrl};
		String[] projection=new String[]{"_down_completed"};
		Cursor cursor=resolver.query(DOWN_URI, projection, selection, selectionArgs, null);
		CompletedEntity completedEntity = null;
		while (cursor.moveToNext()) {
			completedEntity=new CompletedEntity();
			completedEntity.setCompleted(cursor.getInt(cursor.getColumnIndex("_down_completed")));
		}
		closeCursor(cursor);
		return completedEntity;
	}
	
	public static boolean hasDownloaded(String downUrl) {
		ContentResolver resolver=ExampleApp.getInstance().getContentResolver();
		String selection="_down_url=?";
		String[] selectionArgs=new String[] { downUrl};
		String[] projection=new String[]{"_down_completed"};
		Cursor cursor=resolver.query(DOWN_URI, projection, selection, selectionArgs, null);
		if (cursor.moveToFirst()) {
			return true;
		}
		closeCursor(cursor);
		return false;
	}
	
	public static void updateDownload( int threadId,int completed, String downUrl) {
		ContentResolver resolver=ExampleApp.getInstance().
				getContentResolver();
		String where="_thread_id=? and _down_url=?";
		String[] whereArgs={String.valueOf(threadId),downUrl};
		ContentValues contentValues = new ContentValues();
		contentValues.put("_down_completed", completed);
		long rowid =resolver.update(DOWN_URI, contentValues, where, whereArgs);
		Logger.log("updateDownloaded", rowid);
		contentValues.clear();
	}
	
	public static void deleteDownloaded(String downUrl) {
		ContentResolver resolver=ExampleApp.getInstance().
				getContentResolver();
		String where="_down_url=?";
		String[] whereArgs=new String[] {downUrl};
		int rowid=resolver.delete(DOWN_URI, where, whereArgs);
		Logger.log("deleteDowned", rowid);
	}

	public static final class UserColumns implements BaseColumns {
		public static final String USER_NICK = "user_nick";
		public static final String USER_NAME = "user_name";
		public static final String USER_HEAD = "user_head";
		public static final String USER_PWD = "user_pwd";
	}

	/**
	 * 查询已注册的用户
	 * @throws UnsupportedEncodingException 
	 */
	public static ArrayList<UserEntity> queryRegistered() throws UnsupportedEncodingException {
		Logger.log("DBaseUtils", "queryRegistered");
		ArrayList<UserEntity> lUserEntities=new ArrayList<UserEntity>();
		Cursor cursor = resolver.query(USER_URI, USER_COLUMNS, null, null, null);
		if (cursor.moveToFirst()) {
			UserEntity user = new UserEntity();
			String head = cursor.getString(cursor
					.getColumnIndex(UserColumns.USER_HEAD));
			String name = cursor.getString(cursor
					.getColumnIndex(UserColumns.USER_NAME));
			String pwd = cursor.getString(cursor
					.getColumnIndex(UserColumns.USER_PWD));
			String nick = cursor.getString(cursor
					.getColumnIndex(UserColumns.USER_NICK));
			
//			user.setUser_head(Imager.BytesToBimap(head.getBytes("utf-8")));
			user.setUser_name(name);
			user.setUser_pwd(pwd);
			user.setUser_nick(nick);
			lUserEntities.add(user);
		}
		closeCursor(cursor);
		return lUserEntities;
	}

	/**
	 * 保存注册用户
	 * 
	 */
	public static long insertUser(UserEntity user) {
		Logger.log("insertUser",user);
		ContentValues values = new ContentValues();
		values.put(UserColumns.USER_NICK, user.getUser_nick());
		values.put(UserColumns.USER_NAME, user.getUser_name());
//		values.put(UserColumns.USER_HEAD, Imager.BitmapToBytes(
//				user.getUser_head()).toString());
		values.put(UserColumns.USER_PWD, user.getUser_pwd());
		long rowId=ContentUris.parseId(resolver.insert(USER_URI, values));
		return rowId;
	}

	/**
	 * 清除用户
	 */
	public static int deleteUser(String userName) {
		Logger.log("deleteUser",userName);
		String where = UserColumns.USER_NAME+"=?";
		String[] whereArgs=new String[]{userName};
		int rows = resolver.delete(USER_URI, where, whereArgs);
		return rows;
	}

	/**
	 * 更新用户密码
	 */
	public static int updatePwd(String userName,String userPwd) {
		Logger.log("updatePwd",userName+","+userPwd);
		String where = UserColumns.USER_NAME+"=?";
		String[] whereArgs=new String[]{userName};
		ContentValues values = new ContentValues();
		values.put(UserColumns.USER_HEAD, userPwd);
		return resolver.update(USER_URI, values,where , whereArgs);
	}

	/**
	 * 更新用户头像
	 */
	public static int updateHead(String userName,String userHead) {
		Logger.log("updateHead",userName+","+userHead);
		String where = UserColumns.USER_NAME+"=?";
		String[] whereArgs=new String[]{userName};
		ContentValues values = new ContentValues();
		values.put(UserColumns.USER_HEAD, userHead);
		return resolver.update(USER_URI, values,where , whereArgs);
	}
	
	public static void closeCursor(Cursor cursor){
		if(cursor != null)
		{
			cursor.close();
			cursor=null;
		}
	}
}

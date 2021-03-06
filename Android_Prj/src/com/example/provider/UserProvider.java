package com.example.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.example.helper.ExampleHelper;
import com.example.utils.DataBase;
import com.example.utils.Logger;

public class UserProvider extends ContentProvider{
	private ExampleHelper userHelper;
	private SQLiteDatabase readDatabase;
	private SQLiteDatabase writeDatabase;
	
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(DataBase.AUTHORITY, DataBase.U_NAME,
				DataBase.DB_ITEMS);
		uriMatcher.addURI(DataBase.AUTHORITY, DataBase.U_NAME + "/#",
				DataBase.DB_ITEM);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public boolean onCreate() {
		if (userHelper==null) {
			userHelper = new ExampleHelper(getContext());
		}
		Logger.log(this, "onCreate", userHelper);
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Logger.log(this, "insert", uri+","+values.toString());
		writeDatabase = userHelper.getWritableDatabase();
		long rowId = writeDatabase.insert(DataBase.U_NAME, DataBase.ID,
				values);
		if (rowId > 0) {
			Uri uris = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(uris, null);
			closeWriteDatabase();
			return uris;
		}
		throw new IllegalArgumentException("Unknown URI------>" + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Logger.log(this, "query", uri + "/" + projection + "/" + selection
				+ "/" + selectionArgs);
		readDatabase = userHelper.getReadableDatabase();
		Cursor cursor;
		switch (uriMatcher.match(uri)) {
		case DataBase.DB_ITEMS:
			cursor = readDatabase.query(DataBase.U_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		case DataBase.DB_ITEM:
			String id = uri.getPathSegments().get(1);
			cursor = readDatabase.query(DataBase.U_NAME, projection,
					DataBase.ID+ "="+ id
							+ (!TextUtils.isEmpty(selection) ? "AND("
									+ selection + ')' : ""), selectionArgs,
					null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI------>" + uri);
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Logger.log(this, "update", uri + "/" + values + "/" + selection + "/"
				+ selectionArgs);
		writeDatabase = userHelper.getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case DataBase.DB_ITEMS:
			count = writeDatabase.update(DataBase.U_NAME, values, selection,
					selectionArgs);
			break;
		case DataBase.DB_ITEM:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = writeDatabase.update(DataBase.U_NAME, values, where,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI------>"
					+ uri.toString());
		}
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Logger.log(this, "delete", uri + "/" + selection + "/" + selectionArgs);
		writeDatabase = userHelper.getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case DataBase.DB_ITEMS:
			count = writeDatabase.delete(DataBase.U_NAME, selection,
					selectionArgs);
			break;
		case DataBase.DB_ITEM:
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			count = writeDatabase.delete(DataBase.U_NAME, where,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI------>"
					+ uri.toString());
		}
		closeWriteDatabase();
		return count;
	}

	private void closeReadDatabase() {
		if (readDatabase != null) {
			readDatabase.close();
			readDatabase = null;
		}
	}

	private void closeWriteDatabase() {
		if (writeDatabase != null) {
			writeDatabase.close();
			writeDatabase = null;
		}
	}
}

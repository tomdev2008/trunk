package com.example.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.utils.DataBase;
import com.example.utils.Logger;

public class ExampleHelper extends SQLiteOpenHelper {
	public ExampleHelper(Context context) {
		super(context, DataBase.DB_NAME, null, DataBase.DB_VERSION);
		Logger.log(this, "ExampleHelper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.log(this, "onCreate",db);
		String user_table = "create table user ("
										+ " _id  integer primary key,"
										+ "user_name text not null,"
										+ "user_nick  text not null,"
										+ "user_head  text not null," 
										+ "user_pwd text not null)";
		db.execSQL(user_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.log(this, "onUpgrade",db);
		db.execSQL("DROP TABLE IF EXISTS USER");
		onCreate(db);
	}
}

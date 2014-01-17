/**
 * Copyright (C)  2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * ChinesePod-Decks' application class
 */
package com.chinesepod.decks;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.chinesepod.decks.utility.db.CPDecksDBHelper;

public class CPDecksApplication extends Application {
	private static CPDecksApplication instance;
	
    public static CPDecksDBHelper dbHelper;
	private static SQLiteDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        if (dbHelper == null) {
            dbHelper = new CPDecksDBHelper(this);
        }
        dbHelper.getWritableDatabase();
    }
    
    public synchronized static SQLiteDatabase getDatabase() {
    	if( database == null ){
    		database = dbHelper.getWritableDatabase();
    	}
    	return database;
    }
    
	public CPDecksApplication() {
		instance = this;
	}
	
	public static CPDecksApplication getInstance(){
		return instance;
	}
	
	public static Context getContext() {
		return instance;
	}

    static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("Decks", Context.MODE_PRIVATE);
    }
}

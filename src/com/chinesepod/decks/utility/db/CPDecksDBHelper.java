package com.chinesepod.decks.utility.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chinesepod.decks.CPDecksApplication;

public class CPDecksDBHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "chinesepod4";
	private final static int DATABASE_VERSION = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CPDecksApiCacheDBAdapter.API_CACHE_TABLE_CREATE);
		db.execSQL(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_CREATE);
		db.execSQL(CPDecksDecksDBAdapter.DECKS_TABLE_CREATE);
		db.execSQL(CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * Common functions
	 * */
	public CPDecksDBHelper(Context context) throws SQLException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static void clearAllData() {
		SQLiteDatabase database = CPDecksApplication.getDatabase();
		database.beginTransaction();
		try {
			database.execSQL(CPDecksVocabularyToDecksDBAdapter.DELETE_DATA_FROM_VOCABULARY_TO_DECKS_TABLE);
			database.execSQL(CPDecksVocabularyDBAdapter.DELETE_DATA_FROM_VOCABULARY_TABLE);
			database.execSQL(CPDecksDecksDBAdapter.DELETE_DATA_FROM_DECKS_TABLE);
			database.execSQL(CPDecksApiCacheDBAdapter.DELETE_DATA_FROM_API_CACHE_TABLE);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public static void clearVocabularyData() {
		SQLiteDatabase database = CPDecksApplication.getDatabase();
		database.beginTransaction();
		try {
			database.execSQL(CPDecksVocabularyToDecksDBAdapter.DELETE_DATA_FROM_VOCABULARY_TO_DECKS_TABLE);
			database.execSQL(CPDecksVocabularyDBAdapter.DELETE_DATA_FROM_VOCABULARY_TABLE);

			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			database.close();
		}

	}

	public static void clearDecksData() {
		CPDecksDBHelper dbHelper = new CPDecksDBHelper(CPDecksApplication.getInstance().getBaseContext());
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			database.execSQL(CPDecksVocabularyToDecksDBAdapter.DELETE_DATA_FROM_VOCABULARY_TO_DECKS_TABLE);
			database.execSQL(CPDecksDecksDBAdapter.DELETE_DATA_FROM_DECKS_TABLE);

			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			database.close();
		}

	}

	public static void clearCachedApiData() {
		CPDecksDBHelper dbHelper = new CPDecksDBHelper(CPDecksApplication.getContext());
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			database.execSQL(CPDecksApiCacheDBAdapter.DELETE_DATA_FROM_API_CACHE_TABLE);

			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			database.close();
		}

	}
}

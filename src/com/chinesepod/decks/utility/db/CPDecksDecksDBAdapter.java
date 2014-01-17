package com.chinesepod.decks.utility.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.logic.CPDecksDeck;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class CPDecksDecksDBAdapter {

	public static final String TABLE_NAME = "decks";
	public static final String DECKS_TABLE_ID = "_id";
	public static final String DECKS_TABLE_TYPE = "type";
	public static final String DECKS_TABLE_NAME = "name";
	public static final String DECKS_TABLE_WEIGHT = "weight";
	public static final String DECKS_TABLE_EXTRA_DATA = "extra_data";
	public static final String DECKS_TABLE_CREATED_AT = "created_at";
	public static final String DECKS_TABLE_UPDATED_AT = "updated_at";

	public static final int DECK_TYPE_CUSTOM = 1;
	public static final int DECK_TYPE_PREDEFINED = 2;
	public static final int DECK_TYPE_CPOD = 3;

	protected static final String DECKS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ('"+DECKS_TABLE_ID+"' INTEGER PRIMARY KEY NOT NULL, "
			+ DECKS_TABLE_TYPE + " INTEGER NOT NULL DEFAULT "+DECK_TYPE_CUSTOM+", "
			+ DECKS_TABLE_NAME + " VARCHAR(255) NOT NULL, "
			+ DECKS_TABLE_WEIGHT + " INTEGER NOT NULL DEFAULT 0, "
			+ DECKS_TABLE_EXTRA_DATA + " TEXT, "
			+ DECKS_TABLE_CREATED_AT + " TIMESTAMP NOT NULL, "
			+ DECKS_TABLE_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
	protected static final String DELETE_DATA_FROM_DECKS_TABLE = "DELETE from '"+TABLE_NAME+"';";

	/**
	 * Create a new media file record. If the record is successfully created return true. False otherwise
	 * 
	 * @throws SQLException
	 */

	public static long createDeck(Integer type, String name) {
		return createDeck(type, name, null, null);
	}
	public static long createDeck(Integer type, String name, Integer weight) {
		return createDeck(type, name, weight, null);
	}
	public static long createDeck(Integer type, String name, String extraData) {
		return createDeck(type, name, null, extraData);
	}
	public static long createDeck(Integer type, String name, Integer weight, String extraData) {
		long id = -1;
		
		ContentValues initialValues = createContentValues(type, name, weight, extraData);
		try {
			id = CPDecksApplication.getDatabase().insert(TABLE_NAME, null, initialValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	private static ContentValues createContentValues(Integer type, String name, Integer weight, String extraData) {
		ContentValues values = new ContentValues();
		values.put(DECKS_TABLE_TYPE, type);
		values.put(DECKS_TABLE_NAME, name);
		if( weight != null ){
			values.put(DECKS_TABLE_WEIGHT, weight);
		}
		if( extraData != null ){
			values.put(DECKS_TABLE_EXTRA_DATA, extraData);
		}
		
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String createdAt = gmtDateFormat.format(new Date());
		values.put(DECKS_TABLE_CREATED_AT, createdAt);

		return values;
	}

	private static ContentValues createContentValuesForUpdate(CPDecksDeck deck) {
		ContentValues values = new ContentValues();
		values.put(DECKS_TABLE_TYPE, deck.getType());
		values.put(DECKS_TABLE_NAME, deck.getTitle());
		values.put(DECKS_TABLE_WEIGHT, deck.getOrderWeight());
		
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String createdAt = gmtDateFormat.format(new Date());
		values.put(DECKS_TABLE_UPDATED_AT, createdAt);

		return values;
	}

	/**
	 * Return a Cursor positioned at the defined record
	 */
	public static Cursor fetchDeck(long id) throws SQLException {
		Cursor result = null;
		String whereClause = "(" + DECKS_TABLE_ID + " = " + id + ")";
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, null, null, null, null, null);

			if (result == null || result.getCount() < 1) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Return a Cursor of all decks in the database
	 */
	public static Cursor fetchAllDecks() throws SQLException {
		Cursor result = null;
		String whereClause = null;
		
		//Create new querybuilder
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		 
		//Specify books table and add join to categories table (use full_id for joining categories table)
		queryBuilder.setTables(CPDecksDecksDBAdapter.TABLE_NAME);
		         
		//Order by records by title
		String orderBy = CPDecksDecksDBAdapter.DECKS_TABLE_WEIGHT + " ASC, " + CPDecksDecksDBAdapter.DECKS_TABLE_CREATED_AT + " ASC";
		
		try {
			result = queryBuilder.query(CPDecksApplication.getDatabase(), null, whereClause, null, null, null, orderBy);

			if (result == null || result.getCount() < 1) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean removeVocabulary(Integer vocabularyId) {
		boolean result = false;
		
		if( vocabularyId == null || vocabularyId < 1){
			return result;
		}
		
		String whereClause = "(" + DECKS_TABLE_ID + "='" + vocabularyId + "')";
		try {
			result = CPDecksApplication.getDatabase().delete(TABLE_NAME, whereClause, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Cursor fetchDeckByTitle(String deckTitle) {
		Cursor result = null;
		String whereClause = "(" + DECKS_TABLE_NAME + " = ?)";
		String[] whereClauseArgs = {deckTitle};
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, whereClauseArgs, null, null, null, null);

			if (result == null || result.getCount() < 1) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static boolean updateDeck(CPDecksDeck deck) {
		boolean result = false;
		
		ContentValues updateValues = createContentValuesForUpdate(deck);
		try {
			result = (CPDecksApplication.getDatabase().update(TABLE_NAME, updateValues, DECKS_TABLE_ID+" = "+deck.getId(), null) > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean removeDeck(long deckId) {
		boolean result = false;
		
		if( deckId < 1 ){
			return result;
		}

		String whereClause = "(" + DECKS_TABLE_ID + " = " + deckId + ")";
		try {
			result = CPDecksApplication.getDatabase().delete(TABLE_NAME, whereClause, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

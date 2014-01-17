package com.chinesepod.decks.utility.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class CPDecksVocabularyToDecksDBAdapter {

	public static final String TABLE_NAME = "vocabulary_to_decks";
	public static final String VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID = "vocabulary_id";
	public static final String VOCABULARY_TO_DECKS_TABLE_DECKS_ID = "decks_id";
	public static final String VOCABULARY_TO_DECKS_TABLE_WEIGHT = "weight";
	public static final String VOCABULARY_TO_DECKS_TABLE_EXTRA_DATA = "extra_data";
	public static final String VOCABULARY_TO_DECKS_TABLE_CREATED_AT = "created_at";
	public static final String VOCABULARY_TO_DECKS_TABLE_UPDATED_AT = "updated_at";

	protected static final String VOCABULARY_TO_DECKS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
			+ VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID + " INTEGER NOT NULL, "
			+ VOCABULARY_TO_DECKS_TABLE_DECKS_ID + " INTEGER NOT NULL, "
			+ VOCABULARY_TO_DECKS_TABLE_WEIGHT + " INTEGER NOT NULL DEFAULT 0, "
			+ VOCABULARY_TO_DECKS_TABLE_EXTRA_DATA + " TEXT, "
			+ VOCABULARY_TO_DECKS_TABLE_CREATED_AT + " TIMESTAMP NOT NULL, "
			+ VOCABULARY_TO_DECKS_TABLE_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			+ "PRIMARY KEY ("+VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID+", "+VOCABULARY_TO_DECKS_TABLE_DECKS_ID+"), "
			+ "foreign key("+VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID+") references "+CPDecksVocabularyDBAdapter.TABLE_NAME+"("+CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID+"), "
			+ "foreign key("+VOCABULARY_TO_DECKS_TABLE_DECKS_ID+") references "+CPDecksDecksDBAdapter.TABLE_NAME+"("+CPDecksDecksDBAdapter.DECKS_TABLE_ID+") "
			+ ");";
	protected static final String DELETE_DATA_FROM_VOCABULARY_TO_DECKS_TABLE = "DELETE from '"+TABLE_NAME+"';";

	/**
	 * Create a new media file record. If the record is successfully created return true. False otherwise
	 * 
	 * @throws SQLException
	 */

	public static Boolean createVocabularyToDeckRelation(long vocabularyId, long deckId) {
		return createVocabularyToDeckRelation(vocabularyId, deckId, 0);
	}
	public static Boolean createVocabularyToDeckRelation(long vocabularyId, long deckId, String extraData) {
		return createVocabularyToDeckRelation(vocabularyId, deckId, null, extraData);
	}
	public static Boolean createVocabularyToDeckRelation(long vocabularyId, long deckId, Integer weight) {
		return createVocabularyToDeckRelation(vocabularyId, deckId, weight, null);
	}
	
	public static Boolean createVocabularyToDeckRelation(long vocabularyId, long deckId, Integer weight, String extraData) {
		boolean result = false;
		
		ContentValues initialValues = createContentValues(vocabularyId, deckId, weight, extraData);
		try {
			if (CPDecksApplication.getDatabase().insert(TABLE_NAME, null, initialValues) != -1) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static ContentValues createContentValues(long vocabularyId, long deckId, Integer weight, String extraData) {
		ContentValues values = new ContentValues();
		values.put(VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID, vocabularyId);
		values.put(VOCABULARY_TO_DECKS_TABLE_DECKS_ID, deckId);
		if( weight != null ){
			values.put(VOCABULARY_TO_DECKS_TABLE_WEIGHT, weight);
		}
		if( extraData != null ){
			values.put(VOCABULARY_TO_DECKS_TABLE_EXTRA_DATA, extraData);
		}
		
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String createdAt = gmtDateFormat.format(new Date());
		values.put(VOCABULARY_TO_DECKS_TABLE_CREATED_AT, createdAt);

		return values;
	}

	/**
	 * Return a Cursor positioned at the defined record
	 */
	public static Cursor fetchVocabularyToDecksRelation(long vocabularyId, long deckId) throws SQLException {
		Cursor result = null;
		String whereClause = "(" + VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID + "= " + vocabularyId + " AND " + VOCABULARY_TO_DECKS_TABLE_DECKS_ID + " = " + deckId + ")";
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
	 * Return a Cursor positioned at the defined records
	 */
	public static Cursor fetchAllDeckIdsByVocabularyId(long vocabularyId) throws SQLException {
		Cursor result = null;
		String whereClause = "(" + VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID + " = " + vocabularyId + ")";
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
	 * Return a Cursor positioned at the defined records
	 */
	public static Cursor fetchAllDeckIdsByVocabularyIds(ArrayList<Long> vocabularyIdList) throws SQLException {
		if( vocabularyIdList == null || vocabularyIdList.size() < 1 ){
			return null;
		}
		
		Cursor result = null;
		String whereClause = VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID + " IN (" + TextUtils.join(", ", vocabularyIdList) + ")";
		try {
			String[] columns = {VOCABULARY_TO_DECKS_TABLE_DECKS_ID};
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, columns, whereClause, null, null, null, null, null);

			if (result == null || result.getCount() < 1) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * Return a Cursor positioned at the defined record
	 */
	public static Cursor fetchAllVocabularyIdsByDeckId(long deckId) throws SQLException {
		Cursor result = null;
		String whereClause = "(" + VOCABULARY_TO_DECKS_TABLE_DECKS_ID + " = " + deckId + ")";
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, null, null, null, null, null);

			if (result == null || result.getCount() < 1 ) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Return a Cursor positioned at the defined records
	 */
	public static Cursor fetchAllVocabluaryIdsByDeckIds(ArrayList<Long> deckIdList) throws SQLException {
		if( deckIdList == null || deckIdList.size() < 1 ){
			return null;
		}

		Cursor result = null;
		String whereClause = VOCABULARY_TO_DECKS_TABLE_DECKS_ID + " IN (" + TextUtils.join(", ", deckIdList) + ")";
		try {
			String[] columns = {VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID};
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, columns, whereClause, null, null, null, null, null);

			if (result == null || result.getCount() < 1) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static Boolean updateVocabularyWeightInDeck(long vocabularyId, long deckId, Integer weight) {
		boolean result = false;

		if( vocabularyId < 1 || deckId < 1 || weight == null ){
			return result;
		}
		
		ContentValues updateValues = new ContentValues();
		updateValues.put(VOCABULARY_TO_DECKS_TABLE_WEIGHT, weight);
		String whereClause = "(" + VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID + "='" + vocabularyId + "' AND " + VOCABULARY_TO_DECKS_TABLE_DECKS_ID + "=" + deckId + ")";
		try {
			result = CPDecksApplication.getDatabase().update(TABLE_NAME, updateValues, whereClause, null) > 0;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public static Boolean removeVocabularyToDecksRelation(long vocabularyId, long deckId) {
		boolean result = false;
		
		if( vocabularyId < 1 || deckId < 1 ){
			return result;
		}

		String whereClause = "(" + VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID + "='" + vocabularyId + "' AND " + VOCABULARY_TO_DECKS_TABLE_DECKS_ID + "=" + deckId + ")";
		try {
			result = CPDecksApplication.getDatabase().delete(TABLE_NAME, whereClause, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean relationshipExists(CPDecksVocabulary vocabulary, CPDecksDeck deck) {
		Cursor cursor = null;
		try {
			cursor = fetchVocabularyToDecksRelation(vocabulary.getId(), deck.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (cursor != null && cursor.getCount() > 0);
	}
}

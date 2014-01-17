package com.chinesepod.decks.utility.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksVocabulary;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class CPDecksVocabularyDBAdapter {

	public static final String TABLE_NAME = "vocabulary";
	public static final String VOCABULARY_TABLE_ID = "_id";
	public static final String VOCABULARY_TABLE_APP_DECKS_CONTENT_ID = "app_decks_cpod_content_id";
	public static final String VOCABULARY_TABLE_CPOD_VOCAB_ID = "app_decks_cpod_vocab_id";
	public static final String VOCABULARY_TABLE_V3ID = "v3_id";
	public static final String VOCABULARY_TABLE_TYPE = "type";
	public static final String VOCABULARY_TABLE_SOURCE = "source";
	public static final String VOCABULARY_TABLE_TARGET = "target";
	public static final String VOCABULARY_TABLE_TARGET_PHONETICS = "target_phonetics";
	public static final String VOCABULARY_TABLE_AUDIO = "audio";
	public static final String VOCABULARY_TABLE_IMAGE = "image";
	public static final String VOCABULARY_TABLE_EXTRA_DATA = "extra_data";
	public static final String VOCABULARY_TABLE_CREATED_AT = "created_at";
	public static final String VOCABULARY_TABLE_UPDATED_AT = "updated_at";

	public static final int VOCABULARY_TYPE_TERM = 1;
	public static final int VOCABULARY_TYPE_PHRASE = 2;
	public static final int VOCABULARY_TYPE_SENTENCE = 3;

	protected static final String VOCABULARY_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
			+ VOCABULARY_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL, "
			+ VOCABULARY_TABLE_APP_DECKS_CONTENT_ID + " INTEGER, "
			+ VOCABULARY_TABLE_CPOD_VOCAB_ID + " INTEGER, "
			+ VOCABULARY_TABLE_V3ID + " VARCHAR(100), "
			+ VOCABULARY_TABLE_TYPE + " INTEGER NOT NULL DEFAULT "+VOCABULARY_TYPE_TERM+", "
			+ VOCABULARY_TABLE_SOURCE + " TEXT NOT NULL, "
			+ VOCABULARY_TABLE_TARGET + " TEXT NOT NULL, " 
			+ VOCABULARY_TABLE_TARGET_PHONETICS + " TEXT, "
			+ VOCABULARY_TABLE_AUDIO + " TEXT, "
			+ VOCABULARY_TABLE_IMAGE + " TEXT, "
			+ VOCABULARY_TABLE_EXTRA_DATA + " TEXT, "
			+ VOCABULARY_TABLE_CREATED_AT + " TIMESTAMP NOT NULL, "
			+ VOCABULARY_TABLE_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
			+ ");";
	protected static final String DELETE_DATA_FROM_VOCABULARY_TABLE = "DELETE from '"+TABLE_NAME+"';";

	/**
	 * Create a new vocabulary record in the database. If the record is successfully created return true. False otherwise
	 * 
	 * @throws SQLException
	 */

	public static long createVocabulary(CPDecksVocabulary vocabulary) {
		long result = -1;
		
		ContentValues initialValues = createContentValues(vocabulary);
		try {
			result = CPDecksApplication.getDatabase().insert(TABLE_NAME, null, initialValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static ContentValues createContentValues(CPDecksVocabulary vocabulary) {
		ContentValues values = new ContentValues();
		values.put(VOCABULARY_TABLE_TYPE, vocabulary.getType());
		values.put(VOCABULARY_TABLE_SOURCE, vocabulary.getSource());
		values.put(VOCABULARY_TABLE_TARGET, vocabulary.getTarget());
		values.put(VOCABULARY_TABLE_TARGET_PHONETICS, vocabulary.getTargetPhonetics());
		values.put(VOCABULARY_TABLE_AUDIO, vocabulary.getAudioUrl());
		values.put(VOCABULARY_TABLE_IMAGE, vocabulary.getImageUrl());

		if( vocabulary instanceof CPDecksContent ){
			values.put(VOCABULARY_TABLE_APP_DECKS_CONTENT_ID, vocabulary.getId());
		}
		if( vocabulary.getCpodVocabId() > 0 ){
			values.put(VOCABULARY_TABLE_CPOD_VOCAB_ID, vocabulary.getCpodVocabId());
		}
		
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String createdAt = gmtDateFormat.format(new Date());
		values.put(VOCABULARY_TABLE_CREATED_AT, createdAt);

		return values;
	}

	/**
	 * Return a Cursor positioned at the defined record
	 */
	public static Cursor fetchVocabulary(long id) {
		Cursor result = null;
		String whereClause = "(" + VOCABULARY_TABLE_ID + "= " + id + ")";
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, null, null, null, null, null);

			if (result == null || result.getCount() < 0) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static Cursor fetchVocabularyMatchingVocabulary(CPDecksVocabulary term) {
		Cursor result = null;
		
		if( term == null || term.getSource().isEmpty() || term.getTarget().isEmpty() ){
			return result;
		}
		
		String whereClause = "(" + VOCABULARY_TABLE_SOURCE + " = ? AND " + VOCABULARY_TABLE_TARGET + " = ? AND " + VOCABULARY_TABLE_TARGET_PHONETICS + " = ?)";
		String[] whereClauseArgs = {term.getSource(), term.getTarget(), term.getTargetPhonetics()};
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, whereClauseArgs, null, null, null, null);

			if (result == null || result.getCount() < 0) {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Return a Cursor of all files in the database
	 */
	public Cursor fetchAllVocabulary() throws SQLException {
		return fetchAllVocabulary(0);
	}
	public Cursor fetchAllVocabulary(long deckId) throws SQLException {
		Cursor result = null;
		
		//Create new querybuilder
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		 
		//Specify books table and add join to categories table (use full_id for joining categories table)
		queryBuilder.setTables(CPDecksVocabularyDBAdapter.TABLE_NAME);
		         
		//Order by records by title
		String orderBy = CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_CREATED_AT + " ASC";
		
		if( deckId > 0 ){
			queryBuilder.setTables(queryBuilder.getTables() + 
			        " JOIN " + CPDecksVocabularyToDecksDBAdapter.TABLE_NAME + " ON " + 
			        CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID + " = " + CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID);

			queryBuilder.appendWhere("(" + CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_DECKS_ID + " = " + deckId + ")");
			
			orderBy = CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_WEIGHT + " ASC, " + orderBy;
		}
		try {
			result = queryBuilder.query(CPDecksApplication.getDatabase(), null, null, null, null, null, orderBy);

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
		
		String whereClause = "(" + VOCABULARY_TABLE_ID + "='" + vocabularyId + "')";
		try {
			result = CPDecksApplication.getDatabase().delete(TABLE_NAME, whereClause, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Cursor fetchVocabularyByAppDecksContentId(long id) {
		Cursor result = null;
		String whereClause = "(" + VOCABULARY_TABLE_APP_DECKS_CONTENT_ID + " = " + id + ")";
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static Cursor fetchVocabularyByCpodVocabId(long id) {
		Cursor result = null;
		String whereClause = "(" + VOCABULARY_TABLE_CPOD_VOCAB_ID + " = " + id + ")";
		try {
			result = CPDecksApplication.getDatabase().query(true, TABLE_NAME, null, whereClause, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static Boolean removeVocabulary(long vocabularyId) {
		boolean result = false;
		
		if( vocabularyId < 1 ){
			return result;
		}

		String whereClause = "(" + VOCABULARY_TABLE_ID + " = " + vocabularyId + ")";
		try {
			result = CPDecksApplication.getDatabase().delete(TABLE_NAME, whereClause, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

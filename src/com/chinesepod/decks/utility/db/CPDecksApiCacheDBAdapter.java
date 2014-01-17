package com.chinesepod.decks.utility.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.chinesepod.decks.CPDecksApplication;

public class CPDecksApiCacheDBAdapter {

	public static final String API_CACHE_TABLE = "api_cache";
	public static final String API_CACHE_TABLE_URL = "url";
	public static final String API_CACHE_TABLE_FAMILY = "family";
	public static final String API_CACHE_TABLE_RESPONSE = "response";
	public static final String API_CACHE_TABLE_DATE = "date";
	public static final String API_CACHE_TABLE_MAXAGE = "maxage";

	public static final int MEDIAFILE_TYPE_RADIO = 0;
	public static final int MEDIAFILE_TYPE_CD = 1;
	public static final int MEDIAFILE_TYPE_DIALOGUE = 10;
	public static final int MEDIAFILE_TYPE_REVIEW = 20;

	protected static final String API_CACHE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + API_CACHE_TABLE + " ('_id' INTEGER PRIMARY KEY NOT NULL, "
			+ API_CACHE_TABLE_URL + " VARCHAR NOT NULL UNIQUE, " + API_CACHE_TABLE_FAMILY + " VARCHAR, " + API_CACHE_TABLE_RESPONSE + " VARCHAR NOT NULL, "
			+ API_CACHE_TABLE_DATE + " DATETIME NOT NULL, " + API_CACHE_TABLE_MAXAGE + " INTEGER NOT NULL);";
	protected static final String DELETE_DATA_FROM_API_CACHE_TABLE = "DELETE from 'api_cache';";

	private ContentValues createContentValues(String url, String res, String family) {
		ContentValues values = new ContentValues();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		if( family == null || family.isEmpty() ){
			family = getFamily(url);
		}
		values.put(API_CACHE_TABLE_URL, url);
		values.put(API_CACHE_TABLE_FAMILY, family);
		values.put(API_CACHE_TABLE_RESPONSE, res);
		values.put(API_CACHE_TABLE_DATE, dateFormat.format(date));
		values.put(API_CACHE_TABLE_MAXAGE, 10);
		return values;
	}

	public String getResponse(String url) {
		String ret = null;
		String whereClause = "(" + API_CACHE_TABLE_URL + "='" + url + "')";
		Cursor cursor = CPDecksApplication.getDatabase().query(true, API_CACHE_TABLE, new String[] { API_CACHE_TABLE_RESPONSE }, whereClause, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			ret = cursor.getString(0);
			cursor.close();
		}
		return ret;
	}
	
	public String getResponseDateByResponse(String response) {
		String ret = null;
		Cursor cursor = CPDecksApplication.getDatabase().rawQuery("select "+API_CACHE_TABLE_DATE+" from "+API_CACHE_TABLE+" where response = ?", new String[] { response });
		if (cursor != null && cursor.moveToFirst()) {
			ret = cursor.getString(0);
			cursor.close();
		}
		return ret;
	}

	public String queryDatabase(String queryFieldValue) {
		return queryDatabase(API_CACHE_TABLE_URL, queryFieldValue);
	}

	public String queryDatabase(String queryField, String queryFieldValue) {
		return queryDatabase(queryField, queryFieldValue, API_CACHE_TABLE_RESPONSE);
	}

	public String queryDatabase(String queryField, String queryFieldValue, String returnField) {
		if( queryField == null || queryFieldValue == null || returnField == null ){
			return null;
		}
		String ret = null;
		Cursor cursor = CPDecksApplication.getDatabase().rawQuery("select "+returnField+" from "+API_CACHE_TABLE+" where "+queryField+" = ?", new String[] { queryFieldValue });
		if (cursor != null && cursor.moveToFirst()) {
			ret = cursor.getString(0);
			cursor.close();
		}
		return ret;
	}
	
	public boolean saveResponse(String url, String res){
		return saveResponse(url, res, null);
	}

	public boolean saveResponse(String url, String res, String family) {
		ContentValues initialValues = createContentValues(url, res, family);
		Boolean ret = false;
		try {
			ret = (CPDecksApplication.getDatabase().insert(API_CACHE_TABLE, null, initialValues) != -1);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}

	public boolean deleteResponse(String url) {
//		String family = getFamily(url);
//		String whereClause = "(" + API_CACHE_TABLE_FAMILY + "='" + family + "')";
		Boolean ret = false;
		try {
			String whereClause = "(" + API_CACHE_TABLE_URL + "='" + url + "')";
			ret = (CPDecksApplication.getDatabase().delete(API_CACHE_TABLE, whereClause, null) > 0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}

	public String getFamily(String url) {
		url = url.replaceFirst("https{0,1}:/+", "");
		String[] splitted = url.split("/");
		String res = splitted[3] + "-" + splitted[4].replaceAll("\\?.*", "");
		if (url.contains("v3id=")) {
			// if url has v3id add it to family name
			// this way each lesson will have its own family
			String pattern = "(.*?&v3id=)([^&]+)(&.*){0,1}";
			String v3id = url.replaceAll(pattern, "$2");
			res += "-v3id-" + v3id;
		}
		if (url.contains("level_id=")) {
			// if url has level_id add it to family name
			// this way each library lesson set have its own family
			String pattern = "(.*?&level_id=)(\\d?)(&.*){0,1}";
			String levelId = url.replaceAll(pattern, "$2");
			res += "-levelId=" + levelId;
		}
		return res;
	}
	
	public String getLessonData(String v3id) {
		String ret = null;
		String family = "lesson-get-lesson-v3id-" + v3id;
		String whereClause = "(" + API_CACHE_TABLE_FAMILY + "='" + family + "')";
		Cursor cursor = CPDecksApplication.getDatabase().query(true, API_CACHE_TABLE, new String[] { API_CACHE_TABLE_RESPONSE }, whereClause, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			ret = cursor.getString(0);
			cursor.close();
		} 
		return ret;
	}

	public boolean removeLessonData(String lessonId) {
		Boolean ret = false;
		try {
			String family = "lesson-get-lesson-v3id-" + lessonId;
			String whereClause = "(" + API_CACHE_TABLE_FAMILY + "='" + family + "')";
			ret = (CPDecksApplication.getDatabase().delete(API_CACHE_TABLE, whereClause, null) > 0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}

	public String getLessonDate(String lessonId) {
		String ret = null;
		
		String family = "lesson-get-lesson-v3id-" + lessonId;
		Cursor cursor = CPDecksApplication.getDatabase().rawQuery("select "+API_CACHE_TABLE_DATE+" from "+API_CACHE_TABLE+" where "+API_CACHE_TABLE_FAMILY+" = ?", new String[] { family });
		if (cursor != null && cursor.moveToFirst()) {
			ret = cursor.getString(0);
			cursor.close();
		}
		
		return ret;
	}

	public boolean removeResponseByFamily(String family) {
		Boolean ret = false;
		
		String whereClause = "(" + API_CACHE_TABLE_FAMILY + "='" + family + "')";
		ret = (CPDecksApplication.getDatabase().delete(API_CACHE_TABLE, whereClause, null) > 0);
		
		return ret;
	}
	
	static public boolean removeLessonFromCache(Context context, String lessonId) {

		CPDecksApiCacheDBAdapter apiCacheDbHelper = new CPDecksApiCacheDBAdapter();
		return apiCacheDbHelper.removeLessonData(lessonId);
	}

	public static boolean removeResponseByFamily(Context context, String family) {
		CPDecksApiCacheDBAdapter apiCacheDbHelper = new CPDecksApiCacheDBAdapter();
		return apiCacheDbHelper.removeResponseByFamily(family);
	}
}

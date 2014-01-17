package com.chinesepod.decks.utility;

import java.util.ArrayList;

import org.apache.http.HttpClientConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.chinesepod.decks.logic.CPDecksAudio;
import com.chinesepod.decks.logic.CPDecksImage;
import com.chinesepod.decks.logic.CPDecksTranslation;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.db.CPDecksVocabularyDBAdapter;
import com.chinesepod.decks.utility.net.CPDecksContentNetworkUtilityModel;
import com.chinesepod.decks.utility.net.CPDecksTranslationNetworkUtilityModel;
import com.chinesepod.decks.utility.net.HttpNetworkUtilityImplementation;
import com.chinesepod.decks.utility.net.NetworkUtilityInterface;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;
import com.chinesepod.decks.Languages.Language;

public class CPDecksTranslationManager {
	private static int mFetchMode = NetworkUtilityModel.FIRST_NET_THEN_CACHE;

	public static void setFetchMode(int mode) {
		mFetchMode = mode;
	}
	
	public static CPDecksTranslation getTranslation(Language sourceLanguage, Language targetLanguage, String text){
		String translationJsonString = new CPDecksTranslationNetworkUtilityModel(mFetchMode).getTranslationData(sourceLanguage, targetLanguage, text);

		if( translationJsonString == null || translationJsonString.isEmpty() ){
			return null;
		}
		
		try {
			JSONObject translationJSONObj = new JSONObject(translationJsonString);
			CPDecksTranslation translation = new CPDecksTranslation();
			translation.setTranslationId(translationJSONObj.getLong("id"));
			translation.setSource(text);
			translation.setTarget(translationJSONObj.getString("target_text"));
			translation.setTargetPhonetics(translationJSONObj.getString("target_phonetics"));
			translation.setSourceLanguage(sourceLanguage);
			translation.setTargetLanguage(targetLanguage);
			
			if( translationJSONObj.has("source_audio_url") && translationJSONObj.getString("source_audio_url") != null && !  translationJSONObj.getString("source_audio_url").equals("null") ){
				CPDecksAudio sourceAudio = new CPDecksAudio();
				sourceAudio.setAudioUrl(translationJSONObj.getString("source_audio_url"));
				sourceAudio.setAudioFile(CPDecksUtility.generateTranslationAudioFilePath(sourceAudio));
				translation.setSourceAudio(sourceAudio);
			}
			if( translationJSONObj.has("audio_url") && translationJSONObj.getString("audio_url") != null && !  translationJSONObj.getString("audio_url").equals("null") ){
				CPDecksAudio targetAudio = new CPDecksAudio();
				targetAudio.setAudioUrl(translationJSONObj.getString("audio_url"));
				targetAudio.setAudioFile(CPDecksUtility.generateTranslationAudioFilePath(targetAudio));
				translation.setTargetAudio(targetAudio);
			}
			
			if( translationJSONObj.has("image_urls") && translationJSONObj.getString("image_urls") != null && !  translationJSONObj.getString("image_urls").equals("null") ){
				translation.setImageUrlsList(getImageList(translationJSONObj.getString("image_urls")));
			}
			
			return translation;
		}
		catch(JSONException e){
			e.printStackTrace();
			return null;
		}
	}

//	public static String getTranslation2(Language sourceLanguage, Language targetLanguage, String text){
//	    GoogleAPI.setHttpReferrer("http://chinesepod.com");
//	    GoogleAPI.setKey(CPDecksUtility.GOOGLE_API_KEY);
//
//	    String translatedText = null;
//		try {
//			translatedText = Translate.DEFAULT.execute(text, sourceLanguage, targetLanguage);
//		} catch (GoogleAPIException e) {
//			e.printStackTrace();
//		}
//
//		return translatedText;
//	}

	public static ArrayList<CPDecksImage> getImageList(String jsonString){
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			
			ArrayList<CPDecksImage> imageList = new ArrayList<CPDecksImage>();
			
			for(int i = 0; i < jsonArray.length(); ++i){
				try {
					JSONObject imageJsonObj = jsonArray.getJSONObject(i);
					
					CPDecksImage image = new CPDecksImage();
//					image.setImageId(imageJsonObj.getString("image_id"));
					image.setWidth(imageJsonObj.getInt("width"));
					image.setHeight(imageJsonObj.getInt("height"));
					image.setImageUrl(imageJsonObj.getString("url"));
					image.setThumbWidth(imageJsonObj.getInt("thumb_width"));
					image.setThumbHeight(imageJsonObj.getInt("thumb_height"));
					image.setThumbUrl(imageJsonObj.getString("thumb_url"));
					imageList.add(image);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			return imageList;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}

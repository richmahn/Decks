package com.chinesepod.decks.utility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksAudio;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.logic.CPDecksSentence;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.db.CPDecksDecksDBAdapter;
import com.chinesepod.decks.utility.db.CPDecksVocabularyDBAdapter;
import com.chinesepod.decks.utility.db.CPDecksVocabularyToDecksDBAdapter;
import com.chinesepod.decks.utility.net.CPDecksVocabularyNetworkUtilityModel;

public class CPDecksVocabularyManager {

	private static int mFetchMode;

	public static void setFetchMode(int mode) {
		mFetchMode = mode;
	}
	
	public static CPDecksVocabulary createVocabulary(CPDecksVocabulary vocabulary){
		long id = CPDecksVocabularyDBAdapter.createVocabulary(vocabulary);

		if( id >= 0 ){
			vocabulary = getVocabulary(id);
		}
		else {
			vocabulary = null;
		}
		
		return vocabulary;
	}

	public static ArrayList<CPDecksVocabulary> getVocabularyListByDeckId(long deckId){
		ArrayList<CPDecksVocabulary> vocabularyList = new ArrayList<CPDecksVocabulary>();
		try {
			Cursor cursor = CPDecksVocabularyToDecksDBAdapter.fetchAllVocabularyIdsByDeckId(deckId);
			if( cursor != null ){
				while(cursor.moveToNext()){
					CPDecksVocabulary vocabulary = getVocabulary(cursor.getLong(cursor.getColumnIndex(CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_VOCABULARY_ID)));
					vocabularyList.add(vocabulary);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vocabularyList;
	}

	public static CPDecksVocabulary getVocabulary(long vocabularyId) {
		return produceVocabularyFromCursor(CPDecksVocabularyDBAdapter.fetchVocabulary(vocabularyId));
	}

	private static CPDecksVocabulary produceVocabularyFromCursor(Cursor cursor) {
		if( cursor != null && cursor.getPosition() < cursor.getCount() ){
			if( cursor.getPosition() < 0 ){
				cursor.moveToFirst();
			}
			try {
				CPDecksVocabulary vocabulary = new CPDecksVocabulary();
				vocabulary.setId(cursor.getLong(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID)));
				vocabulary.setAppDecksContentId(cursor.getLong(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_APP_DECKS_CONTENT_ID)));
				vocabulary.setCpodVocabId(cursor.getLong(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_CPOD_VOCAB_ID)));
				vocabulary.setType(cursor.getInt(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_TYPE)));
				vocabulary.setSource(cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_SOURCE)));
				vocabulary.setTarget(cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_TARGET)));
				vocabulary.setTargetPhonetics(cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_TARGET_PHONETICS)));
				vocabulary.setCreatedAt(cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_CREATED_AT)));
				vocabulary.setUpdatedAt(cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_UPDATED_AT)));
				
				String audioUrl = cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_AUDIO));
				if( audioUrl != null && ! audioUrl.isEmpty() && audioUrl.startsWith("http") ){
					CPDecksAudio audio = new CPDecksAudio();
					audio.setAudioUrl(audioUrl);
					audio.setAudioFile(CPDecksUtility.generateLinguisticFilePath(vocabulary));
					vocabulary.setTargetAudio(audio);
				}
				
				String imageUrl = cursor.getString(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_IMAGE));
				if( imageUrl != null && ! imageUrl.isEmpty() && imageUrl.startsWith("http") ){
					vocabulary.setImageUrl(imageUrl);
				}
				return vocabulary;
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static CPDecksVocabulary getVocabularyByAppDecksContentId(long id) {
		Cursor cursor = CPDecksVocabularyDBAdapter.fetchVocabularyByAppDecksContentId(id);
		if( cursor != null && cursor.moveToNext() ){
			return produceVocabularyFromCursor(cursor);
		}
		
		return null;
	}

	public static long getVocabularyIdByAppDecksContentId(long id) {
		Cursor cursor = CPDecksVocabularyDBAdapter.fetchVocabularyByAppDecksContentId(id);
		if( cursor != null && cursor.moveToNext() ){
			return cursor.getLong(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID));
		}
		
		return 0;
	}

	public static long getVocabularyIdByCpodVocabId(long id) {
		Cursor cursor = CPDecksVocabularyDBAdapter.fetchVocabularyByCpodVocabId(id);
		if( cursor != null && cursor.moveToNext() ){
			return cursor.getLong(cursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID));
		}
		
		return 0;
	}
	
	public static long createDeck(String name){
		return createDeck(name, null);
	}
	public static long createDeck(String name, String icon){
		Map<String, String> extraDataMap = new HashMap<String, String>();
		extraDataMap.put("icon", icon);
		JSONObject extraDataJsonObject = new JSONObject(extraDataMap);
		
		long id = CPDecksDecksDBAdapter.createDeck(CPDecksDecksDBAdapter.DECK_TYPE_CUSTOM, name, extraDataJsonObject.toString());
		return id;
	}

	public static ArrayList<CPDecksDeck> getDecks(){
		ArrayList<CPDecksDeck> deckList = new ArrayList<CPDecksDeck>();
		try {
			Cursor cursor = CPDecksDecksDBAdapter.fetchAllDecks();
			if( cursor != null ){
				while(cursor.moveToNext()){
					CPDecksDeck deck = produceDeckFromCursor(cursor);
					deckList.add(deck);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return deckList;
	}
	
	public static int getDeckCount(){
		try {
			Cursor cursor = CPDecksDecksDBAdapter.fetchAllDecks();
			if( cursor != null && cursor.getCount() > 0 ){
				return cursor.getCount();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	public static CPDecksDeck getDeck(long deckId){
		try {
			Cursor cursor  = CPDecksDecksDBAdapter.fetchDeck(deckId);
			if( cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() ){
				return produceDeckFromCursor(cursor);
			}
			else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static CPDecksDeck produceDeckFromCursor(Cursor cursor) {
		if( cursor != null && cursor.getPosition() > -1 && cursor.getPosition() < cursor.getCount() ){
			try {
				CPDecksDeck deck = new CPDecksDeck();
				deck.setId(cursor.getLong(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_ID)));
				deck.setType(cursor.getInt(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_TYPE)));
				deck.setTitle(cursor.getString(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_NAME)));
				deck.setOrderWeight(cursor.getInt(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_WEIGHT)));
				deck.setCreatedAt(cursor.getString(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_CREATED_AT)));
				deck.setUpdatedAt(cursor.getString(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_UPDATED_AT)));
				
				String extraData = cursor.getString(cursor.getColumnIndex(CPDecksDecksDBAdapter.DECKS_TABLE_EXTRA_DATA));
				if( extraData != null && ! extraData.isEmpty() ){
					try {
						JSONObject extraDataJsonObject = new JSONObject(extraData);
						Iterator<String> keys = extraDataJsonObject.keys();
						while(keys.hasNext()) {
							String key = keys.next();
							deck.putExtraData(key, extraDataJsonObject.getString(key));
						}
					}
					catch(JSONException e){
						e.printStackTrace();
					}
				}
				
				deck.setVocabulary(CPDecksVocabularyManager.getVocabularyListByDeckId(deck.getId()));
				return deck;
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static boolean saveVocabularyToDeck(CPDecksVocabulary vocabulary, CPDecksDeck deck) {
		boolean result = false;
		
		if( vocabulary == null || deck == null ){
			return result;
		}
		
		if( vocabulary.getId() < 1 ){
			vocabulary = createVocabulary(vocabulary); // Create a new vocabulary entry in the database
		}
		
		if( vocabulary != null && vocabulary.getId() > 0 && deck.getId() > 0 && ! CPDecksVocabularyToDecksDBAdapter.relationshipExists(vocabulary, deck) ){
			result = CPDecksVocabularyToDecksDBAdapter.createVocabularyToDeckRelation(vocabulary.getId(), deck.getId());
		}
		
		return result;
	}

	public static ArrayList<CPDecksDeck> getDecksWithAppDecksContentId(long id) {
		Cursor vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyByAppDecksContentId(id);
		ArrayList<Long> vocabularyIdList = new ArrayList<Long>(); 
		if( vocabularyCursor != null ){
			while( vocabularyCursor.moveToNext() ){
				vocabularyIdList.add(vocabularyCursor.getLong(vocabularyCursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID)));
			}
		}
		Cursor vocabularyToDecksCursor = null;
		try {
			vocabularyToDecksCursor = CPDecksVocabularyToDecksDBAdapter.fetchAllDeckIdsByVocabularyIds(vocabularyIdList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList<CPDecksDeck> deckList = new ArrayList<CPDecksDeck>(); 
		if( vocabularyToDecksCursor != null ){
			while( vocabularyToDecksCursor.moveToNext() ){
				CPDecksDeck deck = getDeck(vocabularyToDecksCursor.getLong(vocabularyToDecksCursor.getColumnIndex(CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_DECKS_ID)));
				if( deck != null && deck.getId() > 0 ){
					deckList.add(deck);
				}
			}
		}
		
		return deckList;
	}

	public static ArrayList<CPDecksDeck> getDecksWithoutAppDecksContentId(long id) {
		Cursor vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyByAppDecksContentId(id);
		ArrayList<Long> vocabularyIdList = new ArrayList<Long>(); 
		if( vocabularyCursor != null ){
			while( vocabularyCursor.moveToNext() ){
				vocabularyIdList.add(vocabularyCursor.getLong(vocabularyCursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID)));
			}
		}
		Cursor vocabularyToDecksCursor = null;
		try {
			vocabularyToDecksCursor = CPDecksVocabularyToDecksDBAdapter.fetchAllDeckIdsByVocabularyIds(vocabularyIdList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList<Long> deckIdList = new ArrayList<Long>(); 
		if( vocabularyToDecksCursor != null ){
			while( vocabularyToDecksCursor.moveToNext() ){
				deckIdList.add(vocabularyToDecksCursor.getLong(vocabularyToDecksCursor.getColumnIndex(CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_DECKS_ID)));
			}
		}
		
		ArrayList<CPDecksDeck> allDecksList = getDecks();
		ArrayList<CPDecksDeck> decksWithoutList = new ArrayList<CPDecksDeck>();
		for(CPDecksDeck deck : allDecksList ){
			if( ! deckIdList.contains(deck.getId()) ){
				decksWithoutList.add(deck);
			}
		}
		
		return decksWithoutList;
	}
	
	public static CPDecksVocabulary getVocabularyInDatabase(CPDecksVocabulary vocabulary){
		Cursor vocabularyCursor = null;
		
		if( vocabulary == null ){
			return null;
		}
		
		if( vocabulary.getId() > 0 ){
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabulary(vocabulary.getId());
		}
		else if( vocabulary.getAppDecksContentId() > 0 ){
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyByAppDecksContentId(vocabulary.getAppDecksContentId());
		}
		else if( vocabulary.getCpodVocabId() > 0 ){
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyByCpodVocabId(vocabulary.getCpodVocabId());
		}
		else if( ! vocabulary.getSource().isEmpty() && ! vocabulary.getTarget().isEmpty() && ! vocabulary.getTargetPhonetics().isEmpty() ){
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyMatchingVocabulary(vocabulary);
		}
		
		if( vocabularyCursor == null || ! vocabularyCursor.moveToFirst() ){
			return null;
		}
		
		return produceVocabularyFromCursor(vocabularyCursor);
	}

	public static ArrayList<CPDecksDeck> getDecksWithoutVocabulary(CPDecksVocabulary vocabulary) {
		Cursor vocabularyCursor = null;
		
		if( vocabulary == null ){
			return null;
		}
		
		if( ! vocabulary.getSource().isEmpty() && ! vocabulary.getTarget().isEmpty() && ! vocabulary.getTargetPhonetics().isEmpty() ){
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyMatchingVocabulary(vocabulary);
		}
		else if( vocabulary instanceof CPDecksContent ){
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyByAppDecksContentId(vocabulary.getId());
		}
		else {
			vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabulary(vocabulary.getId());
		}
		
		ArrayList<Long> vocabularyIdList = new ArrayList<Long>(); 
		if( vocabularyCursor != null ){
			while( vocabularyCursor.moveToNext() ){
				vocabularyIdList.add(vocabularyCursor.getLong(vocabularyCursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID)));
			}
		}
		Cursor vocabularyToDecksCursor = null;
		try {
			vocabularyToDecksCursor = CPDecksVocabularyToDecksDBAdapter.fetchAllDeckIdsByVocabularyIds(vocabularyIdList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList<Long> deckIdList = new ArrayList<Long>(); 
		if( vocabularyToDecksCursor != null ){
			while( vocabularyToDecksCursor.moveToNext() ){
				deckIdList.add(vocabularyToDecksCursor.getLong(vocabularyToDecksCursor.getColumnIndex(CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_DECKS_ID)));
			}
		}
		
		ArrayList<CPDecksDeck> allDecksList = getDecks();
		ArrayList<CPDecksDeck> decksWithoutList = new ArrayList<CPDecksDeck>();
		for(CPDecksDeck deck : allDecksList ){
			if( ! deckIdList.contains(deck.getId()) ){
				decksWithoutList.add(deck);
			}
		}
		
		return decksWithoutList;
	}

	public static boolean removeVocabularyFromDeck(CPDecksVocabulary vocabulary, CPDecksDeck deck) {
		boolean result = false;
		
		if( vocabulary == null || deck == null ){
			return result;
		}
		
		result = CPDecksVocabularyToDecksDBAdapter.removeVocabularyToDecksRelation(vocabulary.getId(), deck.getId());
		
		if( result ){
			try {
				Cursor cursor = CPDecksVocabularyToDecksDBAdapter.fetchAllDeckIdsByVocabularyId(vocabulary.getId());
				if( cursor == null || cursor.getCount() < 1 ){
					removeVocabulary(vocabulary);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static boolean removeVocabulary(CPDecksVocabulary vocabulary){
		if( vocabulary == null || vocabulary.getId() < 0 ){
			return false;
		}
		return CPDecksVocabularyDBAdapter.removeVocabulary(vocabulary.getId());
	}

	public static CPDecksDeck getDeckByTitle(String deckTitle){
		Cursor cursor  = CPDecksDecksDBAdapter.fetchDeckByTitle(deckTitle);
		if( cursor != null && cursor.moveToFirst() ){
			return produceDeckFromCursor(cursor);
		}
		else {
			return null;
		}
	}

	public static boolean updateDeck(CPDecksDeck deck) {
		if( deck == null ){
			return false;
		}
		
		if( deck.getId() < 1 && deck.getTitle() != null && deck.getTitle().length() > 0 ){
			deck = getDeckByTitle(deck.getTitle());
		}
		
		if( deck == null || deck.getId() < 0 ){
			return false;
		}
		
		boolean result = CPDecksDecksDBAdapter.updateDeck(deck);

		return result;
	}

	public static boolean removeDeck(CPDecksDeck deck) {
		boolean result = false;
		
		if( deck == null || deck.getId() < 1 ){
			return result;
		}
		
		for(CPDecksVocabulary vocabulary : deck.getVocabulary() ){
			removeVocabularyFromDeck(vocabulary, deck);
		}
		
		result = CPDecksDecksDBAdapter.removeDeck(deck.getId());
		
		return result;
	}

	public static CPDecksResponse retrieveGlossarySampleSentences(CPDecksVocabulary term, int page, int count) {
		String sentencesJsonString = new CPDecksVocabularyNetworkUtilityModel(mFetchMode).retrieveGlossarySampleSentences(term.getTarget(), page, count);
		CPDecksResponse response = new CPDecksResponse();
		if (sentencesJsonString != null) {
			try {
				JSONObject sentencesJson = new JSONObject(sentencesJsonString);

				ArrayList<CPDecksSentence> sentences = new ArrayList<CPDecksSentence>();
				for (int i = 0; i < sentencesJson.length() - 1; i++) {
					try {
						int order = (i+(page*count)+1);
						JSONObject sentenceJson = sentencesJson.getJSONObject(order + "");
						CPDecksSentence sentence = new CPDecksSentence();
						sentence.setSource(sentenceJson.getString("target"));
						sentence.setTarget(sentenceJson.getString("source"));
						sentence.setTargetPhonetics(sentenceJson.optString("pinyin"));
						sentence.getTargetAudio().setAudioUrl(sentenceJson.optString("audio"));
					
						if( sentenceJson.has("id") ){
							sentence.setCpodVocabId(sentenceJson.getInt("id"));
						}
						else if( ! sentence.getTargetAudio().getAudioUrl().isEmpty() ){
							sentence.setCpodVocabId(CPDecksUtility.makeAudioIdFromAudioUrl(sentence.getTargetAudio().getAudioUrl()));
						}
						
						sentence.setType(CPDecksVocabulary.TYPE_SENTENCE);
					
						if( sentenceJson.has("v3_id") ){
							sentence.setV3id(sentenceJson.getString("v3_id"));
						}
						else if( ! sentence.getTargetAudio().getAudioUrl().isEmpty() ){
							sentence.setV3id(CPDecksUtility.getV3idFromAudioUrl(sentence.getTargetAudio().getAudioUrl()));
						}
						sentence.getTargetAudio().setAudioFile(CPDecksUtility.generateLinguisticFilePath(sentence));

						JSONArray sentenceWordsJson = sentenceJson.getJSONArray("sentence_words");
						for (int j = 0; j < sentenceWordsJson.length(); j++) {
							JSONObject wordJson = sentenceWordsJson.getJSONObject(j);

							String audioUrl = wordJson.optString("audio");

							CPDecksVocabulary word = new CPDecksVocabulary();
						
							word.setCpodVocabId(wordJson.optInt("vcid", wordJson.optInt("id")));
							word.setSource(wordJson.getString("target"));
							word.setTarget(wordJson.getString("source"));
							word.setTargetPhonetics(wordJson.optString("pinyin"));
							word.getTargetAudio().setAudioUrl(audioUrl);

							word.setType(CPDecksVocabulary.TYPE_WORD);
							
							if( wordJson.has("v3_id") ){
								word.setV3id(wordJson.getString("v3_id"));
							}
							else if( ! word.getTargetAudio().getAudioUrl().isEmpty() ){
								word.setV3id(CPDecksUtility.getV3idFromAudioUrl(word.getTargetAudio().getAudioUrl()));
							}
							word.getTargetAudio().setAudioFile(CPDecksUtility.generateLinguisticFilePath(word));
						
							word.setId(getVocabularyIdByCpodVocabId(word.getCpodVocabId()));
							
							sentence.getWords().add(word);
						}
						sentences.add(sentence);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				response.setList(sentences);
				return response;
			} catch (Exception x) {
				return CPDecksManager.processRemoteError(response, sentencesJsonString);
			}
		}
		return CPDecksManager.processNetworkError(response);
	}

	public static ArrayList<CPDecksDeck> getDecksWithVocabulary(CPDecksVocabulary term) {
		Cursor vocabularyCursor = null;
		vocabularyCursor = CPDecksVocabularyDBAdapter.fetchVocabularyMatchingVocabulary(term);
		ArrayList<Long> vocabularyIdList = new ArrayList<Long>(); 
		if( vocabularyCursor != null ){
			while( vocabularyCursor.moveToNext() ){
				vocabularyIdList.add(vocabularyCursor.getLong(vocabularyCursor.getColumnIndex(CPDecksVocabularyDBAdapter.VOCABULARY_TABLE_ID)));
			}
		}
		Cursor vocabularyToDecksCursor = null;
		try {
			vocabularyToDecksCursor = CPDecksVocabularyToDecksDBAdapter.fetchAllDeckIdsByVocabularyIds(vocabularyIdList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList<Long> deckIdList = new ArrayList<Long>(); 
		if( vocabularyToDecksCursor != null ){
			while( vocabularyToDecksCursor.moveToNext() ){
				deckIdList.add(vocabularyToDecksCursor.getLong(vocabularyToDecksCursor.getColumnIndex(CPDecksVocabularyToDecksDBAdapter.VOCABULARY_TO_DECKS_TABLE_DECKS_ID)));
			}
		}
		
		ArrayList<CPDecksDeck> allDecksList = getDecks();
		ArrayList<CPDecksDeck> decksWithVocabulary = new ArrayList<CPDecksDeck>();
		for(CPDecksDeck deck : allDecksList ){
			if( deckIdList.contains(deck.getId()) ){
				decksWithVocabulary.add(deck);
			}
		}
		
		return decksWithVocabulary;
	}

	public static boolean saveDecksInThisOrder(ArrayList<CPDecksDeck> deckList) {
		if( deckList == null || deckList.size() < 1){
			return false;
		}
		int weight = 0 - deckList.size();
		for(CPDecksDeck deck : deckList){
			deck.setOrderWeight(weight);
			++weight;
			CPDecksDecksDBAdapter.updateDeck(deck);
		}
		return true;
	}
}

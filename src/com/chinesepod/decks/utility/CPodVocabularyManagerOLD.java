package com.chinesepod.decks.utility;
 
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.text.Html;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.logic.CPDecksSentence;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.net.CPDecksVocabularyNetworkUtilityModel;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;
import com.chinesepod.decks.utility.CPDecksManager;

public class CPodVocabularyManagerOLD {
//
//	public static final int ERROR = -1;
//	public static final int SUCCEED = 0;
//	public static final int EXISTS = 1;
//	public static final int FAILED = 2;
//	private static int mFetchMode;
//
//
//	public static void setFetchMode(int mode) {
//		mFetchMode = mode;
//	}
//
//	public static CPDecksResponse getDecks(CPDecksAccount account) {
//		CPDecksResponse response = new CPDecksResponse();
//		
//		if( CPDecksApplication.getDecks().size() > 1 && (mFetchMode == NetworkUtilityModel.FIRST_CACHE_THEN_NET || mFetchMode == NetworkUtilityModel.ONLY_USE_CACHE)){
//			response.setList(CPDecksApplication.getDecks());
//			return response;
//		}
//
//		String decksJsonString = new CPDecksVocabularyNetworkUtilityModel(mFetchMode).getDecks(account);
//		if (decksJsonString != null) {
//			try {
//				JSONObject decksJson = new JSONObject(decksJsonString);
//				int total = decksJson.getInt("total");
//				ArrayList<CPDecksDeck> decks = processJsonToDecks(decksJson);
//				CPDecksApplication.setDecks(decks); // this makes sure any deleted decks are gone
//				response.setList(CPDecksApplication.getDecks()); // make sure they are alphabetized
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, decksJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	private static ArrayList<CPDecksVocabulary> processJsonToWords(JSONObject wordsJson) throws JSONException {
//		try {
//			ArrayList<CPDecksVocabulary> words = new ArrayList<CPDecksVocabulary>();
//			for (int i = 0; i < wordsJson.length() - 1; i++) {
//				try {
//					JSONObject wordJson = wordsJson.getJSONObject(i + "");
//				
//					String audioUrl = wordJson.optString("audio");
//					int id = wordJson.optInt("id", wordJson.optInt("vcid"));
//
//					if( id < 1 && ! audioUrl.isEmpty() ){
//						id = CPDecksUtility.makeAudioIdFromAudioUrl(audioUrl);
//					}
//
//					CPDecksVocabulary word = CPDecksApplication.getWord(id);
//					
//					word.setVocabId(wordJson.optInt("vcid", wordJson.optInt("id")));
//					word.setUserVocabId(wordJson.optInt("uvid"));
//					word.setSource(wordJson.getString("source"));
//					word.setSourceTraditional(wordJson.optString("source_t"));
//					word.setTranslation(wordJson.optString("translation"));
//					word.setPinyin(wordJson.optString("pinyin"));
//					word.getAudio().setAudioUrl(audioUrl);
//
//					if( wordJson.has("v3_id") ){
//						word.setV3id(wordJson.getString("v3_id"));
//					}
//					if( (word.getV3id() == null || word.getV3id().isEmpty()) && ! word.getAudio().getAudioUrl().isEmpty() ){
//						word.setV3id(CPDecksUtility.getV3idFromAudioUrl(word.getAudio().getAudioUrl()));
//					}
//					word.getAudio().setAudioFile(CPDecksUtility.generateLinguisticFilePath(word));
//					
//					words.add(word);
//				}
//				catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			return words;
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static Integer addWord(CPDecksAccount account, CPDecksVocabulary word) {
//		String result = new CPDecksVocabularyNetworkUtilityModel().addWord(account, word);
//		if (result != null) {
//			try {
//				JSONObject wordsJson = new JSONObject(result);
//				result = wordsJson.getString("result");
//				if (result.equals("failed")) {
//					return FAILED;
//				} else if (result.equals("succeed")) {
//					return SUCCEED;
//				} else if (result.equals("exists")) {
//					return EXISTS;
//				} else {
//					return ERROR;
//				}
//			} catch (Exception x) {
//				x.printStackTrace();
//			}
//		}
//		return ERROR;
//	}
//
//	public static CPDecksResponse saveTerm(CPDecksAccount account, CPDecksVocabulary word, CPDecksDeck deck) {
//		String resultJsonString = "";
//
//		if( word.getId() < 1 ) {
//			resultJsonString = new CPDecksVocabularyNetworkUtilityModel().addWord(account, word, deck);
//		}
//		else {
//			ArrayList<CPDecksVocabulary> words = new ArrayList<CPDecksVocabulary>();
//			words.add(word);
//			resultJsonString = new CPDecksVocabularyNetworkUtilityModel().saveWords(account, words, deck.getId());
//		}
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getString("result"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse saveWords(CPDecksAccount account, List<CPDecksVocabulary> terms, CPDecksDeck deck) {
//		return saveWords(account, terms, deck.getId());
//	}
//	
//	public static CPDecksResponse saveWords(CPDecksAccount account, List<CPDecksVocabulary> terms, int deckId) {
//		String resultJsonString = new CPDecksVocabularyNetworkUtilityModel().saveWords(account, terms, deckId);
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getString("result"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse searchGlossary(CPDecksAccount account, String query) {
//		String glossaryJsonString = new CPDecksVocabularyNetworkUtilityModel(mFetchMode).searchGlossary(account, query);
//		CPDecksResponse response = new CPDecksResponse();
//		if (glossaryJsonString != null) {
//			try {
//				JSONObject glossaryJson = new JSONObject(Uri.decode(glossaryJsonString));
//				ArrayList<CPDecksVocabulary> words = new ArrayList<CPDecksVocabulary>();
//				for (int i = 0; i < glossaryJson.length() - 1; i++) {
//					JSONObject wordJson = glossaryJson.getJSONObject(i + "");
//
//					String audioUrl = wordJson.optString("audio");
//					int id = wordJson.optInt("id");
//
//					if( id < 1 && ! audioUrl.isEmpty() ){
//						id = CPDecksUtility.makeAudioIdFromAudioUrl(audioUrl);
//					}
//
//					CPDecksVocabulary word = CPDecksApplication.getWord(id);
//					
//					word.setSource(wordJson.getString("source"));
//					word.setSourceTraditional(wordJson.optString("source_t", wordJson.optString("source_tw")));
//					word.setTranslation(wordJson.getString("target"));
//					word.setPinyin(Html.fromHtml(wordJson.optString("phonetics")).toString());
//					word.getAudio().setAudioUrl(audioUrl);
//	
//					if( wordJson.has("v3_id") ){
//						word.setV3id(wordJson.getString("v3_id"));
//					}
//					else if( ! word.getAudio().getAudioUrl().isEmpty() ){
//						word.setV3id(CPDecksUtility.getV3idFromAudioUrl(word.getAudio().getAudioUrl()));
//					}
//					word.getAudio().setAudioFile(CPDecksUtility.generateLinguisticFilePath(word));
//					
//					words.add(word);
//				}
//
//				response.setList(words);
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, glossaryJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse getTermGlossary(CPDecksAccount account, CPDecksVocabulary mTerm, String lessonId) {
//
//		String glossaryJsonString = new CPDecksVocabularyNetworkUtilityModel(mFetchMode).getTermGlossary(account, mTerm.getId(), lessonId);
//		CPDecksResponse response = new CPDecksResponse();
//		if (glossaryJsonString != null) {
//			try {
//				JSONObject glossaryJson = new JSONObject(glossaryJsonString);
//				int total = glossaryJson.optInt("total");
//				ArrayList<CPDecksSentence> sentences = new ArrayList<CPDecksSentence>();
//				for (int i = 0; i < glossaryJson.length() - 1; i++) {
//					JSONObject sentenceJson = glossaryJson.getJSONObject(i + "");
//					CPDecksSentence sentence = new CPDecksSentence();
//					sentence.setSource(sentenceJson.getString("target"));
//					sentence.setTranslation(sentenceJson.getString("source"));
//					sentence.setPinyin(sentenceJson.optString("phonetics"));
//					sentence.setId(sentenceJson.getInt("id"));
//					sentence.getAudio().setAudioUrl(sentenceJson.optString("audio"));
//
//					if( sentenceJson.has("id") ){
//						sentence.setId(sentenceJson.getInt("id"));
//					}
//					else if( ! sentence.getAudio().getAudioUrl().isEmpty() ){
//						sentence.setId(CPDecksUtility.makeAudioIdFromAudioUrl(sentence.getAudio().getAudioUrl()));
//					}
//				
//					if( sentenceJson.has("v3_id") ){
//						sentence.setV3id(sentenceJson.getString("v3_id"));
//					}
//					else if( ! sentence.getAudio().getAudioUrl().isEmpty() ){
//						sentence.setV3id(CPDecksUtility.getV3idFromAudioUrl(sentence.getAudio().getAudioUrl()));
//					}
//					sentence.getAudio().setAudioFile(CPDecksUtility.generateLinguisticFilePath(sentence));
//
//					JSONArray sentenceWordsJson = sentenceJson.getJSONArray("sentence_words");
//					for (int j = 0; j < sentenceWordsJson.length(); j++) {
//						JSONObject wordJson = sentenceWordsJson.getJSONObject(j);
//
//						String audioUrl = wordJson.optString("audio");
//						int id = wordJson.optInt("id");
//
//						if( id < 1 && ! audioUrl.isEmpty() ){
//							id = CPDecksUtility.makeAudioIdFromAudioUrl(audioUrl);
//						}
//
//						CPDecksVocabulary word = CPDecksApplication.getWord(id);
//						
//						word.setSource(wordJson.getString("source"));
//						word.setSourceTraditional(wordJson.optString("source_t"));
//						word.setTranslation(wordJson.getString("target"));
//						word.setPinyin(wordJson.optString("pinyin"));
//						word.getAudio().setAudioUrl(audioUrl);
//
//						if( wordJson.has("v3_id") ){
//							word.setV3id(wordJson.getString("v3_id"));
//						}
//						else if( ! word.getAudio().getAudioUrl().isEmpty() ){
//							word.setV3id(CPDecksUtility.getV3idFromAudioUrl(word.getAudio().getAudioUrl()));
//						}
//						word.getAudio().setAudioFile(CPDecksUtility.generateLinguisticFilePath(word));
//						
//						sentence.getWords().add(word);
//					}
//
//					sentences.add(sentence);
//				}
//
//				response.setList(sentences);
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, glossaryJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse createDeck(CPDecksAccount account, String deckName) {
//		String resultJsonString = new CPDecksVocabularyNetworkUtilityModel().createDeck(account, deckName);
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getInt("deck_id"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse removeDeck(CPDecksAccount account, CPDecksDeck deck) {
//		String resultJsonString = new CPDecksVocabularyNetworkUtilityModel().removeDeck(account, deck);
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getJSONObject("result").getString("status"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse renameDeck(CPDecksAccount account, CPDecksDeck deck, String newName) {
//		String resultJsonString = new CPDecksVocabularyNetworkUtilityModel().renameDeck(account, deck, newName);
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getJSONObject("result").getString("status"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse removeTerm(CPDecksAccount account, CPDecksDeck deck, CPDecksVocabulary term) {
//		String resultJsonString = new CPDecksVocabularyNetworkUtilityModel().removeDeckTerm(account, deck, term);
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getJSONObject("result").getString("status"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
//
//	public static CPDecksResponse moveTerm(CPDecksAccount account, CPDecksDeck oldDeck, CPDecksDeck newDeck, CPDecksVocabulary term) {
//		String resultJsonString = new CPDecksVocabularyNetworkUtilityModel().moveDeckTerm(account, oldDeck, newDeck, term);
//
//		CPDecksResponse response = new CPDecksResponse();
//		if (resultJsonString != null) {
//			try {
//				JSONObject resultJson = new JSONObject(resultJsonString);
//				response.setObject(resultJson.getJSONObject("result").getString("status"));
//				return response;
//			} catch (Exception x) {
//				return CPDecksManager.processRemoteError(response, resultJsonString);
//			}
//		}
//		return CPDecksManager.processNetworkError(response);
//	}
}

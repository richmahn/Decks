package com.chinesepod.decks.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CPDecksDeck implements CPDecksObject, Serializable {
	private static final Object DECK_EXTRA_DATA_KEY_ICON = "icon";
	private String mTitle;
	private long mId;
	private ArrayList<CPDecksVocabulary> mVocabulary;
	private int mType;
	private String mCreatedAt;
	private String mUpdatedAt;
	private Map<String, String> mExtraDataMap = new HashMap<String, String>();
	private String mIcon = "";
	private int mOrderWeight = 0;

	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id){
		mId = id;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getTitle() {
		return mTitle;
	}

	public int getCount() {
		return getVocabulary().size();
	}

	public void setVocabulary(ArrayList<CPDecksVocabulary> vocabularyList) {
		mVocabulary = vocabularyList;
	}
	public ArrayList<CPDecksVocabulary> getVocabulary(){
		return mVocabulary;
	}
	
	public String toString(){
		return this.getTitle();
	}

	@Override
	public String getAudioUrl() {
		return null;
	}

	public void addWords(ArrayList<CPDecksVocabulary> vocabularyList) {
		if( mVocabulary == null || mVocabulary.size() < 1 ){
			return;
		}
		
		for (CPDecksVocabulary vocabulary : vocabularyList) {
			addVocabulary(vocabulary);
		}
	}

	public void addVocabulary(CPDecksVocabulary vocabulary) {
		getVocabulary(); // makes sure it isn't null
		if( ! mVocabulary.contains(vocabulary) ){
			mVocabulary.add(0, vocabulary);
		}
	}
	
	public boolean containsVocabulary(CPDecksVocabulary vocabulary){
		return getVocabulary().contains(vocabulary);
	}

	public void removeWord(CPDecksVocabulary vocabulary) {
		if( vocabulary == null || vocabulary.getId() < 1 ){
			return;
		}
		mVocabulary.remove(vocabulary);
	}
	public void setType(int type) {
		mType = type;
	}
	public int getType(){
		return mType;
	}
	
	public void setCreatedAt(String createdAt) {
		mCreatedAt = createdAt;
	}
	public String getCreatedAt(){
		return mCreatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		mUpdatedAt = updatedAt;
	}
	public String getUpdatedAt(){
		return mUpdatedAt;
	}

	public Map<String, String> getExtraDataMap() {
		return mExtraDataMap ;
	}
	public void setExtraDataMap(Map<String,String> extraData){
		mExtraDataMap = extraData;
	}
	public void putExtraData(String key, String value){
		mExtraDataMap.put(key, value);
		
		if( key.equals(DECK_EXTRA_DATA_KEY_ICON) ){
			setIcon(value);
		}
	}
	public String getExtraData(String key){
		return mExtraDataMap.get(key);
	}

	public void setIcon(String icon) {
		mIcon = icon;
	}
	public String getIcon() {
		return mIcon;
	}
	
	public void setOrderWeight(int weight) {
		mOrderWeight = weight;
	}
	public int getOrderWeight(){
		return mOrderWeight;
	}
}

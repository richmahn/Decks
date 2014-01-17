package com.chinesepod.decks.logic;

import java.io.Serializable;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.Languages;
import com.chinesepod.decks.Languages.Language;
import com.chinesepod.decks.utility.CPDecksUtility;

public class CPDecksVocabulary extends CPodExpandableObject implements Serializable {
	public final static int TYPE_UNKNOWN = 0;
	public final static int TYPE_WORD = 1;
	public final static int TYPE_SENTENCE = 2;
	public final static int TYPE_DICTATION = 3;
	public static final int TYPE_TRANSLATION = 4;		

	private long mId;
	private int mUserVocabId;
	private String mSource = "";
	private String mTargetPhonetics = "";
	private String mTarget = "";
	private CPDecksAudio mSourceAudio;
	private CPDecksAudio mTargetAudio;
	private String mV3id;
	private int mType = TYPE_UNKNOWN;
	private String mCreatedAt;
	private String mUpdatedAt;
	private long mCpodVocabId;
	private Language mSourceLanguage;
	private Language mTargetLanguage;
	
	private Boolean isHeader = false;
	private boolean isSupplementary;
	private String mPartOfSpeech;

	private String mImageUrl;
	private int mOrderWeight;
	private long mAppDecksContentId;
	private String mImageFile;

	public long getId() {
		return mId;
	}
	public void setId(long id) {
		mId = id;
	}

	public int getUserVocabId() {
		return mUserVocabId;
	}
	public void setUserVocabId(int mUserVocabId) {
		this.mUserVocabId = mUserVocabId;
	}

	public String getSource() {
		return mSource;
	}
	public void setSource(String mSource) {
		this.mSource = mSource;
	}

	public String getTargetPhonetics() {
		return mTargetPhonetics;
	}
	public void setTargetPhonetics(String targetPhonetics) {
		mTargetPhonetics = targetPhonetics;
	}

	public String getTarget() {
		return mTarget;
	}
	public void setTarget(String target) {
		mTarget = target;
	}

	public String getV3id() {
		return this.mV3id;
	}
	public void setV3id(String v3id) {
		this.mV3id = v3id;
	}

	public String getRecordAudioFile() {
		return CPDecksUtility.generateLinguisticRecordFilePath(this);
	}

	public void setType(int type) {
		mType = type;
	}
	public int getType() {
		return mType;
	}
	
	public void setSourceAudio(CPDecksAudio audio){
		mSourceAudio = audio;
	}
	
	public CPDecksAudio getSourceAudio(){
		if( mSourceAudio == null ){
			mSourceAudio = new CPDecksAudio();
		}
		if( mSourceAudio.getTitle() == null || mSourceAudio.getTitle().isEmpty() ){
			mSourceAudio.setTitle(this.getSource());
		}
		return mSourceAudio;
	}

	public void setTargetAudio(CPDecksAudio audio){
		mTargetAudio = audio;
	}
	
	public CPDecksAudio getTargetAudio(){
		if( mTargetAudio == null ){
			mTargetAudio = new CPDecksAudio();
		}
		if( mTargetAudio.getTitle() == null || mTargetAudio.getTitle().isEmpty() ){
			mTargetAudio.setTitle(this.getTarget());
		}
		return mTargetAudio;
	}

	@Override
	public String getAudioUrl() {
		return getTargetAudio().getAudioUrl();
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


	public long getCpodVocabId(){
		return mCpodVocabId;
	}
	public void setCpodVocabId(long id) {
		mCpodVocabId = id;
	}

	public void setHeader(Boolean ish) {
		isHeader = ish;
	}

	public Boolean isHeader() {
		return isHeader;
	}

	@Override
	public String toString() {
		String str = getTarget();
		
		if( getTargetPhonetics() != null && ! getTargetPhonetics().isEmpty() ){
			if( this instanceof CPDecksSentence ) {
				str += "\n" + getTargetPhonetics();
			}
			else {
				str += " - " + getTargetPhonetics();
			}
		}
		
		return str;
	}

	public Boolean isSupplementary() {
		return isSupplementary;
	}

	public void setSupplementary(Boolean isSupplementary) {
		this.isSupplementary = isSupplementary;
	}

	public void setImageUrl(String imageUrl){
		mImageUrl = imageUrl;
	}
	public String getImageUrl(){
		return mImageUrl;
	}
	
	public void setOrderWeight(int weight) {
		mOrderWeight = weight;
	}
	public int getOrderWeight(){
		return mOrderWeight;
	}
	
	public void setAppDecksContentId(long appDecksContentId) {
		mAppDecksContentId = appDecksContentId;
	}
	public long getAppDecksContentId(){
		return mAppDecksContentId;
	}
	
	public String getImageFile() {
		if( mImageFile == null ){
			mImageFile = CPDecksUtility.generateVocabularyImageFilePath(this);
		}
		
		return mImageFile;
	}

	public void setSourceLanguage(String shortName){
		Language lang = Language.findLanguageByShortName(shortName);
		if( lang != null ){
			setSourceLanguage(lang);
		}
	}
	public void setSourceLanguage(Language lang) {
		mSourceLanguage = lang;
	}
	public Language getSourceLanguage(){
		return mSourceLanguage;
	}

	public void setTargetLanguage(String shortName){
		Language lang = Language.findLanguageByShortName(shortName);
		if( lang != null ){
			setTargetLanguage(lang);
		}
	}
	public void setTargetLanguage(Language lang) {
		mTargetLanguage = lang;
	}
	public Language getTargetLanguage(){
		return mTargetLanguage;
	}
}


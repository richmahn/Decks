package com.chinesepod.decks.logic;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.utility.FileOperationHelper;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public class CPDecksSentence extends CPDecksVocabulary implements Serializable {
	private String mSpeaker;
	private ArrayList<CPDecksVocabulary> mWords = new ArrayList<CPDecksVocabulary>();
	private boolean mTranslationShow;

	/**
	 * @return the mSpeaker
	 */
	public String getSpeaker() {
		return mSpeaker;
	}

	/**
	 * @param mSpeaker
	 *            the mSpeaker to set
	 */
	public void setSpeaker(String mSpeaker) {
		this.mSpeaker = mSpeaker;
	}

	public ArrayList<CPDecksVocabulary> getWords() {
		return mWords;
	}

	@Override
	public String toString() {
		return getTarget() + "\n" + getTargetPhonetics();
	}

	public boolean isTranslationShown() {
		return mTranslationShow;
	}

	public void setTranslationShown(boolean mTranslationShow) {
		this.mTranslationShow = mTranslationShow;
	}
}

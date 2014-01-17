package com.chinesepod.decks.logic;

import java.util.ArrayList;

public class CPodGrammarBlock {
	private Integer mId;
	private String mTitle;
	private String mIntroduction;
	private ArrayList<CPDecksSentence> mSentences;

	/**
	 * @return the mId
	 */
	public Integer getId() {
		return mId;
	}

	/**
	 * @param mId
	 *            the mId to set
	 */
	public void setId(Integer mId) {
		this.mId = mId;
	}

	/**
	 * @return the mTitle
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param mTitle
	 *            the mTitle to set
	 */
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	/**
	 * @return the mIntroduction
	 */
	public String getIntroduction() {
		return mIntroduction;
	}

	/**
	 * @param mIntroduction
	 *            the mIntroduction to set
	 */
	public void setIntroduction(String mIntroduction) {
		this.mIntroduction = mIntroduction;
	}

	@Override
	public String toString() {
		return mTitle + " | " + mIntroduction;
	}

	public ArrayList<CPDecksSentence> getSentences() {
		if (mSentences == null)
			mSentences = new ArrayList<CPDecksSentence>();
		return mSentences;
	}

}

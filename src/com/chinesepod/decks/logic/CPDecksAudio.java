package com.chinesepod.decks.logic;

import java.io.File;
import java.io.Serializable;

import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.utility.CPDecksUtility;

public class CPDecksAudio implements CPDecksObject, Serializable {
	private String mAudioUrl;
	private String mAudioFile;
	private String mImageUrl;
	private STATUS mStatus = STATUS.NOT_DOWNLOADED;
	private int mProgress;
	private Integer mOrder;
	private	long mId;
	private long mDownloadId;
	private String mTitle;
	private String mAudioId;
	private int mAudioType = -1;
	private String mLevelName;

	public static enum STATUS {
		NOT_DOWNLOADED, DOWNLOADING, PENDING, DOWNLOADED
	}

	/**
	 * @return the mAudioUrl
	 */
	public String getAudioUrl() {
		return mAudioUrl;
	}

	/**
	 * @param mAudioUrl
	 *            the mAudioUrl to set
	 */
	public void setAudioUrl(String mAudioUrl) {
		this.mAudioUrl = mAudioUrl;
	}

	/**
	 * @return the mAudioFile
	 */
	public String getAudioFile() {
		return mAudioFile;
	}
	
	public boolean fileExists(){
		if(mAudioFile == null){
			return false;
		}
		return new File(mAudioFile).exists();
	}

	/**
	 * @param mAudioFile
	 *            the mAudioFile to set
	 */
	public void setAudioFile(String mAudioFile) {
		this.mAudioFile = mAudioFile;
	}

	/**
	 * @return the mImageUrl
	 */
	public String getImageUrl() {
		return mImageUrl;
	}

	/**
	 * @param mImageUrl
	 *            the mImageUrl to set
	 */
	public void setImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}

	public void setStatus(STATUS status) {
		mStatus = status;
	}

	public STATUS getStatus() {
		return mStatus;
	}

	public void setProgress(int pr) {
		mProgress = pr;
	}

	public int getProgress() {
		return mProgress;
	}

	public Integer getOrder() {
		return mOrder;
	}

	public void setOrder(Integer mOrder) {
		this.mOrder = mOrder;
	}

	@Override
	public long getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public void setDownloadId(long downloadId) {
		mDownloadId = downloadId;

	}

	public long getDownloadId() {
		return mDownloadId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public void setAudioId(String audioId) {
		mAudioId = audioId;

	}

	public String getAudioId() {
		return mAudioId;
	}

	public String getLevelName() {
		return mLevelName;
	}

	public int getAudioType() {
		return mAudioType;
	}
	public void setAudioType(int type) {
		this.mAudioType = type;
	}

	public void setLevelName(String mLevelName) {
		this.mLevelName = mLevelName;
	}
	
	public int getAudioUrlFileSize(){
		if( getAudioUrl() == null || getAudioUrl().isEmpty() ){
			return -1;
		}
		return CPDecksUtility.getUrlFileSize(null, getAudioUrl());
	}
}
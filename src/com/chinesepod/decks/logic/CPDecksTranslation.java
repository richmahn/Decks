package com.chinesepod.decks.logic;

import java.io.Serializable;
import java.util.ArrayList;

public class CPDecksTranslation extends CPDecksVocabulary implements Serializable {
	private ArrayList<CPDecksImage> mImageList;
	private long mTranslationId;

	public void setTranslationId(long id) {
		mTranslationId = id;
	}
	public long getTranslationId(){
		return mTranslationId;
	}
	
	public void setImageUrlsList(ArrayList<CPDecksImage> imageList){
		mImageList = imageList;
	}
	public ArrayList<CPDecksImage> getImageUrlsList(){
		return mImageList;
	}
}

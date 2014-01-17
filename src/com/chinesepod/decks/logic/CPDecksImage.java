package com.chinesepod.decks.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;

public class CPDecksImage implements CPDecksObject, Serializable {
	private String mImageId;
	private int mWidth;
	private int mHeight;
	private String mImageUrl;
	private int mThumbWidth;
	private int mThumbHeight;
	private String mThumbUrl;

	public String getImageId() {
		return mImageId;
	}
	public void setImageId(String id){
		mImageId = id;
	}

	public void setWidth(int width) {
		mWidth = width;
	}
	public int getWidth(){
		return mWidth;
	}
	
	public void setHeight(int height) {
		mHeight = height;
	}
	public int getHeight(){
		return mHeight;
	}

	public void setThumbWidth(int width) {
		mThumbWidth = width;
	}
	public int getThumbWidth(){
		return mThumbWidth;
	}

	public void setThumbHeight(int height) {
		mThumbHeight = height;
	}
	public int getThumbHeight(){
		return mThumbHeight;
	}
	
	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}
	public String getImageUrl(){
		return mImageUrl;
	}

	public void setThumbUrl(String imageUrl) {
		mThumbUrl = imageUrl;
	}
	public String getThumbUrl(){
		return mThumbUrl;
	}

	@Override
	public long getId() {
		return 0;
	}
	
	@Override
	public String getAudioUrl() {
		// TODO Auto-generated method stub
		return null;
	}
}

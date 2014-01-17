package com.chinesepod.decks.logic;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.integer;

public class CPDecksContent extends CPDecksVocabulary implements Serializable {
	private int mTypeId;
	private long mCategoryId;
	private long mSubcategoryId;
	private String mPublishedDate;
	
	public int getTypeId() {
		return mTypeId;
	}
	public void setTypeId(int type) {
		mTypeId = type;
	}

	public long getCategoryId() {
		return mCategoryId;
	}
	public void setCategoryId(long id) {
		mCategoryId = id;
	}

	public long getSubcategoryId() {
		return mSubcategoryId;
	}
	public void setSubcategoryId(int id) {
		mSubcategoryId = id;
	}
	
	public String getPublishedDate(){
		String publishedDate = mPublishedDate;
		if( publishedDate != null && ! publishedDate.isEmpty() ){
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mPublishedDate);
				publishedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return publishedDate;
	}
	public void setPublishedDate(String date){
		mPublishedDate = date;
	}
	
	
	public int compareTo(CPDecksContent content2) {
		if(content2.getPublishedDate().compareTo(getPublishedDate()) != 0){
			return content2.getPublishedDate().compareTo(getPublishedDate()); // this is meant to be backwards to put highest date first
		}
        if( getOrderWeight() != content2.getOrderWeight() ){
        	return (new Integer(getOrderWeight())).compareTo(new Integer(content2.getOrderWeight()));
        }
        if( getTargetPhonetics().compareTo(content2.getTargetPhonetics()) != 0 ){
        	return getTargetPhonetics().compareTo(content2.getTargetPhonetics());
        }
        if( getTarget().compareTo(content2.getTarget()) != 0 ){
        	return getTarget().compareTo(content2.getTarget());
        }
        if( getSource().compareTo(content2.getSource()) != 0 ){
        	return getSource().compareTo(content2.getSource());
        }
        if( getCreatedAt().compareTo(content2.getCreatedAt()) != 0 ){
        	return getCreatedAt().compareTo(content2.getCreatedAt());
        }
        if( getUpdatedAt().compareTo(content2.getUpdatedAt()) != 0 ){
        	return getUpdatedAt().compareTo(content2.getUpdatedAt());
        }
		return 0;
	}
}


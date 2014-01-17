package com.chinesepod.decks.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Test;

import android.R.integer;

import com.chinesepod.decks.fragment.ContentCardFragment;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.utility.CPDecksUtility;

public class CPDecksContentCategory extends CPodExpandableObject implements Serializable {
	public static int CONTENT_CATEGORY_LEVEL_TYPE = 0;
	public static int CONTENT_CATEGORY_LEVEL_CATEGORY = 1;
	public static int CONTENT_CATEGORY_LEVEL_SUBCATEGORY = 2;
	
	
	private long mId;
	private long mParentId;
	private String mTitle;
	private String mImageUrl;
	private String mCreatedAt;
	private String mUpdatedAt;
	private ArrayList<Long> mContentIdList = new ArrayList<Long>();  
	private ArrayList<Long> mChildCategoryIdList = new ArrayList<Long>();
	private int mCategoryLevel;  
	
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		mId = id;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String name) {
		mTitle = name;
	}

	public int getCategoryLevel() {
		return mCategoryLevel;
	}
	public void setCategoryLevel(int level) {
		mCategoryLevel = level;
	}
	
	public void setIsType(){
		mCategoryLevel = CONTENT_CATEGORY_LEVEL_TYPE;
	}
	public void setIsCategory(){
		mCategoryLevel = CONTENT_CATEGORY_LEVEL_CATEGORY;
	}
	public void setIsSubcategory(){
		mCategoryLevel = CONTENT_CATEGORY_LEVEL_SUBCATEGORY;
	}
	
	public boolean isType(){
		return (mCategoryLevel == CONTENT_CATEGORY_LEVEL_TYPE);
	}
	public boolean isCategory(){
		return (mCategoryLevel == CONTENT_CATEGORY_LEVEL_CATEGORY);
	}
	public boolean isSubcategory(){
		return (mCategoryLevel == CONTENT_CATEGORY_LEVEL_SUBCATEGORY);
	}
	
	public long getParentId() {
		return mParentId;
	}
	public void setParentId(long id) {
		mParentId = id;
	}
	public CPDecksContentCategory getParent(){
		if( mParentId > 0 ){
			if( getCategoryLevel() - 1 == CONTENT_CATEGORY_LEVEL_TYPE ){
				return CPDecksContentManager.getContentCategoryOfType((int)mParentId);
			}
			else {
				return CPDecksContentManager.getContentCategory(mParentId);
			}
		}
		return null;
	}

	public void setImageUrl(String imageUrl){
		mImageUrl = imageUrl;
	}
	public String getImageUrl(){
		if( mImageUrl == null || mImageUrl.isEmpty() ){
			for(Long id : getContentIdList() ){
				CPDecksContent content = CPDecksContentManager.getContent(id);
				if( content != null && content.getImageUrl() != null && ! content.getImageUrl().isEmpty() ){
					return content.getImageUrl();
				}
			}
			for(Long id : getChildCategoryIdList()){
				CPDecksContentCategory subcategory = CPDecksContentManager.getContentCategory(id);
				if( subcategory != null && subcategory.getImageUrl() != null && ! subcategory.getImageUrl().isEmpty() ){
					return subcategory.getImageUrl();
				}
			}
		}
		
		return mImageUrl;
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
	
	public ArrayList<Long> getChildCategoryIdList(){
		Collections.sort(mChildCategoryIdList, (new CPDecksContentManager()).new ContentCategorySorter());
		return mChildCategoryIdList;
	}
	public void setChildCategoryIdList(ArrayList<Long> categoryIdList){
		mChildCategoryIdList = categoryIdList;
	}
	public void addChildCategoryId(Long id){
		if( ! mChildCategoryIdList.contains(id) ){
			mChildCategoryIdList.add(id);
		}
	}
	
	public ArrayList<Long> getContentIdList(){
		Collections.sort(mContentIdList, (new CPDecksContentManager()).new ContentSorter());
		return mContentIdList;
	}
	public void setContentIdList(ArrayList<Long> contentIdList){
		for(Long id : contentIdList){
			this.addContentId(id);
		}
	}
	public void addContentId(Long id){
		mContentIdList.add(id);
	}
	
	@Override
	public String getAudioUrl() {
		return null;
	}
	
	public int compareTo(CPDecksContentCategory category) {
		if( category == null ){
			return -1;
		}
		return getTitle().compareTo(category.getTitle());
	}
	
	public int getCategoryContentCount(){
		int count = getContentIdList().size();
		for(Long id : getChildCategoryIdList()){
			CPDecksContentCategory subcategory = CPDecksContentManager.getContentCategory(id);
			if( subcategory != null ){
				count += subcategory.getCategoryContentCount();
			}
		}
		return count;
	}
	
	public String getCategoryDescription() {
		String description = "";

		ArrayList<String> nameList = new ArrayList<String>();
		if( getChildCategoryIdList().size() > 0 ){
			for(Long id : getChildCategoryIdList()){
				CPDecksContentCategory subcategory = CPDecksContentManager.getContentCategory(id);
				if( subcategory != null ){
					nameList.add(subcategory.getTitle());
				}
			}
		}
		else {
			for(Long id : getContentIdList()){
				CPDecksContent content = CPDecksContentManager.getContent(id);
				if( content != null & content.getTarget() != null && ! content.getTarget().isEmpty() ){
					nameList.add(content.getTarget());
				}
			}
		}
		if( nameList.size() > 0 ){
			String delim = "";
		    for (String name : nameList) {
		        description += delim+name;
		        delim = "; ";
		    }
		}
		else {
			description = "<EMPTY>";
		}
		
		return description;
	}
	public int getType() {
		int type = -1;
		if( isSubcategory() ){
			type = (int)getParent().getParent().getId();
		}
		else if( isCategory() ){
			type = (int)getParent().getId();
		}
		else {
			type = (int)getId();
		}
		
		return type;
	}
}



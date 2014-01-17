/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity to list out the content
 */
package com.chinesepod.decks;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chinesepod.decks.adapter.CPDecksContentListAdapter;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.utility.CPDecksContentManager;

public class CPDecksContentListActivity extends CPDecksListActivity {
	private static final String SELECTED_ITEM_POSITION = "selected_item_position";
	private CPDecksContentCategory mContentCategory;
	private int mSelectedItemPosition;
	private ArrayList<Long> mContentIdList;
	private CPDecksContentListAdapter mContentListAdapater;
	
	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_content_list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSelectedItemPosition = 0;

		if (savedInstanceState != null) {
			mSelectedItemPosition = savedInstanceState.getInt(SELECTED_ITEM_POSITION);
			if( savedInstanceState.containsKey(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID) ){
				mContentCategory = CPDecksContentManager.getContentCategory(savedInstanceState.getLong(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID));
			}
			else if( savedInstanceState.containsKey(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID) ){
				mContentCategory = CPDecksContentManager.getContentCategoryOfType(savedInstanceState.getInt(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID));
			}
		}
		else {
			mSelectedItemPosition = getIntent().getIntExtra(SELECTED_ITEM_POSITION, 0);
			if( getIntent().hasExtra(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID) ){
				long categoryId = getIntent().getLongExtra(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID, 0);
				mContentCategory = CPDecksContentManager.getContentCategory(categoryId);
			}
			else if( getIntent().hasExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID) ){
				int typeId = getIntent().getIntExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID, 0);
				mContentCategory = CPDecksContentManager.getContentCategoryOfType(typeId);
			}
		}
		
		if( mContentCategory == null ){
			finish();
			return;
		}
		
		mContentIdList = mContentCategory.getContentIdList();

		setTitleAndSubtitle();
		
		mContentListAdapater = new CPDecksContentListAdapter(CPDecksContentListActivity.this, mContentIdList);
		setListAdapter(mContentListAdapater);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				Intent i = new Intent(CPDecksContentListActivity.this, CPDecksContentCardsActivity.class);
				i.putExtra(CPDecksContentCardsActivity.CURRENT_POSITION, position);
				if( mContentCategory.isType() ){
					i.putExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID, (int)mContentCategory.getId());
				}
				else {
					i.putExtra(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID, mContentCategory.getId());
				}
				startActivity(i);
			}
		});
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mSelectedItemPosition = mContentListAdapater.getSelectedItemPosition();
		if (mSelectedItemPosition == ListView.INVALID_POSITION || mSelectedItemPosition >= mContentIdList.size()) {
			mSelectedItemPosition = 0;
		}
		outState.putInt(SELECTED_ITEM_POSITION, mSelectedItemPosition);

		outState.putLong(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID, mContentCategory.getId());
		
		super.onSaveInstanceState(outState);
	}

	public void setTitleAndSubtitle() {
		if( mContentCategory != null ){
			if( mContentCategory.isType() || mContentCategory.isCategory() ){
				switch((int)mContentCategory.getId()){
				case CPDecksContentManager.CONTENT_TYPE_WOTD :
					setTitle(R.string.word_of_the_day_button);
					break;
				case CPDecksContentManager.CONTENT_TYPE_WORDLIST :
					setTitle(R.string.lists_button);
					break;
				case CPDecksContentManager.CONTENT_TYPE_PHRASE :
					setTitle(R.string.phrases_button);
					break;
				default :
					setTitle(R.string.app_name);
					break;
				}
			}
			else {
				setTitle(mContentCategory.getParent().getTitle());
				setSubtitle(mContentCategory.getTitle());
			}
		}
	}
}

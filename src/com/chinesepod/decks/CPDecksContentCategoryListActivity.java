/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity to list out the Content Categories for all of the types (Word of the Day, Phrases, Words)
 */
package com.chinesepod.decks;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;

import com.chinesepod.decks.R;
import com.chinesepod.decks.adapter.CPDecksContentCategoryListAdapter;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.logic.CPDecksError;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;

public class CPDecksContentCategoryListActivity extends CPDecksListActivity {
	private static final String SELECTED_ITEM_POSITION = "selected_item_position";
	public static final String CONTENT_CATEGORY_ID = "content_category_id";
	public static final String CONTENT_TYPE_ID = "content_type_id";

	public boolean isRetrieving;
	public Dialog retrievingDialog;
	private MenuItem refreshButton;
	public CPDecksContentCategoryListAdapter mContentCategoryListAdapater;
	private ArrayList<Long> mContentCategoryIdList;
	
	private ListView mListView;
	private int mSelectedItemPosition;
	private CPDecksContentCategory mContentCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mListView = (ListView) findViewById(android.R.id.list);

		mSelectedItemPosition = 0;

		if (savedInstanceState != null) {
			mSelectedItemPosition = savedInstanceState.getInt(SELECTED_ITEM_POSITION);
			if( savedInstanceState.containsKey(CONTENT_CATEGORY_ID) ){
				mContentCategory = CPDecksContentManager.getContentCategory(savedInstanceState.getLong(CONTENT_CATEGORY_ID));
			}
			else if( savedInstanceState.containsKey(CONTENT_TYPE_ID) ){
				mContentCategory = CPDecksContentManager.getContentCategoryOfType(savedInstanceState.getInt(CONTENT_TYPE_ID));
			}
		}
		else {
			mSelectedItemPosition = getIntent().getIntExtra(SELECTED_ITEM_POSITION, 0);
			if( getIntent().hasExtra(CONTENT_CATEGORY_ID) ){
				mContentCategory = CPDecksContentManager.getContentCategory(getIntent().getLongExtra(CONTENT_CATEGORY_ID, 0));
			}
			else if( getIntent().hasExtra(CONTENT_TYPE_ID) ){
				mContentCategory = CPDecksContentManager.getContentCategoryOfType(getIntent().getIntExtra(CONTENT_TYPE_ID, 0));
			}
		}
		
		if( mContentCategory == null ){
			finish();
			return;
		}
		
		if( mContentCategory.getChildCategoryIdList() == null || mContentCategory.getChildCategoryIdList().size() < 1 ){
			if( mContentCategory.getContentIdList() != null && mContentCategory.getContentIdList().size() > 0 ){
				Intent intent = new Intent(this, CPDecksContentListActivity.class);
				if( mContentCategory.isType() ){
					intent.putExtra(CONTENT_TYPE_ID, (int)mContentCategory.getId());
				}
				else {
					intent.putExtra(CONTENT_CATEGORY_ID, mContentCategory.getId());
				}
				startActivity(intent);
				finish();
			}
		}

		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		mContentCategoryIdList = mContentCategory.getChildCategoryIdList();

		setTitleAndSubtitle();

		mContentCategoryListAdapater = new CPDecksContentCategoryListAdapter(CPDecksContentCategoryListActivity.this, mContentCategoryIdList);
		setListAdapter(mContentCategoryListAdapater);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mSelectedItemPosition = mContentCategoryListAdapater.getSelectedItemPosition();
		if (mSelectedItemPosition == ListView.INVALID_POSITION || mSelectedItemPosition >= mContentCategoryIdList.size()) {
			mSelectedItemPosition = 0;
		}

		outState.putInt(SELECTED_ITEM_POSITION, mSelectedItemPosition);

		if( mContentCategory.isType() ){
			outState.putInt(CONTENT_TYPE_ID, (int)mContentCategory.getId());
		}
		else {
			outState.putLong(CONTENT_CATEGORY_ID, mContentCategory.getId());
		}
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isRetrieving && retrievingDialog == null) {
			retrievingDialog = createLoadingDialog(R.string.loading_contents_from_server);
			retrievingDialog.show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (retrievingDialog != null) {
			retrievingDialog.dismiss();
		}
	}

	class RetrieveContents extends AsyncTask<Integer, Integer, CPDecksResponse> {
		private boolean mRefresh = true;

		public RetrieveContents(boolean mRefresh) {
			this.mRefresh = mRefresh;
		}

		public RetrieveContents() {
			this.mRefresh = true;
		}

		@Override
		protected void onPreExecute() {
			isRetrieving = true;
			if (refreshButton != null){
				refreshButton.setEnabled(false);
				refreshButton.setActionView(R.layout.actionbar_indeterminate_progress);
			}

			retrievingDialog = createLoadingDialog(R.string.loading_contents_from_server);
			retrievingDialog.show();
			
			if (refreshButton != null){
				refreshButton.setEnabled(false);
			}
			
			Log.i(TAG, "onPreExecute in retrieveing content");
			super.onPreExecute();
		}

		@Override
		protected CPDecksResponse doInBackground(Integer... params) {
			CPDecksResponse response = new CPDecksResponse();

			Log.i(TAG, "Started retrieveing categories");

			if( ( mRefresh ||  ! CPDecksContentManager.hasLoadedContent()) && CPDecksUtility.isOnline(CPDecksContentCategoryListActivity.this) ){
				CPDecksContentManager.setFetchMode(NetworkUtilityModel.FIRST_NET_THEN_CACHE);
				CPDecksContentManager.generateContentListsFromServer();
			}
				
			if( CPDecksContentManager.getContentCategoryOfType(1) == null ) {
				response.setError(new CPDecksError(getResources().getString(R.string.network_error)));
				return response;
			}
			
			response.setObject(CPDecksContentManager.getContentCategory(mContentCategory.getId()));

			Log.i(TAG, "Finished retrieveing content");
			return response;
		}

		@Override
		protected void onPostExecute(CPDecksResponse response) {
			mContentCategoryListAdapater.setSelectedItemPosition(0);
			setSelection(0);
			isRetrieving = false;
			if (refreshButton != null){
				refreshButton.setEnabled(true);
				refreshButton.setActionView(null);
			}
			try {
				retrievingDialog.dismiss();
			} catch (Exception x) {
			}
			if (refreshButton != null){
				refreshButton.setEnabled(true);
			}
			if (response.isSuccessfull()) {
				if (!mContentCategoryIdList.isEmpty()) {
					mContentCategoryListAdapater.setSelectedItemPosition(mSelectedItemPosition);
				}
			} else {
				Toast.makeText(CPDecksContentCategoryListActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
			
			
			if( response.isSuccessfull() ){
				mContentCategory = (CPDecksContentCategory) response.getObject();
				
				if( mContentCategory != null ){
					setTitleAndSubtitle();
					mContentCategoryIdList.clear();
					if( mContentCategory.getChildCategoryIdList() != null ){
						mContentCategoryIdList = mContentCategory.getChildCategoryIdList();
					}
				}
			}
			
			mContentCategoryListAdapater.refreshUI(mContentCategoryIdList);
			Log.i(TAG, "onPostExecute in retrieveing contents");

			super.onPostExecute(response);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CPDecksContentCategory contentCategory = CPDecksContentManager.getContentCategory(mContentCategoryIdList.get(position));
		
		if(contentCategory == null){
			return;
		}

		Intent i = null;
		i = new Intent(this, CPDecksContentCategoryListActivity.class);
		i.putExtra(CONTENT_CATEGORY_ID, contentCategory.getId());
		startActivity(i);
	}

	public void setTitleAndSubtitle() {
		if( mContentCategory != null ){
			if( mContentCategory.isType() ){
				setTitle(mContentCategory.getTitle());
				setSubtitle(R.string.categories);
			}
			else {
				setTitle(mContentCategory.getParent().getTitle());
				setSubtitle(mContentCategory.getTitle());
			}
		}
	}

	protected void loadList(){
		if( ! isRetrieving ) {
			mContentCategoryIdList.clear();
			new RetrieveContents(true).execute();
		}
	}
	
	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_content_category_list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
	    refreshButton = menu.findItem(R.id.action_refresh);
	    refreshButton.setVisible(true);
	    return ret;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = super.onOptionsItemSelected(item);
		if( ret ){
			return ret;
		}
		
		switch (item.getItemId()) {
		case R.id.action_refresh:
			if( ! CPDecksUtility.isOnline(this) ){
				Toast.makeText(this, R.string.connection_required, Toast.LENGTH_LONG).show();
				return false;
			}
			loadList();
			return true;
		default:
			return false;
		}
	}
}

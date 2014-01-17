/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for the Dashboard
 */
package com.chinesepod.decks;

import com.chinesepod.decks.logic.CPDecksError;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class CPDecksDashboardActivity extends CPDecksActivity {
	public static final String TAG = "CPDecksDashboardActivity"; 
	
	public static final int LOADING_DIALOG = 0;
	public boolean isCreatingVisitorAccount = false;
	
	public boolean mIsRetrieving;
	public Dialog mRetrievingDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (!isTaskRoot()) {
            Log.i(TAG, "DashboardActivity - onCreate: Detected multiple instance of this activity, closing it and return to root activity");
            Intent reloadIntent = new Intent(CPDecksDashboardActivity.this, CPDecksDashboardActivity.class);
            reloadIntent.setAction(Intent.ACTION_MAIN);
            if (intent != null && intent.getExtras() != null) {
                reloadIntent.putExtras(intent.getExtras());
            }
            if (intent != null && intent.getData() != null) {
                reloadIntent.setData(intent.getData());
            }
            reloadIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            reloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivityIfNeeded(reloadIntent, 0);
        }

        super.onCreate(savedInstanceState);
        
        findViewById(R.id.DecksButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(CPDecksDeckSelectorActivity.class);
			}
		});
        
        findViewById(R.id.WOTDButton).setOnClickListener(new OnContentButtonClickListener(CPDecksContentManager.CONTENT_TYPE_WOTD));
        findViewById(R.id.ListsButton).setOnClickListener(new OnContentButtonClickListener(CPDecksContentManager.CONTENT_TYPE_WORDLIST));
        findViewById(R.id.PhrasesButton).setOnClickListener(new OnContentButtonClickListener(CPDecksContentManager.CONTENT_TYPE_PHRASE));
        
        findViewById(R.id.TranslateButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(CPDecksTranslateActivity.class);
			}
		});

        // If the user hasn't viewed the Intro yet (indicated by ShardPreferences) then show the Intro activity
		SharedPreferences prefs = getSharedPreferences(getApplication().getPackageName(), 0);
		if( ! prefs.getBoolean(CPDecksIntroActivity.PREF_DONT_SHOW_INTRO, false) ){
			startActivity(new Intent(this, CPDecksIntroActivity.class));
		}
		
		if( CPDecksVocabularyManager.getDeckCount() < 1 ){
			CPDecksVocabularyManager.createDeck("First Deck");
		}
		
		Log.i("HERE",getFilesDir().getAbsolutePath());
		Log.i("HERE",getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mIsRetrieving && mRetrievingDialog == null) {
			mRetrievingDialog = createLoadingDialog(R.string.loading_contents_from_server);
			mRetrievingDialog.show();
		}
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_dashboard;
	}


    public class OnContentButtonClickListener implements OnClickListener{
    	int mType;
    	
    	public OnContentButtonClickListener(int type) {
    		mType = type;
		}
    	
		@Override
		public void onClick(View v) {
			if( ! CPDecksContentManager.hasLoadedContent() ){
				new RetrieveContents(mType).execute();
			}
			else {
				if( mType != CPDecksContentManager.CONTENT_TYPE_WOTD ){
					startCategoryListActivity(mType);
				}
				else {
					startListActivity(mType);
				}
			}
		}
	}

	class RetrieveContents extends AsyncTask<Integer, Integer, CPDecksResponse> {
		int mTypeId;
		
		public RetrieveContents(int type) {
			mTypeId = type;
		}

		@Override
		protected void onPreExecute() {
			mIsRetrieving = true;

			mRetrievingDialog = createLoadingDialog(R.string.loading_contents_from_server);
			mRetrievingDialog.show();
			
			Log.i(TAG, "onPreExecute in retrieveing content");
			super.onPreExecute();
		}

		@Override
		protected CPDecksResponse doInBackground(Integer... params) {
			CPDecksResponse response = new CPDecksResponse();

			Log.i(TAG, "Started retrieveing categories");

			boolean result = false;
			if( ! CPDecksContentManager.hasLoadedContent() && CPDecksUtility.isOnline(CPDecksDashboardActivity.this) ){
				CPDecksContentManager.setFetchMode(NetworkUtilityModel.FIRST_NET_THEN_CACHE);
//				CPDecksContentManager.setFetchMode(NetworkUtilityModel.FIRST_CACHE_THEN_NET);
				result = CPDecksContentManager.generateContentListsFromServer();
			}
				
			if( ! result ) {
				response.setError(new CPDecksError(getResources().getString(R.string.network_error)));
				return response;
			}
			
			Log.i(TAG, "Finished retrieveing content");
			return response;
		}

		@Override
		protected void onPostExecute(CPDecksResponse response) {
			mIsRetrieving = false;
			mRetrievingDialog.dismiss();

			if ( CPDecksContentManager.hasLoadedContent()  ) {
				if( mTypeId != CPDecksContentManager.CONTENT_TYPE_WOTD ){
					startCategoryListActivity(mTypeId);
				}
				else {
					startListActivity(mTypeId);
				}
			} else {
				Toast.makeText(CPDecksDashboardActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}

			super.onPostExecute(response);
		}
	}

	public void startCategoryListActivity(int mTypeId) {
		Intent i = new Intent(CPDecksDashboardActivity.this, CPDecksContentCategoryListActivity.class);
		i.putExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID, mTypeId);
		startActivity(i);
	}

	public void startListActivity(int mTypeId) {
		Intent i = new Intent(CPDecksDashboardActivity.this, CPDecksContentListActivity.class);
		i.putExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID, mTypeId);
		startActivity(i);
	}
}
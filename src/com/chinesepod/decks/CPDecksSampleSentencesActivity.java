/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity to show Sample Sentences for a given word (Taken from ChinesePod app's "Glossary" activity)
 */
package com.chinesepod.decks;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;

import com.chinesepod.decks.R;
import com.chinesepod.decks.adapter.CPDecksSampleSentenceAdapter;
import com.chinesepod.decks.adapter.CPodWordAdapter;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.logic.CPDecksSentence;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;

public class CPDecksSampleSentencesActivity extends CPDecksActivity implements OnItemClickListener, OnScrollListener {
	public static final String VOCABULARY_ID = "vocabulary_id";
	public static final String CONTENT_ID = "content_id";
	public static final String CPOD_VOCABULARY = "cpod_vocabulary";

	public static final String RESULT_TERM_NUM = "term_num";
	public static final String TERM = "term";
	private static final int PAGE_COUNT = 25;
	public boolean isRetrieving;
	public Dialog retrievingDialog;
	private MenuItem refreshButton;
	private ExpandableListView mSampleList;
	CPDecksSampleSentenceAdapter mSampleAdapter;
	private int mSelectedSentence;
	private boolean isSentenceOpen;
	private Typeface mFont;
	public boolean isRefresh;
	private boolean mReachedTheLastSample;
	public ArrayList<CPDecksSentence> mSampleSentences;
	private View mEmptyResponse;

	private CPDecksVocabulary mVocabulary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( getIntent().hasExtra(VOCABULARY_ID) ){
			mVocabulary = CPDecksVocabularyManager.getVocabulary(getIntent().getLongExtra(VOCABULARY_ID, 0));
		}
		else if( getIntent().hasExtra(CONTENT_ID) ){
			mVocabulary = CPDecksContentManager.getContent(getIntent().getLongExtra(CONTENT_ID, 0));
		}
		else if( getIntent().hasExtra(CPOD_VOCABULARY) ){
			mVocabulary = (CPDecksVocabulary)getIntent().getSerializableExtra(CPOD_VOCABULARY);
		}
		
		if( mVocabulary == null ){
			finish();
			return;
		}
		
		setTitle(R.string.sample_sentences);

		mSampleList = (ExpandableListView) findViewById(android.R.id.list);
		mEmptyResponse = findViewById(R.id.emptyResponse);

		mSampleSentences = new ArrayList<CPDecksSentence>();
		
		findViewById(R.id.emptyResponse).setVisibility(View.INVISIBLE);
		
		((TextView) findViewById(R.id.termTarget)).setText(mVocabulary.getTarget());

		if (mVocabulary.getSource().isEmpty()) {
			((TextView) findViewById(R.id.termSource)).setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.termSource)).setText(mVocabulary.getSource());
		}
		
		if (mVocabulary.getTargetPhonetics().isEmpty()) {
			((TextView) findViewById(R.id.termTargetPhonetics)).setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.termTargetPhonetics)).setTypeface(mFont);
			((TextView) findViewById(R.id.termTargetPhonetics)).setText(mVocabulary.getTargetPhonetics());
		}
		
		mSampleAdapter = new CPDecksSampleSentenceAdapter(CPDecksSampleSentencesActivity.this, mSampleSentences);
		mSampleList.setAdapter((ExpandableListAdapter) mSampleAdapter);

		mSampleSentences.clear();
		mReachedTheLastSample = false;
		mSampleList.setOnScrollListener(this);
		
		if ( ! isRetrieving) {
			new RetrieveSampleSentencesTask().execute();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void drillDownSentence(int position) {
		mSelectedSentence = position;
		isSentenceOpen = true;
		updateView();

	}

	public void emptyResponse() {
		((TextView) findViewById(R.id.termTarget)).setText("");
		((TextView) findViewById(R.id.termSource)).setText("");
		((TextView) findViewById(R.id.termTargetPhonetics)).setText("");
		mSampleList.setVisibility(View.INVISIBLE);
		findViewById(R.id.emptyResponse).setVisibility(View.VISIBLE);
	}

	@Override
	public void onBackPressed() {
		if (isSentenceOpen) {
			isSentenceOpen = false;
			updateView();
		} else {
			super.onBackPressed();
		}
	}

	private void updateView() {
		ExpandableListView list = (ExpandableListView) findViewById(android.R.id.list);
		View sentenceView = findViewById(R.id.sentenceView);
		View termView = findViewById(R.id.termView);
		if (isSentenceOpen) {
			termView.setVisibility(View.GONE);
			sentenceView.setVisibility(View.VISIBLE);
			final CPDecksSentence sentence = (CPDecksSentence) mSampleAdapter.getGroup(mSelectedSentence);
			TextView sentenceSource = (TextView) findViewById(R.id.sentenceSource);
			TextView sentenceTarget = (TextView) findViewById(R.id.sentenceTarget);
			TextView sentenceTargetPhonetics = (TextView) findViewById(R.id.sentenceTargetPhonetics);
			ExpandableListView sentenceWordList = (ExpandableListView) findViewById(R.id.sentenceList);

			sentenceTarget.setText(sentence.getTarget());

			if (sentence.getTargetPhonetics().isEmpty()) {
				sentenceTargetPhonetics.setVisibility(View.GONE);
			} else {
				sentenceTargetPhonetics.setTypeface(mFont);
				sentenceTargetPhonetics.setText(sentence.getTargetPhonetics());
			}

			if (sentence.getTarget().isEmpty()) {
				sentenceSource.setVisibility(View.GONE);
			} else {
				sentenceSource.setText(sentence.getTarget());
			}

			CPodWordAdapter wordAdapter = new CPodWordAdapter(this, sentence.getWords());
			sentenceWordList.setAdapter((ExpandableListAdapter) wordAdapter);

			setTitle(R.string.drilldown);
		} else {
			termView.setVisibility(View.VISIBLE);
			sentenceView.setVisibility(View.GONE);
			setTitle(R.string.glossary);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
		CPDecksDeck deck = (CPDecksDeck) adapter.getItemAtPosition(position);
		Intent i = new Intent(this, CPDecksFlashcardsActivity.class);
		i.putExtra(CPDecksVocabularyManageActivity.DECK_ID, deck.getId());
		startActivity(i);

	}

	class RetrieveSampleSentencesTask extends AsyncTask<Integer, Integer, CPDecksResponse> {

		@Override
		protected void onPreExecute() {
			findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);
			isRetrieving = true;
		}

		@Override
		protected CPDecksResponse doInBackground(Integer... params) {
			int page = 0;
			int count = getPageCount();
			if (params.length == 2) {
				page = params[0];
				count = params[1];
			}
isRefresh = true;
			if( isRefresh ){
				CPDecksVocabularyManager.setFetchMode(NetworkUtilityModel.FIRST_NET_THEN_CACHE);
			}
			else {
				CPDecksVocabularyManager.setFetchMode(NetworkUtilityModel.FIRST_CACHE_THEN_NET);
			}
			CPDecksResponse response = CPDecksVocabularyManager.retrieveGlossarySampleSentences(mVocabulary, page, count);

			if (response.isSuccessfull()) {
			}
			return response;
		}

		@Override
		protected void onPostExecute(CPDecksResponse response) {
			findViewById(R.id.progressIndicator).setVisibility(View.INVISIBLE);
			isRetrieving = false;
			if (response.isSuccessfull()) {
				ArrayList<CPDecksSentence> addSamples = (ArrayList<CPDecksSentence>) response.getList();
				if (addSamples != null) {
					if (addSamples.size() < getPageCount()) {
						mReachedTheLastSample = true;
					}
					ArrayList<CPDecksSentence> oldSampleSentences = (ArrayList<CPDecksSentence>) mSampleSentences.clone();
					mSampleSentences = new ArrayList<CPDecksSentence>();
					mSampleSentences.addAll(oldSampleSentences);
					mSampleSentences.addAll(addSamples);
				}
				if (!mSampleSentences.isEmpty()) {
					mSampleList.setVisibility(View.VISIBLE);
					mSampleAdapter.refreshUI(mSampleSentences);
				} else {
					mSampleList.setVisibility(View.INVISIBLE);
					mEmptyResponse.setVisibility(View.VISIBLE);
				}
			} else {
				if (response.getError().isNetworkError()) {
					Toast.makeText(CPDecksSampleSentencesActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_sample_sentences;
	}

	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		boolean loadMore = firstVisible + visibleCount >= totalCount;
		if (loadMore && totalCount != 0 && !isRetrieving && !mReachedTheLastSample) {
			int pageCount = getPageCount();
			new RetrieveSampleSentencesTask().execute(totalCount / pageCount, pageCount);
		}
	}

	protected int getPageCount() {
		return PAGE_COUNT;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		if (refreshButton != null) {
			refreshButton.setEnabled(scrollState == OnScrollListener.SCROLL_STATE_IDLE);
		}
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
			if(isRetrieving){
				return false;
			}
			isRefresh = true;
 			new RetrieveSampleSentencesTask().execute();
			return true;
		default:
			return false;
		}
	}
}

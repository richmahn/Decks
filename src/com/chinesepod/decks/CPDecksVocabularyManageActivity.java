/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for managing the vocabulary terms in a deck
 */
package com.chinesepod.decks;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chinesepod.decks.R;
import com.chinesepod.decks.adapter.CPDecksVocabWordAdapter;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;

public class CPDecksVocabularyManageActivity extends CPDecksListActivity implements CPDecksCanPlaySound {
	public static final String DECK_ID = "deck_id";

	public boolean isRetrieving;
	public Dialog retrievingDialog;
	CPDecksDeck mDeck;
	private MediaPlayer mMediaPlayer;
	private long mDeckId = 0;

	private CPDecksVocabWordAdapter mVocabWordAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mVocabularyButton != null) {
			mVocabularyButton.requestFocus();
		}

		if (savedInstanceState != null) {
			mDeckId = savedInstanceState.getLong(CPDecksDeckListActivity.DECK_ID, 0);
		}
		else {
			mDeckId = getIntent().getLongExtra(CPDecksDeckListActivity.DECK_ID, 0);
		}
		
		if( mDeckId < 1 ){
			finish();
			return;
		}
	}

	@Override
	public void refreshPage(){
		mDeck = CPDecksVocabularyManager.getDeck(mDeckId); // refresh vocab
		
		if( mDeck == null ){
			finish();
			return;
		}
	
		setTitle(mDeck.getTitle());

		mVocabWordAdapter = new CPDecksVocabWordAdapter(this, mDeck.getVocabulary(), mDeck, getListView());
		((ExpandableListView)getListView()).setAdapter((ExpandableListAdapter) mVocabWordAdapter);
		
		super.refreshPage();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateList();
	}
	
	public void updateList(){
		if( mDeck == null && mDeck.getId() < 1 ){
			finish();
			return;
		}
		mDeck = CPDecksVocabularyManager.getDeck(mDeck.getId());
		if( mDeck == null && mDeck.getId() < 1 ){
			finish();
			return;
		}
		mVocabWordAdapter.refreshUI(mDeck.getVocabulary());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(CPDecksDeckSelectorActivity.DECK_ID, mDeck.getId());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void playSound(CPDecksVocabulary wordOrSentence, View v, int playingDrawableResourceId, int finishedDrawableResourceId) {
		boolean stoppedPlayer = false;
		if( mPlaySoundTask != null && mPlaySoundTask.isPlaying() ){
			mPlaySoundTask.stopAudio();
			stoppedPlayer = true;
		}
		if( mPlaySoundTask == null || ! stoppedPlayer || (stoppedPlayer && ! mPlaySoundTask.mView.equals(v)) ) {
			mPlaySoundTask = new PlaySoundTask(wordOrSentence.getTargetAudio(), mMediaPlayer, createLoadingDialog(R.string.audio_is_downloading), v, playingDrawableResourceId, finishedDrawableResourceId);
			mPlaySoundTask.execute();
		}
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_vocabulary_words;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.deck_action_bar, menu);
	    
		return true;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = super.onOptionsItemSelected(item);
		if( ret ){
			return ret;
		}
		
		switch (item.getItemId()) {
		case R.id.action_refresh:
		case R.id.action_delete:
			removeDeck(mDeck);
			return true;
		case R.id.action_rename:
			renameDeck(mDeck);
			return true;
		case R.id.action_add:
			Intent intent = new Intent(this, CPDecksCreateVocabularyActivity.class);
			intent.putExtra(CPDecksCreateVocabularyActivity.DECK_ID, mDeck.getId());
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}
}

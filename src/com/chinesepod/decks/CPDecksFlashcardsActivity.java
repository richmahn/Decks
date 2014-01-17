/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for displaying flashcards, front and back
 */
package com.chinesepod.decks;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.chinesepod.decks.R;
import com.chinesepod.decks.adapter.CPodFlashcardsPagerAdapter;
import com.chinesepod.decks.fragment.FlashcardFragment;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;

public class CPDecksFlashcardsActivity extends CPDecksActivity implements CPDecksCanPlaySound, OnPageChangeListener {
	private static final String POSSITION = "cur_card_possition";
	private static final String AUTO_REVIEW_RUNNING = "switch_task_running";
	private CPDecksDeck mDeck;
	private ViewPager mFlashCardsPager;
	private CPodFlashcardsPagerAdapter mFlashCardsPagerAdapter;
	private MediaPlayer mMediaPlayer;
	private int mPosition = 0;
	private AutoReviewTask mAutoReviewTask;
	private boolean mIsAutoTaskRunning;
	private int mTotal;
	private ImageView mFullScreenButton;
	private MenuItem mAutoReviewMenuItem;
	private long mDeckId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		
		if (savedInstanceState != null) {
			mPosition = savedInstanceState.getInt(POSSITION);
			mIsAutoTaskRunning = savedInstanceState.getBoolean(AUTO_REVIEW_RUNNING);
			mDeckId = savedInstanceState.getLong(CPDecksDeckListActivity.DECK_ID, 0);
		}
		else {
			mPosition = 0;
			mIsAutoTaskRunning = getIntent().getBooleanExtra(AUTO_REVIEW_RUNNING, false);
			mDeckId = getIntent().getLongExtra(CPDecksDeckListActivity.DECK_ID, 0);
		}
		
		if( mDeckId < 1 ){
			finish();
			return;
		}
	
		mFlashCardsPager = (ViewPager) findViewById(R.id.flashCardsPager);
		mFlashCardsPager.setOnPageChangeListener(this);

		mFlashCardsPager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("HERE", "HERE");
			}
		});
		
		mFullScreenButton = (ImageView)findViewById(R.id.FullScreenButton);
		mFullScreenButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ActionBar actionBar = getActionBar();
				if( actionBar.isShowing() ){
					//hide notification
					WindowManager.LayoutParams attrs = getWindow().getAttributes();
					attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
					getWindow().setAttributes(attrs);
					
					// Hide action/title bar
					actionBar.hide();

					// Change full-screen icon
					mFullScreenButton.setImageResource(R.drawable.resize_window);
				}
				else {
					// show notification
					WindowManager.LayoutParams attrs = getWindow().getAttributes();
					attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
					getWindow().setAttributes(attrs);				

					// Show action/title bar
					actionBar.show();
					
					// Change full-screen icon
					mFullScreenButton.setImageResource(R.drawable.resize_fullscreen);
				}
			}
		});
		
	}
	
	@Override
	public void refreshPage(){
		mDeck = CPDecksVocabularyManager.getDeck(mDeckId);
		if( mDeck == null || mDeck.getVocabulary().size() < 1 ){
			finish();
			return;
		}
		setTitle(mDeck.getTitle());
		mTotal = mDeck.getCount();
		setCounter();

		mFlashCardsPagerAdapter = new CPodFlashcardsPagerAdapter(getFragmentManager(), mDeck);
		mFlashCardsPager.setAdapter(mFlashCardsPagerAdapter);
		mFlashCardsPager.setCurrentItem(mPosition);

		mFlashCardsPager.setCurrentItem(mPosition);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(POSSITION, mPosition);
		outState.putBoolean(AUTO_REVIEW_RUNNING, mIsAutoTaskRunning);
		outState.putLong(CPDecksDeckSelectorActivity.DECK_ID, mDeck.getId());
		super.onSaveInstanceState(outState);
	}

	private void setCounter() {
		String counter = "";
		if( mDeck.getCount() < 1){
			counter = "No Cards";
		}
		else {
			counter = (mPosition + 1) + " / " + mTotal;
		}
		setSubtitle(counter);
	}

	protected void shuffleCards(Boolean scrollCards) {
		finish();

		Intent i = new Intent(this, CPDecksFlashcardsActivity.class);
		i.putExtra(CPDecksVocabularyManageActivity.DECK_ID, mDeck.getId());
		i.putExtra(AUTO_REVIEW_RUNNING, scrollCards);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(i);
	}

	@Override
	public void playSound(CPDecksVocabulary wordOrSentence, View v, int playingDrawableResourceId, int finishedDrawableResourceId) {
		boolean stoppedPlayer = false;
		if( mPlaySoundTask != null && mPlaySoundTask.isPlaying() ){
			mPlaySoundTask.stopAudio();
			stoppedPlayer = true;
		}
		if( mPlaySoundTask == null || ! stoppedPlayer || (stoppedPlayer && ! mPlaySoundTask.mView.equals(v)) ) {
			mPlaySoundTask = new PlaySoundTask(wordOrSentence.getTargetAudio(), mMediaPlayer, createLoadingDialog(R.string.audio_is_downloading), v, R.drawable.sound_red, finishedDrawableResourceId);
			mPlaySoundTask.execute();
		}
	}

	@Override
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	protected void onPause() {
		if (mAutoReviewTask != null && !mAutoReviewTask.isCancelled()) {
			mAutoReviewTask.cancel(true);
			mIsAutoTaskRunning = true;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		refreshPage();
		if (mIsAutoTaskRunning && (mAutoReviewTask == null || mAutoReviewTask.isCancelled())) {
			startAutoReview();
		}
		super.onResume();
	}

	@Override
	public void onPageSelected(int position) {
		mPosition = position;
		FlashcardFragment frLeft = mFlashCardsPagerAdapter.getFragment(mPosition - 1);
		FlashcardFragment frRight = mFlashCardsPagerAdapter.getFragment(mPosition + 1);
		if (frLeft != null) {
			frLeft.showFront();
		}
		if (frRight != null) {
			frRight.showFront();
		}
		setCounter();
	}

	class AutoReviewTask extends AsyncTask<Integer, Boolean, Integer> {

		private int mInterval;
		private FlashcardFragment mFragment;

		public AutoReviewTask(int interval) {
			mInterval = interval;
		}
		
		@Override
		protected void onPreExecute(){
			String deckBehaviourValue = CPDecksAccount.getInstance().getDeckEndedBehavior();
			String deckBehaviourName = deckBehaviourValue.replace("_", " ");
//			int index = Arrays.asList(getResources().getStringArray(R.array.endOfDeckListValues)).indexOf(deckBehaviourValue);
//			String deckBehaviourName = getResources().getStringArray(R.array.endOfDeckListArray)[index];
			Toast.makeText(CPDecksFlashcardsActivity.this, "Starting Auto Review. After last card will "+deckBehaviourName+".", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			while (!isCancelled()) {
				try {
					Thread.sleep(mInterval);
				} catch (Exception e) {
				}
				mFragment = mFlashCardsPagerAdapter.getFragment(mPosition);
				if (mFragment == null) {
					return 0;
				}
				if (mFragment.isShowingBack()) {
					mPosition += 1;
				}

				if (mPosition > mDeck.getCount() - 1) {
					return 1;
				} else if (!isCancelled()) {
					publishProgress();
				}
			}
			return 0;
		}

		@Override
		protected void onProgressUpdate(Boolean... value) {
			if (mFragment.isShowingBack()) {
				setCounter();
				mFlashCardsPager.setCurrentItem(mPosition);
			} else {
				mFragment.showBack();
			}

			super.onProgressUpdate(value);
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				String deckBehaviour = CPDecksAccount.getInstance().getDeckEndedBehavior();
				if (deckBehaviour.equals(getResources().getStringArray(R.array.endOfDeckListValues)[0])) {
					// Just stop
					mPosition = mDeck.getCount() - 1;
					setCounter();
					stopAutoReview();
				} else if (deckBehaviour.equals(getResources().getStringArray(R.array.endOfDeckListValues)[1])) {
					// Shuffle and stop
					Toast.makeText(CPDecksFlashcardsActivity.this, "Shuffling...", Toast.LENGTH_SHORT).show();
					shuffleCards(false);
					stopAutoReview();
				} else if (deckBehaviour.equals(getResources().getStringArray(R.array.endOfDeckListValues)[2])) {
					// Shuffle and play again
					Toast.makeText(CPDecksFlashcardsActivity.this, "Shuffling...", Toast.LENGTH_SHORT).show();
					shuffleCards(true);
				} else if (deckBehaviour.equals(getResources().getStringArray(R.array.endOfDeckListValues)[3])) {
					// Play from the beginning
					Toast.makeText(CPDecksFlashcardsActivity.this, "Starting from begining again...", Toast.LENGTH_SHORT).show();
					mPosition = 0;
					setCounter();
					mFlashCardsPager.setCurrentItem(mPosition);
				}
			}

		}
	}

	public void nextCard() {
		mPosition++;
		mFlashCardsPager.setCurrentItem(mPosition);

	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_flashcards;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.flashcards_action_bar, menu);
	    
	    mAutoReviewMenuItem = menu.findItem(R.id.action_auto_review);
	    if( mAutoReviewMenuItem != null && mIsAutoTaskRunning ){
	    	mAutoReviewMenuItem.setTitle(R.string.stop_auto_review);
	    }
	    
    	return true;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_manage_cards:
			Intent intent = new Intent(CPDecksFlashcardsActivity.this, CPDecksVocabularyManageActivity.class);
			intent.putExtra(CPDecksVocabularyManageActivity.DECK_ID, mDeckId);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			Intent i = new Intent(CPDecksFlashcardsActivity.this, CPDecksSettingsActivity.class);
			i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.chinesepod.decks.fragment.settings.FlashcardsSettingsFragment");
			startActivity(i);
			return true;
		case R.id.action_delete_term:
			boolean result = CPDecksVocabularyManager.removeVocabularyFromDeck(mDeck.getVocabulary().get(mPosition), mDeck);
			if( result ){
				Toast.makeText(this, "Successfully removed from "+mDeck.getTitle(), Toast.LENGTH_SHORT).show();
				refreshPage();
			}
			else {
				Toast.makeText(this, "Unable to remove. Please try again.", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_change_mode:
			return true;
		case R.id.action_move_term:
			moveTermToAnotherDeck(mDeck.getVocabulary().get(mPosition), mDeck);
			return true;
		case R.id.action_copy_term:
			copyTermToAnotherDeck(mDeck.getVocabulary().get(mPosition));
			return true;
		case R.id.action_auto_review:
			if (mAutoReviewTask == null || mAutoReviewTask.isCancelled()) {
				startAutoReview();
			} else {
				stopAutoReview();
			}
			return true;
		case R.id.action_shuffle_deck:
			shuffleCards(false);
			return true;
		case R.id.action_sync:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startAutoReview() {
		mAutoReviewTask = new AutoReviewTask(CPDecksAccount.getInstance().getFlashCardsTimeInterval());
		mAutoReviewTask.execute();
		if( mAutoReviewMenuItem != null ){
			mAutoReviewMenuItem.setTitle(R.string.stop_auto_review);
		}
	}

	private void stopAutoReview() {
		if (mAutoReviewTask != null) {
			mAutoReviewTask.cancel(true);
			mAutoReviewTask = null;
		}
		if( mAutoReviewMenuItem != null ){
			mAutoReviewMenuItem.setTitle(R.string.auto_review);
		}
	}
}

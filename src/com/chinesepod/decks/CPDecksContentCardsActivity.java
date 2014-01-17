/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * This activity will show the Content Cards for the Content Category selected
 * 
 */
package com.chinesepod.decks;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.CheckBox;

import com.chinesepod.decks.R;
import com.chinesepod.decks.adapter.CPDecksContentCardsPagerAdapter;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksContentManager;

public class CPDecksContentCardsActivity extends CPDecksActivity implements CPDecksCanPlaySound, OnPageChangeListener {
	private static final String POSITION = "cur_card_POSITion";
	private static final String AUTO_SWITCH_RUNNING = "switch_task_running";
	public static final String CURRENT_POSITION = "current_position";
	private CPDecksContentCategory mContentCategory;
	private ViewPager mContentCardsPager;
	private CPDecksContentCardsPagerAdapter mContentCardsPagerAdapter;
	private MediaPlayer mMediaPlayer;
	private int mPosition = 0;
	private CheckBox mAutoSwitch;
	private boolean isTaskRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Content");
		
		if (savedInstanceState != null ) {
			mPosition = savedInstanceState.getInt(CURRENT_POSITION, 0);
			if( savedInstanceState.containsKey(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID) ){
				int typeId = savedInstanceState.getInt(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID, 0);
				mContentCategory = CPDecksContentManager.getContentCategoryOfType(typeId);
			}
			else {
				long categoryId = savedInstanceState.getLong(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID, 0);
				mContentCategory = CPDecksContentManager.getContentCategory(categoryId);
			}
		}
		else {
			mPosition = getIntent().getIntExtra(CURRENT_POSITION, 0);
			if( getIntent().hasExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID) ){
				int typeId = getIntent().getIntExtra(CPDecksContentCategoryListActivity.CONTENT_TYPE_ID, 0);
				mContentCategory = CPDecksContentManager.getContentCategoryOfType(typeId);
			}
			else {
				long categoryId = getIntent().getLongExtra(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID, 0);
				mContentCategory = CPDecksContentManager.getContentCategory(categoryId);
			}
		}
		
		if( mContentCategory == null ){
			finish();
			return;
		}
		
		isTaskRunning = getIntent().getBooleanExtra(AUTO_SWITCH_RUNNING, false);

		if( mContentCategory.isType() || mContentCategory.getCategoryLevel() < 2 ){
			setTitle(mContentCategory.getTitle());
			
			Long contentId = mContentCategory.getContentIdList().get(mPosition);
			if( contentId != null && contentId > 0 ){
				CPDecksContent content = CPDecksContentManager.getContent(contentId);
				String publishDate = content.getPublishedDate();
				if( publishDate != null && ! publishDate.isEmpty() ){
					setSubtitle(publishDate);
				}
			}
		}
		else {
			setTitle(mContentCategory.getParent().getTitle());
			setSubtitle(mContentCategory.getTitle());
		}

		mContentCardsPager = (ViewPager) findViewById(R.id.flashCardsPager);
		mContentCardsPagerAdapter = new CPDecksContentCardsPagerAdapter(getFragmentManager(), mContentCategory);
		mContentCardsPager.setAdapter(mContentCardsPagerAdapter);
		mContentCardsPager.setOnPageChangeListener(this);
		
		if( mPosition > 0 ){
			mContentCardsPager.setCurrentItem(mPosition);
		}

		if (isTaskRunning) {
			mContentCardsPager.setCurrentItem(mPosition);
			mAutoSwitch.setChecked(true);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(POSITION, mPosition);
		outState.putBoolean(AUTO_SWITCH_RUNNING, isTaskRunning);
		super.onSaveInstanceState(outState);

		outState.putLong(CPDecksContentCategoryListActivity.CONTENT_CATEGORY_ID, mContentCategory.getId());
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
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onPageSelected(int position) {
		mPosition = position;
		CPDecksContent content = CPDecksContentManager.getContent(mContentCategory.getContentIdList().get(position));

		String publishDate = content.getPublishedDate();
		if( publishDate != null && ! publishDate.isEmpty() ){
			setSubtitle(publishDate);
		}
	}

	public void nextCard() {
		mPosition++;
		mContentCardsPager.setCurrentItem(mPosition);
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_content_cards;
	}
}

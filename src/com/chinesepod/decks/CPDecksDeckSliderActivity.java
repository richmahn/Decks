/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 */
package com.chinesepod.decks;

import java.util.ArrayList;

import com.chinesepod.decks.adapter.CPDecksDeckAdapter;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;

public class CPDecksDeckSliderActivity extends CPDecksActivity implements OnItemLongClickListener, OnGestureListener {
	static final String DECK_ID = "deck_id";

	private GestureDetector mGesture;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	String TAG = getClass().getSimpleName();

	private CPDecksDeckAdapter mDeckAdapter;

	private ArrayList<CPDecksDeck> mDeckList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.listDecksButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CPDecksDeckSliderActivity.this, CPDecksDeckListActivity.class);
				startActivity(intent);
			}
		});

		mGesture = new GestureDetector(this, mOnGesture);
	}

	@Override
	public void onResume(){
		super.onResume();
		updateDeckLayout();
	}

	private void updateDeckLayout(){
		mDeckList = CPDecksVocabularyManager.getDecks();
		LinearLayout decksLayout = (LinearLayout) findViewById(R.id.Decks);

		decksLayout.removeAllViews();
		
		for(final CPDecksDeck deck : mDeckList) {
		
			View deckView = getLayoutInflater().inflate(R.layout.deck_slider_item, null);
			TextView deckTitle = (TextView)deckView.findViewById(R.id.deckTitle);
			deckTitle.setText(deck.getTitle());
			deckView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CPDecksDeckSliderActivity.this, CPDecksFlashcardsActivity.class);
					intent.putExtra(CPDecksDeckSliderActivity.DECK_ID, deck.getId());
					startActivity(intent);
				}
			});
			LayoutParams params = new LayoutParams((int)getResources().getDimension(R.dimen.deck_width), (int)getResources().getDimension(R.dimen.deck_height));
			params.setMargins(0, 0, 100, 0);
			deckView.setLayoutParams(params);
//			ImageView deckBackground = (ImageView)deckView.findViewById(R.id.deckBackground);
//			deckBackground.setImageResource(getResources().getIdentifier("deck_background"+(int)(Math.random()*3+1), "drawable", this.getPackageName()));
			decksLayout.addView(deckView);
		}
	}
	
	public void createDeck(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(CPDecksDeckSliderActivity.this);
		alertDialog.setTitle(R.string.deck_creation);
		alertDialog.setMessage(R.string.deck_creation_explanation);
		final EditText input = new EditText(this);
		alertDialog.setView(input);
		// alertDialog.setIcon(R.drawable.search);
		alertDialog.setPositiveButton(R.string.deck_creation_create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				long deckId = CPDecksVocabularyManager.createDeck(input.getText().toString());
				if( deckId > 0 ){
					updateDeckLayout();
				}
			}
		});
		alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.create().show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if( position < 0 || mDeckList == null || position >= mDeckList.size() || mDeckList.get(position) == null ){
			return false;
		}
		
		Intent intent = new Intent(this, CPDecksFlashcardsActivity.class);
		intent.putExtra(CPDecksDeckSliderActivity.DECK_ID, mDeckList.get(position).getId());
		mDeckAdapter.setSelectedItemPosition(position);
		startActivity(intent);
		
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_deck_slider;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
	    menu.findItem(R.id.action_add).setVisible(true);
	    return ret;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			createDeck();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled = mGesture.onTouchEvent(ev);
		return handled;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.v("fling", "Flinged.");
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGesture.onTouchEvent(event);// return the double tap events
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
				return false;
			}

			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Log.v(TAG, "Right to Left");
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Log.v(TAG, "Left to Right");
			}
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}

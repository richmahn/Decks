/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity to list decks
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class CPDecksDeckListActivity extends CPDecksListActivity implements OnItemLongClickListener {
	static final String DECK_ID = "deck_id";

	String TAG = getClass().getSimpleName();

    private ArrayList<CPDecksDeck> mDeckList = new ArrayList<CPDecksDeck>();
	private CPDecksDeckAdapter mDeckAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		mDeckAdapter = new CPDecksDeckAdapter(this, mDeckList);
		((ExpandableListView)getListView()).setAdapter((ExpandableListAdapter) mDeckAdapter);
	}

	@Override
	public void onResume(){
		super.onResume();
		
		refreshPage();
	}
	
	public void refreshPage(){
		super.refreshPage();
		
		mDeckList = CPDecksVocabularyManager.getDecks();
		mDeckAdapter.refreshUI(mDeckList);
	}

	public void createDeck(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(CPDecksDeckListActivity.this);
		alertDialog.setTitle(R.string.deck_creation);
		alertDialog.setMessage(R.string.deck_creation_explanation);
		final EditText input = new EditText(this);
		alertDialog.setView(input);
		// alertDialog.setIcon(R.drawable.search);
		alertDialog.setPositiveButton(R.string.deck_creation_create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				long deckId = CPDecksVocabularyManager.createDeck(input.getText().toString());
				if( deckId > 0 ){
					mDeckList = CPDecksVocabularyManager.getDecks();
					mDeckAdapter.refreshUI(mDeckList);
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
		intent.putExtra(CPDecksDeckListActivity.DECK_ID, mDeckList.get(position).getId());
		mDeckAdapter.setSelectedItemPosition(position);
		startActivity(intent);
		
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_deck_list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.deck_manager_action_bar, menu);
	    return true;
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
}

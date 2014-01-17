/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity to select a deck
 */
package com.chinesepod.decks;

import java.util.ArrayList;
import java.util.Arrays;

import com.chinesepod.decks.adapter.CPDecksSmallDecksGridAdapter;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CPDecksDeckSelectorActivity extends CPDecksActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener, OnLongClickListener {
	static final String DECK_ID = "deck_id";

	String TAG = getClass().getSimpleName();

    private ArrayList<CPDecksDeck> mDeckList = new ArrayList<CPDecksDeck>();
	private ArrayList<CPDecksDeck> mSmallDeckList = new ArrayList<CPDecksDeck>();
	private CPDecksSmallDecksGridAdapter mSmallDecksGridAdapter;

	private RelativeLayout mLargeDeckLayout;

	private TextView mLargeDeckTitleTextView;

	private TextView mLargeDeckCountTextView;

	private GridView mSmallDecksGridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLargeDeckLayout = (RelativeLayout)findViewById(R.id.LargeDeck);
		mLargeDeckTitleTextView = (TextView)mLargeDeckLayout.findViewById(R.id.DeckTitle);
		mLargeDeckCountTextView = (TextView)mLargeDeckLayout.findViewById(R.id.DeckCount);
		mSmallDecksGridView = (GridView) findViewById(R.id.SmallDecksGrid);
	    mSmallDecksGridView.setOnItemClickListener(this);
	    mSmallDecksGridView.setOnItemLongClickListener(this);
	}

	@Override
	public void onResume(){
		super.onResume();

		updateDeckLayout();
	}

	private void updateDeckLayout(){
		mDeckList = CPDecksVocabularyManager.getDecks();

		if( mDeckList != null && mDeckList.size() > 0){
			mSmallDeckList = (ArrayList<CPDecksDeck>) mDeckList.clone();
			mSmallDeckList.remove(0);

			CPDecksDeck largeDeck = mDeckList.get(0);
			
			mLargeDeckTitleTextView.setText(largeDeck.getTitle());
			mLargeDeckCountTextView.setText(largeDeck.getCount()+"");

		    mLargeDeckLayout.setOnClickListener(this);
		    mLargeDeckLayout.setOnLongClickListener(this);
			
			if( mSmallDeckList == null || mSmallDeckList.size() < 1 ){
				mSmallDecksGridView.setVisibility(View.INVISIBLE);
			}
			else {
				mSmallDecksGridView.setVisibility(View.VISIBLE);
				mSmallDecksGridAdapter = new CPDecksSmallDecksGridAdapter(this, mSmallDeckList);
			    mSmallDecksGridView.setAdapter(mSmallDecksGridAdapter);
			}
		}
		else {
			mSmallDecksGridView.setVisibility(View.INVISIBLE);
			mLargeDeckTitleTextView.setText(R.string.click_to_create_a_deck);
			mLargeDeckCountTextView.setText("");
			mLargeDeckLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					createDeck();
				}
			});
		}
	}

	public void createDeck(){
//		AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog));
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(R.string.deck_creation);
		alertDialog.setMessage(R.string.deck_creation_explanation);

		View v = getLayoutInflater().inflate(R.layout.create_deck, null);
		final EditText deckName = (EditText)v.findViewById(R.id.DeckName);
		final Spinner iconSpinner = (Spinner)v.findViewById(R.id.IconSpinner);
		iconSpinner.setBackgroundColor(getResources().getColor(R.color.gray));
iconSpinner.setVisibility(View.GONE);
		ArrayList<String> resources = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.DeckIconDrawables)));
		resources.add(0, "");
		ArrayAdapter<String> dataAdapter = new IconsSpinnerArrayAdapter(this, R.layout.create_deck_icon_list_item, resources);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		iconSpinner.setAdapter(dataAdapter);
		alertDialog.setView(v);
		
		// alertDialog.setIcon(R.drawable.search);
		alertDialog.setPositiveButton(R.string.deck_creation_create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				long deckId = CPDecksVocabularyManager.createDeck(deckName.getText().toString(), (String)iconSpinner.getSelectedItem());
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
		if( position < 0 || mSmallDeckList == null || position >= mSmallDeckList.size() || mSmallDeckList.get(position) == null ){
			return false;
		}
		
		mSmallDecksGridAdapter.setSelectedItemPosition(position);
		
		openDeckFlashcardActivity(mSmallDeckList.get(position));
		
		return true;
	}
	
	public void openDeckVocabularyManageActivity(CPDecksDeck deck){
		if( deck == null || deck.getId() < 1 ){
			return;
		}
		
		Intent intent = new Intent(this, CPDecksVocabularyManageActivity.class);
		intent.putExtra(CPDecksDeckSelectorActivity.DECK_ID, deck.getId());
		startActivity(intent);
	}

	public void openDeckFlashcardActivity(CPDecksDeck deck){
 		if( deck == null || deck.getId() < 1 ){
			return;
		}
		
		Intent intent = new Intent(this, CPDecksFlashcardsActivity.class);
		intent.putExtra(CPDecksDeckSelectorActivity.DECK_ID, deck.getId());
		startActivity(intent);
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_deck_selector;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.deck_selector_action_bar, menu);
	    return true;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			createDeck();
			return true;
		case R.id.action_manage_decks:
			Intent intent = new Intent(this, CPDecksDeckListActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		if( position >= 0 && mSmallDeckList.size() > position ){
			if( mSmallDeckList.get(position).getCount() > 0 ){
				openDeckFlashcardActivity(mSmallDeckList.get(position));
			}
			else {
				openDeckVocabularyManageActivity(mSmallDeckList.get(position));
			}
		}
	}

	@Override
	public void onClick(View v) {
		if( mDeckList == null || mDeckList.size() < 1 || mDeckList.get(0) == null ){
			return;
		}
		
		if( mDeckList.get(0).getCount() > 0 ){
			openDeckFlashcardActivity(mDeckList.get(0));
		}
		else {
			openDeckVocabularyManageActivity(mDeckList.get(0));
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		if( mDeckList == null || mDeckList.size() < 1 || mDeckList.get(0) == null ){
			return false;
		}
		
		openDeckVocabularyManageActivity(mDeckList.get(0));
		
		return true;
	}

	class test extends ArrayAdapter<Integer>{

		public test(Context context, int resource) {
			super(context, resource);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	class IconsSpinnerArrayAdapter extends ArrayAdapter<String> {

	    private ArrayList<String> mIcons;

	    public IconsSpinnerArrayAdapter(Context context, int resourceId, ArrayList<String> icons) {
	    	super(context, resourceId, icons);
	        mIcons = icons;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	    	if( convertView == null ){
	        	convertView = new TextView(CPDecksDeckSelectorActivity.this);
	    	}
			convertView.setBackgroundColor(getResources().getColor(R.color.gray));
	        String icon_file = (String)mIcons.get(position);
	        if( icon_file == null || icon_file.isEmpty() ){
	        	((TextView)convertView).setText("<no icon>");
	        	((TextView)convertView).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	        }
	        else {
	        	((TextView)convertView).setText("");
	        	((TextView)convertView).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(getResources().getIdentifier("deck_icon_"+icon_file, "drawable", CPDecksDeckSelectorActivity.this.getPackageName())), null, null, null);
	        }
	        return convertView;
	    }

	    public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        return getView(position, convertView, parent);
	    }
	}
}
